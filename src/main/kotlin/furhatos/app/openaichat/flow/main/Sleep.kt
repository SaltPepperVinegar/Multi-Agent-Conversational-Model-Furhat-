package furhatos.app.openaichat.flow.main

import furhatos.app.openaichat.flow.Idle
import furhatos.flow.kotlin.*
import kotlinx.coroutines.*

var idleJob: Job? = null

fun FlowControlRunner.resetIdleTimer() {
    idleJob?.cancel()
    idleJob = GlobalScope.launch {
        delay(60000) // 60 seconds
        print("sleep after idling ")
        goto(Idle)
    }
}

fun FlowControlRunner.stopIdleTimer() {
    idleJob?.cancel()
    idleJob = null
}