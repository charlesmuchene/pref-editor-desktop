@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.desktop)
}

group = "com.charlesmuchene.prefedit"
version = "1.0.0"

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.JETBRAINS
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.JETBRAINS
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material") // we're jeweling up! :D
    }
    implementation(libs.jewel.standalone)
    implementation(libs.jewel.deco.window)
    implementation(libs.okio)

    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "com.charlesmuchene.prefedit.App"

        jvmArgs("-Dorg.jetbrains.jewel.debug=true")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Preferences Editor"
            packageVersion = "1.0.0"
            description = "Edit shared preferences"
            vendor = "Charles Muchene"
        }
    }

    tasks {
        withType<JavaExec> {
            afterEvaluate {
                javaLauncher = project.javaToolchains.launcherFor {
                    languageVersion = JavaLanguageVersion.of(17)
                    vendor = JvmVendorSpec.JETBRAINS
                }
            }
        }
    }
}
