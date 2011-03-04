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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.cg.businessobject.CfdaUpdateResults;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Cfda;
import org.kuali.kfs.module.external.kc.service.CfdaService;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.webService.CfdaNumberService;
import org.kuali.kfs.module.external.kc.webService.CfdaNumberSoapService;
import org.kuali.kfs.module.external.kc.webService.EffortReportingServiceSoapService;
import org.kuali.kfs.module.external.kc.webService.InstitutionalBudgetCategoryService;
import org.kuali.kfs.module.external.kc.webService.InstitutionalBudgetCategorySoapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public class CfdaServiceImpl implements ExternalizableBusinessObjectService,CfdaService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CfdaServiceImpl.class);
    
    protected  CfdaNumberService getWebService() {
        QName cfdaSoapSERVICE = new QName("KC", "cfdaNumberSoapService");
        //CfdaNumberSoapService   soapService = (CfdaNumberSoapService) GlobalResourceLoader.getService(cfdaSoapSERVICE);
        
        //QName serviceName = new QName("KC", "cfdaNumberService");
        // CfdaNumberService port = (CfdaNumberService) GlobalResourceLoader.getService(serviceName);

        CfdaNumberSoapService soapService = new CfdaNumberSoapService();
        CfdaNumberService port = soapService.getCfdaNumberServicePort();  
        return port;

    }
    public Cfda getByPrimaryId(String acctNumber) {
        Cfda cfda = new Cfda();
        if (StringUtils.isBlank(acctNumber)) {
            return cfda;
        }
         
        String cfdaNumber = getWebService().getCfdaNumber(acctNumber);
        LOG.debug("Web Service sent " + acctNumber + " got " + cfdaNumber);
        cfda.setCfdaNumber(cfdaNumber);
        return cfda;
    }


    public CfdaUpdateResults update() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        return null;
    }

    public Collection findMatching(Map fieldValues) {
        return null;
    }  
}
