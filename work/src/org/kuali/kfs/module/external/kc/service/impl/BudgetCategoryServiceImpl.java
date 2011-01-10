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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.kfs.integration.kc.businessobject.BudgetCategoryDTO;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.service.BudgetCategoryService;
import org.kuali.kfs.module.external.kc.service.impl.InstitutionalBudgetCategoryServiceImpl.InstitutionalBudgetCategoryService;
import org.kuali.kfs.module.external.kc.service.impl.InstitutionalBudgetCategoryServiceImpl.InstitutionalBudgetCategorySoapService;

public class BudgetCategoryServiceImpl implements BudgetCategoryService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetCategoryServiceImpl.class);

    private String wsdlLocation;
    private URL wsdlURL;
    private static final QName SERVICE_NAME = new QName("KC", "budgetCategorySoapService");   
    
    public List <BudgetCategoryDTO> lookupBudgetCategories(Map <String,String>searchCriteria) {
        java.util.List <HashMapElement> hashMapList = new ArrayList<HashMapElement>();

        for (String key : searchCriteria.keySet()) {
            String val = searchCriteria.get(key);
  //          if ( BudgetCategoryService.KC_BA_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
         if  (val.length() > 0) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val); 
                hashMapList.add(hashMapElement);
            }
        }
        InstitutionalBudgetCategorySoapService ss = new InstitutionalBudgetCategorySoapService(wsdlURL, SERVICE_NAME);
        InstitutionalBudgetCategoryService port = ss.getBudgetCategoryServicePort();  
        
        System.out.println("Invoking lookupBudgetCategories...");
          List budgetCategoryDTOs = port.lookupBudgetCategories(hashMapList);     
          return budgetCategoryDTOs;
    }
    
    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
        try {
            wsdlURL = new URL(wsdlLocation);
        }
        catch (MalformedURLException ex) {
          
            LOG.error(KcConstants.BudgetAdjustmentService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND +  ex.getMessage()); 
            //ex.printStackTrace();
        }

    }
}
