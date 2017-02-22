package com.whrsmxmx.kotlintest

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.*
import android.util.Log
import java.io.File
import java.io.IOException

/**
 * Created by Max on 15.02.2017.
 */

const val MSG_STOP_KICK = 901
const val MSG_STOP_SNARE = 902

const val MSG_RECORD_KICK = 911
const val MSG_RECORD_SNARE = 912

const val MSG_STOP_RECORD_KICK = 913
const val MSG_STOP_RECORD_SNARE = 914

const val TAG_KICK_FILE_NAME = 991
const val TAG_SNARE_FILE_NAME = 992


open class TechService : Service(){

    private val TAG:String = this.javaClass.simpleName
    private val bpm = 90L
    private var delayTime: Long = 1000

    private var mKickMediaPlayer: MediaPlayer? = null
    private var mSnareMediaPlayer: MediaPlayer? = null

    private var mIsKickPlaying = false
    private var mIsSnarePlaying = false
    private var mIsKickRecorded = false
    private var mIsSnareRecorded = false
    private var mKickSource: String = ""
    private var mSnareSource: String = ""

    private var kickRunnable:Runnable? = null
    private var snareRunnable: Runnable? = null
    private var mMediaRecorder: MediaRecorder? = null
    private val mServiceHandler = Handler()

    private val messenger: Messenger = Messenger(IncomingHandler())
    private var startPlayIfMayRunnable:Runnable? = null

    override fun onBind(intent: Intent?): IBinder {
        setup()
//        check is it separate thread
        Log.d(TAG, "Is Service in main thread? " + (Looper.getMainLooper() == Looper.myLooper()).toString())
        return messenger.binder
    }

    private fun setup() {
        mKickSource = externalCacheDir.absolutePath + "/kick.3gp"
        mSnareSource = externalCacheDir.absolutePath + "/snare.3gp"
        mKickMediaPlayer = MediaPlayer()
        mSnareMediaPlayer = MediaPlayer()
    }

    private var mClient: Messenger? = null

    inner class IncomingHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            Log.d(TAG, msg!!.what.toString())
//            any call re-register client
            mClient = msg.replyTo
            when (msg!!.what){
                MSG_STOP_KICK -> stopKick()
                MSG_STOP_SNARE -> stopSnare()
                MSG_RECORD_KICK -> startRecord(mKickSource)
                MSG_RECORD_SNARE -> startRecord(mSnareSource)
                MSG_STOP_RECORD_KICK -> stopRecordKick()
                MSG_STOP_RECORD_SNARE -> stopRecordSnare()
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun startRecord(path:String){
        Log.d(TAG, "startRecord")
        mMediaRecorder = MediaRecorder()
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mMediaRecorder!!.setOutputFile(path)
        try {
            mMediaRecorder!!.prepare()
        }catch (e: IOException){
            Log.e(TAG, "MediaRecorder prepare fail")
        }

        mMediaRecorder!!.start()

        startPlayIfMayRunnable = Runnable {
            Log.d(TAG, "record "+path + " "+ mIsKickRecorded)
            if (File(path).exists()&& path.contains("kick") && mIsKickRecorded)
                setupAndStartKick()
            else if(File(path).exists() && path.contains("snare") && mIsSnareRecorded)
                setupAndStartSnare()
            else
                mServiceHandler.postDelayed(startPlayIfMayRunnable, delayTime)
        }

        mServiceHandler.postDelayed(startPlayIfMayRunnable, delayTime)

        Log.d(TAG, "MediaRecorder start()")
    }

    private fun stopRecordKick(){
        stopRecord()
        mIsKickRecorded = true
    }

    private fun stopRecordSnare(){
        stopRecord()
        mIsSnareRecorded=true
    }

    private fun stopRecord() {
        if(mMediaRecorder != null){
            mMediaRecorder!!.stop()
            mMediaRecorder!!.reset()
            mMediaRecorder!!.release()
            mMediaRecorder = null
            Log.d(TAG, "MediaRecorder stop()")
        }
    }

    private fun stopKick() {
        if(mIsKickPlaying){
            mKickMediaPlayer!!.release()
            mKickMediaPlayer = null
            mKickMediaPlayer = MediaPlayer()
        }
//        else{
//            setupAndStartKick()
//        }
        mIsKickPlaying=!mIsKickPlaying
        mIsKickRecorded=false
    }

    private fun stopSnare() {
        if (mIsSnarePlaying){
            mSnareMediaPlayer!!.release()
            mSnareMediaPlayer = null
            mSnareMediaPlayer = MediaPlayer()
        }
//        else{
//            setupAndStartSnare()
//        }
        mIsSnarePlaying=!mIsSnarePlaying
        mIsSnareRecorded = false
    }

    private fun setupAndStartKick() {
        mKickMediaPlayer!!.setDataSource(mKickSource)
        mKickMediaPlayer!!.prepare()
        mIsKickPlaying = true
        kickRunnable = Runnable {
            if (mIsKickPlaying && mKickMediaPlayer !=null){
                mKickMediaPlayer!!.start()
                mServiceHandler.postDelayed(kickRunnable, delayTime)
                Log.d(TAG, "Kick"+System.currentTimeMillis())
            }
        }
        mServiceHandler.postDelayed(kickRunnable, delayTime)
    }

    private fun setupAndStartSnare() {
        mSnareMediaPlayer!!.setDataSource(mSnareSource)
        mSnareMediaPlayer!!.prepare()
        mIsSnarePlaying = true
        snareRunnable = Runnable {
            if(mIsSnarePlaying && mSnareMediaPlayer !=null){
                mServiceHandler.postDelayed(snareRunnable, delayTime)
                mSnareMediaPlayer!!.start()
                Log.d(TAG, "Snare"+System.currentTimeMillis())
            }
        }
        mServiceHandler.postDelayed(snareRunnable, delayTime)
    }

//if (mMediaRecorder != null) {
//    mMediaRecorder!!.release()
//    mMediaRecorder = null
//}

}