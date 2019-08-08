package com.usilitel.findmac

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// класс для обработки входящих push-уведомлений
class FirebaseService : FirebaseMessagingService() {

    private val utils = NotificationUtils
    private val server = Server
    private val client = Client

    // обрабатываем пришедшее FCM уведомление
    public override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val title = remoteMessage?.notification?.title

        when (title) {
            // пришел запрос с клиента
            utils.request_title -> server.getRequest(remoteMessage)
            // пришел ответ с сервера
            utils.response_title -> client.getResponse(remoteMessage)
        }
    }
}
