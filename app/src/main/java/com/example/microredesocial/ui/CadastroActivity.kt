package com.example.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.microredesocial.auth.UserAuth
import com.example.microredesocial.databinding.ActivityCadastroBinding

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var userAuth: UserAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAuth = UserAuth()

        setupListener()
    }

    private fun setupListener() {
        binding.btnCadastrar.setOnClickListener {
            cadastrarUsuario()
        }

        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun cadastrarUsuario() {
        val email = binding.edtEmail.text.toString()
        val senha = binding.edtSenha.text.toString()
        val confirmarSenha = binding.edtConfirmarSenha.text.toString()

        if (email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            return
        }

        if (senha != confirmarSenha) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_LONG).show()
            return
        }

        userAuth.createUserWithEmailAndPassword(
            email = email,
            password = senha,
            onComplete = { success, errorMessage ->
                if (success) {
                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao cadastrar: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}