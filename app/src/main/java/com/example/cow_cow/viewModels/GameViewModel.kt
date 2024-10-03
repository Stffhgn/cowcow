package com.example.cow_cow.viewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cow_cow.enums.PenaltyType
import com.example.cow_cow.enums.PowerUpType
import com.example.cow_cow.managers.*
import com.example.cow_cow.models.*
import com.example.cow_cow.repositories.GameRepository
import com.example.cow_cow.repositories.PlayerRepository
import com.example.cow_cow.repositories.ScavengerHuntRepository
import com.example.cow_cow.utils.TeamUtils

class GameViewModel(application: Application, private val repository: GameRepository) : AndroidViewModel(application) {

    private val TAG = "GameViewModel"

    // ---- LiveData for players and team ----
    private val _players = MutableLiveData<List<Player>>()
    val players: LiveData<List<Player>> get() = _players

    private val _team = MutableLiveData<Team>()
    val team: LiveData<Team> get() = _team

    // ---- Managers ----
    private val scavengerHuntManager = ScavengerHuntManager
    private val powerUpManager = PowerUpManager
    private val penaltyManager = PenaltyManager
    private val playerManager = PlayerManager
    private val teamManager = TeamManager

    // ---- Game settings ----
    var quietGameEnabled: Boolean = false
    var falseCallPenaltyEnabled: Boolean = true
    var noRepeatRuleEnabled: Boolean = true

    // LiveData to hold news messages for the rotating TextView
    private val _gameNews = MutableLiveData<String>()
    val gameNews: LiveData<String> get() = _gameNews

    // Instance of the GameNewsManager
    private val gameNewsManager = GameNewsManager()

    // ---- Initialization ----
    init {
        Log.d(TAG, "ViewModel initialized. Loading players and team data.")
        loadPlayers()
        loadTeam()
    }

    // ---- Player and Team Management ----

    fun loadPlayers() {
        Log.d(TAG, "Loading players from repository.")
        val context = getApplication<Application>().applicationContext
        val playerList = PlayerRepository().getPlayers(context)
        _players.value = playerList
        Log.d(TAG, "Players loaded: ${playerList.size} players.")
    }

    fun loadTeam() {
        Log.d(TAG, "Loading team from repository.")
        val context = getApplication<Application>().applicationContext
        val players = PlayerRepository().getTeam(context)  // This returns List<Player>

        // Create a Team object from the list of players
        val team = Team(id = 1, name = "Default Team", members = players.toMutableList())  // Pass the list as 'members'

        _team.value = team  // Now _team.value is set to a Team object
        Log.d(TAG, "Team loaded: ${team.members.size} players.")
    }


    fun addPlayerToTeam(player: Player) {
        Log.d(TAG, "Adding player ${player.name} to the team.")

        // Set the player's team status to true
        player.isOnTeam = true

        // Get the current team or create a new one if none exists
        val currentTeam = _team.value ?: Team(id = 1, name = "Team", members = mutableListOf())

        // Use TeamUtils to add the player to the team and update the team score
        TeamUtils.addPlayerToTeam(player, currentTeam)

        // Update LiveData with the updated team
        _team.value = currentTeam

        // Save the updated team state to persist the changes
        saveTeam()

        // Ensure the player's status is updated in the system
        updatePlayer(player)

        // Log the successful addition
        Log.d(TAG, "Player ${player.name} has been added to the team.")
    }

    fun removePlayerFromTeam(player: Player) {
        Log.d(TAG, "Removing player ${player.name} from the team.")

        // Set the player's team status to false
        player.isOnTeam = false

        // Retrieve the current team
        val currentTeam = _team.value

        // If the team exists, remove the player and update the team
        currentTeam?.let {
            // Use TeamUtils to remove the player from the team and update the team score
            TeamUtils.removePlayerFromTeam(player, it)

            // Update LiveData with the updated team
            _team.value = it

            // Save the updated team state to persist the changes
            saveTeam()

            // Ensure the player's status is updated in the system
            updatePlayer(player)

            // Log the successful removal
            Log.d(TAG, "Player ${player.name} has been removed from the team.")
        } ?: Log.e(TAG, "No team found to remove player from.")  // If no team is found, log an error
    }
    fun savePlayers() {
        Log.d(TAG, "Saving players to repository.")
        val context = getApplication<Application>().applicationContext
        _players.value?.let { playerList ->
            PlayerRepository().savePlayers(playerList, context)
        }
    }

    fun saveTeam() {
        Log.d(TAG, "Saving team to repository.")
        val context = getApplication<Application>().applicationContext
        _team.value?.let { team ->
            PlayerRepository().saveTeam(team.members, context)
        }
    }

    /**
     * Calculate the total score of the team.
     * Logs the calculated score and returns it.
     *
     * @return The total score of the team.
     */
    fun calculateTeamScore(): Int {
        val score = _team.value?.teamScore ?: 0
        Log.d(TAG, "Calculated team score: $score")
        return score
    }

