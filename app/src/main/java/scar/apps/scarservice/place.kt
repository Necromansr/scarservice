package scar.apps.scarservice

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scar.apps.scarservice.Data.ApiService
import scar.apps.scarservice.Data.DataSelectAdapter
import scar.apps.scarservice.Data.History
import scar.apps.scarservice.Data.Products

class place : AppCompatActivity() {
    val serviceApi = ApiService.create()
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        sharedPreferences = getSharedPreferences("UserActivity", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        context = this@place
        var btn_ok = findViewById<Button>(R.id.ok_place)
        var btn_cancel = findViewById<Button>(R.id.cancel_place)
        var edit_row = findViewById<EditText>(R.id.row)
        var edit_rack= findViewById<EditText>(R.id.rack)
        var edit_shelf = findViewById<EditText>(R.id.shelf)
        var title = findViewById<TextView>(R.id.title)
        var back = findViewById<TextView>(R.id.back)


        back.setOnClickListener {
            finish()
        }
        title.setText("Ввод места")
        edit_row.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                    if (edit_row.hasFocus()) {
                        if (edit_row.text.count() == 2)
                            edit_rack.requestFocus()
                    }
                }


                override fun afterTextChanged(editable: Editable) {
                    if (edit_row.hasFocus()) {
                        if (!edit_row.text.isEmpty()) {
                            if (edit_row.text.isDigitsOnly())
                                edit_row.error = "Поле должно содержать только буквы"
                        }
                    }
                }
            })
        edit_rack.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                    if (edit_rack.hasFocus()) {
                        if (edit_rack.text.count() == 2)
                            edit_shelf.requestFocus()
                    }
                }


                override fun afterTextChanged(editable: Editable) {}
            })
        btn_ok.setOnClickListener {
            var types = intent.extras?.get("type") as String
            if(types=="product"){

                var place:String = edit_row.text.toString().toUpperCase()+"-"+ edit_rack.text.toString()+"-"+ edit_shelf.text.toString()


                try {

                    serviceApi.place("Bearer " + sharedPreferences.getString("Token", null), place)
                        .enqueue(object :
                            Callback<List<String>> {
                            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {

                                if (response.code().toString() == "200" && response.body()!!.size==1) {
                                        val intent = Intent(this@place, Product::class.java)
                                        intent.putExtra("place",  place.trimEnd('-'))
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                }
                                else if (response.code().toString() == "200" && response.body()!!.size>1){
                                    val intent = Intent(baseContext, select_place::class.java)
                                    intent.putExtra("type", "product")
                                    intent.putExtra("place", place.trimEnd('-'))
                                    startActivityForResult(intent, 2)

                                }else
                                {
                                    Toast.makeText(applicationContext, "Место не найдено", Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

                }

            }else{
                var place:String = edit_row.text.toString().toUpperCase()+"-"+ edit_rack.text.toString()+"-"+ edit_shelf.text.toString()


                try {

                    serviceApi.place("Bearer " + sharedPreferences.getString("Token", null), place)
                        .enqueue(object :
                            Callback<List<String>> {
                            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {

                                if (response.code().toString() == "200" && response.body()!!.size==1) {
                                    val intent = Intent(this@place, product_stock::class.java)
                                    intent.putExtra("place",  place.trimEnd('-'))
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                                else if (response.code().toString() == "200" && response.body()!!.size>1){
                                    val intent = Intent(baseContext, select_place::class.java)
                                    intent.putExtra("type", "product_stock")
                                    intent.putExtra("place",  place.trimEnd('-'))
                                    startActivityForResult(intent, 2)

                                }else
                                {
                                    Toast.makeText(applicationContext, "Место не найдено", Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()

                }

            }

        }
        btn_cancel.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data!!.extras?.get("place") as String
                var types = intent.extras?.get("type") as String
                if(types=="product"){
                    val intent = Intent(this@place, Product::class.java)
                    intent.putExtra("place",  place.trimEnd('-'))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else{
                    val intent = Intent(this@place, product_stock::class.java)
                    intent.putExtra("place",  place.trimEnd('-'))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
}
