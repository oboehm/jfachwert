
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst. Als Vorlage diente dabei u.a.
 * https://github.com/patternfly-kotlin/patternfly-kotlin
 */

group = "de.jfachwert"
version = "6.5.1"

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

dependencies {
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.junit.platform:junit-platform-commons:1.10.0")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("org.slf4j:slf4j-api:2.0.17")
    testImplementation("org.patterntesting:patterntesting-rt:2.5.1")
}

// ------------------------------------------------------ source & javadoc

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.main.get().kotlin)
    DuplicatesStrategy.WARN
}

val javadocJar by tasks.creating(Jar::class) {
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

    kotlin {
        jvmToolchain(11)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // ./gradlew assemble
    artifacts {
        archives(sourceJar)
        archives(javadocJar)
        archives(jar)
    }
}

// workaround for "Entry de/jfachwert/Fachwert.java is a duplicate..."
tasks.named<org.gradle.jvm.tasks.Jar>("sourceJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// ------------------------------------------------------ sign & publish

java.sourceCompatibility = JavaVersion.VERSION_11
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
