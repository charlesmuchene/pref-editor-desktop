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

package com.charlesmuchene.prefedit.command

import com.charlesmuchene.prefedit.data.Apps
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.parser.AppListParser
import com.charlesmuchene.prefedit.parser.Parser

data class ListApps(val device: Device, override val parser: Parser<Apps> = AppListParser()) : Command<Apps> {
    override val command: String = "-s ${device.serial} shell pm list packages -3 --user 0"
}