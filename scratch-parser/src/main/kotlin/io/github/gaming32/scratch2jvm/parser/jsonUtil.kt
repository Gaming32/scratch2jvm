package io.github.gaming32.scratch2jvm.parser

import com.google.gson.JsonElement

public val JsonElement.asNullableString: String? get() = if (isJsonNull) null else asString
