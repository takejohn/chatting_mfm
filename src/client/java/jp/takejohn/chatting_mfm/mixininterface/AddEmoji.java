package jp.takejohn.chatting_mfm.mixininterface;

import jp.takejohn.chatting_mfm.emoji.MisskeyEmoji;
import net.minecraft.text.Style;

public interface AddEmoji {
    default boolean chatting_mfm$addEmoji(int index, Style style, MisskeyEmoji.Frame emoji) {
        // dummy body
        return false;
    }
}
