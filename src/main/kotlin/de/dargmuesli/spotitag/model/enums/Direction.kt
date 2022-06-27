package de.dargmuesli.spotitag.model.enums

enum class Direction(
    val type: String
) {
    FORWARD("forward"), NONE("none"), BACKWARD("backward");

    companion object {
        private val map: MutableMap<String, Direction> = HashMap()

        init {
            for (direction in values()) {
                map[direction.type] = direction
            }
        }

        fun keyOf(type: String): Direction? {
            return map[type]
        }
    }
}