package jp.takejohn.chatting_mfm.mfm

import com.github.samunohito.mfm.api.Mfm
import net.minecraft.client.font.TextHandler.WidthRetriever
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DrawableMfmTest {
    private val widthRetriever = WidthRetriever { codePoint, style ->
        val base = when (codePoint) {
            'a'.code, 'b'.code, 'c'.code -> 6.0f
            else -> throw AssertionError("unexpected code point: $codePoint")
        }
        if (style.isBold) {
            base + 1
        } else {
            base
        }
    }

    private fun create(input: String): List<IDrawableMfmNode> {
        return DrawableMfm.listFrom(Style.EMPTY, Mfm.parse(input))
    }

    @Nested
    inner class GetWidth {
        private fun getWidth(input: String): Int {
            return DrawableMfm.getWidth(create(input), widthRetriever)
        }

        @Test
        fun empty() {
            assertEquals(0, getWidth(""))
        }

        @Test
        fun text() {
            assertEquals(18, getWidth("abc"))
        }

        @Test
        fun bold() {
            assertEquals(21, getWidth("**abc**"))
        }

        @Test
        fun link() {
            assertEquals(18, getWidth("[abc](https://example.com/)"))
        }
    }

    @Nested
    inner class GetStyleAt {
        private fun getStyleAt(drawable: List<IDrawableMfmNode>, x: Int): Style? {
            return DrawableMfm.getStyleAt(drawable, x, widthRetriever)
        }

        @Test
        fun empty() {
            assertNull(getStyleAt(create(""), 0))
        }

        @Test
        fun text() {
            val drawable = create("abc")
            assertNull(getStyleAt(drawable, -1))
            assertEquals(Style.EMPTY, getStyleAt(drawable, 0))
            assertEquals(Style.EMPTY, getStyleAt(drawable, 17))
            assertNull(getStyleAt(drawable, 18))
        }

        @Test
        fun bold() {
            val drawable = create("**abc**")
            assertNull(getStyleAt(drawable, -1))
            assertEquals(Style.EMPTY.withBold(true), getStyleAt(drawable, 0))
            assertEquals(Style.EMPTY.withBold(true), getStyleAt(drawable, 20))
            assertNull(getStyleAt(drawable, 21))
        }

        @Test
        fun link() {
            val drawable = create("[abc](https://example.com/)")
            val expectedClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://example.com/")
            val expectedStyle = Style.EMPTY.withClickEvent(expectedClickEvent)
            assertNull(getStyleAt(drawable, -1))
            assertEquals(expectedStyle, getStyleAt(drawable, 0))
            assertEquals(expectedStyle, getStyleAt(drawable, 17))
            assertNull(getStyleAt(drawable, 18))
        }
    }
}
