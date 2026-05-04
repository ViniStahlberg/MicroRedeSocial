package com.example.microredesocial.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.microredesocial.R
import com.example.microredesocial.databinding.ItemPostBinding
import com.example.microredesocial.model.Post
import com.example.microredesocial.utils.Base64Converter
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private val posts: MutableList<Post>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_END = 2
    }

    private var isLoading = false
    private var isEndOfList = false

    fun atualizarPosts(novosPosts: List<Post>) {
        posts.clear()
        posts.addAll(novosPosts)
        isLoading = false
        isEndOfList = false
        notifyDataSetChanged()
    }

    fun adicionarPosts(novosPosts: List<Post>) {
        val posicaoInicial = posts.size
        posts.addAll(novosPosts)
        notifyItemRangeInserted(posicaoInicial, novosPosts.size)
        isLoading = false
    }

    fun setLoading(loading: Boolean) {
        if (isLoading == loading) return
        isLoading = loading
        if (loading) {
            notifyItemInserted(itemCount)
        } else {
            notifyItemRemoved(itemCount)
        }
    }

    fun setEndOfList(end: Boolean) {
        if (isEndOfList == end) return
        isEndOfList = end
        if (end) {
            notifyItemInserted(itemCount)
        } else {
            notifyItemRemoved(itemCount)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == posts.size && isEndOfList -> TYPE_END
            position == posts.size && isLoading -> TYPE_LOADING
            else -> TYPE_POST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_POST -> {
                val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding)
            }
            TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.footer_loading, parent, false)
                LoadingViewHolder(view)
            }
            TYPE_END -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.footer_end, parent, false)
                EndViewHolder(view)
            }
            else -> throw IllegalArgumentException("Tipo inválido")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> holder.bind(posts[position])
            is LoadingViewHolder -> holder.bind()
            is EndViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int {
        var count = posts.size
        if (isLoading) count++
        if (isEndOfList && !isLoading) count++
        return count
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.txtUsername.text = post.autorUsername
            binding.txtTexto.text = post.texto
            binding.txtData.text = dateFormat.format(post.dataCriacao)

            if (post.cidade.isNotEmpty()) {
                binding.txtCidade.text = "📍 ${post.cidade}"
                binding.txtCidade.visibility = View.VISIBLE
            } else {
                binding.txtCidade.visibility = View.GONE
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
                    binding.imgPost.visibility = View.VISIBLE
                } catch (e: Exception) {
                    binding.imgPost.visibility = View.GONE
                }
            } else {
                binding.imgPost.visibility = View.GONE
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val txtMensagem: TextView = itemView.findViewById(R.id.txtMensagem)

        fun bind() {
            progressBar.visibility = View.VISIBLE
            txtMensagem.visibility = View.VISIBLE
            txtMensagem.text = "Carregando mais posts..."
        }
    }

    inner class EndViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.startAnimation(AnimationUtils.loadAnimation(itemView.context, R.anim.fade_in))
        }
    }
}