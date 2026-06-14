
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
version = "6.7.0"

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
    compileOnly("tools.jackson.core:jackson-databind:3.1.4")
    testImplementation("tools.jackson.core:jackson-databind:3.1.4")
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("org.slf4j:slf4j-api:2.0.18")
    testImplementation("org.patterntesting:patterntesting-rt:2.5.1")
}

// ------------------------------------------------------ Kotlin & Java target

kotlin {
    jvmToolchain(17)
}

// ------------------------------------------------------ source & javadoc

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.main.get().kotlin)
    DuplicatesStrategy.WARN
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

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    withType<KotlinJvmCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(11)
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
