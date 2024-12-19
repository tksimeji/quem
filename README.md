# Quem

A quest-style game engine for Minecraft

![Version](https://img.shields.io/badge/version-0.1.0-blue?style=flat-square)
![Licence](https://img.shields.io/badge/licence-MIT-red?style=flat-square)

This plugin depends on [Visualkit](https://github.com/tksimeji/visualkit).

# Commands

Required arguments are represented by `<>` and optional arguments by `[]`.

`/quem debug <type>`: Start the quest in debug mode.

`/quem grant <player> <type>`: Unlock any quest type.

`/quem list`: Lists the quest types that are loaded.

`/quem load <file>`: Loads the quest type from the file name.

`/quem phase <player> [=+-*/%]<phase>`: Identify the quest from the player and change the phase.

`/quem reload [type]`: Re-explore any quest type or `/plugins/quem` and reload.

`/quem revoke <player> <type>`: Lock any quest type.

`/quem unload <type>`: Unload any quest type.

# Quest definition

Quests are loaded by placing a json format definition file in `/plugins/quem`.

> [!TIPS]
> You can also create directories in `/plugins/quem` to organize your definition files.

## Syntax

Here we use "//" to annotate, which cannot be used in real json.

```json
{
  "title": "&aZombie Hunter",
  "description": [
    "&7Simple job of killing zombies."
  ],
  "icon": {
    // You could also simply write: "icon": "minecraft:rotten_flesh
    "type": "minecraft:rotten_flesh",
    "model": 1, // You can specify custom model data
    "aura": true // You can specify whether to have an enchantment aura.
  },
  "requirement": 3, // Progression required to complete the quest
  "category": "general", // You can choose from general, story, daily, and event
  "location": {
    // Starting point of the quest
    "world": "minecraft:overworld",
    "x": 0,
    "y": 0,
    "z": 0,
    "yaw": 0, // Note: This is an arbitrary value
    "pitch": 0 // Note: This is an arbitrary value
  },
  "points": [
    // Note: This is an arbitrary value
    // Quest Checkpoints
    {
      "world": "minecraft:overworld",
      "x": 0,
      "y": 0,
      "z": 0
    }
  ],
  "scripts": {
    // Note: This is an arbitrary value
    "@start+1s": [],
    "@end": [],
    "@complete": [],
    "@incomplete": [],
    "@progress": []
  }
}
```

## Scripting

A script in Quem is a command that is executed in response to a trigger that occurs in a quest.
You can write a script by adding the scripts property to the definition file.

The script must be named as follows:

```@{trigger}``` or ```@{trigger}+{delay}s```

You can write Minecraft commands directly.

```json
"@start": [
  "command arg1 arg2"
]
```

You can use Quem's own syntax by starting with the "$" sign.

```json
"@start": [
  "$ run command arg1 arg2", // If you omit the $, it will be interpreted internally as.
  "$ foreach tellraw ${player} \"Hello, ${player}!\""""
]
```
