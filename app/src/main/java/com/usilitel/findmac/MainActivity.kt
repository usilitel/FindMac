package com.usilitel.findmac

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val client = Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // проверяем есть ли mac-адрес в сети на сервере
        client.sendRequest("8c:64:22:ad:e2:04")

    }

}

