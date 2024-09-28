import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.cow_cow.controllers.ScavengerHuntController
import com.example.cow_cow.models.ScavengerHuntItem
import com.example.cow_cow.repositories.ScavengerHuntRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class ScavengerHuntControllerTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()  // To handle LiveData

    private lateinit var scavengerHuntController: ScavengerHuntController
    private lateinit var repository: ScavengerHuntRepository
    private lateinit var context: Context

    private val testScope = TestCoroutineScope()

    @Before
    fun setup() {
        Dispatchers.setMain(testScope.coroutineContext)  // Set the main dispatcher to the test coroutine scope
        repository = mock(ScavengerHuntRepository::class.java)
        scavengerHuntController = ScavengerHuntController(repository, testScope)
        context = mock(Context::class.java)
    }

    @Test
    fun testLoadScavengerHuntItems() {
        val items = listOf(ScavengerHuntItem("Test Item", DifficultyLevel.EASY))

        // Mock repository return value
        `when`(repository.getScavengerHuntItems(context)).thenReturn(items)

        // Observe LiveData
        val observer = mock(Observer::class.java) as Observer<List<ScavengerHuntItem>>
        scavengerHuntController.scavengerHuntItems.observeForever(observer)

        scavengerHuntController.loadScavengerHuntItems(context)

        // Verify that LiveData was updated with the correct value
        verify(observer).onChanged(items)
    }

    @Test
    fun testAddScavengerHuntItem() {
        val newItem = ScavengerHuntItem("New Item", DifficultyLevel.MEDIUM)

        // Add the item
        scavengerHuntController.addScavengerHuntItem(newItem, context)

        // Verify that repository's add method was called
        verify(repository).addScavengerHuntItem(newItem, context)
    }

    @Test
    fun testRemoveScavengerHuntItem() {
        val item = ScavengerHuntItem("Old Item", DifficultyLevel.HARD)

        // Remove the item
        scavengerHuntController.removeScavengerHuntItem(item, context)

        // Verify that repository's remove method was called
        verify(repository).removeScavengerHuntItem(item, context)
    }

    @Test
    fun testClearScavengerHuntItems() {
        scavengerHuntController.clearScavengerHuntItems(context)

        // Verify that repository's save method was called with an empty list
        verify(repository).saveScavengerHuntItems(emptyList(), context)
    }
}
