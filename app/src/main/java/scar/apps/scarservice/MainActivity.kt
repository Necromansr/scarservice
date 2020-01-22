package scar.apps.scarservice

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import scar.apps.scarservice.Data.Products
import scar.apps.scarservice.GeneralScanner.BarcodeScannerConnectionListener
import scar.apps.scarservice.GeneralScanner.BarcodeScannerHelper

class MainActivity : AppCompatActivity() {
    public val userActivity: String = "UserActivity"
    lateinit var context: Context

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        var btn_search = findViewById<Button>(R.id.search)
        var btn_stock = findViewById<Button>(R.id.btn_stock)
        var button2 = findViewById<Button>(R.id.button2)
        var btn_delivery = findViewById<Button>(R.id.btn_delivery)
        var button8 = findViewById<Button>(R.id.button8)
        var button9 = findViewById<Button>(R.id.button9)
        sharedPreferences = getSharedPreferences(userActivity, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        context = this@MainActivity
        try{
            connectScanner()
        }catch (e:Exception){

        }
        var position_t = findViewById<TextView>(R.id.position_t)
        var place_t = findViewById<TextView>(R.id.place_t)
        var name_t = findViewById<TextView>(R.id.name_t)
        position_t.setText(sharedPreferences.getString("Position", null))
        place_t.setText(sharedPreferences.getString("Store", null))
        name_t.setText(sharedPreferences.getString("Name", null))

        btn_search.setOnClickListener {
            val intent = Intent(baseContext, search_product::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        btn_stock.setOnClickListener {
            val intent = Intent(baseContext, product_stock::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        btn_delivery.setOnClickListener {
            val intent = Intent(baseContext, documents_product::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        button2.setOnClickListener {

        }
        try {

            val mDrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            val openNav = this@MainActivity.findViewById<ImageButton>(R.id.nav)

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
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    R.id.receipt -> {
//                        Toast.makeText(baseContext, "Wallet", Toast.LENGTH_LONG).show()
                    }
                    R.id.extradition -> {
                        val intent = Intent(baseContext, documents_product::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    R.id.store ->{
                        val intent = Intent(baseContext, product_stock::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                    R.id.layout -> {
//                        Toast.makeText(baseContext, "Setting", Toast.LENGTH_LONG).show()
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
    }
    private fun connectScanner() {
        if (!BarcodeScannerHelper.getInstance(context).isConnected) {
            BarcodeScannerHelper.getInstance(context).connectScanner(object :
                BarcodeScannerConnectionListener {
                override fun onDisconnected() {
                    Toast.makeText(context,"Connection failed!", Toast.LENGTH_SHORT).show()
                }

                override fun onConnected() {
                    Toast.makeText(context,"Connection success!", Toast.LENGTH_SHORT).show()

                }
            })

        } else {
//            Toast.makeText(context,"You are already connected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun disconnectScanner() {
        BarcodeScannerHelper.getInstance(context).disconnectScanner()
    }
}

