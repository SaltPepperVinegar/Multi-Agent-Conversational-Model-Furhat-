package furhatos.app.openaichat.flow

import furhatos.app.openaichat.flow.chatbot.serviceKey
import furhatos.app.openaichat.setting.activate
import furhatos.app.openaichat.setting.distanceToEngage
import furhatos.app.openaichat.setting.hostPersona
import furhatos.app.openaichat.setting.maxNumberOfUsers
import furhatos.app.openaichat.setting.speakerPersona
import furhatos.flow.kotlin.State
import furhatos.flow.kotlin.furhat
import furhatos.flow.kotlin.state
import furhatos.flow.kotlin.users

val Init: State = state() {
    init {
        /** Set our default interaction parameters */
        users.setSimpleEngagementPolicy(distanceToEngage, maxNumberOfUsers)

        /** Check API key for the OpenAI GPT3 language model has been set */
        if (serviceKey.isEmpty()) {
            println("Missing API key for OpenAI language model. ")
            exit()
        }

        /** Set the Persona */
        activate(hostPersona)
        /** start the interaction */
        goto(InitFlow)
    }

}

val InitFlow: State = state() {
    onEntry {
        furhat.say("Hello! I am Furhat, a social robot excited of the focus group")
        delay((200))
        when {
            users.hasAny() -> goto(Greeting)
            !users.hasAny() -> goto(Idle)
        }
    }

}


