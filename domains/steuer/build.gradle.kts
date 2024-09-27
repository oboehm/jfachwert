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
    implementation(project(":math"))
    testImplementation(project(":test"))
    testImplementation("org.javamoney:moneta-bp:1.4.1")
    compileOnly("javax.money:money-api:1.1")
}

description = "steuer"
