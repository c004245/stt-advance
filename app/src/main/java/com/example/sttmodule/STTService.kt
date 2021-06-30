package com.example.sttmodule

import io.reactivex.Observable

class STTService(tokenValue: String, expirationTimeMillis: Long) {

    private var voiceRecorderCallback: VoiceRecorderCallback
//    private var voiceRecorder: VoiceRecorder

    private var speechService = SpeechService(tokenValue, expirationTimeMillis)

    private var recordwaveByteArray: ByteArray? = null

    init {
        voiceRecorderCallback = object: VoiceRecorderCallback() {
            override fun onVoiceStart(phrases: String) {
//                speechService.st
            }
        }
    }

    fun getSpeechResultObservable(): Observable<SpeechServiceResultModel> {
        return speechService.getSpeechResultObservable()
    }

    fun start(phrases: String = "") {
        voiceRecorderCallback.onVoiceStart(phrases)
    }
}