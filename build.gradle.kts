/*
 * Diese Datei wurde manuell fuer's Publishing nach Sonatype (Maven Central)
 * erstellt.
 */

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("org.jetbrains.dokka")
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("de.jfachwert.java-conventions")
    signing
}

// ./gradlew publishToSonatype
// check https://oss.sonatype.org/content/repositories/snapshots/de/jfachwert/
nexusPublishing {
    repositories {
        sonatype() {
            // credentials see ~/.gradle/gradle.properties
            // username.set("xxxxxxxx")
            // password.set("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
        }
    }
}
