/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

plugins {
    id("org.jetbrains.dokka")
    id("de.jfachwert.java-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":bank"))
    implementation(project(":formular"))
    implementation(project(":math"))
    implementation(project(":money"))
    implementation(project(":med"))
    implementation(project(":net"))
    implementation(project(":post"))
    implementation(project(":rechnung"))
    implementation(project(":steuer"))
    implementation(project(":zeit"))
}

description = "jFachwert"
