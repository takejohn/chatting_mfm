package jp.takejohn.chatting_mfm.text

import com.github.samunohito.mfm.api.node.*
import jp.takejohn.chatting_mfm.CONFIG
import jp.takejohn.chatting_mfm.emoji.MisskeyEmojiCache
import net.minecraft.text.OrderedText

object ChatTextHandler {
    fun accept(orderedText: OrderedText) {
        // 遅延評価によって styledMfmList は使用されるときまでパースされない
        val styledMfmList by OrderedTextMfmCache.add(orderedText)

        if (CONFIG.isEnabled) {
            for (styledMfm in styledMfmList) {
                loadEmojis(styledMfm.mfm)
            }
        }
    }

    private fun loadEmojis(mfm: List<IMfmNode>) {
        for (node in mfm) {
            if (node is MfmEmojiCode) {
                MisskeyEmojiCache.load(node.props.name)
            } else if (node is IMfmNodeChildrenHolder) {
                loadEmojis(node.children)
            }
        }
    }
}
