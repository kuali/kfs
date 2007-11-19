/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.module.chart.bo.OrganizationRoutingModel;
import org.kuali.module.chart.bo.OrganizationRoutingModelName;

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
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean doCustomPreRules(MaintenanceDocument maintDoc) {
        OrganizationRoutingModelName model = (OrganizationRoutingModelName) maintDoc.getNewMaintainableObject().getBusinessObject();
        copyKeyAttributesToModelDetail(model);
        return true;
    }

    /**
     * 
     * This copies the chart of accounts, object code, and organization model name from the parent {@link OrganizationRoutingModelName} to the 
     * {@link OrganizationRoutingModel} objects  
     * @param model
     */
    protected void copyKeyAttributesToModelDetail(OrganizationRoutingModelName model) {
        for (OrganizationRoutingModel modelDelegate : model.getOrganizationRoutingModel()) {
            modelDelegate.setChartOfAccountsCode(model.getChartOfAccountsCode());
            modelDelegate.setOrganizationCode(model.getOrganizationCode());
            modelDelegate.setOrganizationRoutingModelName(model.getOrganizationRoutingModelName());
        }
    }

}
