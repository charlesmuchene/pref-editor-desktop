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

package com.charlesmuchene.prefeditor.screens.bridge

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.resources.HomeKey
import com.charlesmuchene.prefeditor.ui.padding
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.IndeterminateHorizontalProgressBar
import org.jetbrains.jewel.ui.component.Text

@Composable
fun BridgeLoading(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[HomeKey.BridgeLoading]
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = text, style = Typography.primary)
        Spacer(modifier = Modifier.height(padding))
        IndeterminateHorizontalProgressBar(modifier = Modifier.width(128.dp))
    }
}