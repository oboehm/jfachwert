import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.dokka") version "1.9.10"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "de.jfachwert"
version = "5.3.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    api("javax.money:money-api:1.1")
    api("org.apache.commons:commons-lang3:3.13.0")
    // optional dependency
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation("org.junit.platform:junit-platform-commons:1.10.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.mutabilitydetector:MutabilityDetector:0.10.6")
    testImplementation("org.javamoney:moneta-bp:1.4.1")
    testImplementation("org.javamoney:javamoney-tck:1.1") {
        exclude(group = "org.javamoney", module = "moneta")
        exclude(group = "org.mutabilitydetector", module = "MutabilityDetector")
    }
    testImplementation("org.patterntesting:patterntesting-rt:2.3.0")
    testImplementation("org.slf4j:slf4j-api:2.0.9")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    useJUnitPlatform()
}

// Define the source JAR task
tasks.register("sourceJar", Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.dokkaJavadoc {
    outputDirectory = file("build/apidocs")
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
            "revdate"      to "2024",
            "organization" to "oli b."
        )
    )
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/kotlin")
        }
        java {
            srcDir("src/main/java")
        }
    }
    test {
        java {
            srcDir("src/test/java")
        }
    }
}
