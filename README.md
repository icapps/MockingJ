# MockingJ
[![Release](https://jitpack.io/v/maartenvang/MockingJ.svg)](https://jitpack.io/#maartenvang/MockingJ)

A wrapper around Square's [MockWebserver](https://github.com/square/okhttp/tree/master/mockwebserver) that allows you to mock API responses by placing JSON files in a certain file structure. This will actually spawn a http server, which allows you to test your application without having to mock your network layer. Just use the baseUrl provided by the `MockingJServer` (or `MockingJTestRule`) instead of your normal base URL.

MockingJ comes with a JUnit TestRule (`MockingJTestRule`) and a `@MockResponses` annotation which allows for easy setup in your unit tests.

## Quick start
Add the MockingJ dependency:

```
allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

```
dependencies {
            compile 'com.github.maartenvang:MockingJ:0.0.1-alpha'
    }
```

Place your API responses as json files in your `resources` folder under `/responses` in the following structure:
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

Each file should contain the desired response in the following format:
```javascript
{
  "responseCode": 200,
  "responseHeaders": {
    ...
  },
  "responseBody": {
    ...
  }
}
```

`responseCode` is required, `responseHeaders` and `responseBody` are optional. `responseBody` can either be a JSON object or an array.

Starting the mock server is easy:
```kotlin
val mockServer = MockingJServer()
val url = mockServer.start()
```

## Test example
A simple test setup could look like this:

```kotlin
@RunWith(JUnitRunner::class)
abstract class BaseNetworkMockedTest {

    @Rule
    @JvmField
    val rule = MockingJTestRule(mockAll = false) // if mockAll == true (default), responses will be mocked for all tests

    @Test
    @MockResponses()
    fun testExample() {
        // Responses from /src/test/resources/responses will be used
    }

    @Test
    @MockResponses(overrideResponseDirectory = "responses-overridden")
    fun testAnotherExample() {
        /* Responses from /src/test/resources/responses-overridden will be used,
           if no response was found responses from /src/test/resources/responses will be used as fallback */
    }

}
```

You can then use `MockingJ.baseUrl` as baseURL for your API calls. A
retrofit example with dagger2 could look like this:

```kotlin
@Module
class TestModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(MockingJ.baseUrl ?: throw IllegalStateException("No valid baseURL available in testing environment"))
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