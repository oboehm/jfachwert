plugins {
    id("org.jetbrains.dokka")
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("de.jfachwert.java-conventions")
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    api("org.apache.commons:commons-lang3:3.17.0")
    // optional dependency
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.19.0")
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

description = "core"