package scar.apps.scarservice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import scar.apps.scarservice.GeneralScanner.BarcodeDataListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerConnectionListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.*
import scar.apps.scarservice.Data.*
import android.R.attr.data
import android.app.Activity


class product_stock : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context: Context
    val product = ArrayList<Products>()
    val serviceApi = ApiService.create()
    lateinit var adapter: DataStockAdapter
    lateinit var place: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_stock)
        val rv = findViewById<RecyclerView>(R.id.recyclerView)
        val clear_list  = findViewById<Button>(R.id.clear_lists)
        val btn_search: ImageButton = findViewById<ImageButton>(R.id.search_produts)
        place = findViewById(R.id.place_w)
        context = this@product_stock
        adapter = DataStockAdapter(product, place.text.toString())
        val layoutManager = LinearLayoutManager(applicationContext)
        rv?.layoutManager = layoutManager
        rv?.itemAnimator = DefaultItemAnimator()
        sharedPreferences = getSharedPreferences("UserActivity", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        var title = findViewById<TextView>(R.id.title_edit)
        title.setText("Привязка складского места")
        rv?.adapter = adapter
        adapter.notifyDataSetChanged()
        var back = findViewById<TextView>(R.id.back)
        place.setOnClickListener {
            if (product.size == 0) {
                val intent = Intent(baseContext, scar.apps.scarservice.place::class.java)
                intent.putExtra("type", "product_stock")
                startActivityForResult(intent, 2)
            }
        }

        var edittext:EditText = findViewById(R.id.text)

        back.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }
        btn_search.setOnClickListener {
            if(place.text.toString() != "") {
                var barcode: String = edittext.text.toString()
                serviceApi.search("Bearer " + sharedPreferences.getString("Token", null), barcode).enqueue(object :
                    Callback<List<History>> {
                    override fun onFailure(call: Call<List<History>>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {

                        if (response.code().toString() == "200" && response.body()!!.size == 1) {
//
                            serviceApi.product(
                                "Bearer " + sharedPreferences.getString("Token", null),
                                response.body()!![0].Id
                            ).enqueue(object :
                                Callback<Products> {
                                override fun onFailure(call: Call<Products>, t: Throwable) {
                                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call<Products>, response: Response<Products>) {
                                    if (response.code().toString() == "200") {

                                        if (product.any { obj -> obj.Id == response.body()!!.Id }) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Товар уже добавлен в обработку",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {

                                            product.add(0,response.body()!!)
                                            product[0].Places = place.text.toString()
                                            adapter.notifyDataSetChanged()

                                            Toast.makeText(
                                                applicationContext,
                                                "Товар добавлен  в обработку",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }

                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Выбранный товар не найден",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
                            })

                        } else if (response.code().toString() == "200" && response.body()!!.size > 1) {
                            try {
                                val intent = Intent(context, select_stock_product::class.java)
                                intent.putExtra("Product", barcode)
                                startActivityForResult(intent, 1)
                            } catch (e: Exception) {
                                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG)
                                    .show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Выбранный товар не найден", Toast.LENGTH_SHORT).show()

                        }
                    }
                })
            } else {
                Toast.makeText(applicationContext, "Укажите складское место", Toast.LENGTH_SHORT).show()
            }
        }

        val btn_send  = findViewById<Button>(R.id.succes)
        btn_send.setText("Привязать")
        btn_send.setOnClickListener{

            var json = ArrayList<String>()

            product.forEach{
                json.add(it.Id)
            }
            try {

                serviceApi.Place("Bearer " + sharedPreferences.getString("Token", null), json.toList(),place.text.toString())
                    .enqueue(object : Callback<ErrorMessage> {
                        override fun onFailure(call: Call<ErrorMessage>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<ErrorMessage>, response: Response<ErrorMessage>) {


                            if (response.code().toString() == "204") {
                                product.clear()
                                adapter.notifyDataSetChanged()
                                Toast.makeText(applicationContext, "Складское место установлено", Toast.LENGTH_LONG).show()

                            }
                        }
                    })
            }catch (e:Exception){
                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()

            }
        }

        clear_list.setOnClickListener {

            val builder = AlertDialog.Builder(context)

            builder.setTitle("Очистить список товаров?")
            builder.setPositiveButton("Да") { _, _ ->
                product.clear()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        Toast.makeText(applicationContext, "ytrd", Toast.LENGTH_SHORT).show()
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val products = data!!.extras?.get("Product") as Products
                if (product.any { obj -> obj.Id == products.Id }) {
                    Toast.makeText(
                        applicationContext,
                        "Товар уже добавлен в обработку",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                        product.add(0,products)
                    product[0].Places = place.text.toString()
                        adapter.notifyDataSetChanged()

                    Toast.makeText(applicationContext, "Товар добавлен  в обработку", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val places = data!!.extras?.get("place") as String

                place.setText(places)
            }
        }
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
                        if(!data.isBlank()) {
                            if (data.take(4) == "2014") {
                                serviceApi.place("Bearer " + sharedPreferences.getString("Token", null), data.trim()).enqueue(object: Callback<List<String>>{
                                    override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                                    }

                                    override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                        if(response.code().toString() == "200"){
                                            if (product.size == 0) {
                                                place.setText(response.body()!![0])
                                            }
                                        } else{
                                            Toast.makeText(applicationContext,"Место не найдено", Toast.LENGTH_LONG).show()

                                        }
                                    }
                                })
                            } else {
                                if(place.text.toString() != "") {
                                    serviceApi.search("Bearer " + sharedPreferences.getString("Token", null), data.trim()).enqueue(object :
                                        Callback<List<History>> {
                                        override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                                        }

                                        override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {

                                            if (response.code().toString() == "200" && response.body()!!.size == 1) {
//
                                                serviceApi.product(
                                                    "Bearer " + sharedPreferences.getString("Token", null),
                                                    response.body()!![0].Id
                                                ).enqueue(object :
                                                    Callback<Products> {
                                                    override fun onFailure(call: Call<Products>, t: Throwable) {
                                                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                                                    }

                                                    override fun onResponse(call: Call<Products>, response: Response<Products>) {
                                                        if (response.code().toString() == "200") {

                                                            if (product.any { obj -> obj.Id == response.body()!!.Id }) {
                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Товар уже добавлен в обработку",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                            } else {

                                                                    product.add(0,response.body()!!)
                                                                product[0].Places = place.text.toString()
                                                                    adapter.notifyDataSetChanged()

                                                                Toast.makeText(
                                                                    applicationContext,
                                                                    "Товар добавлен  в обработку",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                            }

                                                        } else {
                                                            Toast.makeText(
                                                                applicationContext,
                                                                "Выбранный товар не найден",
                                                                Toast.LENGTH_SHORT
                                                            ).show()

                                                        }
                                                    }
                                                })

                                            } else if (response.code().toString() == "200" && response.body()!!.size > 1) {
                                                try {
                                                    val intent = Intent(context, select_stock_product::class.java)
                                                    intent.putExtra("Product", data.trim())
                                                    stopBarcodeScanner()
                                                    startActivityForResult(intent, 1)
                                                } catch (e: Exception) {
                                                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG)
                                                        .show()
                                                }
                                            } else {
                                                Toast.makeText(applicationContext, "Выбранный товар не найден", Toast.LENGTH_SHORT).show()

                                            }
                                        }
                                    })
                                } else {
                                    Toast.makeText(applicationContext, "Укажите складское место", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
//                            Toast.makeText(context,"Scanned data: ${data}",Toast.LENGTH_SHORT).show()
                    }
                })
        }catch (e:Exception){
//            Toast.makeText(context,"Scanned data: $e",Toast.LENGTH_LONG).show()
        }

    }
}
