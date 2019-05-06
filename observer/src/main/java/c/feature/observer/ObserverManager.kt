package c.feature.observer

import android.util.Log
import java.util.*

interface TaskListener {
    fun registerComplete()
    fun unregisterComplete()
    fun onReceived(msg: Any) {
        Log.e("onReceive: ", msg.toString())
    }
}

object ObserverManager {
    lateinit var notifyCacheMessage: NotifyCacheMessage

    fun setNotifyCacheMessage(n: (String) -> Unit) {
        notifyCacheMessage = object : NotifyCacheMessage {
            override fun notifyCacheMessage(key: String) {
                n(key)
            }
        }
    }

    val obsCache = HashMap<String, ArrayList<TaskListener>>()

    fun register(key: String, target: TaskListener) {
        var taskListeners = obsCache[key]
        if (taskListeners == null) {
            taskListeners = ArrayList<TaskListener>()
            obsCache[key] = taskListeners
        }
        taskListeners.add(target)
        target.registerComplete()
        notifyCacheMessage(key)
    }

    private fun notifyCacheMessage(key: String) {
        notifyCacheMessage.notifyCacheMessage(key)
    }

    fun unregister(key: String, target: TaskListener) {
        val taskListeners = obsCache[key]
        if (taskListeners != null) {
            taskListeners.remove(target)
            target.unregisterComplete()
        }
    }
}

interface NotifyCacheMessage {
    fun notifyCacheMessage(key: String)
}