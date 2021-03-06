# Changelog

Hier sind alle wichtigen Änderungen dieses Projekts aufgeführt.
Es ersetzt die **Release Notes** aus den Anfangstagen von jFachwert und orientiert sich an 
[Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
genauso wie an [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Aus Gründen der Übersichtlichkeit sind bei älteren Versionen die einzelnen Patch-Versionen nicht extra aufgeführt, sondern in der Minor-Version.

## [4.0.2] - 2021-05-28
### Changed
- NULL-Werte sind jetzt auch von Java aus zugreifbar
- statische validate-Methoden sind wieder für Java verfügbar

## [4.0.1] - 2021-05-19
### Changed
- Java-Variante von Fachwert- und SimpleValidator wiederhergestellt und Kotlin-Variante in KFachwert und KSimpleValidator umbenannt,
  da es bei Interfaces mit Default-Implementierungen Kompatiblitätsprobleme gibt ([KT-4779](https://youtrack.jetbrains.com/issue/KT-4779)) 

## [4.0.0] - 2021-05-13
### Added
- Mehrwertsteuer-Klasse im steuer-Paket
- Zinssatz-Klasse im bank-Paket
- PZN-Klasse im med-Paket

### Changed
- Umstellung auf Kotlin.
- Architektur-Dokumentation aktualisiert
- Geldbetrag wurde jetzt mit javamoney-tck 1.1 getestet
- **Achtung:** Interface LocalizedException ist nicht kompatibel mit der alten Java-Version (LocalizedException).
  Grund dafür ist [KT-6653](https://youtrack.jetbrains.com/issue/KT-6653).


## [3.0] - 2019-10-09
### Added
- viele Fachklassen sind jetzt Comparable
- Unterstützung für Java 8 _und_ 11
- GeldbetragFormatter ist jetzt nur noch für internen Gebrauch gedacht
- Prozent-Klasse im math-Paket hinzugefügt
- Name-Klasse um _equalsSemantic(..)_ ergänzt

### Changed
- Die _equals_-Methode in _Name_ verwendet intern _equalsSemantic(..)_.
  Dies kann sich in künftigen Versionen ändern.
  Von daher sollte man statt _equals_(..) besser _equalsSemantic(..)_ oder _equalsExact(..)_ verwenden, je nachdem, welche Variante gewünscht ist.

## [2.3] - 2019-05-28
### Added
- LANR und BSNR um isPseudoNummer() erweitert
- EMailAdresse um getName() erweitert

## [2.2] - 2019-02-26
### Added
- **v2.2.3**: SmallUUID als Alternative zu TinyUUID hinzugefügt
- **v2.2.2**: Text.replaceUmlaute() ist jetzt um Faktor 4 schneller
- NULL-Objekte für die Initialisierung von Variablen
- Architektur-Dokumentation aktualisert
- Text-Klasse um `equalsIgnore`...`Umlaute(..)` ergänzt 

### Changed
- **v2.2.3** _fixed_ _[#7](https://github.com/oboehm/jfachwert/issues/7)_: Validierung für Rechnungsmonat vervollständigt
- **v2.2.2** _fixed_: ArrayIndexOutOfBoundException im Adressvergleich behoben.
- **v2.2.1**: Adressvergleich kommt jetzt mit mehr Sonderfällen klar
- **v2.2.1**: Namensvergleich funktioniert jetzt auch mit unterschiedlicher Anzahl von Vornamen besser

## [2.1] - 2019-02-20
### Added
- Logischer Vergleich bei Klassen im post-Package:
  Beim Vergleich von Adresse Ort und Strasse müssen sie nicht mehr exakt uebereinstimmen.
  So werden "73730 Esslingen, Badstr. 5" und "73730 Esslingen/N, Badstrasse 5" als die gleiche Adresse angesehen.
  _Achtung_: dieses Verhalte ist bis Version 2.0 anders - hier muss die Adresse exakt übereinstimmen.
- Will man den Vergleich exakt, gibt es dazu in diesen Klassen eine "equalsExact"-Methode.

## [2.0] - 2019-02-09
### Added
- formular-Package

### Changed
- Fachwerte mit String-Attributen wurden überarbeitet, um den Speicherbedarf zu reduzieren (Vermeidung von String-Duplikaten)
- beim Aufruf der of()-Methode werden Duplikate ebenfalls vermieden

### Removed
- Methoden, die als @deprecated gekennzeichnet sind, wurden entfernt.


## [1.0] - 2018-09-22
### Added
- Unterstützung von [JSR-354](http://javamoney.github.io/api.html) (Money und Currency API) durch eine Geldbetrag- und Waehrung-Klasse.
  Ziel ist es, den schwierigen Umgang mit Geldbeträgen zu vereinfachen.
  Bis auf [einen Test](https://github.com/JavaMoney/jsr354-tck/issues/18) wird das [JSR354-TCK](https://github.com/JavaMoney/jsr354-tck) eingehalten.
- JSON-Serialisierung (erfordert Einbindung von jackson-databind als abhängige Bibliothek)


## [0.7] - 2018-04-12
### Added
- TinyUUID als kleine Schwester zur UUID - reduziert den Platzbedarf um 63% (als String)

### Changed
- _verbessert_: Speicherverbrauch wurde teilweise drastisch reduziert.
  Dazu wurde intern auf PackedDecimal aus 0.6 zurückgegriffen, soweit möglich.
- **v0.7.1** _fixed_ _[#5](https://github.com/oboehm/jfachwert/issues/5)_: Fehler mit "Illegal base64 character 2d" in TinyUUID.fromString(..)

## [0.6] - 2018-04-02
### Added
- ein math-Paket mit Nummer-, Bruch- und PackedDecimal-Klasse.
  Der Datentyp [PackedDecimal](http://acc-gmbh.com/dochtml/Datentypen4.html) speichert die einzelnen Ziffern in Nibbles ab
  und ist eine Reminenzenz an das gute alte COBOL.
- **v0.6.1**: um die Informatik-Erstsemester und Programmierneulinge nicht ständig mit der Implementierung von Primzahlen zu quälen,
  gibt es diese Klasse jetzt fertig im math-Paket.

### Changed
* _verbessert_: Speicherverbrauch von Rechnungsmonat wurde um 75% reduziert.

## [0.5] - 2018-01-24
### Added
- eine FachwertFactory ist hinzugekommen, mit der beliebige (valide) Fachwerte angelegt werden können.
- im post-Paket gibt es jetzt noch eine Adressat-Klasse.

### Changed
- Exceptions haben jetzt ein eigenes Package unterhalb von de.jfachwert.pruefung bekommen.
