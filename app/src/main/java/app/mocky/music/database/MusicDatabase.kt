package app.mocky.music.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.mocky.music.modelclass.CurrentMusicDetails

@Database(
    entities = [CurrentMusicDetails::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {

    abstract fun musicDao(): MusicDao?

    companion object {
        @Volatile
        var instance: MusicDatabase? = null

        @Synchronized
        fun getInstance(context: Context): MusicDatabase? {
            val  tempInstance = instance
            if (tempInstance == null) {
//                synchronized(this) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music_db"
                )
                    .allowMainThreadQueries().fallbackToDestructiveMigration()
                    .addCallback(roomCallback).build()
//                }
                return instance
            }
            return instance
        }

        private val roomCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}