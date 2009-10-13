/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.bc.BCConstants.Report.BuildMode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReasonCodePick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSubFundPick;

/**
 * Defines methods that manipulate objects used by the Organization Selection screens. Manipulated objects include
 * BudgetConstructionPullup with methods that populate and depopulate the associated table for a specific user.
 */
public interface BudgetReportsControlListService {

    /**
     * Rebuilds the report control list for the given parameters.
     * 
     * @param principalId - current user who is running the report
     * @param universityFiscalYear - budget fiscal year
     * @param chartOfAccountsCode - point of view chart of accounts code
     * @param organizationCode - point of view organization code
     * @param buildMode - indicates whether the accounts should be restricted to GL pending budget, monthly budget, or bnc
     *        appointment funding
     */
    public void updateReportsControlList(String principalId, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode, BuildMode buildMode);

    /**
     * Rebuilds the subfunds available for selection based on the control list.
     * 
     * @param principalId - current user who is running the report
     */
    public void updateReportSubFundGroupSelectList(String principalId);

    /**
     * Rebuilds the object codes available for selection based on the control list.
     * 
     * @param principalId - current user who is running the report
     */
    public void updateReportObjectCodeSelectList(String principalId);

    /**
     * Rebuilds the reason codes available for selection based on the control list.
     * 
     * @param principalId - current user who is running the report
     */
    public void updateReportReasonCodeSelectList(String principalId);

    /**
     * Retrieves organizations selected by given user.
     * 
     * @param principalId - current user who is running the report
     * @return Collection<BudgetConstructionPullup> that are selected by the given user
     */
    public Collection<BudgetConstructionPullup> retrieveSelectedOrganziations(String principalId);

    /**
     * Retrieves sub funds that are available for selection by the given user.
     * 
     * @param principalId - current user who is running the report
     * @return Collection<BudgetConstructionSubFundPick> that are selected by the given user
     */
    public Collection<BudgetConstructionSubFundPick> retrieveSubFundList(String principalId);

    /**
     * Retrieves object codes that are available for selection by the given user.
     * 
     * @param principalId - current user who is running the report
     * @return Collection<BudgetConstructionObjectPick> that are selected by the given user
     */
    public Collection<BudgetConstructionObjectPick> retrieveObjectCodeList(String principalId);

    /**
     * Retrieves reason codes that are available for selection by the given user.
     * 
     * @param principalId - current user who is running the report
     * @return Collection<BudgetConstructionReasonCodePick> that are selected by the given user
     */
    public Collection<BudgetConstructionReasonCodePick> retrieveReasonCodeList(String principalId);
    
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

