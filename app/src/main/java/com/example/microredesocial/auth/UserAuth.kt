package com.example.microredesocial.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserAuth {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
    fun getCurrentUserEmail(): String? = firebaseAuth.currentUser?.email

    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun updatePassword(
        newPassword: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.currentUser?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            } ?: onComplete(false, "Usuário não autenticado")
    }
}