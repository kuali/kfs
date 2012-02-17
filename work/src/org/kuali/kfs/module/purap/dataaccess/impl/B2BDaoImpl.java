/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.dataaccess.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.kuali.kfs.module.purap.dataaccess.B2BDao;
import org.kuali.kfs.module.purap.exception.B2BConnectionException;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class B2BDaoImpl  extends PlatformAwareDaoBaseOjb  implements B2BDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BDaoImpl.class);

    /**
     * Take the request XML, post it to SciQuest, then get the response XML and return it.
     */
    public String sendPunchOutRequest(String request, String punchoutUrl) {
        LOG.debug("sendPunchOutRequest() started");

        try {
            URL url = new URL(punchoutUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "text/xml");

            OutputStream out = conn.getOutputStream();
            OutputStreamWriter outw = new OutputStreamWriter(out, "UTF-8");
            outw.write(request);
            outw.flush();
            outw.close();
            out.flush();
            out.close();

            InputStream inp = conn.getInputStream();

            StringBuffer response = new StringBuffer();
            int i = inp.read();
            while (i >= 0) {
                if (i >= 0) {
                    response.append((char) i);
                }
                i = inp.read();
            }
            return response.toString();
        }
        catch (MalformedURLException e) {
            LOG.error("postPunchOutSetupRequestMessage() Error posting setup", e);
            throw new B2BConnectionException("Unable to connect to remote site for punchout.", e);
        }
        catch (ProtocolException e) {
            LOG.error("postPunchOutSetupRequestMessage() Error posting setup", e);
            throw new B2BConnectionException("Unable to connect to remote site for punchout.", e);
        }
        catch (UnsupportedEncodingException e) {
            LOG.error("postPunchOutSetupRequestMessage() Error posting setup", e);
            throw new B2BConnectionException("Unable to connect to remote site for punchout.", e);
        }
        catch (IOException e) {
            LOG.error("postPunchOutSetupRequestMessage() Error posting setup", e);
            throw new B2BConnectionException("Unable to connect to remote site for punchout.", e);
        }
    }
}
