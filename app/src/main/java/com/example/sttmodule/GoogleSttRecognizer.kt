package com.example.sttmodule

import android.util.Log
import io.grpc.StatusRuntimeException
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.io.File

/**
 *
 */
class GoogleSttRecognizer(token: String, timestamp: Long,
    private val presenter: RecognizerContract.Presenter
): RecognizerContract.Recognizer {

    private val disposables = CompositeDisposable()

    private val sttService = STTService(token, timestamp)

    private var isPaused: Boolean = false
    private var isStarted: Boolean = false

    override fun startAudioRecordingAndrSttService(
        baseDir: File,
        uniqueId: String,
        loggingAction: (String, Int?, Any) -> Unit
    ) {
        sttService.getSpeechResultObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (text, maxCount, ttsConfidence, sttData) ->
//                Log.d("HWO", "subscribe Google stt")
                loggingAction(text, maxCount, sttData)
                if (!isPaused) presenter.updateREcognizedSpeech(text, maxCount)

                if (maxCount != null) {
                }
            }, {
                if (it is StatusRuntimeException) {
                    it.printStackTrace()
                    presenter.onRecognizerError(it.status)
                }
            }, {
                Log.d("HWO", "subscribe Google complete")

            }).addTo(disposables)

        sttService.start()
        isStarted = true
        }
    }