import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

plugins {
    kotlin("jvm")   // alternativ: id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.junit.platform:junit-platform-commons:1.10.0")
    testImplementation("org.slf4j:slf4j-api:2.0.7")
    testImplementation("org.patterntesting:patterntesting-rt:2.3.0")
}

group = "de.jfachwert"
version = "6.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}
//
//tasks.withType<JavaCompile>() {
//    options.encoding = "UTF-8"
//}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
