package com.example.microredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.microredesocial.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupListener()
    }

    private fun setupListener(){

        binding.btnCadastrar.setOnClickListener {
            cadastrarUsuario()
        }

        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun cadastrarUsuario(){

        val email = binding.edtEmail.text.toString()
        val senha = binding.edtSenha.text.toString()
        val confirmarSenha = binding.edtConfirmarSenha.text.toString()

        if(email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()){
            Toast.makeText(this,"Preencha todos os campos",Toast.LENGTH_LONG).show()
            return
        }

        if(senha != confirmarSenha){
            Toast.makeText(this,"As senhas não coincidem",Toast.LENGTH_LONG).show()
            return
        }

        firebaseAuth
            .createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->

                if(task.isSuccessful){

                    Toast.makeText(this,"Usuário cadastrado com sucesso!",Toast.LENGTH_LONG).show()

                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()

                }else{

                    Toast.makeText(this,"Erro ao cadastrar usuário",Toast.LENGTH_LONG).show()

                }

            }

    }
}