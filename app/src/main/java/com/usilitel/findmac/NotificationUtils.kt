package com.usilitel.findmac

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


// класс для отправки push-уведомлений
object NotificationUtils {

    // Firebase Messaging Service
    private val url = URL("https://fcm.googleapis.com/fcm/send")
    // id приложения в Firebase Cloud Messaging
    private val apiKey = "AAAAizEsZ94:APA91bGgYEFAZLpBUcaXOm03rL9r73d5uSCQRfhP3al8kLVPk4C762lFbEbdSRowcFTyfneyHcMkqh0k4i0Y4Jre-gypa4LSsEi1fHBsS5xPlFu6aGOWBrabPa6B9AuJRbLI15nMWlfU"
    val request_title = "request"
    val response_title = "response"

    // отправляем сообщение через Firebase Cloud Messaging
    public suspend fun sendNotification(title: String, address: String, dataParams:Map<String,String>){

        // формируем сообщение
        val conn = url.openConnection() as HttpURLConnection
        conn.setDoOutput(true)
        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "key=$apiKey")
        conn.setDoOutput(true)

        // формируем тело уведомления
        var data: String="{"
        for((key, value) in dataParams){
            data = data + "\"$key\" : \"$value\","
        }
        data=data.substring(0, data.length-1)+"}"
        val input = "{\"notification\" : {\"title\" : \"$title\"}, \"data\" : $data, \"to\":\"$address\"}"
        val os = conn.getOutputStream()

        // отправляем уведомление в FCM
        os.write(input.toByteArray())
        os.flush()
        os.close()
        val responseCode = conn.getResponseCode()
        Log.d("PushNotificationSend","Sent 'POST' request to URL : $url")
        Log.d("PushNotificationSend","Post parameters : $input")
        Log.d("PushNotificationSend","Response Code : $responseCode")

        // получаем ответ от сервера
        val inn1 = BufferedReader(InputStreamReader(conn.getInputStream()))
        var inputLine: String?=""
        val response = StringBuffer()
        inputLine = inn1.readLine()
        while (inputLine != null) {
            response.append(inputLine!!)
            inputLine = inn1.readLine()
        }
        inn1.close()
        Log.d("PushNotificationSend","response text: " + response.toString())
    }

}