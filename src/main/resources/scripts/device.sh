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

# Add
# executable, serial, package, filename, matcher, content
function add() {
    "$1" -s "$2" exec-out \
    run-as "$3" \
    sed -Ei \
    -e "/$5/i$6" "$4"
}

# Delete
# executable, serial, package, filename, matcher
function delete() {
    "$1" -s "$2" exec-out \
    run-as "$3" \
    sed -Ei \
    -e "/$5/d" "$4"
}

# Change
# executable, serial, package, filename, matcher, content
function change() {
    "$1" -s "$2" exec-out \
    run-as "$3" \
    sed -Ei \
    -e "s/$5/$6/" "$4"
}

# Replace
# executable, serial, package, filename, content
function replace() {
    "$1" -s "$2" exec-out \
    "run-as $3 sh -c \
    \"echo $5 | base64 -d | dd of=$4 status=none"
}

# Backup file
# executable, serial, package, filename, backup
function backup() {
    "$1" -s "$2" exec-out \
    run-as "$3" \
    cp "$4" "$5"
}

#####
# Execute based on the flavor of edit
#####
case $5 in
  add)
    # executable, serial, package, filename, matcher, content
    add "$1" "$2" "$3" "$4" "$6" "$7"
    ;;
  delete)
    # executable, serial, package, filename, matcher
    delete "$1" "$2" "$3" "$4" "$6"
    ;;
  change)
    # executable, serial, package, filename, matcher, content
    change "$1" "$2" "$3" "$4" "$6" "$7"
    ;;
  replace)
    # executable, serial, package, filename, content
    replace "$1" "$2" "$3" "$4" "$6"
    ;;
  backup)
    # executable, serial, package, filename, backup
    backup "$1" "$2" "$3" "$4" "$6"
esac