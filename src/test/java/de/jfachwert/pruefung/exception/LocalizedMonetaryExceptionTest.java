package de.jfachwert.pruefung.exception;/*
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

import de.jfachwert.bank.Geldbetrag;
import org.junit.Before;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Unit-Tests fuer {@link LocalizedMonetaryException}-Klasse.
 *
 * @author oboehm
 */
public class LocalizedMonetaryExceptionTest {
    
    private LocalizedMonetaryException exception;
    
    @Before
    public void setUpException() {
        MonetaryAmount einEuro = new Geldbetrag(1).withCurrency("EUR");
        MonetaryAmount zweiDM = new Geldbetrag(2).withCurrency("DEM");
        exception = new LocalizedMonetaryException("different currencies", einEuro, zweiDM);
    }

    @Test
    public void testGetMessage() {
        checkMessage(exception.getMessage());
    }

    @Test
    public void testGetLocalizedMessage() {
        String msg = exception.getLocalizedMessage();
        checkMessage(exception.getMessage());
        if ("DE".equals(Locale.getDefault().getCountry())) {
            assertThat(msg, containsString("unterschiedliche W\u00e4hrung"));
        } else {
            assertThat(msg, containsString("different currencies"));
        }
    }
    
    private static void checkMessage(String msg) {
        assertThat("too short message: " + msg, msg.length(), greaterThan(30));
    }

}
