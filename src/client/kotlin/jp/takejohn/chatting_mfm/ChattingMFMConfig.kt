package jp.takejohn.chatting_mfm

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config

val CONFIG: ChattingMFMConfig by lazy { AutoConfig.getConfigHolder(ChattingMFMConfig::class.java).config }

@Config(name = "chattingmfm")
class ChattingMFMConfig: ConfigData {
    var isEnabled: Boolean = true
}
