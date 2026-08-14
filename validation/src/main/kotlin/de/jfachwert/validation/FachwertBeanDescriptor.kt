/*
 * Copyright (c) 2026 by Oli B.
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
 * (c)reated 13.08.26 by oboehm (ob@oasd.de)
 */
package de.jfachwert.validation

/**
 * Eine minimale Implementierung des BeanDescriptor-Interface von Jakarta.
 * Fachwerte deklarieren ihre Constraints nicht ueber Annotationen, sondern
 * validieren sich intern selbst. Deshalb liefert dieser Descriptor keine
 * Constraints, wohl aber die Element-Klasse.
 *
 * @author oboehm
 * @since 6.8.1
 * @deprecated ins metadata-Package verschoben
 */
@Deprecated(message = "nach metadata verschoben")
class FachwertBeanDescriptor(elementClass: Class<*>) : de.jfachwert.validation.metadata.FachwertBeanDescriptor(
    elementClass
) {
}