# Changelog

Hier sind alle wichtigen Änderungen dieses Projekts aufgeführt.
Es ersetzt die **Release Notes** aus den Anfangstagen von jFachwert und orientiert sich an 
[Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
genauso wie an [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Aus Gründen der Übersichtlichkeit sind bei älteren Versionen die einzelnen Patch-Versionen nicht extra aufgeführt, sondern in der Minor-Version.

## [4.4.3] - 2023-01-13
### Added
- of(..)-Methoden in Bankverbindung ergänzt

## [4.4.2] - 2023-01-09
### Fixed
- Default-Implementierung der verify-Methode (KSimpleValidator) wandelt wieder `javax.validation.ValidationException` in `IllegalArgumentException` um

## [4.4.1] - 2023-01-08
### Changed
- Abhängigkeit zu com.google.code.findbugs:jsr305 entfernt
- Abhängigkeit zu javax.validation weiter reduziert
- auf [GitHub Actions](https://github.com/oboehm/jfachwert/actions) umgestellt
### Security
* Bump jackson-databind from 2.12.6.1 to 2.13.4.1 by @dependabot in [#17](https://github.com/oboehm/jfachwert/pull/17)
* Bump commons-text from 1.9 to 1.10.0 by @dependabot in [#18](https://github.com/oboehm/jfachwert/pull/18)

## [4.4.0] - 2022-10-14
### Changed
- Abhängigkeit zu commons-collections4 entfernt
- Abhängigkeit zu javax.validation für Entfernung vorbereitet
### Security
- Abhängigkeit zu commons-text entfernt
  ([CVE-2022-42889](https://github.com/advisories/GHSA-599f-7c49-w659/dependabot))
### Fixed
- Encoding-Probleme unter Java 17 behoben

## [4.3] - 2022-08-27
### Added
- Text.of(..) akzeptiert jetzt als 2. Parameter einen Zeichensatz
- Text-Konvertierung nach ASCII (und andere Zeichensätze) wird unterstützt
### Fixed
- 'Text.replaceUmlaute(..)' ersetzt jetzt 'ł' durch 'l'

## [4.2] - 2022-02-11
### Added
- Text-Klasse bietet Encoding-Erkennung und -Umwandlung als leichtgewichtige Alternative zu [Tika](https://tika.apache.org/) an
### Fixed
- **v4.2.3**: `Text.convert(..)` kann jetzt auch 'Senftenberg/Zły Komorow' und andere polnische Grenz-Städte nach ISO-8859-1 konvertieren
- **v4.2.2**: '¿' und '¡' werden als druckbares Zeichen erkannt
  ([#16](https://github.com/oboehm/jfachwert/issues/16))
- **v4.2.2**: Ungenauigkeit bei der IBAN-Validierung korrigiert
- **v4.2.1**: Validator- und Exception-Klassen können wieder abgeleitet werden
  ([#15](https://github.com/oboehm/jfachwert/issues/15))
### Changed
- **v4.2.2**: IBAN nicht mehr von Text abgeleitet
- Tests komplett auf JUnit 5 umgestellt

## [4.1] - 2022-01-11
### Added
- Text.isPrintable(..) gibt es jetzt auch als statische Methode
- fehlende Methoden in Zahlenwert implementiert
### Changed
- Bau und Test unter Java 11
### Fixed
- Probleme mit fehlender CharBuffer.rewind()-Methode in Text-Klasse unter Java 8 behoben


## [4.0] - 2021-05-13
### Fixed
- **v4.0.4**: TinyUUID.randomUUID() mit @JvmStatic gekennzeichnet
  ([#13](https://github.com/oboehm/jfachwert/issues/13))
### Security
- **v4.0.3**: (experimentelle) Abhängigkeit zu Log4J und LogAspect entfernt
### Added
- **v4.0.3**: Text.isPrintable()
- **v4.0.1**: Mehrwertsteuer-Klasse im steuer-Paket
- **v4.0.1**: Zinssatz-Klasse im bank-Paket
- **v4.0.1**: PZN-Klasse im med-Paket
### Changed
- **v4.0.2**: NULL-Werte sind jetzt auch von Java aus zugreifbar
- **v4.0.2**: statische validate-Methoden sind wieder für Java verfügbar
- **v4.0.1**: Java-Variante von Fachwert- und SimpleValidator wiederhergestellt und Kotlin-Variante in KFachwert und KSimpleValidator umbenannt,
  da es bei Interfaces mit Default-Implementierungen Kompatiblitätsprobleme gibt ([KT-4779](https://youtrack.jetbrains.com/issue/KT-4779))
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
