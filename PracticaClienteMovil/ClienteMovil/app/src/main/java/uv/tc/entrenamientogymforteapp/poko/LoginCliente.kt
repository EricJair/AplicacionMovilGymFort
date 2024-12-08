package uv.tc.entrenamientogymforteapp.poko

data class LoginCliente(
    val error : Boolean,
    val mensaje : String,
    var cliente : Cliente ?
)
