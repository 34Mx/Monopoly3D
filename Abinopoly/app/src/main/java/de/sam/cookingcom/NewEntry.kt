package de.sam.cookingcom

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.sam.cookingcom.handler.DataHandler
import org.json.JSONObject

class NewEntry : AppCompatActivity() {

    private val IMAGE_REQUEST_CODE = 100
    private var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val id = intent.getStringExtra("ITEM_ID")


        window.navigationBarColor = 0xffffff
        setContentView(R.layout.activity_new_entry)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        val imageAdd = findViewById<View>(R.id.coverImage) as ImageView
        val recipeList: EditText = findViewById(R.id.recipelist)
        val recipeDescription: EditText = findViewById(R.id.recipedescription)
        val recipeInstructions: EditText = findViewById(R.id.recipeinstructions)
        val recipeName: EditText = findViewById(R.id.recipename)

        imageAdd.setOnClickListener {view ->

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        recipeList.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed here
            }

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return // Prevent recursive calls
                isUpdating = true

                s?.let {
                    val text = it.toString()
                    if (text.contains("- ")) {
                        val updatedText =
                            text.replace("- ", "\t\t\u2022 ") // Replace with a bullet point
                        recipeList.setText(updatedText)
                        recipeList.setSelection(updatedText.length) // Move cursor to the end
                    }
                }

                isUpdating = false
            }
        })


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            val contentRecipeList = recipeList.text.toString()
            val contentRecipeDescription = recipeDescription.text.toString()
            val contentRecipeInstructions = recipeInstructions.text.toString()
            val contentRecipeName = recipeName.text.toString()

            if (contentRecipeName.isEmpty() || contentRecipeList.isEmpty() || contentRecipeInstructions.isEmpty()) {
                Toast.makeText(this, "Bitte fülle alle Felder aus.", Toast.LENGTH_LONG).show()
                false
            } else {
                Toast.makeText(this, "Rezept wurde gespeichert!", Toast.LENGTH_LONG).show()

                val rootObject = JSONObject()
                rootObject.put("name", contentRecipeName)
                rootObject.put("description", contentRecipeDescription)
                rootObject.put("list", contentRecipeList)
                rootObject.put("instructions", contentRecipeInstructions)

                val realId = DataHandler().saveJSON(rootObject, this, id)
                if (realId != null) DataHandler().saveImage(imageAdd.drawable.toBitmap(), realId, this)
                startActivity(Intent(this, MainCanvas::class.java))


                true
            }
        }

        if (id != null) {
            val recipeData = DataHandler().loadEntryFromID(id, this);
            if (recipeData != null) {
                val name = recipeData.getString("name")
                val description = recipeData.getString("description")
                val list = recipeData.getString("list")
                val instructions = recipeData.getString("instructions")

                recipeName.setText(name)
                recipeDescription.setText(description)
                recipeList.setText(list)
                recipeInstructions.setText(instructions)
            }

            val bitmap = DataHandler().loadImageFromId(id, this)

            if (bitmap != null) {
                // Successfully loaded the image
                val imageView = findViewById<ImageView>(R.id.coverImage)
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                imageURI = it
                findViewById<ImageView>(R.id.coverImage).setImageURI(it)
            }
        }
    }
}