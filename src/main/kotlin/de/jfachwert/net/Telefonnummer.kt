/*
 * Copyright (c) 2017 by Oliver Boehm
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 04.09.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.SimpleValidator;
import de.jfachwert.Text;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.exception.InvalidValueException;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Die Klasse Telefonnummer steht fuer alle Arten von Rufnummern im
 * Telefon-Netz wie Fesetnetznummer, Faxnummer oder Mobilfunknummer.
 * Ueblicherweise bestehen Telefonnummern aus
 * <ul>
 *     <li>Laenderkennzahl (LKz),</li>
 *     <li>Ortsnetzkennzahl (ONKz) bzw. die eigentliche Vorwahl</li>
 *     <li>Teilnehmerrufnummer (RufNr),</li>
 *     <li>Durchwahl (optional).</li>
 * </ul>
 * Die Telefonnummer +49 30 12345-67 hat die Laenderkennzahl 49 (Deutschland),
 * die Vorwahl 030 (Berlin), die Teilnehmerrufnummer 12345 und die Durchwahl
 * 67.
 * <p>
 * Frueher waren Telefon-Netz und Computer-Netzwerke strikt getrennt.
 * Inwischen wachsen diese beiden Netze immer mehr zusammen und unterscheiden
 * sich nur noch durch das Netzwerkprotokoll. Deswegen ist diese Klasse
 * im gleichen Package 'net' wie die EMailAdresse zu finden und nicht in
 * 'comm' (fuer Kommunikation), wie urspruenglich geplant.
 * </p>
 *
 * @author oboehm
 * @since 0.5 (04.09.2017)
 */
public class Telefonnummer extends Text {

    private static final SimpleValidator<String> VALIDATOR = new Validator();
    private static final WeakHashMap<String, Telefonnummer> WEAK_CACHE = new WeakHashMap<>();

    /** Null-Konstante fuer Initialisierungen. */
    public static final Telefonnummer NULL = new Telefonnummer("", new NullValidator<>());

    /**
     * Legt eine neue Instanz einer Telefonnummer an, sofern die uebergebene
     * Nummer valide ist.
     *
     * @param nummer z.B. "+49 (0)30 12345-67"
     */
    public Telefonnummer(String nummer) {
        this(nummer, VALIDATOR);
    }

    /**
     * Eine Telefonnummer lasesst sich auch ueber eine URI kreieren. Der
     * RFC 3966 schlaegt dabei "tel:" als Schema vor.
     *
     * @param uri z.B. "tel:+49-30-1234567"
     */
    public Telefonnummer(URI uri) {
        this(uri.getSchemeSpecificPart());
    }

    /**
     * Legt eine Instanz einer Telefonnummer an. Dieser Konstruktor ist
     * hauptsaechlich fuer abgeleitete Klassen gedacht, die ihre eigene
     * Validierung mit einbringen wollen oder aus Performance-Gruenden
     * abschalten wollen.
     *
     * @param nummer    eine gueltige Telefonnummer, z.B. "+49 30 12345-67"
     * @param validator SimpleValidator zur Adressen-Validierung
     */
    public Telefonnummer(String nummer, SimpleValidator<String> validator) {
        super(normalize(nummer), validator);
    }

    /**
     * Liefert eine Telefonnummer zurueck.
     *
     * @param nummer eine gueltige Telefonnummer, z.B. "+49 30 12345-67"
     * @return Telefonnummer
     */
    public static Telefonnummer of(String nummer) {
        return WEAK_CACHE.computeIfAbsent(nummer, Telefonnummer::new);
    }

    /**
     * Liefert die Telefonnummer ohne Laenderkennzahl, dafuer mit Vorwahl
     * inklusive fuehrender Null.
     *
     * @return z.B. 0811/32168
     */
    public Telefonnummer getInlandsnummer() {
        if (getLaenderkennzahl().isPresent()) {
            String nummer = this.getCode().substring(3).trim();
            if (StringUtils.startsWithAny(nummer, "1", "2", "3", "4", "5", "6", "7", "8", "9")) {
                nummer = "0" + nummer;
            }
            return new Telefonnummer(nummer);
        } else {
            return this;
        }
    }

