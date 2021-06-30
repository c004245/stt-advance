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
    val answer = "She’s going to another school"
    val answerWords: List<Word> = answer.splitAsWords()


    val jsonString = """
        [{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"can you","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1334082905}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-142956100},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"can you go","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":534035753}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-848761514},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"can you call","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-428402201}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1320077316},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"can you grow","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1714773096}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":231916409},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"is it going","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1946018787}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1929944120},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":"can you cry","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1180380311}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-947622250},{"alternatives_":[{"bitField0_":0,"confidence_":0.9052766,"transcript_":"can you cry","words_":[{"endTime_":{"nanos_":800000000,"seconds_":1,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-549163672},"startTime_":{"nanos_":400000000,"seconds_":1,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-274327192},"word_":"can","memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-867127057},{"endTime_":{"nanos_":900000000,"seconds_":1,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":455869032},"startTime_":{"nanos_":800000000,"seconds_":1,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-549163672},"word_":"you","memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1800164938},{"endTime_":{"nanos_":300000000,"seconds_":2,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1279357087},"startTime_":{"nanos_":900000000,"seconds_":1,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":455869032},"word_":"cry","memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":519977602}],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1925908735},{"bitField0_":0,"confidence_":0.8867174,"transcript_":"LaCroix","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-13693577},{"bitField0_":0,"confidence_":0.90909094,"transcript_":"Keyshia Cole","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":127550528},{"bitField0_":0,"confidence_":0.90909094,"transcript_":"physically","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1983462063},{"bitField0_":0,"confidence_":0.90909094,"transcript_":"Nikolai","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-710253300}],"bitField0_":0,"isFinal_":true,"stability_":0.0,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1226969786},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":" have","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":623520396}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1632117795},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":" have a","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1878502745}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1002710982},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":" heaven","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1894643747}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1155020024},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":" have Laura","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":740877647}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1763620796},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":" heaven","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1894643747}],"bitField0_":0,"isFinal_":false,"stability_":0.01,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":-1155020024},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":" heaven","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1894643747}],"bitField0_":0,"isFinal_":false,"stability_":0.9,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1743747348},{"alternatives_":[{"bitField0_":0,"confidence_":0.0,"transcript_":" heaven","words_":[],"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1894643747}],"bitField0_":0,"isFinal_":false,"stability_":0.9,"memoizedSerializedSize":-1,"unknownFields":{"count":0,"isMutable":false,"memoizedSerializedSize":-1,"objects":[],"tags":[]},"memoizedHashCode":1743747348}]
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
        val answer = "ey think you're lying!"

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



