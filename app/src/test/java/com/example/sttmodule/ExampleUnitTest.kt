package com.example.sttmodule

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sttmodule.RecognitionProgress.splitAsWords
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

data class Person(
    val name: String,
    val age: Int
)


//@RunWith(RobolectricTestRunner::class)
    class SttTest {

    lateinit var context: Context

//    @Before
//    fun setup() {


//        context = ApplicationProvider.getApplicationContext()
//
//        val assetManager = context.resources.assets
//        val inputStream = assetManager.open("json/sttData.json")
//        val jsonString = inputStream.bufferedReader().use { it.readText() }
//
//        val jObject = JSONObject(jsonString)
//
//        Log.d("HWO", "jOb-> $jObject")
//
//    val transcript = listOf(
//        "I've",
//        "I feel",
//        "I fear",
//        "I",
//        "I fear",
//        "I fear you",
//        "I fear your",
//        "like",
//        "lie",
//        "line",
//        "line"
//    )
//



    val transcript = ArrayList<String>()
//    val answer = "First, breakfast, and then sweets!"

//    val answer = "Six, seven, eight, nine, ten!\t"
//    val answer = "That’s the reason."
    val answer = "teacher"
    val answerWords: List<Word> = answer.splitAsWords()


    val jsonString = """
        [{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"PJ","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":380738390}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":155938079},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"picture","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1416140662}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1768655165},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"PJ","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":380738390}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":155938079},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"pictures","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-948762667}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1594911250},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"picture of","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-176630007}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-410743370},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"pictures","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-948762667}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1594911250},{"alternatives_":[{"bitField0_":0,"confidence_":0.71859455,"transcript_":"pager","words_":[{"endTime_":{"nanos_":900000000,"seconds_":0,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":455866223},"startTime_":{"nanos_":0,"seconds_":0,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":506479},"word_":"pager","memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":67762408}],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1800069735},{"bitField0_":0,"confidence_":0.68143755,"transcript_":"picture","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-264249165},{"bitField0_":0,"confidence_":0.8351208,"transcript_":"pictures","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1144144769},{"bitField0_":0,"confidence_":0.84796524,"transcript_":"pigeon","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1383224098}],"bitField0_":0,"isFinal_":true,"stability_":0.0,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":674776428}]

    """.trimIndent()

    val jobject = JSONArray(jsonString)


//    val jArray = jobject("alternatives_")

    @Before
    fun setup() {
        for (i in 0 until jobject.length() -1) {
            val result = jobject.getJSONObject(i)

            val alternatives = result.getJSONArray("alternatives_")

            for (j in 0 until  alternatives.length()) {
                val obj = alternatives.getJSONObject(j)
                transcript.add(obj.getString("transcript_"))
            }

        }
    }

//    )
//    val transcript = listOf(
//        "Forest",
//        "Forest",
//        "nearest breakfast",
//        "nearest breakfast",
//        "Forest breakfast and",
//        "Forest breakfast and then",
//        "Forest breakfast and then stop",
//        "Forest breakfast and",
//        "Forest breakfast and then",
//        "Forest breakfast and",
//        "Forest breakfast and then street",
//        "tourist breakfast and then Street",
//        "Forest breakfast and then sweet",
//        "Forrest breakfast and then street",
//        "poorest breakfast and then Street",
//        "Forest",
//        "Forest",
//        "nearest breakfast",
//        "nearest breakfast",
//        "Forest breakfast and",
//        "Forest breakfast and then",
//        "Forest breakfast and then stop",
//        "Forest breakfast and",
//        "Forest breakfast and then",
//        "Forest breakfast and",
//        "Forest breakfast and then Street",
//        "tourist breakfast and then Street",
//        "Forest breakfast and then Street",
//        "Forrest breakfast and then Street",
//        "poorest breakfast and then Street",
//
//        )
    //정답 기준
    /**
     * 쉬움
     * 인식된 데이터 = answer 완전일치
     * 인식된 데이터 char 0번째 값 = answer 값 char 0번째 값 일치
     * charEasySet + charDefaultSet (예외 허용 메소드)
     * 마지막 단어는 answer , 인식된 데이터 char이 3개 이상 맞을 경우 정답 (ex. answer: apple, transcript : application 요런식일 경우에도 정답처리)
    */
    @Test
    fun `level1 test`() {
        Log.d("HWO", "answer split-> $answerWords")
        val recognitionChecker = RecognitionChecker(answer, 0)
       recognitionChecker.checkCorrectWordsCounts(transcript, 99)

//        assertTrue(Word("I").isWordMatch(Word("I've"), false, 0))
    }

    /**
     * 보통
     *인식된 데이터 = answer 완전 일치
     * 인식된 데이터 char 0번째 값 = answer 값 char 0번째 값 일치
     * charDefaultSet (예외 허용)
     * 마지막 단어는 answer , 인식된 데이터 char이 3개 이상 맞을 경우 정답
     */
    @Test
    fun `level2 test`() {
        val recognitionChecker = RecognitionChecker(answer, 1)
        recognitionChecker.checkCorrectWordsCounts(transcript, 99)
//        assertTrue(Word("I").isWordMatch(Word("I've"), false, 1))
    }

    /**
     * 어려움
     * 인식된 데이터 = answer 완전 일치
     * 인식된 데이터 char 0번째 값 = answer 값 char 0번째 값 일치
     * 마지막 단어는 answer , 인식된 데이터 char이 3개 이상 맞을 경우 정답
     */
    @Test
    fun `level3 test`() {
        val recognitionChecker = RecognitionChecker(answer, 2)
        recognitionChecker.checkCorrectWordsCounts(transcript, 99)
//        assertTrue(Word("I").isWordMatch(Word("I've"), false, 2))

    }
    @Test
    fun `main test`() {
        val recognitionChecker = RecognitionChecker(answer, 0)
//        recognitionChecker.checkCorrectWordsCount(transcript.get(0), 99)
        //Read json data
        val answer = "teacher"

        val transcript = listOf(
            "I've",
            "I feel",
            "I fear",
            "I",
            "I fear",
            "I fear you",
            "I fear your",
            "like",
            "lie",
            "line",
            "line"
        )

        val answerCnt = answer.splitAsWords().size
        val isCleared = AtomicBoolean(false)

        runBlocking {
            val job = launch {
                recognitionChecker.responseChannel
                    .consumeAsFlow()
                    .collect {
                        if (answerCnt <= it.recognizedWordCount && !isCleared.get()) {
                            Log.d(
                                "HWO",
                                "Resullt data -> ${it.finalizedWordCount} ---- ${it.recognizedWordCount}"
                            )
                        }

                        if (answerCnt <= it.recognizedWordCount && !isCleared.get()) {
                            isCleared.set(true)

                            Log.d("HWO", "Result OK")
                        }
                    }
            }


            for (element in transcript) {
                Log.d("HWO", "element state -> $element")
//                recognitionChecker.checkCorrectWordsCount(element, 99)
                delay(200)

            }
            job.cancel()
        }
    }
        }



