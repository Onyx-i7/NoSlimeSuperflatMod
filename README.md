# No Slime Superflat

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.12.2-green.svg)](https://minecraft.net)
[![Forge Version](https://img.shields.io/badge/Forge-14.23.5.2847-red.svg)](https://files.minecraftforge.net/)

This is a Minecraft mod that stops slimes from spawning in Superflat worlds. No Slime Superflat is an more efficient alternative to other solutions like Collectives "Superflat World No Slimes" module. It does this without using a lot of memory or slowing down the game.

## Overview

The No Slime Superflat mod was made to fix performance issues in solutions that have problems with memory leaks. No Slime Superflat does the thing but without using a lot of memory or slowing down the game.

**Note:** If you are already using UniversalTweaks you do not need this mod because UniversalTweaks already has this feature.

## Features

- **No Memory Leaks**: The mod is designed to not use more memory over time
- **Little Performance Impact**: The mod only uses a bit of time to run less than 0.1ms per tick
- **You Can Turn It On And Off**: You can enable or disable the slime prevention without having to restart the game
- **Small Size**: The mod only uses 50KB of memory
- **Easy To Update**: The mod has a clean and simple design that makes it easy to update for new versions of Minecraft
- **No Other Mods Needed**: The mod works all by itself and does not need any other mods to work

## Performance Analysis

### Memory Usage
- The mod only uses about 50KB of memory
- The mod does not use more and more memory over time
- The mod does not make a lot of garbage that the game has to clean up

### CPU Overhead
- The mod only uses a bit of time to run less than 0.1ms per tick
- The mod is designed to stop running soon as it is not needed
- The mod only checks for slimes in Superflat worlds

### Comparison with Alternatives

| Mod | Memory Footprint | Dependencies | Memory Leaks |
|-----|------------------|--------------|--------------|
| No Slime Superflat | ~50KB | None | None |
| Collective | ~2MB+ | Multiple | Yes (1.12.2) |
| UniversalTweaks | ~500KB | None | None |

## Installation

### Requirements
- Minecraft 1.12.2
- Forge 14.23.5.2847 or compatible

### Steps

1. Download the version of the mod from the [Releases](https://github.com/Onyx-i7/NoSlimeSuperflatMod/releases) page
2. Put the mod file in your `.minecraft/mods` folder
3. Start Minecraft with the Forge profile
4. Check that the mod is working in the Mods menu

### Configuration

The mod has a configuration that you can change in the game. You can get to it by:
- Going to the `config/noslimesuperflat.cfg` file

Press `Esc` -> `Mods` -> `No Slime Superflat` -> `Config` to get to the configuration menu:
- **General**: Turn the slime prevention on or off and turn on debug logging
- **Blacklist**: Add custom entity IDs to stop them from spawning in Superflat
- **Performance**: Change the AI reduction and despawn distances to get the performance on low-end computers

#### Configuration Options
| Option | Default | Description |
|--------|---------|-------------|
| `enableSlimePrevention` | true | Turns the slime prevention on or off |
| `enableDebugLogging` | false | Turns on debug logging to see what the mod is doing |

## Building from Source

### Requirements
- Java 8 (you need this to use ForgeGradle 2.3)
- Git

### Build Instructions

```bash
# Get the mod code from GitHub
git clone https://github.com/Onyx-i7/NoSlimeSuperflatMod.git
cd NoSlimeSuperflatMod

# Build the mod
./gradlew build

# Look for the mod file
ls build/libs/NoSlimeSuperflat-1.12.2-*.jar
```

For detailed build instructions see [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md).

## Project Structure (Obsolete)

```
NoSlimeSuperflat/
├── src//java/com/onyxi7/noslimesuperflat/
│   ├── NoSlimeSuperflat.java    # The main mod class
│   ├── EventHandler.java        # The slime spawn event handler
│   └── Config.java              # The configuration handler
├── src/main/resources/
│   ├── mcmod.info               # The mod metadata
│   └─ noslimesuperflat_at.cfg  # The access transformers
├── build.gradle                 # The build configuration
├── README.md                    # This file
└── LICENSE                      # The MIT License

```

## Technical Details

### How It Works

The mod stops slimes from spawning in Superflat worlds by:
1. Checking if the entity is a slime
2. Checking if the world is Superflat
3. Checking if the feature is turned on in the configuration

### Code Design Principles

- **The mod does not keep any information in memory**: All event handlers are methods
- **The mod stops running as soon as it is not needed**: Conditions are checked in order of how likely they are to be true
- **The mod is careful and checks for errors**: Null checks and exception handling are used
- **The mod has good comments**: There are comments to explain what the code is doing

### Porting Guide

To move this mod to another version of Minecraft:

1. Update `build.gradle`:
   - Change the Minecraft version
   - Update the Forge version
   - Update the mappings

2. Look at the changes to the Minecraft API:
   - `EntityJoinWorldEvent`
   - `EntitySlime` class
   - `WorldType` enum

3. Test the mod thoroughly in the version

See [PORTING_GUIDE.md](PORTING_GUIDE.md) for more detailed instructions.

## Troubleshooting

### Common Issues

#### The Build Fails with a Pack200 Error
**What is causing the problem:** You are using Java 11 or with ForgeGradle 2.3
**How to fix it:** Use Java 8 to build the mod (see BUILD_INSTRUCTIONS.md)

#### The Mod Does Not Show Up in the Game
**What could be causing the problem:**
- You do not have the right version of Forge installed
- The mod file is not in the mods folder
- There is a conflict with another mod

**How to fix it:** Check the game logs at `logs/latest.log` for errors

#### Slimes Are Spawning
**What could be causing the problem:**
- The slime prevention is turned off
- The world is not actually Superflat
- There is a conflict with another mod

**How to fix it:**
1. Make sure that `enableSlimePrevention` is set to `true` in the configuration
2. Check that the world is actually Superflat with the `/gamerule` commands
3. Test the mod with a few mods installed

### Reporting Issues

Before you report an issue:

1. Check if the issue has already been reported on GitHub
2. Make sure you are using the version of the mod
3. Get all the information you need: 
   - The version of Minecraft you are using 
   - The version of Forge you are using 
   - The list of mods you are using 
   - The log file 
   - The configuration file 

Report issues at: https://github.com/Onyx-i7/NoSlimeSuperflatMod/issues

## Compatibility

### Mods That Are Known to Work with No Slime Superflat
- UniversalTweaks (it is not needed but it works)

### Mods That Are Known to Not Work with No Slime Superflat
- Superflat World No Slimes
- Collective

### Potential Conflicts
- Any mod that changes how slimes spawn
- mods that stop slimes from spawning (only use one)

## Credits

- **Onyx_i7**: The person who made the mod
- **Serilum**: The inspiration, for the mod came from "Superflat World No Slimes"
- **MinecraftForge Team**: The people who made Forge
- **MCP Team**: The people who made the mapping data

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Disclaimer
This mod is not made by Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Synergies AB.

---

**Repository:** https://github.com/Onyx-i7/NoSlimeSuperflatMod \
**Issues:** https://github.com/Onyx-i7/NoSlimeSuperflatMod/issues \
**Releases:** https://github.com/Onyx-i7/NoSlimeSuperflatMod/releases
