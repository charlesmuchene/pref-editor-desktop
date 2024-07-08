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

package com.charlesmuchene.prefeditor.screens.preferences.device.viewer

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.prefeditor.extensions.hoverAnimation
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.models.preferenceIconName
import com.charlesmuchene.prefeditor.screens.preferences.device.DevicePreferencesUseCase
import com.charlesmuchene.prefeditor.ui.APP_SPACING
import com.charlesmuchene.prefeditor.ui.listing.ItemListing
import com.charlesmuchene.prefeditor.ui.theme.Typography
import com.charlesmuchene.prefeditor.ui.theme.appGray
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.modifier.onHover
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Viewer(
    prefUseCase: DevicePreferencesUseCase,
    showEditButton: Boolean,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel by remember {
        mutableStateOf(ViewerViewModel(prefUseCase = prefUseCase, scope = scope))
    }

    val items by viewModel.preferences.collectAsState()

    Column(modifier = modifier) {
        ViewerHeader(onClick = onEditClick, showEditButton = showEditButton)
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = Color.LightGray.copy(alpha = 0.75f),
            orientation = Orientation.Horizontal,
        )
        ItemListing {
            items(items = items, key = Preference::key) { preference ->
                PreferenceRow(preference = preference, modifier = Modifier.animateItemPlacement())
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PreferenceRow(
    preference: Preference,
    modifier: Modifier = Modifier,
) {
    val name = preferenceIconName(preference)
    val painter by rememberIconPainter(name)
    val scope = rememberCoroutineScope()
    val animatedScalePercent by remember { mutableStateOf(Animatable(initialValue = 1f)) }

    Column(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .onHover { isHovered ->
                scope.launch {
                    hoverAnimation(
                        targetValue = 1.2f,
                        isHovered = isHovered,
                        animatedScalePercent = animatedScalePercent,
                    )
                }
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.scale(animatedScalePercent.value)) {
            Tooltip(tooltip = { Text(text = preference.text) }) {
                Icon(
                    painter = painter,
                    contentDescription = name,
                    tint = JewelTheme.contentColor,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.width(APP_SPACING))
            Column {
                Text(text = preference.key, fontSize = TextUnit(value = 16f, type = TextUnitType.Sp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = preference.value,
                    color = Color.Gray,
                    fontSize = TextUnit(value = 14f, type = TextUnitType.Sp),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(orientation = Orientation.Horizontal, color = appGray)
    }
}

@Composable
private fun ViewerHeader(
    showEditButton: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = "Preferences", style = Typography.heading)
        if (showEditButton)
            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                DefaultButton(onClick = onClick, modifier = Modifier.pointerOnHover()) {
                    Text(text = "Edit")
                }
            }
    }
}
