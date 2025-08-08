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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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


class ConectarActivity : ComponentActivity() {

    private lateinit var bluetoothHelper: BluetoothHelper
    private var isConnected by mutableStateOf(false)
    private var deviceName by mutableStateOf<String?>(null)
    private var deviceAddress by mutableStateOf<String?>(null)
    private var availableDevices by mutableStateOf<List<BluetoothDevice>>(emptyList())
    private var isScanning by mutableStateOf(false)

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
                        checkAndRequestPermissions()
                    },
                    onContinueWithoutConnection = {
                        navigateToPlimplimActivity()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Verificamos si la conexión se estableció al regresar de la configuración
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun scanForDevices() {
        isScanning = true
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bonded = bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
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
            // Intentamos conectar de forma silenciosa para verificar el estado
            val result = bluetoothHelper.connectToDevice("Plimplim_ESP32")
            isConnected = result.success
            deviceName = result.deviceName
            deviceAddress = result.deviceAddress

            if (result.success) {
                Toast.makeText(this@ConectarActivity, "Conectado a ${result.deviceName}", Toast.LENGTH_SHORT).show()
                navigateToPlimplimActivity(true, result.deviceName, result.deviceAddress)
            } else {
                Toast.makeText(this@ConectarActivity, "Placa no conectada. Por favor, conéctala manualmente.", Toast.LENGTH_LONG).show()
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
    onContinueWithoutConnection: () -> Unit
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
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.texto3),
                contentDescription = "Logo de Conexión Bluetooth", // Descripción para accesibilidad
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .size(250.dp), // Ajusta el tamaño según necesites, por ejemplo 150.dp
                contentScale = ContentScale.Fit // O ContentScale.Crop, según como quieras que se ajuste
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
                            Text("${device.name ?: "Sin nombre"}\n${device.address}", color = Color.White)
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
            onContinueWithoutConnection = {}
        )
    }
}
