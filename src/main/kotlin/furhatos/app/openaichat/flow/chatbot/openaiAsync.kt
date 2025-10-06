package furhatos.app.openaichat.flow.chatbot

import com.theokanning.openai.OpenAiService
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import furhatos.flow.kotlin.DialogHistory
import furhatos.flow.kotlin.Furhat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.String


suspend fun getOpenAiResponseAsync(
    system: String,
    messages: List<ChatMessage>,
    temperature: Double = 0.9,
    maxTokens: Int = 100,
    model: String = "gpt-4o-mini"
): String = withContext(Dispatchers.IO) {

    val service = OpenAiService(serviceKey)
    val prompt = (listOf(ChatMessage("system", system)) + messages) as List<ChatMessage>


    val chatReq = ChatCompletionRequest.builder()
        .model(model)
        .messages(prompt)
        .temperature(temperature)
        .maxTokens(maxTokens)
        .build()
    val chatResp = service.createChatCompletion(chatReq)
    chatResp.choices[0].message.content.trim()
}
