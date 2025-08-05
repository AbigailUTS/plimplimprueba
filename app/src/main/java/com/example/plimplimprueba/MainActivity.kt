package com.example.plimplimprueba

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plimplimprueba.ui.theme.PlimplimpruebaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlimplimpruebaTheme {
                SplashScreen(
                    onSplashComplete = {
                        val intent = Intent(this, InicioActivity::class.java)
                        startActivity(intent)
                        finish() // Cierra la MainActivity para que no se pueda volver atrás
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(5000) // 5 segundos
        onSplashComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Fondo de imagen
        Image(
            painter = painterResource(id = R.drawable.fondo1),
            contentDescription = "Fondo de inicio",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenedor para el logo y el texto, alineado al centro
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Imagen del logo
            Image(
                painter = painterResource(id = R.drawable.logosplash),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(500.dp)
            )

            // Espacio de separación entre el logo y el texto
            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del texto
            Image(
                painter = painterResource(id = R.drawable.texto1),
                contentDescription = "Texto1",
                // Aumentamos el tamaño de la imagen de texto
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Usa el 90% del ancho de la pantalla
                    .height(150.dp), // Aumentamos la altura para que sea más grande
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    PlimplimpruebaTheme {
        SplashScreen(onSplashComplete = {})
    }
}