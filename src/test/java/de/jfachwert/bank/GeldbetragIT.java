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
 * (c)reated 20.07.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.bank;

import org.apache.commons.lang3.StringUtils;
import org.javamoney.tck.JSR354TestConfiguration;
import org.javamoney.tck.TCKRunner;
import org.junit.Test;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Fuer den Integrationstest der {@link Geldbetrag}-Klasse wird das TCK
 * (Technical Compatibility Kit) fuer JSR 354 durchlaufen. Es ist unter
 * <a href="https://github.com/JavaMoney/jsr354-tck">jsr-354-tck</a> zu
 * finden.
 *
 * @author oboehm
 * @since 0.8 (20.07.2018)
 */
public class GeldbetragIT implements JSR354TestConfiguration {

    /**
     * Start der TCK-Suite.
     * 
     * @throws IOException falls Resultat nicht gelesen werden kann
     */
    @Test
    public void runTCK() throws IOException {
        ServiceLoader.load(GeldbetragIT.class);
        TCKRunner.main();
        assertThat("number of failed tests", getNumberOfFailedTests(), equalTo(0));
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
        return new ArrayList<>();
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

}
