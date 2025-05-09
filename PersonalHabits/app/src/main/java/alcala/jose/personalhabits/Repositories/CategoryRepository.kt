package alcala.jose.personalhabits.Repositories

class CategoryRepository {

    private val defaultCategories = listOf(
        "Salud",
        "Estudio",
        "Trabajo",
        "Hogar",
        "Finanzas",
        "Social",
        "Ocio",
        "Desarrollo Personal"
    )

    fun getCategories(): List<String> {
        return defaultCategories
    }
}