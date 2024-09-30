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
    api(project(":math"))
    implementation("javax.money:money-api:1.1")
    testImplementation(project(":test"))
    testImplementation("org.javamoney:javamoney-tck:1.1") {
        exclude(group = "org.javamoney", module = "moneta")
    }
    testImplementation("org.javamoney:moneta-bp:1.4.1")
}

description = "money"
