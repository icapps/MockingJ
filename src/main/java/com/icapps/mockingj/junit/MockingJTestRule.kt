package com.icapps.mockingj.junit

import com.icapps.mockingj.MockingJServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * @author maartenvangiel
 * @version 1
 */
class MockingJTestRule(private val responseDirectory: String = DEFAULT_RESPONSE_DIRECTORY, private val mockAll: Boolean = true) : TestRule {

    companion object {
        const val DEFAULT_RESPONSE_DIRECTORY = "responses"
    }

    private var mockServer: MockingJServer? = null

    var baseUrl: String = ""
        private set

    override fun apply(base: Statement, description: Description): Statement {
        val builder = MockingJServer.Builder()

        val overrideMockResponseAnnotations = description.annotations.filter { it is MockResponses }.map { it as MockResponses }
        overrideMockResponseAnnotations.firstOrNull { it.overrideResponseDirectory.isNotEmpty() }?.let {
            builder.overrideResponsesDirectory(it.overrideResponseDirectory)
        }

        if (mockAll || description.annotations.any { it is MockResponses } || overrideMockResponseAnnotations.isNotEmpty()) {
            mockServer = builder
                    .responseDirectory(responseDirectory)
                    .build()
                    .apply {
                        baseUrl = start()
                    }
        }

        return object : Statement() {
            override fun evaluate() {
                try {
                    base.evaluate()
                } finally {
                    mockServer?.stop()
                }
            }
        }
    }

}