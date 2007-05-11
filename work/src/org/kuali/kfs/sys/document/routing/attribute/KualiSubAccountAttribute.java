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
package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.service.SubAccountService;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.MassRuleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * This class...
 */
public class KualiSubAccountAttribute implements WorkflowAttribute, MassRuleAttribute {

    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiAccountAttribute.class);

    private static final String FIN_COA_CD_KEY = "fin_coa_cd";

    private static final String ACCOUNT_NBR_KEY = "account_nbr";

    public static final String ORG_CD_KEY = "org_cd";

    public static final String SUB_ACCOUNT_NBR_KEY = "sub_acct_nbr";

    private static final String SUB_ACCOUNT_ATTRIBUTE = "KUALI_SUB_ACCOUNT_ATTRIBUTE";

    private static final String DOCUMENT_SUB_ACCOUNT_VALUES_KEY = "subAccounts";

    private String finCoaCd;

    private String accountNbr;
    
    private String subAccountNbr;
    
    private String orgCd;

    private boolean required;

    private List ruleRows;

    private List routingDataRows;

    /**
     * No arg constructor
     */
    public KualiSubAccountAttribute() {
        List fields = new ArrayList();
        ruleRows = new ArrayList();
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY));
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Account.class, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, FIN_COA_CD_KEY));
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Org.class, KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME, ORG_CD_KEY));
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(SubAccount.class, KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, SUB_ACCOUNT_NBR_KEY));

        routingDataRows = new ArrayList();
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Account.class, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, FIN_COA_CD_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Org.class, KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME, ORG_CD_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(SubAccount.class, KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME, SUB_ACCOUNT_NBR_KEY));

    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getSubAccountNbr()) || ( (Utilities.isEmpty(getSubAccountNbr())) && (Utilities.isEmpty(getOrgCd())) ) ) {
            return "";
        }
        StringBuffer accountOrOrg = new StringBuffer();
        if (Utilities.isEmpty(getAccountNbr())) {
            // we do not have sub account... do org
            accountOrOrg.append("<org>").append(getOrgCd()).append("</org>");
        } else {
            // we have sub account
            accountOrOrg.append("<accountNumber>").append(getAccountNbr()).append("</accountNumber>");
        }
        return new StringBuffer("<report><chart>").append(getFinCoaCd()).append("</chart>").append(accountOrOrg).append("<subAccountNumber>").append(getSubAccountNbr()).append("</subAccountNumber></report>").toString();
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRoutingDataRows()
     */
    public List<Row> getRoutingDataRows() {
        return routingDataRows;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRuleExtensionValues()
     */
    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, this.finCoaCd));
        if (!StringUtils.isBlank(this.accountNbr)) {
            extensions.add(new RuleExtensionValue(ACCOUNT_NBR_KEY, this.accountNbr));
        }
        if (!StringUtils.isBlank(this.orgCd)) {
            extensions.add(new RuleExtensionValue(ORG_CD_KEY, this.orgCd));
        }
        extensions.add(new RuleExtensionValue(SUB_ACCOUNT_NBR_KEY, this.subAccountNbr));
        return extensions;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRuleRows()
     */
    public List<Row> getRuleRows() {
        return ruleRows;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isRequired()
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#setRequired(boolean)
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * TODO delyea - what is the difference between this and {@link #validateRuleData} ?

     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRoutingData(java.util.Map)
     */
    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        this.accountNbr = LookupUtils.forceUppercase(SubAccount.class, "accountNumber", (String) paramMap.get(ACCOUNT_NBR_KEY));
        this.orgCd = LookupUtils.forceUppercase(SubAccount.class, "finReportOrganizationCode", (String) paramMap.get(ORG_CD_KEY));
        String fieldName = "chartOfAccountsCode";
        if (StringUtils.isBlank(this.accountNbr)) {
            fieldName = "financialReportChartCode";
        }
        this.finCoaCd = LookupUtils.forceUppercase(SubAccount.class, fieldName, (String) paramMap.get(FIN_COA_CD_KEY));
        this.subAccountNbr = LookupUtils.forceUppercase(SubAccount.class, "subAccountNumber", (String) paramMap.get(SUB_ACCOUNT_NBR_KEY));
        validateSubAccountValues(errors);
        return errors;
    }

    /**
     * TODO delyea - what is the difference between this and {@link #validateRoutingData} ?
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        this.accountNbr = LookupUtils.forceUppercase(SubAccount.class, "accountNumber", (String) paramMap.get(ACCOUNT_NBR_KEY));
        this.orgCd = LookupUtils.forceUppercase(SubAccount.class, "finReportOrganizationCode", (String) paramMap.get(ORG_CD_KEY));
        String fieldName = "chartOfAccountsCode";
        if (StringUtils.isBlank(this.accountNbr)) {
            fieldName = "financialReportChartCode";
        }
        this.finCoaCd = LookupUtils.forceUppercase(SubAccount.class, fieldName, (String) paramMap.get(FIN_COA_CD_KEY));
        this.subAccountNbr = LookupUtils.forceUppercase(SubAccount.class, "subAccountNumber", (String) paramMap.get(SUB_ACCOUNT_NBR_KEY));
        validateSubAccountValues(errors);
        return errors;
    }

    private void validateSubAccountValues(List errors) {
        if ( (!isRequired()) && (StringUtils.isBlank(this.finCoaCd) && StringUtils.isBlank(this.subAccountNbr) && (StringUtils.isBlank(this.accountNbr) && StringUtils.isBlank(this.orgCd))) ) {
            // attribute is not required and no fields are filled in
            return;
        } else if ( (isRequired()) && (StringUtils.isBlank(this.finCoaCd) || StringUtils.isBlank(this.subAccountNbr) || ((StringUtils.isBlank(this.accountNbr)) && (StringUtils.isBlank(this.orgCd)))) ) {
            // attribute is required and at least one needed field is blank
            errors.add(new WorkflowServiceErrorImpl("Chart, Sub Account, and one of Org or Account Number is required.", "routetemplate.xmlattribute.error"));
        } else if (StringUtils.isNotBlank(this.accountNbr) && StringUtils.isNotBlank(this.orgCd)) {
            // 
        }
        else {
            // may or may not be required but we have values to check
            List subAccounts = getSubAccounts();
            if ( (subAccounts == null) || (subAccounts.isEmpty()) ) {
                errors.add(new WorkflowServiceErrorImpl("Chart, Org, or Sub Account is invalid.","routetemplate.xmlattribute.error"));
            }
        }
    }
    
    private List getSubAccounts() {
        List subAccounts = new ArrayList();
        SubAccountService subAccountService = SpringServiceLocator.getSubAccountService();
        if (StringUtils.isNotBlank(this.accountNbr)) {
            // TODO delyea - does this need "withCaching"?
            SubAccount subAccount = subAccountService.getByPrimaryId(this.finCoaCd, this.accountNbr, this.subAccountNbr);
            if (subAccount != null) {
                subAccounts.add(subAccount);
            }
        } else if (StringUtils.isNotBlank(this.orgCd)) {
            // TODO delyea - IMPLEMENT THIS AND DELETE RuntimeException being thrown
            List testSubAccounts = null;
//            List testSubAccounts = subAccountService.getSubAccountsByOrg(this.finCoaCd, this.orgCd, this.subAccountNbr);
            if ( (testSubAccounts != null) && (!(testSubAccounts.isEmpty())) ) {
                subAccounts.addAll(testSubAccounts);
            }
            throw new RuntimeException("THIS HAS NOT BEEN IMPLEMENTED");
        } else {
            throw new IllegalArgumentException("Parameters should have been passed for either account number or org code.");
        }
        return subAccounts;
    }

    /**
     * Actual matching logic is handled in filterNonMatchingRules where the List of rules is narrowed down to
     * those that should fire.
     *
     * @see #filterNonMatchingRules(RouteContext, List)
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isMatch(java.lang.String, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        return true;
    }

    /**
     * Filters the List of Rules by those that will match.
     * TODO delyea - IMPLEMENT THIS
     */
    public List filterNonMatchingRules(RouteContext routeContext, List rules) {
        List filteredRules = new ArrayList();
        DocumentType documentType = routeContext.getDocument().getDocumentType();
        Set subAccountValues = populateFromDocContent(documentType, routeContext.getDocumentContent(), routeContext);
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            List ruleExtensions = rule.getRuleExtensions();
            this.accountNbr = LookupUtils.forceUppercase(SubAccount.class, "accountNumber", getRuleExtentionValue(ACCOUNT_NBR_KEY, ruleExtensions));
            this.orgCd = LookupUtils.forceUppercase(SubAccount.class, "finReportOrganizationCode", getRuleExtentionValue(ORG_CD_KEY, ruleExtensions));
            String fieldName = "chartOfAccountsCode";
            if (StringUtils.isBlank(this.accountNbr)) {
                fieldName = "financialReportChartCode";
            }
            this.finCoaCd = LookupUtils.forceUppercase(SubAccount.class, fieldName, getRuleExtentionValue(FIN_COA_CD_KEY, ruleExtensions));
            this.subAccountNbr = LookupUtils.forceUppercase(SubAccount.class, "subAccountNumber", getRuleExtentionValue(SUB_ACCOUNT_NBR_KEY, ruleExtensions));
            if (ruleMatches(rule, subAccountValues, routeContext)) {
                filteredRules.add(rule);
            }
        }
        return filteredRules;
    }

    /**
     * Determines if the given Rule matches the document data by comparing the values.
     */
    protected boolean ruleMatches(RuleBaseValues rule, Set subAccountValues, RouteContext routeContext) {
        for (Iterator iter = subAccountValues.iterator(); iter.hasNext();) {
            SubAccount subAccount = (SubAccount) iter.next();
            if (StringUtils.isNotBlank(this.getOrgCd())) {
                // check based on org
                if ( (subAccount.getFinancialReportChartCode().equals(this.getFinCoaCd())) &&
                        (subAccount.getFinReportOrganizationCode().equals(this.getOrgCd())) &&
                        (subAccount.getSubAccountNumber().equals(this.getSubAccountNbr())) ) { 
                    return true;
                }
            } else {
                // check based on account
                if ( (subAccount.getChartOfAccountsCode().equals(this.getFinCoaCd())) &&
                        (subAccount.getAccountNumber().equals(this.getAccountNbr())) &&
                        (subAccount.getSubAccountNumber().equals(this.getSubAccountNbr())) ) { 
                    return true;
                }
            }
        }
        return false;
    }

    private String getRuleExtentionValue(String key, List ruleExtensions) {
        for (Iterator iter = ruleExtensions.iterator(); iter.hasNext();) {
            RuleExtension extension = (RuleExtension) iter.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(this.getClass().getName())) {
                for (Iterator iterator = extension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue value = (RuleExtensionValue) iterator.next();
                    if (value.getKey().equals(key)) {
                        return value.getValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * this method will take the document content, and populate a list of SubAccount objects from the document values
     *
     * @param docContent
     * @return a list of SubAccount objects that are contained in the doc
     */
    private Set populateFromDocContent(DocumentType docType, DocumentContent docContent, RouteContext routeContext) {
        Set subAccountValues = null;
        if (routeContext.getParameters().containsKey(DOCUMENT_SUB_ACCOUNT_VALUES_KEY)) {
            subAccountValues = (Set) routeContext.getParameters().get(DOCUMENT_SUB_ACCOUNT_VALUES_KEY);
        }
        else {
            subAccountValues = new HashSet();
            NodeList nodes = null;
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            try {
                String chart = null;
                String account = null;
                String org = null;
                String subAccount = null;
                boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("report").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
                if (isReport) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("chart").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    account = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("accountNumber").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("org").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    subAccount = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("subAccountNumber").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                if ( StringUtils.isNotBlank(chart) && StringUtils.isNotBlank(subAccount) && (StringUtils.isNotBlank(account) || StringUtils.isNotBlank(org)) ) {
                    List subAccounts = getSubAccounts();
                    if ( (subAccounts == null) || (subAccounts.isEmpty()) ) {
                        throw new RuntimeException("Sub Account declared on the document cannot be found in the system, routing cannot continue.");
                    }
                    //  possibly duplicate add, but this is safe in a HashSet
                    for (Iterator iter = subAccounts.iterator(); iter.hasNext();) {
                        SubAccount subAccountToAdd = (SubAccount) iter.next();
                        subAccountValues.add(subAccountToAdd);
                    }
                }
                else {
                    String xpathExp = null;
                    if (KualiWorkflowUtils.isSourceLineOnly(docType.getName())) {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/subAccount").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    else if (KualiWorkflowUtils.isTargetLineOnly(docType.getName())) {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/subAccount").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    else {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append("/subAccount").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).append(" | ").append(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append("/account").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node subAccountNode = nodes.item(i);
                        // TODO: xstreamsafe should be handling this, but is not, therefore this code block
                        String referenceString = xpath.evaluate("@reference", subAccountNode);
                        if (!StringUtils.isEmpty(referenceString)) {
                            subAccountNode = (Node) xpath.evaluate(referenceString, subAccountNode, XPathConstants.NODE);
                        }
                        String accountNbr = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "accountNumber", subAccountNode);
                        String orgCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "finReportOrganizationCode", subAccountNode);
                        String fieldName = "chartOfAccountsCode";
                        if (StringUtils.isBlank(accountNbr)) {
                            fieldName = "financialReportChartCode";
                        }
                        String finCoaCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + fieldName, subAccountNode);
                        String subAccountCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "subAccountNumber", subAccountNode);
                        if ( StringUtils.isNotBlank(finCoaCd) && StringUtils.isNotBlank(subAccountCd) && (StringUtils.isNotBlank(accountNbr) || StringUtils.isNotBlank(orgCd)) ) {
                            List subAccounts = getSubAccounts();
                            if ( (subAccounts == null) || (subAccounts.isEmpty()) ) {
                                throw new RuntimeException("Sub Account declared on the document cannot be found in the system, routing cannot continue.");
                            }
                            //  possibly duplicate add, but this is safe in a HashSet
                            for (Iterator iter = subAccounts.iterator(); iter.hasNext();) {
                                SubAccount subAccountToAdd = (SubAccount) iter.next();
                                subAccountValues.add(subAccountToAdd);
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            routeContext.getParameters().put(DOCUMENT_SUB_ACCOUNT_VALUES_KEY, subAccountValues);
        }
        return subAccountValues;
    }

    /**
     * Gets the accountNbr attribute. 
     * @return Returns the accountNbr.
     */
    public String getAccountNbr() {
        return accountNbr;
    }

    /**
     * Sets the accountNbr attribute value.
     * @param accountNbr The accountNbr to set.
     */
    public void setAccountNbr(String accountNbr) {
        this.accountNbr = accountNbr;
    }

    /**
     * Gets the finCoaCd attribute. 
     * @return Returns the finCoaCd.
     */
    public String getFinCoaCd() {
        return finCoaCd;
    }

    /**
     * Sets the finCoaCd attribute value.
     * @param finCoaCd The finCoaCd to set.
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    /**
     * Gets the orgCd attribute. 
     * @return Returns the orgCd.
     */
    public String getOrgCd() {
        return orgCd;
    }

    /**
     * Sets the orgCd attribute value.
     * @param orgCd The orgCd to set.
     */
    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
    }

    /**
     * Gets the subAccountNbr attribute. 
     * @return Returns the subAccountNbr.
     */
    public String getSubAccountNbr() {
        return subAccountNbr;
    }

    /**
     * Sets the subAccountNbr attribute value.
     * @param subAccountNbr The subAccountNbr to set.
     */
    public void setSubAccountNbr(String subAccountNbr) {
        this.subAccountNbr = subAccountNbr;
    }

    /**
     * Sets the routingDataRows attribute value.
     * @param routingDataRows The routingDataRows to set.
     */
    public void setRoutingDataRows(List routingDataRows) {
        this.routingDataRows = routingDataRows;
    }

    /**
     * Sets the ruleRows attribute value.
     * @param ruleRows The ruleRows to set.
     */
    public void setRuleRows(List ruleRows) {
        this.ruleRows = ruleRows;
    }

}
