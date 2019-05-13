import java.io.File

data class Chat(val id: Long, val tags: Set<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

interface ChatsRepository {

    fun getChats(): Set<Chat>

    fun updateChat(chat: Chat)

}

class FileChatsRepository(private val fileName: String) : ChatsRepository {

    private val chats = HashSet<Chat>()

    init {
        val file = File(fileName)
        if (file.exists()) file.forEachLine { line -> line.fromJson<Chat>()?.let { chats.add(it) } }
    }

    override fun getChats() = synchronized(this) { chats.toSet() }

    override fun updateChat(chat: Chat) {
        synchronized(this) {
            chats.remove(chat)
            chats.add(chat)

            File(fileName).printWriter().use { out ->
                chats.forEach { out.println(toJson(it)) }
            }
        }
    }

}