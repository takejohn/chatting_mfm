package jp.takejohn.chatting_mfm.text

import com.github.samunohito.mfm.api.node.IMfmNode
import com.github.samunohito.mfm.api.node.MfmText
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
            is MfmText -> drawText(drawer, index, style, node)
            else -> drawString(drawer, index, style, node.stringify())
        }
    }

    private fun drawText(drawer: Drawer, index: MutableInt, style: Style, node: MfmText): Boolean {
        return drawString(drawer, index, style, node.props.text)
    }

    private fun drawString(drawer: Drawer, index: MutableInt, style: Style, string: String): Boolean {
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
