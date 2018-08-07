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
 * (c)reated 30.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank;

import org.javamoney.moneta.Money;

import javax.money.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Analog zu den anderen {@link Monetary}-Datentype kann mit dieser Factory
 * ein {@link Geldbetrag} erzeugt und vorblegt werden.
 *
 * @author oboehm
 * @since 0.8 (30.07.2018)
 */
public class GeldbetragFactory implements MonetaryAmountFactory<Geldbetrag> {

    private static final MonetaryContext MAX_CONTEXT =
            MonetaryContextBuilder.of(Money.class).setAmountType(Geldbetrag.class).setPrecision(0).setMaxScale(-1)
                                  .set(RoundingMode.HALF_EVEN).build();
    private Number number = BigDecimal.ZERO;
    private Currency currency = Waehrung.DEFAULT_CURRENCY;
    private MonetaryContext context =
            MonetaryContextBuilder.of(Money.class).setAmountType(Geldbetrag.class).set(64).setMaxScale(63)
                                  .set(RoundingMode.HALF_EVEN).build();

    /**
     * Access the {@link MonetaryAmount} implementation type.
     *
     * @return the {@link MonetaryAmount} implementation type, never {@code null}.
     */
    @Override
    public Class<? extends MonetaryAmount> getAmountType() {
        return Geldbetrag.class;
    }

    /**
     * Setzt die {@link CurrencyUnit}.
     *
     * @param currency {@link CurrencyUnit}, nicht {@code null}
     * @return die Factory selber
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setCurrency(CurrencyUnit currency) {
        this.currency = Currency.getInstance(currency.getCurrencyCode());
        return this;
    }

    /**
     * Setzt die Nummer fuer den Geldbetrag.
     *
     * @param number Betrag, darf nicht {@code null} sein.
     * @return die Factory selber
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setNumber(double number) {
        this.number = number;
        return this;
    }

    /**
     * Setzt die Nummer fuer den Geldbetrag.
     *
     * @param number Betrag, darf nicht {@code null} sein.
     * @return die Factory selber
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setNumber(long number) {
        return setNumber(BigDecimal.valueOf(number));
    }

    /**
     * Setzt die Nummer fuer den Geldbetrag.
     *
     * @param number Betrag, darf nicht {@code null} sein.
     * @return die Factory selber
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setNumber(Number number) {
        this.number = number;
        return this;
    }

    /**
     * Liefert die Maximal-Nummer, die der {@link Geldbetrag} darstellen kann.
     *
     * @return Maximal-Betrag
     */
    @Override
    public NumberValue getMaxNumber() {
        return Geldbetrag.MAX_VALUE.getNumber();
    }

    /**
     * Liefert die Minimal-Nummer, die der {@link Geldbetrag} darstellen kann.
     *
     * @return Minimal-Betrag
     */
    @Override
    public NumberValue getMinNumber() {
        return Geldbetrag.MIN_VALUE.getNumber();
    }

    /**
     * Sets the {@link MonetaryContext} to be used.
     *
     * @param monetaryContext the {@link MonetaryContext} to be used, not {@code null}.
     * @return This factory instance, for chaining.
     * @throws MonetaryException when the {@link MonetaryContext} given exceeds the capabilities supported by this
     *                           factory type.
     * @see #getMaximalMonetaryContext()
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setContext(MonetaryContext monetaryContext) {
        context = monetaryContext;
        return this;
    }

    /**
     * Erzeugt einen neuen {@link Geldbetrag} anhand der eingestellten Daten.
     *
     * @return den entsprechenden {@link Geldbetrag}.
     * @see #getAmountType()
     */
    @Override
    public Geldbetrag create() {
        return new Geldbetrag(number, currency);
    }

    /**
     * In der Standardeinstellung liefert der {@link MonetaryContext} einen 
     * Wertbereich fuer den Geldbetrag von {@link Geldbetrag#MIN_VALUE} bis
     * {@link Geldbetrag#MAX_VALUE}.
     *
     * @return den Default-{@link MonetaryContext}.
     * @see #getMaximalMonetaryContext()
     */
    @Override
    public MonetaryContext getDefaultMonetaryContext() {
        return context;
    }

    /**
     * Der maximale {@link MonetaryContext} schraenkt den Wertebereich eines
     * Geldbetrags nicth ein. D.h. es gibt keine obere und untere Grenze.
     *
     * @return maximaler {@link MonetaryContext}.
     */
    @Override
    public MonetaryContext getMaximalMonetaryContext() {
        return MAX_CONTEXT;
    }

}
