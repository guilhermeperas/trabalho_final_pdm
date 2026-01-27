package com.example.grupo_pdm.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageButton
import com.example.grupo_pdm.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TopBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    // Referências para os botões e campo de pesquisa que existem no XML (view_top_bar.xml)

    private val btnHome: ImageButton
    private val btnUser: ImageButton
    private val tilSearch: TextInputLayout
    private val etSearch: TextInputEditText

    // Callbacks configuráveis (os Fragments “ligam” estes listeners
    private var onHomeClick: (() -> Unit)? = null
    private var onUserClick: (() -> Unit)? = null
    private var onSearch: ((String) -> Unit)? = null

    init {
        // 1) Inflar (carregar) o layout XML do TopBar dentro deste componente
        // - view_top_bar.xml tem os botões e a TextInputLayout
        // - "this" é o FrameLayout (TopBarView)
        // - inflate(..., this, false) cria a View mas ainda não a adiciona
        val v = LayoutInflater.from(context).inflate(R.layout.view_top_bar, this, false)
        addView(v)


        // 2) Encontrar as views do XML (a partir do "v")
        // Importante: procurar dentro do "v" (a view inflada),
        // não diretamente no "this", para garantir que encontra os IDs corretos.
        btnHome = v.findViewById(R.id.btnHome)
        btnUser = v.findViewById(R.id.btnUser)
        tilSearch = v.findViewById(R.id.tilSearch)
        etSearch = v.findViewById(R.id.etSearch)

        // 3) Ligar eventos (clicks) aos callbacks
        // Botões Home/User chamam as funções que o Fragment definiu
        btnHome.setOnClickListener { onHomeClick?.invoke() }
        btnUser.setOnClickListener { onUserClick?.invoke() }

// Clique no ícone da lupa (end icon) dentro do TextInputLayout
        tilSearch.setEndIconOnClickListener { triggerSearch() }

        // 4) Permitir pesquisar pelo teclado (enter / search / done / go / send)
        etSearch.setOnEditorActionListener { _, actionId, event ->
            val imeAction = actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_SEND
            val enterKey = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                event.action == KeyEvent.ACTION_DOWN
            if (imeAction || enterKey) {
                triggerSearch()
                true
            } else {
                false
            }
        }
    }
    /**
     * Lê o texto atual do EditText, normaliza (trim) e chama o callback onSearch.
     * Este método é usado tanto pelo ícone da lupa como pelo teclado.
     */

    private fun triggerSearch() {
        val q = etSearch.text?.toString().orEmpty().trim()
        onSearch?.invoke(q)
    }

    // API para os fragments
// Define o que acontece quando o utilizador carrega no botão Home
    fun setOnHomeClickListener(listener: (() -> Unit)?) { onHomeClick = listener }

    // Define o que acontece quando o utilizador carrega no botão User
    fun setOnUserClickListener(listener: (() -> Unit)?) { onUserClick = listener }

    // Define o que acontece quando o utilizador faz uma pesquisa
    fun setOnSearchListener(listener: ((String) -> Unit)?) { onSearch = listener }

    // Permite ao Fragment “meter texto” na barra (ex.: query inicial vinda do navArgs)
    fun setSearchText(text: String) { etSearch.setText(text) }

    // Permite ao Fragment ler o texto atual da pesquisa (se precisar)
    fun getSearchText(): String = etSearch.text?.toString().orEmpty()

}


