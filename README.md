# Real-Time Leaderboard

This project is a simple Android app that shows a live leaderboard for a game.

It has 2 main parts:

1. Score Generator
2. Leaderboard Updater

The score generator keeps creating score updates.
The leaderboard updater listens to those updates and keeps the ranking correct.

---

## What we used

### Kotlin
Used because Android works very well with Kotlin and it is clean for writing app logic.

### Coroutines
Used for background work without blocking the UI.

### Flow / StateFlow
Used for live updates.
Whenever score changes, new data is pushed automatically to the UI.

### Hilt
Used for dependency injection.
It helps create classes like `ScoreGenerator` and `LeaderboardUseCase` in one place instead of creating them inside `ViewModel`.

### Jetpack Compose
Used for UI.
It is easier for showing live state updates because UI automatically reacts when data changes.

### MVVM style
Used to keep code separated:
- UI only shows data
- ViewModel prepares data for UI
- UseCase handles business logic
- Engine creates score events

---

## Why we used this structure

We wanted to keep each job in a separate place.

- Score generation should not be inside UI
- Ranking logic should not be inside UI
- ViewModel should not calculate ranks
- UI should only display current leaderboard

This makes code easier to read, test, and change later.

---

## Project flow

### Full flow

1. `ScoreGenerator` creates random score updates again and again
2. Each update is sent as a `ScoreEvent`
3. `LeaderboardUseCase` listens to those events
4. It updates the player score
5. It sorts players by score
6. It calculates ranks
7. `LeaderboardViewModel` exposes this data as `StateFlow`
8. `LeaderboardScreen` collects this state and shows it on screen

---

## What each class does

### `ScoreGenerator`
This class simulates the game backend.

What it does:
- stores player list
- stores current scores
- creates random score updates
- emits updates using `Flow`

Important points:
- score only increases
- update comes after random delay
- update is for a random player
- same seed can give same session behavior

It does not know anything about UI.

---

### `ScoreEvent`
This is a small data object.

It contains:
- player id
- new score

This is the message sent from score generator to leaderboard logic.

---

### `LeaderboardUseCase`
This class handles leaderboard business logic.

What it does:
- takes initial player scores
- listens to score events
- updates score map
- sorts players by score in descending order
- calculates rank
- calculates rank movement

Rules used here:
- higher score gets higher position
- same score means same rank
- if ranks are tied, next rank is skipped properly

Example:
- scores: 300, 300, 250
- ranks: 1, 1, 3

This logic is kept here because ranking is business logic, not UI logic.

---

### `LeaderboardItem`
This is the model used by UI.

It contains:
- player id
- player name
- score
- rank
- rank change

UI reads this object and displays the row.

---

### `LeaderboardViewModel`
This is the bridge between logic and UI.

What it does:
- gets `ScoreGenerator`
- gets `LeaderboardUseCase`
- starts listening to score updates
- exposes leaderboard data as `StateFlow`
- exposes last updated player id for row animation

It does not calculate rank by itself.
It only connects the flow of data.

---

### `LeaderboardScreen`
This is the main screen.

What it does:
- collects leaderboard state from ViewModel
- shows the list of players
- shows collapsing header
- passes row update info to each row

The screen only shows data.
It does not generate score updates.

---

### `LeaderboardRow`
This is one row in the leaderboard list.

What it shows:
- rank
- user image
- user name
- score
- rank direction icon

It also has a small highlight animation when that row gets updated.

---

## How score is updated

### Step by step

1. App starts
2. `ScoreGenerator` already has a player list and initial scores
3. `start()` begins a flow
4. It waits for a random delay
5. It selects a random player
6. It adds a random score increment
7. It emits a new `ScoreEvent`
8. `LeaderboardUseCase` receives that event
9. It updates that player’s score
10. It rebuilds the leaderboard
11. UI gets the new list
12. Screen updates automatically

---

## How ranking logic works

### Sorting
Players are sorted by score from high to low.

### Tie handling
If 2 players have same score:
- both get same rank

### Rank skip
If 2 players are rank 1, next player becomes rank 3, not rank 2.

### Rank change
We also compare current rank with previous rank.

Formula used:
- `rankChange = oldRank - newRank`

Meaning:
- positive value = moved up
- negative value = moved down
- zero = no change

---

## Why we used Flow for live updates

We used `Flow` because scores keep changing over time.

