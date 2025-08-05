# Guía de Uso - Bluetooth ESP32

## 📱 Funcionalidad Bluetooth Implementada

### ✅ **Características Implementadas:**

1. **Escaneo de Dispositivos Bluetooth**
   - Busca automáticamente dispositivos ESP32 cercanos
   - Muestra lista de dispositivos encontrados con nombre y dirección MAC
   - Filtra solo dispositivos con nombre visible

2. **Selección de Dispositivo**
   - Interfaz visual con radio buttons para seleccionar ESP32
   - Muestra información del dispositivo (nombre y dirección MAC)
   - Validación antes de conectar

3. **Conexión Bluetooth**
   - Conexión real usando BluetoothSocket
   - Manejo de errores de conexión
   - Indicador visual de estado de conexión
   - **Mensajes Toast** que confirman éxito o fallo de conexión

4. **Comunicación con ESP32**
   - Envío de comandos por Bluetooth
   - Comandos específicos para cada parte del cuerpo:
     - `ojo` - Para los ojos
     - `oreja` - Para las orejas
     - `nariz` - Para la nariz
     - `boca` - Para la boca
     - `cabello` - Para el cabello
   - **Reconexión automática** si se pierde la conexión

5. **Gestión de Permisos**
   - Permisos automáticos para Android 12+ (API 31+)
   - Permisos compatibles para versiones anteriores
   - Solicitud automática de permisos faltantes

6. **Mensajes de Estado**
   - ✅ **Conexión exitosa**: "Conectado exitosamente a [nombre]"
   - ❌ **Error de conexión**: "Error al conectar con [nombre]"
   - ❌ **Error de comunicación**: "Error de conexión con ESP32"
   - 🔄 **Reconexión automática** cuando se detecta pérdida de conexión

## 🔧 **Configuración del ESP32**

### **Código ESP32 Requerido:**

```cpp
#include "BluetoothSerial.h"

BluetoothSerial SerialBT;

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32_Plimplim"); // Nombre del dispositivo
  Serial.println("ESP32 Bluetooth iniciado");
}

void loop() {
  if (SerialBT.available()) {
    String command = SerialBT.readStringUntil('\n');
    command.trim();
    
    // Procesar comandos
    if (command == "ojo") {
      // Activar LED o actuador para ojos
      Serial.println("Comando recibido: ojo");
    }
    else if (command == "oreja") {
      // Activar LED o actuador para orejas
      Serial.println("Comando recibido: oreja");
    }
    else if (command == "nariz") {
      // Activar LED o actuador para nariz
      Serial.println("Comando recibido: nariz");
    }
    else if (command == "boca") {
      // Activar LED o actuador para boca
      Serial.println("Comando recibido: boca");
    }
    else if (command == "cabello") {
      // Activar LED o actuador para cabello
      Serial.println("Comando recibido: cabello");
    }
  }
}
```

## 📋 **Flujo de Uso:**

### **1. Iniciar Aplicación**
- La app inicia en `MainActivity` (splash screen)
- Navega automáticamente a `InicioActivity`

### **2. Ir a Conectar**
- En `InicioActivity`, presionar el botón "Conectar"
- Navega a `ConectarActivity`

### **3. Configurar Bluetooth**
- Presionar el icono de Bluetooth
- La app solicitará permisos si es necesario
- Habilitar Bluetooth si está desactivado

### **4. Buscar Dispositivos**
- La app escaneará automáticamente dispositivos ESP32
- Se mostrará una lista de dispositivos encontrados

### **5. Seleccionar y Conectar**
- Seleccionar el ESP32 deseado de la lista
- Presionar "Conectar a [nombre del dispositivo]"
- **Esperar mensaje de confirmación**:
  - ✅ "Conectado a [nombre]" = Conexión exitosa
  - ❌ "Error al conectar a [nombre]" = Conexión fallida

