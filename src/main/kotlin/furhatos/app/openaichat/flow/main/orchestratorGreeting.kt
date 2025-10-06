package furhatos.app.openaichat.flow

import furhatos.app.openaichat.setting.Persona
import furhatos.app.openaichat.setting.activate
import furhatos.app.openaichat.setting.creativePersona
import furhatos.app.openaichat.setting.emotionalPersona
import furhatos.app.openaichat.setting.logicalPersona
import furhatos.app.openaichat.setting.personas
import furhatos.app.openaichat.setting.speakerPersona
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhatos.records.Location
import kotlinx.coroutines.Delay

val OrchestratorGreeting = state(Parent) {
    onEntry {
        furhat.attend(users.userClosestToPosition(Location(0.0, 0.0, 0.5)))
        furhat.gesture(Gestures.Nod)
        if (furhat.askYN("Would you like me to share my internal thought process with you? ")) {
            furhat.say("sure! I'll demonstrate my though process with you")
            delay((500))
            furhat.say("How Can I help you today?")
            goto(OrchestratorDemonstration)

        } else {
            furhat.say("Cool! Lets get straight into the conversation")
            delay((500))
            furhat.say("How Can I help you today?")

            goto(Orchestrator)
        }

    }
}
