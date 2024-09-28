import com.example.cow_cow.controllers.PlayerController
import com.example.cow_cow.models.Player
import com.example.cow_cow.models.Penalty
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals

class PlayerControllerTest {

    private lateinit var playerController: PlayerController
    private lateinit var player: Player
    private lateinit var players: MutableList<Player>

    @Before
    fun setup() {
        playerController = PlayerController()
        player = Player(id = 1, name = "Test Player")
        players = mutableListOf(player)
    }

    @Test
    fun testAddPlayer() {
        val newPlayer = Player(id = 2, name = "New Player")
        val result = playerController.addPlayer(players, newPlayer)
        assertTrue(result)
        assertTrue(players.contains(newPlayer))
    }

    @Test
    fun testAddDuplicatePlayer() {
        val duplicatePlayer = Player(id = 2, name = "Test Player")
        val result = playerController.addPlayer(players, duplicatePlayer)
        assertFalse(result)
    }

    @Test
    fun testRemovePlayer() {
        val result = playerController.removePlayer(players, player.id)
        assertTrue(result)
        assertFalse(players.contains(player))
    }

    @Test
    fun testUpdatePlayerScore() {
        playerController.updatePlayerScore(player, 10)
        assertEquals(10, player.basePoints)
    }

    @Test
    fun testApplyPenalty() {
        val penalty = Penalty("Test Penalty", 5, true)
        playerController.applyPenalty(player, penalty)
        assertTrue(player.penalties.contains(penalty))
    }

    @Test
    fun testAssignToTeam() {
        playerController.assignToTeam(player, 1)
        assertTrue(player.isOnTeam)
        assertEquals(1, player.teamId)
    }

    @Test
    fun testRemoveFromTeam() {
        playerController.assignToTeam(player, 1)
        playerController.removeFromTeam(player)
        assertFalse(player.isOnTeam)
        assertEquals(null, player.teamId)
    }

    @Test
    fun testResetPlayerStats() {
        player.basePoints = 10
        player.cowCount = 2
        player.churchCount = 1
        playerController.resetPlayerStats(player)

        assertEquals(0, player.basePoints)
        assertEquals(0, player.cowCount)
        assertEquals(0, player.churchCount)
    }
}
