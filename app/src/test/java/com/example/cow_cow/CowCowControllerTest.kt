package com.example.cow_cow

class CowCowControllerTest {
}import com.example.cow_cow.controllers.CowCowController
import com.example.cow_cow.models.Player
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CowCowControllerTest {

    private lateinit var cowCowController: CowCowController
    private lateinit var player: Player

    @Before
    fun setup() {
        cowCowController = CowCowController()
        player = Player(id = 1, name = "Test Player")
    }

    @Test
    fun testApplyPointsForCow() {
        val points = cowCowController.applyPoints(player, "Cow")
        assertEquals(1, points)
        assertEquals(1, player.basePoints)
    }

    @Test
    fun testApplyPointsForChurch() {
        val points = cowCowController.applyPoints(player, "Church")
        assertEquals(2, points)
        assertEquals(2, player.basePoints)
    }

    @Test
    fun testValidateCall() {
        val isValid = cowCowController.validateCall(player, "Cow")
        assertEquals(true, isValid)
    }

    @Test
    fun testResetGame() {
        player.basePoints = 5
        player.cowCount = 2
        player.churchCount = 1
        val players = listOf(player)

        cowCowController.resetGame(players)

        assertEquals(0, player.basePoints)
        assertEquals(0, player.cowCount)
        assertEquals(0, player.churchCount)
    }
}
