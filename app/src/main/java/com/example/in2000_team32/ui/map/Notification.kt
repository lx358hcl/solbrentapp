package com.example.in2000_team32.ui.map

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.in2000_team32.MainActivity
import com.example.in2000_team32.R


class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val alertToActivityintent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, alertToActivityintent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context!!, "notifySolbrent")
            .setSmallIcon(R.drawable.applogo)
            .setContentTitle(context.getString(R.string.notificationTittel))
            .setContentText(context.getString(R.string.notificationText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.notify(1, notification)

    }


}