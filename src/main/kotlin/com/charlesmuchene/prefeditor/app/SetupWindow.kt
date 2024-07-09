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

package com.charlesmuchene.prefeditor.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.resources.AppVersion
import com.charlesmuchene.prefeditor.ui.APP_SPACING
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.ui.component.ExternalLink
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Typography
import java.awt.Desktop
import java.net.URI

@Composable
fun SetupWindow(modifier: Modifier = Modifier) {
    IntUiTheme(isDark = true) {
        val title = "Preference Editor"
        DialogWindow(onCloseRequest = {}, title = title, undecorated = true, transparent = true, resizable = false) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    modifier.background(
                        color = JewelTheme.globalColors.paneBackground,
                        shape = RoundedCornerShape(APP_SPACING),
                    ).fillMaxSize(),
            ) {
                val painter by rememberIconPainter(name = "app@1024x1024")
                Icon(painter = painter, contentDescription = "Application icon", tint = JewelTheme.contentColor)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = title, style = Typography.h0TextStyle())
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = AppVersion.version, style = Typography.labelTextStyle())
                Spacer(modifier = Modifier.height(12.dp))
                val url = "https://www.charlesmuchene.com"
                ExternalLink(
                    text = "Copyright © 2024 Charles Muchene",
                    onClick = { Desktop.getDesktop().browse(URI.create(url)) },
                )
            }
        }
    }
}
