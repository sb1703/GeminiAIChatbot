import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.PlatformImage
import dev.shreyaspatil.ai.client.generativeai.type.content
import dev.shreyaspatil.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
    val uiState = _uiState.asStateFlow()

    private lateinit var generativeModel: GenerativeModel

    init {
        val config = generationConfig {
            temperature = 0.70f
        }

        generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = AppConfig.API_KEY,
            generationConfig = config
        )
    }

    fun questioning(
        userInput: String,
        selectedImage: List<ByteArray>
    ) {
        _uiState.value = HomeUiState.Loading
        val prompt = "Take a look at images, and then answer the following questions: $userInput"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val content = content {
                    for (byteArray in selectedImage) {
                        image(PlatformImage(byteArray))
                    }
                    text(prompt)
                }

                var output = ""
                generativeModel.generateContentStream(content).collect {
                    output += it.text
                    _uiState.value = HomeUiState.Success(output)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Error in Generating content")
            }
        }
    }

}

sealed interface HomeUiState {
    data object Initial: HomeUiState
    data object Loading: HomeUiState
    data class Success(
        val outputText: String
    ): HomeUiState
    data class Error(
        val error: String
    ): HomeUiState
}