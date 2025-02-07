package jp.takejohn.chatting_mfm.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import jp.takejohn.chatting_mfm.emoji.MisskeyEmoji;
import jp.takejohn.chatting_mfm.texture.TextureRectangle;
import jp.takejohn.chatting_mfm.mixininterface.AddEmoji;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TextRenderer.Drawer.class)
abstract public class TextRendererDrawerMixin implements AddEmoji {
    /**
     * おそらくこの {@link TextRenderer.Drawer} がもつ {@link TextRenderer} インスタンスの参照 (<code>this$0</code>)
     */
    @Shadow
    @Final
    TextRenderer field_24240;

    @Shadow
    float x;

    @Shadow
    float y;

    @Unique
    private @Nullable List<TextureRectangle> textureRectangles;

    @Shadow
    @Final
    private Matrix4f matrix;

    @Shadow
    @Final
    private int light;

    @Shadow protected abstract float getForegroundZIndex();

    @Unique
    private void addImage(TextureRectangle textureRectangle) {
        if (this.textureRectangles == null) {
            this.textureRectangles = Lists.newArrayList();
        }
        this.textureRectangles.add(textureRectangle);
    }

    @Override
    public boolean chatting_mfm$addEmoji(int index, Style style, MisskeyEmoji emoji) {
        final float width = emoji.getWidth();
        final float minX = index == 0 ? x - 1.0f : x;
        final float maxX = x + width;
        final float maxY = y + field_24240.fontHeight;
        final float zIndex = getForegroundZIndex() + 0.01f;
        final Identifier textureId = emoji.getTextureId();
        addImage(new TextureRectangle(minX, y, maxX, maxY, zIndex, textureId, 0xff000000, 0));
        x = maxX;
        return true;
    }

    @Inject(method = "drawLayer", at = @At("TAIL"))
    private void drawLayer(float x, CallbackInfoReturnable<Float> cir) {
        if (textureRectangles == null) {
            return;
        }

        for (final TextureRectangle textureRectangle : textureRectangles) {
            drawTextureRectangle(textureRectangle);
        }
    }

    @Unique
    public void drawTextureRectangle(TextureRectangle textureRectangle) {
        final float minX = textureRectangle.getMinX();
        final float minY = textureRectangle.getMinY();
        final float maxX = textureRectangle.getMaxX();
        final float maxY = textureRectangle.getMaxY();
        final float zIndex = textureRectangle.getZIndex();

        final float minU = 0.0f;
        final float minV = 0.0f;
        final float maxU = 1.0f;
        final float maxV = 1.0f;

        Tessellator tessellator = Tessellator.getInstance();

        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        // 長方形の頂点データを作成 (反時計回り)
        setVertex(bufferBuilder, minX, minY, zIndex, minU, minV, light); // 左上
        setVertex(bufferBuilder, minX, maxY, zIndex, minU, maxV, light); // 左下
        setVertex(bufferBuilder, maxX, maxY, zIndex, maxU, maxV, light); // 右下
        setVertex(bufferBuilder, maxX, minY, zIndex, maxU, minV, light); // 右上

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        RenderSystem.setShaderTexture(0, textureRectangle.getTextureId());

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    @Unique
    private void setVertex(BufferBuilder bufferBuilder, float x, float y, float z, float u, float v, int light) {
        bufferBuilder
                .vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .light(light);
    }
}
