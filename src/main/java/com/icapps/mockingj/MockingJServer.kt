package com.icapps.mockingj

import okhttp3.mockwebserver.MockWebServer

class MockingJServer {

    private val server = MockWebServer()
    private val dispatcher = MockWebserverDispatcher()

    fun start(): String {
        server.setDispatcher(dispatcher)
        server.start()
        return server.url("").toString()
    }

    fun stop() {
        server.close()
    }

}