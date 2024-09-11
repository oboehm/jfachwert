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
    implementation("javax.money:money-api:1.1")
    testImplementation(project(":test"))
    testImplementation("org.javamoney:javamoney-tck:1.1") {
        exclude(group = "org.javamoney", module = "moneta")
    }
    testImplementation("org.javamoney:moneta-bp:1.4.1")
}

description = "money"
