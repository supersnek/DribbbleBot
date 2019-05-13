import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.collections.ArrayList

const val BASE_URL = "https://dribbble.com"

data class Shot(
    val id: String,
    val title: String,
    val link: String,
    val attachmentUrl: String,
    val type: Type,
    val date: String
) {

    enum class Type {
        IMAGE,
        GIF,
        VIDEO
    }

}

interface ShotsProvider {

    fun getPostsByTag(tag: String, todayShotsIds: Set<String>?, count: Int = -1): List<Shot>

}

class DribbbleShotsProvider : ShotsProvider {

    override fun getPostsByTag(tag: String, todayShotsIds: Set<String>?, count: Int): List<Shot> {
        val shots = ArrayList<Shot>()
        val currentDate = getDateString()

        try {
            Jsoup.connect("$BASE_URL/tags/$tag?page=1")
                .followRedirects(true)
                .execute()
                .parse()
                .getElementsByAttribute("data-screenshot-id").forEachIndexed { index, element ->
                    val id = element.attr("data-screenshot-id")
                    val link = "$BASE_URL${element.getElementsByClass("dribbble-link").attr("href")}"
                    val type = getType(element.child(0).className())

                    if ((todayShotsIds != null && todayShotsIds.contains(id)) || count == index) return shots

                    val page = Jsoup.connect(link)
                        .followRedirects(true)
                        .execute()
                        .parse()

                    val date = page.getElementsByClass("shot-date")[0]
                        .getElementsByAttribute("href")
                        .attr("href")
                        .replace("/shots?date=", "")

                    if (currentDate != date) return shots

                    shots += Shot(
                        id = id,
                        title = element.getElementsByClass("shot-title").text(),
                        link = link,
                        attachmentUrl = page.getAttachment(type),
                        type = type,
                        date = date
                    )

                }
        } catch (e: Exception) {
            e.printStackTrace()
            //try on next time
        }

        return shots
    }


    private fun Document.getAttachment(type: Shot.Type) = when (type) {
        Shot.Type.VIDEO -> getElementsByAttributeValue("property", "og:video").attr("content")
        Shot.Type.IMAGE -> getElementsByAttributeValue("property", "og:image").attr("content")
        Shot.Type.GIF -> getElementsByAttributeValue("property", "og:image").attr("content")
    }

    private fun getType(type: String): Shot.Type {
        return when (type) {
            "dribbble gif" -> Shot.Type.GIF
            "dribbble video" -> Shot.Type.VIDEO
            else -> Shot.Type.IMAGE
        }
    }

}