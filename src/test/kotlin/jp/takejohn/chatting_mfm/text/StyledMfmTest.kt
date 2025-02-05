package jp.takejohn.chatting_mfm.text

import jp.takejohn.chatting_mfm.text.TestUtils.createChatStyledMfm
import jp.takejohn.chatting_mfm.text.TestUtils.widthRetriever
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StyledMfmTest {
    @Test
    fun listFrom() {
        val list = createChatStyledMfm("abc")
        assertEquals(3, list.size)
    }

    @Test
    fun getStyleAtFromList() {
        val styleMfm = createChatStyledMfm("abc")
        fun getStyleAt(x: Int): Style? {
            return StyledMfm.getStyleAtFromList(styleMfm, x, widthRetriever)
        }

        // 文字列の幅やスタイルは TestUtils にて定義

        assertNull(getStyleAt(-1))

        // "<": 0 <= x < 5
        assertEquals(Style.EMPTY, getStyleAt(0))
        assertEquals(Style.EMPTY, getStyleAt(4))

        // "A": 5 <= x < 11
        val expectedClickEventStyle = Style.EMPTY.withClickEvent(
            ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell A ")
        )
        assertEquals(expectedClickEventStyle, getStyleAt(5))
        assertEquals(expectedClickEventStyle, getStyleAt(10))

        // "> abc": 11 <= x < 38
        assertEquals(Style.EMPTY, getStyleAt(11))
        assertEquals(Style.EMPTY, getStyleAt(37))

        assertNull(getStyleAt(38))
    }
}
