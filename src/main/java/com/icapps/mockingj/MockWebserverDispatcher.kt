package com.icapps.mockingj

import com.google.gson.Gson
import okhttp3.Headers
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
import java.util.logging.Logger
import java.util.regex.Pattern


class MockWebserverDispatcher(private val responseDirectory: String, private val overrideResponseDirectory: String?) : Dispatcher() {

    private val gson = Gson()

    private val responses = FileUtils.listFileTree(File("src/test/resources/$responseDirectory"))
            .mapNotNull { parseMockResponse(it, isOverride = false) }
            .sortedWith(compareBy({ it.fileName.count { it == '*' } }, { Int.MAX_VALUE - it.fileName.length })) // Least amount of wildcards and more specific matches go first

    private val overrideResponses = if (overrideResponseDirectory == null)
        null
    else
        FileUtils.listFileTree(File("src/test/resources/$overrideResponseDirectory"))
                .mapNotNull { parseMockResponse(it, isOverride = true) }
                .sortedWith(compareBy({ it.fileName.count { it == '*' } }, { Int.MAX_VALUE - it.fileName.length })) // Least amount of wildcards and more specific matches go first


    override fun dispatch(request: RecordedRequest): MockResponse {
        val fileName = getFilenameForRequest(request)
        val matchedResponse = findResponseFileForFilename(overrideResponses, fileName) ?: findResponseFileForFilename(responses, fileName)

        return if (matchedResponse == null) {
            MockResponse()
                    .setResponseCode(404)
        } else {
            Logger.getLogger(MockWebserverDispatcher::class.java.simpleName).info("Matched request $fileName with response ${matchedResponse.fileName}")

            try {
                matchedResponse.toMockResponse()
            } catch (ex: Exception) {
                MockResponse()
                        .setResponseCode(500)
                        .setBody(ex.toString())
            }
        }
    }

    private fun getFilenameForRequest(request: RecordedRequest): String {
        val resultFilename = request.path.replaceLast("/", "/${request.method}_")
        return "$resultFilename.json"
    }

    private fun findResponseFileForFilename(responses: Collection<MockingJResponse>?, uri: String): MockingJResponse? {
        return responses?.firstOrNull {
            val pattern = "(.*)${it.fileName.escapeForRegex()}".toRegex()
            uri.matches(pattern)
        }
    }

    private fun String.escapeForRegex(): String {
        return this.split("*").joinToString(separator = "(.*)") { Pattern.quote(it) }
    }

    private fun String.replaceLast(last: String, replacement: String): String {
        val indexOfLast = lastIndexOf(last)
        if (indexOfLast < 0) return this
        return replaceRange(indexOfLast, indexOfLast + last.length, replacement)
    }

    private fun parseMockResponse(file: File, isOverride: Boolean): MockingJResponse? {
        val fileName = file.path.substringAfter(if (isOverride) "/$overrideResponseDirectory" else "/$responseDirectory")
        return gson.fromJson(file.reader(), MockingJResponse::class.java).apply {
            this.fileName = fileName
            this.isOverride = isOverride
        }
    }

}

private fun MockingJResponse.toMockResponse(): MockResponse {
    val response = MockResponse()
    response.setResponseCode(responseCode)

    responseHeaders?.let {
        response.setHeaders(Headers.of(it))
    }
    responseBody?.let {
        response.setBody(it.toString())
    }

    return response
}
