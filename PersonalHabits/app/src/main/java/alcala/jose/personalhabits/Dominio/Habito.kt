package alcala.jose.personalhabits.Dominio

data class Habito(
    var id: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    var hora: String = "",
    var frecuencia: List<String> = listOf(),
    var color: Int = 0,
    var categoria: String = "",
    val userId: String = "",
    )