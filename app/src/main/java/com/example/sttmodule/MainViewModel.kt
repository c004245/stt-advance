package com.example.sttmodule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sttmodule.RecognitionProgress.splitAsWords
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {

    private var currentPopupAnswerChecker: RecognitionChecker? = null

    private var checkingJob: Job? = null

    public fun setupNewChecker(answer: String) {
        val answerCnt = answer.splitAsWords().size
        val isCleared = AtomicBoolean(false)

        val checker = RecognitionChecker(answer, 0)

        currentPopupAnswerChecker = checker

        checkingJob = viewModelScope.launch {
            checker
                .responseChannel
                .consumeAsFlow()
                .collect {
                    Log.d("HWO", "FINIA")
                    if (!isCleared.get()) {
                        Log.d("HWO", "Resullt data -> ${it.finalizedWordCount} ---- ${it.recognizedWordCount}")
                    }

                    if (answerCnt <= it.recognizedWordCount && !isCleared.get()) {
                        isCleared.set(true)

                        Log.d("HWO", "Result OK")
                    }
                }
        }
    }


    fun checkCorrectWordsCount(transcriptArr: ArrayList<String>, maxCount: Int) {
        for (i in 0 until  transcriptArr.size) {
//            currentPopupAnswerChecker!!.checkCorrectWordsCount(transcriptArr[i], maxCount)
        }
    }
}