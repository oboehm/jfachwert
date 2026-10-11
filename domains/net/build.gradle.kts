plugins {
    id("de.jfachwert.domain-conventions")
}

dependencies {
    api(project(":math"))
    implementation(project(":post"))
}
