package scar.apps.scarservice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import scar.apps.scarservice.GeneralScanner.BarcodeScannerConnectionListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper

class splash_screen : AppCompatActivity() {
    lateinit var context:Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        context = this@splash_screen
        val background = object : Thread(){
            override fun run() {
                try {
                    Thread.sleep(1000)
                    val intent = Intent(baseContext, login::class.java)
                    startActivity(intent)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }

}
