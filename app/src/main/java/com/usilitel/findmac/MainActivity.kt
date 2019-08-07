package com.usilitel.findmac

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val utils = NotificationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        utils.sendMacToServer("fc:75:16:a3:f2:5a")
        utils.sendMacToServer("8c:64:22:ad:e2:04")
        utils.sendMacToServer("d8:63:75:99:b8:f6")
        utils.sendMacToServer("28:d2:44:06:13:a2")
        utils.sendMacToServer("00:1e:64:66:b3:7e")
        utils.sendMacToServer("60:36:dd:f8:a0:d9")

    }

}

