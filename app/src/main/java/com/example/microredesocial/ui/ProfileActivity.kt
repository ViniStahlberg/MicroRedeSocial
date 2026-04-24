package com.example.microredesocial.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.microredesocial.auth.UserAuth
import com.example.microredesocial.dao.UserDAO
import com.example.microredesocial.databinding.ActivityProfileBinding
import com.example.microredesocial.model.User
import com.example.microredesocial.utils.Base64Converter

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var userAuth: UserAuth
    private lateinit var userDAO: UserDAO
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAuth = UserAuth()
        userDAO = UserDAO()

        setupListener()
    }

    private fun setupListener() {
        val galeria = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                binding.profilePicture.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.btnSalvarPerfil.setOnClickListener {
            salvarPerfil()
        }
    }

    private fun salvarPerfil() {
        val email = userAuth.getCurrentUserEmail()
        if (email == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_LONG).show()
            return
        }

        val username = binding.edtUsername.text.toString()
        val nomeCompleto = binding.edtNomeCompleto.text.toString()

        if (username.isEmpty() || nomeCompleto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            return
        }

        // Converter imagem para String Base64
        val fotoPerfilString = if (selectedImageUri != null) {
            // Se o usuário selecionou uma nova foto
            val drawable = binding.profilePicture.drawable
            Base64Converter.drawableToString(drawable)
        } else {
            // Se não selecionou nova foto, pegar a atual
            val drawable = binding.profilePicture.drawable
            if (drawable != null) {
                Base64Converter.drawableToString(drawable)
            } else {
                ""
            }
        }

        val user = User(
            email = email,
            username = username,
            nomeCompleto = nomeCompleto,
            fotoPerfil = fotoPerfilString
        )

        userDAO.salvarPerfil(
            user = user,
            onSuccess = {
                Toast.makeText(this, "Perfil salvo com sucesso!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            },
            onFailure = { exception ->
                Toast.makeText(this, "Erro ao salvar: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}