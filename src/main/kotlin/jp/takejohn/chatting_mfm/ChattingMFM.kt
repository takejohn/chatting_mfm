package jp.takejohn.chatting_mfm

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ChattingMFM : ModInitializer {
    val logger: Logger = LoggerFactory.getLogger("chatting_mfm")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}
}
