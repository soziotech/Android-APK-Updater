/*
 * OnlineCheckHelper.java
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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class OnlineCheckHelper {

    /**
     * Utility method to check the online status of Android device.
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null)
            return false;
        return netInfo.isConnectedOrConnecting();
    }
}