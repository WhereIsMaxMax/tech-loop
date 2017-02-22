package com.whrsmxmx.kotlintest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    @Volatile private var mTempo = 90L

    private var mKickFileName = ""
    private var mSnareFileName = ""

//    flags
    @Volatile private var isKickPlaying = false
    @Volatile private var isSnarePlaying = false
    @Volatile private var isPaused = false

    private var mService: Messenger? = null
    private var mBound = false
    private val mConnection: ServiceConnection = object:ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = Messenger(service)
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, TechService::class.java), mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        number_picker.minValue = 40
        number_picker.maxValue = 280
        number_picker.value = 90

        kick_view.text = "Kick"

        kick_view.setOnTouchListener { view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN){
                if(isKickPlaying){
                    sendStopKick()
                    isKickPlaying = false
                    kick_view.setTextColor(Color.BLACK)
                } else{
                    sendStartRecordKick()
                    isKickPlaying = true
                    kick_view.setTextColor(Color.RED)
                }
            } else if(motionEvent.action == MotionEvent.ACTION_UP){
                if(isKickPlaying){
                    sendStopRecordKick()
                    kick_view.setTextColor(Color.BLUE)
                }else{
//                    sendStopKick()
//                    isKickPlaying = false
//                    kick_view.setTextColor(Color.WHITE)
                }
            }
            true
        }
        snare_view.text = "Snare"
        snare_view.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action==MotionEvent.ACTION_DOWN){
                if(isSnarePlaying){
                    sendStopSnare()
                    isSnarePlaying = false
                    snare_view.setTextColor(Color.BLACK)
                }else{
                    sendStartRecordSnare()
                    isSnarePlaying = true
                    snare_view.setTextColor(Color.RED)
                }
            }
            else if(motionEvent.action == MotionEvent.ACTION_UP){
                if(isSnarePlaying){
                    sendStopRecordSnare()
                    snare_view.setTextColor(Color.BLUE)
                }else{
//                    sendStopSnare()
//                    isSnarePlaying = false
//                    snare_view.setTextColor(Color.WHITE)
                }
            }
            true
        }
    }

    inner class incomingHandler

    private fun sendStartRecordKick() {
        if(!mBound) return
        mService!!.send(Message.obtain(null, MSG_RECORD_KICK, 0, 0))
        Log.d(TAG, "sendStartRecordKick")
    }

    private fun sendStartRecordSnare() {
        if(!mBound) return
        mService!!.send(Message.obtain(null, MSG_RECORD_SNARE))
        Log.d(TAG, "sendStartRecordSnare")
    }

    private fun sendStopRecordKick() {
        if(!mBound) return
        mService!!.send(Message.obtain(null, MSG_STOP_RECORD_KICK))
        Log.d(TAG, "sendStopRecordKick")
    }

    private fun sendStopRecordSnare() {
        if(!mBound) return
        mService!!.send(Message.obtain(null, MSG_STOP_RECORD_SNARE))
        Log.d(TAG, "sendStopRecordSnare")
    }

    private fun sendStopSnare() {
        if(!mBound) return
        mService!!.send(Message.obtain(null, MSG_STOP_SNARE))
        Log.d(TAG, "sendStopSnare")
    }

    private fun sendStopKick() {
        if (!mBound) return
        mService!!.send(Message.obtain(null, MSG_STOP_KICK))
        Log.d(TAG, "sendStopKick")
    }


    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection)
            mBound = false
        }
    }
}
