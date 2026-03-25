package com.example.microredesocial.auth

import com.google.firebase.auth.FirebaseAuth

class UserAuth {

    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> callback(task.isSuccessful) }
    }

    fun cadastro(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful, task.exception?.message)
            }
    }

    fun getEmailUsuarioLogado(): String? = auth.currentUser?.email

    fun logout() = auth.signOut()
}