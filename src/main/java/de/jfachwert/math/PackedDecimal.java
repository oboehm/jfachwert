/*
 * Copyright (c) 2018 by Oliver Boehm
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
 * (c)reated 29.03.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.math;

import de.jfachwert.Fachwert;
import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.NullValidator;

/**
 * Die Klasse PackedDecimal dienst zum speicherschonende Speichern von Zahlen.
 * Sie greift die Idee von COBOL auf, wo es den numerischen Datentyp
 * "COMPUTATIONAL-3 PACKED" gibt, wo die Zahlen in Half-Bytes (Nibbles)
 * abgespeichert wird. D.h. In einem Byte lassen sich damit 2 Zahlen
 * abspeichern. Diese Praesentation ist auch als BCD (Binary Coded Decimal)
 * bekannt (s. <a href="https://de.wikipedia.org/wiki/BCD-Code">BCD-Code</a>
 * in Wikipedia).
 * <p>
 * Dieser Datentyp eignet sich damit fuer:
 * <ul>
 *     <li>Abspeichern grosser Menge von Zahlen, wenn dabei die interne
 *     Speichergroesse relevant ist,</li>
 *     <li>Abspeichern von Zahlen beliebiger Groesse
 *     (Ersatz fuer {@link java.math.BigDecimal},</li>
 *     <li>Abspeichern von Zahlen mit fuehrender Null (z.B. Vorwahl).</li>
 * </ul>
 * </p>
 * <p>
 * Eine noch kompaktere Darstellung (ca. 20%) laesst sich mit der Chen-Ho- oder
 * Densely-Packed-Decimal-Kodierung (s.
 * <a href="http://speleotrove.com/decimal/DPDecimal.html">A Summary of Densely Packed Decimal encoding</a>).
 * Diese kommt hier aber nicht zum Einsatz. Stattdessen der BCD-Algorithmus
 * zum Einsatz.
 * </p>
 * <p>
 * Da diese Klasse eher eine technische als eine fachliche Klasse ist, wurde
 * die englische Bezeichnung aus COBOL uebernommen. Sie wird von einigen
 * Fachwert-Klassen intern verwendet, kann aber auch fuer eigene Zwecke
 * verwendet werden.
 * </p>
 *
 * @author oboehm
 * @since 0.6 (29.03.2018)
 */
public class PackedDecimal implements Fachwert {

    private static final SimpleValidator<String> VALIDATOR = new NullValidator();
    private final byte[] code;

    /**
     * Instanziiert ein PackedDecimal.
     *
     * @param zahl String aus Zahlen
     */
    public PackedDecimal(String zahl) {
        this(zahl, VALIDATOR);
    }

    /**
     * Instanziiert ein PackedDecimal. Diesen Konstruktor kann man verwenden,
     * wenn man mehr einen eigenen Validator zur Pruefung heranziehen will.
     *
     * @param zahl      String aus Zahlen
     * @param validator Validator, der die Zahl prueft
     */
    public PackedDecimal(String zahl, SimpleValidator<String> validator) {
        this.code = asNibbles(validator.validate(zahl));
    }

    private static byte[] asNibbles(String zahl) {
        char[] chars = (zahl + "F").toCharArray();
        byte[] bytes = new byte[(chars.length) / 2];
        for (int i = 0; i < bytes.length; i++) {
            int x1 = Character.digit(chars[i * 2], 16);
            int x2 = Character.digit(chars[i * 2 + 1], 16);
            bytes[i] = (byte) (x1 * 16 + x2);
        }
        return bytes;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (byte b : this.code) {
            if ((b & 0x0F) == 0xF) {
                buf.append((b & 0xF0) / 16);
            } else {
                String unpacked = String.format("%02x", b);
                buf.append(unpacked);
            }
        }
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /**
     * Beim Vergleich zweier PackedDecimals werden auch fuehrende Nullen
     * beruecksichtigt. D.h. '711' und '0711' werden als unterschiedlich
     * betrachtet.
     *
     * @param obj zu vergleichende PackedDedimal
     * @return true bei Gleichheit
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PackedDecimal)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

}
