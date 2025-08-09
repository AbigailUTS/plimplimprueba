package com.example.plimplimprueba

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.plimplimprueba.ui.theme.PlimplimpruebaTheme
import kotlinx.coroutines.launch
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult


class ConectarActivity : ComponentActivity() {

    private lateinit var bluetoothHelper: BluetoothHelper
    private var isConnected by mutableStateOf(false)
    private var deviceName by mutableStateOf<String?>(null)
    private var deviceAddress by mutableStateOf<String?>(null)
    private var availableDevices by mutableStateOf<List<BluetoothDevice>>(emptyList())
    private var isScanning by mutableStateOf(false)

    // Lanzador para solicitar permisos de Bluetooth
    private val requestBluetoothPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) @androidx.annotation.RequiresPermission(
            android.Manifest.permission.BLUETOOTH_CONNECT
        ) { permissions ->
            if (permissions.all { it.value }) {
                scanForDevices()
            } else {
                Toast.makeText(this, "Permisos de Bluetooth denegados", Toast.LENGTH_SHORT).show()
            }
        }

    // NUEVO: Lanzador para solicitar permisos de la cámara
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startQRScanner()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show()
            }
        }

    // NUEVO: Lanzador para el resultado del escáner de QR
    private val qrCodeScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intentResult: IntentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        val scannedData = intentResult.contents
        if (scannedData != null) {
            // Aquí puedes manejar el resultado del QR
            Toast.makeText(this, "QR Escaneado: $scannedData", Toast.LENGTH_LONG).show()
            // Por ejemplo, podrías intentar conectar al dispositivo con el nombre del QR
            connectToDeviceFromQR(scannedData)
        } else {
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bluetoothHelper = BluetoothHelper(this)

        setContent {
            PlimplimpruebaTheme {
                ConectarScreen(
                    isConnected = isConnected,
                    deviceName = deviceName,
                    availableDevices = availableDevices,
                    isScanning = isScanning,
                    onDeviceSelected = { device ->
                        connectToSelectedDevice(device)
                    },
                    onScanClick = {
                        if (availableDevices.isNotEmpty()) {
                            availableDevices = emptyList()
                            isScanning = false
                        } else {
                            checkAndRequestPermissions()
                        }
                    },
                    onContinueWithoutConnection = {
                        navigateToPlimplimActivity()
                    },
                    // NUEVO: Pasa la función para escanear el QR
                    onScanQRClick = {
                        checkAndRequestCameraPermission()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (bluetoothHelper.isEnabled()) {
            checkForConnectionStatus()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        if (missingPermissions.isEmpty()) {
            scanForDevices()
        } else {
            requestBluetoothPermissions.launch(missingPermissions)
        }
    }

    // NUEVO: Función para verificar y solicitar permisos de la cámara
    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startQRScanner()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // NUEVO: Función para iniciar el escáner de QR
    private fun startQRScanner() {
        // La biblioteca zxing requiere que la activity sea la que maneje el resultado,
        // por eso usamos el lanzador de actividades para envolver su intent
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el código QR del dispositivo")
        integrator.setCameraId(0)  // Usa la cámara trasera
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(true)
        val intent = integrator.createScanIntent()
        qrCodeScannerLauncher.launch(intent)
    }

    // NUEVO: Función para intentar conectar a un dispositivo a partir de un valor de QR
    private fun connectToDeviceFromQR(scannedData: String) {
        lifecycleScope.launch {
            val result = bluetoothHelper.connectToDevice(scannedData)
            isConnected = result.success
            deviceName = result.deviceName
            deviceAddress = result.deviceAddress

            if (result.success) {
                Toast.makeText(this@ConectarActivity, "Conectado a ${result.deviceName} vía QR", Toast.LENGTH_SHORT).show()
                navigateToPlimplimActivity(true, result.deviceName, result.deviceAddress)
            } else {
                Toast.makeText(this@ConectarActivity, "No se pudo conectar con el dispositivo del QR.", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun scanForDevices() {
        isScanning = true
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bonded = try {
            bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permiso de Bluetooth denegado. No se pueden obtener dispositivos emparejados.", Toast.LENGTH_LONG).show()
            isScanning = false
            return
        }

        availableDevices = bonded
        isScanning = false
        if (bonded.isEmpty()) {
            Toast.makeText(this, "No se encontraron dispositivos emparejados.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToSelectedDevice(device: BluetoothDevice) {
        lifecycleScope.launch {
            val result = bluetoothHelper.connectToDevice(device.name ?: device.address)
            isConnected = result.success
            deviceName = result.deviceName
            deviceAddress = result.deviceAddress

            if (result.success) {
                Toast.makeText(this@ConectarActivity, "Conectado a ${result.deviceName}", Toast.LENGTH_SHORT).show()
                navigateToPlimplimActivity(true, result.deviceName, result.deviceAddress)
            } else {
                Toast.makeText(this@ConectarActivity, "No se pudo conectar.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkForConnectionStatus() {
        lifecycleScope.launch {
            val result = bluetoothHelper.connectToDevice("Plimplim_ESP32")
            isConnected = result.success
            deviceName = result.deviceName
            deviceAddress = result.deviceAddress

            if (result.success) {
                Toast.makeText(this@ConectarActivity, "Conectado a ${result.deviceName}", Toast.LENGTH_SHORT).show()
                navigateToPlimplimActivity(true, result.deviceName, result.deviceAddress)
            } else {
                //Toast.makeText(this@ConectarActivity, "Placa no conectada. Por favor, conéctala manualmente.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openBluetoothSettings() {
        Toast.makeText(this, "Conecta la placa Plimplim_ESP32 manualmente.", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        startActivity(intent)
    }

    private fun navigateToPlimplimActivity(
        connected: Boolean = false,
        name: String? = null,
        address: String? = null
    ) {
        val intent = Intent(this@ConectarActivity, PlimplimActivity::class.java).apply {
            putExtra("isConnected", connected)
            putExtra("deviceName", name)
            putExtra("deviceAddress", address)
        }
        startActivity(intent)
        finish()
    }
}


@Composable
fun ConectarScreen(
    isConnected: Boolean,
    deviceName: String?,
    availableDevices: List<BluetoothDevice>,
    isScanning: Boolean,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onScanClick: () -> Unit,
    onContinueWithoutConnection: () -> Unit,
    // NUEVO: Recibe la función para el escáner de QR
    onScanQRClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo3),
            contentDescription = "Fondo de Plimplim",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Image(
                painter = painterResource(id = R.drawable.texto3),
                contentDescription = "Logo de Conexión Bluetooth",
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .size(250.dp),
                contentScale = ContentScale.Fit
            )
            
            Button(
                onClick = onScanClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0057B8))
            ) {
                Text(if (isScanning) "Buscando..." else "Buscar dispositivos", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (availableDevices.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(Color(0xFFFF9800))
                        .padding(8.dp)
                ) {
                    availableDevices.forEach { device ->
                        Button(
                            onClick = { onDeviceSelected(device) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) {
                            Text(
                                "${device.name ?: "Sin nombre"}\n${device.address}",
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Estado: ${if (isConnected) "Conectado a $deviceName" else "Desconectado"}",
                color = if (isConnected) Color.Green else Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onContinueWithoutConnection,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0057B8))
            ) {
                Text("Continuar sin conexión", color = Color.White)
            }
            // NUEVO: Botón para escanear QR
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onScanQRClick,
                modifier = Modifier
                    .size(100.dp), // Define un tamaño fijo para el botón cuadrado
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Fondo transparente
                contentPadding = PaddingValues(0.dp) // Elimina el padding interno del botón
            ) {
                Box(
                    contentAlignment = Alignment.Center, // Centra el icono dentro del Box
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr),
                        contentDescription = "Escanear QR",
                        tint = Color.Blue, // Cambia el color del icono si lo deseas
                        modifier = Modifier.size(100.dp) // Ajusta el tamaño del icono
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConectarScreenPreview() {
    PlimplimpruebaTheme {
        ConectarScreen(
            isConnected = false,
            deviceName = null,
            availableDevices = emptyList(),
            isScanning = false,
            onDeviceSelected = {},
            onScanClick = {},
            onContinueWithoutConnection = {},
            onScanQRClick = {}
        )
    }
}