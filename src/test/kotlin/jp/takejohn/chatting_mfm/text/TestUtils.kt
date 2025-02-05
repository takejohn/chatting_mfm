package jp.takejohn.chatting_mfm.text

import net.minecraft.client.font.TextHandler
import net.minecraft.text.ClickEvent
import net.minecraft.text.OrderedText
import net.minecraft.text.Style
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * プレイヤーがチャットしたときの描画内容をシミュレートする。
 * プレイヤー "A" が "abc" とチャットすると、 "<A> abc" と描画される。
 * この行が [Style] で分割されると "<", "A", "> abc" となる。
 * "<", "> abc" は空のスタイルで、
 * "A" のスタイルは `clickEvent` に `ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell A ")` が指定される。
 */
object TestUtils {
    val widthRetriever = TextHandler.WidthRetriever { codePoint, _ ->
        when (codePoint) {
            ' '.code -> 4.0f
            '<'.code, '>'.code -> 5.0f
            'A'.code, 'a'.code, 'b'.code, 'c'.code -> 6.0f
            else -> throw AssertionError("unexpected code point: $codePoint")
        }
    }

    val lessThanSignText: OrderedText = OrderedText.styledForwardsVisitedString("<", Style.EMPTY)

    val playerNameText: OrderedText = OrderedText.styledForwardsVisitedString(
        "A", Style.EMPTY.withClickEvent(
            ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell A ")
        )
    )

    fun messageText(message: String): OrderedText {
        return OrderedText.styledForwardsVisitedString("> $message", Style.EMPTY)
    }

    fun createChatOrderedText(message: String): OrderedText {
        return OrderedText.concat(lessThanSignText, playerNameText, messageText(message))
    }

    fun createChatStyledMfm(message: String): List<StyledMfm> {
        val text = createChatOrderedText(message)
        return StyledMfm.listFrom(text)
    }
}

class TestUtilsTest {
    @Test
    fun verifyLessThanSignText() {
        var called = 0
        TestUtils.lessThanSignText.accept { _, style, _ ->
            called++
            assertEquals(Style.EMPTY, style)
            true
        }
        assertEquals(1, called)
    }

    @Test
    fun verifyPlayerNameText() {
        var called = 0
        TestUtils.playerNameText.accept { _, style, _ ->
            called++
            assertEquals(
                Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell A ")),
                style,
            )
            true
        }
        assertEquals(1, called)
    }

    @Test
    fun verifyMessageText() {
        var called = 0
        TestUtils.messageText("abc").accept { _, style, _ ->
            called++
            assertEquals(Style.EMPTY, style)
            true
        }
        assertEquals(5, called)
    }
}
