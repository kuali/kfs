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
package org.kuali.kfs.module.bc.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

public class AccountOrganizationHierarchyRoleTypeServiceImpl extends RoleTypeServiceBase {
    public static final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    public static final String DESCEND_HIERARCHY_FALSE_VALUE = "N";







    protected BudgetDocumentService budgetDocumentService;

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        // if no qualification given but the user is assigned an organization then return they have the role
        if ((qualification == null || qualification.isEmpty()) && roleQualifier != null && !roleQualifier.isEmpty()) {
            return true;
        }

        // if they don't have a qualification then return false for the role
        if (qualification == null || qualification.isEmpty() || roleQualifier == null || roleQualifier.isEmpty()) {
            return false;
        }

        String orgChartOfAccountsCode = qualification.get(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        String organizationCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
        String roleChartOfAccountsCode = roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String roleOrganizationCode = roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE);

        String descendHierarchy = DESCEND_HIERARCHY_FALSE_VALUE;
        if (qualification.containsKey(KfsKimAttributes.DESCEND_HIERARCHY)) {
            descendHierarchy = qualification.get(KfsKimAttributes.DESCEND_HIERARCHY);
        }

        if (DESCEND_HIERARCHY_TRUE_VALUE.equals(descendHierarchy)) {
            Integer universityFiscalYear = Integer.valueOf(qualification.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
            String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);
            Integer organizationLevelCode = Integer.parseInt(qualification.get(BCPropertyConstants.ORGANIZATION_LEVEL_CODE));
            String accountReportExists = qualification.get(BCPropertyConstants.ACCOUNT_REPORTS_EXIST);

            // if account report mapping does not exist, want to give special view access
            if (Boolean.FALSE.toString().equals(accountReportExists)) {
                return true;
            }

            Integer roleOrganizationLevelCode = -1;
            List<BudgetConstructionAccountOrganizationHierarchy> accountOrganizationHierarchy = (List<BudgetConstructionAccountOrganizationHierarchy>) budgetDocumentService.retrieveOrBuildAccountOrganizationHierarchy(universityFiscalYear, chartOfAccountsCode, accountNumber);
            for (BudgetConstructionAccountOrganizationHierarchy accountOrganization : accountOrganizationHierarchy) {
                if (accountOrganization.getOrganizationChartOfAccountsCode().equals(roleChartOfAccountsCode) && accountOrganization.getOrganizationCode().equals(roleOrganizationCode)) {
                    roleOrganizationLevelCode = accountOrganization.getOrganizationLevelCode();
                }
            }

            if (roleOrganizationLevelCode == -1) {
                return false;
            }

            return roleOrganizationLevelCode.intValue() >= organizationLevelCode.intValue();
        }

        return roleChartOfAccountsCode.equals(orgChartOfAccountsCode) && roleOrganizationCode.equals(organizationCode);
    }

    /**
     * Sets the budgetDocumentService attribute value.
     *
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public List<KimAttributeField> getAttributeDefinitions(String kimTypId) {
        List<KimAttributeField> fields = super.getAttributeDefinitions(kimTypId);
        List<KimAttributeField> updatedFields = new ArrayList<KimAttributeField>( fields.size() );

        for (KimAttributeField definition : fields ) {
            if (KFSPropertyConstants.ORGANIZATION_CODE.equals(definition.getAttributeField().getName())) {
                KimAttributeField.Builder updatedField = KimAttributeField.Builder.create(definition);
                updatedField.getAttributeField().setRequired(false);
                updatedFields.add(updatedField.build());
            } else {
                updatedFields.add(definition);
            }
        }

        return fields;
    }
}
