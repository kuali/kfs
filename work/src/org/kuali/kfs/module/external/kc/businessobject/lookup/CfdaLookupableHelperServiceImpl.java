/*
 * Copyright 2010 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.external.kc.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.service.BudgetCategoryLookupService;
import org.kuali.kfs.module.external.kc.service.CfdaService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;

public class CfdaLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    ThreadLocal<Person> currentUser = new ThreadLocal<Person>();

    /**
     * If the account is not closed or the user is an Administrator the "edit" link is added The "copy" link is added for Accounts
     * 
     * @returns links to edit and copy maintenance action for the current maintenance record.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.List)
     */
 

    /**
     * Overridden to allow EBO to use web service to lookup Budget Category.
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> parameters) {
        List cfdas = new ArrayList();
        try {
           List<HashMapElement> hashMapList = new ArrayList<HashMapElement>();
           // CfdaService cfdaService = (CfdaService) SpringContext.getService("cfdaService");
            //List <Cfda> cfdas cfdaService.getByPrimaryId(accountNumber);
            
            for (String key : parameters.keySet()) {
                String val = parameters.get(key);
                if ( BudgetCategoryLookupService.KC_BUDGETCAT_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                    HashMapElement hashMapElement = new HashMapElement();
                    hashMapElement.setKey(key);
                    hashMapElement.setValue(val); 
                    hashMapList.add(hashMapElement);
                }
            }

            //cfdas = cfdaService.lookupCfda(parameters);
           if (cfdas == null) return Collections.EMPTY_LIST;
           return cfdas;
            
        } catch (Exception ex) {
            LOG.error(ContractsAndGrantsConstants.BudgetAdjustmentService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND +  ex.getMessage()); 
            GlobalVariables.getMessageMap().putError("errors", "error.blank",ContractsAndGrantsConstants.KcWebService.ERROR_KC_WEB_SERVICE_FAILURE,  ex.getMessage());
        }
        
        return Collections.EMPTY_LIST;
    }

}
