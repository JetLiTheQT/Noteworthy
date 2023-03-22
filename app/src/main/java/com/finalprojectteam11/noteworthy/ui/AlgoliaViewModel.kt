package com.finalprojectteam11.noteworthy.ui

import com.finalprojectteam11.noteworthy.data.SearchResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algolia.search.client.ClientSearch
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.response.ResponseSearch
import com.finalprojectteam11.noteworthy.data.LoadingStatus
import kotlinx.coroutines.launch
import com.algolia.search.model.search.Query

class AlgoliaViewModel: ViewModel() {
    private val ALGOLIA_APP_ID = "AKMAX5WIPL"
    private val ALGOLIA_API_KEY = "be8dfacf84ba3775b8e703577106a0a5"

    private val client = ClientSearch(
        applicationID = ApplicationID(ALGOLIA_APP_ID),
        apiKey = APIKey(ALGOLIA_API_KEY)
    )

    private val index = client.initIndex(IndexName("notes"))

    private val _searchResults = MutableLiveData<ResponseSearch>(null)
    val searchResults: LiveData<ResponseSearch> = _searchResults

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loadingStatus = MutableLiveData(LoadingStatus.SUCCESS)
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

    fun search(input: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            val query = Query(input)

            val result = index.search(query)
            result.hits.deserialize(SearchResult.serializer())

            _searchResults.value = result
            _errorMessage.value = result.hits.isEmpty().let {
                when(it) {
                    true -> "No results found"
                    false -> null
                }
            }
            _loadingStatus.value = when(result.hits.isNotEmpty()) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }
}