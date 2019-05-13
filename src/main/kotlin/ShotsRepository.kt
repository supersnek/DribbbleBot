import java.io.File

interface ShotsRepository {

    fun getTodayShots(): Set<Shot>

    fun putNewShots(newShots: Set<Shot>)

}

class FileShotsRepository(private val fileName: String) : ShotsRepository {

    private val shots = HashSet<Shot>()

    init {
        val file = File(fileName)

        if (file.exists()) file.forEachLine { line ->
            val currentDate = getDateString()
            line.fromJson<Shot>()?.let { if (it.date == currentDate) shots.add(it) }
        }
    }

    override fun getTodayShots(): Set<Shot> = synchronized(this) {
        shots.filter { it.date == getDateString() }.toSet()
    }

    override fun putNewShots(newShots: Set<Shot>) {
        synchronized(this) {
            shots.apply { addAll(newShots) }
            val filteredShots = this.shots.filter { it.date == getDateString() }

            if (shots.size != filteredShots.size){
                shots.clear()
                shots.addAll(filteredShots)
            }

            File(fileName).printWriter().use { out ->
                shots.forEach { out.println(toJson(it)) }
            }
        }
    }

}