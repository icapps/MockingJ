package com.icapps.mockingj

import okhttp3.mockwebserver.MockWebServer

class MockingJServer private constructor(responseDirectory: String, overrideResponseDirectory: String?) {

    class Builder {

        companion object {
            const val DEFAULT_RESPONSE_DIRECTORY = "responses"
        }

        private var responseDirectory: String = DEFAULT_RESPONSE_DIRECTORY
        private var overrideResponseDirectory: String? = null

        /**
         * @param responseDirectory The directory in the resources folder in which the responses will be fetched
         */
        fun responseDirectory(responseDirectory: String): Builder {
            this.responseDirectory = responseDirectory
            return this
        }

        fun overrideResponsesDirectory(responseDirectory: String?): Builder {
            this.overrideResponseDirectory = responseDirectory
            return this
        }

        fun build(): MockingJServer {
            return MockingJServer(responseDirectory, overrideResponseDirectory)
        }
    }

    private val server = MockWebServer()
    private val dispatcher = MockWebserverDispatcher(responseDirectory, overrideResponseDirectory)

    fun start(): String {
        server.setDispatcher(dispatcher)
        server.start()
        return server.url("").toString()
    }

    fun stop() {
        server.close()
    }

}