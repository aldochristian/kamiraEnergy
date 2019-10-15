package info.twentysixproject.kamiraen.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): ListenableWorker.Result {
        return ListenableWorker.Result.success()
    }

    companion object {
        private val TAG = "MyWorker"
    }
}