/*
 * UpdaterConfigParser.java
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

package org.sociotech.unui.android.updater.config;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import org.sociotech.unui.android.updater.R;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

public class UpdaterConfigParser {

    private static final String LOG_TAG = UpdaterConfigParser.class.getSimpleName();

    private Context m_context = null;
    private UpdaterConfig m_config = null;

    public UpdaterConfigParser(Context context) {
        this.m_context = context;
    }

    public UpdaterConfig getConfig() {
        if(m_config != null) return m_config;
        return loadConfigFromXml();
    }

    private UpdaterConfig loadConfigFromXml() {
        UpdaterConfig config = new UpdaterConfig();
        if(m_context == null) return config;

        InputStream is = m_context.getResources().openRawResource(R.raw.updater_config);
        if(is == null) return config;

        try {
            // Get a new XmlPullParser object from Factory
            XmlPullParser parser = Xml.newPullParser();
            // Set input source
            parser.setInput(is, null);
            // Get event type
            int eventType = parser.getEventType();

            boolean initUpdaterConfig = false;
            // Process tag while not reaching the end of document
            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType) {

                    // Analyse START_TAG
                    case XmlPullParser.START_TAG:
                        // get tag name
                        String tagName = parser.getName();
                        Log.d(LOG_TAG, "tagName = " + tagName);

                        if(!initUpdaterConfig) {
                            if(tagName.equalsIgnoreCase("updaterConfig")) {
                                initUpdaterConfig = true;
                            } else if(!initUpdaterConfig) {
                                throw new Exception("Invalid UpdaterConfig XML format.");
                            }
                        }
                        else if(tagName.equalsIgnoreCase("downloadRelativePath")) {
                            config.setDownloadRelativePath(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("versionCheckEnabled")) {
                            config.setVersionCheckEnabled(Boolean.parseBoolean(parser.nextText()));
                        }
                        else if(tagName.equalsIgnoreCase("versionFileUrl")) {
                            config.setVersionFileUrl(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("versionStoreFileName")) {
                            config.setVersionStoreFileName(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("fileSizeEnabled")) {
                            config.setFileSizeEnabled(Boolean.parseBoolean(parser.nextText()));
                        }
                        else if(tagName.equalsIgnoreCase("fileSizeUrl")) {
                            config.setFileSizeUrl(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("fileSizeStoreFileName")) {
                            config.setFileSizeStoreFileName(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("checksumEnabled")) {
                            config.setChecksumEnabled(Boolean.parseBoolean(parser.nextText()));
                        }
                        else if(tagName.equalsIgnoreCase("checksumFileUrl")) {
                            config.setChecksumFileUrl(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("checksumStoreFileName")) {
                            config.setChecksumStoreFileName(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("apkNamespace")) {
                            config.setApkNamespace(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("apkFileUrl")) {
                            config.setApkFileUrl(parser.nextText());
                        }
                        else if(tagName.equalsIgnoreCase("apkStoreFileName")) {
                            config.setApkStoreFileName(parser.nextText());
                        }

                        break;
                }

                // jump to next event
                eventType = parser.next();
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "TaskError loading updater config. Please inform Martin Burkhard. TaskError code: #099");
        } finally {
            return config;
        }
    }

}
