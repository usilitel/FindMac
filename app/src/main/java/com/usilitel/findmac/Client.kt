package com.usilitel.findmac

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.aspectj.bridge.Version.text

object Client {

    private val server = Server
    private val utils = NotificationUtils

    private var _macAddressStatus = MutableLiveData<String>().apply { value = "" }
    val macAddressStatus : LiveData<String>
        get() = _macAddressStatus


    // отправляем с клиента на сервер запрос с mac-адресом
    public  fun sendRequest(macAddress: String){
        var dataParams = mutableMapOf<String, String>()
        dataParams.put("macAddress", macAddress)
        dataParams.put("clientRegistrationToken", FirebaseInstanceId.getInstance().token!!)

        _macAddressStatus.postValue("request sent...")
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

            _macAddressStatus.postValue(dataMacAddress + " is active = " + dataMacAddressIsActive)
        }
    }

}
