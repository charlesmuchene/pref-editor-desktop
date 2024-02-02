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

package com.charlesmuchene.prefeditor.ui

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.ui.theme.orange
import org.jetbrains.jewel.ui.component.Icon

@Composable
fun FavoriteButton(selected: Boolean, modifier: Modifier = Modifier, onFavorite: () -> Unit) {
    val icon = if (selected) "favorite-solid" else "favorite-outline"
    val painter by rememberIconPainter(icon)
    Icon(
        tint = orange,
        painter = painter,
        contentDescription = "Favorite item",
        modifier = modifier.clickable(onClick = onFavorite),
    )
}