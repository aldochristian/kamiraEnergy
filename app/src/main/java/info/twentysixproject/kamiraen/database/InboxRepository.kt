package info.twentysixproject.kamiraen.database

import androidx.lifecycle.LiveData
import info.twentysixproject.kamiraen.dataclass.Inbox

class InboxRepository (private val inboxDao: InboxDao){
    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allMessages: LiveData<List<Inbox>> = inboxDao.getMessages()

    suspend fun insert(inbox: Inbox) {
        inboxDao.insert(inbox)
    }
}