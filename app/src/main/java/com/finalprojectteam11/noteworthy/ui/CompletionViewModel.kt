package com.finalprojectteam11.noteworthy.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.RetryStrategy
import com.finalprojectteam11.noteworthy.BuildConfig
import com.finalprojectteam11.noteworthy.data.LoadingStatus
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
class CompletionViewModel: ViewModel() {
    private val OPENAI_KEY = BuildConfig.OPENAI_API_KEY

    private val openAIConfig = OpenAIConfig(
        token = OPENAI_KEY,
        timeout = Timeout(socket = 13.seconds),
        retry = RetryStrategy(maxRetries = 1)
    )

    private val openAI = OpenAI(openAIConfig)

    private val _completionResults = MutableLiveData<TextCompletion>(null)
    val completionResults: LiveData<TextCompletion> = _completionResults

    private val _actionResults = MutableLiveData<TextCompletion>(null)
    val actionResults: LiveData<TextCompletion> = _actionResults

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loadingStatus = MutableLiveData(LoadingStatus.SUCCESS)
    val loadingStatus: LiveData<LoadingStatus> = _loadingStatus

    fun fetchAction(input: String) {
        Log.d("CompletionViewModel", "Fetching action")
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            val prompt = "You are a note taking app on an android phone. " +
                    "As the user is adding notes, you should try to suggest the single most helpful actionable item the user can take relating to their note. " +
                    "This action will be shown in the note taking app underneath the current note as a button that the user can click to execute the action, or read your response, the action should be able to be completed on the user's phone. " +
                    "Common actions should include searching, defining, or translating an important term, adding alarms for important reminders, opening a location in maps or weather, calling a phone number or emailing an address in the note, summarizing the note, or suggesting relevant information that can add to or build upon the note, such as suggesting a recipe if the note contains an ingredient list. " +
                    "The response should fall in one of the following categories: \"Search\", \"Define\", \"Map\", \"Summarize\", \"Translate\", \"Reminder\", \"Weather\", \"Call\", \"Email\", \"Other\", and \"None\". " +
                    "If you can not come up with a relevant action, respond with the action set to \"None\"." +
                    "Treat all JSON parameters as strings wrapped in double quotes, except for and \"hour\" / \"minute\" which are integers not wrapped in quotes." +
                    "If you do not respond in JSON, your response will be deleted. Please respond ONLY in JSON, with the following format (omitting unused fields): \n" +
                    "{\n" +
                    "\"title\": [Action Title],\n" +
                    "\"description: \"Action Description\",\n" +
                    "\"response: \"Response to user for summarization or other action\",\n" +
                    "\"category\": [Search, Define, Map, Summarize, Reminder, Calendar, or Other],\n" +
                    "\"url\": [URL to complete action, if applicable. Tel URL for call or mailto for email.]\n" +
                    "\"term\": [Term to search, define, or translate, if applicable]\n" +
                    "\"location\": [Location to open in map or weather, if applicable]\n" +
                    "\"hour\": [Hour to set reminder, if applicable. Int]\n" +
                    "\"minute\": [Minute to set reminder, if applicable. Int]\n" +
                    "}\n" +
                    "\n" +
                    "Note:\n" +
                    "{\n" +
                    "\"value\": \""+ input  + "\",\n" +
                    "}\n"

            val completionRequest = CompletionRequest(
                model = ModelId("text-davinci-003"),
                prompt = prompt,
                echo = false,
                maxTokens = 256,
            )
            val result: TextCompletion = openAI.completion(completionRequest)
            _actionResults.value = result
            _errorMessage.value = result.choices.isEmpty().let {
                when(it) {
                    true -> "No results found"
                    false -> null
                }
            }
            _loadingStatus.value = when(result.choices.isNotEmpty()) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }

    fun fetchCompletion(input: String) {
        viewModelScope.launch {
            _loadingStatus.value = LoadingStatus.LOADING

            val prompt = "Please provide up to 100 characters of autocompletion for the following text, responding with only the additional completion text and no prepended new lines: \n" +
                    input

            val completionRequest = CompletionRequest(
                model = ModelId("text-davinci-003"),
                prompt = prompt,
                echo = false,
                maxTokens = 256,
            )
            val result: TextCompletion = openAI.completion(completionRequest)
            _completionResults.value = result
            _errorMessage.value = result.choices.isEmpty().let {
                when(it) {
                    true -> "No results found"
                    false -> null
                }
            }
            _loadingStatus.value = when(result.choices.isNotEmpty()) {
                true -> LoadingStatus.SUCCESS
                false -> LoadingStatus.ERROR
            }
        }
    }
}