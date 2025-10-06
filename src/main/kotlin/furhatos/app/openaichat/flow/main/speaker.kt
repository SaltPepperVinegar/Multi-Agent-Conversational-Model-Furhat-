package furhatos.app.openaichat.flow

import furhatos.app.openaichat.setting.Persona
import furhatos.app.openaichat.setting.activate
import furhatos.app.openaichat.setting.hostPersona
import furhatos.app.openaichat.setting.personas
import furhatos.app.openaichat.setting.speakerPersona
import furhatos.flow.kotlin.*
import furhatos.records.Location
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.OpenAiService
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import furhatos.app.openaichat.flow.chatbot.serviceKey
import com.google.gson.Gson
import furhatos.app.openaichat.flow.agents.CreativeAgent
import furhatos.app.openaichat.flow.agents.EmotionalAgent
import furhatos.app.openaichat.flow.agents.LogicalAgent
import furhatos.app.openaichat.flow.chatbot.getOpenAiResponse
import furhatos.app.openaichat.setting.SPEAKER_PROMPT
import furhatos.app.openaichat.setting.creativePersona
import furhatos.app.openaichat.setting.emotionalPersona
import furhatos.app.openaichat.setting.logicalPersona
import furhatos.app.openaichat.utils.Segment
import furhatos.app.openaichat.utils.toGesture
import furhatos.gestures.Gestures


val Speaker : State = state(Parent) {
    onEntry {
        activate(speakerPersona)
        switchPersona()
        reentry()
    }

    onReentry {
        furhat.listen(endSil = 2000)
    }

    onResponse(creativePersona.name) {
        goto(CreativeAgent)
    }

    onResponse(emotionalPersona.name) {
        goto(EmotionalAgent)
    }
    onResponse(logicalPersona.name) {
        goto(LogicalAgent)
    }

    onResponse("exit", "quit") {
        goto(Greeting)
    }

    onResponse {

        val history = getFurhatMessage()
        val response = call {
            getOpenAiResponse(SPEAKER_PROMPT, history, 0.0, 50)
        } as String
        println(response)
        try {
            val gson = Gson()
            val decision: SwitchDecision = gson.fromJson(response, SwitchDecision::class.java)
            println("Action = ${decision.action}, Agent = ${decision.agent}")
            when (decision.agent) {
                "logical_agent"  -> goto(LogicalAgent)
                "creative_agent" -> goto(CreativeAgent)
                "emotional_agent"-> goto(EmotionalAgent)
            }

        } catch (e: Exception) {
            println("Failed to parse JSON: $response")
            e.printStackTrace()
        }

        reentry()
    }




    onNoResponse {
        reentry()
    }


}

fun FlowControlRunner.switchPersona() {
    furhat.gesture(Gestures.Nod)


}


data class SwitchDecision(
    val action: String,
    val agent: String
)

