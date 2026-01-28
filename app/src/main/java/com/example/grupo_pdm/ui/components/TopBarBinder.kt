import androidx.fragment.app.Fragment
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import com.example.grupo_pdm.NavGraphDirections
import com.example.grupo_pdm.ui.components.TopBarView

/**
 * Extensão para Fragment: configura a navegação padrão do TopBar.
 *
 * Objetivo:
 * - Evitar repetir o mesmo código em todos os fragments.
 * - Sempre que um fragment tiver um TopBarView, basta chamar:
 *
 *      bindTopBarNavigation(topBar)
 *
 * E passa a ter:
 * - Search -> abre o SearchPage com a query
 * - Home   -> navega para MainPage
 * - User   -> navega para SignIn/Profile (depende do seu fluxo)
 */

fun Fragment.bindTopBarNavigation(topBar: TopBarView) {

    topBar.setOnSearchListener { query ->
        // Normaliza a pesquisa:
        // - trim() remove espaços no início/fim
        val q = query.trim()
        // Se estiver vazia, não faz nada (evita chamadas / navegações inúteis)
        if (q.isBlank()) return@setOnSearchListener

        // Navegação para o SearchPage usando uma ACTION GLOBAL.
        // "Global action" = pode ser chamada a partir de qualquer fragment, sem precisar
        // de criar ações específicas em cada destino.
        //
        // IMPORTANTE:
        // Aqui usamos NavGraphDirections (gerado pelo Safe Args)
        // e não MainPageDirections, porque esta ação é global.
        findNavController().navigate(
            NavGraphDirections.actionGlobalSearchPage(q)
        )
    }

    topBar.setOnHomeClickListener {
        // navegar para Home
        findNavController().navigate(
            NavGraphDirections.actionGlobalMainPage())
    }

    topBar.setOnUserClickListener {
        //navegar para Perfil
        findNavController().navigate(NavGraphDirections.actionGlobalSignInFragment())
    }
}