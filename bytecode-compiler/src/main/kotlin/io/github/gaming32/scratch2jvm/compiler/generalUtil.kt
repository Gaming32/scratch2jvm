package io.github.gaming32.scratch2jvm.compiler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.Util

public fun getLogger(): Logger = LoggerFactory.getLogger(Util.getCallingClass())
