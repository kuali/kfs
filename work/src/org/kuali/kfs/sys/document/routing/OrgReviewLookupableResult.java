/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.workflow.attribute;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.web.UrlResolver;

/**
 * This class wraps the Org Review Values that we need in the lookup result set.
 * 
 * 
 * @see org.kuali.module.chart.web.lookupable.OrgReviewLookupableImpl
 */
public class OrgReviewLookupableResult {
    private String ruleId;
    private String documentTypeName;
    private String activeIndicator;
    private String fin_coa_cd;
    private String org_cd;
    private String overrideCd;
    private String fromAmount;
    private String toAmount;
    private String responsibleParty;

    /**
     * Constructs a OrgReviewLookupableResult.java. This default constructor does nothing.
     */
    public OrgReviewLookupableResult() {
    }

    /**
     * Constructs a OrgReviewLookupableResult.java.
     * 
     * @param ruleBaseValues The source from which this instance's variables are initialized.
     */
    public OrgReviewLookupableResult(RuleBaseValues ruleBaseValues) {
        this.ruleId = ruleBaseValues.getRuleBaseValuesId().toString();
        this.documentTypeName = ruleBaseValues.getDocTypeName();
        this.activeIndicator = ruleBaseValues.getActiveIndDisplay();
        this.fin_coa_cd = ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.FIN_COA_CD_KEY).getValue();
        this.org_cd = ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.ORG_CD_KEY).getValue();
        if (ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.OVERRIDE_CD_KEY) != null) {
            this.overrideCd = ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.OVERRIDE_CD_KEY).getValue();
        }
        if (ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.FROM_AMOUNT_KEY) != null) {
            this.fromAmount = ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.FROM_AMOUNT_KEY).getValue();
        }
        if (ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.TO_AMOUNT_KEY) != null) {
            this.toAmount = ruleBaseValues.getRuleExtensionValue(KualiOrgReviewAttribute.TO_AMOUNT_KEY).getValue();
        }
        StringBuffer responsiblePartyBuffer = new StringBuffer("<a href=\"");
        try {
            if (ruleBaseValues.getResponsibility(0).isUsingWorkgroup()) {
                responsiblePartyBuffer.append(UrlResolver.getInstance().getWorkgroupReportUrl() + "?methodToCall=report&workgroupId=").append(ruleBaseValues.getResponsibility(0).getWorkgroup().getWorkflowGroupId());
            }
            else {
                responsiblePartyBuffer.append(UrlResolver.getInstance().getUserReportUrl() + "?methodToCall=report&workflowId=").append(ruleBaseValues.getResponsibility(0).getWorkflowUser().getWorkflowId());
            }
            responsiblePartyBuffer.append("&showEdit=no").append("\" >");
            if (ruleBaseValues.getResponsibility(0).isUsingWorkgroup()) {
                responsiblePartyBuffer.append(ruleBaseValues.getResponsibility(0).getWorkgroup().getDisplayName());
            }
            else {
                responsiblePartyBuffer.append(ruleBaseValues.getResponsibility(0).getWorkflowUser().getDisplayName());
            }
            responsiblePartyBuffer.append("</a>");
            this.responsibleParty = responsiblePartyBuffer.toString();
        }
        catch (EdenUserNotFoundException e) {
            throw new RuntimeException("Unable to find user for Rule Id: " + ruleBaseValues.getRuleBaseValuesId(), e);
        }
    }

    /**
     * Gets the activeIndicator attribute.
     * 
     * @return Returns the activeIndicator.
     */
    public String getActiveIndicator() {
        return activeIndicator;
    }

    /**
     * Sets the activeIndicator attribute value.
     * 
     * @param activeIndicator The activeIndicator to set.
     */
    public void setActiveIndicator(String activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getFin_coa_cd() {
        return fin_coa_cd;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setFin_coa_cd(String chartOfAccountsCode) {
        this.fin_coa_cd = chartOfAccountsCode;
    }

    /**
     * Gets the documentTypeName attribute.
     * 
     * @return Returns the documentTypeName.
     */
    public String getDocumentTypeName() {
        return documentTypeName;
    }

    /**
     * Sets the documentTypeName attribute value.
     * 
     * @param documentTypeName The documentTypeName to set.
     */
    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    /**
     * Gets the fromAmount attribute.
     * 
     * @return Returns the fromAmount.
     */
    public String getFromAmount() {
        return fromAmount;
    }

    /**
     * Sets the fromAmount attribute value.
     * 
     * @param fromAmount The fromAmount to set.
     */
    public void setFromAmount(String fromAmount) {
        this.fromAmount = fromAmount;
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode.
     */
    public String getOrg_cd() {
        return org_cd;
    }

    /**
     * Sets the organizationCode attribute value.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrg_cd(String organizationCode) {
        this.org_cd = organizationCode;
    }

    /**
     * Gets the overrideCode attribute.
     * 
     * @return Returns the overrideCode.
     */
    public String getOverrideCd() {
        return overrideCd;
    }

    /**
     * Sets the overrideCode attribute value.
     * 
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCd(String overrideCode) {
        this.overrideCd = overrideCode;
    }

    /**
     * Gets the responsibleParty attribute.
     * 
     * @return Returns the responsibleParty.
     */
    public String getResponsibleParty() {
        return responsibleParty;
    }

    /**
     * Sets the responsibleParty attribute value.
     * 
     * @param responsibleParty The responsibleParty to set.
     */
    public void setResponsibleParty(String responsibleParty) {
        this.responsibleParty = responsibleParty;
    }

    /**
     * Gets the ruleId attribute.
     * 
     * @return Returns the ruleId.
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Sets the ruleId attribute value.
     * 
     * @param ruleId The ruleId to set.
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * Gets the toAmount attribute.
     * 
     * @return Returns the toAmount.
     */
    public String getToAmount() {
        return toAmount;
    }

    /**
     * Sets the toAmount attribute value.
     * 
     * @param toAmount The toAmount to set.
     */
    public void setToAmount(String toAmount) {
        this.toAmount = toAmount;
    }
}