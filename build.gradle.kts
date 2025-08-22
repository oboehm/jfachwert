/*
 * Diese Datei wurde manuell fuer's Publishing nach Sonatype (Maven Central)
 * erstellt.
 */

import java.security.MessageDigest

plugins {
    id("maven-publish")
    id("signing")
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

signing {
    sign(publishing.publications["maven"])
}

// ./gradlew publishToSonatype   -> check https://oss.sonatype.org/content/repositories/snapshots/de/jfachwert/
// ./gradlew publishToMavenLocal -> check local maven repo at ~/.m2/repository/de/jfachwert
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            // credentials see ~/.gradle/gradle.properties
            // username.set("xxxxxxxx")
            // password.set("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
        }
    }
}

fun File.checksum(algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm)
    inputStream().use { fis ->
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (fis.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

subprojects {
    tasks.register("generateChecksums") {
        group = "publishing"
        description = "Generates .md5 and .sha1 files for all artifacts"

        doLast {
            // .md5- / .sha1-Dateien unter build/libs
            val artifactsDir = buildDir.resolve("libs")
            if (!artifactsDir.exists()) return@doLast

            artifactsDir.walkTopDown()
                .filter { it.isFile && (it.extension in listOf("jar", "pom")) }
                .forEach { file ->
                    val md5File = File(file.absolutePath + ".md5")
                    val sha1File = File(file.absolutePath + ".sha1")

                    md5File.writeText(file.checksum("MD5"))
                    sha1File.writeText(file.checksum("SHA-1"))

                    println("Generated: ${md5File.name}, ${sha1File.name} for ${file.name}")
                }

            // 2) Artefakte im lokalen Maven-Repo (~/.m2/repository)
            val mavenLocalRepo = File(System.getProperty("user.home"), ".m2/repository/de/jfachwert")
            if (mavenLocalRepo.exists()) {
                mavenLocalRepo.walkTopDown()
                    .filter { it.isFile && (it.extension in listOf("jar", "pom", "module")) }
                    .forEach { file ->
                        val md5File = File(file.absolutePath + ".md5")
                        val sha1File = File(file.absolutePath + ".sha1")
                        if (!md5File.exists()) md5File.writeText(file.checksum("MD5"))
                        if (!sha1File.exists()) sha1File.writeText(file.checksum("SHA-1"))
                        println("Generated (mavenLocal): ${md5File.name}, ${sha1File.name} for ${file.name}")
                    }
            }
        }
    }
}