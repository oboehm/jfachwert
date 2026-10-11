plugins {
    id("de.jfachwert.domain-conventions")
}

dependencies {
    testImplementation(libs.moneta.bp)
    compileOnly(libs.javax.money)
}
