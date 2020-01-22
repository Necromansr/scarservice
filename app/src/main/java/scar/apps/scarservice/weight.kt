package scar.apps.scarservice

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class weight : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight)
        var btn_ok = findViewById<Button>(R.id.ok_place)
        var btn_cancel = findViewById<Button>(R.id.cancel_place)
        var edit_weight = findViewById<EditText>(R.id.weight)
        var title = findViewById<TextView>(R.id.title)
        var back = findViewById<TextView>(R.id.back)


        back.setOnClickListener {
            finish()
        }
        title.setText("Ввод веса")
        btn_cancel.setOnClickListener {
            finish()
        }
        btn_ok.setOnClickListener {
            val intent = Intent(this@weight, Product::class.java)
            intent.putExtra("weight",  edit_weight.text.toString().trimEnd('-'))
            setResult(Activity.RESULT_OK, intent)
            finish()        }
    }
}
