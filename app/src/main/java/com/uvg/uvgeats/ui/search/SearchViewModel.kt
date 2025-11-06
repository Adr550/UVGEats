package com.uvg.uvgeats.ui.search

import androidx.lifecycle.ViewModel
import android.content.Context
import com.uvg.uvgeats.data.repository.FoodRepositoryImpl
import androidx.lifecycle.viewModelScope
import com.uvg.uvgeats.data.model.Result
import com.uvg.uvgeats.data.repository.FakeFoodRepository
import com.uvg.uvgeats.data.repository.FoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val context: Context,
    private val repository: FoodRepository = FoodRepositoryImpl(context)
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<SearchUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadFoodItems()
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.SearchTextChanged -> {
                _uiState.update { it.copy(searchText = event.text) }
                performSearch(event.text)
            }
            is SearchUiEvent.ClearSearch -> {
                _uiState.update { it.copy(searchText = "") }
                loadFoodItems()
            }
            is SearchUiEvent.PriceRangeChanged -> {
                _uiState.update { it.copy(priceRange = event.range) }
                filterByPrice()
            }
            is SearchUiEvent.FoodItemClicked -> {
                viewModelScope.launch {
                    _uiEffect.send(SearchUiEffect.NavigateToDetail(event.foodItem))
                }
            }
            is SearchUiEvent.FavoritesClicked -> {
                viewModelScope.launch {
                    _uiEffect.send(SearchUiEffect.NavigateToFavorites)
                }
            }
            is SearchUiEvent.RetryClicked -> {
                loadFoodItems()
            }
        }
    }

    private fun loadFoodItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repository.getFoodItems()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            foodList = result.data,
                            filteredFoodList = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    val errorMsg = result.exception.message ?: "Error desconocido"
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                    _uiEffect.send(SearchUiEffect.ShowError(errorMsg))
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            loadFoodItems()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repository.searchFoodItems(query)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            filteredFoodList = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    val errorMsg = result.exception.message ?: "Error en búsqueda"
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                    _uiEffect.send(SearchUiEffect.ShowError(errorMsg))
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun filterByPrice() {
        val currentState = _uiState.value
        val filtered = currentState.foodList.filter { food ->
            food.price <= currentState.priceRange
        }

        _uiState.update { it.copy(filteredFoodList = filtered) }
    }

    fun loadMoreItems() {
        // Implementación futura para paginación
    }
}