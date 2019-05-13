import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import org.junit.Before
import org.junit.Test

class ShotsManagerTest {

    private var chatsRepository: ChatsRepository = mockk()
    private var shotsRepository: ShotsRepository = mockk()
    private var shotsProvider: ShotsProvider = mockk()
    private var sender: Sender = mockk()
    private var shotsManager: ShotsManager? = null

    @Before
    fun before() {
        chatsRepository = mockk()
        shotsRepository = mockk()
        shotsProvider = mockk()
        sender = mockk()

        every { shotsRepository.putNewShots(any()) } returns Unit
        every { sender.sendShots(any(), any()) } returns Unit

        shotsManager = ShotsManager(chatsRepository, shotsRepository, shotsProvider, sender)
    }

    @Test
    fun `updateShots() - zero shot, one chat, one tag - zero shot will be send`() {
        val chat = Chat(1L, setOf("some"))

        every { chatsRepository.getChats() } returns (setOf(chat))
        every { shotsRepository.getTodayShots() } returns (setOf())
        every { shotsProvider.getPostsByTag(any(), any()) } returns listOf()

        shotsManager?.updateShots()

        verifyAll { sender.sendShots(chat.id, setOf()) }
    }

    @Test
    fun `updateShots() - one shot, one chat, one tag - one shot will be send`() {
        val chat = Chat(1L, setOf("some"))
        val shot = Shot("1", "title", "link", "url", Shot.Type.IMAGE, "data")

        every { chatsRepository.getChats() } returns (setOf(chat))
        every { shotsRepository.getTodayShots() } returns (setOf())
        every { shotsProvider.getPostsByTag(any(), any()) } returns listOf(shot.copy())

        shotsManager?.updateShots()

        verifyAll { sender.sendShots(chat.id, setOf(shot)) }
    }

    @Test
    fun `updateShots() - two similar shot, one chat, two tags - one shot will be send`() {
        val chat = Chat(1L, setOf("some", "some2"))
        val shot = Shot("1", "title", "link", "url", Shot.Type.IMAGE, "data")

        every { chatsRepository.getChats() } returns (setOf(chat))
        every { shotsRepository.getTodayShots() } returns (setOf())
        every { shotsProvider.getPostsByTag(any(), any()) } returns listOf(shot.copy())

        shotsManager?.updateShots()

        verify(exactly = 2) { shotsProvider.getPostsByTag(any(), any()) }
        verify { sender.sendShots(chat.id, setOf(shot)) }
    }

    @Test
    fun `updateShots() - two shot, one chat, two tags - two shot will be send`() {
        val chat = Chat(1L, setOf("some", "some2"))
        val shot = Shot("1", "title", "link", "url", Shot.Type.IMAGE, "data")

        every { chatsRepository.getChats() } returns (setOf(chat))
        every { shotsRepository.getTodayShots() } returns (setOf())
        every { shotsProvider.getPostsByTag(any(), any()) } returns listOf(shot.copy())

        shotsManager?.updateShots()

        verify(exactly = 2) { shotsProvider.getPostsByTag(any(), any()) }
        verify { sender.sendShots(chat.id, setOf(shot)) }
    }

    @Test
    fun `updateShots() - three chats with different tags - different and same shots will be send`() {
        val chats = arrayOf(Chat(1L, setOf("1", "2")), Chat(2L, setOf("2", "3")), Chat(3L, setOf("4")))

        val shots = Array(6) {
            Shot(it.toString(), "title", "link", "url", Shot.Type.IMAGE, "data")
        }

        every { chatsRepository.getChats() } returns (chats.toSet())
        every { shotsRepository.getTodayShots() } returns (setOf())


        every { shotsProvider.getPostsByTag("1", any()) } returns listOf(shots[1], shots[2], shots[3])
        every { shotsProvider.getPostsByTag("2", any()) } returns listOf(shots[4], shots[5], shots[2])
        every { shotsProvider.getPostsByTag("3", any()) } returns listOf(shots[5])
        every { shotsProvider.getPostsByTag("4", any()) } returns listOf(shots[0])

        shotsManager?.updateShots()

        verify { sender.sendShots(chats[0].id, setOf(shots[1], shots[2], shots[3], shots[4], shots[5])) }
        verify { sender.sendShots(chats[1].id, setOf(shots[4], shots[2], shots[5])) }
        verify { sender.sendShots(chats[2].id, setOf(shots[0])) }
    }


}