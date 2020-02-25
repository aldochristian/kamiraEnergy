package info.twentysixproject.kamiraen.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import info.twentysixproject.kamiraen.dataclass.Inbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Inbox::class], version = 1, exportSchema = false)
abstract class InboxRoomDatabase : RoomDatabase() {

    abstract fun inboxDao(): InboxDao

    companion object {
        @Volatile
        private var INSTANCE: InboxRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): InboxRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InboxRoomDatabase::class.java,
                    "kamira_database"
                )
                    .addCallback(InboxDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class InboxDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.inboxDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(inboxDao: InboxDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            inboxDao.deleteAll()

            //var messages = Inbox("NNN","No Cat", "00000", "00000", "Hellow message", "NN")
            //inboxDao.insert(messages)
            //var word = Word("Hello")
            //wordDao.insert(word)
            //word = Word("World!")
            //wordDao.insert(word)
        }
    }
}