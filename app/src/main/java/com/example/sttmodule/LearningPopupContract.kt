package com.example.sttmodule

import io.grpc.Status
import java.io.File

interface RecognizerContract {
    interface Recognizer {
        fun startAudioRecordingAndrSttService(
            baseDir: File, uniqueId: String, loggingAction: (String, Int?, Any) -> Unit
        )
    }


    interface Presenter {
        fun updateREcognizedSpeech(result: String, maxCount: Int?)
        fun onRecognizerError(status: Status)
    }
}