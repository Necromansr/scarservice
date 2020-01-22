package scar.apps.scarservice.Data
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scar.apps.scarservice.Product
import scar.apps.scarservice.R
import scar.apps.scarservice.place
import scar.apps.scarservice.product_stock


public class DataSearchAdapter(context: Context, private val search: List<History>, token:String) :
    RecyclerView.Adapter<DataSearchAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val contexts: Context
    private val token: String

    init {
        this.inflater = LayoutInflater.from(context)
        this.contexts = context
        this.token = token
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = inflater.inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = search[position]
        holder.nameView.setText(items.Description)
    }

    override fun getItemCount(): Int {
        return search.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val nameView: TextView

        init {

            nameView = view.findViewById(R.id.product)
            view.setOnClickListener{
                val serviceApi = ApiService.create()
                serviceApi.product("Bearer " + token,search[adapterPosition].Id).enqueue(object:
                    Callback<Products> {
                    override fun onFailure(call: Call<Products>, t: Throwable) {
                        Toast.makeText(contexts, t.message, Toast.LENGTH_LONG).show()
                    }
                    override fun onResponse(call: Call<Products>, response: Response<Products>) {
                        if(response.code().toString() == "200"){
                            val intent = Intent(contexts, Product::class.java)
                            var products: Products = response.body()!!
                            intent.putExtra("Product", products)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            contexts.startActivity(intent)
                        }
                    }
                })
            }
        }
    }
}

class DataStockAdapter(private var items: ArrayList<Products>, private var places: String): RecyclerView.Adapter<DataStockAdapter.ViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_stock, parent, false)
        context = parent?.context
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var product = items[position]
        holder?.txtName?.text = product.Description
        if(product.Places!=product.Place) {
            var colorValue = ContextCompat.getColor(context, R.color.login)
            holder?.txtStock?.setBackgroundColor(colorValue)
            colorValue = ContextCompat.getColor(context, R.color.Text)
            holder?.txtStock?.setTextColor(colorValue)
        }else{
            holder?.txtStock?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.edit_product_backgroud))
            var colorValue = ContextCompat.getColor(context, R.color.TextGray)
            holder?.txtStock?.setTextColor(colorValue)
        }
        holder?.txtStock?.text = product.Place
        holder?.txtInStock?.text = product.InStock
        holder?.delete_item?.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Удалить ${items[holder.adapterPosition].Sku + " " +items[holder.adapterPosition].Maker + " " + items[holder.adapterPosition].Name}")
            builder.setPositiveButton("Да") { _, _ ->
                items.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
                notifyItemRangeChanged(holder.adapterPosition,itemCount)
                notifyDataSetChanged()
            }

            builder.setNegativeButton("Нет") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }



    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var txtName: TextView? = null
        var txtInStock: TextView? = null
        var txtStock: TextView? = null
        var delete_item: TextView? = null

        init {
            this.txtName = row?.findViewById<TextView>(R.id.viewName)
            this.txtInStock = row?.findViewById<TextView>(R.id.viewInStock)
            this.txtStock = row?.findViewById<TextView>(R.id.viewStock)
            this.delete_item = row?.findViewById<TextView>(R.id.delete_item)

        }
    }
}

class DataDocumentAdapter(private var items: ArrayList<Documents>): RecyclerView.Adapter<DataDocumentAdapter.ViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_delivery, parent, false)
        context = parent?.context
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var document = items[position]
        holder?.txtIndex?.text = (items.size-position).toString()
        holder?.txtDocument?.text = document.Name
        holder?.delete_item?.setOnClickListener {
            val builder = AlertDialog.Builder(context)

                builder.setTitle("Удалить ${items[holder.adapterPosition].Name}")
                builder.setPositiveButton("Да") { _, _ ->

                    items.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                    notifyItemRangeChanged(holder.adapterPosition,itemCount)
                }

                builder.setNegativeButton("Нет") { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()

        }
    }



    override fun getItemCount(): Int {
        return items.size
    }



    class ViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        var txtDocument: TextView? = null
        var txtIndex: TextView? = null
        var delete_item: TextView? = null
        init {
            this.txtDocument = row?.findViewById<TextView>(R.id.document)
            this.txtIndex = row?.findViewById<TextView>(R.id.index)
            this.delete_item = row?.findViewById<TextView>(R.id.delete_item)

        }
    }
}


