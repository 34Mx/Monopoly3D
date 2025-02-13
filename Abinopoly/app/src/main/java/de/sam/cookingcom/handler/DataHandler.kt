package de.sam.cookingcom.handler

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.Writer


class DataHandler {
    public fun saveJSON(jsonObject: JSONObject, context: Context, timestamp: String?=null): String? {
        // Use app-specific external storage directory
        val folder = File(context.getExternalFilesDir(null), "cookingcom")

        // Ensure folder exists, if not, create it
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // Create or get the existing file
        val file = File(folder, "CookingComData.json")
        if (!file.exists()) {
            file.createNewFile()
        }

        try {
            // Read existing data if any
            val existingData = if (file.length() > 0) {
                JSONObject(file.readText())
            } else {
                JSONObject()  // If the file is empty, create a new JSONObject
            }

            // Get current timestamp as key for new recipe
            var actualTimestamp = timestamp;
            if (timestamp == null) actualTimestamp = System.currentTimeMillis().toString()
            existingData.put(actualTimestamp.toString(), jsonObject)

            // Write the updated JSON object to the file
            val writer: Writer = BufferedWriter(FileWriter(file))
            writer.write(existingData.toString())
            writer.close()

            return actualTimestamp

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }



    fun readJSON(context: Context): JSONObject {
        val folder = File(context.getExternalFilesDir(null), "cookingcom")
        val file = File(folder, "CookingComData.json")

        // Check if file exists and is not empty
        if (!file.exists() || file.length() == 0L) {
            return JSONObject()  // Return empty JSONObject if no file or file is empty
        }

        try {
            // Read the file contents and convert it to a JSONObject
            val jsonString = file.readText()
            return JSONObject(jsonString)  // Return the JSONObject from the file content
        } catch (e: IOException) {
            e.printStackTrace()
            return JSONObject()  // Return empty JSONObject in case of error
        }
    }

    public fun loadEntryFromID(id: String, context: Context): JSONObject? {
        var entry: JSONObject? = null
        if (isExternalStorageWritable()) {
            val folder = File(context.getExternalFilesDir(null), "cookingcom")
            val file = File(folder.absolutePath, "CookingComData.json")

            if (file.exists()) {
                try {
                    val inputStream = FileInputStream(file)
                    val inputString = inputStream.bufferedReader().use { it.readText() }
                    val currentData = JSONObject(inputString)

                    // Check if the entry exists with the given ID (timestamp as key)
                    if (currentData.has(id)) {
                        entry = currentData.getJSONObject(id)
                    }

                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return entry
    }

    fun saveImage(image: Bitmap, imageName: String, context: Context): Boolean {
        if (isExternalStorageWritable()) {
            val folder = File(context.getExternalFilesDir(null), "cookingcom/images")
            if (!folder.exists()) {
                folder.mkdirs()  // Create the directory if it doesn't exist
            }

            val file = File(folder, "$imageName.png")
            return try {
                val outputStream = FileOutputStream(file)
                image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
        return false
    }


    fun loadImageFromId(imageId: String, context: Context): Bitmap? {
        val folder = File(context.getExternalFilesDir(null), "cookingcom/images")
        val file = File(folder, "$imageId.png")

        return if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            null
        }
    }

    private val sharedPrefFile = "com.sam.cookingcom.FAVORITES"

    // Save favorite items
    fun saveFavorites(favorites: List<String>, context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val jsonArray = JSONArray(favorites)
        editor.putString("favorites", jsonArray.toString())
        editor.apply()
    }

    // Load favorite items
    fun loadFavorites(context: Context): List<String> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val favoritesString = sharedPreferences.getString("favorites", "[]")
        val jsonArray = JSONArray(favoritesString)
        val favorites = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            favorites.add(jsonArray.getString(i))
        }
        return favorites
    }

    // Add a favorite item
    fun addFavorite(favoriteId: String, context: Context) {
        val favorites = loadFavorites(context).toMutableList()
        if (!favorites.contains(favoriteId)) {
            favorites.add(favoriteId)
        }
        saveFavorites(favorites, context)
    }

    // Remove a favorite item
    fun removeFavorite(favoriteId: String, context: Context) {
        val favorites = loadFavorites(context).toMutableList()
        favorites.remove(favoriteId)
        saveFavorites(favorites, context)
    }





    private fun isExternalStorageWritable(): Boolean {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            System.out.println("Storage is writeable")
            return true
        }
        return false
    }
}