package scar.apps.scarservice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import scar.apps.scarservice.GeneralScanner.BarcodeDataListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerConnectionListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scar.apps.scarservice.Data.ApiService
import scar.apps.scarservice.Data.DataSearchAdapter
import scar.apps.scarservice.Data.History
import scar.apps.scarservice.Data.Products

class search_product : AppCompatActivity() {
    var history: List<History> = emptyList()
    lateinit var adapter: DataSearchAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val serviceApi = ApiService.create()
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

        var back = findViewById<TextView>(R.id.back)

        back.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }

        try {
            context = this@search_product

            val mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            val openNav = this@search_product.findViewById<ImageButton>(R.id.nav)

            openNav.setOnClickListener {
                mDrawerLayout.openDrawer(Gravity.RIGHT)
            }
            val navigationView: NavigationView = findViewById(R.id.navbar)
            navigationView.setNavigationItemSelectedListener { menuItem ->
                // set item as selected to persist highlight
                menuItem.isChecked = true
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers()

                // Handle navigation view item clicks here.
                when (menuItem.itemId) {

                    R.id.search -> {
                        val intent = Intent(baseContext, search_product::class.java)
                        startActivity(intent)
                    }
                    R.id.receipt -> {
                        Toast.makeText(baseContext, "Wallet", Toast.LENGTH_LONG).show()
                    }
                    R.id.extradition -> {
                        val intent = Intent(baseContext, documents_product::class.java)
                        startActivity(intent)
                    }
                    R.id.layout -> {
                        Toast.makeText(baseContext, "Setting", Toast.LENGTH_LONG).show()
                    }
                    R.id.logout -> {
                        editor.putString("Name", null)
                        editor.putString("Position", null)
                        editor.putString("Store", null)
                        editor.putString("Token", null)
                        editor.commit()
                        val intent = Intent(baseContext, login::class.java)
                        startActivity(intent)
                    }
                }
                // Add code here to update the UI based on the item selected
                // For app, swap UI fragments here

                true
            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.toString(), Toast.LENGTH_SHORT).show()

        }
        try {
            val btn_search: ImageButton = findViewById(R.id.search_produts)

            val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.list)
            // создаем адаптер
//
            sharedPreferences = getSharedPreferences("UserActivity", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()

            serviceApi.history("Bearer " + sharedPreferences.getString("Token", null))
                .enqueue(object : Callback<List<History>> {
                    override fun onFailure(call: Call<List<History>>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                        if (response.code().toString() == "200") {
                            history = response.body()!!
                            adapter = DataSearchAdapter(
                                context,
                                history,
                                sharedPreferences.getString("Token", null)!!
                            )
                            recyclerView.adapter = adapter
                        }
                    }
                })
            var edittext = findViewById<EditText>(R.id.text)



            btn_search.setOnClickListener {
                var barcode:String = edittext.text.toString()
                serviceApi.search("Bearer " + sharedPreferences.getString("Token", null), barcode)
                    .enqueue(object :
                        Callback<List<History>> {
                        override fun onFailure(call: Call<List<History>>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                            if (response.code().toString() == "200" && response.body()!!.size == 1) {
//                                        Toast.makeText(applicationContext, response.body()!!.toString(), Toast.LENGTH_LONG).show()

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
                                            val intent = Intent(context, Product::class.java)
                                            var products: Products = response.body()!!
                                            intent.putExtra("Product", products)
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Выбранный товар не найден",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }
                                })

                            }
                            else if(response.code().toString() == "200" && response.body()!!.size > 1)
                            {

                                try {
                                    val intent = Intent(context, select_product::class.java)
                                    intent.putExtra("Product", barcode)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                }catch (e:Exception){
                                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            else {
                                Toast.makeText(applicationContext, "Выбранный товар не найден", Toast.LENGTH_SHORT)
                                    .show()

                            }
                        }
                    })
                edittext.setText("")
            }
        }catch (e:Exception){
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT)
                .show()
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
                        if(!data.isBlank()){
//                          
                            serviceApi.search("Bearer " + sharedPreferences.getString("Token", null), data.trim())
                                .enqueue(object :
                                    Callback<List<History>> {
                                    override fun onFailure(call: Call<List<History>>, t: Throwable) {
                                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                                    }

                                    override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {
                                        if (response.code().toString() == "200" && response.body()!!.size == 1) {
//                                        Toast.makeText(applicationContext, response.body()!!.toString(), Toast.LENGTH_LONG).show()

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
                                                        val intent = Intent(context, Product::class.java)
                                                        var products: Products = response.body()!!
                                                        intent.putExtra("Product", products)
                                                        stopBarcodeScanner()
                                                        context.startActivity(intent)
                                                    } else {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "Выбранный товар не найден",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    }
                                                }
                                            })

                                        }
                                        else if(response.code().toString() == "200" && response.body()!!.size > 1)
                                        {

                                            try {
                                                val intent = Intent(context, select_product::class.java)
                                                intent.putExtra("Product", data.trim())
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                stopBarcodeScanner()
                                                startActivity(intent)
                                            }catch (e:Exception){
                                                Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG)
                                                    .show()
                                            }
                                        }
                                        else {
                                            Toast.makeText(applicationContext, "Выбранный товар не найден", Toast.LENGTH_SHORT)
                                                .show()

                                        }
                                    }
                                })
                        }

//                            Toast.makeText(context,"Scanned data: ${data}",Toast.LENGTH_SHORT).show()

                    }

                })
        }catch (e:Exception){
//            Toast.makeText(context,"Scanned data: $e",Toast.LENGTH_LONG).show()
        }

    }
    }
