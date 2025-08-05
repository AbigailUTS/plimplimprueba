package com.example.plimplimprueba

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

object PermissionHelper {
    private const val TAG = "PermissionHelper"

    /**
     * Obtiene los permisos requeridos según la versión de Android
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Verifica si todos los permisos necesarios están concedidos
     */
    fun hasAllPermissions(context: Context): Boolean {
        val requiredPermissions = getRequiredPermissions()
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Obtiene la lista de permisos que faltan
     */
    fun getMissingPermissions(context: Context): Array<String> {
        val requiredPermissions = getRequiredPermissions()
        return requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    /**
     * Verifica si un permiso específico está concedido
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Obtiene una descripción legible de los permisos
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.BLUETOOTH_SCAN -> "Escanear dispositivos Bluetooth"
            Manifest.permission.BLUETOOTH_CONNECT -> "Conectar a dispositivos Bluetooth"
            Manifest.permission.BLUETOOTH -> "Usar Bluetooth"
            Manifest.permission.BLUETOOTH_ADMIN -> "Administrar Bluetooth"
            Manifest.permission.ACCESS_FINE_LOCATION -> "Acceso a ubicación precisa"
            else -> "Permiso desconocido"
        }
    }

    /**
     * Registra el estado de los permisos para debugging
     */
    fun logPermissionStatus(context: Context) {
        val requiredPermissions = getRequiredPermissions()
        Log.d(TAG, "=== Estado de Permisos Bluetooth ===")
        
        requiredPermissions.forEach { permission ->
            val isGranted = hasPermission(context, permission)
            val description = getPermissionDescription(permission)
            Log.d(TAG, "${if (isGranted) "✅" else "❌"} $description: $isGranted")
        }
        
        if (hasAllPermissions(context)) {
            Log.d(TAG, "✅ Todos los permisos están concedidos")
        } else {
            val missing = getMissingPermissions(context)
            Log.w(TAG, "❌ Permisos faltantes: ${missing.joinToString(", ")}")
        }
        Log.d(TAG, "=====================================")
    }
} 