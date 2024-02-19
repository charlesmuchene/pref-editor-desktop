@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.desktop)
    alias(libs.plugins.detekt)
}

group = "com.charlesmuchene.prefeditor"
version = "1.0.0-SNAPSHOT"

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = 17
    }
}

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = 17
    }
}

dependencies {
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material") // we're jeweling up! :D
    }
    implementation(libs.immutable.collections)
    implementation(libs.jewel.deco.window)
    implementation(libs.jewel.standalone)
    implementation(libs.logging)
    implementation(libs.kxml2)
    runtimeOnly(libs.sl4j)

    detektPlugins(libs.detekt.compose.rules)

    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "com.charlesmuchene.prefeditor.App"

        jvmArgs("-Dorg.jetbrains.jewel.debug=false", "-Dorg.slf4j.simpleLogger.defaultLogLevel=debug")

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Preferences Editor"
            packageVersion = "1.0.0"
            description = "View/Edit preferences"
            vendor = "Charles Muchene"
            licenseFile = rootProject.file("LICENSE")

            macOS {
                dockName = "Preferences Editor"
                bundleID = "com.charlesmuchene.prefeditor"
                iconFile = file(path = "icons/prefeditor.icns")
            }

            windows {
                iconFile = file(path = "icons/prefeditor.ico")
            }

            linux {
                iconFile = file(path = "icons/prefeditor.png")
            }
        }
    }
}

tasks.withType<JavaExec> {
    afterEvaluate {
        javaLauncher =
            project.javaToolchains.launcherFor {
                vendor = JvmVendorSpec.JETBRAINS
                languageVersion = 17
            }
        setExecutable(javaLauncher.map { it.executablePath.asFile.absoluteFile }.get())
    }
}

@Suppress("unused")
fun Property<JavaLanguageVersion>.assign(version: Int) = set(JavaLanguageVersion.of(version))

detekt {
    config.from(file(path = "detekt.yaml"))
    buildUponDefaultConfig = true
}
