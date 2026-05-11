// Student Reg No: 2501529
package com.example.a2501529_lecturereactiontracker.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a2501529_lecturereactiontracker.R
import com.google.android.material.button.MaterialButton

class FakeAdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fake_ad)

        // Close button for close the ad
        findViewById<MaterialButton>(R.id.btnCloseAd).setOnClickListener {
            finish()
        }

    }
}