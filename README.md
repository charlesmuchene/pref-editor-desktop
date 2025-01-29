# Preferences Editor
A desktop app to view and edit on-device preferences. The app supports reading/writing _legacy_ **shared preferences**.
**Datastore-based** preferences are currently supported in read-only mode. 😎

Built using:
* [Compose](https://github.com/JetBrains/compose-multiplatform) for Desktop
* Themed by [Jewel](https://github.com/JetBrains/jewel)

# Installation
> Ensure `adb` is available on your `PATH`.

Find and install the [latest release](https://github.com/charlesmuchene/pref-editor-desktop/releases/latest).
The app can be run from source too. See the section on [Building](#building) below.

# Usage

>  [!IMPORTANT]  
>  A local installation of ADB is required. It should be included in the Android SDK Platform Tools package downloaded while setting up Android Studio.
If not, download the package with the SDK Manager or get the standalone tools from [here](https://developer.android.com/studio/releases/platform-tools).

From the app's window:
* Select device
* Choose your app
* Select preferences file
* View/Edit preferences
* Star any item for easier filtering

| Device Listing                                | Editing Preferences                        |
|-----------------------------------------------|--------------------------------------------|
| ![image](./images/01-pref-editor-devices.png) | ![image](./images/04-pref-editor-edit.png) |

And we go dark too 😎

| Preferences Files Listing                   | Filtering is supported                         |
|---------------------------------------------|------------------------------------------------|
| ![image](./images/03-pref-editor-files.png) | ![image](./images/02-pref-editor-filtered.png) |

# Building
* [Clone](https://github.com/charlesmuchene/pref-editor-desktop/archive/refs/heads/main.zip) the project
* Add [parser](https://github.com/charlesmuchene/datastore-preferences) as a dependency e.g. publishing it locally
* Execute `./gradlew run`. 

# License

    Copyright (c) 2024 Charles Muchene
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
