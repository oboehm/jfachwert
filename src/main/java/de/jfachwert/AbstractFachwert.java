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
 * (c)reated 14.03.2017 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import de.jfachwert.pruefung.NullValidator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Die meisten Fachwerte sind nur ein ganz duenner Wrapper um ein Attribut vom
 * Typ 'String'. Fuer diese Fachwerte duerfte diese Implementierung ausreichen.
 *
 * @author oboehm
 * @since 14.03.2017
 * @since 0.0.2
 */
@JsonSerialize(using = ToStringSerializer.class)
public abstract class AbstractFachwert<T extends Serializable> implements Fachwert {

    private final T code;

    protected AbstractFachwert(T code) {
        SimpleValidator<T> validator = new NullValidator();
        this.code = validator.validate(code);
    }
    
    /**
     * Liefert die interne Praesentation fuer die abgeleiteten Klassen. Er
     * ist nicht fuer den direkten Aufruf vorgesehen, weswegen die Methode auch
     * 'final' ist.
     *
     * @return die interne Repraesentation
     */
    protected final T getCode() {
        return this.code;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    /**
     * Zwei Fachwerte sind nur dann gleich, wenn sie vom gleichen Typ sind und
     * den gleichen Wert besitzen.
     *
     * @param obj zu vergleichender Fachwert
     * @return true bei Gleichheit
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractFachwert) || (!this.getClass().isAssignableFrom(obj.getClass()))) {
            return false;
        }
        AbstractFachwert other = (AbstractFachwert) obj;
        return this.code.equals(other.getCode());
    }

    /**
     * Fuer die meisten Fachwerte reicht es, einfach den internen Code als
     * String auszugeben.
     *
     * @return den internen code
     */
    @Override
    public String toString() {
        return Objects.toString(this.code);
    }

    /**
     * Liefert die einzelnen Attribute eines Fachwertes als Map. Bei einem
     * einzelnen Wert wird als Default-Implementierung der Klassenname und
     * die toString()-Implementierung herangezogen.
     *
     * @return Attribute als Map
     */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(this.getClass().getSimpleName().toLowerCase(), toString());
        return map;
    }

}
