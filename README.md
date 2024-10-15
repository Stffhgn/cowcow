Cow Cow Game
Cow Cow is an engaging driving game designed to make road trips more interactive and fun. Players earn points by spotting objects like cows, churches, and water towers while driving. The game supports both solo and team play, allowing players to manage teams, track scores, and view their progress.

Features
Simple Gameplay: Players earn points by spotting specific objects during a road trip.

Cows: 1 point
Churches: 2 points
Water Towers: 3 points
White Fence: Team-up feature, allowing players to combine scores.
Solo & Team Play: Play individually or join teams to compete. Points can be tracked for both solo players and teams.

Player Management: Add, remove, and edit up to 10 players. Track individual statistics, manage team assignments, and monitor progress.

Interactive UI: Use dedicated buttons to record object sightings. The interface allows for easy tracking and assignment of points based on player or team.

Player Stats Tracking: Each player’s stats (such as the number of cows, churches, and water towers spotted) are tracked throughout the game.

How to Play
Add Players: Use the player management screen to add up to 10 players.
Start the Game: Spot objects during the trip and press the corresponding button (Cow, Church, or Water Tower).
Assign Points: After pressing a button, select the player who spotted the object to assign points.
Score Points: Points are awarded based on the object spotted:
Cow: 1 point
Church: 2 points
Water Tower: 3 points
White Fence: Use this to toggle team assignments.
Track Stats: View individual stats and team scores during the game.
Team Management: Use the white fence mechanic to create or disband teams. Team points are calculated by combining individual contributions.
Reset the Game: Start fresh by removing players or resetting the session.
Setup Guide
Prerequisites
Android Studio (latest version)
Android device or emulator
Installation
Clone the Repository:

git clone https://github.com/stffhgn/cow_cow.git
cd cow_cow

Open the project in Android Studio:

Sync Gradle to install dependencies.
Run the app on an Android device or emulator.
Project Structure
activity/: Contains main activities, including Game, Player Management, and Team Selection.
models/: Defines the Player model, encapsulating player details and scoring information.
viewmodel/: Handles game state, player data, and score management through GameViewModel.
fragments/: Manages the different game states and user interface screens, such as CowCowFragment, TeamManagementFragment, and PlayerListDialogFragment.
managers/: Includes various game managers (e.g., PlayerManager, TeamManager) responsible for handling game logic and score tracking.
Features & Customizations
Adding Players: Players can be added via the player management screen. They can be renamed, edited, or removed.
Point Assignment: Points are assigned when a player spots an object and is recorded via the game buttons.
Teams and Scoring: Teams can be formed using the White Fence mechanic, and team points are calculated based on individual contributions.
Player Stats: Player statistics are tracked throughout the game for individual and team contributions.
Future Improvements
High Score Tracking: Display a leaderboard for players and teams.
Achievements: Unlock achievements based on the number of objects spotted.
Voice Commands: Implement hands-free gameplay with voice commands.
Multiplayer Mode: Compete with other players in real-time.
Web Integration: Share scores and stats on social media.
Cross-Device Compatibility: Synchronize progress across multiple devices.
Contributions
Want to contribute to Cow Cow? We welcome bug reports, feature suggestions, and pull requests!

Fork the repository.
Create a new branch: git checkout -b feature/my-feature.
Commit your changes: git commit -am 'Add my feature'.
Push the branch: git push origin feature/my-feature.
Create a new pull request.

Contact
For questions, feedback, or suggestions, please contact:

Steffen Hogan
Email: Steffen.Hogan@gmail.com

