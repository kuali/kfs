/*
 * Copyright 2009 The Kuali Foundation
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
