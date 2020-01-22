package scar.apps.scarservice.GeneralScanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.generalscan.SendConstant

/**
 * Created by edwin on 8/7/17.
 */

class BarcodeBroadcastReceiver : BroadcastReceiver() {

    internal var barcodeDataListener: BarcodeDataListener? = null

    internal var barcode = ""

    fun setBarcodeDataListener(barcodeDataListener1: BarcodeDataListener) {
        barcodeDataListener = barcodeDataListener1
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SendConstant.GetBatteryDataAction) {
            val data = intent.getStringExtra(SendConstant.GetBatteryData)
            //Toast.makeText(context, "Battery data: " + data, Toast.LENGTH_SHORT).show();
        }
        if (intent.action == SendConstant.GetDataAction) {
            val data = intent.getStringExtra(SendConstant.GetData)
            barcode += data
            if (data!!.matches("[\\n\\r]+".toRegex())) {
                //   Toast.makeText(context, "Barcode: " + barcode, Toast.LENGTH_SHORT).show();

                barcodeDataListener!!.onDataReceived(barcode)

                barcode = ""
            }
        }
    }
}
