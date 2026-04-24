package com.example.microredesocial.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.microredesocial.auth.UserAuth
import com.example.microredesocial.dao.UserDAO
import com.example.microredesocial.databinding.ActivityEditProfileBinding
import com.example.microredesocial.model.User
import com.example.microredesocial.utils.Base64Converter

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userAuth: UserAuth
    private lateinit var userDAO: UserDAO
    private var selectedImageUri: Uri? = null
    private var userAtual: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userAuth = UserAuth()
        userDAO = UserDAO()

        setupListeners()
        carregarDados()
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
                binding.profilePicture.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun carregarDados() {
        val email = userAuth.getCurrentUserEmail() ?: return

        userDAO.buscarPerfil(email,
            onSuccess = { user ->
                userAtual = user
                user?.let {
                    binding.edtUsername.setText(it.username)
                    binding.edtNomeCompleto.setText(it.nomeCompleto)
                    if (it.fotoPerfil.isNotEmpty()) {
                        try {
                            val bitmap = Base64Converter.stringToBitmap(it.fotoPerfil)
                            binding.profilePicture.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            // Imagem padrão já está setada
                        }
                    }
                }
            },
            onFailure = {
                Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun salvarAlteracoes() {
        val username = binding.edtUsername.text.toString().trim()
        val nomeCompleto = binding.edtNomeCompleto.text.toString().trim()
        val novaSenha = binding.edtNovaSenha.text.toString().trim()
        val confirmarSenha = binding.edtConfirmarSenha.text.toString().trim()

        // Validar nome e username
        if (username.isEmpty() || nomeCompleto.isEmpty()) {
            Toast.makeText(this, "Preencha nome de usuário e nome completo", Toast.LENGTH_SHORT).show()
            return
        }

        // Se o usuário digitou uma nova senha, validar
        if (novaSenha.isNotEmpty() || confirmarSenha.isNotEmpty()) {
            if (novaSenha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return
            }
            if (novaSenha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return
            }
            // Alterar a senha no Firebase Authentication
            alterarSenha(novaSenha)
        }

        // Atualizar foto
        val fotoPerfilString = if (selectedImageUri != null && binding.profilePicture.drawable != null) {
            try {
                Base64Converter.drawableToString(binding.profilePicture.drawable)
            } catch (e: Exception) {
                userAtual?.fotoPerfil ?: ""
            }
        } else {
            userAtual?.fotoPerfil ?: ""
        }

        val userAtualizado = userAtual?.copy(
            username = username,
            nomeCompleto = nomeCompleto,
            fotoPerfil = fotoPerfilString
        ) ?: return

        // Atualizar dados do perfil no Firestore
        userDAO.atualizarPerfil(userAtualizado,
            onSuccess = {
                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = {
                Toast.makeText(this, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun alterarSenha(novaSenha: String) {
        userAuth.updatePassword(
            newPassword = novaSenha,
            onComplete = { success, message ->
                if (success) {
                    Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                    // Limpar campos de senha
                    binding.edtNovaSenha.text?.clear()
                    binding.edtConfirmarSenha.text?.clear()
                } else {
                    Toast.makeText(this, "Erro ao alterar senha: ${message ?: "Tente novamente"}", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}