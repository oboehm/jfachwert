/*
 * Copyright (c) 2018-2024 by Oliver Boehm
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
 * (c)reated 20.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.money;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.convert.IMFRateProvider;
import org.javamoney.tck.JSR354TestConfiguration;
import org.javamoney.tck.TCKRunner;
import org.javamoney.tck.TCKTestSetup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import patterntesting.runtime.annotation.SkipTestOn;
import patterntesting.runtime.junit.extension.SmokeTestExtension;

import javax.money.*;
import javax.money.convert.ExchangeRateProvider;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Fuer den Integrationstest der {@link Geldbetrag}-Klasse wird das TCK
 * (Technical Compatibility Kit) fuer JSR 354 durchlaufen. Es ist unter
 * <a href="https://github.com/JavaMoney/jsr354-tck">jsr-354-tck</a> zu
 * finden.
 * <p>
 * Wenn man die Log-Ausgabe des TCK reduzieren will, sollte man den Test
 * mit
 * <pre>
 *      -Djava.util.logging.config.file=logging.properties
 * </pre>
 * aufrufen.
 * </p>
 *
 * @author oboehm
 * @since 1.0 (20.07.2018)
 */
@ExtendWith(SmokeTestExtension.class)
public class GeldbetragIT implements JSR354TestConfiguration {

    /**
     * Test, ob die Test-Konfiguration als Service registriert wurde.
     */
    @Test
    public void loadTestConfiguration() {
        ServiceLoader<JSR354TestConfiguration> loader = ServiceLoader.load(JSR354TestConfiguration.class);
        assertNotNull(loader);
        assertTrue(loader.iterator().hasNext());
        assertEquals(GeldbetragIT.class, loader.iterator().next().getClass());
    }

    /**
     * Test, ob die Test-Konfiguration geladen werden kann.
     */
    @Test
    public void testLoadTCK() {
        JSR354TestConfiguration testConfiguration = TCKTestSetup.getTestConfiguration();
        assertNotNull(testConfiguration);
    }

    /**
     * Hier gab es beim Testen Probleme mit der Instantiierung
     */
    @Test
    public void testIMFRateProvider() {
        IMFRateProvider provider = new IMFRateProvider();
        assertNotNull(provider.getContext());
    }

    /**
     * Beim Holen des {@link ExchangeRateProvider} konnte leider der
     * {@link IMFRateProvider} nicht instantiiert werden.
     */
    @Test
    public void testGetExchangeRateProvider() {
        ExchangeRateProvider provider = Bootstrap.getService(ExchangeRateProvider.class);
        assertNotNull(provider);
    }

    /**
     * Start der TCK-Suite.
     * 
     * @throws IOException falls Resultat nicht gelesen werden kann
     */
    @Test
    @SkipTestOn(javaVersion = "21")
    @Disabled("wegen zu starker Test-Abhaengigkeit zur Java-Version deaktiviert")
    public void runTCK() throws IOException {
        ServiceLoader.load(GeldbetragIT.class);
        TCKRunner.main();
        MatcherAssert.assertThat("number of failed tests", getNumberOfFailedTests(), lessThanOrEqualTo(2));
    }
    
    private static int getNumberOfFailedTests() throws IOException {
        Path resultsFile = Paths.get("target", "tck-results.txt");
        List<String> lines = Files.readAllLines(resultsFile);
        String testsFailedLine = lines.get(lines.size() - 1);
        String n = StringUtils.substringAfter(testsFailedLine, ":").trim();
        return Integer.parseInt(n);
    }

    /**
     * Return a collection with all {@link MonetaryAmount} classes that are implemented. The list
     * must not be empty and should contain <b>every</b> amount class implemented.<p>
     * This enables the TCK to check in addition to the basic implementation compliance, if
     * according {@link MonetaryAmountFactoryProviderSpi} are registered/available correctly.
     *
     * @return a collection with all implemented amount classes, not null.
     */
    @Override
    public Collection<Class> getAmountClasses() {
        return Collections.singletonList(Geldbetrag.class);
    }

    /**
     * List a collection of {@link CurrencyUnit} implementation.<p>
     * This enables the TCK to check the basic implementation compliance.
     *
     * @return a collection with CurrencyUnit implementations to be tested.
     */
    @Override
    public Collection<Class> getCurrencyClasses() {
        return Collections.singletonList(Waehrung.class);
    }

    /**
     * This method allows to let instances of MonetaryOperator to be tested for requirements and recommendations.
     *
     * @return the list of operators to be checked, not null. It is allowed to return an empty list here, which will
     * disable TCK tests for MonetaryOperator instances.
     */
    @Override
    public Collection<MonetaryOperator> getMonetaryOperators4Test() {
        return new ArrayList<>();
    }

    /**
     * Hier besteht noch Klaerungsbedarf wegen der compareTo-Methode. Nach
     * meinem Verstaendnis ist "CHF 1 > GBP 0", unabhaengig von der
     * eingestellten Waehrung. Zumindest ist es eine valide Implementierung,
     * die vom TCK immer noch als "falsch" bewertet wird.
     */
    @Test
    @Disabled
    public void testCompareToGeldbetrag() {
        checkCompareTo(Monetary.getAmountFactory(Geldbetrag.class));
    }

    /**
     * Die Original-Money-Implementierung liefert fuer den vorigen Test ein
     * anderes Ergebnis, weswegen sie auf "@Ignore" gesetzt wurde.
     */
    @Test
    @Disabled
    public void testCompareToMoney() {
        checkCompareTo(Monetary.getAmountFactory(Money.class));
    }

    private void checkCompareTo(MonetaryAmountFactory factory) {
        factory.setCurrency("EUR").setNumber(1).create();
        MonetaryAmount one = factory.setCurrency("CHF").setNumber(1).create();
        MonetaryAmount zero = factory.setCurrency("CHF").setNumber(0).create();
        MonetaryAmount zeroXXX = factory.setCurrency("GBP").setNumber(0).create();
        MatcherAssert.assertThat(one + " > " + zero, one.compareTo(zero), greaterThan(0));
        MatcherAssert.assertThat(one + " > " + zeroXXX, one.compareTo(zeroXXX), greaterThan(0));
    }

}
