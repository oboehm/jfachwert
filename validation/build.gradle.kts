plugins {
    id("org.jetbrains.dokka")
    id("de.jfachwert.java-conventions")
}

dependencies {
    api(project(":core"))
    testImplementation(project(":test"))
}

description = "validation"
