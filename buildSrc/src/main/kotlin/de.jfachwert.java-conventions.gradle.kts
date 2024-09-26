import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

group = "de.jfachwert"
version = "6.0.0-SNAPSHOT"

object Meta {
    const val desc = "Implementierung einiger Fachwerte nach dem WAM-Ansatz"
    const val license = "Apache-2.0"
    const val githubRepo = "oboehm/jfachwert"
    const val release = "https://s01.oss.sonatype.org/service/local/"
    const val snapshot = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
}

// ------------------------------------------------------ plugins

plugins {
    kotlin("jvm")   // alternativ: id("org.jetbrains.kotlin.jvm")
    `maven-publish`
    signing
}

// ------------------------------------------------------ repositories

val repositories = arrayOf(
    "https://oss.sonatype.org/content/repositories/snapshots/",
    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
)

repositories {
    mavenLocal()
    mavenCentral()
    repositories.forEach { maven(it) }
}

// ------------------------------------------------------ dependencies

dependencies {
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.junit.platform:junit-platform-commons:1.10.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.slf4j:slf4j-api:2.0.7")
    testImplementation("org.patterntesting:patterntesting-rt:2.3.0")
}

// ------------------------------------------------------ source & javadoc

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.main.get().kotlin)
}

//val javadocJar by tasks.creating(Jar::class) {
//    group = JavaBasePlugin.DOCUMENTATION_GROUP
//    description = "Assembles Javadoc JAR"
//    archiveClassifier.set("javadoc")
//    from(tasks.named("dokkaHtml"))
//}

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

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
}

// ------------------------------------------------------ sign & publish

java.sourceCompatibility = JavaVersion.VERSION_11
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

signing {
    setRequired({
        (project.extra["isReleaseVersion"] as Boolean) && gradle.taskGraph.hasTask("publish")
    })
    sign(configurations.runtimeElements.get())
}

//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}
//
//tasks.withType<JavaCompile>() {
//    options.encoding = "UTF-8"
//}
