package com.example.sttmodule

import android.util.Log
import com.google.auth.Credentials
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import io.grpc.*
import io.grpc.internal.DnsNameResolverProvider
import io.grpc.okhttp.OkHttpChannelProvider
import io.grpc.stub.StreamObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import kotlin.math.roundToInt

class SpeechService(
    tokenValue: String,
    expirationTimeMillis: Long
) {
    companion object {
        val SCOPE = listOf("https://www.googleapis.com/auth/cloud-platform")

        const val HOSTNAME = "speech.googleapis.com"
        const val PORT = 443

        const val NUM_MAX_ALTERNATIVES = 5
        const val LANGUAGE_CODE = "en-US"

        const val SAMPLE_RATE = 16000
    }

    private val api: SpeechGrpc.SpeechStub

    private val speechResultSource = PublishSubject.create<SpeechServiceResultModel>()

    private var requestObserver: StreamObserver<StreamingRecognizeRequest>? = null
    init {

        val accessToken = AccessToken(tokenValue, Date(expirationTimeMillis))

        val channel = OkHttpChannelProvider()
            .builderForAddress(HOSTNAME, PORT)
            .nameResolverFactory(DnsNameResolverProvider())
            .intercept(
                GoogleCredentialsInterceptor(
                    GoogleCredentials(accessToken).createScoped(
                        SCOPE
                    )
                )
            ).build()

        api = SpeechGrpc.newStub(channel)
    }

    fun getSpeechResultObservable(): Observable<SpeechServiceResultModel> {
        return speechResultSource
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun startRecognizing(phrases: String) {
        val speechContext = SpeechContext.newBuilder()
            .addPhrases(phrases)
            .build()

        requestObserver = api.streamingRecognize(ResponseObserver(speechResultSource))
            requestObserver?.onNext(
                StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(
                        StreamingRecognitionConfig.newBuilder()
                            .setConfig(
                                RecognitionConfig.newBuilder()
                                    .setLanguageCode(LANGUAGE_CODE)
                                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                    .setSampleRateHertz(SAMPLE_RATE)
                                    .setEnableWordTimeOffsets(true)
                                    .setMaxAlternatives(NUM_MAX_ALTERNATIVES)
                                    .addSpeechContexts(speechContext)
                                    .build()
                            )
                            .setInterimResults(true)
                            .setSingleUtterance(false)
                            .build()
                    )
                    .build()
            )
    }

}

class ResponseObserver(private val speechResultSOurce: PublishSubject<SpeechServiceResultModel>):
    StreamObserver<StreamingRecognizeResponse> {

    override fun onNext(response: StreamingRecognizeResponse?) {
        Log.d("HWO", "Response -> $response")
        response?.let {
            if (it.resultsCount > 0) {
                    val result = it.getResults(0)
                    val isFinal = result.isFinal

                    if (result.alternativesCount >0 ){
                        val alternative = result.getAlternatives(0)
                        val text = alternative.transcript
                        val confidence = (alternative.confidence * 100).roundToInt()

                        val maxCount = result.alternativesList.maxOf { it.wordsCount }

                        speechResultSOurce.onNext(
                            SpeechServiceResultModel(
                                text,
                                if (isFinal) maxCount else null,
                                confidence,
                                result
                            )
                        )
                }
            }
        }
    }

    override fun onError(t: Throwable?) {
        if (t == null) return
        speechResultSOurce.onError(t)
        t.printStackTrace()
    }

    override fun onCompleted() {

    }

    }
private class GoogleCredentialsInterceptor constructor(private val mCredentials: Credentials) :
    ClientInterceptor {

    private var mCached: Metadata? = null
    private var mLastMetadata: Map<String, List<String>>? = null

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {

        return object : ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT>(
            next.newCall(method, callOptions)
        ) {
            @Throws(StatusException::class)
            override fun checkedStart(
                responseListener: ClientCall.Listener<RespT>,
                headers: Metadata
            ) {
                val cachedSaved: Metadata?
                val uri = serviceUri(next, method)
                synchronized(this) {
                    val latestMetadata = getRequestMetadata(uri)
                    if (mLastMetadata == null || mLastMetadata !== latestMetadata) {
                        mLastMetadata = latestMetadata
                        mCached = toHeaders(mLastMetadata)
                    }
                    cachedSaved = mCached
                }
                headers.merge(cachedSaved!!)
                delegate().start(responseListener, headers)
            }
        }

    }


    /**
     * Generate a JWT-specific service URI. The URI is simply an identifier with enough
     * information for a service to know that the JWT was intended for it. The URI will
     * commonly be verified with a simple string equality check.
     */
    @Throws(StatusException::class)
    private fun serviceUri(channel: Channel, method: MethodDescriptor<*, *>): URI {
        val authority = channel.authority() ?: throw Status.UNAUTHENTICATED
            .withDescription("Channel has no authority")
            .asException()
// Always use HTTPS, by definition.
        val scheme = "https"
        val defaultPort = 443
        val path = "/" + MethodDescriptor.extractFullServiceName(method.fullMethodName)!!
        var uri: URI
        try {
            uri = URI(scheme, authority, path, null, null)
        } catch (e: URISyntaxException) {
            throw Status.UNAUTHENTICATED
                .withDescription("Unable to construct service URI for auth")
                .withCause(e).asException()
        }

        // The default port must not be present. Alternative ports should be present.
        if (uri.port == defaultPort) {
            uri = removePort(uri)
        }
        return uri
    }

    @Throws(StatusException::class)
    private fun removePort(uri: URI): URI {
        try {
            return URI(
                uri.scheme, uri.userInfo, uri.host, -1 /* port */,
                uri.path, uri.query, uri.fragment
            )
        } catch (e: URISyntaxException) {
            throw Status.UNAUTHENTICATED
                .withDescription("Unable to construct service URI after removing port")
                .withCause(e).asException()
        }

    }

    @Throws(StatusException::class)
    private fun getRequestMetadata(uri: URI): Map<String, List<String>> {
        try {
            return mCredentials.getRequestMetadata(uri)
        } catch (e: IOException) {
            throw Status.UNAUTHENTICATED.withCause(e).asException()
        }
    }

    private fun toHeaders(metadata: Map<String, List<String>>?): Metadata {
        val headers = Metadata()
        if (metadata != null) {
            for (key in metadata.keys) {
                val headerKey = Metadata.Key.of(
                    key, Metadata.ASCII_STRING_MARSHALLER
                )
                for (value in metadata[key]!!) {
                    headers.put(headerKey, value)
                }
            }
        }
        return headers
    }
}


data class SpeechServiceResultModel(
    val text: String,
    val maxCount: Int?,
    val confidence: Int,
    val sttData: Any
)