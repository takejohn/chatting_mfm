package jp.takejohn.chatting_mfm.mixin.client;

import net.minecraft.client.font.TextHandler;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextHandler.WidthLimitingVisitor.class)
public class TextHandlerWidthLimitingVisitorMixin {
    @Inject(method = "accept", at = @At("HEAD"))
    private void accept(int i, Style style, int j, CallbackInfoReturnable<Boolean> cir) {

    }
}
