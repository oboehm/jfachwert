/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

plugins {
    id("de.jfachwert.java-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation(platform(libs.junit.bom))
    implementation(libs.junit.jupiter)
    implementation(libs.hamcrest)
    implementation(libs.patterntesting.rt)
    api(libs.javax.money)
    testImplementation(libs.mutabilitydetector)
    testImplementation(libs.moneta.bp)
    testImplementation(libs.javamoney.tck) {
        exclude(group = "org.javamoney", module = "moneta")
        exclude(group = "org.mutabilitydetector", module = "MutabilityDetector")
    }
    testImplementation(libs.slf4j.api)
}
