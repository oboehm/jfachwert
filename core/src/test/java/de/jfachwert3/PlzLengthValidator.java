/*
 * Copyright (c) 2022 by Oli B.
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
 * (c)reated 01.06.22 by oboehm
 */
package de.jfachwert3;

import de.jfachwert.pruefung.LengthValidator;

/**
 * Die Klasse PlzLengthValidator wurde zum Nachstellen von Issue #15
 * eingefuehrt.
 *
 * @author oboehm
 * @since 4.1 (01.06.22)
 */
public class PlzLengthValidator extends LengthValidator<Integer> {

    public PlzLengthValidator() {
        super(3, 10);
    }

}
