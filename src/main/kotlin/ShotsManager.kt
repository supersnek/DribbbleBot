class ShotsManager(
    private val chatsRepository: ChatsRepository,
    private val shotsRepository: ShotsRepository,
    private val shotsProvider: ShotsProvider,
    private val sender: Sender
) {

    fun updateShots() {
        val chats = chatsRepository.getChats()
        val shotsIds = shotsRepository.getTodayShots().map { it.id }.toSet()

        val shots = chats
            .map { it.tags }
            .flatten()
            .toSet()
            .map { it to shotsProvider.getPostsByTag(it, shotsIds) }
            .toMap()

        chats.forEach { chat ->
            sender.sendShots(chat.id, chat.tags.mapNotNull { shots[it] }.flatten().toSet())
        }

        shotsRepository.putNewShots(shots.values.flatten().toSet())
    }

}