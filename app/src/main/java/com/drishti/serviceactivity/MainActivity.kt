package com.drishti.serviceactivity

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import com.drishti.serviceactivity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    lateinit var binding: ActivityMainBinding
    private  val TAG = "MainActivity"
    var isAllowed = true
    var locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        for(values in it.entries){
            Log.e(TAG, "in permissions ${values}")
            if(values.value == false){
                isAllowed = false
            }
        }

        if(isAllowed == true){
            var intent = Intent(this, ForegroundLocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }else{
                startService(intent)
            }
        }
    }

    var connectivityReceiver = ConnectivityReceiver()
    var isInternetDialogShowing = false
    lateinit var dialog : Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startService(Intent(this, HelloService::class.java))
        binding.btnService.setOnClickListener {
            locationPermissions.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.FOREGROUND_SERVICE_LOCATION

            ))
        }

        dialog = Dialog(this)
        dialog.setContentView(R.layout.no_internet_dialog)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY),
                RECEIVER_NOT_EXPORTED
            )
        }else{
            registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY))
        }

        connectivityReceiver.connectivityReceiverListener = this

    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Log.e(TAG, "in isConnected $isConnected")
        if(isConnected){
            isInternetDialogShowing = false
            if(dialog.isShowing){
                dialog.dismiss()
            }
        }else{
            dialog.show()
            isInternetDialogShowing = true
        }


    }


}