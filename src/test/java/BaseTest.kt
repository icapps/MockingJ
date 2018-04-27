import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.Assert
import org.junit.Before

/**
 * @author maartenvangiel
 * @version 1
 */
open class BaseTest {

    protected lateinit var okHttp: OkHttpClient

    @Before
    open fun setup() {
        okHttp = OkHttpClient()
    }

    protected fun getResponse(url: String): String {
        val response = okHttp.newCall(Request.Builder()
                .url(url)
                .get()
                .build()).execute()

        val body = response.body()?.string()
        if (!response.isSuccessful || body == null) {
            Assert.fail("No body received or not successful for url $url")
            throw IllegalStateException()
        } else {
            return body
        }
    }

    fun getResponse(url: String, method: String, requestBody: RequestBody? = null): String {
        val response = okHttp.newCall(Request.Builder()
                .url(url)
                .method(method, requestBody)
                .build()).execute()

        val body = response.body()?.string()
        if (body == null) {
            Assert.fail()
            throw IllegalStateException("No body received for url $url")
        } else {
            return body
        }
    }

}