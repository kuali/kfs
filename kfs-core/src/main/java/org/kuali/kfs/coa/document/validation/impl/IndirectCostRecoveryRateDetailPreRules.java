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
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * PreRules checks for the {@link IndirectCostRecoveryRateDetail} that needs to occur while still in the Struts processing. This includes
 * defaults, confirmations, etc.
 */
public class IndirectCostRecoveryRateDetailPreRules extends MaintenancePreRulesBase {


    protected IndirectCostRecoveryRateDetail indirectCostRecoveryRateDetail;


    public IndirectCostRecoveryRateDetailPreRules() {

    }

    /**
     * Executes the following pre rules
     * <ul>
     * <li>{@link IndirectCostRecoveryRateDetailPreRules#setSubAccountToDashesIfBlank()}</li>
     * <li>{@link IndirectCostRecoveryRateDetailPreRules#setSubObjectToDashesIfBlank()}</li>
     * </ul>
     * 
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        setSubAccountToDashesIfBlank();
        setSubObjectToDashesIfBlank();

        return true;
    }

    /**
     * This method checks for continuation accounts and presents the user with a question regarding their use on this account.
     */
    protected void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(indirectCostRecoveryRateDetail.getAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", indirectCostRecoveryRateDetail.getChartOfAccountsCode(), indirectCostRecoveryRateDetail.getAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                indirectCostRecoveryRateDetail.setAccountNumber(account.getAccountNumber());
                indirectCostRecoveryRateDetail.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    /**
     * This sets the {@link SubAccount} number to padded dashes ("-") if blank
     */
    protected void setSubAccountToDashesIfBlank() {
        String newSubAccount = indirectCostRecoveryRateDetail.getSubAccountNumber();
        if (StringUtils.isBlank(newSubAccount)) {
            indirectCostRecoveryRateDetail.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
    }

    /**
     * This sets the {@link org.kuali.module.chart.bo.SubObjCd} code to padded dashes ("-") if blank
     */
    protected void setSubObjectToDashesIfBlank() {
        String newSubObject = indirectCostRecoveryRateDetail.getFinancialSubObjectCode();
        if (StringUtils.isBlank(newSubObject)) {
            indirectCostRecoveryRateDetail.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     */
    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        indirectCostRecoveryRateDetail = (IndirectCostRecoveryRateDetail) document.getNewMaintainableObject().getBusinessObject();
    }
}
