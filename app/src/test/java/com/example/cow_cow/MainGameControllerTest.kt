import com.example.cow_cow.controllers.MainGameController
import com.example.cow_cow.models.Player
import com.example.cow_cow.viewModels.GameViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class MainGameControllerTest {

    private lateinit var mainGameController: MainGameController
    private lateinit var gameViewModel: GameViewModel
    private lateinit var player: Player

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        gameViewModel = mock(GameViewModel::class.java)
        mainGameController = MainGameController(gameViewModel)
        player = Player(id = 1, name = "Test Player")
    }

    @Test
    fun testHandleCowCalled() {
        val points = mainGameController.handleCowCalled(player)
        assert(points == 1)
        verify(gameViewModel).applyPointsForAction(player, "Cow")
    }

    @Test
    fun testHandleChurchCalled() {
        val points = mainGameController.handleChurchCalled(player)
        assert(points == 2)
        verify(gameViewModel).applyPointsForAction(player, "Church")
    }

    @Test
    fun testHandleWaterTowerCalled() {
        val points = mainGameController.handleWaterTowerCalled(player)
        assert(points == 3)
        verify(gameViewModel).applyPointsForAction(player, "Water Tower")
    }

    @Test
    fun testResetMainGame() {
        mainGameController.resetMainGame()
        verify(gameViewModel).resetCalledObjects()
    }
}
