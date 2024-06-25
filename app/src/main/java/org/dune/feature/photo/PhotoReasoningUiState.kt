package org.dune.feature.photo

sealed interface PhotoReasoningUiState {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : PhotoReasoningUiState

    /**
     * Still loading
     */
    data object Loading : PhotoReasoningUiState

    /**
     * Text has been generated
     */
    data class Success(
        val outputText: String
    ) : PhotoReasoningUiState

    /**
     * There was an error generating text
     */
    data class Error(
        val errorMessage: String
    ) : PhotoReasoningUiState
}
