import com.icapps.mockingj.junit.MockResponses
import com.icapps.mockingj.junit.MockingJTestRule
import junit.framework.Assert.fail
import org.junit.Rule
import org.junit.Test

/**
 * @author maartenvangiel
 * @version 1
 */
class TestNoMockAll : BaseTest() {

    private lateinit var baseUrl: String

    @Rule
    @JvmField
    val mockingJRule = MockingJTestRule(mockAll = false)

    override fun setup() {
        super.setup()
        baseUrl = mockingJRule.baseUrl
    }

    @Test
    fun testNoAnnotation() {
        assert(baseUrl.isEmpty())
        try {
            getResponse("${baseUrl}users/1")
            fail("Expected a failure: MockingJ should not be running")
        } catch (ex: Exception) {
        }
    }

    @Test
    @MockResponses
    fun testWithAnnotation() {
        assert(!baseUrl.isEmpty())
        val body = getResponse("${baseUrl}users/1")
        assert(body.contains("Van Giel GET"))
    }

    @Test
    @MockResponses("responses-overridden")
    fun testWithAnnotationWithOverride() {
        val url = "${baseUrl}users/1"
        val body = getResponse(url)
        assert(body.contains("Vermeulen GET"))
        assert(!body.contains("Van Giel"))
    }

}