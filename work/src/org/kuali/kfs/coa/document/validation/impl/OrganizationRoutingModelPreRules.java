/*
 * Copyright 2007 The Kuali Foundation
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
