package jp.takejohn.chatting_mfm.text

import com.github.samunohito.mfm.api.Mfm
import com.github.samunohito.mfm.api.node.IMfmNode
import com.github.samunohito.mfm.api.stringify
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

/**
 * [Style] が適用された MFM。
 */
class StyledMfm(private val mfm: List<IMfmNode>, private val style: Style) {
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
    }

    fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean {
        return MfmDrawer.draw(drawer, index, this.style, this.mfm)
    }
}
