package com.example.zaatkotlin.sendNotifications

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAA3b2q5ro:APA91bF8N6LjCR5v-NU_JuSe6jitxCg6tvQh1oZnqWvLci1FV_VB3-2IBu4NM6HGlmQ6mci1BsJVlhfaDhQ4eXduCHTlcldUo-7i-XzSVR2rQKo-Al9t36a5jonAYAwJwAYf2vwEH-0i"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: NotificationSender): Call<Response>
}