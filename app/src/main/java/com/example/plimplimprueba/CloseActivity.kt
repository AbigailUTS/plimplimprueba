package com.example.plimplimprueba

import android.content.Intent
import android.media.MediaPlayer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.plimplimprueba.ui.theme.PlimplimpruebaTheme

class CloseActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlimplimpruebaTheme {
                CloseScreen(
                    onBackClick = { navigateToInicio() },
                    onGif4Click = { playSound("gif4") },
                    onGif2Click = { playSound("efecto1") }
                )
            }
        }
    }

    private fun playSound(soundType: String) {
        mediaPlayer?.release()
        mediaPlayer = null
        val soundResource = when (soundType) {
            "gif4" -> R.raw.vuelve
            "efecto1" -> R.raw.efecto1
            else -> null
        }
        if (soundResource != null) {
            mediaPlayer = MediaPlayer.create(this, soundResource)
            mediaPlayer?.setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
            mediaPlayer?.start()
        }
    }

    private fun navigateToInicio() {
        val intent = Intent(this, InicioActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}

@Composable
fun CloseScreen(
    onBackClick: () -> Unit,
    onGif4Click: () -> Unit,
    onGif2Click: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // Fondo principal
        Image(
            painter = painterResource(id = R.drawable.fondo1),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // GIF2 en la parte superior (hijo directo del Box principal)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(R.drawable.gif2).build(),
            contentDescription = "Animación superior",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(200.dp)
                .clickable { onGif2Click() },
            contentScale = ContentScale.Fit
        )

        // Un Box para centrar GIF4
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // GIF4 (hijo del Box centrado y clicable)
            AsyncImage(
                model = R.drawable.gif4,
                contentDescription = "Animación central",
                modifier = Modifier
                    .fillMaxWidth(0.99f)
                    .aspectRatio(1f)
                    .clickable { onGif4Click() },
                contentScale = ContentScale.Fit
            )
        }

        // Botón de inicio en la parte superior derecha, con offset
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = "Regresar al inicio",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 150.dp)
                .padding(24.dp)
                .size(60.dp)
                .clickable { onBackClick() },
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CloseScreenPreview() {
    PlimplimpruebaTheme {
        CloseScreen(
            onBackClick = {},
            onGif4Click = {},
            onGif2Click = {}
        )
    }
}