package jp.takejohn.chatting_mfm.mfm

import net.minecraft.client.font.TextHandler.WidthRetriever
import net.minecraft.client.font.TextRenderer
import net.minecraft.text.Style
import org.apache.commons.lang3.mutable.MutableInt

interface IDrawableMfmNode {
    fun getWidth(widthRetriever: WidthRetriever): Int

    fun getStyleAt(x: Int, widthRetriever: WidthRetriever): Style?

    fun draw(drawer: TextRenderer.Drawer, index: MutableInt): Boolean
}
