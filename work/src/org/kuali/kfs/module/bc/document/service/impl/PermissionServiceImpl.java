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
package org.kuali.module.budget.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants;
import org.kuali.module.budget.service.PermissionService;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.service.OrganizationService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RuleExtensionVO;
import edu.iu.uis.eden.clientapp.vo.RuleReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RuleVO;

/**
 * This class implements the Budget Construction module PermissionService interface.
 * 
 * PermissionServiceImpl implements methods used to support the BudgetConstruction Security Model.
 * The User access mode to a Budgeted Account (BC Document) is calculated based on the current level
 * of the document in it's organization hierarchy and the set of organizations where the user is defined
 * as a BC Document approver in the organization review hierarchy or the set of accounts where the user
 * is defined as the Fiscal Officer.
 * 
 * Edit access requires the document to be at the same level as one of the user's organization approval nodes
 * or the user is a fiscal officer or delegate of an account for a document at level zero. the User gets 
 * View access to a document set at a level below a user's organization approval node. No access is allowed
 * to a document set at a level above the user's organization approval node.
 * 
 * Organization review hierarchy approval nodes are defined in Workflow as rules using the
 * KualiOrgReviewTemplate where the Document Type is KualiBudgetConstructionDocument and the
 * Chart and Organization codes define the node in the hierarchy and responsibilty type is 
 * Person or Workgroup and Action Request Code is Approve.
 * 
 * TODO verify the description of the rule definition after implementation.
 * 
 */
@Transactional
public class PermissionServiceImpl implements PermissionService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PermissionServiceImpl.class);
    
    private OrganizationService organizationService; 
    
    private static final String ORG_REVIEW_RULE_CHART_CODE_NAME = "fin_coa_cd";
    private static final String ORG_REVIEW_RULE_ORG_CODE_NAME = "org_cd";

    /**
     * @see org.kuali.module.budget.service.PermissionService#getOrgReview(java.lang.String)
     */
    public List<Org> getOrgReview(String personUserIdentifier) throws Exception {
        
        List<Org> orgReview = new ArrayList();
        String organizationCode = null;
        String chartOfAccounts = null;

        WorkflowInfo info = new WorkflowInfo();
        RuleReportCriteriaVO ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
        ruleReportCriteria.setRuleTemplateName(BudgetConstructionConstants.ORG_REVIEW_RULE_TEMPLATE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO(personUserIdentifier));
        ruleReportCriteria.setActionRequestCodes(new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ});
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        RuleVO[] rules = info.ruleReport(ruleReportCriteria);
        for (int i=0; i < rules.length; i++){
            RuleExtensionVO[] ruleExtensionVOs = rules[i].getRuleExtensions();
            organizationCode = null;
            chartOfAccounts = null;
            for (int j=0; j < ruleExtensionVOs.length; j++){
                RuleExtensionVO extensionVO = ruleExtensionVOs[j];
                if (ORG_REVIEW_RULE_CHART_CODE_NAME.equals(extensionVO.getKey())){
                    chartOfAccounts = extensionVO.getValue();
                } else if (ORG_REVIEW_RULE_ORG_CODE_NAME.equals(extensionVO.getKey())){
                    organizationCode = extensionVO.getValue();
                } else {
                    //do nothing, not an extension we are interested in
                }
            }
            if (chartOfAccounts != null && organizationCode != null){
                Org org = (Org) organizationService.getByPrimaryId(chartOfAccounts, organizationCode);
                if (org != null && !orgReview.contains(org)){
                    orgReview.add(org);
                }
            }
        }
        return orgReview;
    }

    /**
     * @see org.kuali.module.budget.service.PermissionService#isOrgReviewApprover(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean isOrgReviewApprover(String personUserIdentifier, String chartOfAccountsCode, String organizationCode) throws Exception {
        
        boolean retVar = false;

        WorkflowInfo info = new WorkflowInfo();
        RuleReportCriteriaVO ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_NAME);
        ruleReportCriteria.setRuleTemplateName(BudgetConstructionConstants.ORG_REVIEW_RULE_TEMPLATE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO(personUserIdentifier));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setActionRequestCodes(new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ});
        RuleExtensionVO ruleExtensionVO = new RuleExtensionVO(ORG_REVIEW_RULE_CHART_CODE_NAME, chartOfAccountsCode);
        RuleExtensionVO ruleExtensionVO2 = new RuleExtensionVO(ORG_REVIEW_RULE_ORG_CODE_NAME, organizationCode);
        RuleExtensionVO[] ruleExtensionVOs = new RuleExtensionVO[] {ruleExtensionVO,ruleExtensionVO2};
        ruleReportCriteria.setRuleExtensionVOs(ruleExtensionVOs);
        RuleVO[] rules = info.ruleReport(ruleReportCriteria);
        if (rules.length >= 1){
            retVar = true;
        }
        return retVar;
    }

    /**
     * Sets the organizationService attribute value.
     * @param organizationService The organizationService to set.
     */
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}
