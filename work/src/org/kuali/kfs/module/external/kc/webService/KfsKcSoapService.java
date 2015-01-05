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
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.ksb.api.registry.ServiceInfo;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;

public abstract class KfsKcSoapService extends Service {
    protected static final Logger LOG = Logger.getLogger(KfsKcSoapService.class);

    protected KfsKcSoapService(URL wsdlDocumentLocation, QName serviceName) {
        super(wsdlDocumentLocation, serviceName);
    }

    protected static URL getWsdl(QName qname) throws MalformedURLException {
        URL url = null;
        String webServiceServer = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY);

        if (webServiceServer == null) {
            // look for service on the KSB registry
            ServiceRegistry serviceRegistry = SpringContext.getBean(ServiceRegistry.class);
            List<ServiceInfo> wsdlServices = serviceRegistry.getOnlineServicesByName(qname);
            if (wsdlServices.size() > 0 ) {
                ServiceInfo serviceInfo = wsdlServices.get(0);
                String wsdlName = serviceInfo.getEndpointUrl() + "?wsdl";
                url = new URL(wsdlName);
            }
        } else {
            url  = new URL(webServiceServer + "/remoting/" +  qname.getLocalPart() + "?wsdl");
        }
        return url;
    }

    public abstract URL getWsdl() throws MalformedURLException;
}
