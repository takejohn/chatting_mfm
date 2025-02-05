package jp.takejohn.chatting_mfm.mfm

import com.github.samunohito.mfm.api.node.MfmText
import net.minecraft.client.font.TextHandler
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

class DrawableMfmText(private val style: Style, private val props: MfmText.Props): IDrawableMfmNode {
    override fun getWidth(widthRetriever: TextHandler.WidthRetriever): Int {
        return props.text.codePoints().mapToDouble { widthRetriever.getWidth(it, style).toDouble() }.sum().toInt()
    }

    override fun getStyleAt(x: Int, widthRetriever: TextHandler.WidthRetriever): Style? {
        if (x < 0) {
            return null
        }
        var width = 0.0f
        for (codePoint in props.text.codePoints()) {
            width += widthRetriever.getWidth(codePoint, style)
            if (x < width) {
                return style
            }
        }
        return null
    }

    override fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean {
        return DrawableMfm.drawText(drawer, index, style, props.text)
    }
}
