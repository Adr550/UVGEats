package com.uvg.uvgeats.ui.search

import com.uvg.uvgeats.data.model.FoodItem

data class SearchUiState(
    val searchText: String = "",
    val foodList: List<FoodItem> = emptyList(),
    val filteredFoodList: List<FoodItem> = emptyList(),
    val priceRange: Float = 50f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface SearchUiEvent {
    data class SearchTextChanged(val text: String) : SearchUiEvent
    data object ClearSearch : SearchUiEvent
    data class PriceRangeChanged(val range: Float) : SearchUiEvent
    data class FoodItemClicked(val foodItem: FoodItem) : SearchUiEvent
    data object FavoritesClicked : SearchUiEvent
    data object RetryClicked : SearchUiEvent
}

sealed interface SearchUiEffect {
    data class NavigateToDetail(val foodItem: FoodItem) : SearchUiEffect
    data object NavigateToFavorites : SearchUiEffect
    data class ShowError(val message: String) : SearchUiEffect
}