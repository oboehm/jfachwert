plugins {
    id("de.jfachwert.domain-conventions")
}

dependencies {
    api(project(":math"))
    implementation(libs.javax.money)
    testImplementation(libs.javamoney.tck) {
        exclude(group = "org.javamoney", module = "moneta")
    }
    testImplementation(libs.moneta.bp)
}
