package com.uvg.uvgeats.ui.detail

import com.uvg.uvgeats.data.model.FoodItem

// UiState inmutable
data class DetailUiState(
    val foodItem: FoodItem? = null,
    val isFavorite: Boolean = false
)

// Eventos de UI
sealed interface DetailUiEvent {
    data object BackClicked : DetailUiEvent
    data object ToggleFavorite : DetailUiEvent
}

// Efectos de UI
sealed interface DetailUiEffect {
    data object NavigateBack : DetailUiEffect
    data class ShowMessage(val message: String) : DetailUiEffect
}