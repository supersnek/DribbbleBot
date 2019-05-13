
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*

private const val TEST_FILE = "test-shots.txt"

class FileShotsRepositoryTest {

    @Before
    fun before() {
        File(TEST_FILE).apply { if (exists()) delete() }
        unmockkStatic("UtilsKt")
    }

    @Test
    fun `if file does not exist`() {
        val repository = FileShotsRepository(TEST_FILE)

        assertTrue(repository.getTodayShots().isEmpty())
    }

    @Test
    fun `if file is empty`() {
        File(TEST_FILE).printWriter().use { out -> out.print("") }
        val repository = FileShotsRepository(TEST_FILE)

        assertTrue(repository.getTodayShots().isEmpty())
    }

    @Test
    fun `if in file invalid data`() {
        File(TEST_FILE).printWriter().use { out -> out.print("some invalid data") }
        val repository = FileShotsRepository(TEST_FILE)

        assertTrue(repository.getTodayShots().isEmpty())
    }


    @Test
    fun `add different date shots`() {
        val shots = setOf(
            Shot("1", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot(
                "2",
                "",
                "",
                "",
                Shot.Type.IMAGE,
                getDateString(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }.time)
            )
        )

        FileShotsRepository(TEST_FILE).putNewShots(shots)

        println(FileShotsRepository(TEST_FILE).getTodayShots().size)
        assertTrue(FileShotsRepository(TEST_FILE).getTodayShots().size == 1)
    }

    @Test
    fun `add equal shots`() {
        val shots = setOf(
            Shot("1", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot("2", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot("3", "", "", "", Shot.Type.IMAGE, getDateString())
        )

        FileShotsRepository(TEST_FILE).putNewShots(shots)

        val shots2 = setOf(
            Shot("1", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot("2", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot("4", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot("3", "", "", "", Shot.Type.IMAGE, getDateString())
        )

        FileShotsRepository(TEST_FILE).putNewShots(shots2)

        assertTrue(FileShotsRepository(TEST_FILE).getTodayShots().size == 4)
    }

    @Test
    fun `when date change`() {
        val shots = setOf(
            Shot("1", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot("2", "", "", "", Shot.Type.IMAGE, getDateString()),
            Shot("3", "", "", "", Shot.Type.IMAGE, getDateString())
        )

        FileShotsRepository(TEST_FILE).putNewShots(shots)

        mockkStatic("UtilsKt")

        every {
            getDateString(any())
        } returns "1997-09-24"

        assertTrue(FileShotsRepository(TEST_FILE).getTodayShots().isEmpty())
    }

}