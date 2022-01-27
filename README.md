[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.jfachwert/jfachwert/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.jfachwert/jfachwert)
[![Build Status](https://travis-ci.com/oboehm/jfachwert.svg?branch=develop)](https://travis-ci.com/github/oboehm/jfachwert/branches)
[![Coverage Status](https://coveralls.io/repos/github/oboehm/jfachwert/badge.svg?branch=master)](https://coveralls.io/github/oboehm/jfachwert)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?metric=alert_status&project=de.jfachwert:jfachwert)](https://sonarcloud.io/dashboard?id=de.jfachwert%3Ajfachwert)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Was ist jFachwert?

jFachwert ist die Java-Implementierung des [Fachwert](https://de.wikipedia.org/wiki/Werkzeug-_und_Materialansatz#Fachwerte)-Konzepts
aus dem [Werkzeug- und Material](https://de.wikipedia.org/wiki/Werkzeug-_und_Materialansatz)-Ansatzes (WAM).
jFachwert erweitert die primitive Datentypen von Java um einige weitere Datentypen wie IBAN oder BIC, die in
vielen Business-Programmen zwar gebraucht werden, aber immer wieder neu implementiert werden (müssen).

Fachwerte sind sehr eng mit den [Value Objecs](https://de.wikipedia.org/wiki/Value_Object) aus Domain Driven Design (DDD)
verwandt und besitzen folgende Eigenschaften:

* unveraenderlich (immutable),
* einfacher Datentyp,
* serialisierbar.


## Vorteile

Neben des Vorteils der Immutabilitaet (Thread-Sicherheit) erhoeht die Verwenden von Fachwerten die Wartbarkeit und
Lesbarkeit von Programmen. Prinzipiell lassen sich die meisten dieser Fachwerte auch durch einen simplen String
ersetzen, aber dies fuehrt beim Einsatz von Methoden-Aufrufen oft zu Mehrdeutigkeiten:

```java
Bankverbindung neu = createBankverbindung("DE41300606010006605605", "GENODEF1JEV", "Jever Volksbank");
```

Was ist an dieser Erzeugung einer Bankverbindung IBAN, BIC und Bankname? Klar kann man hier in die API-Dokumentation
der createBankverbindung-Methode schauen, aber mal ehrlich - wer macht das schon. Eindeutiger wird es durch die
Verwendung von Fachwerten:

```java
Bankverbindung neu = createBankverbindung(IBAN.of("DE41300606010006605605"), 
                                          BIC.of("GENODEF1JEV"), "Jever Volksbank");
```

Jetzt kann man die Argumente nicht mehr verwechseln, weil sonst der Compiler meckert.


## Aktueller Stand

Version 4 basiert jetzt auf Kotlin, unterstützt aber weiterhin Java 8.
Die Kompatibilität mit der alten API garantieren JUnit-Tests, die weiterhin in Java verblieben sind.

Version 3 unterstützt nach wie vor Java 8, wurde aber auch mit Java 11 getestet.
Dies kam vor allem der (internen) Bereinigung in Zusammenhang der Geldbetrag-Klasse (die das Money-API implementiert) zu Gute.
Ferner wurden die Methodennamen sprechender und eindeutiger benannt.
So hat die Name-Klasse jetzt eine eigene egualsSemantic(..)-Methode für den semantischen Vergleich, der verschiedene Schreibweise von Namen als gleich behandelt.
Methoden und Klassen, die in 2.x als @Deprecated gekennzeichnet waren, wurden entfernt.

Mit 2.0 wurde der Speicherverbrauch dadurch reduziert, dass Duplikate vermieden werden, wenn man die of()-Methode benutzt.
Dies betrifft vor allem einfache Fachwerte, die nicht aus mehrerer Attribute zusammengesetzt sind.
Auch bei internen String-Attributen wurden Duplikate durch die Aufruf der `String.intern()`-Methode vermieden.

Mit 1.0 wurde ein stabiler Stand erreicht, den man produktiv in eigenen Projekten einsetzen kann.
Man profitiert dabei von sprechenderen Methoden-Parametern, die nicht
mehr verwechselt werden koennen. Wer Ideen fuer weitere Fachwerte hat, darf gerne mitmachen - sei es durch Code,
sei es durch Dokumentation oder einem schoenen Icon, oder sei es durch Anregungen und konstruktive Kritik.

So gab es bereits in Version 0.0.2 eine einfache [IBAN](http://jfachwert.de/apidocs/de/jfachwert/bank/IBAN.html)- und 
[BIC](http://jfachwert.de/apidocs/de/jfachwert/bank/BIC.html)-Klasse, die im 
[Maven-Repository](http://search.maven.org/#search%7Cga%7C1%7Cjfachwert)
unter folgenden Koordinaten zu finden ist:

```xml
  <groupId>de.jfachwert</groupId>
  <artifactId>jfachwert</artifactId>
```

Ab 0.1 kamen dann weitere [Bank-Klassen](http://jfachwert.de/apidocs/de/jfachwert/bank/package-summary.html)
sowie Packages fuer steuerliche oder postalische Klassen hinzu.
Mit 0.5 kam die FachwertFactory hinzu, mit der nicht nur beiliebige Fachwert-Klassen erzeugt werden koennen,
sondern die auch zur Validierung eingesetzt werden kann.
Und auch die Architektur wurde nach [arc42](src/main/asciidoc/README.adoc) dokumentiert.
Die weitere Geschichte ist in den [Release Notes](doc/release-notes.adoc) nachzulesen.


# Ziele

Ziel dieser Bibliothek ist es, fuer den deutschsprachigen Raum alle wichtigen Datentypen bereitzustellen, die man
sonst immer wieder selbst implementieren muesste. Mir ist klar, dass damit nicht alle Sonderfaelle abgedeckt werden
koennen - deswegen sind die Klassen auch nicht 'final', sondern koennen erweitert werden.

---

# Weitere Infos

* [Release Notes](CHANGELOG.md)
* Projekt-Seite: http://www.jfachwert.de/
* Architektur-Dokumentation: http://www.jfachwert.de/generated-docs/de/index.html
* Entwickler-Dokumentation: [doc](doc/README.adoc)
