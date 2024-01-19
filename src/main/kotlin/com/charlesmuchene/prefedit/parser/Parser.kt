package com.charlesmuchene.prefedit.parser

import okio.BufferedSource

interface Parser<T> {
    /**
     * Parse the source
     *
     * NB: This source is closed by the caller: verify
     *
     * @param source [BufferedSource] from the OS
     *
     * @return Instance of [T]
     */
    fun parse(source: BufferedSource): T
}