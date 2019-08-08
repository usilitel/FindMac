package com.usilitel.findmac



import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.InetAddress

object Server {

    // Регистрационный токен сервера (куда посылаем уведомление). По умолчанию сервер там же где и клиент.
    // Если сервер и клиент на разных устройствах - прописать сюда токен сервера.
    val serverRegistrationToken = FirebaseInstanceId.getInstance().token
    private val utils = NotificationUtils


    // обрабатываем запрос с клиента
    fun getRequest(remoteMessage: RemoteMessage?){
        if (remoteMessage?.data!!.size > 0) {
            val title = remoteMessage.notification?.title
            val dataMacAddress = remoteMessage.data["macAddress"]
            val dataCientRegistrationToken = remoteMessage.data["clientRegistrationToken"]

            Log.d("PushNotificationReceive", "Client request data: " + remoteMessage.data)
            // ждем пока просканируется сеть
            runBlocking (Dispatchers.IO) {
                scanNet()
            }
            var macAddressIsActive = macAddressIsActive(dataMacAddress!!)
            Log.d("PushNotificationReceive", "Checked on server: macAddress " + dataMacAddress + " is active = " + macAddressIsActive)
            sendResponse(dataMacAddress, macAddressIsActive, dataCientRegistrationToken!!)
        }
    }

    // отправляем с сервера на клиент ответ со статусом mac-адреса
    public  fun sendResponse(dataMacAddress: String, dataMacAddressIsActive: Boolean, address: String){
        var dataParams = mutableMapOf<String, String>()
        dataParams.put("macAddress", dataMacAddress)
        dataParams.put("macAddressIsActive", dataMacAddressIsActive.toString())

        GlobalScope.launch ( Dispatchers.IO ){
            NotificationUtils.sendNotification(utils.response_title, address, dataParams)
        }
    }

    // список активных IP-адресов в сети
    var arrActiveIps: ArrayList<String> = ArrayList<String>()

    // сканируем адреса в сети, запоминаем активные ip-адреса и обновляем ARP-кэш
    //suspend fun scanNet():Deferred<Boolean>{
    suspend fun scanNet(){
        arrActiveIps.clear()
        try {
            val context = FindMac.applicationContext()
            if (context != null) {
                val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                val wm = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val connectionInfo = wm.connectionInfo
                val ipAddress = connectionInfo.ipAddress
                val ipString = Formatter.formatIpAddress(ipAddress)
                val prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1)
                Log.d("PushNotificationScan", "activeNetwork: $activeNetwork")
                Log.d("PushNotificationScan", "ipString: " + ipString.toString())
                Log.d("PushNotificationScan", "prefix: $prefix")

                GlobalScope.async(Dispatchers.IO) {scanIps(prefix)}.await()

            }
        } catch (t: Throwable) {
            Log.d("PushNotificationScan", t.toString())
        }
    }

    suspend fun scanIps(prefix: String){
        for (i in 0..254) {
            val testIp = prefix + i.toString()
            val name = InetAddress.getByName(testIp)
            GlobalScope.async(Dispatchers.IO) {
                checkIsReachable(name)
            }
        }
    }

    // проверяем доступность устройства по ip-адресу (запоминаем активные ip-адреса и обновляем ARP-кэш)
    suspend fun checkIsReachable(name: InetAddress){
        val hostName = name.canonicalHostName
        if (name.isReachable(1000)){
            arrActiveIps.add(hostName)
            Log.d("PushNotificationScan", "Active Host:$hostName")
        }
    }


    // ищем mac-адрес в сети (читаем ARP-кэш (файл /proc/net/arp))
    // возвращаем true если такой mac-адрес есть среди активных устройств в сети (записаны ранее в массив arrActiveIps в процедуре scanNet)
    fun macAddressIsActive(macAddress: String): Boolean {
        var bufRead: BufferedReader? = null
        try {
            bufRead = BufferedReader(FileReader("/proc/net/arp"))
            var fileLine = bufRead.readLine()
            while (fileLine != null) {
                val splitted = fileLine.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitted != null && splitted.size >= 4) {
                    val ip = splitted[0]
                    val mac = splitted[3]
                    // если нашли нужный mac-адрес И он активен - возвращаем true
                    if((mac==macAddress) && (arrActiveIps.indexOf(ip) != -1))
                        return true
                }
                fileLine = bufRead.readLine()
            }
        } catch (e: Exception) {
        } finally {
            try {
                bufRead!!.close()
            } catch (e: IOException) {
            }

        }
        return false
    }

}