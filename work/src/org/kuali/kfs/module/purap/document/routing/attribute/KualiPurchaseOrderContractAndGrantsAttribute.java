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
package org.kuali.workflow.module.purap.attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.Parameter;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.LookupService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapWorkflowConstants;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;

/**
 * Attribute for Purchase Order document types to test whether the document should route to Contract and Grants
 * Review Node<br>
 * <br>
 * <code>This attribute allows wildcard characters</code>
 */
public class KualiPurchaseOrderContractAndGrantsAttribute implements WorkflowAttribute {
    private static Logger LOG = Logger.getLogger(KualiPurchaseOrderContractAndGrantsAttribute.class);

    private static final String SUB_FUND_GROUP_CODE_KEY = "sub_fund_grp_cd";
    private static final String UNIVERSITY_FISCAL_YEAR_KEY = "univ_fiscal_year";
    private static final String CHART_CODE_KEY = "fin_coa_cd";
    private static final String ACCOUNT_NUMBER_KEY = "account_number";
    private static final String OBJECT_CODE_KEY = "object_code";

    private List ruleRows;
    private List routingDataRows;
    private boolean required = false;
    private String fiscalYear;
    private String chartCode;
    private String accountNumber;
    private String objectCode;
    // following attribute only used for rule rows
    private String subFundGroupCode;

    /**
     * Constructs a KualiPurchaseOrderContractAndGrantsAttribute.java.
     */
    public KualiPurchaseOrderContractAndGrantsAttribute() {
        ruleRows = new ArrayList<edu.iu.uis.eden.lookupable.Row>();
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(SubFundGroup.class, KFSPropertyConstants.SUB_FUND_GROUP_CODE, SUB_FUND_GROUP_CODE_KEY));
        
