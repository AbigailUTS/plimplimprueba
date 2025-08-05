package com.example.plimplimprueba

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import coil.compose.AsyncImage // Importamos AsyncImage para mostrar el GIF
import com.example.plimplimprueba.ui.theme.PlimplimpruebaTheme

class CloseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlimplimpruebaTheme {
                CloseScreen(
                    onBackClick = { navigateToInicio() }
                )
            }
        }
    }

    private fun navigateToInicio() {
        val intent = Intent(this, InicioActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun CloseScreen(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)) // Fondo oscuro por si la imagen de fondo tarda en cargar
    ) {
        // Nuevo fondo: fondo4.png
        Image(
            painter = painterResource(id = R.drawable.fondo4),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Nueva imagen central: el GIF (gif3)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = R.drawable.gif3,
                contentDescription = "Animación central",
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Ajusta el ancho para que no sea demasiado grande
                    .aspectRatio(1f), // Mantiene la relación de aspecto
                contentScale = ContentScale.Fit
            )
        }

        // Nuevo botón de regreso: home.png
        Image(
            painter = painterResource(id = R.drawable.home), // Usamos tu imagen home.png
            contentDescription = "Regresar al inicio",
            modifier = Modifier
                // CAMBIO AQUÍ: Alineación superior
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(60.dp) // Un tamaño más grande para que sea fácil de presionar
                .clickable { onBackClick() },
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CloseScreenPreview() {
    PlimplimpruebaTheme {
        CloseScreen(onBackClick = {})
    }
}