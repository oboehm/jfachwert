[![Build Status](https://travis-ci.org/oboehm/jfachwert.svg?branch=develop)](https://travis-ci.org/oboehm/jfachwert)
[![Coverage Status](https://coveralls.io/repos/github/oboehm/jfachwert/badge.svg?branch=develop)](https://coveralls.io/github/oboehm/jfachwert)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.jfachwert/jfachwert/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.jfachwert/jfachwert)
[![Quality Gate](https://sonarcloud.io/api/badges/gate?key=de.jfachwert:jfachwert)](https://sonarcloud.io/dashboard?id=de.jfachwert%3Ajfachwert%3Adevelop)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Was ist jFachwert?

jFachwert ist die Java-Implementierung des [Fachwert](https://de.wikipedia.org/wiki/Werkzeug-_und_Materialansatz#Fachwerte)-Konzepts
aus dem [Werkzeug- und Material](https://de.wikipedia.org/wiki/Werkzeug-_und_Materialansatz)-Ansatzes (WAM).
jFachwert erweitert die primitive Datentypen von Java um einige weitere Datentypen wie IBAN oder BIC, die in
vielen Business-Programmen zwar gebraucht werden, aber immer wieder neu implementiert werden (muessen).

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
Bankverbindung neu = createBankverbindung(new IBAN("DE41300606010006605605"), 
                                          new BIC("GENODEF1JEV"), "Jever Volksbank");
```

Jetzt kann man die Argumente nicht mehr verwechseln, weil sonst der Compiler meckert.


## Aktueller Stand

Die Arbeit hat begonnen und die Funktionaliaeten bei den wenig vorhandenen Fachwerten ist ueberschaubar.
Dennoch kann man sie bereits einsetzen und profitiert von sprechenderen Methoden-Parametern, die nicht
mehr verwechselt werden koennen. Wer Ideen fuer weitere Fachwerte hat, darf gerne mitmachen - sei es durch Code,
sei es durch Dokumentation oder einem schoenen Icon, oder sei es durch Anregungen und konstruktive Kritik.

Bereits in Version 0.0.2 gab es einer einfache [IBAN](http://jfachwert.de/apidocs/de/jfachwert/bank/IBAN.html)- und 
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
Die weitere Geschichte ist in den [Release Notes](http://jfachwert.de/changes-report.html) nachzulesen.


# Ziele

Ziel dieser Bibliothek ist es, fuer den deutschsprachigen Raum alle wichtigen Datentypen bereitzustellen, die man
sonst immer wieder selbst implementieren muesste. Mir ist klar, dass damit nicht alle Sonderfaelle abgedeckt werden
koennen - deswegen sind die Klassen auch nicht 'final', sondern koennen erweitert werden.

Juli 2017, Oli B.

---

# Weitere Infos

* [Release Notes](doc/release-notes.adoc)
* CI-Build: https://travis-ci.org/oboehm/jfachwert
* Projekt-Seite: http://www.jfachwert.de/
* Architektur-Dokumentation: [src/main/asciidoc](src/main/asciidoc/README.adoc)
* Entwickler-Dokumentation: [doc](doc/README.adoc)
