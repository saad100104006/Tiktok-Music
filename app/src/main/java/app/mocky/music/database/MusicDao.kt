package app.mocky.music.database

import androidx.lifecycle.LiveData
import androidx.room.*
import app.mocky.music.modelclass.CurrentMusicDetails


@Dao
interface MusicDao {
    @Insert
     fun insert(obj: CurrentMusicDetails?)

    @Update
     fun update(obj: CurrentMusicDetails?)

    @Delete
     fun delete(obj: CurrentMusicDetails?)

    @Query("DELETE FROM CurrentMusicDetails")
     fun deleteCurrentMusicDetails()

    @Query("SELECT * FROM CurrentMusicDetails ORDER BY id DESC")
     fun currentMusicDetails(): LiveData<CurrentMusicDetails>?

    @Query("SELECT * FROM CurrentMusicDetails ORDER BY id DESC")
    fun currentMusicDetailsService(): CurrentMusicDetails


}