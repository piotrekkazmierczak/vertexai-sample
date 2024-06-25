package org.dune

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.Tool
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.defineFunction
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import org.dune.feature.chat.ChatViewModel
import org.dune.feature.functionscalling.FunctionsChatViewModel
import org.dune.feature.photo.PhotoReasoningViewModel
import org.dune.feature.text.SummarizeViewModel
import org.json.JSONObject

val ForbiddenMachineViewModelFactory = object : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(viewModelClass: Class<T>, extras: CreationExtras): T {
        val config = generationConfig {
            temperature = 0.7f
        }

        return with(viewModelClass) {
            when {
                isAssignableFrom(SummarizeViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-flash` AI model
                    // for text generation
                    val generativeModel = Firebase.vertexAI.generativeModel(
                        modelName = "gemini-1.5-flash-preview-0514",
                        generationConfig = config
                    )
                    SummarizeViewModel(generativeModel)
                }

                isAssignableFrom(PhotoReasoningViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-flash` AI model
                    // for multimodal text generation
                    val generativeModel = Firebase.vertexAI.generativeModel(
                        modelName = "gemini-1.5-flash-preview-0514",
                        generationConfig = config
                    )
                    PhotoReasoningViewModel(generativeModel)
                }

                isAssignableFrom(ChatViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-flash` AI model for chat
                    val generativeModel = Firebase.vertexAI.generativeModel(
                        modelName = "gemini-1.5-flash-preview-0514",
                        generationConfig = config,
                        systemInstruction = content { text("You live in a world from Frank Herbert's Dune. Treat me like I'm one of nobles from House Atreides.") }
                    )
                    ChatViewModel(generativeModel)
                }

                isAssignableFrom(FunctionsChatViewModel::class.java) -> {
                    // Declare the functions you want to make available to the model
                    val tools = listOf(
                        Tool(
                            listOf(
                                defineFunction(
                                    "upperCase",
                                    "Returns the upper case version of the input string",
                                    Schema.str("input", "Text to transform")
                                ) { input ->
                                    JSONObject("{\"response\": \"${input.uppercase()}\"}")
                                }
                            )
                        )
                    )

                    // Initialize a GenerativeModel with the `gemini-pro` AI model for function calling chat
                    val generativeModel = Firebase.vertexAI.generativeModel(
                        modelName = "gemini-1.5-pro-preview-0514",
                        generationConfig = config,
                        tools = tools
                    )
                    FunctionsChatViewModel(generativeModel)
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${viewModelClass.name}")
            }
        } as T
    }
}
