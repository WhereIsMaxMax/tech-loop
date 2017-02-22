package com.whrsmxmx.kotlintest

/**
 * Created by Max on 07.02.2017.
 */

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

import java.io.IOException

class AudioRecordTest : AppCompatActivity() {

    private var mRecordButton: RecordButton? = null
    private var mRecorder: MediaRecorder? = null

    private var mPlayButton: PlayButton? = null
    private var mPlayer: MediaPlayer? = null

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()

    }

    private fun onRecord(start: Boolean) {
        if (start) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun onPlay(start: Boolean) {
        if (start) {
            startPlaying()
        } else {
            stopPlaying()
        }
    }

    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource(mFileName)
            mPlayer!!.prepare()
            mPlayer!!.start()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }

    }

    private fun stopPlaying() {
        mPlayer!!.release()
        mPlayer = null
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder!!.setOutputFile(mFileName)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        try {
            mRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }

        mRecorder!!.start()
    }

    private fun stopRecording() {
        mRecorder!!.stop()
        mRecorder!!.release()
        mRecorder = null
    }

    internal inner class RecordButton(ctx: Context) : Button(ctx) {
        var mStartRecording = true

        var clicker: View.OnClickListener = View.OnClickListener {
            onRecord(mStartRecording)
            if (mStartRecording) {
                text = "Stop recording"
            } else {
                text = "Start recording"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context) : Button(ctx) {
        var mStartPlaying = true

        var clicker: View.OnClickListener = View.OnClickListener {
            onPlay(mStartPlaying)
            if (mStartPlaying) {
                text = "Stop playing"
            } else {
                text = "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        init {
            text = "Start playing"
            setOnClickListener(clicker)
        }
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Record to the external cache directory for visibility
        mFileName = externalCacheDir!!.absolutePath
        mFileName += "/audiorecordtest.3gp"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        val ll = LinearLayout(this)
        mRecordButton = RecordButton(this)
        ll.addView(mRecordButton,
                LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0f))
        mPlayButton = PlayButton(this)
        ll.addView(mPlayButton,
                LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0f))
        setContentView(ll)
    }

    public override fun onStop() {
        super.onStop()
        if (mRecorder != null) {
            mRecorder!!.release()
            mRecorder = null
        }

        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }
    }

    companion object {

        private val LOG_TAG = "AudioRecordTest"
        private val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private var mFileName: String? = null
    }
}