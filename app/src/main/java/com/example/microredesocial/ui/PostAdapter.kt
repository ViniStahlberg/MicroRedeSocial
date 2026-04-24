package com.example.microredesocial.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.microredesocial.databinding.ItemPostBinding
import com.example.microredesocial.model.Post
import com.example.microredesocial.utils.Base64Converter
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private val posts: MutableList<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun atualizarPosts(novosPosts: List<Post>) {
        posts.clear()
        posts.addAll(novosPosts)
        notifyDataSetChanged()
    }

    fun adicionarPosts(novosPosts: List<Post>) {
        val posicaoInicial = posts.size
        posts.addAll(novosPosts)
        notifyItemRangeInserted(posicaoInicial, novosPosts.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.txtUsername.text = post.autorUsername
            binding.txtTexto.text = post.texto
            binding.txtData.text = dateFormat.format(post.dataCriacao)

            if (post.cidade.isNotEmpty()) {
                binding.txtCidade.text = "📍 ${post.cidade}"
                binding.txtCidade.visibility = android.view.View.VISIBLE
            } else {
                binding.txtCidade.visibility = android.view.View.GONE
            }

            if (post.autorFoto.isNotEmpty()) {
                try {
                    val bitmap = Base64Converter.stringToBitmap(post.autorFoto)
                    binding.imgProfile.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    binding.imgProfile.setImageResource(android.R.drawable.sym_def_app_icon)
                }
            } else {
                binding.imgProfile.setImageResource(android.R.drawable.sym_def_app_icon)
            }

            if (post.imagemBase64.isNotEmpty()) {
                try {
                    val bitmap = Base64Converter.stringToBitmap(post.imagemBase64)
                    binding.imgPost.setImageBitmap(bitmap)
                    binding.imgPost.visibility = android.view.View.VISIBLE
                } catch (e: Exception) {
                    binding.imgPost.visibility = android.view.View.GONE
                }
            } else {
                binding.imgPost.visibility = android.view.View.GONE
            }
        }
    }
}