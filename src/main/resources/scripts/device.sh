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

# Add edit
# serial, package, in-place/backup, matcher, content, filename
function add() {
    adb -s "$1" exec-out \
    run-as "$2" \
    sed -E"$3" \
    -e "/$4/i$5" \
    "/data/data/$2/shared_prefs/$6"
}

# Delete edit
# serial, package, in-place/backup, matcher, filename
function delete() {
    adb -s "$1" exec-out \
    run-as "$2" \
    sed -E"$3" \
    -e "/$4/d" \
    "/data/data/$2/shared_prefs/$5"
}

# Change edit
# serial, package, in-place/backup, matcher, change, filename
function change() {
    adb -s "$1" exec-out \
    run-as "$2" \
    sed -E"$3" \
    -e "s/$4/$5/" \
    "/data/data/$2/shared_prefs/$6"
}

#####
# Execute based on type of edit
#####
case $1 in
  add)
    # serial, package, in-place/backup, matcher, content, filename
    add "$2" "$3" "$4" "$5" "$6" "$7"
    ;;
  delete)
    # serial, package, in-place/backup, matcher, filename
    delete "$2" "$3" "$4" "$5" "$6"
    ;;
  change)
    # serial, package, in-place/backup, matcher, change, filename
    change "$2" "$3" "$4" "$5" "$6" "$7"
esac