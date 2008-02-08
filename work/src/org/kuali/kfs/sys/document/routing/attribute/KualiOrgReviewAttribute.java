/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.attribute.WorkflowLookupableImpl;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.OrganizationService;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.MassRuleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * KualiOrgReviewAttribute should be used when using Orgs and thier inner details to do routing.
 */
public class KualiOrgReviewAttribute implements WorkflowAttribute, MassRuleAttribute {

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

    private static final String DOCUMENT_CHART_ORG_VALUES_KEY = "organizations";

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

        // fields = new ArrayList();
        // fields.add(new Field("Total Amount", "", Field.TEXT, true, TOTAL_AMOUNT_KEY, "", null, null, TOTAL_AMOUNT_KEY));
        // routingDataRows.add(new Row(fields));
        routingDataRows.add(KualiWorkflowUtils.buildTextRow(DocumentHeader.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, TOTAL_AMOUNT_KEY));
    }

    /**
     * This method produces a chart row.
     *
     * @return
     */
    

    public edu.iu.uis.eden.lookupable.Row getChartRow() {
        return KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY);
    }

    /**
     * This method produces an org row.
     *
     * @return
     */
   

    public edu.iu.uis.eden.lookupable.Row getOrgRow() {
        Map fieldConversionMap = new HashMap();
        fieldConversionMap.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY);
        return KualiWorkflowUtils.buildTextRowWithLookup(Org.class, KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME, ORG_CD_KEY, fieldConversionMap);
    }

    /**
     * This method produces an overrideCode row.
     *
     * @return
     */
    

    public edu.iu.uis.eden.lookupable.Row getOverrideCodeRow() {
        java.lang.reflect.Field[] overrideCodes = AccountingLineOverride.CODE.class.getDeclaredFields();
        Map optionMap = new LinkedHashMap<String,String>();
        for (int i=0;i<overrideCodes.length ;i++){
            try{
                optionMap.put(overrideCodes[i].get(null), overrideCodes[i].getName());
            }catch (Exception e){
                LOG.error("Error occured reading override codes for dropdown "+e);
            }
        }
        return KualiWorkflowUtils.buildDropdownRow(SourceAccountingLine.class, "overrideCode", OVERRIDE_CD_KEY, optionMap, true);
    }

    /**
     * constructor that takes the chart, org, which calls the no arg constructor
     *
     * @param finCoaCd
     * @param orgCd
     */
    public KualiOrgReviewAttribute(String finCoaCd, String orgCd) {
        this();
        this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", finCoaCd);
        this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", orgCd);
    }

    public List getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, this.finCoaCd));
        extensions.add(new RuleExtensionValue(ORG_CD_KEY, this.orgCd));
        if (!StringUtils.isBlank(this.fromAmount)) {
            extensions.add(new RuleExtensionValue(FROM_AMOUNT_KEY, this.fromAmount));
        }
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
        this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", (String) paramMap.get(FIN_COA_CD_KEY));
        this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", (String) paramMap.get(ORG_CD_KEY));
        this.fromAmount = (String) paramMap.get(FROM_AMOUNT_KEY);
        this.toAmount = (String) paramMap.get(TO_AMOUNT_KEY);
        this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", (String) paramMap.get(OVERRIDE_CD_KEY));
        if (isRequired()) {
            validateOrg(errors);
            if (StringUtils.isNotBlank(toAmount) && !StringUtils.isNumeric(toAmount)) {
                errors.add(new WorkflowServiceErrorImpl("To Amount is invalid.", "routetemplate.dollarrangeattribute.toamount.invalid"));
            }
            if (StringUtils.isNotBlank(fromAmount) && !StringUtils.isNumeric(fromAmount)) {
                errors.add(new WorkflowServiceErrorImpl("From Amount is invalid.", "routetemplate.dollarrangeattribute.fromamount.invalid"));
            }
        }
        return errors;
    }

    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", (String) paramMap.get(FIN_COA_CD_KEY));
        this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", (String) paramMap.get(ORG_CD_KEY));
        this.totalDollarAmount = (String) paramMap.get(TOTAL_AMOUNT_KEY);
        this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", (String) paramMap.get(OVERRIDE_CD_KEY));
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
        else {
            Org org = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(finCoaCd, orgCd);
            if (org == null) {
                errors.add(new WorkflowServiceErrorImpl("Chart/org is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
            }
        }
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getOrgCd())) {
            return "";
        }
        return new StringBuffer(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_PREFIX + "<chart>").append(getFinCoaCd()).append("</chart><org>").append(getOrgCd()).append("</org><totalDollarAmount>").append(getTotalDollarAmount()).append("</totalDollarAmount><overrideCode>").append(getOverrideCd()).append("</overrideCode>" + KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_SUFFIX).toString();
    }

    public String getAttributeLabel() {
        return "";
    }

    /**
     * Actual matching logic is handled in filterNonMatchingRules where the List of rules is narrowed down to those that should
     * fire.
     *
     * @see #filterNonMatchingRules(RouteContext, List)
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isMatch(java.lang.String, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        return true;
    }

    /**
     * Filters the List of Rules by those that will match and then sorts the List with those that have Orgs at the bottom of the
     * hierarchy first. This will allow for requests generated by rules at the bottom of the hierarchy to be activated first. We've
     * collapsed this method and isMatch into one to allow for optimal sorting (i.e. we only sort the rules that actually match and
     * don't have to fetch every Org in the hierarchy to sort the full List of Rules).
     */
    public List filterNonMatchingRules(RouteContext routeContext, List rules) {
        List filteredRules = new ArrayList();
        DocumentType documentType = routeContext.getDocument().getDocumentType();
        Set chartOrgOverrideValues = populateFromDocContent(documentType, routeContext.getDocumentContent(), routeContext);
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            List ruleExtensions = rule.getRuleExtensions();
            this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", getRuleExtentionValue(FIN_COA_CD_KEY, ruleExtensions));
            this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", getRuleExtentionValue(ORG_CD_KEY, ruleExtensions));
            this.fromAmount = getRuleExtentionValue(FROM_AMOUNT_KEY, ruleExtensions);
            this.toAmount = getRuleExtentionValue(TO_AMOUNT_KEY, ruleExtensions);
            this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", getRuleExtentionValue(OVERRIDE_CD_KEY, ruleExtensions));
            if (ruleMatches(rule, chartOrgOverrideValues, routeContext)) {
                filteredRules.add(rule);
            }
        }
        Collections.sort(filteredRules, new ChartOrgRuleComparator(chartOrgOverrideValues));
        return filteredRules;
    }

    /**
     * Determines if the given Rule matches the document data by comparing the Org, total dollar amount, and override code.
     */
    protected boolean ruleMatches(RuleBaseValues rule, Set chartOrgOverrideValues, RouteContext routeContext) {
        boolean matchesOrg = false;
        for (Iterator iter = chartOrgOverrideValues.iterator(); iter.hasNext();) {
            OrgOverride orgOverride = (OrgOverride) iter.next();
            Org org = orgOverride.getOrg();
            if (org.getChartOfAccountsCode().equals(this.getFinCoaCd()) && org.getOrganizationCode().equals(this.getOrgCd())) {
                if (this.overrideCd != null){
                    String docOverrideCd = orgOverride.getOverrideCd();
                    //If the doc doesn't have an override code, match even if the rule specifies an override code
                    if (this.overrideCd.equalsIgnoreCase(docOverrideCd)|| StringUtils.isEmpty(docOverrideCd)){
                        matchesOrg = true;
                        break;
                    }
                    continue;
                }
                matchesOrg = true;
                break;
            }
        }

        if (!matchesOrg) {
            return false;
        }

        Float documentAmount = getAmount(routeContext.getDocument().getDocumentType(), routeContext.getDocumentContent());
        if (documentAmount != null) {
            Float ruleFromAmount = null;
            Float ruleToAmount = null;
            if (!StringUtils.isBlank(fromAmount)) {
                ruleFromAmount = new Float(fromAmount);
                if (ruleFromAmount.floatValue() > documentAmount.floatValue()) {
                    return false;
                }
            }
            if (!StringUtils.isBlank(toAmount)) {
                ruleToAmount = new Float(toAmount);
                if (ruleToAmount.floatValue() < documentAmount.floatValue()) {
                    return false;
                }
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
    private void buildOrgReviewHierarchy(int counter, Set chartOrgOverrideSet, OrgOverride startOrgOverride) {
        LOG.info("buildOrgReviewHierarchy iteration: " + counter);
        String overrideCd = startOrgOverride.getOverrideCd();
        Org startOrg = startOrgOverride.getOrg();
        // this will cause NPEs, so we dont let it through
        if (startOrg == null) {
            throw new IllegalArgumentException("Parameter value for startOrg passed in was null.");
        }
        
        // we're done if the reportsToOrg is the same as the Org, ie we're at the top of the Org hiearchy
        if (startOrg.getChartOfAccountsCode().equalsIgnoreCase(startOrg.getReportsToChartOfAccountsCode())) {
            if (startOrg.getOrganizationCode().equalsIgnoreCase(startOrg.getReportsToOrganizationCode())) {
                return;
            }
        }
        Org reportsToOrg = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(startOrg.getReportsToChartOfAccountsCode(), startOrg.getReportsToOrganizationCode());
        if (reportsToOrg == null) {
            throw new RuntimeException("Org " + startOrg.getChartOfAccountsCode() + "-" + startOrg.getOrganizationCode() + " has a reportsToOrganization (" + startOrg.getReportsToChartOfAccountsCode() + "-" + startOrg.getReportsToOrganizationCode() + ") " + " that does not exist in the system.");
        }
        OrgOverride reportsToOrgOverride = new OrgOverride(reportsToOrg, overrideCd);
        chartOrgOverrideSet.add(reportsToOrgOverride);
        buildOrgReviewHierarchy(++counter, chartOrgOverrideSet, reportsToOrgOverride);
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
    private Set populateFromDocContent(DocumentType docType, DocumentContent docContent, RouteContext routeContext) {
        Set chartOrgOverrideValues = null;
        if (routeContext.getParameters().containsKey(DOCUMENT_CHART_ORG_VALUES_KEY)) {
            chartOrgOverrideValues = (Set) routeContext.getParameters().get(DOCUMENT_CHART_ORG_VALUES_KEY);
        }
        else {
            chartOrgOverrideValues = new HashSet();
            NodeList nodes = null;
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            try {
                String chart = null;
                String org = null;
                boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
                if (isReport) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("chart").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("org").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                else if (KualiWorkflowUtils.ACCOUNT_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.FIS_USER_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.PROJECT_CODE_DOC_TYPE.equals(docType.getName())) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                else if (KualiWorkflowUtils.ORGANIZATION_DOC_TYPE.equals(docType.getName())) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append("finCoaCd").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append("orgCd").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                else if (KualiWorkflowUtils.SUB_ACCOUNT_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.ACCOUNT_DEL_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.SUB_OBJECT_DOC_TYPE.equals(docType.getName())) {
                    // these documents don't have the organization code on them so it must be looked up
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    String accountNumber = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.NEW_MAINTAINABLE_PREFIX).append(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    Account account = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(chart, accountNumber);
                    org = account.getOrganizationCode();
                }
                else if (KualiWorkflowUtils.C_G_AWARD_DOC_TYPE.equals(docType.getName()) || KualiWorkflowUtils.C_G_PROPOSAL_DOC_TYPE.equals(docType.getName())) {
                    chart = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("routingChart").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                    org = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("routingOrg").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                }
                else if (KualiWorkflowUtils.USER_DOC_TYPE.equals(docType.getName())) {
                    // TODO: fix this xpath to use the central stuff after document xml is modified to remove string element
                    chart = xpath.evaluate("//newMaintainableObject/businessObject/moduleProperties/entry[string=\"chart\"]/map/entry[string=\"chartOfAccountsCode\"]/string[2]", docContent.getDocument());
                    org = xpath.evaluate("//newMaintainableObject/businessObject/moduleProperties/entry[string=\"chart\"]/map/entry[string=\"organizationCode\"]/string[2]", docContent.getDocument());
                }
                else if (KualiWorkflowUtils.CHART_ORG_WORKGROUP_DOC_TYPE.equals(docType.getName())) {
                    chart = xpath.evaluate("//workgroup/extensions/extension/data[@key='" + KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME + "']", docContent.getDocument());
                    org = xpath.evaluate("//workgroup/extensions/extension/data[@key='" + KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME + "']", docContent.getDocument());
                }
                if (!StringUtils.isEmpty(chart) && !StringUtils.isEmpty(org)) {
                    Org docOrg = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(chart, org);
                    if (docOrg == null) {
                        throw new RuntimeException("Org declared on the document cannot be found in the system, routing cannot continue.");
                    }
                    OrgOverride docOrgOverride = new OrgOverride(docOrg, null);
                    // possibly duplicate add, but this is safe in a HashSet
                    chartOrgOverrideValues.add(docOrgOverride);
                    buildOrgReviewHierarchy(0, chartOrgOverrideValues, docOrgOverride);
                }
                else {
                    // now look at the global documents
                    List<Org> globalDocOrgs = getGlobalDocOrgs(docType.getName(), xpath, docContent);
                    for (Org globalOrg : globalDocOrgs) {
                        OrgOverride globalOrgOverride = new OrgOverride(globalOrg, null);
                        chartOrgOverrideValues.add(globalOrg);
                        buildOrgReviewHierarchy(0, chartOrgOverrideValues, globalOrgOverride);
                    }
                    String xpathExp = null;
                    if (KualiWorkflowUtils.isMaintenanceDocument(docType)) {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("kualiUser").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    else if (KualiWorkflowUtils.KRA_BUDGET_DOC_TYPE.equalsIgnoreCase(docType.getName()) || KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE.equalsIgnoreCase(docType.getName())) {
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("chartOrg").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                    }
                    else {
                        if (KualiWorkflowUtils.isSourceLineOnly(docType.getName())) {
                            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                        }
                        else if (KualiWorkflowUtils.isTargetLineOnly(docType.getName())) {
                            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                        }
                        else {
                            xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getSourceAccountingLineClassName(docType.getName())).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).append(" | ").append(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.getTargetAccountingLineClassName(docType.getName())).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                        }
                    }
                    nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);
                    for (int i = 0; i < nodes.getLength(); i++) {
                        Node accountingLineNode = nodes.item(i);
                        // TODO: xstreamsafe should be handling this, but is not, therefore this code block
                        String referenceString = xpath.evaluate("@reference", accountingLineNode);
                        if (!StringUtils.isEmpty(referenceString)) {
                            accountingLineNode = (Node) xpath.evaluate(referenceString, accountingLineNode, XPathConstants.NODE);
                        }
                        /**Because the override code is an attribute of the accounting line and the chart and org are attributes of the
                         * account on the line, the override code must be determined before the node is stripped down to represent an account,
                         * when it still has accounting line data.  It must be done at this point so that the override code is correctly
                         * associated with the related chart and org.
                         */
                        String overrideCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "/overrideCode", accountingLineNode);
                        xpathExp = new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append("account").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString();
                        Node accountNode = (Node) xpath.evaluate(xpathExp, accountingLineNode,XPathConstants.NODE);
                        String finCoaCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, accountNode);
                        String orgCd = xpath.evaluate(KualiWorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME, accountNode);
                        if (!StringUtils.isEmpty(finCoaCd) && !StringUtils.isEmpty(orgCd)) {
                            OrgOverride organization = new OrgOverride(SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(finCoaCd, orgCd), overrideCd);
                            chartOrgOverrideValues.add(organization);
                            buildOrgReviewHierarchy(0, chartOrgOverrideValues, organization);
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            routeContext.getParameters().put(DOCUMENT_CHART_ORG_VALUES_KEY, chartOrgOverrideValues);
        }
        return chartOrgOverrideValues;
    }

    private List<Org> getGlobalDocOrgs(String docTypeName, XPath xpath, DocumentContent docContent) throws Exception {
        List<Org> orgs = new ArrayList<Org>();
        if (KualiWorkflowUtils.ACCOUNT_CHANGE_DOC_TYPE.equals(docTypeName) || KualiWorkflowUtils.ACCOUNT_DELEGATE_GLOBAL_DOC_TYPE.equals(docTypeName) || KualiWorkflowUtils.SUB_OBJECT_CODE_CHANGE_DOC_TYPE.equals(docTypeName)) {
            NodeList accountGlobalDetails = (NodeList) xpath.evaluate(KualiWorkflowUtils.ACCOUNT_GLOBAL_DETAILS_XPATH, docContent.getDocument(), XPathConstants.NODESET);
            for (int index = 0; index < accountGlobalDetails.getLength(); index++) {
                Element accountGlobalDetail = (Element) accountGlobalDetails.item(index);
                String chartOfAccountsCode = getChildElementValue(accountGlobalDetail, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
                String accountNumber = getChildElementValue(accountGlobalDetail, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME);
                Account account = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(chartOfAccountsCode, accountNumber);
                orgs.add(account.getOrganization());
            }
        }
        else if (KualiWorkflowUtils.ORG_REVERSION_CHANGE_DOC_TYPE.equals(docTypeName)) {
            NodeList orgReversionChangeDetails = (NodeList) xpath.evaluate(KualiWorkflowUtils.ORG_REVERSION_GLOBALS_XPATH, docContent.getDocument(), XPathConstants.NODESET);
            for (int index = 0; index < orgReversionChangeDetails.getLength(); index++) {
                Element orgReversionChangeDetail = (Element) orgReversionChangeDetails.item(index);
                String chartOfAccountsCode = getChildElementValue(orgReversionChangeDetail, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
                String orgCode = getChildElementValue(orgReversionChangeDetail, KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME);
                Org org = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(chartOfAccountsCode, orgCode);
                orgs.add(org);
            }
        }
        return orgs;
    }

    private String getChildElementValue(Element element, String childTagName) {
        NodeList nodes = element.getChildNodes();
        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (Node.ELEMENT_NODE == node.getNodeType() && node.getNodeName().equals(childTagName)) {
                return node.getFirstChild().getNodeValue();
            }
        }
        return null;
    }


    /**
     * Method returns the absolute value of the document's total
     */
    private Float getAmount(DocumentType docType, DocumentContent docContent) {
        try {
            Document doc = docContent.getDocument();
            XPath xpath = KualiWorkflowUtils.getXPath(doc);
            boolean isReport = ((Boolean) xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
            if (isReport) {
                String floatVal = xpath.evaluate(new StringBuffer(KualiWorkflowUtils.XSTREAM_SAFE_PREFIX).append(KualiWorkflowUtils.XSTREAM_MATCH_ANYWHERE_PREFIX).append(KualiWorkflowUtils.XML_REPORT_DOC_CONTENT_XPATH_PREFIX).append("/totalDollarAmount").append(KualiWorkflowUtils.XSTREAM_SAFE_SUFFIX).toString(), docContent.getDocument());
                if (StringUtils.isNumeric(floatVal) && StringUtils.isNotEmpty(floatVal)) {
                    return new Float(floatVal);
                }
                else {
                    return new Float(0);
                }
            }
            if (KualiWorkflowUtils.isMaintenanceDocument(docType)) {
                return null;
            }
            else if (KualiWorkflowUtils.KRA_BUDGET_DOC_TYPE.equalsIgnoreCase(docType.getName()) || KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE.equalsIgnoreCase(docType.getName())) {
                return null;
            }
            KualiDecimal value = KualiWorkflowUtils.getFinancialDocumentTotalAmount(doc);
            if (ObjectUtils.isNull(value)) {
                throw new RuntimeException("Didn't find amount for document " + docContent.getRouteContext().getDocument().getRouteHeaderId());
            }
            return value.abs().floatValue();
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

    /**
     * Sorts RuleBaseValues by Orgs with the Orgs at the bottom of the hierarchy first.
     */
    private class ChartOrgRuleComparator implements Comparator<RuleBaseValues> {

        private Set chartOrgOverrideSet;

        public ChartOrgRuleComparator(Set chartOrgOverrideSet) {
            this.chartOrgOverrideSet = chartOrgOverrideSet;
        }

        public int compare(RuleBaseValues rule1, RuleBaseValues rule2) {
            String chart1 = rule1.getRuleExtensionValue(FIN_COA_CD_KEY).getValue();
            String chart2 = rule2.getRuleExtensionValue(FIN_COA_CD_KEY).getValue();
            String org1 = rule1.getRuleExtensionValue(ORG_CD_KEY).getValue();
            String org2 = rule2.getRuleExtensionValue(ORG_CD_KEY).getValue();
            Org docOrg1 = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(chart1, org1);
            Org docOrg2 = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(chart2, org2);
            int distanceFromRoot1 = getDistanceFromRoot(docOrg1);
            int distanceFromRoot2 = getDistanceFromRoot(docOrg2);
            if (distanceFromRoot1 == distanceFromRoot2) {
                // if they are the same, compare names
                return (chart1 + "-" + org1).compareTo(chart2 + "-" + org2);
            }
            // sort descending
            return new Integer(distanceFromRoot2).compareTo(new Integer(distanceFromRoot1));
        }

        private int getDistanceFromRoot(Org org) {
            if (org.getChartOfAccountsCode().equalsIgnoreCase(org.getReportsToChartOfAccountsCode())) {
                if (org.getOrganizationCode().equalsIgnoreCase(org.getReportsToOrganizationCode())) {
                    return 0;
                }
            }
            Org reportsToOrg = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(org.getReportsToChartOfAccountsCode(), org.getReportsToOrganizationCode());
            if (reportsToOrg == null) {
                throw new RuntimeException("Org " + org.getChartOfAccountsCode() + "-" + org.getOrganizationCode() + " has a reportsToOrganization (" + org.getReportsToChartOfAccountsCode() + "-" + org.getReportsToOrganizationCode() + ") " + " that does not exist in the system.");
            }
            return 1 + getDistanceFromRoot(reportsToOrg);
        }

    }
    
    private class OrgOverride{
        String overrideCd;
        Org org;

        OrgOverride(Org org){
            this.org=org;      
        }
        
        OrgOverride(Org org, String overrideCd){
            this.org=org;
            this.overrideCd=overrideCd;
        }
        public Org getOrg() {
            return org;
        }
        public void setOrg(Org org) {
            this.org = org;
        }
        
        public String getOverrideCd() {
            return overrideCd;
        }

        public void setOverrideCd(String overrideCd) {
            this.overrideCd = overrideCd;
        }
    }

}