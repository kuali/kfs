/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public class AccountOrganizationHierarchyRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    public static final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    public static final String DESCEND_HIERARCHY_FALSE_VALUE = "N";

    protected List<String> roleQualifierRequiredAttributes = new ArrayList<String>();
    protected List<String> qualificationRequiredAttributes = new ArrayList<String>();
    {
        roleQualifierRequiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        roleQualifierRequiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);

        qualificationRequiredAttributes.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        qualificationRequiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        qualificationRequiredAttributes.add(KfsKimAttributes.ACCOUNT_NUMBER);
        qualificationRequiredAttributes.add(BCPropertyConstants.ORGANIZATION_LEVEL_CODE);
        qualificationRequiredAttributes.add(KfsKimAttributes.DESCEND_HIERARCHY);
    }

    private BudgetDocumentService budgetDocumentService;

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        validateRequiredAttributesAgainstReceived(qualificationRequiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(roleQualifierRequiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);
        // if no qualification is passed, then we have no basis to reject this
        // (if a null is let through, then we get an NPE below)
        if (qualification == null || qualification.isEmpty() || roleQualifier == null || roleQualifier.isEmpty()) {
            return true;
        }

        Integer universityFiscalYear = Integer.valueOf(qualification.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
        String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);
        Integer organizationLevelCode = Integer.parseInt(qualification.get(BCPropertyConstants.ORGANIZATION_LEVEL_CODE));

        String roleChartOfAccountsCode = roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String roleOrganizationCode = roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE);
        Integer roleOrganizationLevelCode = -1;

        List<BudgetConstructionAccountOrganizationHierarchy> accountOrganizationHierarchy = (List<BudgetConstructionAccountOrganizationHierarchy>) budgetDocumentService.retrieveOrBuildAccountOrganizationHierarchy(universityFiscalYear, chartOfAccountsCode, accountNumber);
        for (BudgetConstructionAccountOrganizationHierarchy accountOrganization : accountOrganizationHierarchy) {
            if (accountOrganization.getOrganizationChartOfAccountsCode().equals(roleChartOfAccountsCode) && accountOrganization.getOrganizationCode().equals(roleOrganizationCode)) {
                roleOrganizationLevelCode = accountOrganization.getOrganizationLevelCode();
            }
        }

        String descendHierarchy = qualification.get(KfsKimAttributes.DESCEND_HIERARCHY);
        if (organizationLevelCode == 0 && DESCEND_HIERARCHY_FALSE_VALUE.equals(descendHierarchy)) {
            return false;
        }
        else if (DESCEND_HIERARCHY_TRUE_VALUE.equals(descendHierarchy)) {
            return roleOrganizationLevelCode >= organizationLevelCode;
        }
        else {
            return roleOrganizationLevelCode == organizationLevelCode;
        }
    }

    /**
     * Sets the budgetDocumentService attribute value.
     * 
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

}
