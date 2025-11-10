package com.uvg.uvgeats.ui.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgeats.data.model.Result
import com.uvg.uvgeats.data.repository.FoodRepositoryImpl
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
        Log.d("SearchViewModel", "ViewModel inicializado - cargando items...")
        loadFoodItems()
    }

    fun onEvent(event: SearchUiEvent) {
        Log.d("SearchViewModel", "Evento recibido: $event")
        when (event) {
            is SearchUiEvent.SearchTextChanged -> {
                _uiState.update { it.copy(searchText = event.text) }
                performSearch(event.text)
            }
            is SearchUiEvent.ClearSearch -> {
                _uiState.update { it.copy(searchText = "") }
                performSearch("") // Recargar todos los items
            }
            is SearchUiEvent.PriceRangeChanged -> {
                _uiState.update { it.copy(priceRange = event.range) }
                filterByPrice()
            }
            is SearchUiEvent.FoodItemClicked -> {
                viewModelScope.launch {
                    Log.d("SearchViewModel", "Navegando a detalle: ${event.foodItem.name}")
                    _uiEffect.send(SearchUiEffect.NavigateToDetail(event.foodItem))
                }
            }
            is SearchUiEvent.FavoritesClicked -> {
                viewModelScope.launch {
                    Log.d("SearchViewModel", "Navegando a favoritos")
                    _uiEffect.send(SearchUiEffect.NavigateToFavorites)
                }
            }
            is SearchUiEvent.RetryClicked -> {
                Log.d("SearchViewModel", "Reintentando carga...")
                loadFoodItems()
            }
        }
    }

    private fun loadFoodItems() {
        viewModelScope.launch {
            Log.d("SearchViewModel", "Iniciando carga de items...")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                when (val result = repository.getFoodItems()) {
                    is Result.Success -> {
                        Log.d("SearchViewModel", "Éxito - ${result.data.size} items cargados")
                        _uiState.update { currentState ->
                            currentState.copy(
                                foodList = result.data,
                                filteredFoodList = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Result.Error -> {
                        Log.e("SearchViewModel", "Error - ${result.exception.message}")
                        val errorMsg = result.exception.message ?: "Error desconocido"
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                errorMessage = errorMsg
                            )
                        }
                        _uiEffect.send(SearchUiEffect.ShowError(errorMsg))
                    }
                    is Result.Loading -> {
                        Log.d("SearchViewModel", "Loading...")
                        _uiState.update { currentState -> currentState.copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Excepción inesperada: ${e.message}")
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    private fun performSearch(query: String) {
        val currentState = _uiState.value
        Log.d("SearchViewModel", "Realizando búsqueda: '$query'")

        if (query.isBlank()) {
            // Si la búsqueda está vacía, mostrar toda la lista
            _uiState.update { currentState ->
                currentState.copy(filteredFoodList = currentState.foodList)
            }
            Log.d("SearchViewModel", "Búsqueda vacía - mostrando ${currentState.foodList.size} items")
            return
        }

        // Filtrado local en lugar de llamar al repository
        val filtered = currentState.foodList.filter { food ->
            food.name.contains(query, ignoreCase = true) ||
                    food.brand.contains(query, ignoreCase = true)
        }

        Log.d("SearchViewModel", "Búsqueda completada - ${filtered.size} resultados")
        _uiState.update { currentState ->
            currentState.copy(filteredFoodList = filtered)
        }
    }

    private fun filterByPrice() {
        val currentState = _uiState.value
        val filtered = currentState.foodList.filter { food ->
            food.price <= currentState.priceRange
        }

        Log.d("SearchViewModel", "Filtrado por precio (max ${currentState.priceRange}Q) - ${filtered.size} items")
        _uiState.update { currentState ->
            currentState.copy(filteredFoodList = filtered)
        }
    }

    fun loadMoreItems() {
        // Implementación futura para paginación
        Log.d("SearchViewModel", "loadMoreItems llamado")
    }
}