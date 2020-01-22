package scar.apps.scarservice

import android.app.Application
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper


class ScannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        BarcodeScannerHelper.newInstance(this@ScannerApplication)
    }
}
