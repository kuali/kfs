/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.NonpersonnelCategory;
import org.kuali.module.kra.budget.bo.NonpersonnelObjectCode;
import org.kuali.module.kra.budget.service.BudgetNonpersonnelService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BudgetNonpersonnelServiceImpl implements BudgetNonpersonnelService {

    // set up logging
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetNonpersonnelServiceImpl.class);

    private BusinessObjectService businessObjectService;
 
    public void refreshNonpersonnelObjectCode(List nonpersonnelItems) {
        for (Iterator nonpersonnelItem = nonpersonnelItems.iterator(); nonpersonnelItem.hasNext();) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItem.next();
            budgetNonpersonnel.refreshReferenceObject("nonpersonnelObjectCode");
        }
    }

    public BudgetNonpersonnel findBudgetNonpersonnel(Integer budgetNonpersonnelSequenceNumber, List nonpersonnelItems) {
        BudgetNonpersonnel budgetNonpersonnel = null;

        for (Iterator nonpersonnelItemsIter = nonpersonnelItems.iterator(); nonpersonnelItemsIter.hasNext();) {
            budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItemsIter.next();

            if (budgetNonpersonnelSequenceNumber.equals(budgetNonpersonnel.getBudgetNonpersonnelSequenceNumber())) {
                break;
            }
        }

        return budgetNonpersonnel;
    }

    /**
     * Returns all nonpersonnel categories
     * 
     * @throws Exception
     */
    public List getAllNonpersonnelCategories() {
        Map fieldValuesNonpersonnelCategories = new HashMap();
        fieldValuesNonpersonnelCategories.put(KFSPropertyConstants.ACTIVE, true);
        List<NonpersonnelCategory> nonpersonnelCategories = 
            new ArrayList<NonpersonnelCategory>(businessObjectService.findMatchingOrderBy(NonpersonnelCategory.class, fieldValuesNonpersonnelCategories, KFSPropertyConstants.SORT_NUMBER, true));
        
        for(NonpersonnelCategory nonpersonnelCategory : nonpersonnelCategories) {
            Map fieldValuesNonpersonnelObjectCodes = new HashMap();
            fieldValuesNonpersonnelObjectCodes.put(KFSPropertyConstants.BUDGET_NONPERSONNEL_CATEGORY_CODE, nonpersonnelCategory.getCode());
            fieldValuesNonpersonnelObjectCodes.put(KFSPropertyConstants.ACTIVE, true);
            nonpersonnelCategory.setNonpersonnelObjectCodes(
                    new ArrayList(businessObjectService.findMatchingOrderBy(NonpersonnelObjectCode.class, fieldValuesNonpersonnelObjectCodes, KFSPropertyConstants.BUDGET_NONPERSONNEL_SUB_CATEGORY_CODE, true)));
        }
        
        return nonpersonnelCategories;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
