package com.icapps.mockingj

import com.google.gson.JsonElement

data class MockingJResponse(val responseCode: Int,
                            val responseBody: JsonElement? = null,
                            val responseHeaders: Map<String, String>? = null,
                            var fileName: String)