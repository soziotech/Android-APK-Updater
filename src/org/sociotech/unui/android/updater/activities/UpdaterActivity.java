
/*
 * UpdaterActivity.java
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

package org.sociotech.unui.android.updater.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.sociotech.unui.android.updater.R;
import org.sociotech.unui.android.updater.config.UpdaterConfig;
import org.sociotech.unui.android.updater.config.UpdaterConfigParser;
import org.sociotech.unui.android.updater.handler.OnTaskListener;
import org.sociotech.unui.android.updater.handler.TaskError;
import org.sociotech.unui.android.updater.helper.ApkVersionHelper;
import org.sociotech.unui.android.updater.helper.OnlineCheckHelper;
import org.sociotech.unui.android.updater.tasks.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the Splash activity which is loaded when the application is invoked
 */
public class UpdaterActivity extends Activity
{
    private static String UPDATES_DEFAULT_PATH = "/updates/";
    private static Integer INTENT_RESULT_NR = 85577;

    private UpdaterConfig m_updaterConfig;
    private String m_updatesDownloadPath;
    private String m_progressFormat;
    private ProgressBar m_progressBar;
    private TextView m_progressTextView;

    private String m_fileChecksum = "";
    private String m_fileVersion = "";
    private int m_fileSize = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.updater_screen);

            // Load config
            UpdaterConfigParser configParser = new UpdaterConfigParser(this);
            m_updaterConfig = configParser.getConfig();

            // Prepare updates path
            m_updatesDownloadPath = m_updaterConfig.getDownloadRelativePath();
            if(!m_updatesDownloadPath.isEmpty()) {
                m_updatesDownloadPath = Environment.getExternalStorageDirectory() + m_updatesDownloadPath;
            } else {
                m_updatesDownloadPath = Environment.getDownloadCacheDirectory() + UPDATES_DEFAULT_PATH;
            }

            // Init progress bar
            m_progressFormat = getResources().getString(R.string.apk_progress_format_received);
            m_progressBar = (ProgressBar)findViewById(R.id.progressbar);
            m_progressTextView = (TextView)findViewById(R.id.progress_text);
            hideProgress();

            // hides system bar - showing dots
            View v = findViewById(android.R.id.content);
            v.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

            // check connection
            checkInternetConnection();


        } catch(Exception e) {

            Log.e("ApkUpdater", "[ERROR #000] onCreate() failed. " + e.getMessage(), e);
            showErrorOccurred(getString(R.string.error_000_init));
        }

    }

    /**
     * Checks Internet connection state.
     */
    protected void checkInternetConnection() {
        if (!OnlineCheckHelper.isInternetAvailable(this)) {
            showNoInternetAvailable();
        }  else {
            showCheckForUpdates();
        }
    }

    /**
     * Updater State: retry in case no Internet connection is available.
     */
    protected void showNoInternetAvailable() {

        // Show text that no Internet is available
        final TextView messageText = (TextView)findViewById(R.id.setup_text);
        messageText.setText(getResources().getString (R.string.error_900_connection));
        messageText.setVisibility(View.VISIBLE);

        hideProgress();

        // Show retry button
        final Button retryButton = (Button)findViewById(R.id.setup_button);
        retryButton.setText(getResources().getString(R.string.retry_button));
        retryButton.setVisibility(View.VISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Show retry update check
                checkInternetConnection();
            }
        });
    }

    /**
     * Updater State: Allows update check in case Internet is available.
     */
    protected void showCheckForUpdates() {

        // Show text that allows check for update
        final TextView checkUpdatesText = (TextView)findViewById(R.id.setup_text);
        checkUpdatesText.setText(getResources().getString (R.string.apk_check));
        checkUpdatesText.setVisibility(View.VISIBLE);

        hideProgress();

        // Show Update button
        final Button checkUpdatesButton = (Button)findViewById(R.id.setup_button);
        checkUpdatesButton.setText(getResources().getString (R.string.apk_check_button));
        checkUpdatesButton.setVisibility(View.VISIBLE);
        checkUpdatesButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Show Update Check Progress
                showStartedUpdateCheck();

                // Download Checksum
                if(m_updaterConfig.isChecksumEnabled()) {
                    downloadChecksum();
                }

                // Download File Size
                if(m_updaterConfig.isFileSizeEnabled()) {
                    downloadFileSize();
                }

                // Download Version File
                if(m_updaterConfig.isVersionCheckEnabled()) {
                    downloadVersionFile();
                } else {
                    showDownloadUpdate();
                }
            }
        });
    }

    protected void showStartedUpdateCheck() {

        // Show No Updates Available text
        final TextView messageText = (TextView)findViewById(R.id.setup_text);
        messageText.setText(getResources().getString (R.string.apk_check_progress));
        messageText.setVisibility(View.VISIBLE);

        hideProgress();

        // Hide Button
        final Button checkUpdatesButton = (Button)findViewById(R.id.setup_button);
        checkUpdatesButton.setVisibility(View.GONE);
    }

    protected void showNoUpdatesAvailable() {

        // Show No Updates Available text
        final TextView messageText = (TextView)findViewById(R.id.setup_text);
        messageText.setText(getResources().getString (R.string.apk_check_noupdates));
        messageText.setVisibility(View.VISIBLE);

        hideProgress();

        // Show Retry Button
        final Button checkUpdatesButton = (Button)findViewById(R.id.setup_button);
        checkUpdatesButton.setText(getResources().getString (R.string.retry_button));
        checkUpdatesButton.setVisibility(View.VISIBLE);
        /*checkUpdatesButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                try {
                    // Prepare param
                    DownloadChecksumTaskParam downloadChecksumTaskParam  = new DownloadChecksumTaskParam(UpdaterActivity.this, m_updaterConfig);

                    // Download file info and checksum
                    DownloadChecksumTask checksumTask = new DownloadChecksumTask();
                    checksumTask.execute(new DownloadChecksumTaskParam[] {downloadChecksumTaskParam});
                } catch (Exception e) {
                    Log.e("ApkUpdater", "ERROR #011: " + e.getMessage(), e);
                    showErrorOccurred("Update Error! Please inform Martin Burkhard. Error code: #011");
                }

            }
        });*/
    }

    public void showDownloadUpdate() {

        // Show Apk download text
        final TextView setupText = (TextView)findViewById(R.id.setup_text);
        setupText.setText(getResources().getString (R.string.apk_download));
        setupText.setVisibility(View.VISIBLE);

        hideProgress();

        // Show download button and assign click listener
        final Button downloadButton = (Button)findViewById(R.id.setup_button);
        downloadButton.setText(getResources().getString (R.string.apk_download_button));
        downloadButton.setVisibility(View.VISIBLE);
        downloadButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Change visibility state
                showStartedDownload();

                // Download APK
                downloadApkFile();
            }
        });
    }



    public void showStartedDownload() {
        // Switch text and hide button
        final TextView downloadText = (TextView)findViewById(R.id.setup_text);
        downloadText.setText(getResources().getString (R.string.apk_downloading));
        downloadText.setVisibility(View.VISIBLE);

        m_progressBar = (ProgressBar)findViewById(R.id.progressbar);
        m_progressBar.setProgress(0);
        m_progressBar.setVisibility(View.VISIBLE);

        final Button downloadButton = (Button)findViewById(R.id.setup_button);
        downloadButton.setVisibility(View.GONE);
    }

    public void showInstallUpdate(final DownloadTaskResult downloadTaskResult) {
        // Switch text and show button
        final TextView setupText = (TextView)findViewById(R.id.setup_text);
        setupText.setText(getResources().getString (R.string.apk_download_done));
        setupText.setVisibility(View.VISIBLE);

        hideProgress();

        final Button setupButton = (Button)findViewById(R.id.setup_button);
        setupButton.setText(getResources().getString (R.string.apk_setup_button));
        setupButton.setVisibility(View.VISIBLE);
        setupButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                DownloadTaskParam taskParam = downloadTaskResult.getTaskParam();

                // Start Apk Install / Replacement
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(taskParam.getDownloadPath() + "/" + taskParam.getDownloadFileName())), "application/vnd.android.package-archive");
                startActivityForResult(intent, INTENT_RESULT_NR);
                setupButton.setVisibility(View.GONE);

            }
        });
    }

    public void showIntegrityMismatch() {
        final TextView integrityText = (TextView)findViewById(R.id.setup_text);
        integrityText.setText(getResources().getString (R.string.apk_download_integrity));
        integrityText.setVisibility(View.VISIBLE);

        showExitState();
    }

   protected void exitApplication() {

        final TextView doneText = (TextView)findViewById(R.id.setup_text);
        doneText.setText(getResources().getString (R.string.apk_setup_done));
        doneText.setVisibility(View.VISIBLE);

        showExitState();
    }

    public void showErrorOccurred(final String errorText) {
        final TextView errorTextView = (TextView)findViewById(R.id.setup_text);
        errorTextView.setText(errorText);
        errorTextView.setVisibility(View.VISIBLE);

        hideProgress();

        final Button sendErrorButton = (Button)findViewById(R.id.setup_button);
        sendErrorButton.setText(getResources().getString (R.string.send_error_button));
        sendErrorButton.setVisibility(View.VISIBLE);
        sendErrorButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                PackageManager pm = getPackageManager();
                String applicationName = "(unknown)";
                String packageName = "(unknown)";
                String versionName = "(unknown)";
                String versionCode = "(unknown)";
                String lastUpdateTime = "(unknown)";
                try {

                    // Extract application info
                    ApplicationInfo ai = pm.getApplicationInfo( getPackageName(), 0);
                    applicationName = (String)pm.getApplicationLabel(ai);

                    // Extract package info
                    PackageInfo pi = pm.getPackageInfo( getPackageName(), 0);
                    packageName = pi.packageName;
                    versionName = pi.versionName;
                    versionCode = String.valueOf(pi.versionCode);
                    lastUpdateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(pi.lastUpdateTime));

                } catch (Exception e) {
                    Log.w("ApkUpdater", "[WARNING #990] Could not extract application and package info.");
                }

                StringBuilder bodyTextBuilder = new StringBuilder();
                bodyTextBuilder.append(getResources().getString(R.string.admin_salutation)).append(" ")
                        .append(getResources().getString(R.string.admin_contact)).append(",\n");
                bodyTextBuilder.append(getResources().getString(R.string.error_message)).append("\n").append("\n");
                bodyTextBuilder.append("ApplicationName = ").append(applicationName).append("\n");
                bodyTextBuilder.append("PackageName    = ").append(packageName).append("\n");
                bodyTextBuilder.append("VersionName    = ").append(versionName).append("\n");
                bodyTextBuilder.append("VersionCode    = ").append(versionCode).append("\n");
                bodyTextBuilder.append("LastUpdateTime = ").append(lastUpdateTime).append("\n");
                bodyTextBuilder.append("ErrorText      = ").append(errorText).append("\n").append("\n");

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.admin_address)});
                email.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.error_subject));
                email.putExtra(Intent.EXTRA_TEXT, bodyTextBuilder.toString());
                email.setType("message/rfc822");
                startActivity(email);
            }
        });
    }

    protected void hideProgress() {
        // Hide Progress Text
        final TextView progressText = (TextView)findViewById(R.id.progress_text);
        progressText.setText("");
        progressText.setVisibility(View.GONE);

        // Hide Progress Bar
        m_progressBar.setProgress(0);
        m_progressBar.setVisibility(View.GONE);
    }

    protected void showExitState() {

        hideProgress();

        final Button exitButton = (Button)findViewById(R.id.setup_button);
        exitButton.setText(getResources().getString (R.string.exit_button));
        exitButton.setVisibility(View.VISIBLE);
        exitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Close App
                finish();
            }
        });
    }


    /**
     * Downloads checksum of new file.
     */
    protected void downloadChecksum() {
        try {
            // Prepare params
            DownloadTaskParam downloadTaskParam = new DownloadTaskParam(m_updaterConfig.getChecksumFileUrl(), m_updatesDownloadPath, m_updaterConfig.getChecksumStoreFileName());

            // Download file checksum
            DownloadTask downloadTask = new DownloadTask();

            // Add listener called by DownloadTask
            downloadTask.setOnTaskListener(new OnTaskListener<DownloadTaskParam, DownloadTaskResult>() {
                @Override
                public void onSuccess(DownloadTaskResult downloadTaskResult) {

                    if(downloadTaskResult != null)  {
                        File file = new File(downloadTaskResult.getTaskParam().getDownloadPath(), downloadTaskResult.getTaskParam().getDownloadFileName());
                        FileInputStream in;
                        try {
                            in = new FileInputStream(file);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            // Read MD5 checksum
                            m_fileChecksum = reader.readLine();
                            if (m_fileChecksum == null || m_fileChecksum.isEmpty())
                                throw new NullPointerException("md5Checksum");

                            reader.close();
                            in.close();

                            return;

                        } catch (Exception e) {
                            Log.w("ApkUpdater", "[WARNING #010] downloadChecksum() failed. " + e.getMessage(), e);
                        }
                    }

                    // In case no checksum file exists or any error occurred
                    m_fileChecksum = "";
                }

                @Override
                public void onProgress(int bytesDownloaded) {
                    // Skip
                }

                @Override
                public void onError(TaskError taskError, DownloadTaskParam downloadTaskParam) {
                    Log.w("ApkUpdater", String.format("[WARNING #011] Could not download checksum file. Download Url: %s", downloadTaskParam.getDownloadUrl()));
                 }
            });

            // Execute
            downloadTask.execute(downloadTaskParam);

        } catch (Exception e) {
            Log.e("ApkUpdater", "[ERROR #010] downloadChecksum() failed. " + e.getMessage(), e);
            showErrorOccurred(getString(R.string.error_010_checksum));
        }
    }

    protected void downloadFileSize() {
        try {
            // Prepare params
            DownloadTaskParam downloadTaskParam = new DownloadTaskParam(m_updaterConfig.getFileSizeUrl(), m_updatesDownloadPath, m_updaterConfig.getFileSizeStoreFileName());

            // Download file size
            DownloadTask downloadTask = new DownloadTask();

            // Add listener called by DownloadTask
            downloadTask.setOnTaskListener(new OnTaskListener<DownloadTaskParam, DownloadTaskResult>() {
                @Override
                public void onSuccess(DownloadTaskResult downloadTaskResult) {

                    if(downloadTaskResult != null)  {
                        DownloadTaskParam downloadTaskParam = downloadTaskResult.getTaskParam();
                        File file = new File(downloadTaskParam.getDownloadPath(),downloadTaskParam.getDownloadFileName());
                        FileInputStream in;
                        try {
                            in = new FileInputStream(file);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            // Read file Size
                            String strFileSize = reader.readLine();
                            if (strFileSize == null || strFileSize.isEmpty())
                                throw new NullPointerException("fileSize");

                            m_fileSize = Integer.parseInt(strFileSize);

                            reader.close();
                            in.close();

                            return;

                        } catch (Exception e) {
                            Log.w("ApkUpdater", "[WARNING #020] downloadFileSize() failed. " + e.getMessage(), e);
                        }
                    }

                    // In case no file size exists or any error occurred
                    m_fileSize = -1;
                }

                @Override
                public void onProgress(int bytesDownloaded) {
                    // Skip
                }

                @Override
                public void onError(TaskError taskError, DownloadTaskParam downloadTaskParam) {
                    Log.w("ApkUpdater", String.format("[WARNING #021] Could not download file size. Download Url: %s", downloadTaskParam.getDownloadUrl()));
                }
            });

            // Execute
            downloadTask.execute(downloadTaskParam);

        } catch (Exception e) {
            Log.e("ApkUpdater", "[ERROR #020] downloadFileSize() failed. " + e.getMessage(), e);
            showErrorOccurred(getString(R.string.error_020_filesize));
        }
    }

    protected void downloadVersionFile() {
        try {
            // Prepare params
            DownloadTaskParam downloadTaskParam = new DownloadTaskParam(m_updaterConfig.getVersionFileUrl(), m_updatesDownloadPath, m_updaterConfig.getVersionStoreFileName());

            // Download file version
            DownloadTask downloadTask = new DownloadTask();

            // Add listener called by DownloadTask
            downloadTask.setOnTaskListener(new OnTaskListener<DownloadTaskParam, DownloadTaskResult>() {
                @Override
                public void onSuccess(DownloadTaskResult downloadTaskResult) {

                    if(downloadTaskResult != null)  {
                        DownloadTaskParam taskParam = downloadTaskResult.getTaskParam();
                        File file = new File(taskParam.getDownloadPath(), taskParam.getDownloadFileName());
                        FileInputStream in;
                        try {
                            in = new FileInputStream(file);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            // Read file version
                            m_fileVersion = reader.readLine();

                            reader.close();
                            in.close();

                            // Compare file versions
                            String installedApkVersion = ApkVersionHelper.getInstalledApkVersion(UpdaterActivity.this, m_updaterConfig.getApkNamespace());
                            if(m_fileVersion != null && !m_fileVersion.isEmpty() && installedApkVersion != null && !installedApkVersion.isEmpty() && installedApkVersion.equalsIgnoreCase(m_fileVersion)) {
                                showNoUpdatesAvailable();
                            }
                            else {
                                showDownloadUpdate();
                            }
                            return;

                        } catch (Exception e) {
                            Log.w("ApkUpdater", "[WARNING #030] downloadVersionFile() failed. " + e.getMessage(), e);
                        }
                    }

                    // In case no version file exists or any error occurred
                    showDownloadUpdate();

                }

                @Override
                public void onProgress(int bytesDownloaded) {
                    // Skip
                }

                @Override
                public void onError(TaskError taskError, DownloadTaskParam downloadTaskParam) {
                    Log.w("ApkUpdater", String.format("[WARNING #031] Could not download version file. Download Url: %s", downloadTaskParam.getDownloadUrl()));
                }
            });

            // Execute
            downloadTask.execute(downloadTaskParam);

        } catch (Exception e) {
            Log.e("ApkUpdater", "[ERROR #030] downloadVersionFile() failed. " + e.getMessage(), e);
            showErrorOccurred(getString(R.string.error_030_versionfile));
        }
    }

    private void downloadApkFile() {

        // Prepare param
        DownloadTaskParam downloadTaskParam = new DownloadTaskParam(m_updaterConfig.getApkFileUrl(), m_updatesDownloadPath, m_updaterConfig.getApkStoreFileName());

        // Create download task
        DownloadTask downloadTask = new DownloadTask();

        // Add listener called by DownloadTask
        downloadTask.setOnTaskListener(new OnTaskListener<DownloadTaskParam, DownloadTaskResult>() {

            @Override
            public void onSuccess(DownloadTaskResult taskResult) {
                try {

                    // Perform integrity check
                    String downloadChecksum = taskResult.getDownloadFileChecksum();
                    if(m_updaterConfig.isChecksumEnabled() && downloadChecksum != null && !downloadChecksum.isEmpty() && m_fileChecksum != null && !m_fileChecksum.isEmpty() && !downloadChecksum.equalsIgnoreCase(m_fileChecksum))
                    {
                        showIntegrityMismatch();
                    }
                    else {
                        showInstallUpdate(taskResult);
                    }

                }catch(Exception e) {
                    Log.e("ApkUpdater", "[ERROR #050] downloadApkFile() failed. " + e.getMessage(), e);
                    showErrorOccurred(getString(R.string.error_040_apkfile));
                }
            }

            @Override
            public void onProgress(int bytesDownloaded) {
                // Generate progress text value
                String progressTextValue;
                if(m_updaterConfig.isFileSizeEnabled() && m_fileSize != -1) {
                    double percent = (double)bytesDownloaded / (double)m_fileSize * 100.0;
                    progressTextValue = String.format("%s %.2f%% (%d Bytes)", m_progressFormat, percent, bytesDownloaded);
                    m_progressBar.setProgress((int)percent);
                }
                else {
                    // Hide progress bar in case we have no file size available
                    m_progressBar.setVisibility(View.GONE);

                    // Only show amount of bytes downloaded
                    progressTextValue = String.format("%d Bytes", bytesDownloaded);
                }

                // Update progress text view
                m_progressTextView.setText(progressTextValue);
                m_progressTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(TaskError taskError, DownloadTaskParam downloadTaskParam) {
                Log.w("ApkUpdater", String.format("[WARNING #041] Could not download APK file. Download Url: %s", downloadTaskParam.getDownloadUrl()));
                showErrorOccurred(getString(R.string.error_041_apkfile));
            }
        });

        downloadTask.execute(downloadTaskParam);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            // Called after the elisa App has been installed / started
            if (requestCode == INTENT_RESULT_NR) {
                exitApplication();
            }

        }catch(Exception e) {
            Log.e("ApkUpdater", "[ERROR #050] Could not install APK. " + e.getMessage(), e);
            showErrorOccurred(getString(R.string.error_050_installapk));
        }
    }


}