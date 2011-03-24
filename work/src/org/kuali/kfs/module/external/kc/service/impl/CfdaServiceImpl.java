/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCfda;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Cfda;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.webService.CfdaNumberService;
import org.kuali.kfs.module.external.kc.webService.CfdaNumberSoapService;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public class CfdaServiceImpl implements ExternalizableBusinessObjectService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CfdaServiceImpl.class);
    
    protected  CfdaNumberService getWebService() {
        CfdaNumberSoapService soapService = null;
        try {
            soapService = new CfdaNumberSoapService();
        }
        catch (MalformedURLException ex) {
            LOG.error("Could not intialize CfdaSoapService: " + ex.getMessage());
            throw new RuntimeException("Could not intialize CfdaSoapService: " + ex.getMessage());
        }
        CfdaNumberService port = soapService.getCfdaNumberServicePort(); 
        return port;
    }

    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Collection Cfda = findMatching(primaryKeys);
        if(Cfda != null && Cfda.iterator().hasNext())
            return (ContractsAndGrantsCfda)Cfda.iterator().next();
        else
            return null;
    }

    public Collection findMatching(Map fieldValues) {
        List cfdas = new ArrayList();     
        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KcConstants.Cfda.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                String cfdaNumber = getWebService().getCfdaNumber(val);
                if (cfdaNumber != null) {
                    ContractsAndGrantsCfda cfda = new Cfda(cfdaNumber);
                    cfdas.add(cfda);
                }
            }
        }
        return cfdas;
    }
}
