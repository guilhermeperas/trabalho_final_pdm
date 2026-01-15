package com.example.grupo_pdm.ui.user.signInFragment

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.grupo_pdm.R

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        
        val forgotPasswordTextView: TextView = view.findViewById(R.id.forgot_password_text_view)
        forgotPasswordTextView.paintFlags = forgotPasswordTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        
        return view
    }
}