package com.example.sttmodule

import android.util.Log
import com.example.sttmodule.RecognitionProgress.splitAsWords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import kotlin.math.min


object RegexUtils {
    val wordReg = Regex("""[A-Za-z’'.?!,]+""")
}
data class Word(val word: String) {

    fun isWordMatch(answer: Word, isFinal: Boolean, level: Int): Boolean {

        //Easy
        val charEasySets: List<Pair<Char, Char>> = listOf(
            Pair('a', 'b'),
            Pair('c', 'b'),
            Pair('s', 'b'),
            Pair('p', 'b'),
            Pair('y', 'b'),

            Pair('n', 'h'),
            Pair('c', 'h'),
            Pair('y', 'h'),
            Pair('i', 'h'),

            Pair('d', 'l'),
            Pair('n', 'l'),
            Pair('t', 'l'),

            Pair('a', 's'),
            Pair('b', 's'),
            Pair('c', 's'),
            Pair('h', 's'),

            Pair('r', 'w'),

            Pair('w', 'p'),
            Pair('h', 'y'),
            Pair('m', 'y'),

            //add reverse
            Pair('b', 'a'),
            Pair('b', 'c'),
            Pair('b', 's'),
            Pair('b', 'p'),
            Pair('b', 'y'),

            Pair('h', 'n'),
            Pair('h', 'c'),
            Pair('h', 'y'),
            Pair('h', 'i'),

            Pair('l', 'd'),
            Pair('l', 'n'),
            Pair('l', 't'),

            Pair('s', 'a'),
            Pair('s', 'b'),
            Pair('s', 'c'),
            Pair('s', 'h'),

            Pair('w', 'r'),

            Pair('p', 'w'),
            Pair('y', 'h'),
            Pair('y', 'm'),
        )



        //Normal
        val charDefaultSets: List<Pair<Char, Char>> = listOf(
            Pair('b', 'a'), // bath ~fail
            Pair('o', 'a'), // again all
            Pair('e', 'a'), // again all
            Pair('u', 'a'), // again all

            Pair('f', 'b'),
            Pair('m', 'b'),
            Pair('v', 'b'),
            Pair('o', 'b'),
            Pair('t', 'b'),
            Pair('h', 'b'),

            Pair('t', 'c'),
            Pair('h', 'c'),

            Pair('p', 'd'),
            Pair('h', 'd'),

            Pair('a', 'e'),
            Pair('h', 'e'),
            Pair('i', 'e'), //i

            Pair('p', 'f'),
            Pair('s', 'f'),
            Pair('b', 'f'), // bath ~fail

            Pair('c' ,'t'),
            Pair('c', 'g'), // go
            Pair('k', 'g'),
            Pair('t', 'g'),

            Pair('a', 'h'),
            Pair('p', 'h'), // hot / pop

            Pair('e', 'i'),
            Pair('h', 'i'),
            Pair('l', 'i'),

            Pair('t', 'j'),
            Pair('s', 'j'),

            Pair('n', 'k'), // know
            Pair('m', 'k'), // know

            Pair('r', 'l'),
            Pair('b', 'l'),
            Pair('k', 'l'),

            Pair('b', 'm'),

            Pair('l', 'n'), // not
            Pair('k', 'n'),
            Pair('a', 'n'), //an
            Pair('p', 'n'), //an

            Pair('a', 'o'),
            Pair('e', 'o'),
            Pair('i', 'o'),
            Pair('u', 'o'),

            Pair('m', 'p'),
            Pair('f', 'p'), // fruit 잘 안됨
            Pair('b', 'p'), //be

            Pair('l', 'r'),
            Pair('b', 'r'),
            Pair('w', 'r'),
            Pair('o', 'r'),
            Pair('t', 'r'),
            Pair('n', 'r'),

            Pair('f', 's'),

            Pair('d', 't'),
            Pair('b', 't'),
            Pair('i', 't'),

            Pair('a', 'u'), //an
            Pair('i', 'u'),
            Pair('w', 'u'),

            Pair('b', 'v'),
            Pair('p', 'v'),
            Pair('d', 'v'),

            Pair('a', 'w'),
            Pair('n', 'w'),
            Pair('b', 'w'),
            Pair('h', 'w'),
            Pair('l', 'w'),

            Pair('d', 'y'),
            Pair('i', 'y'),

            Pair('g', 'z'),

            //Add reverse
            Pair('a', 'b'),

            Pair('a', 'o'),
            Pair('a', 'e'),
            Pair('a', 'o'),

            Pair('b', 'f'),
            Pair('b', 'm'),
            Pair('b', 'v'),
            Pair('b', 'o'),
            Pair('b', 't'),
            Pair('b', 'h'),

            Pair('c', 't'),
            Pair('c', 'h'),

            Pair('d', 'p'),
            Pair('d', 'h'),

            Pair('e', 'a'),
            Pair('e', 'h'),
            Pair('e', 'i'),

            Pair('f', 'p'),
            Pair('f', 's'),
            Pair('f', 'b'),

            Pair('t', 'c'),

            Pair('g', 'c'),
            Pair('g', 'k'),
            Pair('g', 't'),

            Pair('h', 'a'),
            Pair('h', 'p'),

            Pair('i', 'e'),
            Pair('i', 'h'),
            Pair('i', 'l'),

            Pair('j', 't'),
            Pair('j', 's'),

            Pair('k', 'n'),
            Pair('k', 'm'),

            Pair('l', 'r'),
            Pair('l', 'b'),
            Pair('l', 'k'),

            Pair('m', 'b'),

            Pair('n', 'l'),
            Pair('n', 'k'),
            Pair('n', 'a'),
            Pair('n', 'p'),

            Pair('o', 'a'),
            Pair('o', 'e'),
            Pair('o', 'i'),
            Pair('o', 'u'),

            Pair('p', 'm'),
            Pair('p', 'f'),
            Pair('p', 'b'),

            Pair('r', 'l'),
            Pair('r', 'b'),
            Pair('r', 'w'),
            Pair('r', 'o'),
            Pair('r', 't'),
            Pair('r', 'n'),

            Pair('s', 'f'),

            Pair('t', 'd'),
            Pair('t', 'b'),
            Pair('t', 'i'),

            Pair('u', 'a'),
            Pair('u', 'i'),
            Pair('u', 'w'),

            Pair('v', 'b'),
            Pair('v', 'p'),
            Pair('v', 'd'),

            Pair('w', 'a'),
            Pair('w', 'n'),
            Pair('w', 'b'),
            Pair('w', 'h'),
            Pair('w', 'l'),

            Pair('y', 'd'),
            Pair('y', 'i'),

            Pair('z', 'g'),

            //picture, teach
        )

        val charSets: List<Pair<Char, Char>> = when(level) {
            0 -> charEasySets + charDefaultSets
            1 -> if (isFinal) charEasySets + charDefaultSets else charDefaultSets
            2 -> listOf()
            else -> throw IllegalStateException("Invalid level")
        }

        val wordSets: List<Pair<String, String>> = listOf()

        val i = word.filter { it.isLetter() }.toLowerCase()
        val a = answer.word.filter { it.isLetter() }.toLowerCase()
        Log.d("HWO", "============ try compared Transcript -> $i ==== answer  -> $a")


        /**
         * Answer와 들어온 Word 값이 같거나,
         * 첫번째 Word 가 같거나
         * WordSets은 쓰지않음
         *
         */

        //i -> can  (c)
        return i == a ||
                i.getOrNull(0) == a.getOrNull(0) ||
                charSets
                    .filter {
//                        Log.d("HWO", "======= try charset -> ${it.first}")
                        //Transcript 에 0번째값 == Pair에 First값이 일치하는 데이터
                        it.first == i.getOrNull(0)
                    }
                    .any {
                        Log.d("HWO", "===== charset -> ${it.first} --- ${it.second}")
                        //t == b h s g
                        //ten = can
                        // c -->
                        val isMatch = a.getOrNull(0) == it.second

                        isMatch
                    } ||
                wordSets
                    .filter {
                        it.first == i
                    }
                    .any {
                        val isMatch = try {
                            a.slice(it.second.indices) == it.second
                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                            false
                        }

                        isMatch
                    } ||
                (isFinal && {
                    var match = false

                    for (startIndex in i.toList().indices) {
                        Log.d("HWO", "i indis --> $startIndex ---- $i")
                        if (!match) {
                            var lastMatchedIndex = startIndex

                            var matchCnt = 0
                            i.forEachIndexed { inputIndex, char ->
                                Log.d("HWO", "====== i change -> $startIndex ---- $inputIndex")
                                if (startIndex > inputIndex) return@forEachIndexed

                                Log.d("HWO", "char state -> $char ---- $a")
                                val matchedIndex = a.indexOf(char, lastMatchedIndex)

                                Log.d("HWO", "match index -> $matchedIndex")

                                if (matchedIndex != -1)  {
                                    lastMatchedIndex = matchedIndex
                                    matchCnt++
                                }
                            }
                            if (matchCnt > 2) {
                                match = true
                            }
                        }
                    }
                    match
                }())

    }
}

