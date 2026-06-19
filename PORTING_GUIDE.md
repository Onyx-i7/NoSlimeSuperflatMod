# Porting Guide - No Slime Superflat

## Overview

This guide helps developers port No Slime Superflat to different Minecraft versions or fix build issues.

## Build Requirements

### For Minecraft 1.12.2

- **JDK**: Java 8
- **ForgeGradle**: 2.3-SNAPSHOT
- **Gradle**: 4.10.3
- **Mappings**: snapshot_20171003

### For Minecraft 1.16.5

- **JDK**: Java 8 (build), Java 8+ (runtime)
- **ForgeGradle**: 5.1.+
- **Gradle**: 7.5.1+
- **Mappings**: official 1.16.5
- **Forge Version**: 36.2.39+

## Java Compatibility Note (v1.2.0+)

Starting from version 1.2.0, this project no longer relies on Pack200 compression.
- **Recommended JDK**: Java 8 for building (due to ForgeGradle 2.3 limitations), but the output jar is compatible with Java 14+ runtimes.
- **Future Proofing**: Code structure avoids deprecated Java APIs, ensuring forward compatibility.

### Common Build Errors and Solutions

#### Error: java/util/jar/Pack200

**Cause**: Using Java 9+ instead of Java 8. Pack200 was deprecated in Java 11 and removed in Java 14+.

**Solution**:
```bash
# Install Java 8
# Ubuntu/Debian
sudo apt install openjdk-8-jdk

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

# Or use SDKMAN
sdk install java 8.0.382-tem
sdk use java 8.0.382-tem
```

**Error**: Could not find forge-userdev.jar

**Cause**: Forge version not available in Maven repositories.

**Solution**: Try alternative Forge versions: \
1.12.2-14.23.5.2847 (recommended) \
1.12.2-14.23.5.2768 \
1.12.2-14.23.5.2859

Update in build.gradle:
```groovy
minecraft {
    version = "1.12.2-14.23.5.2847"  // Change this line
}
```


**Error**: TLS/SSL Connection Issues \
**Cause**: Outdated TLS protocols or certificate issues \
**Solution**:
```bash
./gradlew build --no-daemon -Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStoreType=JKS
``` 
\
**Error**: Listener for event class RegisterCommandsEvent takes an argument that is not a subtype of IModBusEvent (1.16.5) \
**Cause**: `RegisterCommandsEvent` belongs to the Forge Event Bus, not the Mod Event Bus. \
**Solution**: Register on the correct bus:
```java
// Wrong (Mod Bus)
modEventBus.addListener(this::onRegisterCommands);

// Correct (Forge Bus)
MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
```


**Error**: cannot find symbol getChunkMap() or IChunkProvider (1.16.5+) \
**Cause**: API changes in chunk provider access. \
**Solution**: Use ServerChunkProvider with direct field access:
```java
if (world.getChunkSource() instanceof net.minecraft.world.server.ServerChunkProvider) {
    return ((net.minecraft.world.server.ServerChunkProvider) world.getChunkSource()).generator 
        instanceof net.minecraft.world.gen.FlatChunkGenerator;
}
```

### Porting to Other Versions
#### To Minecraft 1.11.2 or 1.10.2
1. Update build.gradle:
```groovy
minecraft {
    version = "1.11.2-13.20.1.2588"  // Example for 1.11.2
    mappings = "snapshot_20171003"    // Check MCP mappings for your version
}
```
2. Check EntitySlime class name - may differ between versions
3. Verify WorldType.FLAT constant - ensure it exists in target version

#### To Minecraft 1.13+ (Major Changes Required)
Minecraft 1.13+ uses a completely different Forge modding system:
1. Update to ForgeGradle 5+ (5.1+ for 1.16.5)
2. Use new event system (@SubscribeEvent changes, separate Mod Bus and Forge Bus)
3. Update entity spawning mechanics (1.14+ changed spawn logic)
4. Use modern Java versions (Java 16+ for 1.17+, Java 17+ for 1.18+)
5. Replace mcmod.info with mods.toml (TOML format in META-INF/)
6. Update configuration system (ForgeConfigSpec instead of Configuration)

Example for 1.16.5:
```groovy
// build.gradle
plugins {
    id 'net.minecraftforge.gradle' version '5.1.+'
}

minecraft {
    mappings channel: 'official', version: '1.16.5'
}

dependencies {
    minecraft 'net.minecraftforge:forge:1.16.5-36.2.39'
}
```

### Code Patches for Version Porting
#### Patch: Entity Position Logging (1.12.2 vs 1.14+)
**1.12.2**:
```java
event.getEntity().posX  // Direct field access
```
**1.14+**:
```java
event.getEntity().getPosX()  // Getter method
```
#### Patch: WorldType Check
**1.12.2**:
```java
event.getWorld().getWorldInfo().getTerrainType() == WorldType.FLAT
```
**1.16.5**:
```java
if (world.getChunkSource() instanceof net.minecraft.world.server.ServerChunkProvider) {
    return ((net.minecraft.world.server.ServerChunkProvider) world.getChunkSource()).generator 
        instanceof net.minecraft.world.gen.FlatChunkGenerator;
}
```
#### Patch: Event Bus Registration
**1.12.2**:
```java
@Mod.EventBusSubscriber
public class EventHandler { ... }
```
**1.13+**
```java
@Mod.EventBusSubscriber(modid = YourMod.MOD_ID)
public class EventHandler { ... }
```
#### Patch: Configuration System
**1.12.2**:
```java
Configuration config = new Configuration(event.getSuggestedConfigurationFile());
config.load();
boolean value = config.getBoolean("option", "category", true, "Description");
config.save();
```
**1.16.5**:
```java
ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
ForgeConfigSpec.BooleanValue value = builder.comment("Description")
    .define("option", true);
ForgeConfigSpec spec = builder.build();
ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec);
```

<div align="center">

### ⚠️ THIS SECTION IS STILL IN PROGRESS ​🛠️​​

</div>



### Testing Checklist
After porting:
- Mod loads without crashes
- Slimes do not spawn in Superflat worlds
- Slimes still spawn in normal worlds
- No memory leaks (monitor heap usage)
- Debug logging works correctly
- Configuration file generates correctly
- mods.toml (1.16.5+) or mcmod.info (1.12.2) displays proper information
- Commands work correctly (/noslimesuperflat stats, /noslimesuperflat reload)
- Mod list screen does not crash when selecting the mod

### Resources
- [Minecraft Forge Documentation](https://docs.minecraftforge.net/en/1.21.x/)
- [MCP Mappings](http://www.modcoderpack.com/)
- [Forge Files Archive](https://files.minecraftforge.net/net/minecraftforge/forge/)
- [ForgeGradle Repository](https://github.com/MinecraftForge/ForgeGradle)
- [Minecraft Wiki - Tutorials](https://minecraft.wiki/w/Tutorials)
- [Forge Javadoc](https://nekoyue.github.io/ForgeJavaDocs-NG/)

### Support
If you encounter issues while porting:
1. Check existing GitHub Issues
2. Review Forge documentation for your target version
3. Compare with similar mods that support multiple versions
4. Ensure you are using the correct Java version for the target Minecraft version

### Version Compatibility Matrix
