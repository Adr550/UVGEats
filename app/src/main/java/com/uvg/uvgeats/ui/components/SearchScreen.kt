package com.uvg.uvgeats.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.uvg.uvgeats.R

// modelo de datos para productos
data class FoodItem(
    val name: String,
    val brand: String,
    val imageRes: Int
)

// pantalla principal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(foodList: List<FoodItem>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar()
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Acción menú */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(foodList) { food ->
                FoodCard(food)
            }
        }
    }
}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Búsqueda",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Clear",
            tint = Color.Black,
            modifier = Modifier.clickable { /* TODO: limpiar búsqueda */ }
        )
    }
}

@Composable
fun FoodCard(food: FoodItem) {
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
                .background(Color(0xFF5E8C5A)) // Verde como en la imagen
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
@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val sampleFoodList = listOf(
        FoodItem("Hamburguesa", "Gitane", android.R.drawable.ic_menu_camera),
        FoodItem("Crepa", "Gitane", android.R.drawable.ic_menu_gallery),
        FoodItem("Camarones", "Gitane", android.R.drawable.ic_menu_report_image),
        FoodItem("Lays", "Gitane", android.R.drawable.ic_menu_slideshow)
    )
    SearchScreen(foodList = sampleFoodList)
}


