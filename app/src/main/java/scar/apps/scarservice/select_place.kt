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
import scar.apps.scarservice.Data.DataSelectPlaceAdapter
import scar.apps.scarservice.Data.DataSelectProductAdapter
import scar.apps.scarservice.Data.History

class select_place : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val serviceApi = ApiService.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_place)
        var context = this@select_place
        val recyclerView: RecyclerView = findViewById(R.id.list)
        sharedPreferences = getSharedPreferences("UserActivity", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        var back = findViewById<TextView>(R.id.back)


        back.setOnClickListener {
            finish()
        }
        var title = findViewById<TextView>(R.id.title)
        title.setText("Выбор места")
        var types = intent.extras?.get("type") as String
        var places = intent.extras?.get("place") as String
        try {
//            Toast.makeText(applicationContext,products, Toast.LENGTH_LONG).show()

            serviceApi.place("Bearer " + sharedPreferences.getString("Token", null), places)
                .enqueue(object :
                    Callback<List<String>> {
                    override fun onFailure(call: Call<List<String>>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {

                        try {
                            var adapter = DataSelectPlaceAdapter(
                                context,
                                response.body()!!,
                                sharedPreferences.getString("Token", null)!!,
                                types
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
