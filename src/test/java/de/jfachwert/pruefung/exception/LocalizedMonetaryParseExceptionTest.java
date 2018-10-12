package de.jfachwert.pruefung.exception;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Unit-Tests for de.jfachwert.pruefung.exception.LocalizedMonetaryParseException.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 */
public final class LocalizedMonetaryParseExceptionTest {

    private final Throwable cause = new IllegalArgumentException("hier stimmt was nicht");
    private final LocalizedMonetaryParseException exception = new LocalizedMonetaryParseException("falscher Input", cause);

    @Test
    public void testGetCause() {
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testGetMessage() {
        assertThat(exception.getMessage(), containsString("falscher Input"));
    }

    @Test
    public void testGetLocalizedMessage() {
        if ("DE".equals(Locale.getDefault().getCountry())) {
            assertThat(exception.getLocalizedMessage(), containsString("ist kein Geldbetrag"));
        }
    }

}