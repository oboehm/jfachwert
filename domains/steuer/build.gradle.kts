/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

plugins {
    id("de.jfachwert.java-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":math"))
    testImplementation(project(":test"))
    compileOnly("javax.money:money-api:1.1")
    testImplementation("org.javamoney:moneta-bp:1.4.1")
}

description = "steuer"
