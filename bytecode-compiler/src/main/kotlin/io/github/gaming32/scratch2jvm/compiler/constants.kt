package io.github.gaming32.scratch2jvm.compiler

public const val SUSPEND_NO_RESCHEDULE: Int = -1
public const val SUSPEND_CANCEL_ALL: Int = -2

public const val EVENT_FLAG_CLICKED: Int = 0
public const val EVENT_COUNT: Int = EVENT_FLAG_CLICKED + 1

public val EXTRA_KEYS: Map<String, Int> = mapOf(
    "space" to ' '.code,
    "up arrow" to 265, // GLFW_KEY_UP
    "down arrow" to 264, // GLFW_KEY_DOWN
    "right arrow" to 262, // GLFW_KEY_RIGHT
    "left arrow" to 263, // GLFW_KEY_LEFT
)
