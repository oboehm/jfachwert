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
 * (c)reated 18.01.2018 by oboehm (ob@oasd.de)
 */
package de.jfachwert.post;

import de.jfachwert.AbstractFachwert;

/**
 * Ein Adressat (oder auch Postempfaenger) ist diejenige Person, die in der
 * Adresse benannt ist und für die damit eine Postsendung bestimmt ist. 
 * Hierbei kann es sich um eine natuerliche oder um eine juristische Person 
 * handeln.
 *
 * @author oboehm
 * @since 0.5 (18.01.2018)
 */
public class Adressat extends AbstractFachwert<String> {

    /**
     * Erzeugt eine Adressat mit dem angegebenen Namen. DAbe kann es sich um
     * eine natuerliche Person (z.B. "Mustermann, Max") oder eine juristische
     * Person (z.B. "Ich AG") handeln.
     * 
     * @param name z.B. "Mustermann, Max"
     */
    public Adressat(String name) {
        super(name);
    }

}
