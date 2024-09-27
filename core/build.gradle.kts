plugins {
    id("org.jetbrains.dokka") version "1.9.10"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("de.jfachwert.java-conventions")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    api("org.apache.commons:commons-lang3:3.13.0")
    // optional dependency
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.15.2")
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

description = "core"