### **6. Usar la Aplicación**
- Presionar "Continuar conectado"
- Navega a `PlimplimActivity`
- **Al iniciar PlimplimActivity**:
  - Se intentará establecer la conexión Bluetooth automáticamente
  - Se mostrará mensaje de estado de conexión
- Al tocar las áreas del cuerpo, se enviarán comandos al ESP32

## 🔍 **Troubleshooting:**

### **Problemas Comunes:**

1. **No se encuentran dispositivos**
   - Verificar que el ESP32 esté encendido y en modo Bluetooth
   - Verificar que el nombre del ESP32 sea visible
   - Verificar permisos de ubicación

2. **Error de conexión**
   - Verificar que el ESP32 no esté conectado a otro dispositivo
   - Reiniciar el ESP32
   - Verificar que el código ESP32 esté ejecutándose
   - **Revisar logs de Android Studio** para más detalles

3. **Comandos no llegan**
   - Verificar conexión Bluetooth
   - Verificar que el ESP32 esté procesando los comandos
   - Revisar logs del ESP32
   - **La app intentará reconectar automáticamente**

4. **Estado "conectado" no aparece en menú del teléfono**
   - **SOLUCIONADO**: La app ahora establece la conexión real en `PlimplimActivity`
   - Verificar que aparezca el mensaje "✅ Conectado exitosamente"
   - Si no aparece, revisar logs para identificar el problema

### **🔧 Solución al Problema de Conexión:**

#### **Problema:** "No aparece como conectado en el menú del teléfono"

#### **Causas Posibles:**
1. **Conexión no se establece correctamente**
2. **Permisos faltantes**
3. **ESP32 no está en modo Bluetooth**
4. **Dispositivo ya conectado a otro aparato**

#### **Pasos para Solucionar:**

1. **Verificar ESP32:**
   ```cpp
   // Asegúrate de que el ESP32 tenga este código
   #include "BluetoothSerial.h"
   BluetoothSerial SerialBT;
   
   void setup() {
     Serial.begin(115200);
     SerialBT.begin("ESP32_Plimplim");
     Serial.println("ESP32 Bluetooth iniciado");
   }
   ```

2. **Verificar Permisos:**
   - Ir a Configuración > Aplicaciones > Plimplimprueba > Permisos
   - Habilitar: Ubicación, Bluetooth, Bluetooth (conectar)
   - En Android 12+: Habilitar "Detectar dispositivos cercanos"

3. **Verificar Estado Bluetooth:**
   - Ir a Configuración > Bluetooth
   - Asegurarse de que Bluetooth esté activado
   - Verificar que el ESP32 aparezca en dispositivos emparejados

4. **Reiniciar Proceso:**
   - Cerrar la aplicación completamente
   - Reiniciar el ESP32
   - Volver a abrir la aplicación
   - Seguir el flujo de conexión

5. **Revisar Logs:**
   - En Android Studio, buscar en Logcat:
     - `BluetoothHelper` - Para logs de conexión
     - `ConectarActivity` - Para logs de selección
     - `PlimplimActivity` - Para logs de reconexión

#### **Mensajes de Debug Importantes:**

**✅ Conexión Exitosa:**
```
BluetoothHelper: ✅ Conectado exitosamente a ESP32_Plimplim
ConectarActivity: ✅ Conexión exitosa a ESP32_Plimplim
```

**❌ Error de Conexión:**
```
BluetoothHelper: ❌ Error al conectar: Connection refused
ConectarActivity: ❌ Error al conectar a ESP32_Plimplim
```

### **🚨 SOLUCIÓN ESPECÍFICA PARA PERMISOS:**

#### **Problema:** "Mensaje de permisos requeridos"

#### **Solución Paso a Paso:**

1. **Verificar Permisos en Configuración:**
   ```
   Configuración > Aplicaciones > Plimplimprueba > Permisos
   ✅ Ubicación
   ✅ Bluetooth
   ✅ Bluetooth (conectar)
   ✅ Detectar dispositivos cercanos (Android 12+)
   ```

