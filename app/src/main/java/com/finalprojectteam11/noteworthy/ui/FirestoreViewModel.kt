package com.finalprojectteam11.noteworthy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finalprojectteam11.noteworthy.data.LoadingStatus
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FirestoreViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _noteID = MutableLiveData<String>(null)
    val noteID: LiveData<String> = _noteID

    private val _noteResults = MutableLiveData<List<DocumentSnapshot>>(null)
    val noteResults: LiveData<List<DocumentSnapshot>> = _noteResults

    private val _pinnedNoteResults = MutableLiveData<List<DocumentSnapshot>>(null)
    val pinnedNoteResults: LiveData<List<DocumentSnapshot>> = _pinnedNoteResults

    private val _noteResult = MutableLiveData<DocumentSnapshot>(null)
    val noteResult: LiveData<DocumentSnapshot> = _noteResult

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loadingStatus = MutableLiveData<LoadingStatus>(LoadingStatus.SUCCESS)
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

    fun saveNote(note: String, title: String, time: String, id: String = "") {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING
            val note = hashMapOf(
                "title" to title,
                "content" to note,
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

    fun getNotes() {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            db.collection("notes")
                .get()
                .addOnSuccessListener { result ->
                    _loadingStatus.value = LoadingStatus.SUCCESS
                    _noteResults.value = result.documents
                }
                .addOnFailureListener { exception ->
                    _loadingStatus.value = LoadingStatus.ERROR
                    _errorMessage.value = exception.message
                }
        }
    }

    fun getPinnedNotes() {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            db.collection("notes")
                .whereEqualTo("pinned", true)
                .get()
                .addOnSuccessListener { result ->
                    _loadingStatus.value = LoadingStatus.SUCCESS
                    _pinnedNoteResults.value = result.documents
                }
                .addOnFailureListener { exception ->
                    _loadingStatus.value = LoadingStatus.ERROR
                    _errorMessage.value = exception.message
                }
        }
    }

}