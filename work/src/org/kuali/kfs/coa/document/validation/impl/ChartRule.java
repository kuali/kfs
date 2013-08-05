/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * Business rule(s) applicable to {@link ChartMaintenance} documents.
 */
public class ChartRule extends MaintenanceDocumentRuleBase {

    /**
     * This method calls specific rules for routing on Chart Maintenance documents Specifically it checks to make sure that
     * reportsToChart exists if it is not the same code as the newly created Chart and it checks to make sure that the chart manager
     * is valid for the Chart Module
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     * @return false if reports to chart code doesn't exist or user is invalid for this module
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean result = true;

        Chart chart = (Chart) document.getNewMaintainableObject().getBusinessObject();
        ChartService chartService = SpringContext.getBean(ChartService.class);
        IdentityService idService = KimApiServiceLocator.getIdentityService();

        String chartCode = chart.getChartOfAccountsCode();

        String reportsToChartCode = chart.getReportsToChartOfAccountsCode();

        if (chartCode != null && !chartCode.equals(reportsToChartCode)) { // if not equal to this newly created chart, then must
                                                                            // exist
            Chart reportsToChart = chartService.getByPrimaryId(reportsToChartCode);
            if (reportsToChart == null) {
                result = false;
                putFieldError("reportsToChartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_CHART_REPORTS_TO_CHART_MUST_EXIST);
            }
        }
        Entity chartManager = idService.getEntityByPrincipalId(chart.getFinCoaManagerPrincipalId());
        if ( chartManager == null ) {
            result = false;
            putFieldError("finCoaManagerUniversal.principalName", KFSKeyConstants.ERROR_DOCUMENT_CHART_MANAGER_MUST_EXIST);
        }

        if (chartManager != null && !SpringContext.getBean(FinancialSystemUserService.class).isActiveFinancialSystemUser(chart.getFinCoaManagerPrincipalId())) {
            result = false;
            putFieldError("finCoaManagerUniversal.principalName", KFSKeyConstants.ERROR_DOCUMENT_CHART_MANAGER_MUST_BE_KUALI_USER);
        }


        return result;

    }

}

