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
include(":formular")
include(":med")
include(":post")
include(":net")
include(":rechnung")
//include(":domains")
//include(":zeit")
//include(":jfachwert")
//include(":steuer")
project(":math").projectDir = file("domains/math")
project(":money").projectDir = file("domains/money")
project(":bank").projectDir = file("domains/bank")
project(":formular").projectDir = file("domains/formular")
project(":med").projectDir = file("domains/med")
project(":post").projectDir = file("domains/post")
project(":net").projectDir = file("domains/net")
project(":rechnung").projectDir = file("domains/rechnung")
//project(":zeit").projectDir = file("domains/zeit")
//project(":steuer").projectDir = file("domains/steuer")
