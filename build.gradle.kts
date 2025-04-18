@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.desktop)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
}

group = "com.charlesmuchene.prefeditor"
version = "1.2.0"

val langVersion = 21

kotlin {
    jvmToolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = langVersion
    }
    compilerOptions {
        optIn.add("kotlin.io.encoding.ExperimentalEncodingApi")
    }
}

java {
    toolchain {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = langVersion
    }
}

dependencies {
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material") // we're jeweling up! :D
    }
    implementation(libs.immutable.collections)
    implementation(libs.jewel.deco.window)
    implementation(libs.jewel.standalone)
    implementation(libs.rebugger)
    implementation(libs.logging)
    implementation(libs.parser)
    implementation(libs.kxml2)
    runtimeOnly(libs.sl4j)

    detektPlugins(libs.detekt.compose)

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

        jvmArgs("-Dorg.jetbrains.jewel.debug=false")

        buildTypes.release.proguard {
            isEnabled = false
            configurationFiles.from(file(path = "prefeditor.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Preferences Editor"
            description = "View/Edit preferences"
            vendor = "Charles Muchene"
            licenseFile = rootProject.file("LICENSE")

            modules("jdk.unsupported")

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
                languageVersion = langVersion
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

val versionTheRelease by tasks.registering {
    description = "Write version file to resources"
    doLast {
        file("src/main/resources/version")
            .writeText(text = "v$version")
    }
}

val jar by tasks.existing(Jar::class) {
    dependsOn(versionTheRelease)
}