    /**
     * Die Laenderkennzahl (LKZ) ist die Vorwahl, die man fuer Telefonate ins
     * Ausland waehlen muss. Fuer Deutschland ist die LKZ "+49*, d.h. wenn
     * man von Oesterreich nach Deutschland waehlen muss, muss man "0049"
     * vorwaehlen.
     * <p>
     * Da die Laenderkennzahl optional ist, wird sie als {@link Optional}
     * zurueckgegeben.
     * </p>
     *
     * @return z.B. "+49"
     */
    public Optional<String> getLaenderkennzahl() {
        String laenderkennzahl = this.getCode().substring(0, 3);
        if (laenderkennzahl.startsWith("+")) {
            return Optional.of(laenderkennzahl);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Liefert die Vorwahl oder auch Ortskennzahl (ONKz).
     *
     * @return z.B. "0711" fuer Stuttgart
     */
    public String getVorwahl() {
        String[] parts = this.getCode().trim().split("[ /-]|(\\(0\\))");
        String vorwahl = parts[0];
        if (vorwahl.startsWith("+")) {
            vorwahl = StringUtils.isBlank(parts[1]) ? parts[2] : parts[1];
        }
        vorwahl = RegExUtils.removeAll(vorwahl, "[ \t+-/(\\(\\))]");
        if (vorwahl.startsWith("0")) {
            return vorwahl;
        }
        return "0" + vorwahl;
    }

    /**
     * Liefert die Nummer der Ortsvermittlungsstelle, d.h. die Telefonnummer
     * ohne Vorwahl und Laenderkennzahl.
     *
     * @return z.B. "32168"
     */
    public Telefonnummer getRufnummer() {
        String inlandsnummer = RegExUtils.replaceAll(this.getInlandsnummer().toString(), "[ /]+", " ");
        return new Telefonnummer(StringUtils.substringAfter(inlandsnummer, " ").replaceAll(" ", ""));
    }

    /**
     * Wenn zwei Telefonnummern gleich sind, muessen sie auch den gleichen
     * Hashcode liefern.
     *
     * @return Hashcode, der nur aus den Ziffern ermittelt wird
     */
    @Override
    public int hashCode() {
        return toShortString().hashCode();
    }

    /**
     * Beim Vergleich zweier Telefonnummern spielen Trennzeichen keine Rolle.
     * Hier sind nur die Nummern relevant.
     *
     * @param obj zu vergleichende Telefonnummer
     * @return true bei Gleichheit
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Telefonnummer)) {
            return false;
        }
        Telefonnummer other = (Telefonnummer) obj;
        return this.toShortString().equals(other.toShortString());
    }

    /**
     * Stellt eine Telefonnummer in verkuerzter Schreibweise ohne Leerzeichen
     * und Trennzeichen dar.
     *
     * @return z.B. "+49301234567"
     */
    public String toShortString() {
        return RegExUtils.removeAll(getCode(), "[ \t+-/]|(\\(0\\))");
    }

    /**
     * Gibt den String nach DIN 5008 aus. Die Nummern werden dabei
     * funktionsbezogen durch Leerzeichen und die Durchwahl mit Bindestrich
     * abgetrennt.
     *
     * @return z.B. "+49 30 12345-67" bzw. "030 12345-67"
     */
    public String toDinString() {
        Optional<String> laenderkennzahl = getLaenderkennzahl();
        return laenderkennzahl.map(s -> s + " " + getVorwahl().substring(1) + " " + getRufnummer()).orElseGet(
                () -> getVorwahl() + " " + getRufnummer());
    }

    /**
     * Die "E.123" ist eine Empfehlung der Internationalen Fernmeldeunion.
     * Dabei werden die einzelnen Bestandteile (Laenderkennzeichen, Vorwahl
     * und Rufnummer) durch Leerzeichen gruppiert.
     *
     * @return z.B. "+49 30 12345 67" oder "(030) 12345 67" (national)
     */
    public String toE123String() {
        if (getLaenderkennzahl().isPresent()) {
            return toDinString().replace('-', ' ');
        } else {
            return "(" + getVorwahl() + ") " + getRufnummer().toString().replace('-', ' ');
        }
    }

    /**
     * Nach RFC 3966 wird die Telefonnummer wie E.123 dargestellt, jedoch mit
     * Bindestrich statt Leerzeichen.
     *
     * @return z.B. "tel:+49-30-12345-67"
     */
    public URI toURI() {
        return URI.create("tel:" + toDinString().replace(' ', '-'));
    }

    private static String normalize(String nummer) {
        String normalized = nummer.trim();
        if (normalized.startsWith("(0)")) {
            normalized = "0" + normalized.substring(3);
        }
        return normalized;
    }


    /**
     * Die Klasse Validator validiert die Schreibweise von Telefonnummern.
     * Urspruenglich war dieser Validator in einer eigenen Klasse
     * ('TelefonnummerValidator' ausgegegliedert. Mit v2.2 wurde der
     * Validator analog zu den anderen Validatoren zur betroffenen
     * Klasse als innere Klasse dazugesteckt.
     *
     * @author oboehm
     * @since 0.5 (05.09.2017)
     */
    public static class Validator implements SimpleValidator<String> {

        private final Pattern pattern;
        private final LengthValidator<String> lengthValidator = new LengthValidator<>(3, 15);

        /**
         * Hier wird der E-Mail-SimpleValidator mit einerm Pattern von
         * https://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
         * aufgesetzt.
         */
        public Validator() {
            this(Pattern.compile("[0-9-+/ ()]+"));
        }

        /**
         * Dieser Konstruktor ist fuer abgeleitete Klassen gedacht, die das Pattern
         * fuer die Adress-Validierung ueberschreiben moechten.
         *
         * @param pattern Pattern fuer die Adress-Validerung
         */
        protected Validator(Pattern pattern) {
            this.pattern = pattern;
        }

        /**
         * Ueberprueft die Telefonnummer, ob sie nur erlaubte Nummern (und
         * Sonderzeichen) enthaelt.
         *
         * @param nummer zu pruefende Telefonnummer
         * @return Wert selber, wenn er gueltig ist
         */
        @Override
        public String validate(String nummer) {
            Matcher matcher = pattern.matcher(nummer);
            if (matcher.matches()) {
                String normalized = RegExUtils.removeAll(nummer, "[ \t+-/]|(\\(0\\))");
                lengthValidator.validate(normalized);
                return nummer;
            }
            throw new InvalidValueException(nummer, "phone_number");
        }

    }
}
