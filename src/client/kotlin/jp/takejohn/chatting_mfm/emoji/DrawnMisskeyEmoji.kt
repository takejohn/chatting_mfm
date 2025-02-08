package jp.takejohn.chatting_mfm.emoji

class DrawnMisskeyEmoji(private val emoji: MisskeyEmoji) {
    private val startTime = System.currentTimeMillis()

    private var index = 0

    val frame get(): MisskeyEmoji.Frame {
        return emoji.frames[index]
    }

    fun update(): MisskeyEmoji.Frame {
        val currentTime = System.currentTimeMillis()
        if (emoji.cycle == 0L) {
            return emoji.frames[0]
        }
        val delta = (currentTime - startTime) % emoji.cycle
        index = deltaToFrame(delta)
        return emoji.frames[index]
    }

    private fun deltaToFrame(delta: Long): Int {
        var delay = 0L
        for (frame in emoji.frames) {
            delay += frame.delay
            if (delta < delay) {
                return frame.index
            }
        }

        return 0
    }
}
