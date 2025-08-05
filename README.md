# Gundata Game (Alpha Version)

**Project Phase**: Alpha  
**Team**: Ravi, Uday, Sudheer  

## Project Overview

Gundata is a dice prediction game where players bet tokens on a number between 1 and 6. After each round, six dice are rolled. If a player’s chosen number appears the most frequently, they win tokens based on a multiplier.

This **Alpha Version** demonstrates the core game logic using Java CLI (no GUI yet). The game supports:
- Multiple players
- Choose on only one number per round
- Token tracking
- Owner profit calculation
- Winner declaration (handles tie cases)

## Team Contributions

| Member   | File(s) Implemented            |
|----------|--------------------------------|
| **Ravi**   | `Main.java`, `GundataGame.java` |
| **Uday**   | `Player.java`                  |
| **Sudheer**| `DiceRoller.java`              |

## Requirements

- Java JDK 8 or later  
- Git (for collaboration)  

## How to Compile and Run

### Compile
```bash
javac Main.java GundataGame.java Player.java DiceRoller.java
