/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.bc.document.dataaccess;

import java.util.List;

import org.kuali.kfs.module.bc.BCConstants.Report.BuildMode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionReasonCodePick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSubFundPick;

/**
 * Data access object for Budget Construction Organization Report.
 */
public interface BudgetReportsControlListDao {
    /**
     * Builds the report controls list for the given user, point of view, and build mode.
     * 
     * @param principalName - person id to build data for
     * @param universityFiscalYear - budget fiscal year
     * @param chartOfAccountsCode - point of view chart of accounts code
     * @param organizationCode - point of view organization code
     * @param buildMode - indicates whether the accounts should be restricted to GL pending budget, monthly budget, or bnc
     *        appointment funding
     */
    public void updateReportControlList(String principalName, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode, BuildMode buildMode);

    /**
     * Builds the sub-fund control list table for the given user.
     * 
     * @param principalName - person id to build data for
     */
    public void updateReportsSubFundGroupSelectList(String principalName);

    /**
     * Builds the object code control list table for the given user.
     * 
     * @param principalName - person id to build data for
     */
    public void updateReportsObjectCodeSelectList(String principalName);

    /**
     * Builds the reason code control list table for the given user.
     * 
     * @param principalName - person id to build data for
     */
    public void updateReportsReasonCodeSelectList(String principalName);

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

