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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.resources.HomeKey
import com.charlesmuchene.prefeditor.ui.halfPadding
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.ExternalLink
import org.jetbrains.jewel.ui.component.Text
import java.awt.Desktop
import java.net.URI

@Composable
fun BridgeUnavailable(modifier: Modifier = Modifier) {
    val primary = LocalBundle.current[HomeKey.BridgeUnavailable]
    val secondary = LocalBundle.current[HomeKey.BridgeUnavailableSecondary]
    val url = "https://developer.android.com/tools/releases/platform-tools"

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = primary, style = Typography.heading, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(halfPadding))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = secondary,
                style = Typography.primary,
                fontSize = TextUnit(value = 16f, type = TextUnitType.Sp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            ExternalLink(text = url, onClick = { Desktop.getDesktop().browse(URI.create(url)) })
        }
    }
}
