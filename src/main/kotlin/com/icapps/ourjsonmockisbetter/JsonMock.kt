package com.icapps.ourjsonmockisbetter

import okhttp3.mockwebserver.MockWebServer

class JsonMock {

    private val server = MockWebServer()
    private val dispatcher = JsonMockDispatcher()

    fun start(): String {
        server.setDispatcher(dispatcher)
        server.start()
        return server.url("").toString()
    }

    fun stop() {
        server.close()
    }

}