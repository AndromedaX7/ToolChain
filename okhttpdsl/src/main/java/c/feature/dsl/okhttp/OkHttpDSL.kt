package c.feature.dsl.okhttp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException

class OkHttpDSL{

    private val okHttpClient: OkHttpClient = OkHttpClient()
    val requestData: RequestData = RequestData()
    lateinit var request: Request
    operator fun invoke(block: OkHttpDSL.() -> Unit) {
        block();
    }

    private fun parsed() {
        val mediaType: MediaType? = MediaType.parse(requestData.mimeType.values())!!
        val body: RequestBody = RequestBody.create(mediaType, requestData.body)
        val builder: Request.Builder = Request.Builder()


        when (requestData.method) {

            Method.GET ->
                builder.get().url(requestData.uri + "?${requestData.body}")

            Method.POST ->
                builder.post(body).url(requestData.uri)


        }

        builder.addHeader("content-type", requestData.mimeType.values())
        for (i in requestData.headers)
            builder.addHeader(i.key, i.value)
        request = builder.build()

    }

    fun callString(function: suspend (String) -> Unit, exception: (Throwable) -> Unit) {
        parsed()
        request.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = okHttpClient.newCall(it).execute()
                    response.body()?.let { body ->
                        function(body.string())
                    }

                } catch (e: IOException) {
                    exception(e)
                    e.printStackTrace()
                }

            }
        }
    }

    fun callBytes(function: suspend (ByteArray) -> Unit, exception: (Throwable) -> Unit) {
        parsed()
        request.let {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = okHttpClient.newCall(it).execute()
                    response.body()?.let { body ->
                        function(body.bytes())
                    }
                } catch (e: IOException) {
                    exception(e)
                    e.printStackTrace()
                }

            }

        }
    }

}

class RequestData {
    lateinit var uri: String
    lateinit var method: Method
    var mimeType: MimeType = MimeType.APPLICATION_JSON
    var headers = ArrayList<Header>()
    var body: String = ""
    operator fun invoke(block: RequestData.() -> Unit) {
        block()
    }
}

enum class MimeType(private val value: String) {
    APPLICATION_X_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_JSON("application/json"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_XML("application/xml"),
    TEXT_XML("text/xml"),
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain");

    fun values(): String {
        return value
    }
}

data class Header(var key: String, var value: String)

enum class Method(private val methodValue: String) {
    GET("GET"),
    POST("POST");

    fun getValue(): String {
        return methodValue
    }

}
