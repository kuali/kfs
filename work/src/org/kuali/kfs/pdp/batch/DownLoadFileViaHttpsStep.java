/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.pdp.batch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.util.HttpsTrustManager;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

/**
 * Batch step to download a text file from an HTTPS URL to the specified target directory.
 */
public class DownLoadFileViaHttpsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DownLoadFileViaHttpsStep.class);

    private String targetDirectoryPath;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        //String sourceFileUrl = "https://www.fededirectory.frb.org/FedACHdir.txt";
        String sourceFileUrl = getParameterService().getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, PdpParameterConstants.FEDERAL_ACH_BANK_FILE_URL);
        String targetFileName = getParameterService().getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, PdpParameterConstants.ACH_BANK_INPUT_FILE);
        String targetFilePath = targetDirectoryPath + targetFileName;

        LOG.info("Downloading file from " + sourceFileUrl + " to " + targetFilePath);

        try {
            X509TrustManager manager = new HttpsTrustManager();
            TrustManager managers[] = {manager};
            SSLContext context = SSLContext.getInstance(PdpConstants.SECURE_SOCKET_PROTOCOL);
            context.init(null, managers, null);
            SSLSocketFactory factory = context.getSocketFactory();

            URL url = new URL(sourceFileUrl);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setSSLSocketFactory(factory);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintWriter out = new PrintWriter(targetFilePath);

            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {
               out.println(line);
               count++;
            }

            in.close();
            out.close();

            LOG.info("Total number of ACH Bank records downloaded: " + count);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public void setTargetDirectoryPath(String targetDirectoryPath) {
        this.targetDirectoryPath = targetDirectoryPath;
    }

}
