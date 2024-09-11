/*
 * Diese Datei wurde ueber 'gradle init' erstellt und dann manuell nach und
 * nach angepasst.
 */

rootProject.name = "jfachwert-parent"
include(":core")
include(":test")
include(":math")
include(":money")
include(":bank")
//include(":rechnung")
//include(":net")
//include(":post")
//include(":formular")
//include(":domains")
//include(":zeit")
//include(":jfachwert")
//include(":steuer")
//include(":med")
project(":math").projectDir = file("domains/math")
project(":money").projectDir = file("domains/money")
project(":bank").projectDir = file("domains/bank")
//project(":rechnung").projectDir = file("domains/rechnung")
//project(":net").projectDir = file("domains/net")
//project(":post").projectDir = file("domains/post")
//project(":formular").projectDir = file("domains/formular")
//project(":zeit").projectDir = file("domains/zeit")
//project(":steuer").projectDir = file("domains/steuer")
//project(":med").projectDir = file("domains/med")
