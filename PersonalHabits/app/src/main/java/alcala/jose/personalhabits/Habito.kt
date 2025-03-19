package alcala.jose.personalhabits

data class Habito(
    val id: Int,
    var nombre: String,
    var categoria: String,
    var completado: Boolean = false  // Nuevo campo para indicar si está completado
)
