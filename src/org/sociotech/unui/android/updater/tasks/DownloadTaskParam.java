/*
 * DownloadTaskParam.java
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

package org.sociotech.unui.android.updater.tasks;

import java.net.URL;

public class DownloadTaskParam {

    private URL m_downloadUrl;
    private String m_downloadPath;
    private String m_downloadFileName;

    public DownloadTaskParam(URL downloadUrl, String downloadPath, String downloadFileName) {
        this.m_downloadUrl = downloadUrl;
        this.m_downloadPath = downloadPath;
        this.m_downloadFileName = downloadFileName;
    }

    public URL getDownloadUrl() {
        return m_downloadUrl;
    }

    public String getDownloadPath() {
        return m_downloadPath;
    }

    public String getDownloadFileName() {
        return m_downloadFileName;
    }
}
