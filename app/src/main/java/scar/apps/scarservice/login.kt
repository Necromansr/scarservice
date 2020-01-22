package scar.apps.scarservice


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import com.generalscan.bluetooth.BluetoothSettings
import scar.apps.scarservice.GeneralScanner.BarcodeDataListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scar.apps.scarservice.Data.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.net.Uri
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import android.provider.Settings.canDrawOverlays
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class login : AppCompatActivity() {
    public val userActivity: String = "UserActivity"
    lateinit var login: EditText
    lateinit var password: EditText
    lateinit var remember: CheckBox
    lateinit var loggin: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context: Context
    private val REQUEST_ENABLE_BT = 1
    private val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469
    var btAdapter: BluetoothAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login = findViewById<EditText>(R.id.login)
        password = findViewById<EditText>(R.id.password)
        remember = findViewById<CheckBox>(R.id.checkBox7)
        loggin = findViewById<Button>(R.id.loggin)
        context = this@login


        val setting = findViewById<ImageButton>(R.id.setting)
        setting.setOnClickListener {
            try {

                btAdapter = BluetoothAdapter.getDefaultAdapter()
                CheckBluetoothState()

            }
            catch(e:Exception){
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

            }
        }
        val serviceApi = ApiService.create()

        var saveLogin:Boolean
        sharedPreferences = getSharedPreferences(userActivity, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        if(sharedPreferences.getString("Token", null)!=null){
            serviceApi.history("Bearer " + sharedPreferences.getString("Token", null))
                .enqueue(object : Callback<List<History>> {
                    override fun onFailure(call: Call<List<History>>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                        if (response.code().toString() == "200") {
                            val intent = Intent(baseContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else if(response.code().toString() == "401"){
                            editor.putString("Name", null)
                            editor.putString("Position",null)
                            editor.putString("Store", null)
                            editor.putString("Token",null)
                            editor.commit()
                            saveLogin = sharedPreferences.getBoolean("saveLogin", false)
                            if (saveLogin) {
                                login.setText(sharedPreferences.getString("username", null))
                                password.setText(sharedPreferences.getString("password", null))
                                remember.isChecked = saveLogin
                            }

                            loggin.setOnClickListener {
                                login()
                            }
                        }
                    }
                })


        }else {
            saveLogin = sharedPreferences.getBoolean("saveLogin", false)
            if (saveLogin) {
                login.setText(sharedPreferences.getString("username", null))
                password.setText(sharedPreferences.getString("password", null))
                remember.isChecked = saveLogin
            }

            loggin.setOnClickListener {
                login()
            }

        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBluetoothState()
        }
    }
    fun CheckBluetoothState() {
        // Checks for the Bluetooth support and then makes sure it is turned on
        // If it isn't turned on, request to turn it on
        // List paired devices
        if (btAdapter == null) {
            return
        } else {
            if (btAdapter!!.isEnabled()) {

                // Listing paired devices
                val devices = btAdapter!!.getBondedDevices()
                val mChoose = mutableListOf<String>()
                val mChoose1 = mutableListOf<BluetoothDevice>()
                for (device in devices) {
                    mChoose.add(device.name)
                    mChoose1.add(device)
                }
                var arr: Array<String> = mChoose.toTypedArray()
                try{
                val builder = AlertDialog.Builder(this@login)
                builder.setTitle("Выберите устройство")
                    .setCancelable(false)

                    .setNeutralButton("",
                        DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

                    .setSingleChoiceItems(arr, -1,
                        DialogInterface.OnClickListener { dialog, item ->
                            BluetoothSettings.SetScaner(context,mChoose1[item])
                            dialog.cancel()
                        })

                val alert = builder.create()

                alert.show()
                }catch(e:Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

                }
            } else {
                //Prompt user to turn on Bluetooth
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        startBarcodeScanner()
    }

    override fun onStop() {
        super.onStop()

        stopBarcodeScanner()
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
    fun login(){
        try {


            val username: String = login.text.toString().trim()
            val password: String = password.text.toString().trim()
            val serviceApi = ApiService.create()



            if (remember.isChecked) {
                editor.putBoolean("saveLogin", true)
                editor.putString("username", username)
                editor.putString("password", password)
                editor.commit()
            } else {
                editor.clear()
                editor.commit()
            }
            serviceApi.login(
                Login(
                    username,
                    password
                )
            ).enqueue(object: Callback<User>{
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.code().toString() == "200"){
                        editor.putString("Name", response.body()?.Name)
                        editor.putString("Position", response.body()?.Position)
                        editor.putString("Store", response.body()?.Store)
                        editor.putString("Token", response.body()?.Token)
                        editor.commit()
                        val intent = Intent(baseContext, MainActivity::class.java)
                        startActivity(intent)
                    } else{
                        try {
                            val gson = Gson()
                            val type = object : TypeToken<ErrorMessage>() {}.type
                            var errorResponse: ErrorMessage? = gson.fromJson(response.errorBody()!!.charStream(), type)
                            Toast.makeText(baseContext, errorResponse!!.message, Toast.LENGTH_LONG).show()

                        } catch (e: Exception) {
                            Toast.makeText(baseContext, e.message, Toast.LENGTH_LONG).show()
                        }

                    }
                }
            })
        }
        catch (e:Exception){
            Toast.makeText(baseContext, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
