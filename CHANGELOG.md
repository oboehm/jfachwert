# Changelog

Hier sind alle wichtigen Änderungen dieses Projekts aufgeführt.
Es ersetzt die **Release Notes** aus den Anfangstagen von jFachwert und orientiert sich an 
[Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
genauso wie an [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Aus Gründen der Übersichtlichkeit sind bei älteren Versionen die einzelnen Patch-Versionen nicht extra aufgeführt, sondern in der Minor-Version.

## [Planned]
- Speichervergleich BigDecimal zu PackedDecimal

## [Unreleased]
### Fixed
- Caching von Bestellnummer.of(..) korrigiert

## [6.3.1] - 2025-06-13
### Added
- Prozent.of(double) und Promille.of(double) hinzugefügt
- Konstanten Prozent.HUNDRED und Promille.THOUSAND hinzugefügt
### Fixed
- Caching von PLZ.of(..) korrigiert

## [6.3.0] - 2025-06-07
### Fixed
- Postfach-Validierung entschärft, Postfach ohne Ort jetzt auch erlaubt
  ([#30](https://github.com/oboehm/jfachwert/issues/30))

## [6.2.2] - 2025-05-29
### Fixed
- fehlerhaftes Caching von Zinssatz im bank-Package korrigiert
- fehlerhaftes Caching von Prozent und Promille im math-Package korrigiert
- fehlerhaftes Caching von SNOMED im med-Package korrigiert
- fehlerhaftes Caching von Mehrwertsteuer im steuer-Package korrigiert

## [6.2.1] - 2025-05-27
### Fixed
- fehlerhaftes Caching in Text.of(..) und anderen Fachwerten korrigiert 
  ([#29](https://github.com/oboehm/jfachwert/issues/29))

## [6.2.0] - 2025-05-12
### Added
- Text.trim(), das auch geschützte Leerzeichen entfernt
- Geldbetrag.format(..) zur Steuerung der String-Ausgabe
### Fixed
- Geldbetrag.toString() verwendet geschütztes Leerzeichen
  ([#28](https://github.com/oboehm/jfachwert/issues/28))

## [6.1.1] - 2025-04-28
### Fixed
- Validierung negativer Beträge
  ([#27](https://github.com/oboehm/jfachwert/issues/27))

## [6.1.0] - 2025-04-14
### Added
- LEGS (Leistungserbringergruppenschlüssel) in med-Package hinzugefügt
- Geldbetrag.toCent() und Geldbetrag.getBetrag() hinzugefügt
- Geldbetrag.ONE und Geldbetrag.TEN als Konstanten hinzugefügt
### Fixed
- Typo in `SimpleValidator.isValid(..)` korrigiert
  ([#26](https://github.com/oboehm/jfachwert/issues/26))

## [6.0.2] - 2025-01-02
### Added
- weitere Unterstützung für offene Zeiträume
  ([#23](https://github.com/oboehm/jfachwert/issues/23))
- Zeitdauer.of(long) hinzugefügt


## [6.0.1] - 2024-09-30
### Fixed
- Kompatibilität zur alten Geldbetrag-Klassen aus dem bank-Package (jetzt: money-Modul) erhöht
- transitive Abhängigkeiten zu Modulen in de.jfachwert:jfachwert


## [6.0.0] - 2024-09-29
### Changed
- Aufteilung in Module zur Verringerung der Abhängigkeiten
- Umstellung auf Gradle zum Bau und Deployment
- GeldBetrag & Co in eigenes Modul (wegen Abhängigkeit zu javax.money:money-api)
- Package von Geldbetrag & Waehrung ist jetzt `de.jfachwert.money`

---

## [5.5] - 2024-07-12
### Added
- Hilfsmittelnummer im med-Package hinzugefügt
- PZN um toShortString() erweitert, der PZN ohne Prefix ausgibt

## [5.4] - 2024-07-09
### Fixed
- **v5.4.3**: fehlende Abhängigkeit von `javax.money:money-api` führt nicht mehr zu fehlerhafter Initialisierung der FachwertFactory-Klasse
  ([#25](https://github.com/oboehm/jfachwert/issues/25))
### Changed
- **v5.4.1**: Optimierung von Text.isPrintable()
### Added
- **v5.4.2**: Anrede um DAMEN und HERREN ergänzt
  ([#24](https://github.com/oboehm/jfachwert/issues/24))
- Zeiteinheit für große Einheiten als Ergänzung zu TimeUnit eingeführt
- Zeitpunkt kann jetzt bis zum Urknall zurückgehen
- Rechnungsnummer kann jetzt mit Zahl angegelegt und zurückgegeben werden 
  ([#22](https://github.com/oboehm/jfachwert/issues/22))
- ZANR kann jetzt mit LANR erzeugt werden
  ([#21](https://github.com/oboehm/jfachwert/issues/21))

## [5.3] - 2024-02-10
### Added
- ZANR-Klasse im med-Package
- Zeitpunkt- und Zeitraum-Klasse akzeptieren Date als of(..)-Parameter

## [5.2] - 2024-01-22
### Added
- Fachwert-Klassen haben jetzt eine isValid()-Methode (falls man den Validator abschaltet bzw. austauscht)
- Zeitraum-Klasse
### Breaking Changes
- Zeitdauer: startTime und endTime sind jetzt private Properties; für Zeiträume gibt es jetzt die Zeitraum-Klasse

## [5.1] - 2024-01-09
### Fixed
- **v5.1.1**: führende Nullen bei IKs führen nicht mehr zu Valdierungsfehlern
  ([#20](https://github.com/oboehm/jfachwert/issues/20))
### Added
- med-Package: 
  - Versichertennummer (7-Stellig) und [Krankenversichertennummer](https://de.wikipedia.org/wiki/Krankenversichertennummer)
  - systematisierte Nomenklatur der Medizin [SNOMED](https://de.wikipedia.org/wiki/Systematisierte_Nomenklatur_der_Medizin)

## [5.0] - 2023-09-14
### Added
- **v5.0.1**: Zeitdauer-Klasse um getTimeInMillis(), start() und stop() ergänzt
- zeit-Package mit Zeitdauer-Klasse hinzugefügt
- Text.toPrintable() hinzugefügt
### Fixed
- **v5.0.1**: versteckte Abhängigkeit zu javax.validation entfernt
### Changed
- Logging erfolgt maximal im Log-Level FINE und auf Deutsch
- Logging von behandelten Exceptions (Stacktrace) wird nur im Log-Level FINER ausgegeben
- Exceptions mit deutschen Fehlermeldungen

---

## [4.5] - 2023-05-06
### Changed
- Bibliothek kann jetzt ohne Abhängigkeit zu `javax.money:money-api` betrieben werden (optional)
- abhängige Bibliotheken jeweils auf den neuesten Stand gebracht

## [4.4] - 2022-10-14
### Added
- **v4.4.3**: of(..)-Methoden in Bankverbindung ergänzt
### Fixed
- **v4.4.2**: Default-Implementierung der verify-Methode (KSimpleValidator) wandelt wieder `javax.validation.ValidationException` in `IllegalArgumentException` um
- Encoding-Probleme unter Java 17 behoben
### Changed
- **v4.4.1**: Abhängigkeit zu com.google.code.findbugs:jsr305 entfernt
- **v4.4.1**: Abhängigkeit zu javax.validation weiter reduziert
- **v4.4.1**: auf [GitHub Actions](https://github.com/oboehm/jfachwert/actions) umgestellt
- Abhängigkeit zu commons-collections4 entfernt
- Abhängigkeit zu javax.validation für Entfernung vorbereitet
### Security
- **v4.4.1**: Bump jackson-databind from 2.12.6.1 to 2.13.4.1 by @dependabot in [#17](https://github.com/oboehm/jfachwert/pull/17)
- **v4.4.1**: Bump commons-text from 1.9 to 1.10.0 by @dependabot in [#18](https://github.com/oboehm/jfachwert/pull/18)
- Abhängigkeit zu commons-text entfernt
  ([CVE-2022-42889](https://github.com/advisories/GHSA-599f-7c49-w659/dependabot))

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
