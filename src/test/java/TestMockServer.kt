
import com.icapps.mockingj.junit.MockResponses
import com.icapps.mockingj.junit.MockingJTestRule
import okhttp3.RequestBody
import org.junit.Rule
import org.junit.Test

class TestMockServer : BaseTest() {

    private lateinit var baseUrl: String

    @Rule
    @JvmField
    val mockingJRule = MockingJTestRule()

    override fun setup() {
        super.setup()
        baseUrl = mockingJRule.baseUrl
    }

    @Test
    fun testUrlMatching() {
        val url1 = "${baseUrl}users"
        val url2 = "${baseUrl}users/1"
        val url3 = "${baseUrl}users/2.json"
        val url4 = "${baseUrl}users/1?query=param"
        val url5 = "${baseUrl}users/3"
        val url6 = "${baseUrl}users/3?query=param"

        val body = getResponse(url1)
        assert(body.contains("Van Giel"))
        assert(body.contains("Van Looveren"))

        val body2 = getResponse(url2)
        assert(body2.contains("Van Giel"))
        assert(!body2.contains("Van Looveren"))

        val body3 = getResponse(url3)
        assert(body3.contains("Verbeeck"))

        val body4 = getResponse(url4)
        assert(body4.contains("Van Giel"))
        assert(!body4.contains("Van Looveren"))

        val body5 = getResponse(url5)
        val body6 = getResponse(url6)
        assert(body5.contains("Verbeeck"))
        assert(!body5.contains("Van Giel"))
        assert(body5 == body6)
    }

    @Test
    fun testMethodMatching() {
        val url = "${baseUrl}users/1"

        val getBody = getResponse(url, "GET")
        assert(getBody.contains("Van Giel GET"))

        val postBody = getResponse(url, "POST", RequestBody.create(null, "content"))
        assert(postBody.contains("Van Giel POST"))

        val deleteBody = getResponse(url, "DELETE")
        assert(deleteBody.contains("Van Giel DELETE"))
    }

    @Test
    fun testWildcards() {
        val url1 = "${baseUrl}users/1"
        val url2 = "${baseUrl}users/3"
        val url3 = "${baseUrl}users/3?query=param&lol=lol"

        val body1 = getResponse(url1)
        assert(body1.contains("Van Giel GET"))

        val body2 = getResponse(url2)
        assert(body2.contains("Verbeeck GET"))
        assert(!body2.contains("Van Giel GET"))

        val body3 = getResponse(url3)
        assert(body3.contains("Verbeeck GET"))
        assert(!body2.contains("Van Giel GET"))
    }

    @Test
    @MockResponses("responses-overridden")
    fun testOverride() {
        val url = "${baseUrl}users/1"
        val body = getResponse(url)
        assert(body.contains("Vermeulen GET"))
        assert(!body.contains("Van Giel"))
    }

}