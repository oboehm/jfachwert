package de.jfachwert3;

import de.jfachwert.post.Adresse;
import de.jfachwert.post.Name;
import de.jfachwert.post.Ort;
import de.jfachwert.post.PLZ;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Klasse Anschrift.
 *
 * @author oboehm
 */
public class Anschrift {
    
    private Name name = Name.NULL;
    private String strasse = "";
    private PLZ plz = PLZ.NULL;
    private String ort = "";

    public Anschrift(String name, String strasse, PLZ plz, String ort) {
        this(Name.of(name), strasse, plz, ort);
    }

    public Anschrift(Name name, String strasse, PLZ plz, String ort) {
        this.name = name;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    /**
     * Liefert die PLZ als String (und nicht als PLZ). Der Grund dafuer ist,
     * dass JPA das nicht automatisch zu einem String konvertieren kann, auch
     * wenn man dafuer einen eigenen AttributeConverter implementiert.
     *
     * @return PLZ als String
     */
    public String getPlz() {
        return plz.toString();
    }

    public void setPlz(String plz) {
        if (StringUtils.isBlank(plz)) {
            setPlz(PLZ.NULL);
        } else {
            setPlz(PLZ.of(plz));
        }
    }

    public void setPlz(PLZ plz) {
        this.plz = plz;
    }

    /**
     * Wegen JPA koennen wir (trotz Versuche mit einer NameConverter-Klasse)
     * hier nicht Name als Typ zurueckgeben. Sonst klappt es mit dem DB-Zugriff
     * nicht.
     *
     * @return Name als String
     */
    public String getName() {
        return Objects.toString(name);
    }

    public void setName(String name) {
        this.name = Name.of(name);
    }
    
    public boolean hasName(String s) {
        return name.equals(Name.of(s));
    }

    public Adresse getAdresse() {
        return Adresse.of(Ort.of(plz, ort), strasse);
    }

    @Override
    public String toString() {
        return this.getName() + " in " + this.getPlz() + " " + this.getOrt() + ", " + this.getStrasse();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  Anschrift)) {
            return false;
        }
        Anschrift other = (Anschrift) obj;
        return Objects.equals(name, other.name) && this.getAdresse().equals(other.getAdresse());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
