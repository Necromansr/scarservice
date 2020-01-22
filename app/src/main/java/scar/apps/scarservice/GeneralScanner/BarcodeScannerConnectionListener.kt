package scar.apps.scarservice.GeneralScanner

/**
 * Created by edwin on 25/12/2017.
 */

interface BarcodeScannerConnectionListener {
    fun onDisconnected()
    fun onConnected()
}
