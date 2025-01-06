package com.phinix.androidfinalprojectia.view.activities.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.phinix.androidfinalprojectia.R

/**
 * Fragmento que contiene la barra superior de la interfaz, donde se encuentra el enlace al repositorio y otros fragmentos.
 */
class TopBarFragment : Fragment() {

    // Botón para abrir el URL del repositorio.
    private lateinit var openUrlButton: ImageButton

    private val userFragment = UserFragment()
    private val geminiModelFragment = GeminiModelFragment()

    /**
     * Inflamos la vista del fragmento y configuramos el enlace y los fragmentos secundarios.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_bar, container, false)

        // Configuración del botón para abrir el enlace al repositorio de GitHub.
        openUrlButton = view.findViewById(R.id.openUrlButton)
        openUrlButton.setOnClickListener {
            val url = "https://github.com/lPhiNix/MobileAppGeminiChat"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        // Transacción de fragmentos para mostrar UserFragment y GeminiModelFragment
        childFragmentManager.beginTransaction()
            .replace(R.id.userFragmentContainer, userFragment)
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.modelFragmentContainer, geminiModelFragment)
            .commit()

        return view
    }

    fun getUserFragment(): UserFragment {
        return userFragment
    }
}