import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

private const val TEST_FILE = "test-chats.txt"

class FileChatsRepositoryTest {

    @Before
    fun before() {
        File(TEST_FILE).apply { if (exists()) delete() }
    }

    @Test
    fun `if file does not exist`() {
        val repository = FileChatsRepository(TEST_FILE)

        assertTrue(repository.getChats().isEmpty())
    }

    @Test
    fun `if file is empty`() {
        File(TEST_FILE).printWriter().use { out -> out.print("") }
        val repository = FileChatsRepository(TEST_FILE)

        assertTrue(repository.getChats().isEmpty())
    }

    @Test
    fun `if in file invalid data`() {
        File(TEST_FILE).printWriter().use { out -> out.print("some invalid data") }
        val repository = FileChatsRepository(TEST_FILE)

        assertTrue(repository.getChats().isEmpty())
    }

    @Test
    fun `update one shot in empty repository`() {
        val repository = FileChatsRepository(TEST_FILE)

        val chat = Chat(123L, setOf("11", "111"))
        repository.updateChat(chat.copy())

        assertTrue(repository.getChats() == setOf(chat.copy()))
    }

    @Test
    fun `update one shot in empty repository and restart repository`() {
        var repository = FileChatsRepository(TEST_FILE)

        val chat = Chat(123L, setOf("11", "111"))
        repository.updateChat(chat.copy())

        repository = FileChatsRepository(TEST_FILE)

        assertTrue(repository.getChats() == setOf(chat.copy()))
    }

    @Test
    fun `update one shot in repository with one shot`() {
        val repository = FileChatsRepository(TEST_FILE)

        val chat = Chat(123L, setOf("11", "111"))
        repository.updateChat(chat.copy())

        assertArrayEquals(repository.getChats().first().tags.toTypedArray(), chat.copy().tags.toTypedArray())

        val chat2 = Chat(123L, setOf("22", "222"))
        repository.updateChat(chat2.copy())

        assertArrayEquals(repository.getChats().first().tags.toTypedArray(), chat2.copy().tags.toTypedArray())
    }

    @Test
    fun `update one shot in repository with one shot and restart repository`() {
        var repository = FileChatsRepository(TEST_FILE)

        val chat = Chat(123L, setOf("11", "111"))
        repository.updateChat(chat.copy())

        repository = FileChatsRepository(TEST_FILE)
        assertArrayEquals(repository.getChats().first().tags.toTypedArray(), chat.copy().tags.toTypedArray())

        val chat2 = Chat(123L, setOf("22", "222"))
        repository.updateChat(chat2.copy())
        repository = FileChatsRepository(TEST_FILE)
        assertArrayEquals(repository.getChats().first().tags.toTypedArray(), chat2.copy().tags.toTypedArray())
    }

    @Test
    fun `update several shot in repository and restart repository`() {
        var repository = FileChatsRepository(TEST_FILE)

        val chat1 = Chat(1L, setOf("11", "111"))
        val chat2 = Chat(2L, setOf("22", "222"))
        val chat3 = Chat(3L, setOf("33", "333"))
        val chat4 = Chat(4L, setOf("44", "444"))
        repository.updateChat(chat1.copy())
        repository.updateChat(chat2.copy())
        repository.updateChat(chat3.copy())
        repository.updateChat(chat4.copy())

        repository = FileChatsRepository(TEST_FILE)
        assertArrayEquals(repository.getChats().find { it.id == chat1.id }?.tags?.toTypedArray(), chat1.copy().tags.toTypedArray())
        assertArrayEquals(repository.getChats().find { it.id == chat2.id }?.tags?.toTypedArray(), chat2.copy().tags.toTypedArray())
        assertArrayEquals(repository.getChats().find { it.id == chat3.id }?.tags?.toTypedArray(), chat3.copy().tags.toTypedArray())
        assertArrayEquals(repository.getChats().find { it.id == chat4.id }?.tags?.toTypedArray(), chat4.copy().tags.toTypedArray())

        val chat22 = Chat(2L, setOf("222", "2222"))
        val chat33 = Chat(3L, setOf("333", "3333"))
        val chat55 = Chat(5L, setOf("555", "5555"))

        repository.updateChat(chat22.copy())
        repository.updateChat(chat33.copy())
        repository.updateChat(chat55.copy())

        repository = FileChatsRepository(TEST_FILE)
        assertArrayEquals(repository.getChats().find { it.id == chat1.id }?.tags?.toTypedArray(), chat1.copy().tags.toTypedArray())
        assertArrayEquals(repository.getChats().find { it.id == chat2.id }?.tags?.toTypedArray(), chat22.copy().tags.toTypedArray())
        assertArrayEquals(repository.getChats().find { it.id == chat3.id }?.tags?.toTypedArray(), chat33.copy().tags.toTypedArray())
        assertArrayEquals(repository.getChats().find { it.id == chat4.id }?.tags?.toTypedArray(), chat4.copy().tags.toTypedArray())
        assertArrayEquals(repository.getChats().find { it.id == chat55.id }?.tags?.toTypedArray(), chat55.copy().tags.toTypedArray())
    }

}