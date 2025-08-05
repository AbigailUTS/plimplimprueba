package com.example.plimplimprueba

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.plimplimprueba.ui.theme.PlimplimpruebaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlimplimActivity : ComponentActivity() {

    // Se cambia de 'val' a 'lateinit var' para inicializarlo en onCreate
    private lateinit var bluetoothHelper: BluetoothHelper
    private var isConnected by mutableStateOf(false)
    private var deviceName by mutableStateOf<String?>(null)
    private var deviceAddress: String? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ¡¡SOLUCIÓN AQUÍ!! Inicializamos bluetoothHelper dentro de onCreate
        bluetoothHelper = BluetoothHelper(this)

        // Se extraen los datos del Intent que viene de ConectarActivity
        val initialConnectionState = intent.getBooleanExtra("isConnected", false)
        deviceName = intent.getStringExtra("deviceName")
        deviceAddress = intent.getStringExtra("deviceAddress")
        isConnected = initialConnectionState

        setContent {
            PlimplimpruebaTheme {
                PlimplimScreen(
                    isConnected = isConnected,
                    deviceName = deviceName,
                    onEyeLeftClick = { playSound("ojo") },
                    onEyeRightClick = { playSound("ojo") },
                    onEarLeftClick = { playSound("oreja") },
                    onEarRightClick = { playSound("oreja") },
                    onNoseClick = { playSound("nariz") },
                    onMouthClick = { playSound("boca") },
                    onHairClick = { playSound("cabello") },
                    onCloseClick = { navigateToClose() }
                )
            }
        }
    }

    private fun playSound(soundType: String) {
        mediaPlayer?.release()
        mediaPlayer = null
        val soundResource = when (soundType) {
            "ojo" -> R.raw.ojo
            "oreja" -> R.raw.oreja
            "nariz" -> R.raw.nariz
            "boca" -> R.raw.boca
            "cabello" -> R.raw.cabello
            else -> null
        }
        if (soundResource != null) {
            mediaPlayer = MediaPlayer.create(this, soundResource)
            mediaPlayer?.start()
        }
        if (isConnected) {
            sendCommandToESP32(soundType)
        } else {
            Toast.makeText(this, "No conectado a ESP32", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCommandToESP32(command: String) {
        CoroutineScope(Dispatchers.Main).launch {
            // Se usa la instancia de bluetoothHelper ya inicializada en onCreate
            val success = bluetoothHelper.sendData(command)
            if (!success) {
                Toast.makeText(this@PlimplimActivity, "❌ Error al enviar comando", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToClose() {
        val intent = Intent(this, CloseActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        bluetoothHelper.disconnect()
    }
}

private enum class PartesCuerpo(val imageResId: Int) {
    OJO(R.drawable.ojo),
    OREJA(R.drawable.oreja),
    NARIZ(R.drawable.nariz),
    BOCA(R.drawable.boca),
    CABELLO(R.drawable.cabello)
}

// ... (El resto del código con los Composable, AnimatedImage y Preview se mantiene igual) ...

@Composable
fun PlimplimScreen(
    isConnected: Boolean,
    deviceName: String?,
    onEyeLeftClick: () -> Unit,
    onEyeRightClick: () -> Unit,
    onEarLeftClick: () -> Unit,
    onEarRightClick: () -> Unit,
    onNoseClick: () -> Unit,
    onMouthClick: () -> Unit,
    onHairClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    var rotationGlobosIzquierda by remember { mutableFloatStateOf(0f) }
    var rotationGlobosDerecha by remember { mutableFloatStateOf(0f) }

    var showEyeLeft by remember { mutableStateOf(false) }
    var showEyeRight by remember { mutableStateOf(false) }
    var showEarLeft by remember { mutableStateOf(false) }
    var showEarRight by remember { mutableStateOf(false) }
    var showNose by remember { mutableStateOf(false) }
    var showMouth by remember { mutableStateOf(false) }
    var showHair by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo4),
            contentDescription = "Fondo Plimplim",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.globos),
                contentDescription = "Decoración superior izquierda",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 15.dp, y = 100.dp)
                    .height(250.dp)
                    .graphicsLayer(rotationZ = rotationGlobosIzquierda)
                    .clickable {
                        coroutineScope.launch {
                            val targetRotation = rotationGlobosIzquierda + 360f * 2
                            animate(
                                initialValue = rotationGlobosIzquierda,
                                targetValue = targetRotation,
                                animationSpec = tween(durationMillis = 1000)
                            ) { value, _ ->
                                rotationGlobosIzquierda = value
                            }
                            rotationGlobosIzquierda = 0f
                        }
                    },
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.globos),
                contentDescription = "Decoración superior derecha",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-15).dp, y = 100.dp)
                    .height(250.dp)
                    .graphicsLayer(rotationZ = rotationGlobosDerecha)
                    .clickable {
                        coroutineScope.launch {
                            val targetRotation = rotationGlobosDerecha - 360f * 2
                            animate(
                                initialValue = rotationGlobosDerecha,
                                targetValue = targetRotation,
                                animationSpec = tween(durationMillis = 1000)
                            ) { value, _ ->
                                rotationGlobosDerecha = value
                            }
                            rotationGlobosDerecha = 0f
                        }
                    },
                contentScale = ContentScale.Fit
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(45.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cerrar),
                contentDescription = "Botón para salir",
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onCloseClick() },
                contentScale = ContentScale.Fit
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.cara),
                contentDescription = "Cara Plimplim",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .offset(x = (-20).dp, y = 35.dp)
                    .size(15.dp)
                    .clickable {
                        onEyeLeftClick()
                        showEyeLeft = true
                        coroutineScope.launch {
                            delay(2000)
                            showEyeLeft = false
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .offset(x = 20.dp, y = 35.dp)
                    .size(15.dp)
                    .clickable {
                        onEyeRightClick()
                        showEyeRight = true
                        coroutineScope.launch {
                            delay(2000)
                            showEyeRight = false
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .offset(x = (-55).dp, y = (40).dp)
                    .size(15.dp)
                    .clickable {
                        onEarLeftClick()
                        showEarLeft = true
                        coroutineScope.launch {
                            delay(2000)
                            showEarLeft = false
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .offset(x = 55.dp, y = 40.dp)
                    .size(15.dp)
                    .clickable {
                        onEarRightClick()
                        showEarRight = true
                        coroutineScope.launch {
                            delay(2000)
                            showEarRight = false
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .offset(x = 0.dp, y = 55.dp)
                    .size(20.dp)
                    .clickable {
                        onNoseClick()
                        showNose = true
                        coroutineScope.launch {
                            delay(2000)
                            showNose = false
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .offset(x = 0.dp, y = 75.dp)
                    .size(15.dp, 15.dp)
                    .clickable {
                        onMouthClick()
                        showMouth = true
                        coroutineScope.launch {
                            delay(2000)
                            showMouth = false
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .offset(x = 0.dp, y = (-45).dp)
                    .size(100.dp, 30.dp)
                    .clickable {
                        onHairClick()
                        showHair = true
                        coroutineScope.launch {
                            delay(2000)
                            showHair = false
                        }
                    }
            )
            AnimatedImage(
                isVisible = showEyeLeft,
                imageResId = PartesCuerpo.OJO.imageResId,
                modifier = Modifier.offset(x = (0).dp, y = 200.dp)
            )
            AnimatedImage(
                isVisible = showEyeRight,
                imageResId = PartesCuerpo.OJO.imageResId,
                modifier = Modifier.offset(x = 0.dp, y = 200.dp)
            )
            AnimatedImage(
                isVisible = showEarLeft,
                imageResId = PartesCuerpo.OREJA.imageResId,
                modifier = Modifier.offset(x = (0).dp, y = 200.dp)
            )
            AnimatedImage(
                isVisible = showEarRight,
                imageResId = PartesCuerpo.OREJA.imageResId,
                modifier = Modifier.offset(x = 0.dp, y = 200.dp)
            )
            AnimatedImage(
                isVisible = showNose,
                imageResId = PartesCuerpo.NARIZ.imageResId,
                modifier = Modifier.offset(x = 0.dp, y = 200.dp)
            )
            AnimatedImage(
                isVisible = showMouth,
                imageResId = PartesCuerpo.BOCA.imageResId,
                modifier = Modifier.offset(x = 0.dp, y = 200.dp)
            )
            AnimatedImage(
                isVisible = showHair,
                imageResId = PartesCuerpo.CABELLO.imageResId,
                modifier = Modifier.offset(x = 0.dp, y = (200).dp)
            )
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(R.drawable.gif3).build(),
            contentDescription = "Animación inferior",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(150.dp),
            contentScale = ContentScale.Fit
        )

        if (isConnected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color.Green, shape = CircleShape)
                    )

                    deviceName?.let { name ->
                        Text(
                            text = "Conectado: $name",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedImage(
    isVisible: Boolean,
    imageResId: Int,
    modifier: Modifier
) {
    if (isVisible) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = modifier
                .size(100.dp, 60.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlimplimScreenPreview() {
    PlimplimpruebaTheme {
        PlimplimScreen(
            isConnected = false,
            deviceName = null,
            onEyeLeftClick = {}, onEyeRightClick = {},
            onEarLeftClick = {}, onEarRightClick = {},
            onNoseClick = {}, onMouthClick = {},
            onHairClick = {}, onCloseClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlimplimScreenConnectedPreview() {
    PlimplimpruebaTheme {
        PlimplimScreen(
            isConnected = true,
            deviceName = "ESP32_TEST",
            onEyeLeftClick = {}, onEyeRightClick = {},
            onEarLeftClick = {}, onEarRightClick = {},
            onNoseClick = {}, onMouthClick = {},
            onHairClick = {}, onCloseClick = {}
        )
    }
}