package neilyich.correction.codes.channel

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import neilyich.correction.codes.core.Code
import neilyich.correction.codes.core.words.Word
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class RestClient(
    private val uri: URI,
    private val objectMapper: ObjectMapper
): Client {
    private val client = HttpClient.newHttpClient()
    override fun <C, WordC: Word<C>> send(word: Word<C>, code: Code<*, C, *, WordC>, url: String?): Word<C> {
        val requestUri = if (url == null) uri else URI.create(url)
        val request = HttpRequest.newBuilder(requestUri)
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(WordInfo(word, code))))
            .header("content-type", "application/json").build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return objectMapper.readValue(response.body())
    }
}