public class DataSelectAdapter(context: Context, private val search: List<History>, token:String) :
    RecyclerView.Adapter<DataSelectAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val contexts: Context
    private val token: String

    init {
        this.inflater = LayoutInflater.from(context)
        this.contexts = context
        this.token = token
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = inflater.inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = search[position]
        holder.nameView.setText(items.Maker)
    }

    override fun getItemCount(): Int {
        return search.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val nameView: TextView

        init {

            nameView = view.findViewById(R.id.product)
            view.setOnClickListener{
                val serviceApi = ApiService.create()
                serviceApi.product("Bearer " + token,search[adapterPosition].Id).enqueue(object:
                    Callback<Products> {
                    override fun onFailure(call: Call<Products>, t: Throwable) {
                        Toast.makeText(contexts, t.message, Toast.LENGTH_LONG).show()
                    }
                    override fun onResponse(call: Call<Products>, response: Response<Products>) {
                        if(response.code().toString() == "200"){
                            val intent = Intent(contexts, Product::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            var products: Products = response.body()!!
                            intent.putExtra("Product", products)
                            contexts.startActivity(intent)
                        }
                    }
                })
            }
        }
    }
}

public class DataSelectProductAdapter(context: Context, private val search: List<History>, token:String) :
    RecyclerView.Adapter<DataSelectProductAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val contexts: Context
    private val token: String

    init {
        this.inflater = LayoutInflater.from(context)
        this.contexts = context
        this.token = token
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = inflater.inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = search[position]
        holder.nameView.setText(items.Maker)
    }

    override fun getItemCount(): Int {
        return search.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val nameView: TextView

        init {

            nameView = view.findViewById(R.id.product)
            view.setOnClickListener{
                val serviceApi = ApiService.create()
                serviceApi.product("Bearer " + token,search[adapterPosition].Id).enqueue(object:
                    Callback<Products> {
                    override fun onFailure(call: Call<Products>, t: Throwable) {
                        Toast.makeText(contexts, t.message, Toast.LENGTH_LONG).show()
                    }
                    override fun onResponse(call: Call<Products>, response: Response<Products>) {
                        if(response.code().toString() == "200"){
                            val intent = Intent(contexts, product_stock::class.java)
                            var products: Products = response.body()!!
                            intent.putExtra("Product", products)
                            var active: Activity = contexts as Activity
                            active.setResult(RESULT_OK, intent)
                            active.finish()
                        }
                    }
                })
            }
        }
    }
}


public class DataSelectPlaceAdapter(context: Context, private val search: List<String>, token:String,type:String) :
    RecyclerView.Adapter<DataSelectPlaceAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val contexts: Context
    private val token: String
    private val type: String

    init {
        this.inflater = LayoutInflater.from(context)
        this.contexts = context
        this.token = token
        this.type = type

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = inflater.inflate(R.layout.item_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = search[position]
        holder.nameView.setText(items)
    }

    override fun getItemCount(): Int {
        return search.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal val nameView: TextView

        init {

            nameView = view.findViewById(R.id.product)
            view.setOnClickListener {
                var active: Activity = contexts as Activity
                if (type == "product") {
                    val intent = Intent(contexts, place::class.java)
                    intent.putExtra("place", search[adapterPosition])
                    active.setResult(RESULT_OK, intent)
                    active.finish()
                } else {
                    val intent = Intent(contexts, place::class.java)
                    intent.putExtra("place", search[adapterPosition])
                    active.setResult(RESULT_OK, intent)
                    active.finish()
                }
            }
        }
    }
}