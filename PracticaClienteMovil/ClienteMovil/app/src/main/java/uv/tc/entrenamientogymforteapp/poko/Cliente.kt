package uv.tc.entrenamientogymforteapp.poko

data class Cliente(
    val idCliente : Int,
    var nombreCliente : String,
    var apellidoPaterno : String,
    var apellidoMaterno : String,
    var fechaNacimiento : String,
    val telefono : String,
    var peso : String,
    var estatura : String,
    var correo : String,
    var password : String ?,
    val idColaborador : Int,
    val colaborador :String,
    val entrenador : String,
    var fotoBase64 : String ?,
    val fechaInscripcion : String
)
