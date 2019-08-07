package com.usilitel.findmac

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val utils = NotificationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // проверяем есть ли mac-адрес в сети на сервере
        utils.sendMacToServer("fc:75:16:a3:f2:5a")

    }

}

