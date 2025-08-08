package com.example.plimplimprueba

import android.content.Intent
import android.media.MediaPlayer // Importa MediaPlayer para la reproducción de sonido
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Necesario para la pantalla completa
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Necesario para ImageRequest.Builder
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.GifDecoder // Necesario para decodificar GIFs
import coil.request.ImageRequest // Necesario para cargar imágenes con Coil
import com.example.plimplimprueba.ui.theme.PlimplimpruebaTheme

class InicioActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null // Declara el objeto MediaPlayer para reproducir sonidos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el modo de pantalla completa
        setContent {
            PlimplimpruebaTheme {
                // Llama a InicioScreen pasando las lambdas para los eventos de clic
                InicioScreen(
                    onStartClick = {
                        val intent = Intent(this@InicioActivity, ConectarActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onGif2Click = {
                        playSound("gif2")
                    },
                    onGif1Click = {
                        playSound("gif1")
                    }
                )
            }
        }
    }

    // Función para reproducir sonidos según el tipo
    private fun playSound(soundType: String) {
        mediaPlayer?.release() // Libera cualquier reproductor anterior para evitar fugas de memoria
        mediaPlayer = null // Asegura que el reproductor se inicialice como nulo
        val soundResource = when (soundType) {
            "gif2" -> R.raw.efecto1
            "gif1" -> R.raw.efecto1
            else -> null // Si el tipo de sonido no coincide, no se reproduce nada
        }
        if (soundResource != null) {
            mediaPlayer = MediaPlayer.create(this, soundResource) // Crea el MediaPlayer con el recurso de sonido
            mediaPlayer?.setOnCompletionListener {
                // Configura un listener para liberar el MediaPlayer una vez que el sonido haya terminado de reproducirse
                it.release()
                mediaPlayer = null
            }
            mediaPlayer?.start() // Inicia la reproducción del sonido
        }
    }

    // Sobrescribe onDestroy para asegurar que el MediaPlayer se libere cuando la actividad se destruya
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Libera el MediaPlayer para evitar problemas de memoria
    }
}

@Composable
fun InicioScreen(onStartClick: () -> Unit, onGif2Click: () -> Unit, onGif1Click: () -> Unit ) {
    Box(
        modifier = Modifier.fillMaxSize(), // El Box principal ocupa toda la pantalla
        contentAlignment = Alignment.Center // Centra el contenido por defecto dentro del Box
    ) {
        // 1. Fondo de pantalla (fondo2.png)
        Image(
            painter = painterResource(id = R.drawable.fondo2), // Carga la imagen de fondo
            contentDescription = "Fondo de la pantalla de inicio",
            modifier = Modifier.fillMaxSize(), // La imagen de fondo ocupa todo el tamaño del Box
            contentScale = ContentScale.Crop // Escala la imagen para que cubra todo el área sin distorsión
        )

        // 2. GIF superior (gif2)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.gif2) // Carga el GIF2
                .decoderFactory(GifDecoder.Factory()) // Necesario para que Coil reproduzca GIFs
                .build(),
            contentDescription = "Animación superior del título",
            modifier = Modifier
                .fillMaxWidth() // El GIF ocupa todo el ancho de la pantalla
                .align(Alignment.TopCenter) // Alinea el GIF en la parte superior central del Box
                .height(200.dp) // Define una altura específica para el GIF superior
                .clickable { onGif2Click() }, // Hace que el GIF sea clicable y dispare el evento onGif2Click
            contentScale = ContentScale.Fit // Ajusta el GIF dentro de sus límites manteniendo su relación de aspecto
        )

        // 3. Columna para el contenido central (gif1 y botón)
        // Esta Column se centrará dentro del Box principal debido al contentAlignment del Box
        Column(
            modifier = Modifier.padding(top = 120.dp), // Añade un padding superior para bajar la columna
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // <-- Alinea el contenido en la parte superior de la columna
        ) {
            // Título: GIF animado (gif1), ahora más grande
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.gif1)
                    .decoderFactory(GifDecoder.Factory())
                    .build(),
                contentDescription = "Título animado de Plimplim",
                modifier = Modifier
                    .width(600.dp)
                    .height(300.dp)
                    .clickable { onGif1Click() },
                contentScale = ContentScale.FillBounds
            )

            // Botón de iniciar: imagen estática (texto1.png), ahora más grande
            Image(
                painter = painterResource(id = R.drawable.texto2), // Carga la imagen del botón
                contentDescription = "Botón de Iniciar la aplicación",
                modifier = Modifier
                    .size(200.dp) // Tamaño aumentado para el botón (200x200 dp)
                    .padding(top = 16.dp) // Espacio superior entre el GIF1 y el botón
                    .clickable { onStartClick() } // Hace que el botón sea clicable y dispare el evento onStartClick
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioScreenPreview() {
    PlimplimpruebaTheme {
        InicioScreen(onStartClick = {}, onGif2Click = {}, onGif1Click = {})
    }
}