/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.rules.budget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetInstitutionCostShare;
import org.kuali.module.kra.budget.web.struts.form.BudgetCostShareFormHelper;
import org.kuali.module.kra.util.AuditCluster;
import org.kuali.module.kra.util.AuditError;

public class BudgetCostShareRule {

    /**
     * Error checks for Cost Share.
     * 
     * @param budget
     * @return
     */
    protected boolean isCostShareValid(Budget budget) {
        boolean valid = true;

        for (Iterator institutionCostShareIter = budget.getInstitutionCostShareItems().iterator(); institutionCostShareIter.hasNext();) {
            BudgetInstitutionCostShare budgetCostShare = (BudgetInstitutionCostShare) institutionCostShareIter.next();

            if (!StringUtils.isNotBlank(budgetCostShare.getChartOfAccountsCode()) && !StringUtils.isNotBlank(budgetCostShare.getOrganizationCode())) {
                GlobalVariables.getErrorMap().putError("budget.institutionCostShareItem.chartOrg.required", KFSKeyConstants.ERROR_REQUIRED, "Institution Direct Source");
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Audit checks for Cost Share.
     * 
     * @param budget
     * @return
     */
    protected boolean runCostShareAuditErrors(Budget budget) {
        List<AuditError> costShareAuditErrors = new ArrayList<AuditError>();
        BudgetCostShareFormHelper budgetCostShareFormHelper = new BudgetCostShareFormHelper(budget.getPeriods(), budget.getPersonnel(), budget.getNonpersonnelItems(), budget.getInstitutionCostSharePersonnelItems(), budget.getInstitutionCostShareItems(), budget.getThirdPartyCostShareItems());

        if (!budgetCostShareFormHelper.getInstitutionDirect().getTotalBalanceToBeDistributed().equals(KualiInteger.ZERO)) {
            costShareAuditErrors.add(new AuditError("document.budget.audit.costShare.institution.distributed", KraKeyConstants.AUDIT_COST_SHARE_INSTITUTION_DISTRIBUTED, "costshare"));
        }
        if (!budgetCostShareFormHelper.getThirdPartyDirect().getTotalBalanceToBeDistributed().equals(KualiInteger.ZERO)) {
            costShareAuditErrors.add(new AuditError("document.budget.audit.costShare.3rdParty.distributed", KraKeyConstants.AUDIT_COST_SHARE_3P_DISTRIBUTED, "costshare"));
        }

        if (!costShareAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("costShareAuditErrors", new AuditCluster("Cost Share", costShareAuditErrors));
            return false;
        }

        return true;
    }
}
