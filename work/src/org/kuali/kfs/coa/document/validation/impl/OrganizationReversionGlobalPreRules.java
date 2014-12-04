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
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalDetail;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobalOrganization;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 * PreRules checks for the {@link OrganizationReversionGlobal} that needs to occur while still in the Struts processing. This includes defaults
 */
public class OrganizationReversionGlobalPreRules extends MaintenancePreRulesBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionGlobalPreRules.class);

    /**
     * This is the hook method for the {@link MaintenancePreRulesBase} to call. It calls
     * <ul>
     * <li>{@link OrganizationReversionGlobalPreRules#checkForContinuationAccounts(OrganizationReversionGlobal)}</li>
     * <li>{@link OrganizationReversionGlobalPreRules#copyKeyAttributesToCollections(OrganizationReversionGlobal)}</li>
     * </ul>
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public boolean doCustomPreRules(MaintenanceDocument maintenanceDocument) {
        OrganizationReversionGlobal globalOrgReversion = (OrganizationReversionGlobal) maintenanceDocument.getNewMaintainableObject().getBusinessObject();
        checkForContinuationAccounts(globalOrgReversion);
        copyKeyAttributesToCollections(globalOrgReversion);
        return true;
    }

    /**
     * This method checks to see if the budget reversion or cash reversion accounts have continuation accounts.
     * 
     * @param globalOrgReversion Global Organization Reversion to check.
     */
    public void checkForContinuationAccounts(OrganizationReversionGlobal globalOrgReversion) {
        if (!StringUtils.isBlank(globalOrgReversion.getBudgetReversionChartOfAccountsCode()) && !StringUtils.isBlank(globalOrgReversion.getBudgetReversionAccountNumber())) {
            Account account = checkForContinuationAccount("Budget Reversion Account Number", globalOrgReversion.getBudgetReversionChartOfAccountsCode(), globalOrgReversion.getBudgetReversionAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) {
                globalOrgReversion.setBudgetReversionChartOfAccountsCode(account.getChartOfAccountsCode());
                globalOrgReversion.setBudgetReversionAccountNumber(account.getAccountNumber());
            }
        }
        if (!StringUtils.isBlank(globalOrgReversion.getCashReversionFinancialChartOfAccountsCode()) && !StringUtils.isBlank(globalOrgReversion.getBudgetReversionAccountNumber())) {
            Account account = checkForContinuationAccount("Cash Reversion Account Number", globalOrgReversion.getCashReversionFinancialChartOfAccountsCode(), globalOrgReversion.getCashReversionAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) {
                globalOrgReversion.setCashReversionFinancialChartOfAccountsCode(account.getChartOfAccountsCode());
                globalOrgReversion.setCashReversionAccountNumber(account.getAccountNumber());
            }
        }
    }

    /**
     * This method updates all children of a Global Organization Reversion so that they all are associated with the Global
     * Organization Reversion document, by upudating their primary keys.
     * 
     * @param globalOrgRev the global organization reversion document to update.
     */
    public void copyKeyAttributesToCollections(OrganizationReversionGlobal globalOrgRev) {
        for (OrganizationReversionGlobalDetail orgRevDetail : globalOrgRev.getOrganizationReversionGlobalDetails()) {
            orgRevDetail.setDocumentNumber(globalOrgRev.getDocumentNumber());
        }

        for (OrganizationReversionGlobalOrganization orgRevOrg : globalOrgRev.getOrganizationReversionGlobalOrganizations()) {
            orgRevOrg.setDocumentNumber(globalOrgRev.getDocumentNumber());
        }
    }

}
