package scar.apps.scarservice

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scar.apps.scarservice.Data.ApiService
import scar.apps.scarservice.Data.DataSelectAdapter
import scar.apps.scarservice.Data.DataSelectProductAdapter
import scar.apps.scarservice.Data.History

class select_product : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val serviceApi = ApiService.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_product)
        var context = this@select_product
        val recyclerView: RecyclerView = findViewById(R.id.list)
        sharedPreferences = getSharedPreferences("UserActivity", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        var products = intent.extras?.get("Product") as String
        var back = findViewById<TextView>(R.id.back)

        back.setOnClickListener {
            finish()
        }
        var title = findViewById<TextView>(R.id.title)
        title.setText("Выбор товара")

        try {
//            Toast.makeText(applicationContext,products, Toast.LENGTH_LONG).show()

            serviceApi.search("Bearer " + sharedPreferences.getString("Token", null), products)
                .enqueue(object :
                    Callback<List<History>> {
                    override fun onFailure(call: Call<List<History>>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<List<History>>, response: Response<List<History>>) {

                        try {
//                            Toast.makeText(applicationContext, response.body()!![0].toString(), Toast.LENGTH_LONG).show()

                            var adapter = DataSelectAdapter(
                                context,
                                response.body()!!,
                                sharedPreferences.getString("Token", null)!!
                            )
                            recyclerView.adapter = adapter
                        } catch (e: Exception) {
                            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                })
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

        }
    }
}




