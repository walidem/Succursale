package cgodin.qc.ca.network

import okhttp3.Interceptor
import okhttp3.Response

class CustomDeleteInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.method == "POST" && request.header("X-HTTP-Method-Override") == "DELETE") {
            val newRequest = request.newBuilder()
                .method("DELETE", request.body)
                .removeHeader("X-HTTP-Method-Override")
                .build()
            request = newRequest
        }
        return chain.proceed(request)
    }
}
