# Requests / Responses

## Requests
All requests are sent to the server as json. All replies are in JSON.

Request must contain a `username` field for who is making the request and a `request-type` field.

---
## Responses
Responses are sent in JSON. Every request has a response.

Responses contain a `status` field. If the request was successfull `status` is set to `ok`. 
An unsuccessful request has a status of `error`, and has an error message in the `error` field.
---
## Types of Requests


### Scoreboard

`request-type: scoreboard`

Get the scoreboard.

**Required Fields:**
- `username`: Username of the person making the request.

**Response**
- `status` -`ok` on success, `error` on error.
- `scores` - Json array of user scores.
  - Each user score has a `player` field and a `score` field.

---


### New Game

`request-type: newgame`

Creates an instance of a new game.

**Required Fields:** 
- `opponent`: Username of the opponent to challenge
- `username`: Username of user making the request.

**Response**
- `status` -`ok` on success, `error` on error.
- `id` - UUID of the new game.

---
### Get Games

`request-type: getgames`

Get all the current player's active games.

**Required Fields:**
- `username`: Username of user making the request.

**Response**
- `status` -`ok` on success, `error` on error.
- `games` - Json array of all the games the requesting user is playing / has played.
  - Game objects contain `player1`, `player2`, `isPlayer1Turn`, `board`. 
  Where board is a json object with keys 0-8, and values of `X`, `O`, or `NONE`.

---
### Move

`request-type: move`

Play a move.

**Required Fields:**
- `username`: Username of the person making the request.
- `gameid`: ID of the game to play on
- `position`: Integer of position. Cells are assigned positions by starting at the top left and moving horizontally.
Top left is `0`, top right is `2`, bottom left is `6`.

**Response**
- `status` -`ok` on success, `error` on error.
