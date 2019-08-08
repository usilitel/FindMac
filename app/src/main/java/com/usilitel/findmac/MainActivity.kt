package com.usilitel.findmac

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val client = Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        client.macAddressStatus.observe(this@MainActivity, Observer {
            if(it==null) return@Observer
            textViewStatus.text = it
        })

        editTextMacAddress.text.clear()
        editTextMacAddress.text.insert(0,"8c:64:22:ad:e2:04")

        buttonSendRequest.setOnClickListener { sendRequest() }
    }

    fun sendRequest(){
        // проверяем есть ли mac-адрес в сети на сервере
        client.sendRequest(editTextMacAddress.text.toString())
    }


}

