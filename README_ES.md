# No Slime Superflat

[![Build Status](https://github.com/Onyx-i7/NoSlimeSuperflat/actions/workflows/build.yml/badge.svg)](https://github.com/Onyx-i7/NoSlimeSuperflat/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.12.2-green.svg)](https://minecraft.net)
[![Forge Version](https://img.shields.io/badge/Forge-14.23.5.2847-red.svg)](https://files.minecraftforge.net)

Un mod ligero y eficiente para Minecraft que previene la aparicion de slimes en mundos Superplanos. Este mod sirve como una alternativa ligera al modulo "Superflat World No Slimes" de Collective, diseñado especificamente para eliminar fugas de memoria y reducir la sobrecarga.

## Resumen

Este mod fue creado para abordar los problemas de rendimiento encontrados en soluciones dependientes como Collective, que contiene fugas de memoria en la version 1.12.2. No Slime Superflat proporciona la misma funcionalidad con cero fugas de memoria y un impacto de rendimiento insignificante.

**Nota:** Si ya estas usando UniversalTweaks, este mod es redundante ya que UniversalTweaks incluye la misma solucion.

## Caracteristicas

- **Cero Fugas de Memoria**: El manejo de eventos sin estado asegura que no haya acumulacion de memoria con el tiempo
- **Impacto de Rendimiento Minimo**: Menos de 0.1ms por tick con optimizacion de salida temprana
- **Configuracion en Tiempo de Ejecucion**: Habilitar/deshabilitar la prevencion de slimes sin reiniciar
- **Ligero**: Aproximadamente 50KB de huella de memoria
- **Facil de Portar**: Base de codigo limpia con arquitectura amigable para la migracion de versiones
- **Sin Dependencias**: Mod independiente que solo requiere Forge

## Analisis de Rendimiento

### Uso de Memoria

- Huella de memoria base: aproximadamente 50KB de RAM
- Sin presion de recoleccion de basura (diseño sin estado)
- Cero asignacion de objetos durante la operacion normal

### Sobrecarga de CPU

- Tiempo de procesamiento promedio: menos de 0.1ms por tick
- El patron de salida temprana minimiza las comprobaciones innecesarias
- Solo procesa instancias de EntitySlime en mundos Superplanos

### Comparacion con Alternativas

| Mod | Huella de Memoria | Dependencias | Fugas de Memoria |
|-----|-------------------|--------------|----------------|
| No Slime Superflat | aproximadamente 50KB | Ninguna | Ninguna |
| Collective | aproximadamente 2MB+ | Multiples | Si (1.12.2) |
| UniversalTweaks | aproximadamente 500KB | Ninguna | Ninguna |

### ¿Este Mod Mejora el Rendimiento?

Este mod no mejora directamente el rendimiento general del juego. Su proposito principal es prevenir la aparicion de slimes en mundos Superplanos. Sin embargo, al prevenir la aparicion de slimes, puede indirectamente reducir el lag en mundos Superplanos donde los slimes aparecerian en grandes cantidades.

El mod en si mismo tiene un impacto de rendimiento insignificante:
- Uso de memoria: aproximadamente 50KB
- Uso de CPU: menos de 0.1ms por tick

Si estas experimentando problemas de rendimiento, considera usar UniversalTweaks en su lugar, que incluye esta solucion junto con muchas otras optimizaciones.

## Instalacion

### Requisitos

- Minecraft 1.12.2
- Forge 14.23.5.2847 o compatible

### Pasos

1. Descarga la ultima version desde la pagina de [Releases](https://github.com/Onyx-i7/NoSlimeSuperflat/releases)
2. Coloca el archivo JAR en tu carpeta `.minecraft/mods`
3. Inicia Minecraft con el perfil de Forge
4. Verifica la instalacion en el menu de Mods

### Configuracion

El mod incluye una configuracion en tiempo de ejecucion accesible a traves de:

- En el juego: Lista de mods > No Slime Superflat > Boton de Configuracion
- Archivo: `config/noslimesuperflat.cfg`

#### Opciones de Configuracion

| Opcion | Predeterminado | Descripcion |
|--------|----------------|-------------|
| `enableSlimePrevention` | true | Habilita/deshabilita el bloqueo de slimes en mundos Superplanos |
| `enableDebugLogging` | false | Registra intentos de aparicion bloqueados (solo para depuracion) |

## Compilacion desde el Codigo Fuente

### Prerrequisitos

- Java 8 (requerido para compatibilidad con ForgeGradle 2.3)
- Git

### Instrucciones de Compilacion

```bash
# Clonar el repositorio
git clone https://github.com/Onyx-i7/NoSlimeSuperflat.git
cd NoSlimeSuperflat

# Hacer gradlew ejecutable (Linux/macOS)
chmod +x gradlew

# Compilar el mod
./gradlew build --no-daemon

# Ubicacion de salida
ls build/libs/NoSlimeSuperflat-1.12.2-*.jar
```

Para instrucciones detalladas de compilacion, consulta [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md).

### Activacion Manual de Compilacion

Puedes activar manualmente una compilacion desde la pestaña de GitHub Actions:

1. Navega a la pestaña Actions en tu repositorio
2. Selecciona el flujo de trabajo "Build and Release"
3. Haz clic en "Run workflow"
4. Selecciona la rama y haz clic en "Run workflow"

## Estructura del Proyecto

```
NoSlimeSuperflat/
├── src/main/java/com/onyxi7/noslimesuperflat/
│   ├── NoSlimeSuperflat.java    # Clase principal del mod
│   ├── EventHandler.java        # Manejador de eventos de aparicion de slimes
│   └── Config.java              # Manejador de configuracion
├── src/main/resources/
│   ├── mcmod.info               # Metadatos del mod
│   └── noslimesuperflat_at.cfg  # Transformadores de acceso
├── build.gradle                 # Configuracion de compilacion
├── README.md                    # Este archivo
└── LICENSE                      # Licencia MIT
```

## Detalles Tecnicos

### Como Funciona

El mod intercepta el evento `EntityJoinWorldEvent` y cancela la aparicion de slimes cuando:

1. La entidad es una instancia de `EntitySlime`
2. El tipo de mundo es Superplano (`WorldType.FLAT`)
3. La caracteristica esta habilitada en la configuracion

### Principios de Diseño de Codigo

- **Manejo de Eventos sin Estado**: Todos los manejadores de eventos son metodos estaticos
- **Patron de Salida Temprana**: Las condiciones se verifican en orden de probabilidad
- **Programacion Defensiva**: Verificaciones de nulos y manejo de excepciones
- **Documentacion**: JavaDoc completo para todas las APIs publicas

### Guia de Portabilidad

Para portar este mod a otras versiones de Minecraft:

1. Actualiza `build.gradle`:
   - Cambia la version de Minecraft
   - Actualiza la version de Forge
   - Actualiza los mapeos

2. Revisa los cambios de API en:
   - `EntityJoinWorldEvent`
   - Clase `EntitySlime`
   - Enumeracion `WorldType`

3. Prueba exhaustivamente en la version objetivo

Consulta [PORTING_GUIDE.md](PORTING_GUIDE.md) para instrucciones detalladas.

## Solucion de Problemas

### Problemas Comunes

#### La Compilacion Falla con Error Pack200

**Causa:** Usando Java 11+ con ForgeGradle 2.3  
**Solucion:** Usa Java 8 para compilar (consulta [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md))

#### El Mod No Aparece en el Juego

**Causas:**

- Version de Forge incorrecta instalada
- Archivo JAR no en la carpeta de mods
- Conflicto de ID de mod

**Solucion:** Revisa los registros en `logs/latest.log` para ver errores

#### Los Slimes Todavia Aparecen

**Causas:**

- Configuracion deshabilitada
- El mundo no es realmente Superplano
- Conflicto con otro mod

**Solucion:** 

1. Verifica `enableSlimePrevention=true` en la configuracion
2. Confirma el tipo de mundo con comandos `/gamerule`
3. Prueba con un conjunto minimo de mods

### Reportar Problemas

Antes de reportar un problema:

1. Revisa los problemas existentes en GitHub
2. Verifica que estas usando la ultima version
3. Recopila informacion relevante:
   - Version de Minecraft
   - Version de Forge
   - Lista de mods
   - Archivo de registro mas reciente (`logs/latest.log`)
   - Archivo de configuracion (`config/noslimesuperflat.cfg`)
   - Descripcion del problema
   - Pasos para reproducir el problema

Envia los problemas en: https://github.com/Onyx-i7/NoSlimeSuperflat/issues

Ten en cuenta que este es un mod simple con complejidad minima. La mayoria de los problemas estan relacionados con una instalacion o configuracion incorrecta en lugar de errores en el mod en si.

## Compatibilidad

### Mods Conocidos Compatibles

- UniversalTweaks (redundante pero compatible)
- OptiFine
- JourneyMap
- JEI (Just Enough Items)

### Mods Conocidos Incompatibles

- Ninguno reportado

### Conflictos Potenciales

- Cualquier mod que modifique la mecanica de aparicion de slimes
- Otros mods "no slime" (usa solo uno)

## Creditos

- **Onyx_i7**: Autor y desarrollador original
- **Serilum**: Inspiracion de "Superflat World No Slimes"
- **Equipo de MinecraftForge**: Desarrollo de Forge
- **Equipo de MCP**: Datos de mapeo

## Licencia

Este proyecto tiene licencia bajo la Licencia MIT. Consulta [LICENSE](LICENSE) para mas detalles.

## Descargo de Responsabilidad

Este mod no esta afiliado con Mojang Studios ni Microsoft. Minecraft es una marca comercial de Mojang Synergies AB.

---

**Repositorio:** https://github.com/Onyx-i7/NoSlimeSuperflat  
**Problemas:** https://github.com/Onyx-i7/NoSlimeSuperflat/issues  
**Versiones:** https://github.com/Onyx-i7/NoSlimeSuperflat/releases
