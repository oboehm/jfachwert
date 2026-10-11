plugins {
    id("de.jfachwert.domain-conventions")
}

dependencies {
    api(project(":math"))
    implementation(libs.javax.money)
    testImplementation(project(":money"))
}
