package de.jfachwert3;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ByteAdresseTest {

    private static final Logger LOG = LogManager.getLogger();

    @Test
    public void testAdresseNull() {
        assertThrows(IllegalArgumentException.class, () -> ByteAdresse.of(0));
    }

    @Test
    public void testAdresseEins() {
        assertEquals(1, ByteAdresse.of(1).intValue());
    }

    @Test
    public void testAdresse256() {
        assertEquals(256, ByteAdresse.of(256).intValue());
    }

    @Test
    public void testAdresseZuGross() {
        assertThrows(IllegalArgumentException.class, () -> ByteAdresse.of(257));
    }

    @Test
    public void testToString() {
        assertEquals("222", ByteAdresse.of(222).toString());
    }

    @Test
    public void testToMap() {
        ByteAdresse adresse = ByteAdresse.of(128);
        try {
            Map<String, Object> map = adresse.toMap();
            MatcherAssert.assertThat(map.values(), Matchers.not(empty()));
        } catch (UnsupportedOperationException mayhappen) {
            LOG.info("{}.toMap() schlug fehl:", adresse, mayhappen);
        }
    }

}
