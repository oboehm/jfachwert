/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

rootProject.name = "jfachwert-parent"
include(":core")
include(":test")
include(":math")
include(":money")
include(":bank")
include(":formular")
include(":med")
include(":post")
include(":net")
include(":rechnung")
include(":steuer")
include(":zeit")
include(":jfachwert")
project(":math").projectDir = file("domains/math")
project(":money").projectDir = file("domains/money")
project(":bank").projectDir = file("domains/bank")
project(":formular").projectDir = file("domains/formular")
project(":med").projectDir = file("domains/med")
project(":post").projectDir = file("domains/post")
project(":net").projectDir = file("domains/net")
project(":rechnung").projectDir = file("domains/rechnung")
project(":steuer").projectDir = file("domains/steuer")
project(":zeit").projectDir = file("domains/zeit")

pluginManagement {
    plugins {
        id("org.jetbrains.dokka") version "1.9.20"
        id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    }
    repositories {
        gradlePluginPortal()
    }
}
