package com.uvg.uvgeats.ui.components

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// modelo de datos
data class FoodItem(
    val name: String,
    val brand: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(foodList: List<FoodItem>, onItemClick: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // CoroutineScope para abrir/cerrar el menú
    val scope = rememberCoroutineScope()
    var sliderValue by remember { mutableFloatStateOf(50f) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideBar(
                sliderValue = sliderValue,
                onSliderChange = { newValue ->
                    sliderValue = newValue
                },
                onMenuClick = {
                    // Cierra el menú cuando se hace clic en una opción
                    scope.launch {
                        drawerState.close()
                    }
                    // logica en el futuro
                }
            )
        }
    ) {
        // variables de estado
        val items = remember { foodList.toMutableStateList() }
        val listState = rememberLazyGridState()
        var searchText by remember { mutableStateOf("") } // Usando 'by' para simplificar

        // actualiza la lista cuando se llega al final
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collectLatest { lastVisible ->
                    if (lastVisible == items.size - 1) {
                        items.addAll(foodList)
                    }
                }
        }

        // contenedor de los elementos, scaffold
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        SearchBar(
                            searchText = searchText,
                            onClearClick = { searchText = "" }
                        )
                    },
                    navigationIcon = {
                        // 5. El ícono del menú ahora abre el drawer
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
                items(items) { food ->
                    FoodCard(
                        food = food,
                        onClick = { onItemClick() }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(searchText: String, onClearClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = searchText.ifEmpty { "Búsqueda" },
            color = if (searchText.isEmpty()) Color.Gray else Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Clear",
            tint = Color.Black,
            modifier = Modifier.clickable { onClearClick() }
        )
    }
}

@Composable
fun FoodCard(food: FoodItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .fillMaxWidth()
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
        }
    }
}

@Composable
fun SideBar(
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    onMenuClick: () -> Unit
) {
    // ModalDrawerSheet es el contenedor estándar para el contenido del drawer
    ModalDrawerSheet {
        // Un encabezado simple para el menú
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

        // Espaciador
        Spacer(Modifier.height(12.dp))

        // Opciones del menú
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Favoritos") },
            // contentDescription para accesibilidad
            label = { Text("Favoritos") },
            selected = false,
            onClick = onMenuClick // cerrar el drawer
        )

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Create, contentDescription = "Rango de precios") },
            label = { Text("Rango de precios") },
            selected = false,
            onClick = onMenuClick
        )

        Column(modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)) {
            Text(
                text = "Rango de precios",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = sliderValue,
                onValueChange = onSliderChange, // Llama a la lambda del padre
                valueRange = 0f..100f, // Define el rango (ej. 0 a 100 quetzales)
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val sampleFoodList = listOf(
        FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera),
        FoodItem("Crepa", "Saúl", android.R.drawable.ic_menu_gallery),
        FoodItem("Camarones", "Gitane", android.R.drawable.ic_menu_report_image),
        FoodItem("Lays", "Gitane", android.R.drawable.ic_menu_slideshow),
        FoodItem("Pizza", "Gitane", android.R.drawable.ic_menu_gallery),
        FoodItem("Tacos", "Gitane", android.R.drawable.ic_menu_camera),
        FoodItem("Ensalada", "Gitane", android.R.drawable.ic_menu_report_image),
        FoodItem("Sushi", "Gitane", android.R.drawable.ic_menu_slideshow),
    )
    SearchScreen(foodList = sampleFoodList, {})
}
