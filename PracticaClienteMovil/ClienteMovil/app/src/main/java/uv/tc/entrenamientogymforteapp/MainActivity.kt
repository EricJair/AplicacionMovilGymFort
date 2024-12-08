package uv.tc.entrenamientogymforteapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import uv.tc.entrenamientogymforteapp.databinding.ActivityMainBinding
import uv.tc.entrenamientogymforteapp.poko.Cliente
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.entrenamientogymforteapp.poko.Mensaje
import uv.tc.entrenamientogymforteapp.util.Constantes
import java.io.ByteArrayOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cliente: Cliente
    private var fotoPerfilBytes: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        obtenerDatosCliente()
        cargarDatosCliente()
    }

    override fun onStart() {
        super.onStart()
        obtenerFotoCliente(cliente.idCliente)
        binding.ivIrEditar.setOnClickListener {
            irPantallaEditar()
        }

        binding.ivIrEditarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            seleccionarFotoPerfil.launch(intent)
        }
    }

    fun irPantallaEditar() {
        val gson = Gson()
        val clienteJSON = gson.toJson(cliente)
        val intent = Intent(this@MainActivity, EditarClienteActivity::class.java)
        intent.putExtra("cliente", clienteJSON)
        startActivity(intent)
    }

    fun obtenerDatosCliente() {
        val jsonCliente = intent.getStringExtra("cliente")
        if (jsonCliente != null) {
            val gson = Gson()
            cliente = gson.fromJson(jsonCliente, Cliente::class.java)
        }
    }

    fun cargarDatosCliente() {
        binding.tvNombreCliente.text =
            cliente.nombreCliente + " " + cliente.apellidoPaterno + " " + cliente.apellidoMaterno
        binding.tvCorreoCliente.text = cliente.correo
        binding.tvTelefonoCliente.text = cliente.telefono
        binding.tvFechaNacimientoCliente.text = cliente.fechaNacimiento
        binding.tvFechaInscripcionCliente.text = cliente.fechaInscripcion
        binding.tvEstaturaCliente.text = cliente.estatura
        binding.tvPesoCliente.text = cliente.peso
        binding.tvEntrenadorAsignadoCliente.text = cliente.entrenador
    }

    fun obtenerFotoCliente(idCliente: Int) {
        Ion.with(this@MainActivity)
            .load("GET", "${Constantes().URL_WS}Clientes/obtener-foto/${idCliente}")
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    cargarFotoCliente(result)
                } else {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }
//
fun cargarFotoCliente(json: String) {
    if (json.isNotEmpty()) {
        val gson = Gson()
        val clienteFoto = gson.fromJson(json, Cliente::class.java)
        if (clienteFoto.fotoBase64 != null) {
            try {
                val imgBytes = Base64.decode(clienteFoto.fotoBase64, Base64.DEFAULT)
                val imgBitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.size)
                binding.ivFotoCliente.setImageBitmap(imgBitmap)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this@MainActivity, "No tiene una foto de perfil", Toast.LENGTH_LONG)
                .show()
        }
    }
}
//
//   // Implementacion seleccion foto
//
    private val seleccionarFotoPerfil = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imgURI = data?.data
            if (imgURI != null) {
                fotoPerfilBytes = uriToByteArray(imgURI)
                if (fotoPerfilBytes != null) {
                    subirFotoPerfil(cliente.idCliente)
                }

            }
        }

    }

    //
    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream =
                ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //
    fun subirFotoPerfil(idCliente: Int) {
        Ion.with(this@MainActivity)
            .load("PUT", "${Constantes().URL_WS}Clientes/subir-foto/${idCliente}")
            .setByteArrayBody(fotoPerfilBytes)
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    val gson = Gson()
                    val msj = gson.fromJson(result, Mensaje::class.java)
                    Toast.makeText(this@MainActivity, msj.mensaje, Toast.LENGTH_LONG)
                        .show()
                    if (!msj.error) {
                        obtenerFotoCliente(cliente.idCliente)
                    }

                } else {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
    }


}

