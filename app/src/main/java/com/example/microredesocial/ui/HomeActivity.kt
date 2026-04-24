package com.example.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.microredesocial.R
import com.example.microredesocial.auth.UserAuth
import com.example.microredesocial.dao.PostDAO
import com.example.microredesocial.databinding.ActivityHomeBinding
import com.example.microredesocial.model.Post

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userAuth: UserAuth
    private lateinit var postDAO: PostDAO
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()
    private var ultimoPost: Post? = null
    private var carregando = false
    private var cidadeBusca: String? = null
    private var isBuscando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "MicroRedeSocial"

        userAuth = UserAuth()
        postDAO = PostDAO()

        setupRecyclerView()
        carregarPosts()

        binding.fabNovaPostagem.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    cidadeBusca = query
                    buscarPorCidade(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    cidadeBusca = null
                    isBuscando = false
                    carregarPosts()
                }
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, EditProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(posts)
        binding.rvPosts.layoutManager = LinearLayoutManager(this)
        binding.rvPosts.adapter = postAdapter

        binding.rvPosts.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItems = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                if (!carregando && !isBuscando && lastVisible >= totalItems - 1 && cidadeBusca == null) {
                    carregarMaisPosts()
                }
            }
        })
    }

    private fun carregarPosts() {
        if (carregando) return

        carregando = true
        mostrarLoading(true)

        postDAO.buscarPosts(
            ultimoPost = null,
            limite = 10,
            onSuccess = { novosPosts ->
                posts.clear()
                posts.addAll(novosPosts)
                ultimoPost = if (novosPosts.isNotEmpty()) novosPosts.last() else null
                postAdapter.notifyDataSetChanged()
                carregando = false
                mostrarLoading(false)

                if (novosPosts.isEmpty()) {
                    Toast.makeText(this, "Nenhum post ainda. Crie o primeiro!", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, "Erro ao carregar posts: ${exception.message}", Toast.LENGTH_SHORT).show()
                carregando = false
                mostrarLoading(false)
            }
        )
    }

    private fun carregarMaisPosts() {
        if (carregando) return

        carregando = true

        postDAO.buscarPosts(
            ultimoPost = ultimoPost,
            limite = 10,
            onSuccess = { novosPosts ->
                if (novosPosts.isNotEmpty()) {
                    val posicaoInicial = posts.size
                    posts.addAll(novosPosts)
                    postAdapter.notifyItemRangeInserted(posicaoInicial, novosPosts.size)
                    ultimoPost = novosPosts.last()
                }
                carregando = false
            },
            onFailure = {
                carregando = false
            }
        )
    }

    private fun buscarPorCidade(cidade: String) {
        if (cidade.isEmpty()) return
        if (carregando) return

        isBuscando = true
        mostrarLoading(true)

        postDAO.buscarPostsPorCidade(
            cidade = cidade,
            onSuccess = { novosPosts ->
                posts.clear()
                posts.addAll(novosPosts)
                postAdapter.notifyDataSetChanged()
                isBuscando = false
                mostrarLoading(false)

                if (novosPosts.isEmpty()) {
                    Toast.makeText(this, "Nenhum post encontrado em '$cidade'", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Encontrados ${novosPosts.size} posts em '$cidade'", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, "Erro na busca: ${exception.message}", Toast.LENGTH_SHORT).show()
                isBuscando = false
                mostrarLoading(false)
            }
        )
    }

    private fun mostrarLoading(mostrar: Boolean) {
        if (mostrar) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}