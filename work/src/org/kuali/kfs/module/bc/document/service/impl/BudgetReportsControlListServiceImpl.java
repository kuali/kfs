/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BudgetPropertyConstants;
import org.kuali.module.budget.BCConstants.Report.BuildMode;
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.bo.BudgetConstructionReasonCodePick;
import org.kuali.module.budget.bo.BudgetConstructionSubFundPick;
import org.kuali.module.budget.dao.BudgetReportsControlListDao;
import org.kuali.module.budget.service.BudgetReportsControlListService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.module.budget.service.BudgetReportsControlListService
 */
@Transactional
public class BudgetReportsControlListServiceImpl implements BudgetReportsControlListService {
    BudgetReportsControlListDao budgetReportsControlListDao;
    BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReportsControlList(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, org.kuali.module.budget.BCConstants.Report.BuildMode)
     */
    public void updateReportsControlList(String personUserIdentifier, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode, BuildMode buildMode) {
        budgetReportsControlListDao.updateReportControlList(personUserIdentifier, universityFiscalYear, chartOfAccountsCode, organizationCode, buildMode);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReportsSubFundGroupSelectList(java.lang.String)
     */
    public void updateReportSubFundGroupSelectList(String personUserIdentifier) {
        budgetReportsControlListDao.updateReportsSubFundGroupSelectList(personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReportObjectCodeSelectList(java.lang.String)
     */
    public void updateReportObjectCodeSelectList(String personUserIdentifier) {
        budgetReportsControlListDao.updateReportsObjectCodeSelectList(personUserIdentifier);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReportReasonCodeSelectList(java.lang.String)
     */
    public void updateReportReasonCodeSelectList(String personUserIdentifier) {
        budgetReportsControlListDao.updateReportsReasonCodeSelectList(personUserIdentifier);
    }

    /**
     * Sets the budgetReportsControlListDao
     * 
     * @param budgetReportsControlListDao The budgetReportsControlListDao to set.
     */
    public void setBudgetReportsControlListDao(BudgetReportsControlListDao budgetReportsControlListDao) {
        this.budgetReportsControlListDao = budgetReportsControlListDao;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#retrieveSelectedOrganziations(java.lang.String)
     */
    public Collection<BudgetConstructionPullup> retrieveSelectedOrganziations(String personUserIdentifier) {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.KUALI_USER_PERSON_USER_IDENTIFIER, personUserIdentifier);
        criteria.put(BudgetPropertyConstants.PULL_FLAG, new Integer(1));

        return businessObjectService.findMatching(BudgetConstructionPullup.class, criteria);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#retrieveSubFundList(java.lang.String)
     */
    public Collection<BudgetConstructionSubFundPick> retrieveSubFundList(String personUniversalIdentifier) {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUniversalIdentifier);

        return businessObjectService.findMatching(BudgetConstructionSubFundPick.class, criteria);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#retrieveObjectCodeList(java.lang.String)
     */
    public Collection<BudgetConstructionObjectPick> retrieveObjectCodeList(String personUniversalIdentifier) {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUniversalIdentifier);

        return businessObjectService.findMatching(BudgetConstructionObjectPick.class, criteria);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#retrieveReasonCodeList(java.lang.String)
     */
    public Collection<BudgetConstructionReasonCodePick> retrieveReasonCodeList(String personUniversalIdentifier) {
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUniversalIdentifier);

        return businessObjectService.findMatching(BudgetConstructionReasonCodePick.class, criteria);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateObjectCodeSelectFlags(java.util.List)
     */
    public void updateObjectCodeSelectFlags(List<BudgetConstructionObjectPick> objectCodePickList) {
        budgetReportsControlListDao.updateObjectCodeSelectFlags(objectCodePickList);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateReasonCodeSelectFlags(java.util.List)
     */
    public void updateReasonCodeSelectFlags(List<BudgetConstructionReasonCodePick> reasonCodePickList) {
        budgetReportsControlListDao.updateReasonCodeSelectFlags(reasonCodePickList);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateSubFundSelectFlags(java.util.List)
     */
    public void updateSubFundSelectFlags(List<BudgetConstructionSubFundPick> subFundPickList) {
        budgetReportsControlListDao.updateSubFundSelectFlags(subFundPickList);
    }
}
