/*
 * Konventions-Plugin fuer alle Fachwert-Domaenen-Module: ergaenzt
 * de.jfachwert.java-conventions um die gemeinsamen Abhaengigkeiten auf
 * die Basis-Module :core und :test.
 */

plugins {
    id("de.jfachwert.java-conventions")
}

dependencies {
    api(project(":core"))
    testImplementation(project(":test"))
}
