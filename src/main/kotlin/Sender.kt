import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender

interface Sender {

    fun sendShots(chatId: Long, shots: Set<Shot>)

}

class TelegramSender(private val absSender: AbsSender) : Sender {

    override fun sendShots(chatId: Long, shots: Set<Shot>) {
        shots.forEach { absSender.sendShot(it, chatId) }
    }

    private fun AbsSender.sendShot(shot: Shot, chatId: Long) {
        if (shot.attachmentUrl.isEmpty()) return

        if (shot.type == Shot.Type.IMAGE) {
            safeExecute(SendPhoto().apply {
                setChatId(chatId)
                caption = shot.title
                setPhoto(shot.attachmentUrl)
                replyMarkup = getKeyboard(shot)
            })
        } else {
            safeExecute(SendDocument().apply {
                setChatId(chatId)
                caption = shot.title
                setDocument(shot.attachmentUrl)
                replyMarkup = getKeyboard(shot)
            })
        }
    }

    private fun getKeyboard(shot: Shot): InlineKeyboardMarkup = InlineKeyboardMarkup()
        .setKeyboard(
            listOf(
                listOf(
                    InlineKeyboardButton()
                        .setText(shot.type.name)
                        .setUrl(shot.attachmentUrl),
                    InlineKeyboardButton()
                        .setText("SHOT")
                        .setUrl(shot.link)
                )
            )
        )

}

