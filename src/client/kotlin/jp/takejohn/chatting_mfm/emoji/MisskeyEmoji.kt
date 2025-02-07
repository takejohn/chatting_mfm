package jp.takejohn.chatting_mfm.emoji

import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.util.Identifier

class MisskeyEmoji(val name: String, val textureId: Identifier, val image: NativeImage) {
    val width = image.width.toFloat() / image.height.toFloat() * MinecraftClient.getInstance().textRenderer.fontHeight
}
