package scar.apps.scarservice

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import scar.apps.scarservice.GeneralScanner.BarcodeDataListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerConnectionListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper

class scanner : AppCompatActivity() {
    private val TAG = scanner::class.java.simpleName

    private lateinit var btnConnectScanner: Button
    private lateinit var btnDisconnectScanner: Button
    private lateinit var btnSelectDevice: Button
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        context = this@scanner
        bindViews()

        clickHandler()

    }

   
    override fun onStart() {
        super.onStart()

        startBarcodeScanner()
    }

    override fun onStop() {
        super.onStop()

        stopBarcodeScanner()
    }

    private fun clickHandler() {
        btnSelectDevice.setOnClickListener{ selectScanner() }

        btnDisconnectScanner.setOnClickListener{ disconnectScanner() }

        btnConnectScanner.setOnClickListener{ connectScanner() }
    }
    private fun connectScanner() {
        if (!BarcodeScannerHelper.getInstance(context).isConnected) {
            Toast.makeText(baseContext,"Connecting to barcode scanner...",Toast.LENGTH_SHORT).show()
            BarcodeScannerHelper.getInstance(context).connectScanner(object :
                BarcodeScannerConnectionListener {
                override fun onDisconnected() {
                    Toast.makeText(context,"Connection failed!",Toast.LENGTH_SHORT).show()
                }

                override fun onConnected() {
                    Toast.makeText(context,"Connection success!",Toast.LENGTH_SHORT).show()

                }
            })

        } else {
            Toast.makeText(context,"You are already connected",Toast.LENGTH_SHORT).show()
        }
    }
    private fun disconnectScanner() {
        BarcodeScannerHelper.getInstance(context).disconnectScanner()
    }
    private fun selectScanner() {
        BarcodeScannerHelper.getInstance(context).selectScanner()
    }
    private fun stopBarcodeScanner() {
        BarcodeScannerHelper.getInstance(context).stopBarcodeBroadcastReceiver(context)
    }
    private fun startBarcodeScanner() {
        try {
            BarcodeScannerHelper.getInstance(context).startBarcodeBrodcastReceiver(context)

            BarcodeScannerHelper.getInstance(context).setOnBarcodeDataListener (

                object: BarcodeDataListener {
                    override fun onDataReceived(data: String) {
                        if(!data.isBlank())
                            Toast.makeText(context,"Scanned data: ${data}",Toast.LENGTH_SHORT).show()
                    }
                })
        }catch (e:Exception){
//            Toast.makeText(context,"Scanned data: $e",Toast.LENGTH_LONG).show()
        }

    }
    private fun bindViews() {
        btnConnectScanner = findViewById(R.id.btnConnectScanner)
        btnDisconnectScanner = findViewById(R.id.btnDisconnectScanner)
        btnSelectDevice = findViewById(R.id.btnSelectDevice)
    }
}
