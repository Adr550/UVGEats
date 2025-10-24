package com.uvg.uvgeats.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uvg.uvgeats.data.model.FoodItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Screen con ViewModel
@Composable
fun SearchScreenRoute(
    onNavigateToDetail: (FoodItem) -> Unit,
    onNavigateToFavorites: () -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SearchUiEffect.NavigateToDetail -> onNavigateToDetail(effect.foodItem)
                is SearchUiEffect.NavigateToFavorites -> onNavigateToFavorites()
            }
        }
    }

    SearchScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onLoadMore = viewModel::loadMoreItems
    )
}

// Composable puro sin side effects
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onEvent: (SearchUiEvent) -> Unit,
    onLoadMore: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyGridState()

    // Detectar scroll al final para cargar más items
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastVisible ->
                if (lastVisible == uiState.filteredFoodList.size - 1) {
                    onLoadMore()
                }
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideBarContent(
                priceRange = uiState.priceRange,
                onPriceRangeChange = { onEvent(SearchUiEvent.PriceRangeChanged(it)) },
                onMenuClick = {
                    scope.launch { drawerState.close() }
                },
                onFavoritesClick = {
                    onEvent(SearchUiEvent.FavoritesClicked)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        SearchBar(
                            searchText = uiState.searchText,
                            onSearchTextChange = { onEvent(SearchUiEvent.SearchTextChanged(it)) },
                            onClearClick = { onEvent(SearchUiEvent.ClearSearch) }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = listState,
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(8.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredFoodList) { food ->
                    FoodCard(
                        food = food,
                        onClick = { onEvent(SearchUiEvent.FoodItemClicked(food)) }
                    )
                }
            }
        }
    }
}

// Composables puros auxiliares
@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.TextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text("Buscar comida o restaurante") },
            modifier = Modifier.weight(1f),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        if (searchText.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = Color.Black,
                modifier = Modifier.clickable { onClearClick() }
            )
        }
    }
}

@Composable
fun FoodCard(food: FoodItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = food.imageRes),
            contentDescription = food.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(130.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5E8C5A))
                .padding(8.dp)
        ) {
            Text(
                text = food.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = food.brand,
                fontSize = 14.sp,
                color = Color(0xFFEEEEEE)
            )
            Text(
                text = "${food.price}Q",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SideBarContent(
    priceRange: Float,
    onPriceRangeChange: (Float) -> Unit,
    onMenuClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5E8C5A))
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "UVG Eats",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
            selected = false,
            onClick = onFavoritesClick
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Create, contentDescription = "Rango de precios") },
            label = { Text("Rango de precios") },
            selected = false,
            onClick = onMenuClick
        )

        Column(modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)) {
            Text(
                text = "Precio máximo: ${priceRange.toInt()}Q",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = priceRange,
                onValueChange = onPriceRangeChange,
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val sampleFoodList = listOf(
        FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera, 30, "Cafetería CIT"),
        FoodItem("Pizza", "Gitane", android.R.drawable.ic_menu_gallery, 35, "Cafetería CIT")
    )
    SearchScreen(
        uiState = SearchUiState(filteredFoodList = sampleFoodList),
        onEvent = {},
        onLoadMore = {}
    )
}