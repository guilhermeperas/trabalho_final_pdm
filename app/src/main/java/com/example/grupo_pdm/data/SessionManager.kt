package com.example.grupo_pdm.data


object SessionManager {
    var currentUser: LoginResponse? = null

    fun isAdmin(): Boolean {
        return currentUser?.role == "admin"
    }
}