# MockingJ
A simple wrapper around Square's [MockWebserver](https://github.com/square/okhttp/tree/master/mockwebserver) that allows you to mock API responses by placing JSON files in a certain file structure.

## Quick start
Place your json files in your `resources` folder under `/responses` in the following structure:
```
src/test/resources/responses -- users.json
                             \_ users -- GET_1.json
                                      \_ GET_2.json
                                      \_ GET_*.json
```

The file name corresponds to the HTTP method used and the request URI. The
filenames support wildcards. Non-wildcards and more specific filenames have
priority over wildcards.

e.g. `GET http://example.com/users/3` will match with the file
`responses/users/GET_*.JSON` but `GET http://example.com/users/2` will match
`GET_2.json` because it has fewer wildcards.

Starting the mock server is easy:
```kotlin
val mockServer = MockingJServer()
val url = mockServer.start()
```

## Example use case
An example use case for MockingJ is simulating network traffic to an API in unit tests. A simple test setup could look like this:

```kotlin
object TestEnvironment {
    var baseUrl: String? = null
}
```

```kotlin
@RunWith(JUnitRunner::class)
abstract class BaseNetworkMockedTest {

    private lateinit var mockServer: MockingJServer

    @Before
    @CallSuper
    open fun setup() {
        mockServer = MockingJServer()
        TestEnvironment.baseUrl = mockServer.start()
    }

    @After
    @CallSuper
    open fun tearDown() {
        mockServer.stop()
    }

}
```

You can then use `TestEnvironment.baseUrl` as baseURL for your API calls. A
retrofit example with dagger2 could look like this:

```kotlin
@Module
class TestModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(TestEnvironment.baseUrl ?: throw IllegalStateException("No valid baseURL available in testing environment"))
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideExampleService(retrofit: Retrofit): ExampleService {
        return retrofit.create(ExampleService::class.java)
    }

}
```

You can now inject your service wherever you want in your tests. The ExampleService will now work because it uses the baseUrl we have set in our `BaseNetworkMockedTest`!
