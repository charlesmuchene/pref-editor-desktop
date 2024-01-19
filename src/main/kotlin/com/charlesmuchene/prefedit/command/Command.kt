package com.charlesmuchene.prefedit.command

import com.charlesmuchene.prefedit.parser.Parser
import okio.BufferedSource

interface Command<T> {
    val command: String
    val parser: Parser<T>

    fun execute(source: BufferedSource): T = parser.parse(source)
}