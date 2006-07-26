/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers, Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees, Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on behalf of the University of Arizona, and the r*smart group. Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining, using and/or copying this Original Work, you agree that you have read, understand, and will comply with the terms and conditions of the Educational Community License. You may obtain a copy of the License at: http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.workflow.KualiConstants;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * KualiOrgReviewAttribute should be used when using Orgs and thier inner details to do routing.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class KualiOrgReviewAttribute implements WorkflowAttribute {

    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiOrgReviewAttribute.class);

    public static final String FIN_COA_CD_KEY = "fin_coa_cd";

    public static final String ORG_CD_KEY = "org_cd";

    public static final String FROM_AMOUNT_KEY = "fromAmount";

    public static final String TO_AMOUNT_KEY = "toAmount";

    private static final String TOTAL_AMOUNT_KEY = "totalAmount";

    public static final String OVERRIDE_CD_KEY = "overrideCd";

    private static final String ORG_REVIEW_ATTRIBUTE = "KUALI_ORG_REVIEW_ATTRIBUTE";

    private static Map ORGS = new HashMap();

    private static final String MAINTAINABLE_PREFIX = "//newMaintainableObject/businessObject/";

    private static final String ACCOUNT_DOC_TYPE = "KualiAccountMaintenanceDocument";

    private static final String ACCOUNT_DEL_DOC_TYPE = "KualiAccountDelegateMaintenanceDocument";

    private static final String FIS_USER_DOC_TYPE = "KualiUserMaintenanceDocument";

    private static final String ORGANIZATION_DOC_TYPE = "KualiOrganizationMaintenanceDocument";

    private static final String SUB_ACCOUNT_DOC_TYPE = "KualiSubAccountMaintenanceDocument";

    private static final String SUB_OBJECT_DOC_TYPE = "KualiSubObjectMaintenanceDocument";

    private static final String PROJECT_CODE_DOC_TYPE = "KualiProjectCodeMaintenanceDocument";

    private String finCoaCd;

    private String orgCd;

    private String toAmount;

    private String fromAmount;

    private String overrideCd;

    private String totalDollarAmount;

    private boolean required;

    private List ruleRows;

    private List routingDataRows;

    /**
     * TODO clean the rest of the field defs up once chart is working this code up no arg constructor, which will initialize the
     * fields and rows of the attribute
     */
    public KualiOrgReviewAttribute() {
        List fields = new ArrayList();

        ruleRows = new ArrayList();
        ruleRows.add(getChartRow());
        ruleRows.add(getOrgRow());
        ruleRows.add(getOverrideCodeRow());

        fields = new ArrayList();
        fields.add(new Field("From Amount", "", Field.TEXT, true, FROM_AMOUNT_KEY, "", null, null, FROM_AMOUNT_KEY));
        ruleRows.add(new Row(fields));
        fields = new ArrayList();
        fields.add(new Field("To Amount", "", Field.TEXT, true, TO_AMOUNT_KEY, "", null, null, TO_AMOUNT_KEY));
        ruleRows.add(new Row(fields));

        routingDataRows = new ArrayList();
        routingDataRows.add(getChartRow());
        routingDataRows.add(getOrgRow());
        routingDataRows.add(getOverrideCodeRow());

        fields = new ArrayList();
        fields.add(new Field("Total Amount", "", Field.TEXT, true, TOTAL_AMOUNT_KEY, "", null, null, TOTAL_AMOUNT_KEY));
        routingDataRows.add(new Row(fields));
    }

    private edu.iu.uis.eden.lookupable.Row getChartRow() {
        org.kuali.core.web.uidraw.Field kualiChartField = FieldUtils.getPropertyField(Chart.class, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, false);
        List chartFields = new ArrayList();
        chartFields.add(new Field(kualiChartField.getFieldLabel(), WorkflowLookupableImpl.getHelpUrl(kualiChartField), Field.TEXT, true, FIN_COA_CD_KEY, kualiChartField.getPropertyValue(), kualiChartField.getFieldValidValues(), WorkflowLookupableImpl.getLookupableImplName(Chart.class), FIN_COA_CD_KEY));
        chartFields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, WorkflowLookupableImpl.getLookupableName(WorkflowLookupableImpl.getLookupableImplName(Chart.class), new StringBuffer(WorkflowLookupableImpl.LOOKUPABLE_IMPL_NAME_PREFIX).append(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).append(":").append(FIN_COA_CD_KEY).toString())));
        return new Row(chartFields);
    }

    private edu.iu.uis.eden.lookupable.Row getOrgRow() {
        List orgFields = new ArrayList();
        orgFields.add(new Field("Org", "", Field.TEXT, true, ORG_CD_KEY, "", null, null, ORG_CD_KEY));
        return new Row(orgFields);
    }

    private edu.iu.uis.eden.lookupable.Row getOverrideCodeRow() {
        List orgFields = new ArrayList();
        orgFields.add(new Field("Override Code", "", Field.TEXT, true, OVERRIDE_CD_KEY, "", null, null, OVERRIDE_CD_KEY));
        return new Row(orgFields);
    }

    /**
     * constructor that takes the chart, org, which calls the no arg constructor
     * 
     * @param finCoaCd
     * @param orgCd
     */
    public KualiOrgReviewAttribute(String finCoaCd, String orgCd) {
        this();
        this.finCoaCd = finCoaCd;
        this.orgCd = orgCd;
    }

    public List getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, this.finCoaCd));
        extensions.add(new RuleExtensionValue(ORG_CD_KEY, this.orgCd));
        if (StringUtils.isBlank(this.fromAmount)) {
            this.fromAmount = "0";
        }
        extensions.add(new RuleExtensionValue(FROM_AMOUNT_KEY, this.fromAmount));
        if (!StringUtils.isBlank(this.toAmount)) {
            extensions.add(new RuleExtensionValue(TO_AMOUNT_KEY, this.toAmount));
        }
        if (!StringUtils.isBlank(this.overrideCd)) {
            extensions.add(new RuleExtensionValue(OVERRIDE_CD_KEY, this.overrideCd));
        }
        return extensions;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        if (isRequired()) {
            this.finCoaCd = (String) paramMap.get(FIN_COA_CD_KEY);
            this.orgCd = (String) paramMap.get(ORG_CD_KEY);
            this.fromAmount = (String) paramMap.get(FROM_AMOUNT_KEY);
            this.toAmount = (String) paramMap.get(TO_AMOUNT_KEY);
            this.overrideCd = (String) paramMap.get(OVERRIDE_CD_KEY);
            validateOrg(errors);
            if (StringUtils.isNotBlank(toAmount) && !StringUtils.isNumeric(toAmount)) {
                errors.add(new WorkflowServiceErrorImpl("To Amount is invalid.", "routetemplate.dollarrangeattribute.toamount.invalid"));
            }
            if (StringUtils.isNotBlank(toAmount) && !StringUtils.isNumeric(fromAmount)) {
                errors.add(new WorkflowServiceErrorImpl("From Amount is invalid.", "routetemplate.dollarrangeattribute.fromamount.invalid"));
            }
        }
        return errors;
    }

    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        this.finCoaCd = (String) paramMap.get(FIN_COA_CD_KEY);
        this.orgCd = (String) paramMap.get(ORG_CD_KEY);
        this.totalDollarAmount = (String) paramMap.get(TOTAL_AMOUNT_KEY);
        this.overrideCd = (String) paramMap.get(OVERRIDE_CD_KEY);
        if (isRequired()) {
            validateOrg(errors);
            if (!StringUtils.isNumeric(this.totalDollarAmount)) {
                errors.add(new WorkflowServiceErrorImpl("Total Amount is invalid.", ""));
            }
        }
        return errors;
    }

    private void validateOrg(List errors) {
        if (StringUtils.isBlank(this.finCoaCd) || StringUtils.isBlank(this.orgCd)) {
            errors.add(new WorkflowServiceErrorImpl("Chart/org is required.", "routetemplate.chartorgattribute.chartorg.required"));
        }
        else if (SpringServiceLocator.getOrganizationService().getByPrimaryId(finCoaCd, orgCd) == null) {
            errors.add(new WorkflowServiceErrorImpl("Chart/org is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
        }
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getOrgCd())) {
            return "";
        }
        return "<report><chart>" + getFinCoaCd() + "</chart><org>" + getOrgCd() + "</org><totalDollarAmount>" + getTotalDollarAmount() + "</totalDollarAmount>" + "<overrideCode>" + getOverrideCd() + "</overrideCode></report>";
    }

    public String getAttributeLabel() {
        return "";
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isMatch(java.lang.String, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {

        this.finCoaCd = getRuleExtentionValue(FIN_COA_CD_KEY, ruleExtensions);
        this.orgCd = getRuleExtentionValue(ORG_CD_KEY, ruleExtensions);
        this.fromAmount = getRuleExtentionValue(FROM_AMOUNT_KEY, ruleExtensions);
        this.toAmount = getRuleExtentionValue(TO_AMOUNT_KEY, ruleExtensions);
        this.overrideCd = getRuleExtentionValue(OVERRIDE_CD_KEY, ruleExtensions);
        DocumentType documentType = docContent.getRouteContext().getDocument().getDocumentType();
        Set chartOrgValues = populateFromDocContent(documentType, docContent);

        boolean matchesOrg = false;
        for (Iterator iter = chartOrgValues.iterator(); iter.hasNext();) {
            Org org = (Org) iter.next();
            if (org.getChartOfAccountsCode().equals(this.getFinCoaCd()) && org.getOrganizationCode().equals(this.getOrgCd())) {
                matchesOrg = true;
                break;
            }
        }

        if (!matchesOrg) {
            return false;
        }

        Float documentAmount = getAmount(documentType, docContent);
        if (documentAmount != null) {
            Float ruleFromAmount = new Float(fromAmount);
            if (!StringUtils.isEmpty(toAmount)) {
                Float ruleToAmount = new Float(toAmount);
                if (!(ruleFromAmount.floatValue() <= documentAmount.floatValue() && documentAmount.floatValue() >= ruleToAmount.floatValue())) {
                    return false;
                }
            }
            else if (!(ruleFromAmount.floatValue() <= documentAmount.floatValue())) {
                return false;
            }
        }

        if (this.overrideCd != null) {
            String docOverrideCd = getOverrideCd(documentType, docContent);
            if (!docOverrideCd.equals(this.overrideCd)) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method is a recursive method that will retrive reports to orgs to build up the hierarchy of organizations
     * 
     * @param chartOrgSet
     * @param chartOrg
     */
    private void buildOrgReviewHierarchy(Set chartOrgSet, Org startOrg) {
        if (startOrg.getReportsToChartOfAccountsCode().equals(startOrg.getChartOfAccountsCode()) && startOrg.getReportsToOrganizationCode().equals(startOrg.getOrganizationCode())) {
            return;
        }
        Org reportsToOrg = SpringServiceLocator.getOrganizationService().getByPrimaryId(startOrg.getReportsToChartOfAccountsCode(), startOrg.getReportsToOrganizationCode());
        chartOrgSet.add(reportsToOrg);
        buildOrgReviewHierarchy(chartOrgSet, reportsToOrg);
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
     * this method will take the document content, and populate a list of OrgReviewAttribute objects that also contain the rollup in
     * terms of organizational hierarchy as well.
     * 
     * @param docContent
     * @return a list of OrgReviewAttribute objects that are contained in the doc, or roll up to able by one that is contained in
     *         the document
     */
    private Set populateFromDocContent(DocumentType docType, DocumentContent docContent) {
        Set chartOrgValues = new HashSet();
        NodeList nodes = null;
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        try {
            String chart = null;
            String org = null;
            boolean isReport = ((Boolean) xpath.evaluate("wf:xstreamsafe('//report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                chart = xpath.evaluate("wf:xstreamsafe('//chart')", docContent.getDocument());
                org = xpath.evaluate("wf:xstreamsafe('//org')", docContent.getDocument());
            }
            else if (docType.getName().equals(ACCOUNT_DOC_TYPE) || docType.getName().equals(FIS_USER_DOC_TYPE) || docType.getName().equals(PROJECT_CODE_DOC_TYPE)) {
                chart = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "chartOfAccountsCode')", docContent.getDocument());
                org = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "organizationCode')", docContent.getDocument());
            }
            else if (docType.getName().equals(ORGANIZATION_DOC_TYPE)) {
                chart = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "finCoaCd')", docContent.getDocument());
                org = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "orgCd')", docContent.getDocument());
            }
            else if (docType.getName().equals(SUB_ACCOUNT_DOC_TYPE) || docType.getName().equals(ACCOUNT_DEL_DOC_TYPE) || docType.getName().equals(SUB_OBJECT_DOC_TYPE)) {
                // these documents don't have the organization code on them so it must be looked up
                chart = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "chartOfAccountsCode')", docContent.getDocument());
                String accountNumber = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "accountNumber')", docContent.getDocument());
                Account account = SpringServiceLocator.getAccountService().getByPrimaryId(chart, accountNumber);
                org = account.getOrganizationCode();
            }
            if (!StringUtils.isEmpty(chart) && !StringUtils.isEmpty(org)) {
                buildOrgReviewHierarchy(chartOrgValues, SpringServiceLocator.getOrganizationService().getByPrimaryId(chart, org));
            }
            else {
                String xpathExp = null;
                do {
                    if (KualiConstants.MAINTENANCE_DOC_TYPE.equalsIgnoreCase(docType.getName())) {
                        xpathExp = "wf:xstreamsafe('//kualiUser')";
                        break;
                    }
                    else if (KualiConstants.PROCUREMENT_CARD_DOC_TYPE.equalsIgnoreCase(docType.getName())) {
                        xpathExp = "wf:xstreamsafe('//org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine/account')";
                        break;
                    }
                    else if (docType.getName().equals(KualiConstants.BUDGET_ADJUSTMENT_DOC_TYPE)) {
                        xpathExp = "//org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine/account | //org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine/account";
                        break;
                    }
                    else if (KualiConstants.isSourceLineOnly(docType.getName())) {
                        xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account')";
                        break;
                    }
                    else if (KualiConstants.isTargetLineOnly(docType.getName())) {
                        xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account')";
                        break;
                    }
                    else if (KualiConstants.FINANCIAL_DOC_TYPE.equalsIgnoreCase(docType.getName())) {
                        xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account') | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account')";
                        break;
                    }
                    else if (KualiConstants.FINANCIAL_YEAR_END_DOC_TYPE.equalsIgnoreCase(docType.getName())) {
                        xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account') | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account')";
                        break;
                    }
                    else {
                        docType = docType.getParentDocType();
                    }
                } while (docType != null);

                if (xpathExp == null) {
                    throw new RuntimeException("Did not find expected document type.  Doc type used = " + docType.getName());
                }
                nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);

                for (int i = 0; i < nodes.getLength(); i++) {
                    Node accountingLineNode = nodes.item(i);
                    // TODO: xstreamsafe should be handling this, but is not, therefore this code block
                    String referenceString = xpath.evaluate("@reference", accountingLineNode);
                    if (!StringUtils.isEmpty(referenceString)) {
                        accountingLineNode = (Node) xpath.evaluate(referenceString, accountingLineNode, XPathConstants.NODE);
                    }
                    String finCoaCd = xpath.evaluate("./chartOfAccountsCode", accountingLineNode);
                    String orgCd = xpath.evaluate("./organizationCode", accountingLineNode);
                    if (!StringUtils.isEmpty(finCoaCd) && !StringUtils.isEmpty(orgCd)) {
                        Org organization = SpringServiceLocator.getOrganizationService().getByPrimaryId(finCoaCd, orgCd);
                        chartOrgValues.add(organization);
                        buildOrgReviewHierarchy(chartOrgValues, organization);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return chartOrgValues;
    }

    private String getOverrideCd(DocumentType docType, DocumentContent docContent) {
        try {
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            boolean isReport = ((Boolean) xpath.evaluate("wf:xstreamsafe('//report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                return xpath.evaluate("wf:xstreamsafe('//report/overrideCode')", docContent.getDocument());
            }
            String xpathExp = null;
            do {
                if (docType.getName().equals("KualiMaintenanceDocument")) {
                    return null;
                }
                else if (KualiConstants.isSourceLineOnly(docType.getName())) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/overrideCode')";
                    break;
                }
                else if (KualiConstants.isTargetLineOnly(docType.getName())) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/overrideCode')";
                    break;
                }
                else if (docType.getName().equals("KualiFinancialDocument")) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/overrideCode') | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/overrideCode')";
                    break;
                }
                else {
                    docType = docType.getParentDocType();
                }

            } while (docType != null);

            return xpath.evaluate(xpathExp, docContent.getDocument());

        }
        catch (Exception e) {
            LOG.error("Caught excpeption getting document override code", e);
            throw new RuntimeException(e);
        }

    }

    private Float getAmount(DocumentType docType, DocumentContent docContent) {
        try {
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            boolean isReport = ((Boolean) xpath.evaluate("wf:xstreamsafe('//report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                String floatVal = xpath.evaluate("wf:xstreamsafe('//report/totalDollarAmount')", docContent.getDocument());
                if (StringUtils.isNumeric(floatVal) && StringUtils.isNotEmpty(floatVal)) {
                    return new Float(floatVal);
                }
                else {
                    return new Float(0);
                }
            }
            String xpathExp = null;
            do {
                if (docType.getName().equals("KualiMaintenanceDocument")) {
                    return null;
                }
                else if (KualiConstants.isSourceLineOnly(docType.getName())) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/amount/value')";
                    break;
                }
                else if (KualiConstants.isTargetLineOnly(docType.getName())) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/amount/value')";
                    break;
                }
                else if (docType.getName().equals("KualiFinancialDocument")) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/amount/value') | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/amount/value')";
                    break;
                }
                else {
                    docType = docType.getParentDocType();
                }

            } while (docType != null);

            String value = xpath.evaluate("sum(" + xpathExp + ")", docContent.getDocument());
            if (value == null) {
                throw new RuntimeException("Didn't find amount for document " + docContent.getRouteContext().getDocument().getRouteHeaderId());
            }
            return new Float(value);
        }
        catch (Exception e) {
            LOG.error("Caught excpeption getting document amount", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * simple getter for the rule rows
     */
    public List getRuleRows() {
        return ruleRows;
    }

    /**
     * simple getter for the routing data rows
     */
    public List getRoutingDataRows() {
        return routingDataRows;
    }

    /**
     * simple getter for fincoacd
     * 
     * @return String
     */
    public String getFinCoaCd() {
        return this.finCoaCd;
    }

    /**
     * simple setter for fincoacd
     * 
     * @param finCoaCd
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    /**
     * simple getter for org code
     * 
     * @return String
     */
    public String getOrgCd() {
        return this.orgCd;
    }

    /**
     * simple setter for org code
     * 
     * @param orgCd
     */
    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
    }

    public String getTotalDollarAmount() {
        return totalDollarAmount;
    }

    public void setTotalDollarAmount(String totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }

    public String getOverrideCd() {
        return overrideCd;
    }

    public void setOverrideCd(String overrideCd) {
        this.overrideCd = overrideCd;
    }

    /**
     * simple getter for required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * simple setter for required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

}