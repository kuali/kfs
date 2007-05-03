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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;

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

    private static final String ACCOUNT_ATTRIBUTE = "KUALI_ACCOUNT_ATTRIBUTE";

    private String finCoaCd;

    private String accountNbr;
    
    private String subAccountNbr;
    
    private String orgCd;

    private boolean required;

    private List ruleRows;

    private List routingDataRows;

    /**
     * No arg constructor
     * TODO delyea - IMPLEMENT THIS
     */
    public KualiSubAccountAttribute() {
        ruleRows = new ArrayList();
        routingDataRows = new ArrayList();
    }

    /**
     * Constructor that takes chart, account, and total dollar amount
     * TODO delyea - IMPLEMENT THIS
     */
    public KualiSubAccountAttribute(String finCoaCd, String accountNbr, String subAccountNbr, String totalDollarAmount) {
        this();
//        this.finCoaCd = LookupUtils.forceUppercase(SubAccount.class, "chartOfAccountsCode", finCoaCd);
//        this.accountNbr = LookupUtils.forceUppercase(SubAccount.class, "accountNumber", accountNbr);
//        this.subAccountNbr = LookupUtils.forceUppercase(SubAccount.class, "subAccountNumer", subAccountNbr);
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getSubAccountNbr()) || ( (Utilities.isEmpty(getSubAccountNbr())) && (Utilities.isEmpty(getOrgCd())) ) ) {
            return "";
        }
        StringBuffer prefix = new StringBuffer("<report><chart>").append(getFinCoaCd()).append("</chart>");
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
     * TODO delyea - IMPLEMENT THIS
     */
    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
//        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, this.finCoaCd));
//        extensions.add(new RuleExtensionValue(ORG_CD_KEY, this.orgCd));
//        if (!StringUtils.isBlank(this.fromAmount)) {
//            extensions.add(new RuleExtensionValue(FROM_AMOUNT_KEY, this.fromAmount));
//        }
//        if (!StringUtils.isBlank(this.toAmount)) {
//            extensions.add(new RuleExtensionValue(TO_AMOUNT_KEY, this.toAmount));
//        }
//        if (!StringUtils.isBlank(this.overrideCd)) {
//            extensions.add(new RuleExtensionValue(OVERRIDE_CD_KEY, this.overrideCd));
//        }
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
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRoutingData(java.util.Map)
     * TODO delyea - IMPLEMENT THIS
     */
    public List validateRoutingData(Map arg0) {
        List errors = new ArrayList();
//        this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", (String) paramMap.get(FIN_COA_CD_KEY));
//        this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", (String) paramMap.get(ORG_CD_KEY));
//        this.totalDollarAmount = (String) paramMap.get(TOTAL_AMOUNT_KEY);
//        this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", (String) paramMap.get(OVERRIDE_CD_KEY));
//        if (isRequired()) {
//            validateOrg(errors);
//            if (!StringUtils.isNumeric(this.totalDollarAmount)) {
//                errors.add(new WorkflowServiceErrorImpl("Total Amount is invalid.", ""));
//            }
//        }
        return errors;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     * TODO delyea - IMPLEMENT THIS
     */
    public List validateRuleData(Map arg0) {
        List errors = new ArrayList();
//        if (isRequired()) {
//            this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", (String) paramMap.get(FIN_COA_CD_KEY));
//            this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", (String) paramMap.get(ORG_CD_KEY));
//            this.fromAmount = (String) paramMap.get(FROM_AMOUNT_KEY);
//            this.toAmount = (String) paramMap.get(TO_AMOUNT_KEY);
//            this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", (String) paramMap.get(OVERRIDE_CD_KEY));
//            validateOrg(errors);
//            if (StringUtils.isNotBlank(toAmount) && !StringUtils.isNumeric(toAmount)) {
//                errors.add(new WorkflowServiceErrorImpl("To Amount is invalid.", "routetemplate.dollarrangeattribute.toamount.invalid"));
//            }
//            if (StringUtils.isNotBlank(fromAmount) && !StringUtils.isNumeric(fromAmount)) {
//                errors.add(new WorkflowServiceErrorImpl("From Amount is invalid.", "routetemplate.dollarrangeattribute.fromamount.invalid"));
//            }
//        }
        return errors;
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
//        DocumentType documentType = routeContext.getDocument().getDocumentType();
//        Set chartOrgValues = populateFromDocContent(documentType, routeContext.getDocumentContent(), routeContext);
//        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
//            RuleBaseValues rule = (RuleBaseValues) iterator.next();
//            List ruleExtensions = rule.getRuleExtensions();
//            this.finCoaCd = LookupUtils.forceUppercase(Org.class, "chartOfAccountsCode", getRuleExtentionValue(FIN_COA_CD_KEY, ruleExtensions));
//            this.orgCd = LookupUtils.forceUppercase(Org.class, "organizationCode", getRuleExtentionValue(ORG_CD_KEY, ruleExtensions));
//            this.fromAmount = getRuleExtentionValue(FROM_AMOUNT_KEY, ruleExtensions);
//            this.toAmount = getRuleExtentionValue(TO_AMOUNT_KEY, ruleExtensions);
//            this.overrideCd = LookupUtils.forceUppercase(SourceAccountingLine.class, "overrideCode", getRuleExtentionValue(OVERRIDE_CD_KEY, ruleExtensions));
//            if (ruleMatches(rule, chartOrgValues, routeContext)) {
//                filteredRules.add(rule);
//            }
//        }
//        Collections.sort(filteredRules, new ChartOrgRuleComparator(chartOrgValues));
        return filteredRules;
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
