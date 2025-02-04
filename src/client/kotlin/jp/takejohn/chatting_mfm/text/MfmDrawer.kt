package jp.takejohn.chatting_mfm.text

import com.github.samunohito.mfm.api.node.*
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.font.TextRenderer.Drawer
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

object MfmDrawer {
    fun draw(drawer: TextRenderer.Drawer, index: MutableInt, style: Style, mfm: List<IMfmNode>): Boolean {
        for (node in mfm) {
            val doContinue = drawNode(drawer, index, style, node)
            if (!doContinue) {
                return false
            }
        }
        return true
    }

    private fun drawNode(drawer: TextRenderer.Drawer, index: MutableInt, style: Style, node: IMfmNode): Boolean {
        return when (node) {
            is MfmText -> drawText(drawer, index, style, node.props.text)
            is MfmBold -> draw(drawer, index, style.withBold(true), node.children)
            is MfmItalic -> draw(drawer, index, style.withItalic(true), node.children)
            is MfmStrike -> draw(drawer, index, style.withStrikethrough(true), node.children)
            is MfmQuote -> drawQuote(drawer, index, style, node)
            else -> drawText(drawer, index, style, node.stringify())
        }
    }

    private fun drawQuote(drawer: Drawer, index: MutableInt, style: Style, node: MfmQuote): Boolean {
        // "<PlayerName> message" が "<", "PlayerName", "> message" に分割されるため、
        // 引用構文を意図していないものが入る可能性がある。
        // そのため、"> "を描画しておく。
        if (!drawText(drawer, index, style, "> ")) {
            return false
        }
        return draw(drawer, index, style, node.children)
    }

    private fun drawText(drawer: Drawer, index: MutableInt, style: Style, string: String): Boolean {
        val codePoints = string.codePoints().iterator()
        while (codePoints.hasNext()) {
            val codePoint = codePoints.next()
            val doContinue = drawer.accept(index.value, style, codePoint)
            if (!doContinue) {
                return false
            }
            index.increment()
        }
        return true
    }
}
