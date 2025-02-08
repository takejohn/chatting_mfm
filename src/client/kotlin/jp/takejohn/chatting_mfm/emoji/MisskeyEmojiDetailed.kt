package jp.takejohn.chatting_mfm.emoji

interface MisskeyEmojiDetailed {
    val id: String

    val aliases: List<String>

    val name: String

    val category: String?

    val host: String?

    val url: String

    val license: String?

    val isSensitive: Boolean

    val localOnly: Boolean

    val roleIdsThatCanBeUsedThisEmojiAsReaction: List<String>
}
