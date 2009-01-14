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

import org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.PassThruRoleTypeServiceBase;

public class DocumentDerivedRoleTypeServiceImpl extends PassThruRoleTypeServiceBase {
    private static final String DOCUMENT_VIEWER_ROLE_NAME = "Document Viewer";
    private static final String DOCUMENT_EDITOR_ROLE_NAME = "Document Editor";
    private static final String BC_PROCESSOR_ROLE_NAME = "Processor";
    
    private BudgetConstructionOrganizationReportsService organizationService;

    @Override
    public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, AttributeSet qualification) {
        AttributeSet newQualification = new AttributeSet();

        int organizationLevelCode = Integer.parseInt(qualification.get(BCPropertyConstants.ORGANIZATION_LEVEL_CODE));

        if (BC_PROCESSOR_ROLE_NAME.equals(memberRoleName)) {
            String chartOfAccountsCode = qualification.get(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
            String organizationCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
            String descendHierarchy = OrganizationOptionalHierarchyRoleTypeServiceImpl.DESCEND_HIERARCHY_FALSE_VALUE;

            if (DOCUMENT_VIEWER_ROLE_NAME.equals(roleName)) {
                descendHierarchy = OrganizationOptionalHierarchyRoleTypeServiceImpl.DESCEND_HIERARCHY_TRUE_VALUE;
            }

            if (organizationLevelCode == 0) {
                if (DOCUMENT_EDITOR_ROLE_NAME.equals(roleName)) {
                    organizationCode = UNMATCHABLE_QUALIFICATION;
                }
            }
            else {
                for (int i = 2; i <= organizationLevelCode; i++) {
                    BudgetConstructionOrganizationReports newOrganization = organizationService.getByPrimaryId(chartOfAccountsCode, organizationCode);
                    chartOfAccountsCode = newOrganization.getReportsToChartOfAccountsCode();
                    organizationCode = newOrganization.getReportsToOrganizationCode();
                }
            }

            newQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            newQualification.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
            newQualification.put(KfsKimAttributes.DESCEND_HIERARCHY, descendHierarchy);

        }
        else {
            String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);

            if (organizationLevelCode != 0) {
                accountNumber = UNMATCHABLE_QUALIFICATION;
            }

            newQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            newQualification.put(KfsKimAttributes.ACCOUNT_NUMBER, accountNumber);
        }

        return newQualification;
    }

    public void setOrganizationService(BudgetConstructionOrganizationReportsService organizationService) {
        this.organizationService = organizationService;
    }
}