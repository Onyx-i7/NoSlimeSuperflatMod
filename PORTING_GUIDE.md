# Porting Guide - No Slime Superflat

## Overview

This guide helps developers port No Slime Superflat to different Minecraft versions or fix build issues.

## Build Requirements

### For Minecraft 1.12.2 (Current Target)

- **JDK**: Java 8
- **ForgeGradle**: 2.3-SNAPSHOT
- **Gradle**: 4.10.3
- **Mappings**: snapshot_20171003

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

#### Error: Could not find forge-userdev.jar

**Cause**: Forge version not available in Maven repositories.

**Solution**: Try alternative Forge versions:
- `1.12.2-14.23.5.2847` (recommended)
- `1.12.2-14.23.5.2768`
- `1.12.2-14.23.5.2859`

Update in `build.gradle`:
```groovy
minecraft {
    version = "1.12.2-14.23.5.2847"  // Change this line
}
```

#### Error: TLS/SSL Connection Issues

**Cause**: Outdated TLS protocols or certificate issues.

**Solution**:
```bash
./gradlew build --no-daemon -Dhttps.protocols=TLSv1.2 -Djavax.net.ssl.trustStoreType=JKS
```

## Porting to Other Versions

### To Minecraft 1.11.2 or 1.10.2

1. **Update build.gradle**:
```groovy
minecraft {
    version = "1.11.2-13.20.1.2588"  // Example for 1.11.2
    mappings = "snapshot_20171003"    // Check MCP mappings for your version
}
```

2. **Check EntitySlime class name** - may differ between versions

3. **Verify WorldType.FLAT constant** - ensure it exists in target version

### To Minecraft 1.13+ (Major Changes Required)

Minecraft 1.13+ uses a completely different Forge modding system:

1. **Update to ForgeGradle 5+**
2. **Use new event system** (`@SubscribeEvent` changes)
3. **Update entity spawning mechanics** (1.14+ changed spawn logic)
4. **Use modern Java versions** (Java 16+ for 1.17+, Java 17+ for 1.18+)

Example for 1.16.5:
```groovy
// build.gradle
plugins {
    id 'net.minecraftforge.gradle' version '5.1.+'
}

minecraft {
    mappings channel: 'official', version: '1.16.5'
}
```

## Code Patches for Version Porting

### Patch: Entity Position Logging (1.12.2 vs 1.14+)

**1.12.2**:
```java
event.getEntity().posX  // Direct field access
```

**1.14+**:
```java
event.getEntity().getPosX()  // Getter method
```

### Patch: WorldType Check

**1.12.2**:
```java
event.getWorld().getWorldType() == WorldType.FLAT
```

**1.16+**:
```java
event.getLevel().getLevelData().isFlatWorld()
```

### Patch: Event Bus Registration

**1.12.2**:
```java
@Mod.EventBusSubscriber
public class EventHandler { ... }
```

**1.13+**:
```java
@Mod.EventBusSubscriber(modid = YourMod.MOD_ID)
public class EventHandler { ... }
```

## Testing Checklist

After porting:

- [ ] Mod loads without crashes
- [ ] Slimes do not spawn in Superflat worlds
- [ ] Slimes still spawn in normal worlds
- [ ] No memory leaks (monitor heap usage)
- [ ] Debug logging works correctly
- [ ] Compatible with UniversalTweaks (no conflicts)
- [ ] Configuration file generates correctly
- [ ] mcmod.info displays proper information

## Resources

- [Minecraft Forge Documentation](https://mcforge.readthedocs.io/)
- [MCP Mappings](http://www.modcoderpack.com/)
- [Forge Files Archive](https://files.minecraftforge.net/)
- [ForgeGradle Repository](https://github.com/MinecraftForge/ForgeGradle)
- [Minecraft Wiki - Tutorials](https://minecraft.wiki/w/Tutorials)

## Support

If you encounter issues while porting:

1. Check existing [GitHub Issues](https://github.com/Onyx-i7/NoSlimeSuperflatMod/issues)
2. Review Forge documentation for your target version
3. Compare with similar mods that support multiple versions
4. Ensure you are using the correct Java version for the target Minecraft version

## Version Compatibility Matrix

| Minecraft Version | Java Version | ForgeGradle | Status      |
|-------------------|--------------|-------------|-------------|
| 1.10.2            | Java 8       | 2.3         | Untested    |
| 1.11.2            | Java 8       | 2.3         | Untested    |
| 1.12.2            | Java 8       | 2.3         | Supported   |
| 1.13.2            | Java 8       | 3.4         | Not Planned |
| 1.14.4            | Java 8       | 4.1         | Not Planned |
| 1.15.2            | Java 8       | 4.2         | Not Planned |
| 1.16.5            | Java 11      | 5.x         | Not Planned |
| 1.17.1            | Java 16      | 5.x         | Not Planned |
| 1.18.2+           | Java 17      | 5.x/6.x     | Not Planned |
