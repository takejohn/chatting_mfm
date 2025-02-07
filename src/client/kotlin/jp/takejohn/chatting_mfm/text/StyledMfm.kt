package jp.takejohn.chatting_mfm.text

import com.github.samunohito.mfm.api.Mfm
import com.github.samunohito.mfm.api.node.IMfmNode
import jp.takejohn.chatting_mfm.mfm.DrawableMfm
import net.minecraft.client.font.TextHandler.WidthRetriever
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

/**
 * [Style] が適用された MFM。
 */
class StyledMfm(val mfm: List<IMfmNode>, style: Style) {
    private val drawable = DrawableMfm.listFrom(style, mfm)

    companion object {
        /**
         * [OrderedText] から [StyledMfm] の [List] を作成する。
         */
        fun listFrom(orderedText: OrderedText): List<StyledMfm> {
            val items = mutableListOf<StyledMfm>()
            OrderedTextWrapper(orderedText).accept { style: Style, string: String ->
                items.add(StyledMfm(Mfm.parse(string), style))
            }
            return items
        }

        fun getStyleAtFromList(mfmList: List<StyledMfm>, x: Int, widthRetriever: WidthRetriever): Style? {
            if (x < 0) {
                return null
            }
            var localX: Int = x
            for (mfm in mfmList) {
                val style = mfm.getStyleAt(localX, widthRetriever)
                if (style != null) {
                    return style
                }
                localX -= mfm.getWidth(widthRetriever)
            }
            return null
        }
    }

    fun getWidth(widthRetriever: WidthRetriever): Int {
        return DrawableMfm.getWidth(drawable, widthRetriever)
    }

    fun getStyleAt(x: Int, widthRetriever: WidthRetriever): Style? {
        return DrawableMfm.getStyleAt(drawable, x, widthRetriever)
    }

    fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean {
        return DrawableMfm.draw(drawer, index, drawable)
    }
}
