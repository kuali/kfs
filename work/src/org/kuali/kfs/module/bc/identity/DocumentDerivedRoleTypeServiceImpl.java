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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.PassThruRoleTypeServiceBase;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.MessageMap;

public class DocumentDerivedRoleTypeServiceImpl extends PassThruRoleTypeServiceBase implements BudgetConstructionNoAccessMessageSetting {
    protected BudgetConstructionProcessorService budgetConstructionProcessorService;
    protected BudgetDocumentService budgetDocumentService;
    
    @Override
    public Map<String,String> convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String,String> qualification) {
        Map<String,String> newQualification = new HashMap<String,String>();

        if(qualification!=null && !qualification.isEmpty()){
            String universityFiscalYear = qualification.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            String chartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String accountNumber = qualification.get(KfsKimAttributes.ACCOUNT_NUMBER);
            String orgChartOfAccountsCode = qualification.get(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
            String organizationCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
            String accountReportExists = qualification.get(BCPropertyConstants.ACCOUNT_REPORTS_EXIST);
    
            Integer organizationLevelCode = qualification.get(BCPropertyConstants.ORGANIZATION_LEVEL_CODE)!=null?Integer.parseInt(qualification.get(BCPropertyConstants.ORGANIZATION_LEVEL_CODE)):null;
            if (BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME.equals(memberRoleName)) {
                if (BCConstants.KimApiConstants.DOCUMENT_EDITOR_ROLE_NAME.equals(roleName) && (organizationLevelCode!=null && organizationLevelCode.intValue() == 0)) {
                    organizationCode = UNMATCHABLE_QUALIFICATION;
                }
            }
            else {
                if (organizationLevelCode!=null && organizationLevelCode.intValue() != 0) {
                    if (BCConstants.KimApiConstants.DOCUMENT_EDITOR_ROLE_NAME.equals(roleName) || Boolean.TRUE.toString().equals(accountReportExists)) {
                        accountNumber = UNMATCHABLE_QUALIFICATION;
                    }
                }
            }
    
            String descendHierarchy = OrganizationOptionalHierarchyRoleTypeServiceImpl.DESCEND_HIERARCHY_FALSE_VALUE;
            if (BCConstants.KimApiConstants.DOCUMENT_VIEWER_ROLE_NAME.equals(roleName)) {
                descendHierarchy = OrganizationOptionalHierarchyRoleTypeServiceImpl.DESCEND_HIERARCHY_TRUE_VALUE;
    
                newQualification.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
                newQualification.put(BCPropertyConstants.ORGANIZATION_LEVEL_CODE, organizationLevelCode!=null?organizationLevelCode.toString():null);
            }
    
            newQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
            newQualification.put(KfsKimAttributes.ACCOUNT_NUMBER, accountNumber);
            newQualification.put(BCPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE, orgChartOfAccountsCode);
            newQualification.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
            newQualification.put(KfsKimAttributes.DESCEND_HIERARCHY, descendHierarchy);
            newQualification.put(BCPropertyConstants.ACCOUNT_REPORTS_EXIST, accountReportExists);
            if (qualification.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                newQualification.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
            }
        }
        return newQualification;
    }

    public boolean isApplicationRoleType() {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.identity.BudgetConstructionNoAccessMessageSetting#setNoAccessMessage(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.rice.kim.api.identity.Person, org.kuali.rice.krad.util.MessageMap)
     */
    public void setNoAccessMessage(BudgetConstructionDocument document, Person user, MessageMap messageMap) {
        Map<String,String> qualification = new HashMap<String,String>(3);
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, document.getChartOfAccountsCode());
        qualification.put(KfsKimAttributes.ACCOUNT_NUMBER, document.getAccountNumber());
        qualification.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, BCConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);

        RoleService roleService = KimApiServiceLocator.getRoleService();

        boolean isFiscalOfficerOrDelegate = roleService.principalHasRole(user.getPrincipalId()
                , Collections.singletonList(roleService.getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.FISCAL_OFFICER_KIM_ROLE_NAME))
                , qualification);
        boolean isBCProcessor = false;
        boolean isProcessorInAccountHierarchy = false;

        List<Organization> userProcessingOrgs = budgetConstructionProcessorService.getProcessorOrgs(user); 
        if (userProcessingOrgs != null && !userProcessingOrgs.isEmpty()) {
            isBCProcessor = true;
            
            List<BudgetConstructionAccountOrganizationHierarchy> accountOrganizationHierarchy = (List<BudgetConstructionAccountOrganizationHierarchy>) budgetDocumentService.retrieveOrBuildAccountOrganizationHierarchy(document.getUniversityFiscalYear(), document.getChartOfAccountsCode(), document.getAccountNumber());
            for (BudgetConstructionAccountOrganizationHierarchy accountOrganization : accountOrganizationHierarchy) {
                if (userProcessingOrgs.contains(accountOrganization.getOrganization())) {
                    isProcessorInAccountHierarchy = true;
                }
            }
        }

        if (document.getOrganizationLevelCode().intValue() == 0) {
            messageMap.putError(KFSConstants.DOCUMENT_ERRORS, BCKeyConstants.ERROR_BUDGET_USER_NOT_IN_HIERARCHY);
        }
        else {
            if (isFiscalOfficerOrDelegate || isProcessorInAccountHierarchy) {
                messageMap.putError(KFSConstants.DOCUMENT_ERRORS, BCKeyConstants.ERROR_BUDGET_USER_BELOW_DOCLEVEL);
            }
            else if (isBCProcessor) {
                messageMap.putError(KFSConstants.DOCUMENT_ERRORS, BCKeyConstants.ERROR_BUDGET_USER_NOT_IN_HIERARCHY);
            }
            else {
                messageMap.putError(KFSConstants.DOCUMENT_ERRORS, BCKeyConstants.ERROR_BUDGET_USER_NOT_ORG_APPROVER);
            }
        }
    }

    public void setBudgetConstructionProcessorService(BudgetConstructionProcessorService budgetConstructionProcessorService) {
        this.budgetConstructionProcessorService = budgetConstructionProcessorService;
    }

    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    public List<KeyValue> getAttributeValidValues(String kimTypeId, String attributeName) {
        return Collections.emptyList();
    }

    public List<String> getQualifiersForExactMatch() {
        return Collections.emptyList();
    }

    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String, String> qualification) throws RiceIllegalArgumentException {
        return Collections.emptyList();
    }

    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) throws RiceIllegalArgumentException {
        return Collections.emptyList();
    }

    public List<RoleMembership> sortRoleMembers(List<RoleMembership> roleMembers) throws RiceIllegalArgumentException {
        return Collections.emptyList();
    }
    
    @Override
    public List<RemotableAttributeError> validateUniqueAttributes(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes) throws RiceIllegalArgumentException {
        return Collections.emptyList();
    }
    
    @Override
    public List<RemotableAttributeError> validateUnmodifiableAttributes(String kimTypeId, Map<String, String> originalAttributes, Map<String, String> newAttributes) throws RiceIllegalArgumentException {
        return Collections.emptyList();
    }
}
