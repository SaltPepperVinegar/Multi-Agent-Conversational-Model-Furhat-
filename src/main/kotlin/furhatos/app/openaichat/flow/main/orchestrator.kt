package furhatos.app.openaichat.flow

import com.theokanning.openai.completion.chat.ChatMessage
import furhatos.app.openaichat.setting.personas
import furhatos.flow.kotlin.*
import kotlinx.coroutines.*
import furhatos.app.openaichat.setting.GESTURE_PROMPT
import furhatos.app.openaichat.setting.orchestratorPersona
import furhatos.app.openaichat.utils.splitByGestureTags

import furhatos.app.openaichat.flow.chatbot.getOpenAiResponseAsync
import furhatos.app.openaichat.setting.ORCHESTRATOR_PROMPT_SELECT
import furhatos.app.openaichat.setting.ORCHESTRATOR_PROMPT_SUMMARY

val Orchestrator : State = state(Parent) {

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


        var summary : String = ""
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
            val results: List<String> = withTimeoutOrNull(10000L) {
                coroutineScope {
                    personas.map { persona ->
                        async {
                            // call the per-task work; replace with your real call
                            getOpenAiResponseAsync( persona.desc, persona.context)
                        }
                    }.awaitAll()     // <- waits for *all* tasks here
                }
            } ?: emptyList()

            personas.zip(results).map { (persona, results) ->
                persona.context.add(ChatMessage("assistant",results))
            }

            println(results)
            val input = orchestratorPersona.context + results.map {
                result -> ChatMessage("user","candidate of the summary :" +result)
            }

            summary = getOpenAiResponseAsync(ORCHESTRATOR_PROMPT_SELECT + GESTURE_PROMPT , input, 0.5,75)
        }
        println(summary)
        val segments = splitByGestureTags(summary)

        presentSpeech(segments)
        orchestratorPersona.context.add(ChatMessage("assistant", summary))

        print(orchestratorPersona.context)
        reentry()

    }


    onNoResponse {
        reentry()
    }

}




