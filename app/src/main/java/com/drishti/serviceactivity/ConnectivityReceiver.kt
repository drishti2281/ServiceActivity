package com.drishti.serviceactivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class ConnectivityReceiver: BroadcastReceiver() {

    var connectivityReceiverListener: ConnectivityReceiverListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("TAG", "in on receiver")
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener?.onNetworkConnectionChanged(isConnectedOrConnecting(context!!))
        }

    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
    }
}