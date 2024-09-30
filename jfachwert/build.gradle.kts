/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

plugins {
    id("org.jetbrains.dokka")
    id("de.jfachwert.java-conventions")
}

dependencies {
    api(project(":core"))
    api(project(":bank"))
    api(project(":formular"))
    api(project(":math"))
    api(project(":money"))
    api(project(":med"))
    api(project(":net"))
    api(project(":post"))
    api(project(":rechnung"))
    api(project(":steuer"))
    api(project(":zeit"))
}

description = "jFachwert"
