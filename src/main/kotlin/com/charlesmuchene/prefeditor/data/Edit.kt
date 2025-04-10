/*
 * Copyright (c) 2024 Charles Muchene
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.charlesmuchene.prefeditor.data

/**
 * Edit commands
 */
sealed interface Edit {
    /**
     * Add [content] after [matcher]
     */
    data class Add(val matcher: String = "</${Tags.ROOT}>", val content: String) : Edit

    /**
     * Delete line matching [matcher]
     */
    data class Delete(val matcher: String) : Edit

    /**
     * Change [matcher] to [content]
     */
    data class Change(val matcher: String, val content: String) : Edit

    /**
     * Replace with [content]
     *
     * NB: Content is base64 encoded because byte 0 (null) doesn't play well with
     * [java.lang.ProcessBuilder] command list.
     *
     * @see `device.sh` -> replace
     */
    data class Replace(val content: String) : Edit
}
