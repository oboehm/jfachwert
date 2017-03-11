[![Build Status](https://travis-ci.org/oboehm/jfachwert.svg?branch=master)](https://travis-ci.org/oboehm/jfachwert) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# Was ist jFachwert?

jFachwert ist die Java-Implementierung des [Fachwert](https://de.wikipedia.org/wiki/Werkzeug-_und_Materialansatz#Fachwerte)-Konzepts
aus dem [Werkzeug- und Material](https://de.wikipedia.org/wiki/Werkzeug-_und_Materialansatz)-Ansatzes (WAM).
jFachwert erweitert die primitive Datentypen von Java um einige weitere Datentypen wie IBAN oder BIC, die in
vielen Business-Programmen zwar gebraucht werden, aber immer wieder neu implmentiert werden (muessen).

Fachwerte sind sehr eng mit den [Value Objecs](https://de.wikipedia.org/wiki/Value_Object) aus Domain Driven Design (DDD)
verwand und besitzen folgende Eigenschaften:

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
Bankverbindung neu = createBankverbindung(new  IBAN("DE41300606010006605605"), new BIC("GENODEF1JEV"), "Jever Volksbank");
```

Jetzt kann man die Argumente nicht mehr verwechseln, weil sonst der Compiler meckert.


## Aktueller Stand

Die Arbeit hat erst gerade begonnen und die Funktionaliaeten bei den wenig vorhandenen Fachwerten ist noch ausserst
bescheiden. Dennoch kann man sie bereits einsetzen und profitiert von sprechenderen Methoden-Parametern, die nicht
mehr verwechselt werden koennen. Wer Ideen fuer weitere Fachwerte hat, darf gerne mitmachen - sei es durch Code,
sei es durch Dokumentation oder einem schoenen Icon, oder sei es durch Anregungen und konstruktive Kritik.


# Ziele

Ziel dieser Bibliothek ist es, fuer den deutschsprachigen Raum alle wichtigen Datentypen bereitzustellen, die man
sonst immer wieder selbst implementieren muesste. Mir ist klar, dass damit nicht alle Sonderfaelle abgedeckt werden
koennen - deswegen sind die Klassen auch nicht 'final', sondern koennen erweitert werden.

Maerz 2017, Oli B.

---

# Weitere Infos

* CI-Build: https://travis-ci.org/oboehm/jenkins
* Projekt-Seite: http://www.jfachwert.de/ (geplant)
