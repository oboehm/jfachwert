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
 * (c)reated 06.07.17 by oliver (ob@oasd.de)
 */
package de.jfachwert.bank;

import de.jfachwert.Fachwert;

import java.util.Objects;
import java.util.Optional;

/**
 * Eine Bankverbindung besteht aus dem Zahlungsempfaenger oder Kontoinhaber,
 * einer IBAN und einer BIC. Bei Inlandsverbindungen kann die BIC entfallen,
 * weswegen sie hier auch optional ist.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 0.3.0
 */
public class Bankverbindung implements Fachwert {

    private final String kontoinhaber;
    private final IBAN iban;
    private final BIC bic;

    /**
     * Erzeugt eine neue Bankverbindung.
     *
     * @param name Name des Zahlungsempfaengers
     * @param iban die IBAN
     */
    public Bankverbindung(String name, IBAN iban) {
        this(name, iban, null);
    }

    /**
     * Erzeugt eine neue Bankverbindung.
     *
     * @param name Name des Zahlungsempfaengers
     * @param iban die IBAN
     * @param bic die BIC
     */
    public Bankverbindung(String name, IBAN iban, BIC bic) {
        this.kontoinhaber = name;
        this.iban = iban;
        this.bic = bic;
    }

    public String getKontoinhaber() {
        return kontoinhaber;
    }

    public IBAN getIban() {
        return iban;
    }

    /**
     * Da die BIC bei Inlands-Ueberweisungen optional ist, wird sie hier als
     * {@link Optional} zurueckgegeben.
     *
     * @return BIC
     */
    public Optional<BIC> getBic() {
        return (bic == null) ? Optional.empty() : Optional.of(bic);
    }

    @Override
    public int hashCode() {
        return iban.hashCode();
    }

    /**
     * Zwei Bankverbindungen sind gleich, wenn IBAN und BIC uebereinstimmen.
     *
     * @param obj die andere Bankverbindung
     * @return true bei Gleichheit
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bankverbindung)) {
            return false;
        }
        Bankverbindung other = (Bankverbindung) obj;
        return this.iban.equals(other.iban) && Objects.equals(this.bic, other.bic);
    }

    @Override
    @SuppressWarnings("squid:S3655")
    public String toString() {
        StringBuilder buf = new StringBuilder(getKontoinhaber());
        buf.append(", IBAN ");
        buf.append(getIban());
        if (getBic().isPresent()) {
            buf.append(", BIC ");
            buf.append(getBic().get());
        }
        return buf.toString();
    }

}
