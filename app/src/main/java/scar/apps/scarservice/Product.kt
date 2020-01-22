package scar.apps.scarservice

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import scar.apps.scarservice.Data.ApiService
import scar.apps.scarservice.Data.ErrorMessage
import scar.apps.scarservice.Data.Products
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scar.apps.scarservice.Data.History
import scar.apps.scarservice.GeneralScanner.BarcodeDataListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerConnectionListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper

class Product : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var Name:TextView
    lateinit var Sku:TextView
    lateinit var Maker:TextView
    lateinit var InStock:TextView
    lateinit var Place:TextView
    lateinit var Weight:TextView
    lateinit var context:Context
    val serviceApi = ApiService.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        sharedPreferences = getSharedPreferences("UserActivity", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        Name = findViewById<TextView>(R.id.Name)
        Sku = findViewById<TextView>(R.id.Sku)
        Maker = findViewById<TextView>(R.id.Maker)
        InStock = findViewById<TextView>(R.id.InStock)
        Place = findViewById<TextView>(R.id.Place)
        Weight = findViewById<TextView>(R.id.Weight)
        var weightBtn = findViewById<Button>(R.id.weight)
        var placeBtn = findViewById<Button>(R.id.place)
        var printBtn = findViewById<Button>(R.id.print)
        var printsBtn = findViewById<Button>(R.id.prints)
        var back = findViewById<TextView>(R.id.back)
        var new_search = findViewById<TextView>(R.id.search_new)
        context = this@Product
        Place.setOnClickListener {
            val intent = Intent(baseContext, place::class.java)
            intent.putExtra("type", "product")
            startActivityForResult(intent, 2)
        }
        Weight.setOnClickListener {
            val intent = Intent(baseContext, weight::class.java)
            startActivityForResult(intent, 3)
        }
        new_search.setOnClickListener {
            val intent = Intent(baseContext, search_product::class.java)
            startActivity(intent)
        }

        back.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }

        try {

            val mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
//            val openNav = this@Product.findViewById<ImageButton>(R.id.nav)

//            openNav.setOnClickListener {
//                mDrawerLayout.openDrawer(Gravity.RIGHT)
//            }
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
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    R.id.receipt -> {
                        Toast.makeText(baseContext, "Wallet", Toast.LENGTH_LONG).show()
                    }
                    R.id.extradition -> {
                        val intent = Intent(baseContext, documents_product::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    R.id.layout -> {
                        Toast.makeText(baseContext, "Setting", Toast.LENGTH_LONG).show()
                    }
                    R.id.logout -> {
                        editor.putString("Name", null)
                        editor.putString("Position",null)
                        editor.putString("Store", null)
                        editor.putString("Token",null)
                        editor.commit()
                        val intent = Intent(baseContext, login::class.java)
                        startActivity(intent)
                    }
                }
                // Add code here to update the UI based on the item selected
                // For app, swap UI fragments here

                true
            }
        }catch (e:Exception)
        {
            Toast.makeText(baseContext,e.toString(),Toast.LENGTH_SHORT).show()

        }

        var products = intent.extras?.get("Product") as Products
        setText(products)
        val serviceApi = ApiService.create()

        printBtn.setOnClickListener {


            serviceApi.print("Bearer " + sharedPreferences.getString("Token", null),products.Id).enqueue(object:
                Callback<ErrorMessage> {
                override fun onFailure(call: Call<ErrorMessage>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ErrorMessage>, response: Response<ErrorMessage>) {
                    if(response.code().toString() == "204"){

                        Toast.makeText(baseContext, "Ярлык напечатан успешно", Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(baseContext, response.body()?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        placeBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            if(TextUtils.isEmpty(Place.text)){

                builder.setTitle("Удалить ли складское место?")
                builder.setPositiveButton("Да") { _, _ ->
                        serviceApi.place("Bearer " + sharedPreferences.getString("Token", null),products.Id,"").enqueue(object:
                            Callback<Products> {
                            override fun onFailure(call: Call<Products>, t: Throwable) {
                                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                                if(response.code().toString() == "200"){

                                    products = response.body()!!
                                    setText(products)

                                }
                                else{
                                    Toast.makeText(baseContext, "", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    Place.clearFocus()

                }

                builder.setNegativeButton("Нет") { _, _ ->
                    Place.setText(products.Place)
                    Place.clearFocus()

                }
                builder.show()
            }else{
                if(products.Place!=Place.text) {
                    builder.setTitle("Изменить ли складское место ${products.Place} на ${Place.text}?")
                    builder.setPositiveButton("Да") { _, _ ->
                        try {

                            serviceApi.place(
                                "Bearer " + sharedPreferences.getString("Token", null),
                                products.Id,
                                Place.text.toString()
                            ).enqueue(object :
                                Callback<Products> {
                                override fun onFailure(call: Call<Products>, t: Throwable) {
                                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call<Products>, response: Response<Products>) {
                                    if (response.code().toString() == "200") {

                                        products = response.body()!!
                                        setText(products)

                                    } else {
                                        Toast.makeText(baseContext, "", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        } catch (e: Exception) {
                            Toast.makeText(baseContext, "error: " + e.toString(), Toast.LENGTH_LONG).show()
                        }

                        Place.clearFocus()

                    }

                    builder.setNegativeButton("Нет") { _, _ ->
                        Place.setText(products.Place)
                        Place.clearFocus()
                    }
                    builder.show()
                }
            }
        }
        weightBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            if(TextUtils.isEmpty(Weight.text)){
                builder.setTitle("Удалить ли вес?")
                builder.setPositiveButton("Да") { _, _ ->
                    serviceApi.weight("Bearer " + sharedPreferences.getString("Token", null),products.Id,0.0).enqueue(object:
                        Callback<Products> {
                        override fun onFailure(call: Call<Products>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<Products>, response: Response<Products>) {
                            if(response.code().toString() == "200"){

                               products = response.body()!!
                               setText(products)

                            }
                            else{
                                Toast.makeText(baseContext, "", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    Weight.clearFocus()

                }

                builder.setNegativeButton("Нет") { _, _ ->
                    Weight.setText(products.WeightSharp.toString())
                    Weight.clearFocus()

                }
                builder.show()

            }else {
                if (products.WeightSharp != Weight.text.toString().toDouble()) {
                    builder.setTitle("Изменить ли вес ${products.WeightSharp} на ${Weight.text}?")
                    builder.setPositiveButton("Да") { _, _ ->
                        serviceApi.weight(
                            "Bearer " + sharedPreferences.getString("Token", null),
                            products.Id,
                            Weight.text.toString().toDouble()
                        ).enqueue(object :
                            Callback<Products> {
                            override fun onFailure(call: Call<Products>, t: Throwable) {
                                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<Products>, response: Response<Products>) {
                                if (response.code().toString() == "200") {

                                    products = response.body()!!
                                    setText(products)

                                } else {
                                    Toast.makeText(baseContext, "", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                        Weight.clearFocus()
                    }

                    builder.setNegativeButton("Нет") { _, _ ->
                        Weight.setText(products.WeightSharp.toString())
                        Weight.clearFocus()
                    }
                    builder.show()
                }
            }
        }
        printsBtn.setOnClickListener {
            val mChoose = arrayOf("1", "2", "3", "5")
            val builder = AlertDialog.Builder(this@Product)
            builder.setTitle("Выберите количество этикеток")
                .setCancelable(false)

                .setNeutralButton("",
                    DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

                .setSingleChoiceItems(mChoose, -1,
                    DialogInterface.OnClickListener { dialog, item ->
                        serviceApi.prints("Bearer " + sharedPreferences.getString("Token", null),products.Id,mChoose[item]).enqueue(object:
                Callback<ErrorMessage> {
                override fun onFailure(call: Call<ErrorMessage>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ErrorMessage>, response: Response<ErrorMessage>) {
                    if(response.code().toString() == "204"){

                        Toast.makeText(baseContext, "Ярлыки напечатаны успешно", Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(baseContext, response.body()?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
                        dialog.cancel()
                    })

            val alert = builder.create()
            alert.show()
        }

    }
    fun setText(products: Products){
        Name.setText(products.Name)
        Sku.setText(products.Sku)
        Maker.setText(products.Maker)
        InStock.setText(products.InStock)
        Place.setText(products.Place)
        Weight.setText(products.WeightSharp.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data!!.extras?.get("place") as String
                Place.setText(place)
            }
        }
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                val weight = data!!.extras?.get("weight") as String
                Weight.setText(weight)
            }
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
                        if(!data.isBlank()) {
                            if (data.take(4) == "2014") {
                                serviceApi.place("Bearer " + sharedPreferences.getString("Token", null), data.trim()).enqueue(object: Callback<List<String>>{
                                    override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                                    }

                                    override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                        if(response.code().toString() == "200"){
                                            Place.setText(response.body()!![0])
                                        } else{
                                            Toast.makeText(applicationContext,"Место не найдено", Toast.LENGTH_LONG).show()

                                        }
                                    }
                                })
                            } else {
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
