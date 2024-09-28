import android.content.Context
import android.media.MediaPlayer
import com.example.cow_cow.controllers.SoundController
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class SoundControllerTest {

    private lateinit var soundController: SoundController
    private lateinit var context: Context
    private lateinit var mediaPlayer: MediaPlayer

    @Before
    fun setup() {
        context = mock(Context::class.java)
        mediaPlayer = mock(MediaPlayer::class.java)
        soundController = SoundController(context)
    }

    @Test
    fun testPlaySound() {
        val soundResId = 1234 // Replace with a valid sound resource ID
        soundController.playSound(soundResId)

        // Verify that the media player was created and started
        verify(mediaPlayer).start()
    }

    @Test
    fun testStopAndRelease() {
        soundController.playSound(1234)
        soundController.stopAndRelease()

        // Verify that stop and release were called on the MediaPlayer
        verify(mediaPlayer).stop()
        verify(mediaPlayer).release()
    }

    @Test
    fun testCleanup() {
        soundController.playSound(1234)
        soundController.cleanup()

        // Verify cleanup calls stopAndRelease
        verify(mediaPlayer).stop()
        verify(mediaPlayer).release()
    }
}
