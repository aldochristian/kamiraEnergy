package info.twentysixproject.kamiraen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import info.twentysixproject.kamiraen.database.InboxRepository
import info.twentysixproject.kamiraen.database.InboxRoomDatabase
import info.twentysixproject.kamiraen.dataclass.Inbox
import kotlinx.coroutines.launch

class InboxViewModel (application: Application) : AndroidViewModel(application){
    private val repository: InboxRepository
    val allMessages: LiveData<List<Inbox>>

    init {
        val inboxDao = InboxRoomDatabase.getDatabase(application, viewModelScope).inboxDao()
        repository = InboxRepository(inboxDao)
        allMessages = repository.allMessages
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on the mainthread, blocking
     * the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called viewModelScope which we
     * can use here.
     */
    fun insert(inbox: Inbox) = viewModelScope.launch {
        repository.insert(inbox)
    }
}