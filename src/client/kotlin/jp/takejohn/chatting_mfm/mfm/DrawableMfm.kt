package jp.takejohn.chatting_mfm.mfm

import com.github.samunohito.mfm.api.node.*
import net.minecraft.client.font.TextHandler.WidthRetriever
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.Drawer
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

/** リンクの色 (#4493f8)。 */
const val COLOR_LINK = 0x4493f8

object DrawableMfm {
    fun listFrom(style: Style, mfm: List<IMfmNode>): List<IDrawableMfmNode> {
        return mfm.map {
            when (it) {
                is MfmText -> DrawableMfmText(style, it.props)
                is MfmBold -> DrawableSimpleStyle.bold(style, it.children)
                is MfmItalic -> DrawableSimpleStyle.italic(style, it.children)
                is MfmStrike -> DrawableSimpleStyle.strike(style, it.children)
                is MfmQuote -> DrawableMfmQuote(style, it.children)
                is MfmUrl -> DrawableMfmText(createUrlStyle(style, it.props.url), MfmText.Props(it.props.url))
                is MfmLink -> DrawableSimpleStyle(createUrlStyle(style, it.props.url), it.children)
                is MfmEmojiCode -> DrawableMfmEmojiCode(style, it.props)
                else -> DrawableMfmText(style, MfmText.Props(it.stringify()))
            }
        }
    }

    fun getWidth(tree: List<IDrawableMfmNode>, widthRetriever: WidthRetriever): Int {
        return tree.sumOf { it.getWidth(widthRetriever) }
    }

    fun getStyleAt(tree: List<IDrawableMfmNode>, x: Int, widthRetriever: WidthRetriever): Style? {
        var localX = x
        for (node in tree) {
            val style = node.getStyleAt(localX, widthRetriever)
            if (style != null) {
                return style
            }
            localX -= node.getWidth(widthRetriever)
        }
        return null
    }

    fun draw(drawer: TextRenderer.Drawer, index: MutableInt, tree: List<IDrawableMfmNode>): Boolean {
        for (node in tree) {
            if (!node.draw(drawer, index)) {
                return false
            }
        }
        return true
    }

    fun drawText(drawer: Drawer, index: MutableInt, style: Style, string: String): Boolean {
        val codePoints = string.codePoints().iterator()
        while (codePoints.hasNext()) {
            val codePoint = codePoints.next()
            if (!drawer.accept(index.value, style, codePoint)) {
                return false
            }
            index.increment()
        }
        return true
    }

    private fun createUrlStyle(style: Style, url: String): Style {
        return style.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url)).withColor(COLOR_LINK)
    }
}
