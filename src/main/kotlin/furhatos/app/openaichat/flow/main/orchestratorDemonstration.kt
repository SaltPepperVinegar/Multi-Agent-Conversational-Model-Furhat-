package furhatos.app.openaichat.flow

import com.theokanning.openai.completion.chat.ChatMessage
import furhatos.app.openaichat.flow.chatbot.getOpenAiResponse
import furhatos.app.openaichat.setting.activate
import furhatos.app.openaichat.setting.personas
import furhatos.flow.kotlin.*
import furhatos.records.Location
import kotlinx.coroutines.*
import furhatos.app.openaichat.setting.GESTURE_PROMPT
import furhatos.app.openaichat.setting.orchestratorPersona
import furhatos.app.openaichat.utils.splitByGestureTags

import furhatos.app.openaichat.flow.chatbot.getOpenAiResponseAsync
import furhatos.app.openaichat.setting.ORCHESTRATOR_PROMPT_SELECT
import furhatos.app.openaichat.setting.ORCHESTRATOR_PROMPT_SUMMARY



val OrchestratorDemonstration : State = state(Parent) {

        onEntry {
            reentry()
        }

    onReentry {
        println("listen")


        furhat.listen(endSil = 2000)
    }

    onResponse("exit", "quit") {
        goto(Greeting)
    }

    onResponse {

        // Build your parallel tasks (example: 3 variants/agents)
        val results: MutableList<String> = mutableListOf()
        getFurhatMessage().lastOrNull()?.let { last ->
            orchestratorPersona.context.add(last)
        }

        runBlocking {
            // fan-out + await all
            val questions: List<String> = withTimeoutOrNull(10000L) {
                coroutineScope {
                    personas.map { persona ->
                        async {
                            // call the per-task work; replace with your real call
                            getOpenAiResponseAsync(
                                    "You are ORCHESTRATOR_AGENT.\n" +
                                    "\n" +
                                    "[ROLE]\n" +
                                    "- Coordinate multiple personas.\n" +
                                    "- Address ONLY ${persona.name} (${persona.intro}).\n" +
                                    "- Instruct ${persona.name} how to respond to the user’s query or dialogue.\n" +
                                    "- Do not respond to the user directly.\n" +
                                    "\n" +
                                    "[CONSTRAINTS]\n" +
                                    "- Output exactly ONE sentence.\n" +
                                    "- Sentence must begin with: \"${persona.name}, as a [role/characteristic], ...\"\n" +
                                    "- No quotation marks, emojis, or meta-explanations.\n" +
                                    "- Do not use second-person (\"you\") when referring to the user.\n" +
                                    "\n" +
                                    "[OUTPUT FORMAT]\n" +
                                    "Return one instructive sentence directed at ${persona.name}.\n" +
                                    "\n" +
                                    "[EXAMPLE]\n" +
                                    "Input:\n" +
                                    "User: \"What is the probability of rolling a six on a die?\"\n" +
                                    "Persona: LogicAgent (precise and analytical)\n" +
                                    "\n" +
                                    "Output:\n" +
                                    "Maurice, as a precise and analytical agent, please respond to the user about the probability of rolling a six.\n" +
                                    "\"\"\"\n"
                                , orchestratorPersona.context)
                        }
                    }.awaitAll()
                }
            } ?: emptyList()

            personas.zip(questions).map { (persona, question) ->
                persona.context.add(ChatMessage("user",question))
            }
        }

        for (persona in personas) {
            activate(orchestratorPersona)
            furhat.attend(Location(1.0, 0.0, 2.0))
            furhat.say(persona.context.lastOrNull()?.content ?: "")
            furhat.attend(Location(-1.0, 0.0, 2.0))
            activate(persona)
            val result = getOpenAiResponse(persona.desc, persona.context)
            persona.context.add(ChatMessage("assistant",result))
            results.add(persona.name + result)
            furhat.say(result)
        }


        activate(orchestratorPersona)
        furhat.attend(users.current)
        val input = orchestratorPersona.context + results.map {
                result -> ChatMessage("user","candidate of the summary:" +result)
        }
        val summary = getOpenAiResponse(ORCHESTRATOR_PROMPT_SELECT + GESTURE_PROMPT , input, 0.5,75)

        println(summary)
        orchestratorPersona.context.add(ChatMessage("assistant", summary))
        print(orchestratorPersona.context)

        val segments = splitByGestureTags(summary)
        presentSpeech(segments)

        reentry()

    }


    onNoResponse {
        reentry()
    }

}




