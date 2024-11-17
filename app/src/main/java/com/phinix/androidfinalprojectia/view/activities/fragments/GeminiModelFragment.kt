package com.phinix.androidfinalprojectia.view.activities.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.view.activities.MainActivity

class GeminiModelFragment : Fragment() {
    private lateinit var modelSelectionButton: Button
    private lateinit var modelDropdownMenu: View
    private lateinit var modelGemini15: Button
    private lateinit var modelGemini158b: Button
    private lateinit var modelGemini15pro: Button
    private lateinit var modelGemini10pro : Button

    var selectedModel: String = ""
        private set

    var onModelSelected: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gemini_model, container, false)

        modelSelectionButton = view.findViewById(R.id.modelSelectionButton)
        modelDropdownMenu = view.findViewById(R.id.modelDropdownMenu)
        modelGemini15 = view.findViewById(R.id.modelGemini15)
        modelGemini158b = view.findViewById(R.id.modelGemini158b)
        modelGemini15pro = view.findViewById(R.id.modelGemini15pro)
        modelGemini10pro = view.findViewById(R.id.modelGemini10pro)

        modelSelectionButton.setOnClickListener {
            toggleDropdownMenu()
        }

        modelGemini15.setOnClickListener { updateModel("gemini-1.5-flash") }
        modelGemini158b.setOnClickListener { updateModel("gemini-1.5-flash-8b")}
        modelGemini15pro.setOnClickListener { updateModel("gemini-1.5-pro")}
        modelGemini10pro.setOnClickListener { updateModel("gemini-1.0-pro") }

        return view
    }

    private fun toggleDropdownMenu() {
        modelDropdownMenu.visibility =
            if (modelDropdownMenu.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun updateModel(newModel: String) {
        val mainActivity = activity as? MainActivity
        mainActivity?.setModelName(newModel)

        selectedModel = newModel
        modelSelectionButton.text = newModel
        modelDropdownMenu.visibility = View.GONE
        onModelSelected?.invoke(newModel)
    }
}