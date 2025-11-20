package com.uvg.uvgeats.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgeats.data.model.FoodItem
import com.uvg.uvgeats.data.model.Result
import com.uvg.uvgeats.data.repository.FoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<DetailUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun setFoodItem(foodItem: FoodItem) {
        _uiState.update {
            it.copy(
                foodItem = foodItem,
                isFavorite = foodItem.isFavorite
            )
        }
    }

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            is DetailUiEvent.BackClicked -> {
                viewModelScope.launch {
                    _uiEffect.send(DetailUiEffect.NavigateBack)
                }
            }
            is DetailUiEvent.ToggleFavorite -> {
                toggleFavorite()
            }
        }
    }

    private fun toggleFavorite() {
        val currentFood = _uiState.value.foodItem ?: return
        val newFavoriteState = !_uiState.value.isFavorite

        // Optimista en UI
        _uiState.update {
            it.copy(
                isFavorite = newFavoriteState,
                foodItem = currentFood.copy(isFavorite = newFavoriteState)
            )
        }

        viewModelScope.launch {
            val result = if (newFavoriteState) {
                repository.addToFavorites(currentFood)
            } else {
                repository.removeFromFavorites(currentFood)
            }

            val message = when (result) {
                is Result.Success<*> ->
                    if (newFavoriteState) "Añadido a favoritos" else "Eliminado de favoritos"
                is Result.Error ->
                    "Error al actualizar favoritos"
                is Result.Loading ->
                    return@launch
            }

            _uiEffect.send(DetailUiEffect.ShowMessage(message))
        }
    }
}
