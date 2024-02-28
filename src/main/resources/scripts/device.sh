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
# executable, serial, package, filename, in-place/backup, matcher, content
function add() {
    "$1" -s "$2" exec-out \
    run-as "$3" \
    sed -E"$5" \
    -e "/$6/i$7" \
    "/data/data/$3/shared_prefs/$4"
}

# Delete edit
# executable, serial, package, filename, in-place/backup, matcher
function delete() {
    "$1" -s "$2" exec-out \
    run-as "$3" \
    sed -E"$5" \
    -e "/$6/d" \
    "/data/data/$3/shared_prefs/$4"
}

# Change edit
# executable, serial, package, filename, in-place/backup, matcher, content
function change() {
    "$1" -s "$2" exec-out \
    run-as "$3" \
    sed -E"$5" \
    -e "s/$6/$7/" \
    "/data/data/$3/shared_prefs/$4"
}

#####
# Execute based on the flavor of edit
#####
case $5 in
  add)
    # executable, serial, package, filename, in-place/backup, matcher, content
    add "$1" "$2" "$3" "$4" "$6" "$7" "$8"
    ;;
  delete)
    # executable, serial, package, filename, in-place/backup, matcher
    delete "$1" "$2" "$3" "$4" "$6" "$7"
    ;;
  change)
    # executable, serial, package, filename, in-place/backup, matcher, content
    change "$1" "$2" "$3" "$4" "$6" "$7" "$8"
esac