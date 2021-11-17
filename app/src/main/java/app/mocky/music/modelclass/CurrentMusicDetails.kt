package app.mocky.music.modelclass

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "CurrentMusicDetails")
data class CurrentMusicDetails(
    var totalTime: String,
    var currentTime: String,
    var speed: Float,
    var seekTime: String,
    var title: String,
    var date: String,
    var seekToStatus: String,
    var playStatus: String,
    var downloadStatus: String
): Serializable {
    @PrimaryKey(autoGenerate = true)var id: Int? = null
}