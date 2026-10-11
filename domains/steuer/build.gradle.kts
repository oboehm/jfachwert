plugins {
    id("de.jfachwert.domain-conventions")
}

dependencies {
    api(project(":math"))
    testImplementation(libs.moneta.bp)
    compileOnly(libs.javax.money)
}
