package com.example.microredesocial.dao

import com.example.microredesocial.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID

class PostDAO {
    private val db = FirebaseFirestore.getInstance()
    private val collectionName = "posts"

    fun criarPost(
        post: Post,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val postId = UUID.randomUUID().toString()
        val postComId = post.copy(id = postId)

        db.collection(collectionName)
            .document(postId)
            .set(postComId)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun buscarPosts(
        ultimoPost: Post? = null,
        limite: Int = 5,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query = db.collection(collectionName)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .limit(limite.toLong())

        if (ultimoPost != null && ultimoPost.dataCriacao != null) {
            query = query.startAfter(ultimoPost.dataCriacao)
        }

        query.get()
            .addOnSuccessListener { documents ->
                val posts = mutableListOf<Post>()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                onSuccess(posts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun buscarPostsPorCidade(
        cidade: String,
        onSuccess: (List<Post>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Busca por cidade ignorando maiúsculas/minúsculas
        val cidadeLower = cidade.lowercase()

        db.collection(collectionName)
            .orderBy("dataCriacao", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val posts = mutableListOf<Post>()
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    // Filtrar por cidade manualmente (case insensitive)
                    if (post.cidade.lowercase().contains(cidadeLower)) {
                        posts.add(post)
                    }
                }
                onSuccess(posts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}