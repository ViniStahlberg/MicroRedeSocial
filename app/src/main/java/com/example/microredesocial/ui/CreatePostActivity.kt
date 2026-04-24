package com.example.microredesocial.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.microredesocial.auth.UserAuth
import com.example.microredesocial.dao.PostDAO
import com.example.microredesocial.dao.UserDAO
import com.example.microredesocial.databinding.ActivityCreatePostBinding
import com.example.microredesocial.model.Post
import com.example.microredesocial.utils.Base64Converter
import com.example.microredesocial.utils.LocationHelper
import java.util.Date

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var userAuth: UserAuth
    private lateinit var userDAO: UserDAO
    private lateinit var postDAO: PostDAO
    private lateinit var locationHelper: LocationHelper

    private var selectedImageUri: Uri? = null
    private var cidadeAtual: String = ""
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val LOCATION_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userAuth = UserAuth()
        userDAO = UserDAO()
        postDAO = PostDAO()
        locationHelper = LocationHelper(this)

        setupListeners()
        verificarPermissaoLocalizacao()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupListeners() {
        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.imgPost.setImageURI(uri)
                binding.imgPost.visibility = android.view.View.VISIBLE
            }
        }

        binding.btnSelecionarImagem.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnObterLocalizacao.setOnClickListener {
            obterLocalizacao()
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }
    }

    private fun verificarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacao()
        }
    }

    private fun obterLocalizacao() {
        locationHelper.getCurrentLocation(
            onSuccess = { location ->
                latitude = location.latitude
                longitude = location.longitude
                cidadeAtual = locationHelper.getCityFromLocation(location)
                binding.edtCidade.setText(cidadeAtual)
                binding.txtCidade.text = "📍 Localização obtida: $cidadeAtual"
                binding.txtCidade.visibility = android.view.View.VISIBLE
            },
            onFailure = {
                Toast.makeText(this, "Não foi possível obter localização", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun publicarPost() {
        val texto = binding.edtTexto.text.toString()
        val cidade = binding.edtCidade.text.toString()

        if (texto.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "Adicione um texto ou uma imagem", Toast.LENGTH_SHORT).show()
            return
        }

        val email = userAuth.getCurrentUserEmail()
        if (email == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        userDAO.buscarPerfil(email,
            onSuccess = { user ->
                if (user != null) {
                    val imagemBase64 = if (selectedImageUri != null && binding.imgPost.drawable != null) {
                        try {
                            Base64Converter.drawableToString(binding.imgPost.drawable)
                        } catch (e: Exception) {
                            ""
                        }
                    } else ""

                    val post = Post(
                        autorEmail = email,
                        autorUsername = user.username,
                        autorFoto = user.fotoPerfil,
                        texto = texto,
                        imagemBase64 = imagemBase64,
                        cidade = cidade,
                        latitude = latitude,
                        longitude = longitude,
                        dataCriacao = Date()
                    )

                    postDAO.criarPost(post,
                        onSuccess = {
                            Toast.makeText(this, "Post publicado!", Toast.LENGTH_SHORT).show()
                            finish()
                        },
                        onFailure = {
                            Toast.makeText(this, "Erro ao publicar", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onFailure = {
                Toast.makeText(this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
            }
        )
    }
}