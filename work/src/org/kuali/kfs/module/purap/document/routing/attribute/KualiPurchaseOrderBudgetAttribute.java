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
package org.kuali.kfs.module.purap.document.routing.attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.web.ui.Row;

/**
 * TODO delyea - documentation
 */
public class KualiPurchaseOrderBudgetAttribute implements WorkflowAttribute {
    private static Logger LOG = Logger.getLogger(KualiPurchaseOrderBudgetAttribute.class);

    public static final String FIN_COA_CD_KEY = "fin_coa_cd";
    private static final String UNIVERSITY_FISCAL_YEAR_KEY = "univ_fiscal_year";

    private boolean required = false;
    private String finCoaCd;
    private String fiscalYear;
    private List ruleRows;
    private List routingDataRows;

    /**
     * No arg constructor
     */
    public KualiPurchaseOrderBudgetAttribute() {
        ruleRows = new ArrayList<Row>();
        ruleRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, FIN_COA_CD_KEY));

        routingDataRows = new ArrayList<Row>();
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(SystemOptions.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, UNIVERSITY_FISCAL_YEAR_KEY));
        routingDataRows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, FIN_COA_CD_KEY));
    }

    /**
     * Constructs a new object given the chart code
     */
    public KualiPurchaseOrderBudgetAttribute(String finCoaCode) {
        this();
        this.finCoaCd = finCoaCode;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if ((StringUtils.isBlank(getFinCoaCd())) && (StringUtils.isBlank(getFiscalYear()))) {
            return "";
        }
        StringBuffer returnValue = new StringBuffer(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_PREFIX);
        returnValue.append("<" + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR + ">").append(getFiscalYear()).append("</" + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR + ">");
        returnValue.append("<" + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE + ">").append(getFinCoaCd()).append("</" + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE + ">");
        return returnValue.append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_SUFFIX).toString();
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getRoutingDataRows()
     */
    public List<Row> getRoutingDataRows() {
        return routingDataRows;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getRuleRows()
     */
    public List<Row> getRuleRows() {
        return ruleRows;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#getRuleExtensionValues()
     */
    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, getFinCoaCd()));
        return extensions;
    }

    /**
     * This method will return true for a match if all the following conditions are met:
     * <ol>
     * <li>The fiscal year of the document is less than or equal to the system's current fiscal year
     * <li>At least one account with the rule extension chart code (passed in via the ruleExtensions parameter) has insufficient
     * funds on it
     * </ol>
     * 
     * @param docContent - contains the data in XML format that will be compared with the rules saved in workflow
     * @param ruleExtensions - These should include chart codes to match against coming from rules in the system
     * @return true if this document contains required criteria and rule should fire
     */
    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        boolean alwaysRoutes = false;
        String documentHeaderId = null;
        String currentXpathExpression = null;
        try {
            String ruleChartCode = getRuleExtentionValue(FIN_COA_CD_KEY, ruleExtensions);
            if ((StringUtils.isBlank(ruleChartCode)) || (KFSConstants.WILDCARD_CHARACTER.equalsIgnoreCase(ruleChartCode))) {
                // if rule extension is blank or the Wildcard character... always match this rule if criteria is true
                alwaysRoutes = true;
                // String errorMsg = "Attempted matching of Rule Extension where " +
                // KualiWorkflowUtils.getBusinessObjectAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE) + "
                // is blank but is required";
                // LOG.error(errorMsg);
                // throw new RuntimeException(errorMsg);
            }
            XPath xPath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            currentXpathExpression = KualiWorkflowUtils.xstreamSafeXPath(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX);
            boolean isReport = ((Boolean) xPath.evaluate(currentXpathExpression, docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                currentXpathExpression = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX + "/" + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR;
            }
            else {
                currentXpathExpression = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + "document/postingYear";
            }
            String documentFiscalYearString = KualiWorkflowUtils.xstreamSafeEval(xPath, currentXpathExpression, docContent.getDocContent());
            // if document's fiscal year is less than or equal to the current fiscal year
            if (SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().compareTo(Integer.valueOf(documentFiscalYearString)) >= 0) {
                if (alwaysRoutes) {
                    return true;
                }
                if (isReport) {
                    currentXpathExpression = KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX + KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX + "/" + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
                    return ruleChartCode.equalsIgnoreCase(KualiWorkflowUtils.xstreamSafeEval(xPath, currentXpathExpression, docContent.getDocContent()));
                }
                else {
                    documentHeaderId = docContent.getRouteContext().getDocument().getRouteHeaderId().toString();
                    PurchaseOrderDocument po = (PurchaseOrderDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentHeaderId);
                    // get list of sufficientfundItems
                    List<SufficientFundsItem> fundsItems = SpringContext.getBean(SufficientFundsService.class).checkSufficientFunds(po.getPendingLedgerEntriesForSufficientFundsChecking());
                    for (SufficientFundsItem fundsItem : fundsItems) {
                        if (ruleChartCode.equalsIgnoreCase(fundsItem.getAccount().getChartOfAccountsCode())) {
                            LOG.debug("Chart code of rule extension matches chart code of at least one Sufficient Funds Item");
                            return true;
                        }
                    }
                }
            }
        }
        catch (WorkflowException we) {
            String errorMsg = "Error attempted to get document using doc id  " + documentHeaderId;
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
        catch (XPathExpressionException xee) {
            String errorMsg = "Error trying to use xpath expression " + currentXpathExpression;
            LOG.error(errorMsg, xee);
            throw new RuntimeException(errorMsg, xee);
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
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#validateRoutingData(java.util.Map)
     */
    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        setFiscalYear(LookupUtils.forceUppercase(SystemOptions.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, (String) paramMap.get(UNIVERSITY_FISCAL_YEAR_KEY)));
        setFinCoaCd(LookupUtils.forceUppercase(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, (String) paramMap.get(FIN_COA_CD_KEY)));
        String label = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        if (StringUtils.isBlank(getFinCoaCd())) {
            String errorMessage = label + " is required";
            errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
        }
        else {
            // not blank so check value for validity
            Chart chart = SpringContext.getBean(ChartService.class).getByPrimaryId(getFinCoaCd());
            if (chart == null) {
                String errorMessage = label + " entered is invalid";
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
        }
        label = KualiWorkflowUtils.getBusinessObjectAttributeLabel(SystemOptions.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        if (StringUtils.isBlank(getFiscalYear())) {
            String errorMessage = label + " is required";
            errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
        }
        else {
            // not blank so check value for validity
            SystemOptions options = SpringContext.getBean(OptionsService.class).getOptions(Integer.valueOf(getFiscalYear()));
            if (options == null) {
                String errorMessage = label + " entered is invalid";
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
        }
        return errors;
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        setFinCoaCd(LookupUtils.forceUppercase(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, (String) paramMap.get(FIN_COA_CD_KEY)));
        String label = KualiWorkflowUtils.getBusinessObjectAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        if (isRequired() && StringUtils.isBlank(getFinCoaCd())) {
            // value is blank but required
            String errorMessage = label + " is required";
            errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
        }
        else if (StringUtils.isNotBlank(getFinCoaCd())) {
            // not blank so check value for validity
            Chart chart = SpringContext.getBean(ChartService.class).getByPrimaryId(getFinCoaCd());
            if (chart == null) {
                String errorMessage = label + " entered is invalid";
                errors.add(new WorkflowServiceErrorImpl(errorMessage, "routetemplate.xmlattribute.error", errorMessage));
            }
        }
        return errors;
    }

    /**
     * Gets the required attribute.
     * 
     * @return Returns the required.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the required attribute value.
     * 
     * @param required The required to set.
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Gets the finCoaCd attribute.
     * 
     * @return Returns the finCoaCd.
     */
    public String getFinCoaCd() {
        return finCoaCd;
    }

    /**
     * Sets the finCoaCd attribute value.
     * 
     * @param finCoaCd The finCoaCd to set.
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    /**
     * Gets the fiscalYear attribute.
     * 
     * @return Returns the fiscalYear.
     */
    public String getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear attribute value.
     * 
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }
}
