package jp.takejohn.chatting_mfm.emoji

import com.google.gson.Gson

data class MisskeyEmojiDetailedData(
    override val id: String,
    override val aliases: List<String>,
    override val name: String,
    override val category: String?,
    override val host: String?,
    override val url: String,
    override val license: String?,
    override val isSensitive: Boolean,
    override val localOnly: Boolean,
    override val roleIdsThatCanBeUsedThisEmojiAsReaction: List<String>,
): MisskeyEmojiDetailed {
    companion object {
        private val gson = Gson()

        fun fromJson(json: String): MisskeyEmojiDetailedData {
            return gson.fromJson<MisskeyEmojiDetailedData>(json, MisskeyEmojiDetailedData::class.java)
        }
    }
}
