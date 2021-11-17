package app.mocky.music.modelclass

import java.io.Serializable


data class MusicModelClass(
    val audioPath: String,
    val creator: Creator,
    val dateCreated: String,
    val shortID: String,
    val title: String
): Serializable