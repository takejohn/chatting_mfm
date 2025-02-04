package jp.takejohn.chatting_mfm.mixin.client;

import jp.takejohn.chatting_mfm.text.OrderedTextMfmCache;
import jp.takejohn.chatting_mfm.text.StyledMfm;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
    /**
     * {@link TextRenderer#drawLayer(OrderedText, float, float, int, boolean, Matrix4f, VertexConsumerProvider, TextRenderer.TextLayerType, int, int, boolean)}
     * において <code>text.accept(drawer);</code> の処理を代替する。
     */
    @Redirect(method = "drawLayer(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/OrderedText;accept(Lnet/minecraft/text/CharacterVisitor;)Z"))
    private boolean redirectTextAccept(OrderedText instance, CharacterVisitor characterVisitor) {
        final @Nullable List<StyledMfm> mfmList = OrderedTextMfmCache.INSTANCE.get(instance);
        if (mfmList == null) {
            // MFM が作成されていない場合は通常通り実行する。
            return instance.accept(characterVisitor);
        }
        // MFM が存在する場合
        return setMfmList(mfmList, (TextRenderer.Drawer) characterVisitor);
    }

    @Unique
    private boolean setMfmList(List<StyledMfm> mfmList, TextRenderer.Drawer drawer) {
        final MutableInt index = new MutableInt(0);
        for (final StyledMfm mfm : mfmList) {
            boolean doContinue = mfm.draw(drawer, index);
            if (!doContinue) {
                return false;
            }
        }
        return true;
    }
}
