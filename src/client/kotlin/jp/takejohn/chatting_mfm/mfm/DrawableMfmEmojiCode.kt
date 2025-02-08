package jp.takejohn.chatting_mfm.mfm

import com.github.samunohito.mfm.api.node.MfmEmojiCode
import com.github.samunohito.mfm.api.node.MfmText
import jp.takejohn.chatting_mfm.emoji.DrawnMisskeyEmoji
import jp.takejohn.chatting_mfm.emoji.MisskeyEmojiCache
import jp.takejohn.chatting_mfm.mixininterface.AddEmoji
import net.minecraft.client.font.TextHandler
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import org.apache.commons.lang3.mutable.MutableInt

class DrawableMfmEmojiCode(private val style: Style, private val props: MfmEmojiCode.Props): IDrawableMfmNode {
    private inner class Pending(text: DrawableMfmText): IDrawableMfmNode by text

    private inner class Resolved(style: Style, private val emoji: DrawnMisskeyEmoji): IDrawableMfmNode {
        private val style = style.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(altText)))

        override fun getWidth(widthRetriever: TextHandler.WidthRetriever): Int {
            return emoji.frame.width.toInt()
        }

        override fun getStyleAt(x: Int, widthRetriever: TextHandler.WidthRetriever): Style? {
            return if (x > 0 && x < emoji.frame.width) {
                style
            } else {
                null
            }
        }

        override fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean {
            // インターフェースインジェクションによって追加したメソッドを呼び出す
            return (drawer as AddEmoji).`chatting_mfm$addEmoji`(index.value, style, emoji.update())
        }
    }

    private val altText = ":${props.name}:"

    private var state: IDrawableMfmNode = Pending(DrawableMfmText(style, MfmText.Props(altText)))

    override fun getWidth(widthRetriever: TextHandler.WidthRetriever): Int {
        return state.getWidth(widthRetriever)
    }

    override fun getStyleAt(x: Int, widthRetriever: TextHandler.WidthRetriever): Style? {
        return state.getStyleAt(x, widthRetriever)
    }

    override fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean {
        if (state is Pending) {
            MisskeyEmojiCache.get(props.name)?.let {
                state = Resolved(style, DrawnMisskeyEmoji(it))
            }
        }
        return state.draw(drawer, index)
    }
}
