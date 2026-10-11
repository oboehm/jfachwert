/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

plugins {
    id("de.jfachwert.domain-conventions")
}

dependencies {
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
    api(project(":validation"))
}

description = "jFachwert"
