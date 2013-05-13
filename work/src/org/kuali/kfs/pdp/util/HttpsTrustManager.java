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
package org.kuali.kfs.pdp.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * A simple implementation of X509TrustManager which bypass any server certificate.
 * This implementation can be used for known trusted HTTPS URLs that don't require authentication.
 */
public class HttpsTrustManager implements X509TrustManager {

    public HttpsTrustManager() { 
    }

    public void checkClientTrusted(X509Certificate chain[], String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate chain[], String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
