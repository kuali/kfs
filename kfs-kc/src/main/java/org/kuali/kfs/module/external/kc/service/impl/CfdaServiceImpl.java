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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.CfdaDTO;
import org.kuali.kfs.module.external.kc.service.ExternalizableLookupableBusinessObjectService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.CfdaNumberSoapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kra.external.Cfda.service.CfdaNumberService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class CfdaServiceImpl implements ExternalizableLookupableBusinessObjectService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CfdaServiceImpl.class);

    protected ConfigurationService configurationService;

    protected CfdaNumberService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        CfdaNumberService cfdaNumberService = (CfdaNumberService) GlobalResourceLoader.getService(KcConstants.Cfda.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (cfdaNumberService == null) {
            LOG.warn("Couldn't get CfdaNumberService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            CfdaNumberSoapService soapService = null;
            try {
                soapService = new CfdaNumberSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize CfdaNumberSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize CfdaNumberSoapService: " + ex.getMessage());
            }
            cfdaNumberService = soapService.getCfdaNumberServicePort();
        }
        return cfdaNumberService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {

        Collection cfda = findMatching(primaryKeys);

        if (cfda != null && cfda.iterator().hasNext()) {
            return (ContractsAndGrantsCfda) cfda.iterator().next();
        }
        else {
            return null;
        }
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        List<CfdaDTO> cfdas = null;
        java.util.List<HashMapElement> hashMapList = new ArrayList<HashMapElement>();

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if (KcConstants.Cfda.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key) && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val);
                hashMapList.add(hashMapElement);
            }
        }

        try {
            cfdas = getWebService().lookupCfda(hashMapList);
        }
        catch (WebServiceException ex) {
            LOG.error("Could not retrieve cfda: "+ ex.getMessage());
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, getConfigurationService().getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY));
        }

        if (cfdas == null) {
            cfdas = new ArrayList();
        }

        return cfdas;
    }

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return new ArrayList(findMatching(fieldValues));
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
