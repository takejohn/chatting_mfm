package jp.takejohn.chatting_mfm.text

import jp.takejohn.chatting_mfm.text.TestUtils.createChatOrderedText
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OrderedTextWrapperTest {
    @Test
    fun accept() {
        val orderedText = createChatOrderedText("abc")
        val called = mutableListOf<Pair<Style, String>>()
        OrderedTextWrapper(orderedText).accept { style, string ->
            called.add(Pair(style, string))
        }
        assertEquals(listOf(
            Pair(Style.EMPTY, "<"),
            Pair(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell A ")), "A"),
            Pair(Style.EMPTY, "> abc")
        ), called)
    }
}
