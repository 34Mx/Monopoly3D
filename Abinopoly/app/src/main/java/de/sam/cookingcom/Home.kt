package de.sam.cookingcom

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import de.sam.cookingcom.handler.DataHandler

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val listView: ListView = view.findViewById(R.id.listView)
        listView.divider = null

        val listItem = mutableListOf<Item>()

        // Read JSON data
        val recipeData = DataHandler().readJSON(requireContext())

        // Iterate over the keys (timestamps)
        val keys = recipeData.keys()
        while (keys.hasNext()) {
            val timestamp = keys.next()
            val jsonObject = recipeData.getJSONObject(timestamp)

            // Get the other data (name, description)
            val name = jsonObject.getString("name")
            val description = jsonObject.getString("description")



            val bitmap = DataHandler().loadImageFromId(timestamp, requireContext())
            if (bitmap != null) listItem.add(Item(timestamp, bitmap, name, description))
            else listItem.add(Item(timestamp, BitmapFactory.decodeResource(resources, R.drawable.logoblue), name, description))
        }

        val adapter = ItemAdapter(requireContext(), listItem)
        listView.adapter = adapter

        return view
    }


    data class Item(val id: String, val imageBitmap: Bitmap, val title: String, val description: String, var isFavorite: Boolean = false)


    class ItemAdapter(context: Context, private val itemList: MutableList<Item>) :
        ArrayAdapter<Item>(context, 0, itemList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.list_item, parent, false)

            val item = itemList[position]

            val imageView = view.findViewById<ImageView>(R.id.item_image)
            val titleTextView = view.findViewById<TextView>(R.id.item_title)
            val descriptionTextView = view.findViewById<TextView>(R.id.item_description)
            val heartImageView = view.findViewById<ImageView>(R.id.favoriteHeart)  // Heart icon for favorite toggle

            imageView.setImageBitmap(item.imageBitmap)
            titleTextView.text = item.title
            descriptionTextView.text = item.description

            // Load current favorite state
            val isFavorite = DataHandler().loadFavorites(context).contains(item.id)
            heartImageView.setImageResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite)

            // Toggle favorite on click
            heartImageView.setOnClickListener {
                if (isFavorite) {
                    DataHandler().removeFavorite(item.id, context)
                    heartImageView.setImageResource(R.drawable.ic_favorite)
                } else {
                    DataHandler().addFavorite(item.id, context)
                    heartImageView.setImageResource(R.drawable.ic_favorite_filled)
                }

                // Reload the list and notify the adapter about the change
                updateItemList()
                notifyDataSetChanged()
            }

            // Handle item click if needed
            view.setOnClickListener {
                Toast.makeText(context, "Clicked item with ID: ${item.id}", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, NewEntry::class.java)
                intent.putExtra("ITEM_ID", item.id)
                context.startActivity(intent)
            }

            return view
        }

        // Helper function to update the list based on the current favorites
        private fun updateItemList() {
            // Update the item list with the latest favorites
            val favoriteIds = DataHandler().loadFavorites(context)

            // Update the itemList by checking if the item is in the favorites list
            for (item in itemList) {
                val isFavorite = favoriteIds.contains(item.id)
                item.isFavorite = isFavorite  // Update the item’s favorite state
            }
        }
    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
