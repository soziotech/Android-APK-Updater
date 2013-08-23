/*
 * Md5Helper.java
 *
 * Copyright (c) 2013. Martin Burkhard, CSCM Cooperation Systems Center Munich,
 * at the Institute for Software Technology, Bundeswehr University Munich.
 *
 * This program is connected to the research project SI-Screen funded by the European
 * AAL Joint Programme (AAL-2009-2-088), the German Ministry of Education and Research
 * and German VDI/VDE IT (BMBF – FKZ: 16SV3982). The joint project was coordinated by
 * the Innovationsmanufaktur GmbH and carried out by ten international partners.
 * For more information, see the project website http://www.si-screen.eu.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Project: Android-APK-Updater
 *   Author: Martin Burkhard
 *     Date: 8/23/13 7:40 AM
 */

package org.sociotech.unui.android.updater.helper;

public class Md5Helper {

    public static String parseMd5Digest(byte[] md5Digest) {

        // Extract md5 hex string
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < md5Digest.length; i++) {
            if ((0xff & md5Digest[i]) < 0x10) {
                hexString.append("0"
                        + Integer.toHexString((0xFF & md5Digest[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & md5Digest[i]));
            }
        }

        return hexString.toString();
    }

}
