/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.rules.budget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.module.kra.budget.KraKeyConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.util.AuditCluster;
import org.kuali.module.kra.budget.util.AuditError;
import org.kuali.module.kra.budget.web.struts.form.BudgetCostShareFormHelper;

public class BudgetCostShareRule {

    /**
     * Error checks for Cost Share.
     * @param budget
     * @return
     */
    protected boolean isCostShareValid(Budget budget) {
        boolean valid = true;

        for (Iterator universityCostShareIter = budget.getUniversityCostShareItems().iterator(); universityCostShareIter.hasNext();) {
            BudgetUniversityCostShare budgetCostShare = (BudgetUniversityCostShare) universityCostShareIter.next();

            if (!StringUtils.isNotBlank(budgetCostShare.getChartOfAccountsCode()) && !StringUtils.isNotBlank(budgetCostShare.getOrganizationCode())) {
                GlobalVariables.getErrorMap().putError("budget.universityCostShareItem.chartOrg.required", KeyConstants.ERROR_REQUIRED, "Institution Direct Source");
                valid = false;
            }
        }

        return valid;
    }
    
    /**
     * Audit checks for Cost Share.
     * @param budget
     * @return
     */
    protected boolean runCostShareAuditErrors(Budget budget) {
        List<AuditError> costShareAuditErrors = new ArrayList<AuditError>();
        BudgetCostShareFormHelper budgetCostShareFormHelper = new BudgetCostShareFormHelper(budget.getPeriods(), budget.getPersonnel(), budget.getNonpersonnelItems(), budget.getUniversityCostSharePersonnelItems(), budget.getUniversityCostShareItems(), budget.getThirdPartyCostShareItems());

        if (!budgetCostShareFormHelper.getInstitutionDirect().getTotalBalanceToBeDistributed().equals(new KualiInteger(0))) {
            costShareAuditErrors.add(new AuditError("document.budget.audit.costShare.institution.distributed",
            KraKeyConstants.AUDIT_COST_SHARE_INSTITUTION_DISTRIBUTED, "costshare"));
        }
        if (!budgetCostShareFormHelper.getThirdPartyDirect().getTotalBalanceToBeDistributed().equals(new KualiInteger(0))) {
            costShareAuditErrors.add(new AuditError("document.budget.audit.costShare.3rdParty.distributed",
            KraKeyConstants.AUDIT_COST_SHARE_3P_DISTRIBUTED, "costshare"));
        }
        
        if (!costShareAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("costShareAuditErrors", new AuditCluster("Cost Share", costShareAuditErrors));
            return false;
        }
        
        return true;
    }
}
