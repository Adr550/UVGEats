package com.uvg.uvgeats.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgeats.data.model.FoodItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<DetailUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun setFoodItem(foodItem: FoodItem) {
        _uiState.update {
            it.copy(foodItem = foodItem)
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
        val newFavoriteState = !_uiState.value.isFavorite
        _uiState.update {
            it.copy(isFavorite = newFavoriteState)
        }

        viewModelScope.launch {
            val message = if (newFavoriteState) {
                "Añadido a favoritos"
            } else {
                "Eliminado de favoritos"
            }
            _uiEffect.send(DetailUiEffect.ShowMessage(message))
        }
    }
}