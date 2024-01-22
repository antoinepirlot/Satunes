package earth.mp3.models.utils

fun <T, U> mapToList(map: Map<U, T>): List<T> {
    val listToReturn = mutableListOf<T>()
    map.map { entry ->
        listToReturn.add(entry.value)
    }
    return listToReturn
}

fun <T> loadObjectsTo(toList: MutableList<T>, fromList: List<T>) {
    toList.clear()
    for (obj: T in fromList) {
        toList.add(obj)
    }
}