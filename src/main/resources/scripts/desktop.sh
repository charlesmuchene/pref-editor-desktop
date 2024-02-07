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
# matcher, content, filename
function add() {
  sed -i '' "/$1/ i\\
  $2
  " "$3"
}

# Delete edit
# matcher, filename
function delete() {
  sed -i '' "/$1/ d" "$2"
}

# Change edit
# matcher, change, filename
function change() {
  sed -i '' "s/$1/$2/" "$3"
}

#####
# Execute based on type of edit
#####
case $1 in
  add)
    # matcher, content, filename
    add "$2" "$3" "$4"
    ;;
  delete)
    # matcher, filename
    delete "$2" "$3"
    ;;
  change)
    # matcher, change, filename
    change "$2" "$3" "$4"
esac