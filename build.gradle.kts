import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "de.jfachwert"
version = "4.1.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven {
        url = uri("https://repository.jboss.org/nexus/content/repositories/releases/")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("javax.annotation:javax.annotation-api:1.3.2")
    api("com.google.code.findbugs:jsr305:3.0.2")
    api("javax.money:money-api:1.1")
    api("javax.validation:validation-api:2.0.1.Final")
    //compile ")com.fasterxml.jackson.core:jackson-databind"), optional
    api("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    api("org.apache.commons:commons-lang3:3.12.0")
    api("org.apache.commons:commons-collections4:4.4")
    api("org.apache.commons:commons-text:1.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation("org.junit.platform:junit-platform-commons:1.8.2")
    testImplementation("org.junit.platform:junit-platform-launcher:1.8.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.mutabilitydetector:MutabilityDetector:0.10.4")
    testImplementation("org.jboss.test-audit:jboss-test-audit-impl:1.1.4.Final")
    testImplementation("org.javamoney:moneta-bp:1.4.1")
    testImplementation("org.javamoney:javamoney-tck:1.1") {
        exclude(group = "org.javamoney", module = "moneta")
        exclude(group = "org.mutabilitydetector", module = "MutabilityDetector")
    }
    testImplementation("org.jboss.test-audit:jboss-test-audit-impl:1.1.4.Final")
    testImplementation("org.patterntesting:patterntesting-rt:2.2.20-YEARS")
    testImplementation("org.slf4j:slf4j-api:1.7.35")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
