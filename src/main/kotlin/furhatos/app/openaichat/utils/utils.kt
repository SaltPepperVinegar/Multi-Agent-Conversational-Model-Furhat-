package furhatos.app.openaichat.flow

import com.theokanning.openai.completion.chat.ChatMessage
import furhatos.app.openaichat.utils.Segment
import furhatos.app.openaichat.utils.toGesture
import furhatos.flow.kotlin.*
import furhatos.gestures.ARKitParams
import furhatos.gestures.BasicParams
import furhatos.gestures.defineGesture
import kotlin.random.Random


fun FlowControlRunner.askForAnything(text: String) {

    call(state {
        onEntry {
            furhat.ask(text)
        }
        onResponse {
            terminate()
        }
    })

}

val random = Random(0)

fun GazeAversion(duration: Double = 1.0) = defineGesture("GazeAversion") {
    val dur = duration.coerceAtLeast(0.2)
    frame(0.05, dur-0.05) {
        when (random.nextInt(4)) {
            0 -> {
                ARKitParams.EYE_LOOK_DOWN_LEFT to 0.5
                ARKitParams.EYE_LOOK_DOWN_RIGHT to 0.5
                ARKitParams.EYE_LOOK_OUT_LEFT to 0.5
                ARKitParams.EYE_LOOK_IN_RIGHT to 0.5
            }
            1 -> {
                ARKitParams.EYE_LOOK_UP_LEFT to 0.5
                ARKitParams.EYE_LOOK_UP_RIGHT to 0.5
                ARKitParams.EYE_LOOK_OUT_LEFT to 0.5
                ARKitParams.EYE_LOOK_IN_RIGHT to 0.5
            }
            2 -> {
                ARKitParams.EYE_LOOK_DOWN_LEFT to 0.5
                ARKitParams.EYE_LOOK_DOWN_RIGHT to 0.5
                ARKitParams.EYE_LOOK_OUT_RIGHT to 0.5
                ARKitParams.EYE_LOOK_IN_LEFT to 0.5
            }
            else -> {
                ARKitParams.EYE_LOOK_UP_LEFT to 0.5
                ARKitParams.EYE_LOOK_UP_RIGHT to 0.5
                ARKitParams.EYE_LOOK_OUT_RIGHT to 0.5
                ARKitParams.EYE_LOOK_IN_LEFT to 0.5
            }
        }
    }
    reset(dur)
}
fun getFurhatHistory(n : Int  = 50) : String {
    return Furhat.dialogHistory.all.takeLast(n).mapNotNull {
        when (it) {
            is DialogHistory.ResponseItem -> {
                "user ${it.response.text}"
            }
            is DialogHistory.UtteranceItem -> {
                "assistant: ${it.toText()}"
            }
            else -> null
        }
    }.joinToString(separator = "\n")
}
fun getUserMessage(n : Int  = 50) : List<ChatMessage> {
    val history = Furhat.dialogHistory.all.takeLast(n).mapNotNull {
        when (it) {
            is DialogHistory.ResponseItem -> {
                ChatMessage("user", it.response.text)
            }
            else -> null
        }
    } as List<ChatMessage>

    return history
}

fun getFurhatMessage(n : Int  = 10) : List<ChatMessage> {
    val history = Furhat.dialogHistory.all.takeLast(n).mapNotNull {
        when (it) {
            is DialogHistory.ResponseItem -> {
                ChatMessage("user", it.response.text)
            }
            is DialogHistory.UtteranceItem -> {
                ChatMessage("assistant", it.toText())
            }
            else -> null
        }
    } as List<ChatMessage>

    return history
}


fun FlowControlRunner.presentSpeech(segments : List<Segment>) {
    for (segment in segments){
        val g = toGesture(segment.gesture.name)
        furhat.gesture(g)
        furhat.say(segment.sentence)

    }

}