        routingDataRows = new ArrayList<edu.iu.uis.eden.lookupable.Row>();
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Options.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, UNIVERSITY_FISCAL_YEAR_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, CHART_CODE_KEY));
        Map fieldConversionMap = new HashMap();
        fieldConversionMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, CHART_CODE_KEY);
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER, ACCOUNT_NUMBER_KEY, fieldConversionMap));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, OBJECT_CODE_KEY, fieldConversionMap));
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if ( (StringUtils.isBlank(getFiscalYear())) && (StringUtils.isBlank(getChartCode())) && (StringUtils.isBlank(getAccountNumber())) && (StringUtils.isBlank(getObjectCode())) ) {
            return "";
        } // attributeContent
        StringBuffer returnValue = new StringBuffer(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_PREFIX);
        returnValue.append("<" + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR + ">").append(getFiscalYear()).append("</" + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR + ">");
        returnValue.append("<" + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE + ">").append(getChartCode()).append("</" + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE + ">");
        returnValue.append("<" + KFSPropertyConstants.ACCOUNT_NUMBER + ">").append(getAccountNumber()).append("</" + KFSPropertyConstants.ACCOUNT_NUMBER + ">");
        returnValue.append("<" + KFSPropertyConstants.FINANCIAL_OBJECT_CODE + ">").append(getObjectCode()).append("</" + KFSPropertyConstants.FINANCIAL_OBJECT_CODE + ">");
        return returnValue.append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_SUFFIX).toString();
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
        if (StringUtils.isNotBlank(getSubFundGroupCode())) {
            extensions.add(new RuleExtensionValue(SUB_FUND_GROUP_CODE_KEY, getSubFundGroupCode()));
        }
        return extensions;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRuleRows()
     */
    public List<Row> getRuleRows() {
        return ruleRows;
    }

    /**
     * This method returns true if all the following conditions are met:
     * <ol>
     *   <li>Account is marked as a 'Contract and Grants' account (see {@link org.kuali.module.chart.bo.Account#isForContractsAndGrants()})
     *   <li>Account has a Contract and Grants Restricted Object Code as defined in the system business rules
     *   <li>Account has Sub Fund Group Code matching rule value
     * </ol>
     * 
     * @param docContent
     * @param ruleExtensions
     * @return
     */
    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        boolean alwaysMatches = false;
        String ruleSubFundGroupCode = getRuleExtentionValue(SUB_FUND_GROUP_CODE_KEY, ruleExtensions);
        if ( (StringUtils.isBlank(ruleSubFundGroupCode)) || (KFSConstants.WILDCARD_CHARACTER.equalsIgnoreCase(ruleSubFundGroupCode)) ) {
            // if rule extension is blank or the Wildcard character... always match this rule if criteria is true
            alwaysMatches = true;
        }
        ruleSubFundGroupCode = LookupUtils.forceUppercase(SubFundGroup.class, KFSPropertyConstants.SUB_FUND_GROUP_CODE, ruleSubFundGroupCode);
        Set<AccountContainer> accountContainers = populateFromDocContent(docContent);
                
        Parameter parameterRulesByChart = SpringContext.getBean(KualiConfigurationService.class).getParameter( KFSConstants.PURAP_NAMESPACE, PurapConstants.Components.PURCHASE_ORDER, PurapParameterConstants.WorkflowParameters.PurchaseOrderDocument.CG_RESTRICTED_OBJECT_CODE_RULE_PARM_NM );
        for (AccountContainer accountContainer : accountContainers) {
            // check to see if account is a C&G account
            if (accountContainer.account.isForContractsAndGrants()) {
                // check the restricted object code in rule table (object codes listed in the table should route via this attribute)
                boolean ruleSucceeds = SpringContext.getBean(KualiConfigurationService.class).evaluateConstrainedParameter(parameterRulesByChart, accountContainer.account.getChartOfAccountsCode(), accountContainer.objectCode.getFinancialObjectCode() );
                if ( !ruleSucceeds ) {
                    if (StringUtils.isBlank(accountContainer.account.getSubFundGroupCode())) {
                        // sub fund is blank
                        String errorMsg = "SubFund not found for account '" + accountContainer.account.getChartOfAccountsCode() + "-" + accountContainer.account.getAccountNumber() + "'";
                        LOG.error(errorMsg);
                        throw new RuntimeException(errorMsg);
                    }
                    // if alwaysMatches is true then we return here
                    if (alwaysMatches) { return true; }
                    // rule fails (since rules are denoted as 'denied' this means the object code is in the rule parameter text)
                    int indexOfWildcard = ruleSubFundGroupCode.indexOf(KFSConstants.WILDCARD_CHARACTER);
                    if (indexOfWildcard != -1) {
                        // found a wildcard character
                        String prefix = ruleSubFundGroupCode.substring(0, indexOfWildcard);
                        String suffix = ruleSubFundGroupCode.substring(indexOfWildcard + 1);
                        if ( ((prefix.length() == 0) && (accountContainer.account.getSubFundGroupCode().endsWith(suffix))) || 
                             ((suffix.length() == 0) && (accountContainer.account.getSubFundGroupCode().startsWith(prefix))) || 
                             ((prefix.length() != 0) && (suffix.length() != 0) && (accountContainer.account.getSubFundGroupCode().startsWith(prefix)) && (accountContainer.account.getSubFundGroupCode().endsWith(suffix))) ) {
                            return true;
                        }
                    }
                    else {
                        // no valid wildcard character found
                        if (accountContainer.account.getSubFundGroupCode().equals(ruleSubFundGroupCode)) {
                            return true;
                        }
                    }
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

    public Set<AccountContainer> populateFromDocContent(DocumentContent docContent) {
        Set accounts = new HashSet();
        String currentXPath = null;
        try {
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            String finChart = null;
            String finAccount = null;
            String finObjectCode = null;
            String finFiscalYear = null;
            currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            boolean isReport = ((Boolean) xpath.evaluate(currentXPath, docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finChart = xpath.evaluate(currentXPath, docContent.getDocument());
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KFSPropertyConstants.ACCOUNT_NUMBER).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finAccount = xpath.evaluate(currentXPath, docContent.getDocument());
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finObjectCode = xpath.evaluate(currentXPath, docContent.getDocument());
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finFiscalYear = xpath.evaluate(currentXPath, docContent.getDocument());
                AccountContainer ac = getValidAccountContainer(finFiscalYear, finChart, finAccount, finObjectCode);
                if (ObjectUtils.isNotNull(ac)) {
                    accounts.add(ac);
                }
            }
            else {
                // now look at the global documents
                String docTypeName = docContent.getRouteContext().getDocument().getDocumentType().getName();
                if (!KualiWorkflowUtils.isTargetLineOnly(docTypeName)) {
                    currentXPath = KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KualiWorkflowUtils.getSourceAccountingLineClassName(docTypeName));
                    NodeList sourceLineNodes = (NodeList) xpath.evaluate(currentXPath, docContent.getDocument(), XPathConstants.NODESET);
                    accounts.addAll(getAccountContainers(xpath, sourceLineNodes));
                }
                if (!KualiWorkflowUtils.isSourceLineOnly(docTypeName)) {
                    currentXPath = KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KualiWorkflowUtils.getTargetAccountingLineClassName(docTypeName));
                    NodeList targetLineNodes = (NodeList) xpath.evaluate(currentXPath, docContent.getDocument(), XPathConstants.NODESET);
                    accounts.addAll(getAccountContainers(xpath, targetLineNodes));
                }
            }
            return accounts;
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException("XPath Exception caught executing expression: " + currentXPath, e);
        }
    }

    private Set<AccountContainer> getAccountContainers(XPath xpath, NodeList accountingLineNodes) {
        String currentXPath = null;
        try {
            Set<AccountContainer> accountContainers = new HashSet();
            for (int i = 0; i < accountingLineNodes.getLength(); i++) {
                Node accountingLineNode = accountingLineNodes.item(i);
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
                String finChart = xpath.evaluate(currentXPath, accountingLineNode);
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.ACCOUNT_NUMBER;
                String finAccount = xpath.evaluate(currentXPath, accountingLineNode);
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
                String finObjectCode = xpath.evaluate(currentXPath, accountingLineNode);
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.POSTING_YEAR;
                String finFiscalYear = xpath.evaluate(currentXPath, accountingLineNode);
                AccountContainer ac = getValidAccountContainer(finFiscalYear, finChart, finAccount, finObjectCode);
                if (ObjectUtils.isNotNull(ac)) {
                    accountContainers.add(ac);
                }
            }
            return accountContainers;
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException("XPath Exception caught executing expression: " + currentXPath, e);
        }
    }
    
    private AccountContainer getValidAccountContainer(String finFiscalYear, String finChart, String finAccount, String finObjectCode) {
        AccountContainer ac = getPotentialAccountContainer(finFiscalYear, finChart, finAccount, finObjectCode);
        if (ObjectUtils.isNotNull(ac)) {
            String fiscalYearLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Options.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            String chartCodeLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            String accountNumberLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER);
            String objectCodeLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            if (ObjectUtils.isNull(ac.account)) {
                String errorMsg = "Valid Account not found for values " + chartCodeLabel + ": " + finChart + " and " + accountNumberLabel + ":" + finAccount;
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            if (ObjectUtils.isNull(ac.objectCode)) {
                String errorMsg = "Valid Object Code not found for values " + fiscalYearLabel + ": " + finFiscalYear + ", " + chartCodeLabel + ": " + finChart + ", and " + objectCodeLabel + ": " + finObjectCode;
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }
        return ac;
    }
    
    /**
     * @param finFiscalYear  - university fiscal year value
     * @param finChart  - chart code value
     * @param finAccount  - account number value
     * @param finObjectCode  - object code value
     * @return null if one of the variables required is missing or the AccountContainer object holding the potentially valid Account object and the potentially valid Object Code
     * object.  One or both may or may not be null themselves.
     */
    private AccountContainer getPotentialAccountContainer(String finFiscalYear, String finChart, String finAccount, String finObjectCode) {
        if (StringUtils.isNotEmpty(finChart) && StringUtils.isNotEmpty(finAccount) && StringUtils.isNotEmpty(finObjectCode) && StringUtils.isNotEmpty(finFiscalYear)) {
            Account testAccount = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(finChart, finAccount);
            ObjectCode testObjectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(Integer.valueOf(finFiscalYear), finChart, finObjectCode);
            return new AccountContainer(testAccount,testObjectCode);
        }
        return null;
    }

    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        String fiscalYearLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Options.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        setFiscalYear(LookupUtils.forceUppercase(Options.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, (String) paramMap.get(UNIVERSITY_FISCAL_YEAR_KEY)));
        String chartCodeLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        setChartCode(LookupUtils.forceUppercase(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, (String) paramMap.get(CHART_CODE_KEY)));
        String accountNumberLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER);
        setAccountNumber(LookupUtils.forceUppercase(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER, (String) paramMap.get(ACCOUNT_NUMBER_KEY)));
        String objectCodeLabel = KualiWorkflowUtils.getBusinessObjectAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        setObjectCode(LookupUtils.forceUppercase(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, (String) paramMap.get(OBJECT_CODE_KEY)));
        AccountContainer ac = getPotentialAccountContainer(getFiscalYear(), getChartCode(), getAccountNumber(), getObjectCode());
        if (ObjectUtils.isNotNull(ac)) {
            if (ObjectUtils.isNull(ac.account)) {
                String errorMessage = "Valid Account not found for values " + chartCodeLabel + ": " + getChartCode() + " and " + accountNumberLabel + ":" + getAccountNumber();
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
            if (ObjectUtils.isNull(ac.objectCode)) {
                String errorMessage = "Valid Object Code not found for values " + fiscalYearLabel + ": " + getFiscalYear() + ", " + chartCodeLabel + ": " + getChartCode() + ", and " + objectCodeLabel + ": " + getObjectCode();
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
        } else {
            // if the account container is null then we are missing at least one value to use in the lookups
            String errorMessage = "All values must be entered in order to continue";
            errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
        }
        return errors;
    }

    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        setSubFundGroupCode(LookupUtils.forceUppercase(SubFundGroup.class, KFSPropertyConstants.SUB_FUND_GROUP_CODE, (String) paramMap.get(SUB_FUND_GROUP_CODE_KEY)));
        if (isRequired() && StringUtils.isBlank(getSubFundGroupCode())) {
            // value is required but is also blank
            String errorMessage = KualiWorkflowUtils.getBusinessObjectAttributeLabel(SubFundGroup.class, KFSPropertyConstants.SUB_FUND_GROUP_CODE) + " is required";
            errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
        } else if (isRequired() && StringUtils.isNotBlank(getSubFundGroupCode()) && KFSConstants.WILDCARD_CHARACTER.equalsIgnoreCase(getSubFundGroupCode())) {
            // value is required but entered value is the wildcard character
            setSubFundGroupCode(null);
        } else if (StringUtils.isNotBlank(getSubFundGroupCode())) {
            // value is not blank so check value for validity of value
            Map formProps = new HashMap();
            formProps.put(KFSPropertyConstants.SUB_FUND_GROUP_CODE, getSubFundGroupCode());
            Collection subFundGroups = SpringContext.getBean(LookupService.class).findCollectionBySearchUnbounded(SubFundGroup.class, formProps);
            if (subFundGroups.isEmpty()) {
                String errorMessage = KualiWorkflowUtils.getBusinessObjectAttributeLabel(SubFundGroup.class, KFSPropertyConstants.SUB_FUND_GROUP_CODE) + " value does not correspond to any valid entries in the system";
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
        }
        return errors;
    }
    
    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartCode attribute. 
     * @return Returns the chartCode.
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * Sets the chartCode attribute value.
     * @param chartCode The chartCode to set.
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * Gets the fiscalYear attribute. 
     * @return Returns the fiscalYear.
     */
    public String getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear attribute value.
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * Gets the objectCode attribute. 
     * @return Returns the objectCode.
     */
    public String getObjectCode() {
        return objectCode;
    }

    /**
     * Sets the objectCode attribute value.
     * @param objectCode The objectCode to set.
     */
    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * Gets the required attribute. 
     * @return Returns the required.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the required attribute value.
     * @param required The required to set.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Gets the subFundGroupCode attribute. 
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode attribute value.
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    private static class AccountContainer {
        public Account account;
        public ObjectCode objectCode;

        public AccountContainer(Account account, ObjectCode objectCode) {
            super();
            this.account = account;
            this.objectCode = objectCode;
        }
    }
}
