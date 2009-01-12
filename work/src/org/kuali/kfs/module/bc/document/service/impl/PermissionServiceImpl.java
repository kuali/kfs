/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.kuali.rice.kew.dto.RuleDTO;
import org.kuali.rice.kew.dto.RuleExtensionDTO;
import org.kuali.rice.kew.dto.RuleReportCriteriaDTO;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the Budget Construction module PermissionService interface. PermissionServiceImpl implements methods used
 * to support the BudgetConstruction Security Model. The User access mode to a Budgeted Account (BC Document) is calculated based on
 * the current level of the document in it's organization hierarchy and the set of organizations where the user is defined as a BC
 * Document approver in the organization review hierarchy or the set of accounts where the user is defined as the Fiscal Officer.
 * Edit access requires the document to be at the same level as one of the user's organization approval nodes or the user is a
 * fiscal officer or delegate of an account for a document at level zero. the User gets View access to a document set at a level
 * below a user's organization approval node. No access is allowed to a document set at a level above the user's organization
 * approval node. Organization review hierarchy approval nodes are defined in Workflow as rules using the KualiOrgReviewTemplate
 * where the Document Type is BudgetConstructionDocument and the Chart and Organization codes define the node in the hierarchy and
 * responsibilty type is Person or Workgroup and Action Request Code is Approve. TODO verify the description of the rule definition
 * after implementation.
 */
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PermissionServiceImpl.class);

    private OrganizationService organizationService;
    private BusinessObjectService businessObjectService;

    private static final String ORG_REVIEW_RULE_CHART_CODE_NAME = "fin_coa_cd";
    private static final String ORG_REVIEW_RULE_ORG_CODE_NAME = "org_cd";

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#getOrgReview(org.kuali.rice.kim.bo.Person)
     */
    public List<Organization> getOrgReview(Person person) throws Exception {
        return this.getOrgReview(person.getPrincipalId());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isOrgReviewApprover(java.lang.String, java.lang.String,
     *      org.kuali.rice.kim.bo.Person)
     */
    public boolean isOrgReviewApprover(String chartOfAccountsCode, String organizationCode, Person person) throws Exception {

        RuleExtensionDTO ruleExtensionDTO = new RuleExtensionDTO(ORG_REVIEW_RULE_CHART_CODE_NAME, chartOfAccountsCode);
        RuleExtensionDTO ruleExtensionVO2 = new RuleExtensionDTO(ORG_REVIEW_RULE_ORG_CODE_NAME, organizationCode);
        RuleExtensionDTO[] ruleExtensionVOs = new RuleExtensionDTO[] { ruleExtensionDTO, ruleExtensionVO2 };

        RuleReportCriteriaDTO ruleReportCriteria = this.getRuleReportCriteriaForBudgetDocument(person.getPrincipalName());
        ruleReportCriteria.setRuleExtensionVOs(ruleExtensionVOs);

        RuleDTO[] rules = new WorkflowInfo().ruleReport(ruleReportCriteria);

        return rules.length > 0;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isOrgReviewApprover(org.kuali.kfs.coa.businessobject.Org,
     *      org.kuali.rice.kim.bo.Person)
     */
    public boolean isOrgReviewApprover(Organization organzation, Person person) {
        try {
            return this.isOrgReviewApprover(organzation.getChartOfAccountsCode(), organzation.getOrganizationCode(), person);
        }
        catch (Exception e) {
            String errorMessage = String.format("Fail to determine if %s is an approver for %s. ", person, organzation);
            LOG.info(errorMessage + e);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#getOrganizationReviewHierachy(org.kuali.rice.kim.bo.Person)
     */
    public List<Organization> getOrganizationReviewHierachy(Person person) {
        List<Organization> organazationReview = null;

        try {
            organazationReview = this.getOrgReview(person);
        }
        catch (Exception e) {
            String errorMessage = String.format("Fail to get organazation review hierachy for %s. ", person);
            LOG.info(errorMessage + e);
            e.printStackTrace();
        }

        return organazationReview;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isAccountManagerOrDelegate(org.kuali.kfs.coa.businessobject.Account,
     *      org.kuali.rice.kim.bo.Person)
     */
    public boolean isAccountManagerOrDelegate(Account account, Person person) {
        boolean isAccountManager = StringUtils.equals(person.getPrincipalId(), account.getAccountFiscalOfficerSystemIdentifier());

        return isAccountManager || this.isAccountDelegate(account, person);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isAccountDelegate(org.kuali.kfs.coa.businessobject.Account,
     *      org.kuali.rice.kim.bo.Person)
     */
    public boolean isAccountDelegate(Account account, Person person) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_SYSTEM_ID, person.getPrincipalId());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_ACTIVE_INDICATOR, Boolean.TRUE);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSConstants.FinancialDocumentTypeCodes.BUDGET_CONSTRUCTION);
        int countOfAccountDelegate = businessObjectService.countMatching(AccountDelegate.class, fieldValues);

        if (countOfAccountDelegate <= 0) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSConstants.FinancialDocumentTypeCodes.ALL);
            countOfAccountDelegate += businessObjectService.countMatching(AccountDelegate.class, fieldValues);
        }

        return countOfAccountDelegate > 0;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isRootApprover(org.kuali.rice.kim.bo.Person)
     */
    public boolean isRootApprover(Person person) {

        String[] rootNode = organizationService.getRootOrganizationCode();
        String rootChart = rootNode[0];
        String rootOrganization = rootNode[1];
        boolean isRootOrgApprover;
        try {
            isRootOrgApprover = this.isOrgReviewApprover(rootChart, rootOrganization, person);

        }
        catch (Exception e) {
            isRootOrgApprover = false;
        }
        return isRootOrgApprover;
    }

    /**
     * collect the list of organizations where the user is a BC document approver
     * 
     * @param principalName the specified person user identifier
     * @return the list of organizations where the user is a BC document approver
     */
    private List<Organization> getOrgReview(String principalId) throws Exception {
        List<Organization> orgReview = new ArrayList<Organization>();

        RuleReportCriteriaDTO ruleReportCriteria = this.getRuleReportCriteriaForBudgetDocument(principalId);
        RuleDTO[] rules = new WorkflowInfo().ruleReport(ruleReportCriteria);

        for (RuleDTO ruleDTO : rules) {
            String organizationCode = null;
            String chartOfAccounts = null;

            RuleExtensionDTO[] ruleExtensionVOs = ruleDTO.getRuleExtensions();
            for (RuleExtensionDTO extensionDTO : ruleExtensionVOs) {
                if (ORG_REVIEW_RULE_CHART_CODE_NAME.equals(extensionDTO.getKey())) {
                    chartOfAccounts = extensionDTO.getValue();
                }
                else if (ORG_REVIEW_RULE_ORG_CODE_NAME.equals(extensionDTO.getKey())) {
                    organizationCode = extensionDTO.getValue();
                }
            }

            if (chartOfAccounts != null && organizationCode != null) {
                Organization org = organizationService.getByPrimaryId(chartOfAccounts, organizationCode);
                if (org != null && !orgReview.contains(org)) {
                    orgReview.add(org);
                }
            }
        }
        return orgReview;
    }

    /**
     * get the rule report criteria for budget construction document with the specified user
     * 
     * @param principalName the specified user
     * @return the rule report criteria for budget construction document with the specified user
     */
    private RuleReportCriteriaDTO getRuleReportCriteriaForBudgetDocument(String principalId) {
        RuleReportCriteriaDTO ruleReportCriteria = new RuleReportCriteriaDTO();

        ruleReportCriteria.setDocumentTypeName(BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
        ruleReportCriteria.setRuleTemplateName(BudgetConstructionConstants.ORG_REVIEW_RULE_TEMPLATE);
        ruleReportCriteria.setResponsiblePrincipalId(principalId);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setActionRequestCodes(new String[] { KEWConstants.ACTION_REQUEST_APPROVE_REQ });

        return ruleReportCriteria;
    }

    /**
     * Sets the organizationService attribute value.
     * 
     * @param organizationService The organizationService to set.
     */
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

