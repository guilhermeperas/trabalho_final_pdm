package com.example.grupo_pdm.ui.movie.searchPageScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.grupo_pdm.R
import com.example.grupo_pdm.ui.components.TopBarView
import com.example.grupo_pdm.ui.components.bindTopBarNavigation

class SearchPage : Fragment(R.layout.fragment_search_page) {
    private val args: SearchPageArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "Query: [${args.query}]", Toast.LENGTH_LONG).show()

        val q = args.query
        // Só para testar: meter o texto na barra
        val topBar = view.findViewById<TopBarView>(R.id.topBar)
        topBar.setSearchText(q)
        topBar.setOnSearchListener { text ->
            val query = text.trim()
            Toast.makeText(requireContext(), "Nova pesquisa: [$query]", Toast.LENGTH_LONG).show()
        }

    }

}
