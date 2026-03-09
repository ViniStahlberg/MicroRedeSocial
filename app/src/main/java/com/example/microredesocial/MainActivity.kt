package com.example.microredesocial

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.microredesocial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpFirebase()
        setUpLitener()
    }

    fun setUpFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun setUpLitener(){
        binding.btnLogin.setOnClickListener{autenticarUuario()}
        binding.txtCriarConta.setOnClickListener{
            startActivity(Intent(this, CadastroActivity::class.java))
            finish()
        }
    }

    fun autenticarUuario(){
        val email = binding.edtEmail.text.toString()
        val password = binding.edtSenha.text.toString()

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
                }
            }
    }

}