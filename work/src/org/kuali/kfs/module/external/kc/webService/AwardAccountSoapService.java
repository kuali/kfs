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
package org.kuali.kfs.module.external.kc.webService;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.KfsService;
import org.kuali.kra.external.award.AwardAccountService;

@WebServiceClient(name = KcConstants.AwardAccount.SOAP_SERVICE_NAME, 
                  wsdlLocation = "http://test.kc.kuali.org/kc-trunk/remoting/awardAccountSoapService?wsdl",
                  targetNamespace = KcConstants.KC_NAMESPACE_URI) 
public class AwardAccountSoapService extends KfsService {

    public final static QName awardAccountServicePort = new QName(KcConstants.KC_NAMESPACE_URI, KcConstants.AwardAccount.SERVICE_PORT);
    static {
        try {
           getWsdl(KcConstants.AwardAccount.SERVICE); 
         } catch (MalformedURLException e) {
             LOG.warn("Can not initialize the wsdl");
         }
    }

    public AwardAccountSoapService() throws MalformedURLException {
        super(getWsdl(KcConstants.AwardAccount.SERVICE), KcConstants.AwardAccount.SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns AwardAccountService
     */
    @WebEndpoint(name = "awardAccountServicePort")
    public AwardAccountService getAwardAccountServicePort() {
        return super.getPort(awardAccountServicePort, AwardAccountService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns AwardAccountService
     */
    @WebEndpoint(name = "awardAccountServicePort")
    public AwardAccountService getAwardAccountServicePort(WebServiceFeature... features) {
        return super.getPort(awardAccountServicePort, AwardAccountService.class, features);
    }

    public URL getWsdl() throws MalformedURLException {
        return super.getWsdl(KcConstants.AwardAccount.SERVICE);
    }

}
