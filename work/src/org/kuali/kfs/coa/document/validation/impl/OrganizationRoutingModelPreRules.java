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

import org.kuali.kfs.coa.businessobject.AccountDelegateModel;
import org.kuali.kfs.coa.businessobject.AccountDelegateModelDetail;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * 
 * This class...
 */
public class OrganizationRoutingModelPreRules extends MaintenancePreRulesBase {
    public OrganizationRoutingModelPreRules() {
    }

    /**
     * This performs pre rules checks 
     * <ul>
     * <li>{@link OrganizationRoutingModelPreRules#copyKeyAttributesToModelDetail(OrganizationRoutingModelName)}</li>
     * </ul>
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument maintDoc) {
        AccountDelegateModel model = (AccountDelegateModel) maintDoc.getNewMaintainableObject().getBusinessObject();
        copyKeyAttributesToModelDetail(model);
        return true;
    }

    /**
     * 
     * This copies the chart of accounts, object code, and organization model name from the parent {@link OrganizationRoutingModelName} to the 
     * {@link OrganizationRoutingModel} objects  
     * @param model
     */
    protected void copyKeyAttributesToModelDetail(AccountDelegateModel model) {
        for (AccountDelegateModelDetail modelDelegate : model.getAccountDelegateModelDetails()) {
            modelDelegate.setChartOfAccountsCode(model.getChartOfAccountsCode());
            modelDelegate.setOrganizationCode(model.getOrganizationCode());
            modelDelegate.setAccountDelegateModelName(model.getAccountDelegateModelName());
        }
    }

}
