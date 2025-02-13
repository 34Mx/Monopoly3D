package de.sam.cookingcom

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import de.sam.cookingcom.handler.DataHandler

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Favorites.newInstance] factory method to
 * create an instance of this fragment.
 */
class Favorites : Fragment() {
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
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        val listView: ListView = view.findViewById(R.id.listView)
        val context = requireContext()

        // Fetch favorite IDs
        val dataHandler = DataHandler()
        val favoriteIds = dataHandler.loadFavorites(context)

        // Fetch item details for each favorite ID
        val favoriteItems = mutableListOf<Home.Item>()
        val recipeData = dataHandler.readJSON(context)

        for (id in favoriteIds) {
            if (recipeData.has(id)) {
                val jsonObject = recipeData.getJSONObject(id)
                val name = jsonObject.getString("name")
                val description = jsonObject.getString("description")
                val bitmap = dataHandler.loadImageFromId(id, context)
                    ?: BitmapFactory.decodeResource(resources, R.drawable.logoblue)

                favoriteItems.add(Home.Item(id, bitmap, name, description))
            }
        }

        // Set up adapter
        val adapter = Home.ItemAdapter(context, favoriteItems)
        listView.adapter = adapter

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Favorites.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Favorites().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}