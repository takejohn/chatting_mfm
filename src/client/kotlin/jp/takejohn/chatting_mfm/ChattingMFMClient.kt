package jp.takejohn.chatting_mfm

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ClientModInitializer

object ChattingMFMClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		AutoConfig.register(ChattingMFMConfig::class.java, ::GsonConfigSerializer)
	}
}
