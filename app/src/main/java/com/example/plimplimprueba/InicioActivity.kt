package com.example.plimplimprueba

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.plimplimprueba.ui.theme.PlimplimpruebaTheme

class InicioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlimplimpruebaTheme {
                InicioScreen {
                    val intent = Intent(this@InicioActivity, ConectarActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

@Composable
fun InicioScreen(onStartClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Fondo: imagen estática fondo2.png
        Image(
            painter = painterResource(id = R.drawable.fondo2),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título: GIF animado gif2.gif
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.gif1)
                    .decoderFactory(GifDecoder.Factory())
                    .build(),
                contentDescription = "Título animado",
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp), // Ajusta el tamaño si es necesario
                contentScale = ContentScale.Fit
            )

            // Botón de iniciar: imagen estática texto2.png
            Image(
                painter = painterResource(id = R.drawable.texto2),
                contentDescription = "Botón de Iniciar",
                modifier = Modifier
                    .size(150.dp)
                    .padding(top = 16.dp)
                    .clickable { onStartClick() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioScreenPreview() {
    PlimplimpruebaTheme {
        InicioScreen(onStartClick = {})
    }
}