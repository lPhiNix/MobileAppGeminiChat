package com.phinix.androidfinalprojectia.view.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phinix.androidfinalprojectia.R

class TopBarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_bar, container, false)

        // Añadir los fragmentos existentes al contenedor de la cabecera
        val userFragment = UserFragment()
        val geminiModelFragment = GeminiModelFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.userFragmentContainer, userFragment)
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.modelFragmentContainer, geminiModelFragment)
            .commit()

        return view
    }
}