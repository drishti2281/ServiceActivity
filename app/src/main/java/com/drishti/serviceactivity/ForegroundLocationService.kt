package com.drishti.serviceactivity

import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat


class ForegroundLocationService: Service() {
    private val TAG = "ForegroundLocationServi"
    private val CHANNEL_ID = "CHANNEL ID"
    val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?): IBinder? {
      return null!!
    }
    private fun startForeground(){
        Log.e(TAG, " Start foreground")
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE_LOCATION)== PackageManager.PERMISSION_GRANTED
            ){
           try{
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   val name = getString(R.string.channels_name)
                   val descriptionText = getString(R.string.description_text)
                   val importance = NotificationManager.IMPORTANCE_HIGH
                   val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                       description = descriptionText
                   }
                   notificationManager.createNotificationChannel(channel)
               }
               var builder = NotificationCompat.Builder(this, CHANNEL_ID)
                   .setSmallIcon(R.drawable.img)
                   .setContentTitle("Notification tools")
                   .setContentText("This is a notification message.")

               val notification= builder.build()
               ServiceCompat.startForeground(
                   /* service = */ this,
                   /* id = */ 100, // Cannot be 0
                   /* notification = */ notification,
                   /* foregroundServiceType = */
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                       ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                   } else {
                       0
                   },
               )

           }catch (exception: Exception){
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                   && exception is ForegroundServiceStartNotAllowedException
               ) {
                   Log.e(TAG, " in foreground service exception $exception")
                   // App not in a valid state to start foreground service
                   // (e.g. started from bg)
               }

           }
        }else{
            stopSelf()
        }

        return
    }

    override fun onCreate() {
        super.onCreate()
        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
