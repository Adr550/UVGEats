package com.uvg.uvgeats.ui.components

//Template base creada con ayuda de IA
//Prompt: Write the code of the UI for an app, you can check it in the image of the mockup (made in figma) attached. Write it in kotlin using jetpack compose components.
//Specially material 3 items. Just the code for the ui, no business logic. Thought it has to be a reponsive UI, not static. Thank you.
//https://Duck.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App() {
    // Use a NavHost or similar to switch between screens
    // For simplicity, we'll just show one screen at a time
    HomeScreen()
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("JVG eats", fontSize = 32.sp, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { /* Navigate to Login */ }) {
            Text("Get Started")
        }
    }
}

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", fontSize = 24.sp, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = "", onValueChange = {}, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = "", onValueChange = {}, label = { Text("Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle Login */ }) {
            Text("Login")
        }
    }
}

@Composable
fun SignupScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", fontSize = 24.sp, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = "", onValueChange = {}, label = { Text("Username") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = "", onValueChange = {}, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = "", onValueChange = {}, label = { Text("Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle Signup */ }) {
            Text("Sign Up")
        }
    }
}

@Composable
fun MenuScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Menu", fontSize = 24.sp, style = MaterialTheme.typography.titleLarge)
        // Add menu items here
        // Example of a card for a menu item
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Burger", fontSize = 20.sp)
                Text("Delicious beef burger", fontSize = 16.sp)
            }
        }
        // Repeat for other menu items
    }
}

@Composable
fun OrderScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Order", fontSize = 24.sp, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle Order */ }) {
            Text("Confirm Order")
        }
    }
}