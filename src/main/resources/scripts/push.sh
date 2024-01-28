#!/usr/bin/env bash
#
# Copyright (c) 2024 Charles Muchene
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

adb -s "$1" exec-out \
run-as "$2" \
sed -Ei.backup-"$3" \
-e "1s/.*/@@/g" \
-e "#^@@#!d" \
-e "s#@@#${PREF_EDIT_CONTENT}#" \
"/data/data/$2/shared_prefs/$4"