2. **Si los permisos no aparecen:**
   - Ir a Configuración > Aplicaciones > Plimplimprueba
   - Tocar "Permisos"
   - Habilitar manualmente todos los permisos de Bluetooth

3. **Para Android 12+ (API 31+):**
   - Asegurarse de que "Detectar dispositivos cercanos" esté habilitado
   - Este permiso es obligatorio para el escaneo Bluetooth

4. **Reiniciar la Aplicación:**
   - Cerrar completamente la app
   - Volver a abrirla
   - Intentar conectar nuevamente

5. **Verificar Logs:**
   - En Android Studio > Logcat
   - Buscar mensajes de `ConectarActivity`
   - Identificar qué permisos específicos están faltando

#### **Mensajes de Error de Permisos:**

**Permisos Denegados:**
```
ConectarActivity: Algunos permisos fueron denegados
ConectarActivity: Permisos necesarios denegados: BLUETOOTH_SCAN, BLUETOOTH_CONNECT
```

**Permisos Faltantes:**
```
ConectarActivity: Permiso BLUETOOTH_SCAN no concedido
ConectarActivity: Permiso de escaneo Bluetooth requerido
```

#### **Solución Manual de Permisos:**

Si la app no solicita permisos automáticamente:

1. **Ir a Configuración del Sistema:**
   ```
   Configuración > Aplicaciones > Plimplimprueba > Permisos
   ```

2. **Habilitar Manualmente:**
   - **Ubicación** - Necesario para escaneo Bluetooth
   - **Bluetooth** - Para funcionalidad básica
   - **Bluetooth (conectar)** - Para establecer conexiones
   - **Detectar dispositivos cercanos** - Para Android 12+

3. **Verificar Bluetooth del Sistema:**
   ```
   Configuración > Bluetooth
   ✅ Habilitar Bluetooth
   ✅ Permitir que las apps usen Bluetooth
   ```

## 📱 **Permisos Requeridos:**

### **Android 12+ (API 31+):**
- `BLUETOOTH_SCAN` - Para escanear dispositivos
- `BLUETOOTH_CONNECT` - Para conectar a dispositivos
- `ACCESS_FINE_LOCATION` - Para escaneo Bluetooth

### **Android < 12:**
- `BLUETOOTH` - Funcionalidad básica
- `BLUETOOTH_ADMIN` - Administración Bluetooth
- `ACCESS_FINE_LOCATION` - Para escaneo Bluetooth

## 🔧 **Archivos Principales:**

- **`ConectarActivity.kt`** - Manejo de Bluetooth y selección de dispositivos (mejorado)
- **`BluetoothHelper.kt`** - Clase helper para comunicación Bluetooth
- **`PlimplimActivity.kt`** - Envío de comandos al ESP32 (con reconexión automática)
- **`AndroidManifest.xml`** - Permisos Bluetooth

## 🎯 **Mejoras Implementadas:**

### **Solución al Problema de Conexión:**
1. **Conexión automática** en `PlimplimActivity` al iniciar
2. **Mensajes Toast** que confirman éxito o fallo de conexión
3. **Reconexión automática** si se pierde la conexión
4. **Mejor manejo de errores** con logs detallados
5. **Verificación de estado** de la conexión Bluetooth
6. **Logs de debugging** para identificar problemas
7. **Manejo mejorado de permisos** con mensajes específicos

### **Mensajes de Estado:**
- ✅ **Conexión exitosa**: "Conectado exitosamente a [nombre]"
- ❌ **Error de conexión**: "Error al conectar con [nombre]"
- ❌ **Error de comunicación**: "Error de conexión con ESP32"
- 🔄 **Reconexión automática** cuando se detecta pérdida

## 🎯 **Próximos Pasos:**

1. **Implementar reconexión automática** ✅ (Implementado)
2. **Agregar configuración de velocidad de comunicación**
3. **Implementar recepción de datos del ESP32**
4. **Agregar indicadores de señal Bluetooth**
5. **Implementar guardado de dispositivos favoritos** 