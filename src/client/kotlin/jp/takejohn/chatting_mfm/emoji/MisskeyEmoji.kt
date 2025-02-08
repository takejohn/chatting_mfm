package jp.takejohn.chatting_mfm.emoji

import jp.takejohn.chatting_mfm.ChattingMFM
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.util.Identifier
import org.apache.commons.io.FilenameUtils
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.metadata.IIOMetadataNode

class MisskeyEmoji(private val data: MisskeyEmojiDetailed, inputStream: InputStream) : MisskeyEmojiDetailed by data {
    inner class Frame(
        val image: NativeImage,
        val index: Int,
        /**
         * フレームの遅延時間 (ミリ秒単位)
         */
        val delay: Long,
    ) {
        val width = image.width.toFloat() / image.height.toFloat() * MinecraftClient.getInstance().textRenderer.fontHeight

        val textureId: Identifier = Identifier.of("chatting_mfm", "textures/temporal/emoji_${name}_${index}")
    }

    companion object {
        /**
         * 例外がある場合は投げるのではなく null を返す。
         */
        fun create(data: MisskeyEmojiDetailed, inputStream: InputStream): MisskeyEmoji? {
            try {
                return MisskeyEmoji(data, inputStream)
            } catch (e: Throwable) {
                ChattingMFM.logger.warn("Invalid emoji content ($data):", e)
                return null
            }
        }

        private const val MIN_FRAME_DELAY = 20L

        private const val DEFAULT_FRAME_DELAY = 100L
    }

    val frames: List<Frame>

    val cycle: Long

    init {
        val suffix = FilenameUtils.getExtension(data.url)
        val reader = ImageIO.getImageReadersBySuffix(suffix).next()
        val imageInputStream = ImageIO.createImageInputStream(inputStream)
        reader.input = imageInputStream

        val frames = mutableListOf<Frame>()

        if (suffix == "gif") {
            val imageParam = reader.defaultReadParam
            for (index in 0 until reader.getNumImages(true)) {
                val bufferedImage = reader.read(index, imageParam)
                val delay = getGifFrameDelay(reader, index)
                val fixedDelay = if (delay < MIN_FRAME_DELAY) {
                    DEFAULT_FRAME_DELAY
                } else {
                    delay
                }
                frames.add(Frame(bufferedImageToNativeImage(bufferedImage), index, fixedDelay))
            }
        } else {
            for (index in 0 until reader.getNumImages(true)) {
                frames.add(Frame(bufferedImageToNativeImage(reader.read(index)), index, DEFAULT_FRAME_DELAY))
            }
        }

        this.frames = frames
        this.cycle = frames.sumOf { it.delay }
    }

    /**
     * GIFフレームの遅延時間 (ミリ秒単位) を取得する
     */
    private fun getGifFrameDelay(reader: javax.imageio.ImageReader, index: Int): Long {
        val metadata = reader.getImageMetadata(index)
        val root = metadata.getAsTree("javax_imageio_gif_image_1.0") as IIOMetadataNode
        val nodeList = root.getElementsByTagName("GraphicControlExtension")

        if (nodeList.length > 0) {
            val node = nodeList.item(0) as IIOMetadataNode
            val delayTime = node.getAttribute("delayTime") // 1/100秒単位
            return delayTime.toLongOrNull()?.let { it * 10L } ?: DEFAULT_FRAME_DELAY // ミリ秒単位に変換
        }
        return 100L // デフォルト100ms
    }

    private fun bufferedImageToNativeImage(bufferedImage: BufferedImage): NativeImage {
        val nativeImage = NativeImage(bufferedImage.width, bufferedImage.height, false)
        val width = bufferedImage.width
        val height = bufferedImage.height
        for (y in 0 until height) {
            for (x in 0 until width) {
                nativeImage.setColorArgb(x, y, bufferedImage.getRGB(x, y))
            }
        }
        return nativeImage
    }
}
