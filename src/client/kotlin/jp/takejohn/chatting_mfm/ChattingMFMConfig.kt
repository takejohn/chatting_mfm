package jp.takejohn.chatting_mfm

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config

val CONFIG: ChattingMFMConfig by lazy { AutoConfig.getConfigHolder(ChattingMFMConfig::class.java).config }

/**
 * Chatting MFMの設定
 */
@Config(name = "chattingmfm")
class ChattingMFMConfig: ConfigData {
    /**
     * このModを有効にする
     */
    var isEnabled: Boolean = true

    /**
     * カスタム絵文字を取得するMisskeyサーバー
     */
    var emojiMisskeyServer: String = ""
}
