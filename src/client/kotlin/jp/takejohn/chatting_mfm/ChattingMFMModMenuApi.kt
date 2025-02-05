package jp.takejohn.chatting_mfm

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.autoconfig.AutoConfig

class ChattingMFMModMenuApi: ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent ->
            AutoConfig.getConfigScreen(ChattingMFMConfig::class.java, parent).get()
        }
    }
}
