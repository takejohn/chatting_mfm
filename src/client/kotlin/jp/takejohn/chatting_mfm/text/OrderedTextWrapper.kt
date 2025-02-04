package jp.takejohn.chatting_mfm.text

import net.minecraft.text.OrderedText
import net.minecraft.text.Style

/**
 * [OrderedText] から文字を取得し、
 * [Style] が同じ文字を [String] としてまとめるラッパー。
 */
class OrderedTextWrapper(private val text: OrderedText) {
    fun interface Visitor {
        fun accept(style: Style, string: String)
    }

    /**
     * [Style] と [String] を [visitor] に渡す。
     */
    fun accept(visitor: Visitor) {
        var builder = StringBuilder()
        var currentStyle: Style? = null

        text.accept { _, style, codePoint ->
            if (currentStyle != null && currentStyle != style) {
                visitor.accept(style, builder.toString())
                builder = StringBuilder()
            }
            currentStyle = style
            builder.appendCodePoint(codePoint)
            true
        }

        currentStyle?.let { visitor.accept(it, builder.toString()) }
    }
}
