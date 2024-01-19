package com.charlesmuchene.prefedit.parser

import okio.BufferedSource

interface Parser<T> {
    fun parse(source: BufferedSource): T
}