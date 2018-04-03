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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express orimplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 24.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.steuer;

import de.jfachwert.AbstractFachwert;
import de.jfachwert.PruefzifferVerfahren;
import de.jfachwert.pruefung.exception.InvalidValueException;
import de.jfachwert.pruefung.LengthValidator;
import de.jfachwert.pruefung.Mod11Verfahren;
import de.jfachwert.pruefung.NoopVerfahren;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Die Umsatzsteuer-Identifikationsnummer (USt-IdNr) ist eine eindeutige
 * Kennzeichnung eines Unternehmens innerhalb der Europaeischen Union im
 * umsatzsteuerlichen Sinne.
 *
 * @author oboehm
 * @since 0.1.0
 */
public class UStIdNr extends AbstractFachwert<String> {

    private static final Map<String, PruefzifferVerfahren<String>> PRUEFZIFFER_VERFAHREN = new HashMap<>();

    static {
        PRUEFZIFFER_VERFAHREN.put("DE", new Mod11Verfahren(8));
    }

    /**
     * Erzeugt eine Umsatzsteuer-IdNr. Die uebergebene Nummer besteht aus
     * einer 2-stelligen Laenderkennung, gefolgt von maximal 12
     * alphanumerischen Zeichen.
     *
     * @param nr, z.B. "DE999999999"
     */
    public UStIdNr(String nr) {
        this(nr, selectPruefzifferVerfahrenFor(nr));
    }

    /**
     * Dieser Konstruktor ist hauptsaechlich fuer abgeleitete Klassen gedacht,
     * damit diese das {@link PruefzifferVerfahren} ueberschreiben koennen.
     * Man kann es auch verwenden, um das PruefzifferVerfahren abzuschalten,
     * indem man das {@link de.jfachwert.pruefung.NoopVerfahren} verwendet.
     *
     * @param nr          die Umsatzsteuer-IdNr.
     * @param pzVerfahren das verwendete PruefzifferVerfahren
     */
    public UStIdNr(String nr, PruefzifferVerfahren<String> pzVerfahren) {
        super(validate(nr, pzVerfahren));
    }

    private static PruefzifferVerfahren<String> selectPruefzifferVerfahrenFor(String nr) {
        String laenderkuerzel = toLaenderkuerzel(nr);
        PruefzifferVerfahren<String> verfahren = PRUEFZIFFER_VERFAHREN.get(laenderkuerzel);
        if (verfahren == null) {
            verfahren = new NoopVerfahren<>();
        }
        return verfahren;
    }

    /**
     * Eine Umsatzsteuer-Id beginnt mit der Laenderkennung (2 Zeichen), gefolgt
     * von maximal 12 alphanumerischen Zeichen. Bei dieser wird, je nach Land, die
     * Pruefziffer validiert (falls bekannt).
     *
     * Die kuerzeste Umsatzsteuer kann in GB mit 5 alphanumerischen Zeichen
     * auftreten.
     *
     * @param nr die Umsatzsteuer-Id, z.B. "DE136695970"
     * @return die validierte Id zur Weiterverarbeitung
     * @since 0.2.0
     */
    public static String validate(String nr) {
        return validate(nr, selectPruefzifferVerfahrenFor(nr));
    }

    private static String validate(String nr, PruefzifferVerfahren<String> verfahren) {
        String unformatted = StringUtils.remove(nr, ' ');
        LengthValidator.validate(unformatted, 7, 14);
        verfahren.validate(unformatted.substring(2));
        return unformatted;
    }

    /**
     * Liefert das Land, zu dem die IBAN gehoert.
     *
     * @return z.B. "DE" (als Locale)
     */
    public Locale getLand() {
        return new Locale(toLaenderkuerzel(this.getCode()));
    }

    private static String toLaenderkuerzel(String nr) {
        String kuerzel = nr.substring(0, 2).toUpperCase();
        if (StringUtils.isAlpha(kuerzel)) {
            return kuerzel;
        } else {
            throw new InvalidValueException(nr, "country");
        }
    }

}
