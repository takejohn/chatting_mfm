package jp.takejohn.chatting_mfm.text

import jp.takejohn.chatting_mfm.CONFIG
import net.minecraft.text.OrderedText
import java.util.WeakHashMap

/**
 * [OrderedText] から作成された [StyledMfm] の [List] を取得する機能を提供する。
 * [WeakHashMap] を使用しているので、
 * [OrderedText] がメモリから解放されると [StyledMfm] も自動的に削除される。
 */
object OrderedTextMfmCache {
    private val map = WeakHashMap<OrderedText, Lazy<List<StyledMfm>>>()

    /**
     * [OrderedText] から作成された [StyledMfm] の [List] を返す。
     */
    fun get(orderedText: OrderedText): List<StyledMfm>? {
        if (!CONFIG.isEnabled) {
            return null
        }
        return map[orderedText]?.value
    }

    /**
     * [OrderedText] から [StyledMfm] の [List] を作成して保存する。
     */
    fun add(orderedText: OrderedText): Lazy<List<StyledMfm>> {
        val result = object : Lazy<List<StyledMfm>> {
            var maybeValue: List<StyledMfm>? = null

            override val value: List<StyledMfm>
                get() {
                    maybeValue?.let {
                        return it
                    }
                    val value = StyledMfm.listFrom(orderedText)
                    maybeValue = value
                    return value
                }

            override fun isInitialized(): Boolean = maybeValue != null
        }
        map[orderedText] = result
        return result
    }
}
