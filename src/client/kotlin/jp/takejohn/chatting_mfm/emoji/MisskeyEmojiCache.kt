package jp.takejohn.chatting_mfm.emoji

import jp.takejohn.chatting_mfm.CONFIG
import jp.takejohn.chatting_mfm.ChattingMFM
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImageBackedTexture
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

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

        val uri = tryCreateEmojiApiUri() ?: return CompletableFuture.completedFuture(null)
        val request = HttpRequest
            .newBuilder()
            .uri(uri)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("""{"name": "$name"}"""))
            .build()

        val future = client
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .handle(this::handleEmojiApiResponse)
            .thenCompose(this::requestEmoji)
        emojis[name] = future
        return future
    }

    /**
     * 絵文字のキャッシュを削除する。
     */
    fun clear() {
        emojis.clear()
    }

    private fun tryCreateEmojiApiUri(): URI? {
        try {
            return URI.create("https://${CONFIG.emojiMisskeyServer}/api/emoji")
        } catch (e: Exception) {
            ChattingMFM.logger.warn("Invalid Misskey server:", e)
            return null
        }
    }

    private fun registerEmojiTexture(frame: MisskeyEmoji.Frame) {
        MinecraftClient.getInstance().textureManager.registerTexture(
            frame.textureId, NativeImageBackedTexture(frame.image)
        )
    }

    private fun handleEmojiApiResponse(response: HttpResponse<String>, e: Throwable?): MisskeyEmojiDetailed? {
        val body = checkResponse(response, e) ?: return null

        try {
            return MisskeyEmojiDetailedData.fromJson(body)
        } catch (e: Exception) {
            ChattingMFM.logger.warn("Failed to parse response from Misskey server:", e)
            return null
        }
    }

    private fun requestEmoji(data: MisskeyEmojiDetailed?): CompletableFuture<MisskeyEmoji?> {
        data ?: return CompletableFuture.completedFuture(null)
        val uri = URI.create(data.url)
        val request = HttpRequest.newBuilder().uri(uri).build()
        return client
            .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
            .handle { response, e ->
                val body = checkResponse(response, e) ?: return@handle null
                return@handle createMisskeyEmoji(data, body)
            }
    }

    private fun createMisskeyEmoji(data: MisskeyEmojiDetailed?, inputStream: InputStream): MisskeyEmoji? {
        return data?.let {
            val emoji = MisskeyEmoji.create(data, inputStream) ?: return null
            for (frame in emoji.frames) {
                registerEmojiTexture(frame)
            }
            emoji
        }
    }

    private fun <T> checkResponse(response: HttpResponse<T>, e: Throwable?): T? {
        e?. let {
            ChattingMFM.logger.warn("Failed to send request to Misskey server (${response.uri()}):", e)
            return null
        }

        // HTTP レスポンスステータスコード 200: OK
        val statusCode = response.statusCode()
        return if (statusCode == 200) {
            response.body()
        } else {
            ChattingMFM.logger.warn("Misskey server responded with status other than 200 (${response.uri()}): $statusCode")
            null
        }
    }
}
