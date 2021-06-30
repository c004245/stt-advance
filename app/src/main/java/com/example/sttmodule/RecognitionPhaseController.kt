package com.example.sttmodule

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope

class RecognitionPhaseController(
    private val scope: LifecycleCoroutineScope,
    private val type: Type
): RecognizerPhaseController.Controller {

    sealed class Type {
        class GOOGLE_STT(val context: Context, val token: String, val timeStamp: Long): Type()
    }

//    private var recognitionProgress

    override fun updateRecognizedSpeech(result: String) {
//        recogni
    }

    override fun onRecognizerError() {

    }


}

interface RecognizerPhaseController {

    interface Controller {
        fun updateRecognizedSpeech(result: String)
        fun onRecognizerError()
    }
}