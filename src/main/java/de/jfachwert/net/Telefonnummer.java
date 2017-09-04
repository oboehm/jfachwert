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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (c)reated 04.09.2017 by oboehm (ob@oasd.de)
 */
package de.jfachwert.net;

import de.jfachwert.*;

/**
 * Die Klasse Telefonnummer steht fuer alle Arten von Rufnummern im
 * Telefon-Netz wie Fesetnetznummer, Faxnummer oder Mobilfunknummer.
 * Ueblicherweise bestehen Telefonnummern aus
 * <ul>
 *     <li>Laenderkennzahl (LKz),</li>
 *     <li>Ortsnetzkennzahl (ONKz) bzw. die eigentliche Vorwahl</li>
 *     <li>Teilnehmerrufnummer (RufNr),</li>
 *     <li>Durchwahl (optional).</li>
 * </ul>
 * Die Telefonnummer +49 30 12345-67 hat die Laenderkennzahl 49 (Deutschland),
 * die Vorwahl 030 (Berlin), die Teilnehmerrufnummer 12345 und die Durchwahl
 * 67.
 * <p>
 * Frueher waren Telefon-Netz und Computer-Netzwerke strikt getrennt.
 * Inwischen wachsen diese beiden Netze immer mehr zusammen und unterscheiden
 * sich nur noch durch das Netzwerkprotokoll. Deswegen ist diese Klasse
 * im gleichen Package 'net' wie die EMailAdresse zu finden und nicht in
 * 'comm' (fuer Kommunikation), wie urspruenglich geplant.
 * </p>
 *
 * @author oboehm
 * @since 0.5 (04.09.2017)
 */
public class Telefonnummer extends AbstractFachwert<String> {

    public Telefonnummer(String nummer) {
        super(nummer);
    }

}
