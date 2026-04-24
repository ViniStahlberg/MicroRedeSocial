package com.example.microredesocial.dao

import com.example.microredesocial.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserDAO {
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "usuarios"

    fun salvarPerfil(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionName)
            .document(user.email)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun buscarPerfil(
        email: String,
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionName)
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSuccess(document.toObject(User::class.java))
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun atualizarPerfil(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionName)
            .document(user.email)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}