    fun distributePointsToTeam(totalPoints: Int) {
        Log.d(TAG, "Distributing $totalPoints points to the team.")
        val currentTeam = _team.value
        currentTeam?.let {
            TeamUtils.distributeTeamPoints(it, totalPoints)
            _team.value = it
            saveTeam()
        } ?: Log.e(TAG, "No team found to distribute points to.")
    }

    // ---- Game Logic ----

    fun applyPointsForAction(player: Player, action: String) {
        Log.d(TAG, "Applying points for action: $action for player ${player.name}.")
        when (action) {
            "Cow" -> player.cowCount += 1
            "Church" -> player.churchCount += 1
            "Water Tower" -> player.waterTowerCount += 1
        }
        playerManager.calculatePlayerPoints(player)
        updatePlayer(player)
    }

    fun updatePlayer(player: Player) {
        Log.d(TAG, "Updating player: ${player.name}")
        _players.value = _players.value?.map {
            if (it.id == player.id) player else it
        }
        savePlayers()
    }

    fun resetCalledObjects() {
        Log.d(TAG, "Resetting called objects for all players.")
        _players.value = _players.value?.map { player ->
            player.copy(cowCount = 0, churchCount = 0, waterTowerCount = 0)
        }
        savePlayers()  // Save updated players to the repository
    }

    // ---- Scavenger Hunt ----

    fun initializeScavengerHunt(player: Player, scavengerHuntRepository: ScavengerHuntRepository, context: Context, difficulty: String? = null) {
        Log.d(TAG, "Initializing scavenger hunt for player: ${player.name} with difficulty: ${difficulty ?: "Default"}")

        // Start the scavenger hunt, passing in the player, repository, context, and optional difficulty
        scavengerHuntManager.startScavengerHunt(player, scavengerHuntRepository, context, difficulty)

        // Add any additional logic if needed after starting the scavenger hunt
    }

    fun markScavengerItemAsFound(itemName: String, player: Player) {
        Log.d(TAG, "Marking scavenger hunt item '$itemName' as found for player ${player.name}.")

        // Assuming scavengerHuntItems is the list of available scavenger hunt items
        val item = scavengerHuntManager.getActiveScavengerHuntItems().find { it.name == itemName }

        if (item != null) {
            scavengerHuntManager.markItemAsFound(item, player)  // Now passing the ScavengerHuntItem object
            Log.d(TAG, "Item marked as found: ${item.name}")
        } else {
            Log.e(TAG, "Scavenger hunt item '$itemName' not found.")
        }
    }

    fun isScavengerHuntComplete(): Boolean {
        val isComplete = scavengerHuntManager.isHuntCompleted()
        Log.d(TAG, "Scavenger hunt complete: $isComplete")
        return isComplete
    }

    // ---- Power-Up Management ----

    fun activatePowerUp(player: Player, type: PowerUpType, duration: Long) {
        Log.d(TAG, "Activating power-up $type for player ${player.name}.")
        powerUpManager.activatePowerUp(player, type, duration)
        updatePlayer(player)
    }

    fun deactivatePowerUp(player: Player, type: PowerUpType) {
        Log.d(TAG, "Deactivating power-up $type for player ${player.name}.")
        powerUpManager.deactivatePowerUp(player, type)
        updatePlayer(player)
    }

    // ---- Penalty Management ----

    fun applyPenalty(player: Player, penaltyType: PenaltyType, duration: Long) {
        Log.d(TAG, "Applying penalty of type $penaltyType to player ${player.name}.")

// Create a Penalty object using the provided penaltyType and duration
        val penalty = Penalty(
            id = Penalty.generatePenaltyId(player.id, penaltyType),  // Generate a unique ID for the penalty
            name = penaltyType.name,                                 // Use the name from the PenaltyType enum
            pointsDeducted = calculatePenaltyPoints(penaltyType),    // Calculate the points to deduct
            penaltyType = penaltyType,                               // Assign the penalty type
            isActive = true,                                         // Set the penalty as active
            duration = duration,                                     // Duration in milliseconds
            startTime = System.currentTimeMillis(),                  // Set the penalty start time to the current time
            roundsRemaining = null,                                  // Optional: Penalty can be time-based instead of round-based
            stackable = true,                                        // Allow the penalty to be stackable by default
            multiplier = 1.0                                         // Default multiplier, adjust as needed
        )

        // Apply the penalty to the player
        penaltyManager.applyPenalty(player, penalty)

        // Update the player's data after applying the penalty
        updatePlayer(player)

        Log.d(TAG, "Penalty $penaltyType applied successfully to player ${player.name}.")
    }

    fun isPlayerPenalized(player: Player): Boolean {
        val isPenalized = penaltyManager.isPlayerPenalized(player)
        Log.d(TAG, "Player ${player.name} penalized: $isPenalized")
        return isPenalized
    }

    // ---- Game News ----

    fun addGameNewsMessage(message: String) {
        Log.d(TAG, "Adding game news message: $message")
        gameNewsManager.addNewsMessage(message)
        _gameNews.value = message
    }

    fun rotateGameNews() {
        val nextMessage = gameNewsManager.getNextNewsMessage()
        Log.d(TAG, "Rotating game news: $nextMessage")
        _gameNews.value = nextMessage
    }
}
