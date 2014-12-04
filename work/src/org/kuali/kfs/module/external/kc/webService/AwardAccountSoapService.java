/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
