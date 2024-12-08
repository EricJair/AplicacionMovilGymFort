package uv.tc.entrenamientogymforteapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uv.tc.entrenamientogymforteapp.databinding.ActivityEditarClienteBinding
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.entrenamientogymforteapp.poko.Cliente
import uv.tc.entrenamientogymforteapp.poko.LoginCliente
import uv.tc.entrenamientogymforteapp.poko.Mensaje
import uv.tc.entrenamientogymforteapp.util.Constantes
import java.util.Calendar


class EditarClienteActivity : AppCompatActivity() {
//


    private lateinit var binding: ActivityEditarClienteBinding
    private lateinit var cliente: Cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        obtenerDatosCliente()
        cargarDatosCliente()

        binding.btEditar.setOnClickListener {
            if (validarCampos()) {
                guardarCambios()
            } else {
                Toast.makeText(this, "Campos invalidos", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun obtenerDatosCliente() {
        val jsonCliente = intent.getStringExtra("cliente")
        if (jsonCliente != null) {
            val gson = Gson()
            cliente = gson.fromJson(jsonCliente, Cliente::class.java)
        }
    }

    private fun cargarDatosCliente() {
        binding.etNombre.setText(cliente.nombreCliente)
        binding.etApellidoPaterno.setText(cliente.apellidoPaterno)
        binding.etApellidoMaterno.setText(cliente.apellidoMaterno)
        binding.etTelefono.setText(cliente.telefono)
        binding.etFecha.setText(cliente.fechaNacimiento)
        binding.etPeso.setText(cliente.peso.toString())
        binding.etEstatura.setText(cliente.estatura.toString())
        binding.etCorreo.setText(cliente.correo)
    }

    private fun guardarCambios() {
        cliente.nombreCliente = binding.etNombre.text.toString()
        cliente.apellidoPaterno = binding.etApellidoPaterno.text.toString()
        cliente.apellidoMaterno = binding.etApellidoMaterno.text.toString()
        cliente.fechaNacimiento = binding.etFecha.text.toString()
        cliente.peso = binding.etPeso.text.toString()
        cliente.estatura = binding.etEstatura.text.toString()

        Ion.with(this)
            .load("PUT", "${Constantes().URL_WS}Clientes/actualizarCliente")
            .setJsonPojoBody(cliente)
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    val gson = Gson()
                    val mensaje = gson.fromJson(result, Mensaje::class.java)
                    Toast.makeText(this, mensaje.mensaje, Toast.LENGTH_LONG).show()

                    if (!mensaje.error) {
                        val intent = Intent()
                        intent.putExtra("clienteActualizado", Gson().toJson(cliente))
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun validarCampos() : Boolean {
        val nombre = binding.etNombre.text.toString()
        val apellidoPaterno = binding.etApellidoPaterno.text.toString()
        val apellidoMaterno = binding.etApellidoMaterno.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val fechaNacimiento = binding.etFecha.text.toString()
        val peso = binding.etPeso.text.toString()
        val estatura = binding.etEstatura.text.toString()
        return nombre.isNotEmpty() && apellidoPaterno.isNotEmpty() &&
                apellidoMaterno.isNotEmpty() && telefono.isNotEmpty() &&
                fechaNacimiento.isNotEmpty() && peso.isNotEmpty() &&
                estatura.isNotEmpty()
    }

    private fun cancelarEdicion() {
        setResult(RESULT_CANCELED)
        finish()
    }


}







