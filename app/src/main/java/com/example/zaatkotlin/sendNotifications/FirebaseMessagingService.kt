package com.example.zaatkotlin.sendNotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.zaatkotlin.R
import com.example.zaatkotlin.activities.MainActivity
import com.example.zaatkotlin.activities.MemoryActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class FirebaseMessagingService :
    FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data["Title"] as String
        var message = remoteMessage.data["Message"] as String
        val memoryID = remoteMessage.data["MemoryID"] as String
        val ownerMemoryID = remoteMessage.data["OwnerMemoryID"] as String
        val type = remoteMessage.data["Type"] as String
        message = if (type == "0")
            message + " " + resources.getString(R.string.notification_react)
        else
            message + " " + resources.getString(R.string.notification_comment)
        if (ownerMemoryID == FirebaseAuth.getInstance().uid)
            sendNotification(
                title = resources.getString(R.string.app_name)
                , message = message, memoryID = memoryID
            )
    }

    private fun sendNotification(title: String, message: String, memoryID: String) {
        val intent = Intent(this, MemoryActivity::class.java)
        intent.putExtra("memoryID", memoryID)
        intent.putExtra("userID", FirebaseAuth.getInstance().uid)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val channelID = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.mipmap.ic_zaat)
            .setContentTitle(title)
            .setSound(defaultSoundUri)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                resources.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(
            Calendar.getInstance().timeInMillis.toInt(),
            notificationBuilder.build()
        )
    }
}