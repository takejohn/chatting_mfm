package jp.takejohn.chatting_mfm.mixin.client;

import jp.takejohn.chatting_mfm.text.OrderedTextMfmCache;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHudLine.Visible.class)
public class ChatHudLineVisibleMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(int i, OrderedText orderedText, MessageIndicator messageIndicator, boolean bl, CallbackInfo ci) {
        OrderedTextMfmCache.INSTANCE.add(orderedText);
    }
}
