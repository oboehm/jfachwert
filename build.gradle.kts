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

// s. https://asciidoctor.github.io/asciidoctor-gradle-plugin/development-3.x/user-guide/
tasks.asciidoctor {
    sourceDir("src/main/asciidoc/de/")
    setOutputDir("build/generated-docs")
    sources(delegateClosureOf<PatternSet> {
        include("index.adoc")
    })
    resources(delegateClosureOf<CopySpec> {
        from("src/main/asciidoc/images") {
            include("**/*.jpg", "**/*.gif", "**/*.png")
            exclude("**/.txt")
        }
        into("./images")
    })
    baseDirFollowsSourceDir()
}

// s. https://github.com/asciidoctor/asciidoctor-gradle-plugin/blob/master/docs/src/docs/asciidoc/parts/asciidoctor-diagram.adoc
asciidoctorj {
    modules {
        diagram.use()
        diagram.version("2.2.13")
    }
    options(mapOf("header_footer" to true))
    attributes(
        mapOf(
            "revnumber"    to "${project.version}",
            "revdate"      to "2025",
            "organization" to "oli b."
        )
    )
}

// ./gradlew publishToSonatype   -> check https://oss.sonatype.org/content/repositories/snapshots/de/jfachwert/
// ./gradlew publishToMavenLocal -> check local maven repo at ~/.m2/repository/de/jfachwert
nexusPublishing {
    repositories {
        sonatype {
            // credentials see ~/.gradle/gradle.properties
            // username.set("xxxxxxxx")
            // password.set("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
        }
    }
}
