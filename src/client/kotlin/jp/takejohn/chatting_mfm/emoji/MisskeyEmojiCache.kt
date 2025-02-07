package jp.takejohn.chatting_mfm.emoji

import jp.takejohn.chatting_mfm.CONFIG
import jp.takejohn.chatting_mfm.ChattingMFM
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import java.awt.image.BufferedImage
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

object MisskeyEmojiCache {
    private val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

    private val emojis = mutableMapOf<String, CompletableFuture<MisskeyEmoji?>>()

    /**
     * 保存された絵文字を取得する。
     */
    fun get(name: String): MisskeyEmoji? {
        return emojis[name]?.getNow(null)
    }

    /**
     * 絵文字のGETリクエストをMisskeyサーバーに非同期で送る。
     * レスポンスが返ってくると [get] で取得できるようになる。
     */
    fun load(name: String): CompletableFuture<MisskeyEmoji?> {
        emojis[name]?.let {
            return it
        }

        if (!CONFIG.isEnabled || CONFIG.emojiMisskeyServer.isEmpty()) {
            return CompletableFuture.completedFuture(null)
        }

        val uri = tryCreateEmojiUri(name) ?: return CompletableFuture.completedFuture(null)
        val request = HttpRequest.newBuilder()
            .uri(uri)
            .build()

        val future = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).handle { response, e ->
            e?.let {
                ChattingMFM.logger.warn("Failed to send request to Misskey server:", e)
                return@handle null
            }
            try {
                if (response.statusCode() == 200) {
                    // HTTP レスポンスステータスコード 200: OK
                    return@handle createMisskeyEmoji(name, ImageIO.read(response.body()))
                }
                ChattingMFM.logger.warn("Misskey server responded with status other than 200: ${response.statusCode()}")
            } catch (e: Exception) {
                ChattingMFM.logger.warn("Failed to parse emoji:", e)
            }
            return@handle null
        }
        emojis[name] = future
        return future
    }

    /**
     * 絵文字のキャッシュを削除する。
     */
    fun clear() {
        emojis.clear()
    }

    private fun tryCreateEmojiUri(emojiName: String): URI? {
        try {
            return URI.create("https://${CONFIG.emojiMisskeyServer}/emoji/${emojiName}.webp")
        } catch (e: Exception) {
            ChattingMFM.logger.warn("Invalid Misskey server:", e)
            return null
        }
    }

    private fun createMisskeyEmoji(name: String, image: BufferedImage): MisskeyEmoji {
        val nativeImage = bufferedImageToNativeImage(image)
        val id = registerEmojiTexture(name, nativeImage)
        return MisskeyEmoji(name, id, nativeImage)
    }

    private fun bufferedImageToNativeImage(bufferedImage: BufferedImage): NativeImage {
        val nativeImage = NativeImage(bufferedImage.width, bufferedImage.height, false);
        val width = bufferedImage.width
        val height = bufferedImage.height
        for (y in 0..<height) {
            for (x in 0..<width) {
                nativeImage.setColorArgb(x, y, bufferedImage.getRGB(x, y))
            }
        }
        return nativeImage
    }

    private fun registerEmojiTexture(name: String, image: NativeImage): Identifier {
        val id = Identifier.of("chatting_mfm", "textures/temporal/emoji_$name")
        MinecraftClient.getInstance().textureManager.registerTexture(id, NativeImageBackedTexture(image))
        return id
    }
}
