package com.example.plimplimprueba

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.*

data class BluetoothConnectionResult(
    val success: Boolean,
    val deviceName: String?,
    val deviceAddress: String?
)

class BluetoothHelper(private val context: Context) {

    private val tag = "BluetoothHelper"
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    companion object {
        @Volatile
        private var bluetoothSocket: BluetoothSocket? = null
        @Volatile
        private var outputStream: OutputStream? = null
    }

    // UUID estándar para SPP (Serial Port Profile)
    private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun isEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    fun checkBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun connectToDevice(deviceName: String): BluetoothConnectionResult {
        return withContext(Dispatchers.IO) {
            Log.d(tag, "Intentando conectar con el dispositivo: $deviceName")
            if (!checkBluetoothPermissions() || bluetoothAdapter == null) {
                Log.e(tag, "Permisos de Bluetooth no concedidos o adaptador no disponible.")
                return@withContext BluetoothConnectionResult(false, null, null)
            }

            bluetoothAdapter.cancelDiscovery()

            val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            Log.d(tag, "Dispositivos emparejados: ${pairedDevices.map { it.name }}")
            val esp32Device: BluetoothDevice? = pairedDevices.find { it.name == deviceName }

            if (esp32Device == null) {
                Log.e(tag, "Dispositivo '$deviceName' no encontrado en la lista de emparejados.")
                return@withContext BluetoothConnectionResult(false, null, null)
            }

            // Cerrar conexiones previas
            try {
                outputStream?.close()
            } catch (e: IOException) {
                Log.w(tag, "Error al cerrar outputStream anterior: ${e.message}")
            }
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                Log.w(tag, "Error al cerrar bluetoothSocket anterior: ${e.message}")
            }

            try {
                Log.d(tag, "Creando socket RFCOMM con UUID SPP...")
                bluetoothSocket = esp32Device.createRfcommSocketToServiceRecord(sppUuid)
                bluetoothSocket?.connect()

                if (bluetoothSocket?.isConnected == true) {
                    outputStream = bluetoothSocket?.outputStream
                    Log.d(tag, "Conexión exitosa a ${esp32Device.name} (${esp32Device.address}) ${outputStream == null}")
                    sendData("All")

                    return@withContext BluetoothConnectionResult(true, esp32Device.name, esp32Device.address)
                } else {
                    Log.e(tag, "El socket no se conectó correctamente.")
                    bluetoothSocket?.close()
                    return@withContext BluetoothConnectionResult(false, null, null)
                }
            } catch (e: IOException) {
                Log.e(tag, "Excepción durante la conexión: ${e.message}")
                try {
                    bluetoothSocket?.close()
                } catch (e2: IOException) {
                    Log.e(tag, "Error al cerrar el socket después de la excepción: ${e2.message}")
                }
                return@withContext BluetoothConnectionResult(false, null, null)
            }
        }
    }
    /**
     * Verifica si la conexión Bluetooth está activa y lista para enviar datos.
     */
    fun isConnected(): Boolean {
        return bluetoothSocket?.isConnected == true && outputStream != null
    }

    suspend fun sendData(data: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.e(tag, "Dato: ${bluetoothSocket?.isConnected}")
                Log.e(tag, "Dato2: ${outputStream != null}")
                if (bluetoothSocket?.isConnected == true && outputStream != null) {
                    Log.d(tag, "Enviando datos: $data")
                    outputStream?.write(data.toByteArray())
                    outputStream?.flush()
                    true
                } else {
                    Log.e(tag, "No se pueden enviar datos: el socket no está conectado o el outputStream es nulo.")
                    false
                }
            } catch (e: IOException) {
                Log.e(tag, "Error al enviar datos!!  :   ${e.message}")
                //disconnect() // Cierra la conexión si hay error de IO
                false
            }
        }
    }

    fun disconnect() {
        try {
            Log.d(tag, "Cerrando conexión Bluetooth...")
            outputStream?.close()
        } catch (e: IOException) {
            Log.w(tag, "Error al cerrar outputStream: ${e.message}")
        } finally {
            outputStream = null
        }
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.w(tag, "Error al cerrar bluetoothSocket: ${e.message}")
        } finally {
            bluetoothSocket = null
        }
        Log.d(tag, "Conexión cerrada exitosamente")
    }
}
