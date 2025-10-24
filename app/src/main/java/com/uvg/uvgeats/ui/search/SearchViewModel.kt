package com.uvg.uvgeats.ui.search

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

class SearchViewModel : ViewModel() {

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
                _uiState.update {
                    it.copy(searchText = event.text)
                }
                filterFoodList()
            }
            is SearchUiEvent.ClearSearch -> {
                _uiState.update {
                    it.copy(searchText = "")
                }
                filterFoodList()
            }
            is SearchUiEvent.PriceRangeChanged -> {
                _uiState.update {
                    it.copy(priceRange = event.range)
                }
                filterFoodList()
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
        }
    }

    private fun loadFoodItems() {
        // Aquí cargarías los datos de una fuente real (API, DB, etc.)
        val foodList = listOf(
            FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera, 30, "Cafetería CIT"),
            FoodItem("Crepa", "Saúl", android.R.drawable.ic_menu_gallery, 25, "Cafetería CIT"),
            FoodItem("Camarones", "Gitane", android.R.drawable.ic_menu_report_image, 45, "Cafetería CIT"),
            FoodItem("Lays", "Gitane", android.R.drawable.ic_menu_slideshow, 15, "Máquina expendedora"),
            FoodItem("Pizza", "Gitane", android.R.drawable.ic_menu_gallery, 35, "Cafetería CIT"),
            FoodItem("Tacos", "Gitane", android.R.drawable.ic_menu_camera, 28, "Cafetería CIT"),
            FoodItem("Ensalada", "Gitane", android.R.drawable.ic_menu_report_image, 22, "Cafetería CIT"),
            FoodItem("Sushi", "Gitane", android.R.drawable.ic_menu_slideshow, 40, "Cafetería CIT"),
        )

        _uiState.update {
            it.copy(
                foodList = foodList,
                filteredFoodList = foodList
            )
        }
    }

    private fun filterFoodList() {
        val currentState = _uiState.value
        val filtered = currentState.foodList.filter { food ->
            val matchesSearch = if (currentState.searchText.isBlank()) {
                true
            } else {
                food.name.contains(currentState.searchText, ignoreCase = true) ||
                        food.brand.contains(currentState.searchText, ignoreCase = true)
            }

            val matchesPrice = food.price <= currentState.priceRange

            matchesSearch && matchesPrice
        }

        _uiState.update {
            it.copy(filteredFoodList = filtered)
        }
    }

    fun loadMoreItems() {
        // Para el infinite scroll
        val currentState = _uiState.value
        val moreItems = currentState.foodList // En una app real, cargarías más items

        _uiState.update {
            it.copy(
                foodList = it.foodList + moreItems
            )
        }
        filterFoodList()
    }
}