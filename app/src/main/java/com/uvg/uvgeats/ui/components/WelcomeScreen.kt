package com.uvg.uvgeats.ui.components

//import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    // eventos
    onCreateAccountClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    // contenedor de los elementos, scaffold
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder temporal del logo
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                contentAlignment = Alignment.Center
            ) {
                //Icon(
                  //  imageVector = Icons.Default.Restaurant,
                    //contentDescription = "Logo UVG Eats",
                    //tint = Color.White,
                    //modifier = Modifier.size(80.dp)
                //)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Bienvenido a UVG eats", style = MaterialTheme.typography.headlineMedium)

            Text(
                "Explora los menús de tus restaurantes favoritos sin prisas o colas",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onCreateAccountClick },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crea Cuenta")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    WelcomeScreen()
}

