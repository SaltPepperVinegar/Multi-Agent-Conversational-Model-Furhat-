package furhatos.app.openaichat.flow.chatbot

import com.google.gson.Gson
import com.theokanning.openai.OpenAiService
import com.theokanning.openai.completion.CompletionRequest
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import furhatos.app.openaichat.flow.getFurhatHistory
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat
import kotlinx.coroutines.flow.merge

val serviceKey = ""
fun getOpenAiResponse(system: String, messages : List<ChatMessage>, temperature : Double = 0.9, maxTokens : Int = 100): String {
    val service = OpenAiService(serviceKey)
    val prompt = (listOf(ChatMessage("system", system)) + messages) as List<ChatMessage>


    val chatReq = ChatCompletionRequest.builder()
        .model("gpt-4o-mini")
        .messages(prompt)
        .temperature(temperature)
        .maxTokens(maxTokens)
        .build()
    val chatResp = service.createChatCompletion(chatReq)
    val out = chatResp.choices[0].message.content.trim()
    return out
}

