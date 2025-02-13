package de.sam.cookingcom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils.replace
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.sam.cookingcom.databinding.ActivityCanvasBinding
import de.sam.cookingcom.databinding.ActivityMainBinding

class MainCanvas : AppCompatActivity() {

    private lateinit var binding : ActivityCanvasBinding
    private val REQUEST_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = 0xffffff


        binding = ActivityCanvasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.favorites -> replaceFragment(Favorites())
                R.id.add -> requestPerms()
                else -> {

                }
            }
            true
        }
    }

    private fun requestPerms () {
        startActivity(Intent(this, NewEntry::class.java))
    }


    private fun replaceFragment (fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}