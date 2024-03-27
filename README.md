# Games

## Table of Contents
- [Introduction](#introduction)
- [Snake](#snake)
- [Escape](#escape)
- [Arkanoid](#arkanoid)
- [Tetris](#tetris)
- [How to play](#how-to-play)

## Introduction
This repository has games created in the programming class either as a class lesson or a work assignment.

It's possible to play the following games:
- Snake
- Escape
- Arkanoid
- Tetris

## Snake
The requirements for the snake game are:

The work consists of continuing the development of the program made in the second work to make a playable version of the 
traditional Snake game.

In this version, the snake already eats apples. 
For each apple eaten, the snake increases its length by five elements, and the score is incremented. 
The new elements appear during the next moves in the previous position of the snake's head.

At the beginning of the game, the snake already has five elements. 
There are already some brick blocks in the corners of the arena, and an apple appears in a random free position. 
When the snake eats the apple, another appears immediately in another random free position as long as there are free positions.

New brick blocks continue to appear every 5 seconds as long as there are free positions. 
The snake stops moving when it goes against a brick block or against its own body.

The program presents a status bar at the bottom where it shows the current length of the snake, the score, and other information.

The game ends when the snake can no longer move in any direction, displaying the message “You Win” on the status bar if 
the snake has reached the minimum length of 60 elements or “You Lose” otherwise.

The only variable of the `main()` function is of type `Game`, which now must be the following aggregate type:
```kotlin
data class Game(val snake: Snake, val wall: List<Position>, val apple: Position?, val score: Int, ...)
```
where the ellipsis represents other necessary properties.

The `Snake` type will now be the following type:
```kotlin
data class Snake(val body: List<Position>, val dir: Direction, val stopped: Boolean, val toGrow: Int, ...)
```
where the position of the head is the first element of the list; the position of the tail is the last element of the list, 
and the `toGrow` property is the number of elements that still need to be added to the snake in the next moves.

In carrying out this work, the rules already stated in previous works must continue to be respected: 
- Avoid mutability; 
- Do not repeat code; 
- Do not make functions too extensive; 
- Do not repeat values with the same meaning nor use “magic values”; 
- The declarations of the types and functions of the program must be distributed in several source files (.kt) whose 
- responsibility must be described in the initial comment of each of them using just one paragraph.

Optionally, more features can be added to the game, for example:
- Play a sound when the snake eats an apple.
- Support various game levels with different initial blocks and minimum length of the snake.
- Progressively move the snake's head instead of “jumping” from cell to cell.

## Escape
The requirements for the escape game are:

Done during class.

## Arkanoid
The requirements for the arkanoid game are:

The requirements for the snake game are:

This assignment continues from the second project, aimed at creating a simplified version of the "Arkanoid" game.
In this game, there's only one ball at a time that bounces off the paddle, sides, top, and now also off bricks.
Normal bricks are destroyed upon collision, with points awarded based on the brick's color.
The goal is to score as many points as possible by destroying bricks with the available balls.
The game ends when there are no more bricks to destroy or no more balls available.

An illustration shows a possible game state where some bricks have been destroyed, totaling 84 points,
with five spare balls remaining.

The paddle maintains the dimensions of the end zones but is now 60 pixels wide.
Each brick measures 32 pixels in width and 15 in height.
The arena is 13 bricks wide and 600 pixels tall.

Initially, there are five spare balls in addition to the one in play.
The initial ball and each spare ball stay attached to the paddle until a mouse click.

Another illustration shows the initial brick arrangement in the arena,
with a separation at the top corresponding to three bricks in height and four vertical separations between the brick columns.
The points for each brick, depending on its color, are:
WHITE → 1; ORANGE → 2; CYAN → 3; GREEN → 4; RED → 6; BLUE → 7; MAGENTA → 8; YELLOW → 9.
Silver bricks (3 at the base of the central column) disappear after two collisions without awarding points.
Gold bricks (1 at the top of the central column) never disappear.
At the game's end, 10 points are added for each spare ball.

**Assignment Rules:**
- Avoid mutability.
- Do not repeat code.
- Do not create overly long functions.
- Do not repeat values with the same meaning or use "magic numbers."
- Distribute the code across several source files.

The program should have only one point of mutability, a single variable (var) of type Game, whose value changes with each modification.
Except for the main() function, there may be some functions with local variables whose lifespan is only during the function's execution.

A demo of the intended program is available in the file arkanoid.jar.

**Optional Features:**
- Sound effects for significant events, toggleable with the 's' key.
- Multiple game levels, advancing to the next when all bricks are destroyed. The game ends when there are no more levels.
- Random bricks may release prizes with special effects when destroyed, activated in the demo with the 'G' key (Gifts). Possible prizes include: "E" → (Extended) Paddle width becomes 90 pixels; "B" → (Balls) Doubles the balls in play; "S" → (Slow) Reduces ball speed; "F" → (Fast) Increases ball speed; "G" → (Glue) The next 3 balls stick to the paddle; "C" → (Cancel) Cancels all current effects.


## Tetris
The requirements for the tetris game are:

Done during class.

## How to play
To play the games, its only needed to have the Java 16 installed and run the main function in each folder.