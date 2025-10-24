package com.uvg.uvgeats.ui.search

import com.uvg.uvgeats.data.model.FoodItem

// UiState inmutable
data class SearchUiState(
    val searchText: String = "",
    val foodList: List<FoodItem> = emptyList(),
    val filteredFoodList: List<FoodItem> = emptyList(),
    val priceRange: Float = 50f,
    val isLoading: Boolean = false
)

// Eventos de UI
sealed interface SearchUiEvent {
    data class SearchTextChanged(val text: String) : SearchUiEvent
    data object ClearSearch : SearchUiEvent
    data class PriceRangeChanged(val range: Float) : SearchUiEvent
    data class FoodItemClicked(val foodItem: FoodItem) : SearchUiEvent
    data object FavoritesClicked : SearchUiEvent
}

// Efectos de UI
sealed interface SearchUiEffect {
    data class NavigateToDetail(val foodItem: FoodItem) : SearchUiEffect
    data object NavigateToFavorites : SearchUiEffect
}