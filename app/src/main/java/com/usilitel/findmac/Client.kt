package com.usilitel.findmac

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Client {

    private val server = Server
    private val utils = NotificationUtils

    // отправляем с клиента на сервер запрос с mac-адресом
    public  fun sendRequest(macAddress: String){
        var dataParams = mutableMapOf<String, String>()
        dataParams.put("macAddress", macAddress)
        dataParams.put("clientRegistrationToken", FirebaseInstanceId.getInstance().token!!)

        GlobalScope.launch ( Dispatchers.IO ){
            NotificationUtils.sendNotification(utils.request_title, server.serverRegistrationToken!!, dataParams)
        }
    }

    // обрабатываем ответ с сервера
    fun getResponse(remoteMessage: RemoteMessage?){
        if (remoteMessage?.data!!.size > 0) {
            val dataMacAddress = remoteMessage?.data["macAddress"]
            val dataMacAddressIsActive = remoteMessage.data["macAddressIsActive"]

            Log.d("PushNotificationReceive", "Server response data: " + remoteMessage.data)
            Log.d("PushNotificationReceive", "Server response: macAddress " + dataMacAddress + " is active = " + dataMacAddressIsActive)
        }
    }

}
