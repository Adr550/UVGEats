package com.uvg.uvgeats.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    food: FoodItem
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu de ${food.name}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atras")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // imagen principal
            Image(
                painter = painterResource(id = food.imageRes),
                contentDescription = food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF5E8C5A))
                    .padding(16.dp)
            ) {
                Text("Precio: 30Q", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Ubicación: Cafetería CIT", fontSize = 16.sp, color = Color.Black)
                Text("Restaurante: ${food.brand}", fontSize = 16.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))


            Text(
                text = "El combo incluye una porción de papas fritas, una bebida a elección y la ${food.name.lowercase()} tradicional con tocino.",
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    val navController = rememberNavController()
    val sampleFood = FoodItem(
        name = "Hamburguesa",
        brand = "Gitane",
        imageRes = android.R.drawable.ic_menu_camera
    )
    DetailScreen(navController = navController, food = sampleFood)
}
