import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.concurrent.TimeUnit


const val NAME = "DribbbleBot"

const val CHAT_FILE = "chat.txt"
const val SHOTS_FILE = "shots.txt"

const val HELLO_MESSAGE = """
Hi!

I send to you new dribbble shots, all that you need to do is write the tags that interest you.

Example:
/tags app web log
"""

fun main() {
    val chatsRepository: ChatsRepository = FileChatsRepository(CHAT_FILE)
    val shotsRepository: ShotsRepository = FileShotsRepository(SHOTS_FILE)
    val shotsProvider: ShotsProvider = DribbbleShotsProvider()

    val bot = messageBot(name = NAME, token = API_KEY) { bot ->
        when {
            text?.startsWith("/tags") == true -> bot.handleTagsCommand(text, chatId, chatsRepository)
            else -> bot.sendMessage(chatId, HELLO_MESSAGE)
        }
    }

    val sender = TelegramSender(bot)
    val shotsManager = ShotsManager(chatsRepository, shotsRepository, shotsProvider, sender)

    timer(TimeUnit.MINUTES.toMillis(5)) { shotsManager.updateShots() }
}

fun AbsSender.handleTagsCommand(text: String, chatId: Long, chatsRepository: ChatsRepository) {
    val newTags = text.split(' ').drop(1).toSet()
    chatsRepository.updateChat(Chat(chatId, newTags))
    if (newTags.isNotEmpty()) {
        sendMessage(
            chatId,
            newTags.joinToString(prefix = "You subscribe to: ", postfix = ".", transform = { it })
        )
    } else {
        sendMessage(chatId, "You subscribe to: nothing")
    }
}


