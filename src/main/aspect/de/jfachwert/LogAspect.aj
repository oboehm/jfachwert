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
 * (c)reated 01.05.17 by oliver (ob@oasd.de)
 */
package de.jfachwert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public aspect LogAspect {

    private static final Logger LOG = LogManager.getLogger(LogAspect.class);

    private pointcut constructors():
            execution(de.jfachwert..*.new(..)) && (!within(LogAspect));

    /**
     * Nach der Beendigung des Konstruktors wird das kreierte Objekt samt
     * Signature ausgegeben.
     */
    after() : constructors() {
        LOG.debug("'{}' was created with {}.", thisJoinPoint.getThis(), thisJoinPoint.getSignature());
    }

}
