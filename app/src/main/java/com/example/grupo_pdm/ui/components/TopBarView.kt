package com.example.grupo_pdm.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
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

    private val btnHome: ImageButton
    private val btnUser: ImageButton
    private val tilSearch: TextInputLayout
    private val etSearch: TextInputEditText

    private var onHomeClick: (() -> Unit)? = null
    private var onUserClick: (() -> Unit)? = null
    private var onSearch: ((String) -> Unit)? = null

    init {
        // Inflar o XML do topo como uma View separada
        val v = LayoutInflater.from(context).inflate(R.layout.view_top_bar, this, false)
        addView(v)



        // Buscar elementos a partir da view inflada (não do "this")
        btnHome = v.findViewById(R.id.btnHome)
        btnUser = v.findViewById(R.id.btnUser)
        tilSearch = v.findViewById(R.id.tilSearch)
        etSearch = v.findViewById(R.id.etSearch)

        btnHome.setOnClickListener { onHomeClick?.invoke() }
        btnUser.setOnClickListener { onUserClick?.invoke() }

        tilSearch.setEndIconOnClickListener { triggerSearch() }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                triggerSearch()
                true
            } else false
        }
    }


    private fun triggerSearch() {
        val q = etSearch.text?.toString().orEmpty().trim()
        onSearch?.invoke(q)
    }

    // API para os fragments
    fun setOnHomeClickListener(listener: (() -> Unit)?) { onHomeClick = listener }
    fun setOnUserClickListener(listener: (() -> Unit)?) { onUserClick = listener }
    fun setOnSearchListener(listener: ((String) -> Unit)?) { onSearch = listener }

    fun setSearchText(text: String) { etSearch.setText(text) }
    fun getSearchText(): String = etSearch.text?.toString().orEmpty()
}


