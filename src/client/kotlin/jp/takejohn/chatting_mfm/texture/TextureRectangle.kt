package jp.takejohn.chatting_mfm.texture

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.render.*
import net.minecraft.util.Identifier
import org.joml.Matrix4f

data class TextureRectangle(
    val minX: Float,
    val minY: Float,
    val maxX: Float,
    val maxY: Float,
    val zIndex: Float,
    val textureId: Identifier,
    var shadowColor: Int,
    var shadowOffset: Int
) {
    fun draw(matrix: Matrix4f, light: Int) {
        val minX: Float = this.minX
        val minY: Float = this.minY
        val maxX: Float = this.maxX
        val maxY: Float = this.maxY
        val zIndex: Float = this.zIndex

        val minU = 0.0f
        val minV = 0.0f
        val maxU = 1.0f
        val maxV = 1.0f

        val tessellator = Tessellator.getInstance()

        val bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)

        // 長方形の頂点データを作成 (反時計回り)
        setVertex(bufferBuilder, matrix, minX, minY, zIndex, minU, minV, light) // 左上
        setVertex(bufferBuilder, matrix, minX, maxY, zIndex, minU, maxV, light) // 左下
        setVertex(bufferBuilder, matrix, maxX, maxY, zIndex, maxU, maxV, light) // 右下
        setVertex(bufferBuilder, matrix, maxX, minY, zIndex, maxU, minV, light) // 右上

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX)
        RenderSystem.setShaderTexture(0, this.textureId)

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end())
    }

    private fun setVertex(bufferBuilder: BufferBuilder, matrix: Matrix4f, x: Float, y: Float, z: Float, u: Float, v: Float, light: Int) {
        bufferBuilder
            .vertex(matrix, x, y, z)
            .color(255, 255, 255, 255)
            .texture(u, v)
            .light(light)
    }
}
