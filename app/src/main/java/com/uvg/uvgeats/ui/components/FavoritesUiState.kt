package com.uvg.uvgeats.ui.components

import com.uvg.uvgeats.data.model.FoodItem

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favorites: List<FoodItem> = emptyList()
)
