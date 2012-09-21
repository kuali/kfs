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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.service.KfsService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.AwardAccountService;
import org.kuali.kfs.module.external.kc.webService.InstitutionalBudgetCategoryService;
import org.kuali.kfs.module.external.kc.webService.InstitutionalBudgetCategorySoapService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class BudgetCategoryServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetCategoryServiceImpl.class);

    protected InstitutionalBudgetCategoryService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        InstitutionalBudgetCategoryService institutionalBudgetCategoryService = (InstitutionalBudgetCategoryService) GlobalResourceLoader.getService(KcConstants.BudgetCategory.SERVICE);
        
        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (institutionalBudgetCategoryService == null) {
            LOG.warn("Couldn't get BudgetCategoryService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");

            InstitutionalBudgetCategorySoapService ss = null;
            try {
                ss = new InstitutionalBudgetCategorySoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize BudgetCategorySoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize BudgetCategorySoapService: " + ex.getMessage());
            }
            institutionalBudgetCategoryService = ss.getBudgetCategoryServicePort(); 
        }
        return institutionalBudgetCategoryService;
    }

    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Collection budgetCategoryDTOs = findMatching(primaryKeys);
        if(budgetCategoryDTOs != null && budgetCategoryDTOs.iterator().hasNext())
            return (ExternalizableBusinessObject) budgetCategoryDTOs.iterator().next();
        else
            return null;
    }

    public Collection findMatching(Map fieldValues) {
        List<HashMapElement> hashMapList = new ArrayList<HashMapElement>();
        List budgetCategoryDTOs = new ArrayList();
        
        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KcConstants.BudgetCategory.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val); 
                hashMapList.add(hashMapElement);
            }
        }
        if (hashMapList.size() == 0) hashMapList = null;
   
        try {
            budgetCategoryDTOs = this.getWebService().lookupBudgetCategories(hashMapList);     
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, KfsService.getWebServiceServerName());
        }
        return budgetCategoryDTOs;
    }
    
 }
