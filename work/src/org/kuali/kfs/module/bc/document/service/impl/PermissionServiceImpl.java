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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Delegate;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RuleExtensionVO;
import edu.iu.uis.eden.clientapp.vo.RuleReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RuleVO;

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
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#getOrgReview(java.lang.String)
     */
    public List<Org> getOrgReview(String personUserIdentifier) throws Exception {
        List<Org> orgReview = new ArrayList<Org>();

        RuleReportCriteriaVO ruleReportCriteria = this.getRuleReportCriteriaForBudgetDocument(personUserIdentifier);
        RuleVO[] rules = new WorkflowInfo().ruleReport(ruleReportCriteria);

        for (RuleVO ruleVO : rules) {
            String organizationCode = null;
            String chartOfAccounts = null;

            RuleExtensionVO[] ruleExtensionVOs = ruleVO.getRuleExtensions();
            for (RuleExtensionVO extensionVO : ruleExtensionVOs) {
                if (ORG_REVIEW_RULE_CHART_CODE_NAME.equals(extensionVO.getKey())) {
                    chartOfAccounts = extensionVO.getValue();
                }
                else if (ORG_REVIEW_RULE_ORG_CODE_NAME.equals(extensionVO.getKey())) {
                    organizationCode = extensionVO.getValue();
                }
            }

            if (chartOfAccounts != null && organizationCode != null) {
                Org org = organizationService.getByPrimaryId(chartOfAccounts, organizationCode);
                if (org != null && !orgReview.contains(org)) {
                    orgReview.add(org);
                }
            }
        }
        return orgReview;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#getOrgReview(org.kuali.core.bo.user.UniversalUser)
     */
    public List<Org> getOrgReview(UniversalUser universalUser) throws Exception {
        return this.getOrgReview(universalUser.getPersonUserIdentifier());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isOrgReviewApprover(java.lang.String, java.lang.String,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isOrgReviewApprover(String chartOfAccountsCode, String organizationCode, UniversalUser universalUser) throws Exception {

        RuleExtensionVO ruleExtensionVO = new RuleExtensionVO(ORG_REVIEW_RULE_CHART_CODE_NAME, chartOfAccountsCode);
        RuleExtensionVO ruleExtensionVO2 = new RuleExtensionVO(ORG_REVIEW_RULE_ORG_CODE_NAME, organizationCode);
        RuleExtensionVO[] ruleExtensionVOs = new RuleExtensionVO[] { ruleExtensionVO, ruleExtensionVO2 };

        RuleReportCriteriaVO ruleReportCriteria = this.getRuleReportCriteriaForBudgetDocument(universalUser.getPersonUserIdentifier());
        ruleReportCriteria.setRuleExtensionVOs(ruleExtensionVOs);

        RuleVO[] rules = new WorkflowInfo().ruleReport(ruleReportCriteria);

        return rules.length > 0;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isOrgReviewApprover(org.kuali.kfs.coa.businessobject.Org,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isOrgReviewApprover(Org organzation, UniversalUser universalUser) {
        try {
            return this.isOrgReviewApprover(organzation.getChartOfAccountsCode(), organzation.getOrganizationCode(), universalUser);
        }
        catch (Exception e) {
            String errorMessage = String.format("Fail to determine if %s is an approver for %s. ", universalUser, organzation);
            LOG.info(errorMessage + e);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#getOrganizationReviewHierachy(org.kuali.core.bo.user.UniversalUser)
     */
    public List<Org> getOrganizationReviewHierachy(UniversalUser universalUser) {
        List<Org> organazationReview = null;

        try {
            organazationReview = this.getOrgReview(universalUser);
        }
        catch (Exception e) {
            String errorMessage = String.format("Fail to get organazation review hierachy for %s. ", universalUser);
            LOG.info(errorMessage + e);
            e.printStackTrace();
        }

        return organazationReview;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isAccountManagerOrDelegate(org.kuali.kfs.coa.businessobject.Account,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isAccountManagerOrDelegate(Account account, UniversalUser universalUser) {
        boolean isAccountManager = StringUtils.equals(universalUser.getPersonUserIdentifier(), account.getAccountManagerUserPersonUserIdentifier());

        return isAccountManager || this.isAccountDelegate(account, universalUser);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.PermissionService#isAccountDelegate(org.kuali.kfs.coa.businessobject.Account,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    public boolean isAccountDelegate(Account account, UniversalUser universalUser) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_SYSTEM_ID, universalUser.getPersonUserIdentifier());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_DELEGATE_ACTIVE_INDICATOR, Boolean.TRUE);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSConstants.FinancialDocumentTypeCodes.BUDGET_CONSTRUCTION);
        int countOfAccountDelegate = businessObjectService.countMatching(Delegate.class, fieldValues);

        if (countOfAccountDelegate <= 0) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, KFSConstants.FinancialDocumentTypeCodes.ALL);
            countOfAccountDelegate += businessObjectService.countMatching(Delegate.class, fieldValues);
        }

        return countOfAccountDelegate > 0;
    }

    /**
     * get the rule report criteria for budget construction document with the specified user
     * 
     * @param personUserIdentifier the specified user
     * @return the rule report criteria for budget construction document with the specified user
     */
    private RuleReportCriteriaVO getRuleReportCriteriaForBudgetDocument(String personUserIdentifier) {
        RuleReportCriteriaVO ruleReportCriteria = new RuleReportCriteriaVO();

        ruleReportCriteria.setDocumentTypeName(BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
        ruleReportCriteria.setRuleTemplateName(BudgetConstructionConstants.ORG_REVIEW_RULE_TEMPLATE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO(personUserIdentifier));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setActionRequestCodes(new String[] { EdenConstants.ACTION_REQUEST_APPROVE_REQ });

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
