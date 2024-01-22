package earth.mp3.models.utils

fun <T> loadObjectsTo(toList: MutableList<T>, fromList: List<T>) {
    toList.clear()
    for (obj: T in fromList) {
        toList.add(obj)
    }
}