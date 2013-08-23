/*
 * DownloadTask.java
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

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import org.sociotech.unui.android.updater.handler.OnTaskListener;
import org.sociotech.unui.android.updater.handler.TaskError;
import org.sociotech.unui.android.updater.helper.Md5Helper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class DownloadTask extends AsyncTask<DownloadTaskParam, Integer, DownloadTaskResult> {

    private DownloadTaskParam m_taskParam;
    private OnTaskListener<DownloadTaskParam, DownloadTaskResult> m_onTaskListener = null;

    public void setOnTaskListener(OnTaskListener<DownloadTaskParam, DownloadTaskResult> onTaskListener) {
        this.m_onTaskListener = onTaskListener;
    }

    @Override
    protected DownloadTaskResult doInBackground(DownloadTaskParam... downloadTaskParams) {

        if(m_onTaskListener == null) {
            Log.e("ApkUpdater", "OnTaskListener is missing");
            return null;
        }
        if(downloadTaskParams == null || downloadTaskParams.length != 1) {
            Log.e("ApkUpdater", "DownloadTaskParam is missing");
            return null;
        }

        try {

            m_taskParam = downloadTaskParams[0];

            // Prepare url
            URL downloadUrl = m_taskParam.getDownloadUrl();
            if(downloadUrl == null) {
                onError(new TaskError("Download Url is null."));
                return null;
            }
            Log.d("ApkUpdater", downloadUrl.toString());

            //Prepare Http url connection
            HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
            if(connection == null) {
                onError(new TaskError("Http connection is null."));
                return null;
            }
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

            // Prepare output path
            File downloadPath = new File(m_taskParam.getDownloadPath());
            if(downloadPath == null) {
                onError(new TaskError("Download path error."));
                return null;
            }

            // Create directory in case it doesn't exist
            if(!downloadPath.exists())
                downloadPath.mkdirs();

            // Prepare file output path
            File downloadFilePath = new File(downloadPath, m_taskParam.getDownloadFileName());
            if(downloadFilePath == null) {
                onError(new TaskError("Download file path error."));
                return null;
            }

            // Prepare file output stream
            FileOutputStream outputStream = new FileOutputStream(downloadFilePath, false);  // overwrite existing file
            if(outputStream == null) {
                onError(new TaskError("Download file output stream error."));
                return null;
            }

            // Create Message Digest
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            if(messageDigest == null) {
                onError(new TaskError("Message digest instantiation error."));
                return null;
            }

            // Write file from HTTP Stream into file
            InputStream is = connection.getInputStream();

            // Create digest stream out of file stream
            is = new DigestInputStream(is, messageDigest);

            // Create buffer to read several bytes at once
            byte[] buffer = new byte[1024];
            int len1 = 0;
            int bytesDownloaded = 0;
            while ((len1 = is.read(buffer)) > 0) {

                // Writes into file output stream
                outputStream.write(buffer, 0, len1);

                // Measure the amount of downloaded files
                bytesDownloaded += len1;

                // Calls onProgressUpdate and hands over progress values
                publishProgress(bytesDownloaded);
            }
            outputStream.close();
            is.close();

            // Extract MD5 checksum
            byte[] md5Digest = messageDigest.digest();
            String downloadFileChecksum = Md5Helper.parseMd5Digest(md5Digest);

            // Calls onPostExecute
            return new DownloadTaskResult(m_taskParam, downloadFileChecksum);

        } catch (Exception e) {
            Log.w("ApkUpdater", "[WARNING #900] Download failed. Download Url: " + e.getMessage(), e);
        }

        return null; // Only in case of error
    }


    @Override
    protected void onProgressUpdate(Integer... progress) {

        if(progress.length != 1) {
            onError(new TaskError("Progress update error."));
            return;
        }

        // Progress Callback
        onProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(final DownloadTaskResult taskResult) {

        // Success Callback
        onSuccess(taskResult);  // Note: No null checks needed
    }

    private void onSuccess(final DownloadTaskResult downloadTaskResult) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                m_onTaskListener.onSuccess(downloadTaskResult);
            }
        });
    }

    private void onProgress(final Integer progress) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                m_onTaskListener.onProgress(progress);
            }
        });
    }

    private void onError(final TaskError taskError) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                m_onTaskListener.onError(taskError, m_taskParam);
            }
        });
    }


}
