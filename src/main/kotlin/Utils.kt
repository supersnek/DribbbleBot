import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

fun messageBot(name: String, token: String, onMessage: Message.(bot: TelegramLongPollingBot) -> Unit): AbsSender {
    ApiContextInitializer.init()
    val bot = object : TelegramLongPollingBot() {
        override fun getBotUsername() = name
        override fun getBotToken() = token
        override fun onUpdateReceived(update: Update) {
            update.message?.onMessage(this)
        }
    }
    TelegramBotsApi().registerBot(bot)
    return bot
}

fun AbsSender.sendMessage(chatId: Long, text: String) {
    try {
        execute(SendMessage().apply {
            setChatId(chatId)
            setText(text)
        })
    } catch (e: Exception) {
        e.printStackTrace()
        //TODO add some handling
    }

}

fun AbsSender.safeExecute(sendDocument: SendDocument) {
    try {
        execute(sendDocument)
    } catch (e: Exception) {
        e.printStackTrace()
        //TODO add some handling
    }
}

fun AbsSender.safeExecute(sendPhoto: SendPhoto) {
    try {
        execute(sendPhoto)
    } catch (e: Exception) {
        e.printStackTrace()
        //TODO add some handling
    }
}

inline fun timer(period: Long, delay: Long = 0, crossinline run: () -> Unit) {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            run()
        }
    }, delay, period)
}

fun getDateString(date: Date = Date()): String = SimpleDateFormat("yyyy-MM-dd").format(date)

private val moshi = Moshi.Builder().build()

fun getGetMoshi(): Moshi = moshi

inline fun <reified T> String.fromJson() = try {
    getGetMoshi().adapter<T>(T::class.java).fromJson(this)
} catch (e: JsonEncodingException) {
    null
}

inline fun <reified T> toJson(value: T): String = getGetMoshi().adapter<T>(T::class.java).toJson(value)