object RecognitionProgress {
    fun String.splitAsWords(): List<Word> {
        val reg = RegexUtils.wordReg
        return reg.findAll(this).map { Word(it.value) }.toList()
    }
}


class RecognitionChecker(answer: String, private val difficultyLevel: Int) {
    var isCurrent = false
    var successCnt = 0
    var failCnt = 0

    private val answerCnt = answer.splitAsWords().size
    private val answerWords: List<Word> = answer.splitAsWords()

    private val newWords = mutableListOf<Word>()

    private val leftAnswers: List<Word>?
        get() = answerWords.subList(correctCnt, answerWords.size)

    data class ResponseInfo(
        val finalizedWordCount: Int,
        val recognizedWordCount: Int,
        val isFinal: Boolean
    )

    val responseChannel = Channel<ResponseInfo>(2)
    private var correctCnt = 0

    private val recognizedWordList: MutableList<String> = mutableListOf()


    private var lastRecognizedWords = listOf<Word>()
    private var accumulatedMaxCount: Int = 0

    fun getRecognizedWordList(): List<String> {
        val list = recognizedWordList.toList()
        recognizedWordList.removeAll { true }
        return list
    }

    fun getLeftAnswer(): List<Word>? {
        return leftAnswers
    }


    //return added sets
    private fun filterNewWords(words: List<Word>): List<Word> {
        val itList = listOf("it", "is", "it's")
        val filterLastRecognizedWords =
            words.filterIndexed { index, word ->
                val lastWord = lastRecognizedWords.getOrNull(index)
                word != lastWord
            }

        val newWords = filterLastRecognizedWords.filterIndexed { index, newWord ->
            val oldWord = newWords.getOrNull(index)
            oldWord != newWord ||
                    !(itList.contains(oldWord.word) && itList.contains(newWord.word))
        }

        this.newWords.clear()
        this.newWords.addAll(newWords)
        return newWords
    }

