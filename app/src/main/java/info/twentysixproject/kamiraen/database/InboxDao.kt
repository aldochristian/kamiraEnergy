package info.twentysixproject.kamiraen.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import info.twentysixproject.kamiraen.dataclass.Inbox

@Dao
interface InboxDao {
    @Query("SELECT * from inbox_table")
    fun getMessages(): LiveData<List<Inbox>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(inbox: Inbox)

    @Query("DELETE FROM inbox_table")
    suspend fun deleteAll()
}