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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;

public class KualiPurchaseOrderBudgetAttribute implements WorkflowAttribute {
    private static Logger LOG = Logger.getLogger(KualiPurchaseOrderBudgetAttribute.class);

    public static final String FIN_COA_CD_KEY = "fin_coa_cd";

    private boolean required = false;
    private String finCoaCd;
    private List rows;

    /**
     * No arg constructor
     */
    public KualiPurchaseOrderBudgetAttribute() {
        rows = new ArrayList();
        rows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, FIN_COA_CD_KEY));
    }

    /**
     * Constructs a new object given the chart code
     */
    public KualiPurchaseOrderBudgetAttribute(String finCoaCode) {
        this();
        this.finCoaCd = finCoaCode;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd())) {
            return "";
        }
        return new StringBuffer("<report><chart>").append(getFinCoaCd()).append("</chart></report>").toString();
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRoutingDataRows()
     */
    public List<Row> getRoutingDataRows() {
        return rows;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRuleRows()
     */
    public List<Row> getRuleRows() {
        return rows;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getRuleExtensionValues()
     */
    public List<RuleExtensionValue> getRuleExtensionValues() {
        List extensions = new ArrayList();
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, getFinCoaCd()));
        return extensions;
    }

    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRoutingData(java.util.Map)
     */
    public List validateRoutingData(Map paramMap) {
        return validateRuleData(paramMap);
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        setFinCoaCd(LookupUtils.forceUppercase(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, (String) paramMap.get(FIN_COA_CD_KEY)));
        String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        if (isRequired() && StringUtils.isBlank(getFinCoaCd())) {
            // value is blank but required
            errors.add(new WorkflowServiceErrorImpl(label + " is required.", "routetemplate.xmlattribute.error"));
        } else if (StringUtils.isBlank(getFinCoaCd())) {
            // check value for validity
            Chart chart = getChart(getFinCoaCd());
            if (chart == null) {
                errors.add(new WorkflowServiceErrorImpl(label + " entered is invalid.","routetemplate.xmlattribute.error"));
            }
        }
        return errors;
    }
    
    private Chart getChart(String finCoaCode) {
        return SpringServiceLocator.getChartService().getByPrimaryId(finCoaCd);
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

}
