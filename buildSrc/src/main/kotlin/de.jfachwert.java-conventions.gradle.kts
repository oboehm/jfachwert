
import org.cyclonedx.gradle.CyclonedxDirectTask
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst. Als Vorlage diente dabei u.a.
 * https://github.com/patternfly-kotlin/patternfly-kotlin
 */

group = "de.jfachwert"
version = "6.8.2-SNAPSHOT"
description = project.name

object Meta {
    const val desc = "Implementierung einiger Fachwerte nach dem WAM-Ansatz"
    const val license = "Apache-2.0"
    const val githubRepo = "oboehm/jfachwert"
}

// ------------------------------------------------------ plugins

plugins {
    kotlin("jvm")   // alternativ: id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    signing
    id("org.cyclonedx.bom")
    id("org.jetbrains.dokka")
}

// ------------------------------------------------------ repositories

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "ossrh-staging-api"
        url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
        credentials {
            username = findProperty("sonatypeUsername") as String?
            password = findProperty("sonatypePassword") as String?
        }
    }
}

// ------------------------------------------------------ dependencies

// Version-Catalog (gradle/libs.versions.toml) zentral nutzen. In
// Precompiled-Script-Plugins ist kein `libs`-Accessor verfuegbar, daher
// wird der Katalog ueber die VersionCatalogsExtension aufgeloest.
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    compileOnly(libs.findLibrary("jackson-databind").get())
    testImplementation(libs.findLibrary("jackson-databind").get())
    testImplementation(platform(libs.findLibrary("junit-bom").get()))
    testImplementation(libs.findLibrary("junit-jupiter").get())
    testImplementation(libs.findLibrary("junit-jupiter-engine").get())
    testImplementation(libs.findLibrary("junit-jupiter-params").get())
    testImplementation(libs.findLibrary("junit-platform-launcher").get())
    testImplementation(libs.findLibrary("hamcrest").get())
    testImplementation(libs.findLibrary("slf4j-api").get())
    testImplementation(libs.findLibrary("patterntesting-rt").get())
}

// ------------------------------------------------------ Kotlin & Java target

kotlin {
    jvmToolchain(17)
}

// ------------------------------------------------------ source & javadoc

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.main.get().kotlin)
}

val javadocJar by tasks.registering(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Javadoc JAR"
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaHtml"))
}

// ------------------------------------------------------ Kotlin, testing & dokka

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            // set options for log level LIFECYCLE
            events(TestLogEvent.FAILED, TestLogEvent.STANDARD_OUT, TestLogEvent.STANDARD_ERROR)
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    assemble {
        dependsOn("cyclonedxDirectBom")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    withType<KotlinJvmCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(11)
    }

}

// workaround for "Entry de/jfachwert/Fachwert.java is a duplicate..."
tasks.named<org.gradle.jvm.tasks.Jar>("sourceJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// ------------------------------------------------------ sign & publish

extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

signing {
    setRequired({
        (project.extra["isReleaseVersion"] as Boolean) && gradle.taskGraph.hasTask("publish")
    })
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["kotlin"])
            artifact(tasks["sourceJar"])
            artifact(tasks["javadocJar"])
            // SBOMs (CycloneDX) mit Classifier 'cyclonedx' publizieren - wie beim Maven-Build
            val cyclonedxBom = tasks.named<CyclonedxDirectTask>("cyclonedxDirectBom")
            artifact(cyclonedxBom.flatMap { it.jsonOutput }) { classifier = "cyclonedx" }
            artifact(cyclonedxBom.flatMap { it.xmlOutput }) { classifier = "cyclonedx" }
            pom {
                name.set(project.name)
                description.set(Meta.desc)
                url.set("https://github.com/${Meta.githubRepo}")
                licenses {
                    license {
                        name.set(Meta.license)
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("oboehm")
                        name.set("Oli B.")
                        organization.set("JUGS")
                        organizationUrl.set("ob@jfachwert.de")
                    }
                }
                scm {
                    url.set("https://github.com/${Meta.githubRepo}.git")
                    connection.set("scm:git:git://github.com/${Meta.githubRepo}.git")
                    developerConnection.set("scm:git:git://github.com/#${Meta.githubRepo}.git")
                }
                issueManagement {
                    url.set("https://github.com/${Meta.githubRepo}/issues")
                }
            }
        }
    }
}
