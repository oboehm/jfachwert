/*
 * Copyright (c) 2026 by Oliver Boehm
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
 * (c)reated 12.08.2026 by oboehm (ob@jfachwert.de)
 */
package de.jfachwert.validation

import jakarta.validation.ClockProvider
import java.time.Clock

/**
 * Ein ClockProvider, der die Systemuhr in der Standard-Zeitzone liefert.
 *
 * @author oboehm
 * @since 6.8.1
 */
class FachwertClockProvider : ClockProvider {

    override fun getClock(): Clock = Clock.systemDefaultZone()

    override fun toString(): String = "FachwertClockProvider"

}
