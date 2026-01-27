package com.example.grupo_pdm.ui.components

import androidx.fragment.app.Fragment
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import com.example.grupo_pdm.NavGraphDirections

fun Fragment.bindTopBarNavigation(topBar: TopBarView) {

    topBar.setOnSearchListener { query ->
        val q = query.trim()
        if (q.isBlank()) return@setOnSearchListener

        // Global action do nav_graph (não é MainPageDirections)
        findNavController().navigate(
            NavGraphDirections.actionGlobalSearchPage(q)
        )
    }

    topBar.setOnHomeClickListener {
        // exemplo: navegar para Home (se você tiver action global)
        findNavController().navigate(
            NavGraphDirections.actionGlobalMainPage())
    }

    topBar.setOnUserClickListener {
        // exemplo: navegar para Perfil/Login
        //findNavController().navigate(NavGraphDirections.actionGlobalSignInFragment())
    }
}
