/*
 * UpdaterConfig.java
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

import java.net.MalformedURLException;
import java.net.URL;

public class UpdaterConfig {

    private String m_downloadRelativePath;
    private boolean m_versionCheckEnabled;
    private URL m_versionFileUrl;
    private String m_versionStoreFileName;
    private boolean m_fileSizeEnabled;
    private URL m_fileSizeUrl;
    private String m_fileSizeStoreFileName;
    private boolean m_checksumEnabled;
    private URL m_checksumFileUrl;
    private String m_checksumStoreFileName;
    private String m_apkNamespace;
    private URL m_apkFileUrl;
    private String m_apkStoreFileName;

    public UpdaterConfig() {
        m_downloadRelativePath = "";
        m_versionCheckEnabled = false;
        m_versionFileUrl = null;
        m_versionStoreFileName = "";
        m_fileSizeEnabled = false;
        m_fileSizeUrl = null;
        m_fileSizeStoreFileName = "";
        m_checksumEnabled = false;
        m_checksumFileUrl = null;
        m_checksumStoreFileName = "";
        m_apkNamespace = "";
        m_apkFileUrl = null;
        m_apkStoreFileName = "";
    }

    public String getDownloadRelativePath() {
        return m_downloadRelativePath;
    }

    public void setDownloadRelativePath(String downloadRelativePath) {
        this.m_downloadRelativePath = downloadRelativePath;
    }

    public boolean isVersionCheckEnabled() {
        return m_versionCheckEnabled;
    }

    public void setVersionCheckEnabled(boolean versionCheckEnabled) {
        this.m_versionCheckEnabled = versionCheckEnabled;
    }

    public URL getVersionFileUrl() {
        return m_versionFileUrl;
    }

    public void setVersionFileUrl(String versionFileUrl) throws MalformedURLException {
        this.m_versionFileUrl = new URL(versionFileUrl);
    }

    public String getVersionStoreFileName() {
        return m_versionStoreFileName;
    }

    public void setVersionStoreFileName(String versionStoreFileName) {
        this.m_versionStoreFileName = versionStoreFileName;
    }

    public boolean isFileSizeEnabled() {
        return m_fileSizeEnabled;
    }

    public void setFileSizeEnabled(boolean fileSizeEnabled) {
        this.m_fileSizeEnabled = fileSizeEnabled;
    }

    public URL getFileSizeUrl() {
        return m_fileSizeUrl;
    }

    public void setFileSizeUrl(String fileSizeUrl) throws MalformedURLException {
        this.m_fileSizeUrl = new URL(fileSizeUrl);
    }

    public String getFileSizeStoreFileName() {
        return m_fileSizeStoreFileName;
    }

    public void setFileSizeStoreFileName(String fileSizeStoreFileName) {
        this.m_fileSizeStoreFileName = fileSizeStoreFileName;
    }

    public boolean isChecksumEnabled() {
        return m_checksumEnabled;
    }

    public void setChecksumEnabled(boolean checksumEnabled) {
        this.m_checksumEnabled = checksumEnabled;
    }

    public URL getChecksumFileUrl() {
        return m_checksumFileUrl;
    }

    public void setChecksumFileUrl(String checksumFileUrl) throws MalformedURLException {
        this.m_checksumFileUrl = new URL(checksumFileUrl);
    }

    public String getChecksumStoreFileName() {
        return m_checksumStoreFileName;
    }

    public void setChecksumStoreFileName(String checksumStoreFileName) {
        this.m_checksumStoreFileName = checksumStoreFileName;
    }

    public String getApkNamespace() {
        return m_apkNamespace;
    }

    public void setApkNamespace(String apkNamespace) {
        this.m_apkNamespace = apkNamespace;
    }

    public URL getApkFileUrl() {
        return m_apkFileUrl;
    }

    public void setApkFileUrl(String apkUrl) throws MalformedURLException {
        this.m_apkFileUrl = new URL(apkUrl);
    }

    public String getApkStoreFileName() {
        return m_apkStoreFileName;
    }

    public void setApkStoreFileName(String apkStoreFileName) {
        this.m_apkStoreFileName = apkStoreFileName;
    }
}
