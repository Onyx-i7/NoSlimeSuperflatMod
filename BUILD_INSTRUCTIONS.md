# Build Instructions for No Slime Superflat

## Prerequisites

This mod must be built with **Java 8 (JDK 8)**. Using Java 9 or higher will result in build failures due to ForgeGradle 2.3 compatibility requirements.

### Why Java 8?

1. **Pack200 Removal**: Java 14+ removed the Pack200 compression tool required by ForgeGradle 2.3
2. **ForgeGradle Compatibility**: ForgeGradle 2.3.x is only compatible with Java 8
3. **Minecraft 1.12.2 Toolchain**: The entire modding toolchain for MC 1.12.2 was built around Java 8

### Error Symptoms

If you are using the wrong Java version, you will see errors like:

```
> Task :applyBinaryPatches FAILED
> java/util/jar/Pack200
```

or

```
Unsupported class file major version 55
```

## Setting Up Java 8

### Option 1: Install Java 8

#### Windows
1. Download JDK 8 from [Adoptium](https://adoptium.net/temurin/releases/?version=8)
2. Install and set JAVA_HOME:
   ```cmd
   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8-x64
   set PATH=%JAVA_HOME%\bin;%PATH%
   ```

#### macOS
```bash
brew install openjdk@8
export JAVA_HOME=/usr/local/opt/openjdk@8
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt-get install openjdk-8-jdk
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

### Option 2: Use SDKMAN (Linux/macOS)
```bash
curl -s "https://get.sdkman.io" | bash
sdk install java 8.0.382-tem
sdk use java 8.0.382-tem
```

### Option 3: Use jEnv (macOS/Linux)
```bash
brew install jenv
jenv add /path/to/jdk8
jenv local 8
```

## Verifying Java Version

Before building, verify you are using Java 8:

```bash
java -version
# Should show: openjdk version "1.8.0_xxx" or "8.x.x"

javac -version
# Should show: javac 1.8.0_xxx
```

## Building the Mod

Once Java 8 is properly configured:

```bash
# Clone repository
git clone https://github.com/Onyx-i7/NoSlimeSuperflat.git
cd NoSlimeSuperflat

# Make gradlew executable (Linux/macOS)
chmod +x gradlew

# Build
./gradlew build --no-daemon

# Output will be at:
# build/libs/NoSlimeSuperflat-1.12.2-1.1.0.jar
```

### Build Options

```bash
# Clean build
./gradlew clean build --no-daemon

# Build with stacktrace for debugging
./gradlew build --no-daemon --stacktrace

# Build and refresh dependencies
./gradlew build --no-daemon --refresh-dependencies
```

## GitHub Actions

The GitHub Actions workflow is configured to use Java 8 and includes manual trigger support:

```yaml
on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:  # Manual trigger from GitHub UI
```

To manually trigger a build:
1. Go to the Actions tab in your GitHub repository
2. Select "Build and Release" workflow
3. Click "Run workflow"
4. Select branch and click "Run workflow"

This ensures consistent builds regardless of your local environment.

## Troubleshooting

### Still Getting Pack200 Error?

1. Ensure JAVA_HOME points to JDK 8, not JRE
2. Restart your terminal/IDE after changing Java version
3. Run `./gradlew --stop` to stop any Gradle daemons using wrong Java
4. Clear Gradle cache: `rm -rf ~/.gradle/caches`

### Multiple Java Versions Installed?

Use the full path to Java 8:
```bash
export JAVA_HOME=/path/to/jdk8
export PATH=$JAVA_HOME/bin:$PATH
```

Or use a version manager like SDKMAN or jEnv.

### TLS/SSL Connection Errors

If you encounter connection errors during dependency download:

```bash
./gradlew build --no-daemon -Dhttps.protocols=TLSv1.2
```

## IDE Configuration

### IntelliJ IDEA
1. File → Project Structure → Project
2. Set Project SDK to 1.8
3. Set Project language level to 8

### Eclipse
1. Window → Preferences → Java → Installed JREs
2. Add JDK 8 if not present
3. Set as default

### VS Code
Add to settings.json:
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-1.8",
      "path": "/path/to/jdk8",
      "default": true
    }
  ]
}
```

## Build Outputs

After a successful build, you will find:

- **Mod JAR**: `build/libs/NoSlimeSuperflat-1.12.2-1.1.0.jar`
- **Sources JAR**: `build/libs/NoSlimeSuperflat-1.12.2-1.1.0-sources.jar`
- **Mappings**: `build/deobf_mapped_src/`

## Version Information

Current mod version: **1.1.0**
Target Minecraft: **1.12.2**
Required Forge: **14.23.5.2847+**
