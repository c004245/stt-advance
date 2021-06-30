package com.example.sttmodule

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.STATE_UNINITIALIZED
import android.media.MediaRecorder
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class VoiceRecorder(val voiceRecorderCallback: VoiceRecorderCallback) {

    companion object {
        val SAMPLE_RATE_CANDIDATES = listOf(16000, 11025, 22050, 44100)
        const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
        const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        const val AMPLITUDE_THRESHOLD = 250
        const val SPEECH_TIMEOUT_MILLIS = 300000
        const val MAX_SPEECH_LENGTH_MILLIS = 300 * 1000
        const val MAX_OVERALL_BUFFER_SIZE = 1024 * 1024 * 5
    }

    private var audioBuffer: ByteArray? = null
    private var audioRecord: AudioRecord? = null
    private var sampleRate: Int = SAMPLE_RATE_CANDIDATES[0]

    private var lastVoiceHeardMillis = Long.MAX_VALUE

    private var voiceStartedMillis = Long.MAX_VALUE

    private var voiceBufferSize = 0

    private var processVoiceTaskFuture: Future<*>? = null
    private val voiceBuffer = ByteArray(MAX_OVERALL_BUFFER_SIZE)

    private var paused: Boolean = false
    fun pause() {
        paused = true
    }

    fun restart() {
        paused = false
    }
    private val recordWaveByteArray: ByteArray?
        get() {
            return WavUtils.rawToWaveByteArray(voiceBuffer, voiceBufferSize, sampleRate)
        }

    private val threadExecutor = Executors.newSingleThreadExecutor()

    private val processVoiceTask = object: Runnable {
        override fun run() {
            var size: Int?
            var now: Long
            while (!Thread.currentThread().isInterrupted) {
                val audioBuffer = audioBuffer ?: break

                size = audioRecord?.read(audioBuffer, 0, audioBuffer.size)

                if (size != null) {
                    System.arraycopy(audioBuffer, 0, voiceBuffer, voiceBufferSize, size)

                    voiceBufferSize += size

                    now = System.currentTimeMillis()

                    if (lastVoiceHeardMillis == Long.MAX_VALUE &&
                                voiceStartedMillis == Long.MAX_VALUE) {
                        voiceStartedMillis = now
                    }

                    if (!paused && isHearingVoice(audioBuffer, size)) {
                        voiceRecorderCallback.onVoice(audioBuffer, size)
                        lastVoiceHeardMillis = now

                        if (now - voiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                            end()
                        }
                    } else if (!paused && lastVoiceHeardMillis != Long.MAX_VALUE) {
                        voiceRecorderCallback.onVoice(audioBuffer, size)
                        if (now - lastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                            end()
                        }
                    } else if (lastVoiceHeardMillis == Long.MAX_VALUE) {
                        if (now - voiceStartedMillis > SPEECH_TIMEOUT_MILLIS) {
                            end()
                        }
                    }
                }
                try {
                    Thread.sleep(20)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }

        private fun end() {
            stop(true)
            voiceRecorderCallback.onVoiceEnd(recordWaveByteArray)
        }

        private fun isHearingVoice(buffer: ByteArray, size: Int): Boolean {
            var i =0
            while (i < size -1) {
                var s = buffer[i + 1].toInt()
                if (s < 0) s *= -1
                s = s shl 8
                s += Math.abs(buffer[i].toInt())
                if (s > AMPLITUDE_THRESHOLD) {
                    return true
                }
                i += 2
            }
            return false
        }
    }

    fun stop(isAudioRecordEnd: Boolean) {
        if (isAudioRecordEnd) {
            dismiss()
            processVoiceTaskFuture?.cancel(true)
            processVoiceTaskFuture = null
            audioRecord?.apply {
                if (this.state != STATE_UNINITIALIZED) {
                    release()
                }
                audioRecord = null
            }
            audioBuffer = null
        } else {

        }
    }
    private fun dismiss() {
        if (lastVoiceHeardMillis != Long.MAX_VALUE) {
            lastVoiceHeardMillis = Long.MAX_VALUE
            voiceRecorderCallback.onVoiceEnd(recordWaveByteArray)
        }
    }

    fun start() {
        stop(true)

        voiceBufferSize = 0
        voiceStartedMillis = Long.MAX_VALUE
        lastVoiceHeardMillis = Long.MAX_VALUE

        audioRecord = createAudioRecord()

        audioRecord?.startRecording()
            ?: throw IllegalStateException("Cannot instantiate VoiceRecorder..")

        processVoiceTaskFuture = threadExecutor.submit(processVoiceTask)
    }

    fun release() {
        stop(true)
        threadExecutor.shutdownNow()
    }

    private fun createAudioRecord(): AudioRecord? {

        var sizeInBytes: Int
        SAMPLE_RATE_CANDIDATES.forEach { _sampleRate ->
            sizeInBytes = AudioRecord.getMinBufferSize(_sampleRate, CHANNEL, ENCODING)
            if (sizeInBytes != AudioRecord.ERROR_BAD_VALUE) {
                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    _sampleRate, CHANNEL, ENCODING, sizeInBytes
                )

                return if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
                    audioBuffer = ByteArray(sizeInBytes)
                    this.sampleRate = _sampleRate
                    audioRecord
                } else {
                    audioRecord.release()
                    null
                }
            }
        }
        return null
    }
}


open class VoiceRecorderCallback {

    open fun onVoiceStart(phrases: String) {}

    open fun onVoice(data: ByteArray, size: Int) {}

    open fun onVoiceEnd(recordWaveByteArray: ByteArray?) {}


}

object WavUtils {


    @Throws(IOException::class)
    fun rawToWaveByteArray(rawData: ByteArray, length: Int, sampleRate: Int): ByteArray? {
        //44 bytes headers
        try {
            val baos = ByteArrayOutputStream()
            val output = DataOutputStream(baos)
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF") // chunk id
            writeInt(output, 36 + length) // chunk size
            writeString(output, "WAVE") // format
            writeString(output, "fmt ") // subchunk 1 id
            writeInt(output, 16) // subchunk 1 size
            writeShort(output, 1.toShort()) // audio format (1 = PCM)
            writeShort(output, 1.toShort()) // number of channels
            writeInt(output, sampleRate) // sample rate
            writeInt(output, sampleRate) // byte rate
            writeShort(output, 2.toShort()) // block align
            writeShort(output, 16.toShort()) // bits per sample
            writeString(output, "data") // subchunk 2 id
            writeInt(output, length) // subchunk 2 size
            output.write(rawData, 44, length)
            output.flush()
            return baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @Throws(IOException::class)
    fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }

    @Throws(IOException::class)
    fun writeShort(output: DataOutputStream, value: Short) {
        output.write(value.toInt() shr 0)
        output.write(value.toInt() shr 8)
    }

    @Throws(IOException::class)
    fun writeString(output: DataOutputStream, value: String) {
        for (element in value) {
            output.write(element.toInt())
        }
    }
}