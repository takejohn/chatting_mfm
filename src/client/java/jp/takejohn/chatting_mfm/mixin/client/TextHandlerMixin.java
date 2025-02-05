package jp.takejohn.chatting_mfm.mixin.client;

import jp.takejohn.chatting_mfm.mfm.DrawableMfm;
import jp.takejohn.chatting_mfm.text.OrderedTextMfmCache;
import jp.takejohn.chatting_mfm.text.StyledMfm;
import net.minecraft.client.font.TextHandler;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TextHandler.class)
public class TextHandlerMixin {
    @Shadow @Final
    TextHandler.WidthRetriever widthRetriever;

    @Inject(method = "getStyleAt(Lnet/minecraft/text/OrderedText;I)Lnet/minecraft/text/Style;", at = @At("HEAD"), cancellable = true)
    private void getStyleAt(OrderedText text, int x, CallbackInfoReturnable<@Nullable Style> cir) {
        final @Nullable List<StyledMfm> mfmList = OrderedTextMfmCache.INSTANCE.get(text);
        if (mfmList == null) {
            // MFM が作成されていない場合は通常通り実行する。
            return;
        }

        cir.setReturnValue(StyledMfm.Companion.getStyleAtFromList(mfmList, x, widthRetriever));
    }
}
