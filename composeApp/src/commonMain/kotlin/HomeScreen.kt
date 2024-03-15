
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mohamedrejeb.calf.core.LocalPlatformContext
import com.mohamedrejeb.calf.io.readByteArray
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Composable
fun AppContent(
    viewModel: HomeViewModel
) {
    val appUiState = viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()

    HomeScreen(
        uiState = appUiState.value
    ) { inputText, selectedItems ->

        viewModel.questioning(
            userInput = inputText,
            selectedImage = selectedItems
        )
//        scope.launch {
//            viewModel.questioning(
//                userInput = inputText,
//                selectedImage = selectedItems
//            )
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState = HomeUiState.Loading,
    onSendClicked: (String, List<ByteArray>) -> Unit
) {

    var userQues by rememberSaveable() {
        mutableStateOf("")
    }

    val imageLists = remember {
        mutableStateListOf<ByteArray>()
    }

    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { files ->
            scope.launch {
                files.firstOrNull()?.let { file ->
                    val byteArray = file.readByteArray(context)
                    imageLists.add(byteArray)
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Gemini AI Chatbot CMP")
                },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Column {
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            pickerLauncher.launch()
                        },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Image"
                        )
                    }

                    OutlinedTextField(
                        value = userQues,
                        onValueChange = {
                            userQues = it
                        },
                        label = {
                            Text(text = "User Input")
                        },
                        placeholder = {
                            Text(text = "Upload Image and ask Question")
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.83f)
                    )

                    IconButton(
                        modifier = Modifier,
                        onClick = {
                            if (userQues.isNotBlank()) {
                                onSendClicked(userQues, imageLists)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Icon"
                        )
                    }
                }

                AnimatedVisibility(visible = imageLists.size > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        LazyRow(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            items(imageLists) { imageUri ->
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .requiredSize(50.dp),
                                        model = imageUri,
                                        contentDescription = "Image"
                                    )
                                    TextButton(
                                        onClick = {
                                            imageLists.remove(imageUri)
                                        }
                                    ) {
                                        Text(text = "Remove")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when(uiState) {
                is HomeUiState.Error -> {
                    Card(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.error
                        )
                    }
                }
                HomeUiState.Initial -> { }
                HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is HomeUiState.Success -> {
                    Card(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            text = uiState.outputText
                        )
                    }
                }
            }
        }

    }

}