/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

plugins {
    id("de.jfachwert.java-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    api("javax.money:money-api:1.1")
    api("org.apache.commons:commons-lang3:3.13.0")
    // optional dependency
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.junit.platform:junit-platform-commons:1.10.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    implementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.mutabilitydetector:MutabilityDetector:0.10.6")
    testImplementation("org.javamoney:moneta-bp:1.4.1")
    testImplementation("org.javamoney:javamoney-tck:1.1") {
        exclude(group = "org.javamoney", module = "moneta")
        exclude(group = "org.mutabilitydetector", module = "MutabilityDetector")
    }
    implementation("org.patterntesting:patterntesting-rt:2.3.0")
    testImplementation("org.slf4j:slf4j-api:2.0.9")
}

description = "test"
