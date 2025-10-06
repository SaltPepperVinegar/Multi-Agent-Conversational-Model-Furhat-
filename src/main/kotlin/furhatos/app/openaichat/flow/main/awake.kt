package furhatos.app.openaichat.flow.main

import furhatos.app.openaichat.flow.Greeting
import furhatos.app.openaichat.flow.OrchestratorGreeting
import furhatos.app.openaichat.flow.Parent
import furhatos.app.openaichat.flow.SpeakerGreeting
import furhatos.app.openaichat.setting.orchestratorPersona
import furhatos.app.openaichat.setting.personas
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.onResponse
import furhatos.flow.kotlin.onUserLeave
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users

val Awake = state(Parent) {

    onEntry {
        furhat.say(
            "Welcome! Say Hi! Furhat if you want to start the conversation."
        )


        furhat.listen(endSil = 2000)

    }


    onResponse("Hi Furhat") {

        goto(Greeting)

    }

}