package com.phinix.androidfinalprojectia.view.activities.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.phinix.androidfinalprojectia.R

class TopBarFragment : Fragment() {

    private lateinit var openUrlButton: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_bar, container, false)

        val userFragment = UserFragment()
        val geminiModelFragment = GeminiModelFragment()

        openUrlButton = view.findViewById(R.id.openUrlButton)
        openUrlButton.setOnClickListener {
            val url = "https://github.com/lPhiNix/MobileAppGeminiChat"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.userFragmentContainer, userFragment)
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.modelFragmentContainer, geminiModelFragment)
            .commit()

        return view
    }
}