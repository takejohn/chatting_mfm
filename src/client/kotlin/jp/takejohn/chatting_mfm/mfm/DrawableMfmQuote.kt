package jp.takejohn.chatting_mfm.mfm

import com.github.samunohito.mfm.api.node.IMfmNode
import net.minecraft.client.font.TextHandler
import net.minecraft.client.font.TextHandler.WidthRetriever
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

class DrawableMfmQuote(private val style: Style, children: List<IMfmNode>): IDrawableMfmNode {
    private val children = DrawableMfm.listFrom(style, children)

    override fun getWidth(widthRetriever: TextHandler.WidthRetriever): Int {
        return prefixWidth(widthRetriever) + DrawableMfm.getWidth(children, widthRetriever)
    }

    override fun getStyleAt(x: Int, widthRetriever: TextHandler.WidthRetriever): Style? {
        if (x < 0) {
            return null
        }
        val width = prefixWidth(widthRetriever)
        if (x < width) {
            return style
        }
        return DrawableMfm.getStyleAt(children, x - width.toInt(), widthRetriever)
    }

    override fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean {
        // "<PlayerName> message" が "<", "PlayerName", "> message" に分割されるため、
        // 引用構文を意図していないものが入る可能性がある。
        // そのため、"> "を描画しておく。
        if (!DrawableMfm.drawText(drawer, index, style, "> ")) {
            return false
        }
        return DrawableMfm.draw(drawer, index, children)
    }

    private fun prefixWidth(widthRetriever: WidthRetriever): Int {
        return (widthRetriever.getWidth('>'.code, style) + widthRetriever.getWidth(' '.code, style)).toInt()
    }
}
