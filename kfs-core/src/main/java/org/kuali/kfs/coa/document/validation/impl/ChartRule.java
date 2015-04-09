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

