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

//dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
//    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.20")
//    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
//    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
//    testImplementation("org.junit.platform:junit-platform-commons:1.10.0")
//    testImplementation("org.slf4j:slf4j-api:2.0.7")
//    testImplementation("org.patterntesting:patterntesting-rt:2.3.0")
//    testImplementation("org.javamoney:javamoney-tck:1.1")
//    testImplementation("org.testng:testng:7.8.0")
//    testImplementation("org.mutabilitydetector:MutabilityDetector:0.10.6")
//    testImplementation("org.javamoney:moneta-bp:1.4.1")
//    testImplementation("javax.annotation:javax.annotation-api:1.3.2")
//}

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
//
//tasks.named<Test>("test") {
//    useTestNG()
//}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
