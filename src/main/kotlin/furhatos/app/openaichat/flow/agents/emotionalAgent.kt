package furhatos.app.openaichat.flow.agents

import furhatos.app.openaichat.flow.Greeting
import furhatos.app.openaichat.flow.Parent
import furhatos.app.openaichat.flow.Speaker
import furhatos.app.openaichat.flow.chatbot.getOpenAiResponse
import furhatos.app.openaichat.flow.getFurhatMessage
import furhatos.app.openaichat.flow.presentSpeech
import furhatos.app.openaichat.flow.switchPersona
import furhatos.app.openaichat.setting.GESTURE_PROMPT
import furhatos.app.openaichat.setting.activate
import furhatos.app.openaichat.setting.creativePersona
import furhatos.app.openaichat.setting.emotionalPersona
import furhatos.app.openaichat.utils.splitByGestureTags
import furhatos.app.openaichat.utils.toGesture
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.gestures.Gestures
import furhatos.nlu.common.Greeting

val EmotionalAgent = state(Parent) {
    val persona = emotionalPersona
    onEntry {
        furhat.say {
            random {
                +"${persona.name}, your turn."
                +"${persona.name}, mind jumping in?"
                +"All yours, ${persona.name}."
            }
        }
        delay(500)

        activate(persona)
        switchPersona()
        furhat.say{
            random {
                +"${persona.name} here."
                +"${persona.name}, speaking."
                +"Alright, ${persona.name}."
            }
        }
        val prompt = persona.desc + GESTURE_PROMPT
        val history = getFurhatMessage()

        val response = call {
            getOpenAiResponse(prompt, history)
        } as String
        println(response)

        val segments = splitByGestureTags(response)

        presentSpeech(segments)
        delay(500)
        goto(Speaker)
    }

}
