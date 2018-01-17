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
 * (c)reated 17.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert;

import de.jfachwert.pruefung.NullValidator;

/**
 * Die Klasse Text ist der einfachste Fachwerte, der eigentlich nur ein
 * Wrapper um die String-Klasse ist. Allerdings mit dem Unterschied, dass
 * man keinen Null-Text oder leeren Text anlegen kann.
 * <p>
 * Diese Klasse wurde mit der {@link FachwertFactory} eingefuehrt. Sie dient
 * dort als Fallback, wenn kein Fachwert erzeugt werden kann, auf den der
 * uebergebene Name passt.
 * </p>
 *
 * @author oboehm
 * @since 0.5 (17.01.2018)
 */
public class Text extends AbstractFachwert<String> {
    
    private static final SimpleValidator<String> VALIDATOR = new NullValidator();

    /**
     * Erzeugt einen Text.
     * 
     * @param text darf nicht null sein 
     */
    public Text(String text) {
        super(validate(text));
    }

    /**
     * Ueberprueft den uebergebenen Text.
     * 
     * @param text Text
     * @return den validierten Text zur Weiterverabeitung
     */
    public static String validate(String text) {
        return VALIDATOR.validate(text);
    }

}
