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

import javax.money.*;

/**
 * Analog zu den anderen {@link Monetary}-Datentype kann mit dieser Factory
 * ein {@link Geldbetrag} erzeugt und vorblegt werden.
 *
 * @author oboehm
 * @since 0.8 (30.07.2018)
 */
public class GeldbetragFactory implements MonetaryAmountFactory<Geldbetrag> {

    /**
     * Access the {@link MonetaryAmount} implementation type.
     *
     * @return the {@link MonetaryAmount} implementation type, never {@code null}.
     */
    @Override
    public Class<? extends MonetaryAmount> getAmountType() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets the {@link CurrencyUnit} to be used.
     *
     * @param currency the {@link CurrencyUnit} to be used, not {@code null}
     * @return This factory instance, for chaining.
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setCurrency(CurrencyUnit currency) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets the number to be used.
     *
     * @param number the number to be used
     * @return This factory instance, for chaining.
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setNumber(double number) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets the number to be used.
     *
     * @param number the number to be used
     * @return This factory instance, for chaining.
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setNumber(long number) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Sets the number to be used.
     *
     * @param number the number to be used, not {@code null}.
     * @return This factory instance, for chaining.
     */
    @Override
    public MonetaryAmountFactory<Geldbetrag> setNumber(Number number) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Get the maximum possible number that this type can represent. If the numeric model has no limitations on the
     * numeric range, null should be returned. If {@link MonetaryContext#getPrecision()} returns a value > 0 this
     * method is required to provide a maximal amount.
     *
     * @return the maximum possible number, or null.
     */
    @Override
    public NumberValue getMaxNumber() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Get the minimum possible number that this type can represent. If the numeric model has no limitations on the
     * numeric range, null should be returned.  If {@link MonetaryContext#getPrecision()} returns a value > 0 this
     * method is required to provide a maximal amount.
     *
     * @return the minimum possible number, or null.
     */
    @Override
    public NumberValue getMinNumber() {
        throw new UnsupportedOperationException("not yet implemented");
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
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Erzeugt einen neuen {@link Geldbetrag} anhand der eingestellten Daten.
     *
     * @return den entsprechenden {@link Geldbetrag}.
     * @see #getAmountType()
     */
    @Override
    public Geldbetrag create() {
        return Geldbetrag.ZERO;
    }

    /**
     * Returns the default {@link MonetaryContext} used, when no {@link MonetaryContext} is
     * provided.
     * <p>
     * The default context is not allowed to exceed the capabilities of the maximal
     * {@link MonetaryContext} supported.
     *
     * @return the default {@link MonetaryContext}, never {@code null}.
     * @see #getMaximalMonetaryContext()
     */
    @Override
    public MonetaryContext getDefaultMonetaryContext() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
