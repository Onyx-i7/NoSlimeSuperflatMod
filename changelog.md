# Changelog

All notable changes to this project will be documented in this file.

## [1.2.0] - 2026-05-28

### 🚀 Características Principales
- **Sistema de Lista Negra de Entidades**: Añadido sistema configurable para prevenir el spawn de entidades específicas (como Magma Cubes o slimes de mods) en mundos Superflat
- **Categoría de Optimización de Rendimiento**: Nueva sección de configuración dedicada al ajuste de rendimiento
  - `maxSlimesPerChunk`: Limita el número máximo de slimes permitidos por chunk
  - `slimeDespawnDistance`: Obliga a desaparecer slimes más allá de cierta distancia para reducir recuento de entidades
  - `reduceSlimeAI`: Opción para reducir actualizaciones de IA para slimes cuando los jugadores están lejos
  - `slimeUpdateFrequency`: Controla con qué frecuencia los slimes actualizan su lógica de IA

### 🛠️ Correcciones
- **Corrección de Compilación**: Arreglado error de importación de `EntityList`. Cambiado del paquete incorrecto de Forge a `net.minecraft.entity.EntityList` para compatibilidad con Minecraft 1.12.2
- **Recuperación de Nombre de Registro**: Reemplazado `EntityRegistry.getRegistry()` incompatible con `EntityList.getKey()` para identificar correctamente tipos de entidad en 1.12.2
- **Inferencia de Tipo**: Arreglado problema de compatibilidad con Java 8 en inicialización de `ArrayList` en carga de configuración
- **Prevención de Fugas de Memoria**: Asegurado que no se mantengan referencias estáticas a objetos World o Entity, previniendo fugas de memoria durante descargas de dimensión

### ⚡ Optimizaciones
- **Lógica de Salida Temprana**: Añadidos retornos inmediatos en manejadores de eventos cuando las características están deshabilitadas para minimizar sobrecarga de CPU
- **Manejo Eficiente de Listas**: Optimizada la verificación de lista negra usando comparación de cadenas en minúsculas para búsquedas más rápidas
- **Caché de Configuración**: Los valores de configuración se leen en variables estáticas para evitar E/S de archivo durante eventos de tiempo de ejecución

### 📝 Documentación
- Actualizado `README.md` con nuevas opciones de configuración de rendimiento
- Actualizado `PORTING_GUIDE.md` con notas sobre cambios en registro de entidades entre versiones
- Añadidos comentarios detallados en código explicando el propósito de cada optimización

### 🗑️ Eliminado
- Eliminadas referencias obsoletas a Pack200 para asegurar compatibilidad con entornos de construcción Java modernos
- Eliminados imports no utilizados y bloques de código heredados de versiones anteriores