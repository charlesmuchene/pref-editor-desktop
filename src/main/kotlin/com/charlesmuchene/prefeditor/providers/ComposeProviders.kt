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

package com.charlesmuchene.prefeditor.providers

import androidx.compose.runtime.compositionLocalOf
import com.charlesmuchene.prefeditor.app.AppState
import com.charlesmuchene.prefeditor.models.ReloadSignal
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.resources.TextBundle

val LocalReloadSignal = compositionLocalOf<ReloadSignal> { error("No reload signal provided") }
val LocalNavigation = compositionLocalOf<Navigation> { error("No navigation provided") }
val LocalAppState = compositionLocalOf<AppState> { error("No app state provided") }
val LocalBundle = compositionLocalOf<TextBundle> { error("No bundle provided") }