It helps because:
- updates come one by one
- UI can listen continuously
- no manual refresh needed
- easy to connect backend-like events to screen

This is better than using timers inside UI.

---

## Why we used Compose over XML

We used Compose because this screen has live data and changing UI.

Compose is helpful here because:
- UI reacts automatically when state changes
- less boilerplate than XML
- easier to build animated rows
- easier to connect `StateFlow` to screen

XML is still valid, but for this type of state-driven UI, Compose is simpler.

---

## What logic is used for real-time feeling

Current logic:
- score updates come continuously
- UI listens to state changes
- changed row gets a short highlight
- list updates immediately after each event

This gives a real-time feeling even though updates are locally simulated.

---

## What changes will be needed to handle real real-time events

Right now updates are generated inside the app.
For actual real-time production events, these changes will be needed:

### 1. Replace local generator
Instead of local `ScoreGenerator`, app should receive events from:
- WebSocket
- Server-Sent Events
- Firebase
- polling API if needed

### 2. Add repository layer
A repository can handle:
- network calls
- retry
- reconnect
- caching
- local database

### 3. Handle connection states
We should show:
- connecting
- connected
- disconnected
- retrying

### 4. Add error handling
Need to handle:
- no internet
- delayed events
- duplicate events
- out of order events

### 5. Add lifecycle handling
When app goes background:
- pause expensive work if needed
- reconnect safely when app comes back

### 6. Add database or cache
For real app, leaderboard should survive:
- app restart
- configuration change
- temporary network loss

### 7. Add event ordering protection
Real server events may arrive late.
We may need:
- event timestamp
- sequence number
- server version check

### 8. Add scaling strategy
For many users:
- do partial updates
- avoid rebuilding full list every time
- maybe update only changed rows
- maybe page large leaderboard

---

## Current good points

- clear separation between generator, logic, ViewModel, and UI
- ranking logic is outside UI
- UI listens reactively
- score updates happen continuously
- row update animation is added
- ViewModel is not creating business logic classes directly

---

## Current limitations

- score updates are local only, not server driven
- no real backend connection
- no unit tests yet
- header still needs to be fully data-driven if we want it perfect
- full leaderboard is rebuilt on every event
- no offline cache yet
- no retry/reconnect logic yet

---

## Scope of improvement for production readiness

### 1. Better UI state model
Create one `UiState` instead of sending separate flows.

### 2. Better scaling
For large player count:
- smarter sorting
- smarter row updates
- paging for long leaderboard


### 3. Better update animation
Improve row animation with:
- score color animation
- movement animation
- rank change animation

---

## Simple summary

This app works like this:

- one class creates score updates
- one class updates leaderboard and ranks
- ViewModel sends data to UI
- Compose screen shows live leaderboard
- updated row gets a small animation

Main idea:
keep score generation, ranking logic, and UI separate so the app is easier to understand and improve.






### Planning & Ownership (7-Day Delivery)
## NON-NEGOTIABLE
If this must ship in 7 days, I would ensure:
1. Core functionality (must work perfectly)
   Real-time leaderboard updates using Flow 
   Correct ranking logic (including ties → 1,1,3)
   Stable data flow (no crashes, no inconsistencies)


2. Clean architecture
   Proper separation: Data → Domain → UI
   Business logic inside domain (not UI/ViewModel)


3. Smooth user experience
   No UI flickering or lag during updates
   Basic collapsing header behavior working


4. Reliability
   No memory leaks
   Handles lifecycle (rotation, background)




## NEGOTIABLES
To meet deadline, I would consciously defer:

1. Advanced UI polish
2. Heavy optimizations
3. Large-scale (100K users) simulation
3. Non-critical features
4. Pagination / filtering
These can be added later without affecting core functionality.




### Work Distribution
## Junior Developer---
Focus: UI + simple tasks
Implement leaderboard list UI (Compose)
Create row layout (rank, name, score)
Add icons and basic styling




## Mid-Level Developer--
Focus: feature implementation
Integrate Flow with ViewModel
Implement Score Generator
Build basic leaderboard logic
Connect UI with data layer


## Myself (Lead)--
Focus: architecture + critical correctness
Design system architecture (modular + scalable)
Implement/validate ranking logic (edge cases)
Ensure performance decisions (Flow, threading)
Review code and enforce best practices



## Bonus
App with support the system default theme (Light/Dark.)



