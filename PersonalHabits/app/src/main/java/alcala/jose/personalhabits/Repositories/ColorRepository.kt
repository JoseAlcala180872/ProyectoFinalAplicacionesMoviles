package alcala.jose.personalhabits.Repositories

class ColorRepository {

    private val hardcodedColors = listOf(
        Pair("Rojo", "#FF0000"),
        Pair("Verde", "#00FF00"),
        Pair("Azul", "#0000FF"),
        Pair("Amarillo", "#FFFF00"),
        Pair("Morado", "#800080"),
        Pair("Naranja", "#FFA500"),
        Pair("Rosa", "#FFC0CB"),
        Pair("Cian", "#00FFFF"),
        Pair("Gris", "#808080"),
        Pair("Negro", "#000000")
    )

    fun getColors(): List<Pair<String, String>> {
        return hardcodedColors
    }

}