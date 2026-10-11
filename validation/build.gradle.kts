plugins {
    id("de.jfachwert.domain-conventions")
}

dependencies {
    api(libs.jakarta.validation)
    testImplementation(project(":bank"))
}
