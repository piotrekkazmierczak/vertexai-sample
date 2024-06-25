package org.dune.feature.functionscalling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.FunctionResponsePart
import com.google.firebase.vertexai.type.InvalidStateException
import com.google.firebase.vertexai.type.asTextOrNull
import com.google.firebase.vertexai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FunctionsChatViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {
    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") {
                text("Hello, what can you do?.")
            },
            content(role = "model") {
                text("Great to meet you. I can return the upper case version of the text you send me")
            }
        )
    )

    private val _uiState: MutableStateFlow<FunctionsChatUiState> =
        MutableStateFlow(
            FunctionsChatUiState(
                chat.history.map { content ->
                    // Map the initial messages
                    FunctionsChatMessage(
                        text = content.parts.first().asTextOrNull() ?: "",
                        participant = if (content.role == "user") Participant.USER else Participant.MODEL,
                        isPending = false
                    )
                }
            )
        )
    val uiState: StateFlow<FunctionsChatUiState> =
        _uiState.asStateFlow()

    fun sendMessage(userMessage: String) {
        // Add a pending message
        _uiState.value.addMessage(
            FunctionsChatMessage(
                text = userMessage,
                participant = Participant.USER,
                isPending = true
            )
        )

        viewModelScope.launch {
            try {
                var response =
                    chat.sendMessage("What would be the uppercase representation of the following text: $userMessage")

                // Getting the first matched function call
                val firstFunctionCall = response.functionCalls.firstOrNull()

                if (firstFunctionCall != null) {
                    val matchingFunction =
                        generativeModel.tools?.flatMap { it.functionDeclarations }
                            ?.first { it.name == firstFunctionCall.name }
                            ?: throw InvalidStateException(
                                "Model requested nonexistent function \"${firstFunctionCall.name}\" "
                            )

                    val funResult = matchingFunction.execute(firstFunctionCall)

                    response = chat.sendMessage(
                        content(role = "function") {
                            part(FunctionResponsePart("output", funResult))
                        }
                    )
                }

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                        FunctionsChatMessage(
                            text = modelResponse,
                            participant = Participant.MODEL,
                            isPending = false
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    FunctionsChatMessage(
                        text = e.localizedMessage,
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }
}
