package scar.apps.scarservice

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import scar.apps.scarservice.GeneralScanner.BarcodeDataListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerConnectionListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper
import retrofit2.*
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import scar.apps.scarservice.Data.ApiService
import scar.apps.scarservice.Data.DataDocumentAdapter
import scar.apps.scarservice.Data.Documents
import scar.apps.scarservice.Data.ErrorMessage
import retrofit2.adapter.rxjava.Result.response
import android.R.string
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject




class documents_product : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context:Context
    lateinit var adapter: DataDocumentAdapter
    val document = ArrayList<Documents>()
    val serviceApi = ApiService.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents_product)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        val clear_list  = findViewById<Button>(R.id.button4)
        adapter = DataDocumentAdapter(document)
        val layoutManager = LinearLayoutManager(applicationContext)
        context = this@documents_product
        rv?.layoutManager = layoutManager
        rv?.itemAnimator = DefaultItemAnimator()
        sharedPreferences = getSharedPreferences("UserActivity", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        rv?.adapter = adapter
        adapter.notifyDataSetChanged()
        var back = findViewById<TextView>(R.id.back)
        var title = findViewById<TextView>(R.id.title_edit)
        title.setText("Выдача документов")

        back.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }
        val btn_send  = findViewById<Button>(R.id.succes)
        btn_send.setText("Выдача")
        btn_send.setOnClickListener{
            var json = ArrayList<String>()

            document.forEach{
                json.add(it.Barcode)
            }
            try {

                serviceApi.documents("Bearer " + sharedPreferences.getString("Token", null), json.toList())
                    .enqueue(object : Callback<ErrorMessage> {
                        override fun onFailure(call: Call<ErrorMessage>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<ErrorMessage>, response: Response<ErrorMessage>) {


                            if (response.code().toString() == "204") {
                                document.clear()
                                adapter.notifyDataSetChanged()
                                Toast.makeText(applicationContext, "Документы выданы", Toast.LENGTH_LONG).show()

                            } else {
                                Toast.makeText(applicationContext, "Документы уже были выданы ранее", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }catch (e:Exception){
                                        Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()

            }
        }

        clear_list.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            builder.setTitle("Очистить список документов?")
            builder.setPositiveButton("Да") { _, _ ->
                document.clear()
                adapter.notifyDataSetChanged()
            }

            builder.setNegativeButton("Нет") { dialog, _ ->
                dialog.cancel()
            }
            builder.show()


        }
    }

    override fun onResume() {
        super.onResume()
        startBarcodeScanner()
    }
    override fun onPause() {
        super.onPause()
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
//                            Toast.makeText(context,"Scanned data: ${data}",Toast.LENGTH_SHORT).show()
                        {
                                        serviceApi.document("Bearer " + sharedPreferences.getString("Token", null),data.trim()).enqueue(object:
                Callback<Documents> {
                override fun onFailure(call: Call<Documents>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Documents>, response: Response<Documents>) {
                    if(response.code().toString() == "200"){
                        if(response.body()!! in document)
                        {
                            Toast.makeText(applicationContext, "Документ уже добавлен в список", Toast.LENGTH_LONG).show()

                        }else{
                            document.add(0,response.body()!!)
                            adapter.notifyDataSetChanged()
                        }

                    }else{
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
                    }
                })
        }catch (e:Exception){
//            Toast.makeText(context,"Scanned data: $e",Toast.LENGTH_LONG).show()
        }

    }

}
