package jp.takejohn.chatting_mfm.mfm

import com.github.samunohito.mfm.api.node.IMfmNode
import net.minecraft.client.font.TextHandler
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

class DrawableSimpleStyle(private val style: Style, children: List<IMfmNode>): IDrawableMfmNode {
    companion object {
        fun bold(style: Style, children: List<IMfmNode>): DrawableSimpleStyle {
            return DrawableSimpleStyle(style.withBold(true), children)
        }

        fun italic(style: Style, children: List<IMfmNode>): DrawableSimpleStyle {
            return DrawableSimpleStyle(style.withItalic(true), children)
        }

        fun strike(style: Style, children: List<IMfmNode>): DrawableSimpleStyle {
            return DrawableSimpleStyle(style.withStrikethrough(true), children)
        }
    }

    private val children: List<IDrawableMfmNode> = DrawableMfm.listFrom(style, children)

    override fun getWidth(widthRetriever: TextHandler.WidthRetriever): Int {
        return DrawableMfm.getWidth(children, widthRetriever)
    }

    override fun getStyleAt(x: Int, widthRetriever: TextHandler.WidthRetriever): Style? {
        return DrawableMfm.getStyleAt(children, x, widthRetriever)
    }

    override fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean {
        return DrawableMfm.draw(drawer, index, children)
    }
}
