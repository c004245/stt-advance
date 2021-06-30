package com.example.sttmodule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import kotlinx.coroutines.Job
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private var checkingJob: Job? = null

    private var  transcriptArr = ArrayList<String>()

    private val TAG = MainActivity::class.java.toString()

    var maxCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val assetManager = resources.assets
        val inputStream= assetManager.open("json/sttData.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

//        Log.d("HWO", "jsonString -> $jsonString")
        val jObject = JSONObject(jsonString)

        val answer = jObject.getString("answer")
        val jArray = jObject.getJSONArray("sttData")

        for (i in 0 until jArray.length() -1) {
            val obj = jArray.getJSONObject(i)
            val title = obj.getJSONArray("alternatives_")
//            Log.d("HWO",  "title($i): $title")

            for (j in 0 until  title.length()) {
                val obj2 = title.getJSONObject(j)
                val transcript = obj2.getString("transcript_")
                val word = obj2.getJSONArray("words_")

                for (t in 0 until word.length()) {
                    val obj3 = word.getJSONObject(t)

                    maxCount = word.length()
                }
                transcriptArr.add(transcript)
            }

        }


        //answer
        mainViewModel.setupNewChecker(answer)

        mainViewModel.checkCorrectWordsCount(transcriptArr, maxCount)
    }


}