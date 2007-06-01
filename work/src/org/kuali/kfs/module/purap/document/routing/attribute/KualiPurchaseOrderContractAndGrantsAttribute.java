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
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    private static final String REPORT_XML_BASE_TAG_NAME = "report";

    public static final String SUB_FUND_GROUP_CODE_KEY = "sub_fund_grp_cd";

    private List ruleRows;
    private List routingDataRows;
    /**
     * Constructs a KualiPurchaseOrderContractAndGrantsAttribute.java.
     */
    public KualiPurchaseOrderContractAndGrantsAttribute() {
        ruleRows = new ArrayList<edu.iu.uis.eden.lookupable.Row>();
//        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, FIN_COA_CD_KEY));
        
        routingDataRows = new ArrayList<edu.iu.uis.eden.lookupable.Row>();
//        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, FIN_COA_CD_KEY));
    }

    public String getDocContent() {
        // TODO delyea - Auto-generated method stub
        return null;
    }

    public List<Row> getRoutingDataRows() {
        // TODO delyea - Auto-generated method stub
        return null;
    }

    public List<RuleExtensionValue> getRuleExtensionValues() {
        // TODO delyea - Auto-generated method stub
        return null;
    }

    public List<Row> getRuleRows() {
        // TODO delyea - Auto-generated method stub
        return null;
    }

    /**
     * This method returns true if all the following conditions are met:
     * <ol>
     *   <li>Account is marked as a 'Contract and Grants' account (see {@link org.kuali.module.chart.bo.Account#isForContractsAndGrants()})
     *   <li>Account has a Contract and Grants Restricted Object Code (see TODO delyea - add class here)
     *   <li>Account has Sub Fund Group Code matching rule value
     * </ol>
     * 
     * @param docContent
     * @param ruleExtensions
     * @return
     */
    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        String ruleSubFundGroupCode = getRuleExtentionValue(SUB_FUND_GROUP_CODE_KEY, ruleExtensions);
        if (StringUtils.isBlank(ruleSubFundGroupCode)) {
            // if rule has an extension but no value then auto match rule
            return true;
        }
        ruleSubFundGroupCode = ruleSubFundGroupCode.toUpperCase();
        Set<AccountContainer> accountContainers = populateFromDocContent(docContent);
        Map<String,KualiParameterRule> parameterRulesByChart = SpringServiceLocator.getKualiConfigurationService().getRulesByGroup("temp");
        for (AccountContainer accountContainer : accountContainers) {
            if (accountContainer.account.isForContractsAndGrants()) {
                // check the restricted object code in rule table
                KualiParameterRule rule = parameterRulesByChart.get(accountContainer.account.getChartOfAccountsCode());
                if ( (rule != null) && (rule.failsRule(accountContainer.objectCode.getFinancialObjectCode())) ) {
                    // rule fails (since rules are denoted as 'denied' this means the object code is in the rule parameter text)
                    int indexOfWildcard = ruleSubFundGroupCode.indexOf(KFSConstants.WILDCARD_CHARACTER);
                    if (indexOfWildcard != -1) {
                        // found a wildcard character
                        String prefix = ruleSubFundGroupCode.substring(0, indexOfWildcard);
                        String suffix = ruleSubFundGroupCode.substring(indexOfWildcard + 1);
                        if (((prefix.length() == 0) && (accountContainer.account.getSubFundGroup().getSubFundGroupCode().endsWith(suffix))) || ((suffix.length() == 0) && (accountContainer.account.getSubFundGroup().getSubFundGroupCode().startsWith(prefix))) || ((prefix.length() != 0) && (suffix.length() != 0) && (accountContainer.account.getSubFundGroup().getSubFundGroupCode().startsWith(prefix)) && (accountContainer.account.getSubFundGroup().getSubFundGroupCode().endsWith(suffix)))) {
                            return true;
                        }
                    }
                    else {
                        // no valid wildcard character found
                        if (accountContainer.account.getSubFundGroup().getSubFundGroupCode().equals(ruleSubFundGroupCode)) {
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
            currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("report").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
            boolean isReport = ((Boolean) xpath.evaluate(currentXPath, docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finChart = xpath.evaluate(currentXPath, docContent.getDocument());
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KFSPropertyConstants.ACCOUNT_NUMBER).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finAccount = xpath.evaluate(currentXPath, docContent.getDocument());
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KFSPropertyConstants.FINANCIAL_OBJECT_CODE).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finObjectCode = xpath.evaluate(currentXPath, docContent.getDocument());
                currentXPath = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KFSPropertyConstants.POSTING_YEAR).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                finFiscalYear = xpath.evaluate(currentXPath, docContent.getDocument());
            }
            if (StringUtils.isNotEmpty(finChart) && StringUtils.isNotEmpty(finAccount) && StringUtils.isNotEmpty(finObjectCode) && StringUtils.isNotEmpty(finFiscalYear)) {
                Account testAccount = getValidAccount(finChart, finAccount);
                ObjectCode testObjectCode = getValidObjectCode(finFiscalYear, finChart, finObjectCode);
                if (ObjectUtils.isNull(testAccount)) {
                    String errorMsg = "Account not found for values " + finChart + " and " + finAccount;
                    LOG.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                if (ObjectUtils.isNull(testObjectCode)) {
                    String errorMsg = "Valid Object Code not found for values " + finFiscalYear + ", " + finChart + ", and " + finObjectCode;
                    LOG.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                accounts.add(new AccountContainer(testAccount,testObjectCode));
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
//                String referenceString = xpath.evaluate("@reference", accountingLineNode);
//                if (!StringUtils.isEmpty(referenceString)) {
//                    currentXPath = referenceString;
//                    accountingLineNode = (Node) xpath.evaluate(currentXPath, accountingLineNode, XPathConstants.NODE);
//                }
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
                String chartCode = xpath.evaluate(currentXPath, accountingLineNode);
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.ACCOUNT_NUMBER;
                String accountNumber = xpath.evaluate(currentXPath, accountingLineNode);
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
                String objectCode = xpath.evaluate(currentXPath, accountingLineNode);
                currentXPath = KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSPropertyConstants.POSTING_YEAR;
                String fiscalYear = xpath.evaluate(currentXPath, accountingLineNode);
                if (StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(accountNumber) && StringUtils.isNotBlank(objectCode)) {
                    accountContainers.add(new AccountContainer(getValidAccount(chartCode, accountNumber),getValidObjectCode(fiscalYear, chartCode, objectCode)));
                }
            }
            return accountContainers;
        }
        catch (XPathExpressionException e) {
            throw new RuntimeException("XPath Exception caught executing expression: " + currentXPath, e);
        }
    }
    
    private AccountContainer getPotentialAccountContainer(String finFiscalYear, String finChart, String finAccount, String finObjectCode) {
        if (StringUtils.isNotEmpty(finChart) && StringUtils.isNotEmpty(finAccount) && StringUtils.isNotEmpty(finObjectCode) && StringUtils.isNotEmpty(finFiscalYear)) {
            Account testAccount = getValidAccount(finChart, finAccount);
            ObjectCode testObjectCode = getValidObjectCode(finFiscalYear, finChart, finObjectCode);
            if (ObjectUtils.isNull(testAccount)) {
                String errorMsg = "Account not found for values " + finChart + " and " + finAccount;
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            if (ObjectUtils.isNull(testObjectCode)) {
                String errorMsg = "Valid Object Code not found for values " + finFiscalYear + ", " + finChart + ", and " + finObjectCode;
                LOG.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            return new AccountContainer(testAccount,testObjectCode);
        }
        return null;
    }

    private static Account getValidAccount(String chartCode, String accountNumber) {
        Account account = SpringServiceLocator.getAccountService().getByPrimaryIdWithCaching(chartCode, accountNumber);
        if (account == null) {
            String errorMsg = "Account (chartCode: " + chartCode + "  accountNumber: " + accountNumber + ") declared on the document cannot be found in the system, routing cannot continue.";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return account;
    }

    private static ObjectCode getValidObjectCode(String fiscalYear, String chartCode, String objectCode) {
        ObjectCode financialObjectCode = SpringServiceLocator.getObjectCodeService().getByPrimaryId(Integer.valueOf(fiscalYear), chartCode, objectCode);
        if (financialObjectCode == null) {
            String errorMsg = "Object Code (fiscalYear: " + fiscalYear + "  chartCode: " + chartCode + "  objectCode: " + objectCode + ") declared on the document cannot be found in the system, routing cannot continue.";
            LOG.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return financialObjectCode;
    }

    public boolean isRequired() {
        // TODO delyea - Auto-generated method stub
        return false;
    }

    public void setRequired(boolean required) {
        // TODO delyea - Auto-generated method stub

    }

    public List validateRoutingData(Map paramMap) {
        // TODO delyea - Auto-generated method stub
        return null;
    }

    public List validateRuleData(Map paramMap) {
        // TODO delyea - Auto-generated method stub
        return null;
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
