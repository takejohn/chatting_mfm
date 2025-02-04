package jp.takejohn.chatting_mfm.text

import net.minecraft.text.OrderedText
import java.util.WeakHashMap

/**
 * [OrderedText] から作成された [StyledMfm] の [List] を取得する機能を提供する。
 * [WeakHashMap] を使用しているので、
 * [OrderedText] がメモリから解放されると [StyledMfm] も自動的に削除される。
 */
object OrderedTextMfmCache {
    private val map = WeakHashMap<OrderedText, List<StyledMfm>>()

    /**
     * [OrderedText] から作成された [StyledMfm] の [List] を返す。
     */
    fun get(orderedText: OrderedText): List<StyledMfm>? {
        return map[orderedText]
    }

    /**
     * [OrderedText] から [StyledMfm] の [List] を作成して保存する。
     */
    fun add(orderedText: OrderedText) {
        map[orderedText] = StyledMfm.listFrom(orderedText)
    }
}
