plugins {
    id("org.jetbrains.dokka")
    id("de.jfachwert.java-conventions")
}

dependencies {
    api(project(":core"))
    api("jakarta.validation:jakarta.validation-api:3.1.0")
    testImplementation(project(":test"))
    testImplementation(project(":bank"))
}

description = "validation"
