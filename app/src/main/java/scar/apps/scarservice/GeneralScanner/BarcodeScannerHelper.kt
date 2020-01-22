package scar.apps.scarservice.GeneralScanner

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.view.WindowManager

import com.generalscan.NotifyStyle
import com.generalscan.SendConstant
import com.generalscan.bluetooth.BluetoothConnect
import com.generalscan.bluetooth.BluetoothSettings

/**
 * Created by edwin on 9/28/17.
 */

class BarcodeScannerHelper(context: Context) {
    private var barcodeBroadcastReceiver: BarcodeBroadcastReceiver? = null
    private var context: Context? = null

    val isConnected: Boolean
        get() {
            checkRequirements()

            return BluetoothConnect.isConnected()
        }

    init {
        this.context = context

        initScanner()
    }

    fun initScanner() {
        BluetoothConnect.CurrentNotifyStyle = NotifyStyle.NotificationStyle1
        BluetoothConnect.BindService(context)
    }


    fun selectScanner() {
        checkRequirements()

        BluetoothSettings.SetScaner(context)
    }

    fun connectScanner(barcodeScannerConnectionListener: BarcodeScannerConnectionListener) {
        checkRequirements()

        BluetoothConnect.Connect()


        BluetoothConnect.SetOnConnectedListener {
            barcodeScannerConnectionListener.onConnected()
        }

        BluetoothConnect.SetOnConnectFailListener {
            barcodeScannerConnectionListener.onDisconnected()
        }

    }

    fun isConnected(barcodeScannerConnectionListener: BarcodeScannerConnectionListener) {
        checkRequirements()

        BluetoothConnect.SetOnConnectedListener {
            barcodeScannerConnectionListener.onConnected()
        }

        BluetoothConnect.SetOnConnectFailListener {
            barcodeScannerConnectionListener.onDisconnected()
        }
    }

    fun disconnectScanner() {
        checkRequirements()

        BluetoothConnect.Stop(context)
    }

    fun startBarcodeBrodcastReceiver(mContext: Context) {

        checkRequirements()

        barcodeBroadcastReceiver = BarcodeBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(SendConstant.GetDataAction)
        filter.addAction(SendConstant.GetReadDataAction)
        filter.addAction(SendConstant.GetBatteryDataAction)

        try {
            mContext.registerReceiver(barcodeBroadcastReceiver, filter)
        } catch (e: Exception) {
        }

    }


    fun setOnBarcodeDataListener(barcodeDataListener: BarcodeDataListener?) {
        if (barcodeDataListener != null) {
            barcodeBroadcastReceiver!!.setBarcodeDataListener (
                object: BarcodeDataListener {
                    override fun onDataReceived(data: String) {
                        barcodeDataListener.onDataReceived(data)
                    }
                })
        } else {
            throw NullPointerException("Broadcast receiver is null. You must call startBarcodeBrodcastReceiver().")
        }
    }

    fun stopBarcodeBroadcastReceiver(mContext: Context) {
        try {
            if (barcodeBroadcastReceiver != null) {
                mContext.unregisterReceiver(barcodeBroadcastReceiver)
            }
        } catch (e: Exception) {
        }

    }

    protected fun checkRequirements() {
        if (instance == null) {
            throw NullPointerException("Please call newInstance(context) in application class. Thanks!")
        }
    }

    companion object {
        private val TAG = BarcodeScannerHelper::class.java.simpleName
        private  var instance: BarcodeScannerHelper? = null
        fun newInstance(context: Context) {
            if (instance == null) {
                instance =
                    BarcodeScannerHelper(context)
            }
        }

        fun getInstance(context: Context): BarcodeScannerHelper {
            if (instance == null)
                instance =
                    BarcodeScannerHelper(context)
            return instance!!
        }
    }

}