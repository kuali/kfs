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
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;

public class OrganizationRoutingModelPreRules extends MaintenancePreRulesBase {
    public OrganizationRoutingModelPreRules() {}
    
    protected boolean doCustomPreRules(MaintenanceDocument maintDoc) {
        OrganizationRoutingModelName model = (OrganizationRoutingModelName)maintDoc.getNewMaintainableObject().getBusinessObject();
        copyKeyAttributesToModelDetail(model);
        return true;
    }
    
    protected void copyKeyAttributesToModelDetail(OrganizationRoutingModelName model) {
        for (OrganizationRoutingModel modelDelegate: model.getOrganizationRoutingModel()) {
            modelDelegate.setChartOfAccountsCode(model.getChartOfAccountsCode());
            modelDelegate.setOrganizationCode(model.getOrganizationCode());
            modelDelegate.setOrganizationRoutingModelName(model.getOrganizationRoutingModelName());
        }
    }
    
}
