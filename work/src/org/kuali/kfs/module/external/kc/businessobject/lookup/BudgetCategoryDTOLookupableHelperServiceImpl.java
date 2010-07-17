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
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.kfs.module.external.kc.dto.BudgetCategoryDTO;
import org.kuali.kfs.module.external.kc.service.BudgetCategoryService;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
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
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> parameters) {
     
        //TODO: budgetCategoryServiceSOAP should be up
        List<BudgetCategoryDTO> budgetCategoryList = new ArrayList<BudgetCategoryDTO>();
//        BudgetCategoryService budgetCategoryService = (BudgetCategoryService) GlobalResourceLoader.getService(new QName("KC", "budgetCategorytServiceSOAP"));
//        budgetCategoryList = budgetCategoryService.lookupBudgetCategories(parameters);
//        
//        return budgetCategoryList;
               
        // for test
        BudgetCategoryDTO bc = new BudgetCategoryDTO();
        bc.setBudgetCategoryCode("BA1");
        bc.setBudgetCategoryTypeCode("BATY1");
        budgetCategoryList.add(bc);
        
        return budgetCategoryList;
    }
}
