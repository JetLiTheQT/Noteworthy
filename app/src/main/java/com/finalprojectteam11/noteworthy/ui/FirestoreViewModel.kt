package com.finalprojectteam11.noteworthy.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalprojectteam11.noteworthy.data.LoadingStatus
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import com.finalprojectteam11.noteworthy.data.AppSettings
import com.google.firebase.firestore.DocumentReference

class FirestoreViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _noteID = MutableLiveData<String>(null)
    val noteID: LiveData<String> = _noteID

    private val _noteResults = MutableLiveData<List<DocumentSnapshot>>(null)
    val noteResults: LiveData<List<DocumentSnapshot>> = _noteResults

    private val _pinnedNoteResults = MutableLiveData<List<DocumentSnapshot>>(null)
    val pinnedNoteResults: LiveData<List<DocumentSnapshot>> = _pinnedNoteResults

    private val _noteResult = MutableLiveData<DocumentSnapshot>(null)
    val noteResult: LiveData<DocumentSnapshot> = _noteResult

    private val _categoryResults = MutableLiveData<List<String>>(null)
    val categoryResults: LiveData<List<String>> = _categoryResults

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loadingStatus = MutableLiveData(LoadingStatus.SUCCESS)
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

    fun saveNote(noteString: String, title: String, time: String, id: String = "") {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val note = hashMapOf(
                "title" to title,
                "content" to noteString,
                "time" to time
            )

            if (id != "") {
                db.collection("notes").document(id)
                    .set(note)
                    .addOnSuccessListener {
                        _loadingStatus.value = LoadingStatus.SUCCESS
                    }
                    .addOnFailureListener { e ->
                        _loadingStatus.value = LoadingStatus.ERROR
                        _errorMessage.value = e.message
                    }
            } else {
                db.collection("notes")
                    .add(note)
                    .addOnSuccessListener { documentReference ->
                        _loadingStatus.value = LoadingStatus.SUCCESS
                        _noteID.value = documentReference.id
                    }
                    .addOnFailureListener { e ->
                        _loadingStatus.value = LoadingStatus.ERROR
                        _errorMessage.value = e.message
                    }
            }


        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            db.collection("notes").document(id)
                .delete()
                .addOnSuccessListener {
                    _loadingStatus.value = LoadingStatus.SUCCESS
                }
                .addOnFailureListener { e ->
                    _loadingStatus.value = LoadingStatus.ERROR
                    _errorMessage.value = e.message
                }
        }
    }

    fun getNote(id: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            val docRef = db.collection("notes").document(id)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        _loadingStatus.value = LoadingStatus.SUCCESS
                        _noteResult.value = document
                    } else {
                        _loadingStatus.value = LoadingStatus.ERROR
                        _errorMessage.value = "No such document"
                    }
                }
                .addOnFailureListener { exception ->
                    _loadingStatus.value = LoadingStatus.ERROR
                    _errorMessage.value = exception.message
                }
        }
    }

    fun getNotes(filter: String = "") {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            val queryDirection = if (AppSettings.selectedSortBy == "Name (A-Z)" || AppSettings.selectedSortBy == "Date (Oldest-Newest)") {
                Query.Direction.ASCENDING
            } else {
                Query.Direction.DESCENDING
            }

            val selectedSort = if (AppSettings.selectedSortBy == "Name (A-Z)" || AppSettings.selectedSortBy == "Name (Z-A)") {
                "title"
            } else {
                "time"
            }

            val query = db.collection("notes")
                .orderBy(selectedSort, queryDirection)

            query.get()
                .addOnSuccessListener { result ->
                    _loadingStatus.value = LoadingStatus.SUCCESS
                    _noteResults.value = filterNotes(filter, result.documents)
                }
                .addOnFailureListener { exception ->
                    _loadingStatus.value = LoadingStatus.ERROR
                    _errorMessage.value = exception.message
                }
        }
    }

    fun getPinnedNotes(filter: String = "") {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val queryDirection = if (AppSettings.selectedSortBy == "Name (A-Z)" || AppSettings.selectedSortBy == "Date (Oldest-Newest)") {
                Query.Direction.ASCENDING
            } else {
                Query.Direction.DESCENDING
            }

            val selectedSort = if (AppSettings.selectedSortBy == "Name (A-Z)" || AppSettings.selectedSortBy == "Name (Z-A)") {
                "title"
            } else {
                "time"
            }

            val query = db.collection("notes")
                .whereEqualTo("pinned", true)
                .orderBy(selectedSort, queryDirection)

            query.get()
                .addOnSuccessListener { result ->
                    _loadingStatus.value = LoadingStatus.SUCCESS
                    _pinnedNoteResults.value = filterNotes(filter, result.documents)
                }
                .addOnFailureListener { exception ->
                    _loadingStatus.value = LoadingStatus.ERROR
                    _errorMessage.value = exception.message
                }
        }
    }
    fun toggleNotePinned(noteId: String, currentPinnedStatus: Boolean) {
        val noteRef = db.collection("notes").document(noteId)

        // Toggle the pinned status
        val newPinnedStatus = !currentPinnedStatus

        noteRef.update("pinned", newPinnedStatus)
            .addOnSuccessListener {
                Log.d("FirestoreViewModel", "Note pinned status updated")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreViewModel", "Error updating note pinned status", e)
            }
    }

    fun toggleNotePrivate(noteId: String, currentPrivateStatus: Boolean) {
        val noteRef = db.collection("notes").document(noteId)

        // Toggle the pinned status
        val newPrivateStatus = !currentPrivateStatus

        noteRef.update("private", newPrivateStatus)
            .addOnSuccessListener {
                Log.d("FirestoreViewModel", "Note private status updated")
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreViewModel", "Error updating note private status", e)
            }
    }

    private fun filterNotes(filter: String, notes: MutableList<DocumentSnapshot>): List<DocumentSnapshot>? {
        return when (filter) {
            "NW_INTERNAL_NEW" -> {
                // sort notes by time and return the first 5
                val sortedNotes = notes.sortedByDescending { if (it.data?.get("time") != null) it.data?.get("time").toString().toLong() else 0 }
                sortedNotes.take(5)
            }
            "NW_INTERNAL_PRIVATE" -> {
                val privateNotes = mutableListOf<DocumentSnapshot>()
                for (note in notes) {
                    val notePrivate = note.data?.get("private")
                    if (notePrivate != null && notePrivate == true) {
                        privateNotes.add(note)
                    }
                }
                privateNotes
            }
            "NW_INTERNAL_AI_ASSIST" -> {
                val aiNotes = mutableListOf<DocumentSnapshot>()
                for (note in notes) {
                    val notePrivate = note.data?.get("private")
                    if (notePrivate == null || notePrivate == false) {
                        aiNotes.add(note)
                    }
                }
                aiNotes
            }
            "" -> notes
            else -> {
                val filteredNotes = mutableListOf<DocumentSnapshot>()
                for (note in notes) {
                    val noteCategories = note.data?.get("categories") as List<String>
                    if (noteCategories.contains(filter)) {
                        filteredNotes.add(note)
                    }
                }
                filteredNotes
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            db.collection("notes")
                .whereNotEqualTo("categories", null)
                .get()
                .addOnSuccessListener { result ->
                    _loadingStatus.value = LoadingStatus.SUCCESS
                    val categories = mutableListOf<String>()
                    for (document in result) {
                        for (category in document.data["categories"] as List<String>) {
                            if (!categories.contains(category)) {
                                categories.add(category)
                            }
                        }
                    }
                    _categoryResults.value = categories
                }
                .addOnFailureListener { exception ->
                    _loadingStatus.value = LoadingStatus.ERROR
                    _errorMessage.value = exception.message
                }
        }
    }

}