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

import java.util.List;

import org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.PassThruRoleTypeServiceBase;

public class DocumentDerivedRoleTypeServiceImpl extends PassThruRoleTypeServiceBase {
    private static final String DOCUMENT_VIEWER_ROLE_NAME = "Document Viewer";
    private static final String DOCUMENT_EDITOR_ROLE_NAME = "Document Editor";
    private static final String BC_PROCESSOR_ROLE_NAME = "Processor";

    @Override
    public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, AttributeSet qualification) {
        AttributeSet newQualification = new AttributeSet();

        String universityFiscalYear = qualification.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);
        String orgChartOfAccountsCode = qualification.get(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        String organizationCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
        String accountReportExists = qualification.get(BCPropertyConstants.ACCOUNT_REPORTS_EXIST);
        
        Integer organizationLevelCode = Integer.parseInt(qualification.get(BCPropertyConstants.ORGANIZATION_LEVEL_CODE));
        if (BC_PROCESSOR_ROLE_NAME.equals(memberRoleName)) {
            if (DOCUMENT_EDITOR_ROLE_NAME.equals(roleName) && organizationLevelCode.intValue() == 0) {
                organizationCode = UNMATCHABLE_QUALIFICATION; 
            }
        }
        else {
            if (organizationLevelCode.intValue() != 0) {
                accountNumber = UNMATCHABLE_QUALIFICATION; 
            }        
        }

        String descendHierarchy = OrganizationOptionalHierarchyRoleTypeServiceImpl.DESCEND_HIERARCHY_FALSE_VALUE;
        if (DOCUMENT_VIEWER_ROLE_NAME.equals(roleName)) {
            descendHierarchy = OrganizationOptionalHierarchyRoleTypeServiceImpl.DESCEND_HIERARCHY_TRUE_VALUE;
            
            newQualification.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
            newQualification.put(BCPropertyConstants.ORGANIZATION_LEVEL_CODE, organizationLevelCode.toString());
        }

        newQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        newQualification.put(KfsKimAttributes.ACCOUNT_NUMBER, accountNumber);
        newQualification.put(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE, orgChartOfAccountsCode);
        newQualification.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
        newQualification.put(KfsKimAttributes.DESCEND_HIERARCHY, descendHierarchy);
        newQualification.put(BCPropertyConstants.ACCOUNT_REPORTS_EXIST, accountReportExists);

        return newQualification;
    }
}