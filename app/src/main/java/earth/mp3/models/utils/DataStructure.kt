package earth.mp3.models.utils

fun <T, U> mapToList(map: Map<U, T>): List<T> {
    val listToReturn = mutableListOf<T>()
    map.map { entry ->
        listToReturn.add(entry.value)
    }
    return listToReturn
}