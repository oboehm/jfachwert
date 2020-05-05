package de.jfachwert.pruefung.exception;

import javax.money.format.MonetaryParseException;

/**
 * Die LocalizedMonetaryParseException ist fuer Fehler beim Parsen von
 * Geldbetraegen gedacht.
 *
 * @author <a href="ob@aosd.de">oliver</a>
 * @since 1.0.1 (12.10.18)
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class LocalizedMonetaryParseException extends MonetaryParseException implements LocalizedException {

    private final Throwable cause;

    /**
     * Erzeuge eine LocalizedMonetaryParseException.
     *
     * @param parsedData Text mit Geldbetrag
     * @param cause Ursache fuer den Parse-Fehler
     */
    public LocalizedMonetaryParseException(CharSequence parsedData, Throwable cause) {
        super(parsedData, 0);
        this.cause = cause;
    }

    /**
     * Liefert die Ursache fuer die Exception.
     *
     * @return Ursache, moeglicherweise auch {@code null}
     */
    @Override
    public synchronized Throwable getCause() {
        return cause;
    }

    /**
     * Um eine aussagekraeftige Meldung zu bekommen, reichen wir das Ganze
     * noch um den Input-String an.
     *
     * @return detaillierte Meldung
     */
    @Override
    public String getMessage() {
        return getLocalizedMessage();
    }

    /**
     * Im Gegensatz {@code getMessage()} wird hier die Beschreibung auf deutsch
     * zurueckgegeben, wenn die Locale auf Deutsch steht.
     *
     * @return lokalisierte Beschreibung
     */
    @Override
    public String getLocalizedMessage() {
        return getLocalizedString(
                getLocalizedString("is_no_monetary_amount") + ": '" + getInput() + "' (" + super.getMessage() + ")");
    }

}
