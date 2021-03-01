package okhttp3.logging

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Dummy HttpLoggingInterceptor implementation for release build
 */
class HttpLoggingInterceptor : Interceptor {

    enum class Level {
    	BODY
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}
