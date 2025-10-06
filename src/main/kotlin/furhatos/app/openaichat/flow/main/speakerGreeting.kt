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

val SpeakerGreeting = state(Parent) {
    onEntry {
        furhat.attend(users.userClosestToPosition(Location(0.0, 0.0, 0.5)))
        furhat.gesture(Gestures.Nod)
        furhat.say("Hi!, I'm your multi agent assistant. Before we start, I'll give a introduction to each of the persona in me")
            delay((200))

            furhat.say("This is Emma, our creativity figure. ready to generate ideas.")
            delay((200))

            activate(creativePersona)
            delay((200))


            furhat.say("Hi! I'm emma ")
            delay((400))

            activate(speakerPersona)

            furhat.say("This is Jane, she’s here for emotional support. Ask her anything.")
            delay((200))

            activate(emotionalPersona)
            delay((200))
            furhat.say("Hi! I'm Jane ")
            delay((400))

            activate(speakerPersona)

            furhat.say("Meet Maurice, our reasoning expert. you can ask him about anything")
            delay((200))

            activate(logicalPersona)
            delay((200))

            furhat.say("Hi! I'm Maurice ")
            delay((400))

            activate(speakerPersona)


        furhat.say("What can I help you today? ")

        goto(Speaker)

    }
}
