import org.junit.Test
import org.junit.Assert.*

class DribbbleShotsProviderTest {

    private val provider = DribbbleShotsProvider()

    @Test
    fun `fields availability test`() {
        provider.getPostsByTag("app", null, 1)[0].apply {
            assertTrue(id.isNotEmpty())
            assertTrue(title.isNotEmpty())
            assertTrue(link.isNotEmpty())
            assertTrue(attachmentUrl.isNotEmpty())
            assertTrue(date.isNotEmpty())
        }
    }

    @Test
    fun `stop parsing test`() {
        val shotsIds = provider.getPostsByTag("app", null, 4).drop(2).map { it.id }.toSet()
        val shots = provider.getPostsByTag("app", shotsIds)

        assertTrue(shots.size == 2)
        shots.forEach { assertFalse(shotsIds.contains(it.id)) }
    }

}