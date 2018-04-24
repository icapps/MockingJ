package com.icapps.ourjsonmockisbetter

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

class JsonMockDispatcher : Dispatcher() {

    override fun dispatch(request: RecordedRequest): MockResponse {
        val resourceUri = JsonMockDispatcher::class.java.getResource(getResourceUri(request))?.toURI()
                ?: return MockResponse().setResponseCode(404)
        val path = Paths.get(resourceUri)

        if (Files.exists(path)) {
            val fileContents = Files.readAllBytes(path)

            return MockResponse()
                    .setResponseCode(200)
                    .setBody(fileContents.toString(Charset.forName("UTF-8")))
        }

        return MockResponse().setResponseCode(404)
    }

    private fun getResourceUri(request: RecordedRequest): String {
        val resultFilename = request.path.replaceLast("/", "/${request.method}_")
        return "$resultFilename.json"
    }

    private fun String.replaceLast(last: String, replacement: String): String {
        val indexOfLast = lastIndexOf(last)
        if (indexOfLast < 0) return this
        return replaceRange(indexOfLast, indexOfLast + last.length, replacement)
    }

}