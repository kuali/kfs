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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.webService.BudgetCategory.InstitutionalBudgetCategoryService;
import org.kuali.kfs.module.external.kc.webService.BudgetCategory.InstitutionalBudgetCategorySoapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

public class BudgetCategoryServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetCategoryServiceImpl.class);

    private String wsdlLocation;
    private URL wsdlURL = InstitutionalBudgetCategorySoapService.WSDL_LOCATION;
    private static final QName SERVICE_NAME = new QName(KFSConstants.Research.KC_NAMESPACE_URI, KFSConstants.Research.KC_BUDGET_CATEGORY_SERVICE);   
    public static final List <String> KC_BUDGETCAT_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("budgetCategoryTypeCode","description","budgetCategoryCode");
    
    
    //@Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Collection budgetCategoryDTOs = findMatching(primaryKeys);
        if(budgetCategoryDTOs != null && budgetCategoryDTOs.iterator().hasNext())
            return (ExternalizableBusinessObject) budgetCategoryDTOs.iterator().next();
        else
            return null;
    }

    //@Override
    public Collection findMatching(Map fieldValues) {
        List<HashMapElement> hashMapList = new ArrayList<HashMapElement>();
        
        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KC_BUDGETCAT_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val); 
                hashMapList.add(hashMapElement);
            }
        }
        if (hashMapList.size() == 0) hashMapList = null;
   
        InstitutionalBudgetCategorySoapService ss = new InstitutionalBudgetCategorySoapService(wsdlURL, SERVICE_NAME);
        InstitutionalBudgetCategoryService port = ss.getBudgetCategoryServicePort();  
   
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
          
            LOG.error(ContractsAndGrantsConstants.BudgetAdjustmentService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND +  ex.getMessage()); 
            //ex.printStackTrace();
        }
    }
}
