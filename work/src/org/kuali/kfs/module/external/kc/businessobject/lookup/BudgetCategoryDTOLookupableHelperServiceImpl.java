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

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;

public class BudgetCategoryDTOLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

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
/*    
    public List<? extends BusinessObject> getSearchResults(Map<String, String> parameters) {
        List budgetCategories = new ArrayList();
        try {
            BudgetCategoryLookupService budgetCategoryService = SpringContext.getBean(BudgetCategoryLookupService.class);
            budgetCategories = budgetCategoryService.lookupBudgetCategory(parameters);
           if (budgetCategories == null) return Collections.EMPTY_LIST;
           return budgetCategories;
            
        } catch (Exception ex) {
            LOG.error(ContractsAndGrantsConstants.BudgetAdjustmentService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND +  ex.getMessage()); 
            GlobalVariables.getMessageMap().putError("errors", "error.blank",ContractsAndGrantsConstants.KcWebService.ERROR_KC_WEB_SERVICE_FAILURE,  ex.getMessage());
        }
        
        return Collections.EMPTY_LIST;
    }
*/
}
