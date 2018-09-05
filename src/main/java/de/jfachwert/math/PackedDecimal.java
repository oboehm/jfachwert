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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import de.jfachwert.Fachwert;
import de.jfachwert.SimpleValidator;
import de.jfachwert.pruefung.NullValidator;
import de.jfachwert.pruefung.exception.LocalizedIllegalArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

/**
 * Die Klasse PackedDecimal dienst zum speicherschonende Speichern von Zahlen.
 * Sie greift die Idee von COBOL auf, wo es den numerischen Datentyp
 * "COMPUTATIONAL-3 PACKED" gibt, wo die Zahlen in Halb-Bytes (Nibbles)
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
 * <p>
 * Eine noch kompaktere Darstellung (ca. 20%) laesst sich mit der Chen-Ho- oder
 * Densely-Packed-Decimal-Kodierung (s.
 * <a href="http://speleotrove.com/decimal/DPDecimal.html">A Summary of Densely Packed Decimal encoding</a>).
 * Diese kommt hier aber nicht zum Einsatz. Stattdessen kommt der BCD-Algorithmus
 * zum Einsatz. Dadurch koennen auch weitere Trenn- und Fuell-Zeichen aufgenommen
 * werden:
 * </p>
 * <ul>
 *     <li>Vorzeichen (+, -)</li>
 *     <li>Formattierung ('.', ',')</li>
 *     <li>Leerzeichen</li>
 *     <li>Trennzeichen (z.B. fuer Telefonnummern)</li>
 * </ul>
 * <p>
 * Die einzelnen Werte, die ein Halb-Byte (Nibble) aufnimmt, sind (angelehnt an
 * <a href="http://acc-gmbh.com/dochtml/Datentypen4.html">COMPUTATIONAL-3 PACKED</a>
 * in COBOL):
 * </p>
 * <pre>
 * +-----+---+--------------------------------------------------+
 * | 0x0 | 0 | Ziffer 0                                         |
 * | 0x1 | 1 | Ziffer 1                                         |
 * | ... |   |                                                  |
 * | 0x9 | 9 | Ziffer 9                                         |
 * | 0xA | / | Trennzeichen fuer Brueche                        |
 * | 0xB |   | Leerzeichen (Blank)                              |
 * | 0xC | + | positives Vorzeichen                             |
 * | 0xD | - | Leerzeichen (Blank)                              |
 * | 0xE | . | Formatzeichen Tausenderstelle (im Deutschen)     |
 * | 0xF | , | Trennung Vorkomma/Nachkommastelle (im Deutschen) |
 * +-----+---+--------------------------------------------------+
 * </pre>
 * <p>
 * Damit koennen auch Zeichenketten nachgebildet werden, die strenggenommen
 * keine Dezimalzahl darstellen, z.B. "+49/811 32 16-8". Dies ist zwar
 * zulaessig, jedoch duerfen damit keine mathematische Operation angewendet
 * werden. Ansonsten kann die Klasse ueberall dort eingesetzt werden, wo
 * auch eine {@link java.math.BigDecimal} verwendet wird.
 * </p>
 * <p>
 * Die API orientiert sich an die API von {@link BigDecimal} und ist auch von
 * der {@link Number}-Klasse abgeleitet. Allerdings werden noch nicht alle
 * Methoden von {@link BigDecimal unterstuetzt}. In diesem Fall kann man auf
 * die Methode {@link #toBigDecimal()} ausweichen.
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
@JsonSerialize(using = ToStringSerializer.class)
public class PackedDecimal extends AbstractNumber implements Fachwert, Comparable<PackedDecimal> {

    private static final Logger LOG = Logger.getLogger(PackedDecimal.class.getName());
    private static final NullValidator VALIDATOR = new NullValidator();
    private static final PackedDecimal[] CACHE = new PackedDecimal[11];
    private final byte[] code;

    static {
        for (int i = 0; i < CACHE.length; i++) {
            CACHE[i] = new PackedDecimal(i);
        }
    }

    /** Leere PackedDecimal. */
    public static final PackedDecimal EMPTY = new PackedDecimal("");

    /** Die Zahl 0. */
    public static final PackedDecimal ZERO = CACHE[0];

    /** Die Zahl 1. */
    public static final PackedDecimal ONE = CACHE[1];

    /** Die Zahl 10. */
    public static final PackedDecimal TEN = CACHE[10];

    /**
     * Instanziiert ein PackedDecimal.
     *
     * @param zahl Zahl
     */
    public PackedDecimal(long zahl) {
        this(Long.toString(zahl));
    }

    /**
     * Instanziiert ein PackedDecimal.
     *
     * @param zahl Zahl
     */
    public PackedDecimal(double zahl) {
        this(Double.toString(zahl));
    }

    /**
     * Falls man eine {@link BigDecimal} in eine {@link PackedDecimal} wandeln
     * will, kann man diesen Konstruktor hier verwenden. Besser ist es
     * allerdings, wenn man dazu {@link #valueOf(BigDecimal)} verwendet.
     *
     * @param zahl eine Dezimalzahl
     */
    public PackedDecimal(BigDecimal zahl) {
        this(zahl.toString());
    }

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

    /**
     * Liefert den uebergebenen String als {@link PackedDecimal} zurueck.
     * Diese Methode ist dem Konstruktor vorzuziehen, da fuer gaengige Zahlen
     * wie "0" oder "1" immer das gleiche Objekt zurueckgegeben wird.
     *
     * @param zahl beliebige long-Zahl
     * @return Zahl als {@link PackedDecimal}
     */
    public static PackedDecimal valueOf(long zahl) {
        return valueOf(Long.toString(zahl));
    }

    /**
     * Liefert den uebergebenen String als {@link PackedDecimal} zurueck.
     *
     * @param zahl beliebige long-Zahl
     * @return Zahl als {@link PackedDecimal}
     */
    public static PackedDecimal valueOf(double zahl) {
        return valueOf(Double.toString(zahl));
    }

    /**
     * Liefert den uebergebenen String als {@link PackedDecimal} zurueck.
     * Diese Methode ist dem Konstruktor vorzuziehen, da fuer gaengige Zahlen
     * wie "0" oder "1" immer das gleiche Objekt zurueckgegeben wird.
     *
     * @param zahl beliebige long-Zahl
     * @return Zahl als {@link PackedDecimal}
     */
    public static PackedDecimal valueOf(BigDecimal zahl) {
        return valueOf(zahl.toString());
    }

    /**
     * Liefert den uebergebenen String als {@link PackedDecimal} zurueck.
     *
     * @param bruch beliebiger Bruch
     * @return Bruch als {@link PackedDecimal}
     */
    public static PackedDecimal valueOf(AbstractNumber bruch) {
        return valueOf(bruch.toString());
    }

    /**
     * Liefert den uebergebenen String als {@link PackedDecimal} zurueck.
     * Diese Methode ist dem Konstruktor vorzuziehen, da fuer gaengige Zahlen
     * wie "0" oder "1" immer das gleiche Objekt zurueckgegeben wird.
     * <p>
     * Im Gegensatz zum String-Konstruktor darf man hier auch 'null' als Wert
     * uebergeben. In diesem Fall wird dies in {@link #EMPTY} uebersetzt.
     * </p>
     *
     * @param zahl String aus Zahlen
     * @return Zahl als {@link PackedDecimal}
     */
    public static PackedDecimal valueOf(String zahl) {
        String trimmed = StringUtils.trimToEmpty(zahl);
        if (StringUtils.isEmpty(trimmed)) {
            return EMPTY;
        }
        if ((trimmed.length() == 1 && Character.isDigit(trimmed.charAt(0)))) {
            return CACHE[Character.getNumericValue(trimmed.charAt(0))];
        } else {
            return new PackedDecimal(zahl);
        }
    }

    /**
     * Liefert true zurueck, wenn die Zahl als Bruch angegeben ist.
     *
     * @return true oder false
     */
    public boolean isBruch() {
        String s = toString();
        if (s.contains("/")) {
            try {
                Bruch.of(s);
                return true;
            } catch (IllegalArgumentException ex) {
                LOG.fine(s + " is not a fraction: " + ex);
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * Da sich mit {@link PackedDecimal} auch Telefonnummer und andere
     * Zahlenkombinationen abspeichern lassen, die im eigentlichen Sinn
     * keine Zahl darstellen, kann man ueber diese Methode abfragen, ob
     * eine Zahl abespeichdert wurde oder nicht.
     * 
     * @return true, falls es sich um eine Zahl handelt.
     */
    public boolean isNumber() {
        String packed = toString().replaceAll(" ", "");
        try {
            new BigDecimal(packed);
            return true;
        } catch (NumberFormatException nfe) {
            LOG.fine(packed + " is not a number: " + nfe);
            return isBruch();
        }
    }

    /**
     * Liefert die Zahl als Bruch zurueck.
     *
     * @return Bruch als Zahl
     */
    public Bruch toBruch() {
        return Bruch.of(toString());
    }

    /**
     * Liefert die gepackte Dezimalzahl wieder als {@link BigDecimal} zurueck.
     *
     * @return gepackte Dezimalzahl als {@link BigDecimal}
     */
    public BigDecimal toBigDecimal() {
        return new BigDecimal(toString());
    }

    /**
     * Summiert den uebergebenen Summanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param summand Summand
     * @return Summe
     */
    public PackedDecimal add(PackedDecimal summand) {
        if (this.isBruch() || summand.isBruch()) {
            return add(summand.toBruch());
        } else {
            return add(summand.toBigDecimal());
        }
    }

    /**
     * Summiert den uebergebenen Summanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param summand Operand
     * @return Differenz
     */
    public PackedDecimal add(BigDecimal summand) {
        BigDecimal summe = toBigDecimal().add(summand);
        return PackedDecimal.valueOf(summe);
    }

    /**
     * Summiert den uebergebenen Summanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param summand Operand
     * @return Differenz
     */
    public PackedDecimal add(Bruch summand) {
        AbstractNumber summe = toBruch().add(summand);
        return PackedDecimal.valueOf(summe);
    }

    /**
     * Subtrahiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Summand
     * @return Summe
     */
    public PackedDecimal subtract(PackedDecimal operand) {
        if (this.isBruch() || operand.isBruch()) {
            return subtract(operand.toBruch());
        } else {
            return subtract(operand.toBigDecimal());
        }
    }

    /**
     * Subtrahiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Operand
     * @return Differenz
     */
    public PackedDecimal subtract(BigDecimal operand) {
        BigDecimal result = toBigDecimal().subtract(operand);
        return PackedDecimal.valueOf(result);
    }

    /**
     * Subtrahiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Operand
     * @return Differenz
     */
    public PackedDecimal subtract(Bruch operand) {
        AbstractNumber result = toBruch().subtract(operand);
        return PackedDecimal.valueOf(result);
    }

    /**
     * Mulitpliziert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Summand
     * @return Produkt
     */
    public PackedDecimal multiply(PackedDecimal operand) {
        if (this.isBruch() || operand.isBruch()) {
            return multiply(operand.toBruch());
        } else {
            return multiply(operand.toBigDecimal());
        }
    }

    /**
     * Multipliziert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Operand
     * @return Produkt
     */
    public PackedDecimal multiply(BigDecimal operand) {
        BigDecimal produkt = toBigDecimal().multiply(operand);
        return PackedDecimal.valueOf(produkt);
    }

    /**
     * Multipliziert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Operand
     * @return Produkt
     */
    public PackedDecimal multiply(Bruch operand) {
        AbstractNumber produkt = toBruch().multiply(operand);
        return PackedDecimal.valueOf(produkt);
    }

    /**
     * Dividiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Operand
     * @return Ergebnis der Division
     */
    public PackedDecimal divide(PackedDecimal operand) {
        if (this.isBruch() || operand.isBruch()) {
            return divide(operand.toBruch());
        } else {
            return divide(operand.toBigDecimal());
        }
    }

    /**
     * Dividiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Operand
     * @return Ergebnis der Division
     */
    public PackedDecimal divide(Bruch operand) {
        return multiply(operand.kehrwert());
    }

    /**
     * Dividiert den uebergebenen Operanden und liefert als Ergebnis eine neue
     * {@link PackedDecimal} zurueck
     *
     * @param operand Operand
     * @return Ergebnis der Division
     */
    public PackedDecimal divide(BigDecimal operand) {
        BigDecimal result = toBigDecimal().divide(operand, RoundingMode.HALF_UP);
        return PackedDecimal.valueOf(result);
    }

    /**
     * Verschiebt den Dezimalpunkt um n Stellen nach links.
     *
     * @param n Anzahl Stellen
     * @return eine neue {@link PackedDecimal}
     */
    public PackedDecimal movePointLeft(int n) {
        BigDecimal result = toBigDecimal().movePointLeft(n);
        return PackedDecimal.valueOf(result);
    }

    /**
     * Verschiebt den Dezimalpunkt um n Stellen nach rechts.
     *
     * @param n Anzahl Stellen
     * @return eine neue {@link PackedDecimal}
     */
    public PackedDecimal movePointRight(int n) {
        BigDecimal result = toBigDecimal().movePointRight(n);
        return PackedDecimal.valueOf(result);
    }

    /**
     * Setzt die Anzahl der Nachkommastellen.
     *
     * @param n z.B. 0, falls keine Nachkommastelle gesetzt sein soll
     * @param mode Rundungs-Mode
     * @return eine neue {@link PackedDecimal}
     */
    public PackedDecimal setScale(int n, RoundingMode mode) {
        BigDecimal result = toBigDecimal().setScale(n, mode);
        return PackedDecimal.valueOf(result);
    }

    private static byte[] asNibbles(String zahl) {
        char[] chars = (zahl + " ").toCharArray();
        byte[] bytes = new byte[(chars.length) / 2];
        try {
            for (int i = 0; i < bytes.length; i++) {
                int upper = decode(chars[i * 2]);
                int lower = decode(chars[i * 2 + 1]);
                bytes[i] = (byte) ((upper << 4) | lower);
            }
        } catch (IllegalArgumentException ex) {
            throw new LocalizedIllegalArgumentException(zahl, "number", ex);
        }
        return bytes;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (byte b : this.code) {
            buf.append(encode(b >> 4));
            buf.append(encode(b & 0x0F));
        }
        return buf.toString().trim();
    }

    private static int decode(char x) {
        switch (x) {
            case '0':   return 0x0;
            case '1':   return 0x1;
            case '2':   return 0x2;
            case '3':   return 0x3;
            case '4':   return 0x4;
            case '5':   return 0x5;
            case '6':   return 0x6;
            case '7':   return 0x7;
            case '8':   return 0x8;
            case '9':   return 0x9;
            case '/':   return 0xA;
            case '\t':
            case ' ':   return 0xB;
            case '+':   return 0xC;
            case '-':   return 0xD;
            case '.':   return 0xE;
            case ',':   return 0xF;
            default:    throw new LocalizedIllegalArgumentException(x, "number");
        }
    }

    private static char encode(int nibble) {
        switch (0x0F & nibble) {
            case 0x0:   return '0';
            case 0x1:   return '1';
            case 0x2:   return '2';
            case 0x3:   return '3';
            case 0x4:   return '4';
            case 0x5:   return '5';
            case 0x6:   return '6';
            case 0x7:   return '7';
            case 0x8:   return '8';
            case 0x9:   return '9';
            case 0xA:   return '/';
            case 0xB:   return ' ';
            case 0xC:   return '+';
            case 0xD:   return '-';
            case 0xE:   return '.';
            case 0xF:   return ',';
            default:    throw new IllegalStateException("internal error");
        }
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
        return obj instanceof PackedDecimal && this.toString().equals(obj.toString());
    }

    /**
     * Vergleicht die andere Zahl mit der aktuellen Zahl.
     *
     * @param other die andere {@link PackedDecimal}, die verglichen wird.
     * @return negtive Zahl, falls this &lt; other, 0 bei Gleichheit, ansonsten
     * positive Zahl.
     */
    @Override
    public int compareTo(PackedDecimal other) {
        return this.toBruch().compareTo(other.toBruch());
    }

}
