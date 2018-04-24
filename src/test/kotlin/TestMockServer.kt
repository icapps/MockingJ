import com.icapps.ourjsonmockisbetter.JsonMock
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class TestMockServer {

    private lateinit var server: JsonMock
    private lateinit var okHttp: OkHttpClient
    private lateinit var baseUrl: String

    @Before
    fun setup() {
        server = JsonMock()
        okHttp = OkHttpClient()
        baseUrl = server.start()
    }

    @After
    fun tearDown() {
        server.stop()
    }

    @Test
    fun testUrlMatching() {
        val url1 = "${baseUrl}users"
        val url2 = "${baseUrl}users/1"
        val url3 = "${baseUrl}users/2.json"

        fun getResponse(url: String): String {
            val response = okHttp.newCall(Request.Builder()
                    .url(url)
                    .get()
                    .build()).execute()

            val body = response.body()?.string()
            if (body == null) {
                fail()
                throw IllegalStateException("No body received for url $url")
            } else {
                return body
            }
        }

        val body = getResponse(url1)
        assert(body.contains("Van Giel"))
        assert(body.contains("Van Looveren"))

        val body2 = getResponse(url2)
        assert(body2.contains("Van Giel"))

        val body3 = getResponse(url3)
        assert(body3.contains("Verbeeck"))
    }

    @Test
    fun testMethodMatching() {
        val url = "${baseUrl}users/1"

        fun getResponse(url: String, method: String, requestBody: RequestBody? = null): String {
            val response = okHttp.newCall(Request.Builder()
                    .url(url)
                    .method(method, requestBody)
                    .build()).execute()

            val body = response.body()?.string()
            if (body == null) {
                fail()
                throw IllegalStateException("No body received for url $url")
            } else {
                return body
            }
        }

        val getBody = getResponse(url, "GET")
        assert(getBody.contains("Van Giel GET"))

        val postBody = getResponse(url, "POST", RequestBody.create(null, "content"))
        assert(postBody.contains("Van Giel POST"))
    }

}