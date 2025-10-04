package com.uvg.uvgeats.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//import com.example.uvgeats.R

@Composable
fun LoginScreen(
    // eventos
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
) {
    // variables de estado
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // contenedor de elementos, columna
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
       // Image(
         // painter = painterResource(id = R.drawable.ic_hungry), // tu recurso
           // contentDescription = "Hungry",
        //modifier = Modifier.size(140.dp)
        //)

        Spacer(modifier = Modifier.height(16.dp))

        Text("¿Hambriento?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("¡Inicia sesión para comenzar!")

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { onForgotPasswordClick }
            ) { Text("¿Olvidaste tu contraseña?", color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onLoginClick },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginClick = {},
        onForgotPasswordClick = {}
    )
}