package earth.mp3.models.utils

fun <T> loadObjectsTo(toList: MutableList<T>, fromList: List<T>) {
    toList.clear()
    toList.addAll(fromList)
}

fun <T, U> loadObjectsToMap(toMap: MutableMap<T, U>, fromMap: Map<T, U>) {
    toMap.clear()
    toMap.putAll(fromMap)
}