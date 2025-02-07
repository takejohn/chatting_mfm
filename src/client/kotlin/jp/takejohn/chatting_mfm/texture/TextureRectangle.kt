package jp.takejohn.chatting_mfm.texture

import net.minecraft.util.Identifier

data class TextureRectangle(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float,
    val zIndex: Float,
    val textureId: Identifier,
    var shadowColor: Int,
    var shadowOffset: Int
) {}
