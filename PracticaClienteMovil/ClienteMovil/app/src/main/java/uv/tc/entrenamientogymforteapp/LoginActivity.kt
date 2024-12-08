package uv.tc.entrenamientogymforteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.entrenamientogymforteapp.databinding.ActivityLoginBinding
import uv.tc.entrenamientogymforteapp.poko.LoginCliente
import uv.tc.entrenamientogymforteapp.util.Constantes

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun onStart() {
        super.onStart()
        binding.btPass.setOnClickListener{
            val correo = binding.etCorreo.text.toString()
            val contrasenia = binding.etPassword.text.toString()

            if(sonCamposValidos(correo, contrasenia)){
                verificarCredenciales(correo, contrasenia)
            }

        }
    }

    fun sonCamposValidos(correo : String, contrasenia : String) : Boolean{
        var camposValidos = true
        if(correo.isEmpty()){
            camposValidos = false
            binding.etCorreo.setError("Obligatorio")
        }

        if(contrasenia.isEmpty()){
            camposValidos = false
            binding.etPassword.setError("Obligatorio")
        }
        return camposValidos
    }

    fun verificarCredenciales(correo: String, password: String){
        // Configuración de biblitoeca solo la primera linea
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(false)
        // Consumo de WS
        Ion.with(this@LoginActivity).load("POST","${Constantes().URL_WS}login/clienteR").setHeader("Content-Type", "application/x-www-form-urlencoded").setBodyParameter("correo", correo).setBodyParameter("password", password).asString().setCallback { e, result ->
            if(e == null){
                serializarInformacion(result)
            }else{
                Toast.makeText(this@LoginActivity, "error en" + e.message, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun serializarInformacion(json :String){
        val gson = Gson()
        val respuestaLoginCliente = gson.fromJson(json, LoginCliente::class.java)
        Toast.makeText(this@LoginActivity, respuestaLoginCliente.mensaje, Toast.LENGTH_LONG).show()
        if(!respuestaLoginCliente.error){
            val clienteJSON = gson.toJson(respuestaLoginCliente.cliente)
            irPantallaPrincipal(clienteJSON)
        }
    }

    fun irPantallaPrincipal(cliente : String){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("cliente", cliente)
        startActivity(intent)
        finish()
    }

}