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
package org.kuali.module.budget.dao;

import java.util.List;

import org.kuali.module.budget.BCConstants.Report.BuildMode;
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionReasonCodePick;
import org.kuali.module.budget.bo.BudgetConstructionSubFundPick;

/**
 * Data access object for Budget Construction Organization Report.
 */
public interface BudgetReportsControlListDao {
    /**
     * Builds the report controls list for the given user, point of view, and build mode.
     * 
     * @param personUserIdentifier - person id to build data for
     * @param universityFiscalYear - budget fiscal year
     * @param chartOfAccountsCode - point of view chart of accounts code
     * @param organizationCode - point of view organization code
     * @param buildMode - indicates whether the accounts should be restricted to GL pending budget, monthly budget, or bnc
     *        appointment funding
     */
    public void updateReportControlList(String personUserIdentifier, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode, BuildMode buildMode);

    /**
     * Builds the sub-fund control list table for the given user.
     * 
     * @param personUserIdentifier - person id to build data for
     */
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier);

    /**
     * Builds the object code control list table for the given user.
     * 
     * @param personUserIdentifier - person id to build data for
     */
    public void updateReportsObjectCodeSelectList(String personUserIdentifier);

    /**
     * Builds the reason code control list table for the given user.
     * 
     * @param personUserIdentifier - person id to build data for
     */
    public void updateReportsReasonCodeSelectList(String personUserIdentifier);

    /**
     * Updates the select flag for each sub fund selection record.
     * 
     * @param subFundPickList - List containing the updated BudgetConstructionSubFundPick objects
     */
    public void updateSubFundSelectFlags(List<BudgetConstructionSubFundPick> subFundPickList);

    /**
     * Updates the select flag for each object code selection record.
     * 
     * @param objectCodePickList - List containing the updated BudgetConstructionObjectPick objects
     */
    public void updateObjectCodeSelectFlags(List<BudgetConstructionObjectPick> objectCodePickList);

    /**
     * Updates the select flag for each reason code selection record.
     * 
     * @param objectCodePickList - List containing the updated BudgetConstructionReasonCodePick objects
     */
    public void updateReasonCodeSelectFlags(List<BudgetConstructionReasonCodePick> reasonCodePickList);

}