    private fun changeNumeric(words: String): List<Word> {
        var strNumeric = Word("")
        when (words) {
            "0" -> {
                strNumeric = Word("zero")
            }
            "1" -> {
                strNumeric = Word("one")
            }
            "2" -> {
                strNumeric = Word("two")
            }
            "3" -> {
                strNumeric = Word("three")
            }
            "4" -> {
                strNumeric = Word("four")
            }
            "5"-> {
                strNumeric = Word("five")
            }
            "6" -> {
                strNumeric = Word("six")
            }
            "7" -> {
                strNumeric = Word("seven")
            }
            "8" -> {
                strNumeric = Word("eight")
            }
            "9" -> {
                strNumeric = Word("nine")
            }
            "10" -> {
                strNumeric = Word("ten")
            }
            "11" -> {
                strNumeric = Word("eleven")
            }
        }
        return listOf(strNumeric)

    }
    fun checkCorrectWordsCounts(inputSentences: List<String>, maxCount: Int?) {

        Log.d("HWO", "transcript List -> $inputSentences")
//        for (inputSentence in inputSentences) {
//        for (i in inputSentences.indices) {
        var index1 = 0
        lateinit var newWords: List<Word>

        inputSentences.forEachIndexed { index, s ->
            val transcript = inputSentences[index1]
            Log.d("HWO", "========= inputSentences -> ${transcript}")
            newWords = if (transcript.toIntOrNull() != null) {
                val integer = transcript.toIntOrNull()
                changeNumeric(integer.toString().first().toString())
            } else {
                filterNewWords(transcript.splitAsWords())
            }
            Log.d("HWO", "========= and try filterNewWord transcript -> ${leftAnswers}")
            recognizedWordList.addAll(transcript.splitAsWords().map { it.word })

            if (maxCount != null) {
                accumulatedMaxCount = if (accumulatedMaxCount + maxCount > answerWords.size) {
                    answerWords.size
                } else {
                    accumulatedMaxCount + maxCount
                }
            }

            val cnt = run loop@{ // count matching inputs
                leftAnswers?.foldIndexed(0) { answerIndex, acc, answer ->


                    Log.d("HWO", "answerIndex111 ======  $answer")
//                    Log.d("HWO", "acc ====== $acc")
//                    Log.d("HWO", "answer ====== $answer")
//                    val checkRange = 0..4 - difficultyLevel
//                    Log.d("HWO", "checkRange -> $checkRange")
                    val isFinal = maxCount != null
                    var isMatcher = false

                    run loop2@ {
                        newWords.forEachIndexed { index, word ->
                            val newWord = newWords.getOrNull(index)
                            val isMatch = if (maxCount != null) {
                                newWord != null && answerIndex > -1 && newWord.isWordMatch(
                                    answer, false, difficultyLevel
                                )
                            } else {
                                newWord != null && answerIndex > -1 && newWord.isWordMatch(
                                    answer, false, difficultyLevel
                                )
                            }
                            Log.d("HWO", "answerIndex222 -> $isMatch")

                            if (isMatch) {
                                Log.d("HWO", "Match !")
                                isMatcher = true
                                return@loop2

                            }


                        }
                    }


                    Log.d("HWO", "isMatcher -> $isMatcher")
                    if (!isMatcher) {
//                        if (newWords != null) {
                            index1++
//                        } else {
//                            index1
//                        }

                        return@loop acc
                    }// count until first match fail happens
                    else {
                        isMatcher = false
                        index1 = 0

                        return@loop acc +1
//                        return@loop acc +1
                    }
                    Log.d("HWO", "index state222 -> $index1")

                }
            } ?: 0


            correctCnt += cnt

            //accumulatedMaxCount -> answer split
            if (maxCount != null) {
                Log.d("HWO" ,"count -> $correctCnt ---- $accumulatedMaxCount")
                if (correctCnt > accumulatedMaxCount) {
                    correctCnt = accumulatedMaxCount
                }
            }
            val finalizedWordCount = min(correctCnt, accumulatedMaxCount)
            CoroutineScope(Dispatchers.Default).launch {
                Log.d("HWO", "finalcount -> $finalizedWordCount --- $correctCnt")
                responseChannel.send(ResponseInfo(finalizedWordCount, correctCnt, maxCount != null))
            }




            lastRecognizedWords = transcript.splitAsWords()
        }
        isCurrent = if (answerCnt <= correctCnt) {
            Log.d("HWO", "정답!")
            true
        } else {
            Log.d("HWO", "틀림!")
            false
        }
        Log.d("HWO", "FINIA")
        if (isCurrent) {
            successCnt++
        } else {
            failCnt++
        }


        Log.d("HWO", "success -> $successCnt ---- $failCnt")
    }

//
//    fun checkCorrectWordsCount(inputSentence: String, maxCount: Int?) {
//        val newWords = filterNewWords(inputSentence.splitAsWords())
//        Log.d("HWO", "transcript -> $newWords")
//        recognizedWordList.addAll(newWords.map { it.word })
//
//        if (maxCount != null) {
//            accumulatedMaxCount = if (accumulatedMaxCount + maxCount > answerWords.size) {
//                answerWords.size
//            } else {
//                accumulatedMaxCount + maxCount
//            }
//        }
//
//        val cnt = run loop@{ // count matching inputs
//            leftAnswers?.foldIndexed(0) { answerIndex, acc, answer ->
//
//
//                val checkRange = 0..4 - difficultyLevel
//                val isFinal = maxCount != null
//
//                val match = checkRange.any { offset ->
//                    val index = answerIndex + offset
//                    val new = newWords.getOrNull(index)
//                    val isMatch = if (maxCount != null) {
//                        new != null && index > -1 && new.isWordMatch(answer, true, difficultyLevel)
//                    } else {
//                        new != null && index > -1 && new.isWordMatch(answer, false, difficultyLevel)
//                    }
//
//                    isMatch
//                }
//
//                //정답
//                Log.d("HWO", "match state --> $match")
//
//                if (!match) return@loop acc // count until first match fail happens
//
//                acc + 1
//            }
//        } ?: 0
//
//        Log.d("HWO", "cnt -> $cnt")
//
//        correctCnt += cnt
//
//        if (maxCount != null) {
//            if (correctCnt > accumulatedMaxCount) {
//                correctCnt = accumulatedMaxCount
//            }
//        }
//        val finalizedWordCount = min(correctCnt, accumulatedMaxCount)
//        CoroutineScope(Dispatchers.Default).launch {
//            responseChannel.send(ResponseInfo(finalizedWordCount, correctCnt, maxCount != null))
//        }
//
//        lastRecognizedWords = inputSentence.splitAsWords()
//    }



}

