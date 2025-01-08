# Quem

A quest-style game engine for Minecraft

![Version](https://img.shields.io/badge/version-0.4.0-blue?style=flat-square)
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

# Quest File

A file that defines a quest (type of quest) is called a quest file.

These files are written in json (*.json) or yaml (*.yaml or *.yml) and can be placed anywhere under the `./plugins/quem` hierarchy.

### Json

The root element is an object.

```json
{}
```

Specifies the title of the quest. You can use decorations using the `&` symbol.

```json
{
  "title": "&aExample Quest"
}
```

Specifies the description of the quest.
Of course, you can also use the text decoration.

```json
{
  "description": [
    "&7This is a sample."
  ]
}
```

Next is the quest icon.
There are two ways to specify this.

Simply specify from the item:

```json
{
  "icon": "minecraft:paper"
}
```

To specify custom model data or the presence or absence of auras:

```json
{
  "icon": {
    "type": "minecraft:paper",
    "model": 1,
    "aura": true
  }
}
```

This is the progress needed to complete the quest.

```json
{
  "requirement": 8
}
```

This is the category of the quest.
It can be one of "general", "story", "daily" or "event".

```json
{
  "category": "general"
}
```

This is the starting point of the quest.
(Yaw and pitch are optional)

```json
{
  "location": {
    "world": "minecraft:overworld",
    "x": 0,
    "y": 0,
    "z": 0,
    "yaw": 0,
    "pitch": 0
  }
}
```

You can also specify waypoints for your quest. (This is optional)
Navigation will be based on these.

The method for specifying the position is the same as for `location`.

```json
{
  "points": [
    {
      "world": "minecraft:overworld",
      "x": 0,
      "y": 0,
      "z": 0
    }
  ]
}
```

Finally, the script. (This is optional)

We will cover how to actually write a script in a later chapter.

```json
{
  "scripts": {
    "start+2s": [
      "$foreach tellraw ${player} \"Hello, ${player}!\""
    ]
  }
}
```

### Yaml

The basics remain the same when writing in Yaml.

So here I'll just give you the big picture.

```yaml
title: "&aExample Quest"
description:
  - "This is a sample."
icon:
  type: "minecraft:paper"
  model: 1
  aura: true
requirement: 8
category: "general"
location:
  world: "minecraft:overworld"
  x: 0
  y: 0
  z: 0
  yaw: 0
  pitch: 0
points:
  - world: "minecraft:overworld"
    x: 0
    y: 0
    z: 0
scripts:
  start+2s:
    - "$ foreach tellraw ${player} \"Hello, ${player}!\""
```

## Scripting

Scripts perform actions based on triggers that occur during quests.

The script must be named as follows:

```{trigger}``` or ```{trigger}+{delay}s```

There are the following types of triggers:

| Name       | When called                                   |
|:-----------|:----------------------------------------------|
| start      | At the start of the quest                     |
| end        | When a quest ends, regardless of how it ended |
| complete   | When a quest is completed                     |
| incomplete | When a quest is not completed                 |

The script code is represented as a string array.

You can write Minecraft commands directly:

```json
"@start": [
  "command arg1 arg2"
]
```

You can also use special syntax by starting the token with a "$" sign:

```json
"@start": [
  "$ run command arg1 arg2",
  "$ declare var = Hello!",
  "$ foreach player in players tellraw ${player} \"${var}\""""
]
```
