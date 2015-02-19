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
package org.kuali.kfs.coa.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.DocumentHeader;

/**
 * Maintainable implementation for the chart maintenance document
 */
public class ChartMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Override to push chart manager id into KIM
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        if (documentHeader.getWorkflowDocument().isProcessed()) {
            Chart chart = (Chart) getBusinessObject();
            Person oldChartManager = SpringContext.getBean(ChartService.class).getChartManager(chart.getChartOfAccountsCode());

            // Only make the KIM calls if the chart manager was changed
            if ( oldChartManager == null || !StringUtils.equals(chart.getFinCoaManagerPrincipalId(), oldChartManager.getPrincipalId() ) ) {
                RoleService roleService = KimApiServiceLocator.getRoleService();

                Map<String,String> qualification = new HashMap<String,String>(1);
                qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chart.getChartOfAccountsCode());

                if (oldChartManager != null) {
                    roleService.removePrincipalFromRole(oldChartManager.getPrincipalId(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimApiConstants.CHART_MANAGER_KIM_ROLE_NAME, qualification);
                }

                if (StringUtils.isNotBlank(chart.getFinCoaManagerPrincipalId())) {
                    roleService.assignPrincipalToRole(chart.getFinCoaManagerPrincipalId(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimApiConstants.CHART_MANAGER_KIM_ROLE_NAME, qualification);
                }
            }
        }

        super.doRouteStatusChange(documentHeader);
    }

}
