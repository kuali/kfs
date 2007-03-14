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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;

/**
 * KualiChartAttribute which should be used when using charts to do routing
 * 
 * 
 */
public class KualiChartAttribute implements RoleAttribute, WorkflowAttribute {

    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiChartAttribute.class);

    private static final String FIN_COA_CD_KEY = "fin_coa_cd";

    private static final String UNIVERSITY_CHART_MANAGER_ROLE_KEY = "UNIVERSITY-CHART-MANAGER";

    private static final String UNIVERSITY_CHART_MANAGER_ROLE_LABEL = "University Chart Manager";

    private static final String CHART_MANAGER_ROLE_KEY = "CHART-MANAGER";

    private static final String CHART_MANAGER_ROLE_LABEL = "Chart Manager";

    private static final String CHART_ATTRIBUTE = "KUALI_CHART_ATTRIBUTE";

    private static final String CHART_REVIEW_FIN_COA_CD_KEY = "chart_review_fin_coa_cd";

    private static final String ROLE_STRING_DELIMITER = "~!~!~";

    private static final String MAINTAINABLE_PREFIX = "//newMaintainableObject/businessObject/";

    private static final String ORGANIZATION_DOC_TYPE = "KualiOrganizationMaintenanceDocument";

    private String finCoaCd;

    private boolean required;

    private List rows;

    /**
     * Constructs a KualiChartAttribute.java.
     */
    public KualiChartAttribute() {

        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY));
    }

    /**
     * Constructs a KualiChartAttribute.java.
     * 
     * @param finCoaCd - the chart code
     */
    public KualiChartAttribute(String finCoaCd) {

        this();
        this.finCoaCd = LookupUtils.forceUppercase(Chart.class, "chartOfAccountsCode", finCoaCd);

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
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#getRuleRows()
     */
    public List getRuleRows() {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#getRoutingDataRows()
     */
    public List getRoutingDataRows() {
        return rows;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd())) {
            return "";
        }
        return "<report><" + CHART_ATTRIBUTE + ">" + "<" + FIN_COA_CD_KEY + ">" + getFinCoaCd() + "</" + FIN_COA_CD_KEY + ">" + "</" + CHART_ATTRIBUTE + "></report>";
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#getRuleExtensionValues()
     */
    public List getRuleExtensionValues() {
        return Collections.EMPTY_LIST;
    }

    /**
     * Validates chart using database
     * 
     * @param finCoaCd
     * @return true if chart is valid
     */
    private boolean isValidChart(String finCoaCd) {
        return (getChart(finCoaCd) != null);
    }

    /**
     * Returns a Chart object from the code
     * 
     * @param finCoaCd
     * @return Chart
     */
    private Chart getChart(String finCoaCd) {
        return SpringServiceLocator.getChartService().getByPrimaryId(finCoaCd);
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#validateRoutingData(java.util.Map)
     */
    public List validateRoutingData(Map paramMap) {

        List errors = new ArrayList();
        this.finCoaCd = LookupUtils.forceUppercase(Chart.class, "chartOfAccountsCode", (String) paramMap.get(CHART_REVIEW_FIN_COA_CD_KEY));
        if (isRequired() && (this.finCoaCd == null || "".equals(finCoaCd))) {
            errors.add(new WorkflowServiceErrorImpl("Chart is required.", "routetemplate.chartorgattribute.chartorg.required"));
        }

        if (this.finCoaCd != null && !"".equals(finCoaCd)) {
            if (!isValidChart(finCoaCd)) {
                errors.add(new WorkflowServiceErrorImpl("Chart is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
            }
        }
        return errors;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        return Collections.EMPTY_LIST;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#getFieldConversions()
     */
    public List getFieldConversions() {
        List fieldConversions = new ArrayList();
        fieldConversions.add(new KeyLabelPair(FIN_COA_CD_KEY, CHART_REVIEW_FIN_COA_CD_KEY));
        return fieldConversions;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#setRequired(boolean)
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#isRequired()
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.RoleAttribute#getRoleNames()
     */
    public List getRoleNames() {
        List roles = new ArrayList();
        roles.add(new Role(this.getClass(), CHART_MANAGER_ROLE_KEY, CHART_MANAGER_ROLE_LABEL));
        roles.add(new Role(this.getClass(), UNIVERSITY_CHART_MANAGER_ROLE_KEY, UNIVERSITY_CHART_MANAGER_ROLE_LABEL));
        return roles;
    }

    /**
     * This method will build a string representation of a qualified role a qualified role is the role, with the corresponding
     * string values that further qualify the role to apply for a given object.
     * 
     * @param roleName
     * @param chart
     * @return String
     */
    private String getQualifiedRoleString(String roleName, String chart) {
        return roleName + ROLE_STRING_DELIMITER + chart;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.RoleAttribute#getQualifiedRoleNames(java.lang.String, java.lang.String)
     */
    public List getQualifiedRoleNames(String roleName, DocumentContent docContent) throws EdenUserNotFoundException {
        Set qualifiedRoleNames = new HashSet();
        if (CHART_MANAGER_ROLE_KEY.equals(roleName)) {
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            String chart = null;
            String chartXPath = null;

            try {
                // the report business is to support Routing Reports, which we
                // need to work on Chart
                boolean isReport = ((Boolean) xpath.evaluate("wf:xstreamsafe('//report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
                if (isReport) {
                    chartXPath = "wf:xstreamsafe('//report/chart')";
                }
                else if (KualiWorkflowUtils.ACCOUNT_DELEGATE_GLOBAL_DOC_TYPE.equals(docContent.getRouteContext().getDocument().getDocumentType().getName())) {
                    chartXPath = "wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "accountChangeDetails/list/org.kuali.module.chart.bo.AccountChangeDetail/chartOfAccountsCode')";
                }
                //  this is the typical path during normal workflow operation
                else { 
                    chartXPath = "wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "chartOfAccountsCode')";
                }
                chart = xpath.evaluate(chartXPath, docContent.getDocument());
            }
            catch (XPathExpressionException e) {
                throw new RuntimeException("Error evaluating xpath expression to locate chart.", e);
            }
            catch (Exception e) {
                throw new RuntimeException("An unexpected error occurred while trying to locate the Chart.", e);
            }

            if (StringUtils.isNotEmpty(chart)) {
                qualifiedRoleNames.add(getQualifiedRoleString(roleName, chart));
            }
            /*
             * Document doc = null; doc = XmlHelper.buildJDocument(docContent.getDocument()); List chartElements =
             * XmlHelper.findElements(doc.getRootElement(), CHART_ATTRIBUTE); for (Iterator iter = chartElements.iterator();
             * iter.hasNext();) { Element chartElement = (Element)iter.next();
             * qualifiedRoleNames.add(getQualifiedRoleString(roleName, chartElement.getChild(FIN_COA_CD_KEY).getText())); }
             */
        }
        else if (UNIVERSITY_CHART_MANAGER_ROLE_KEY.equals(roleName)) {
            qualifiedRoleNames.add(UNIVERSITY_CHART_MANAGER_ROLE_KEY);
        }
        return new ArrayList(qualifiedRoleNames);
    }

    /**
     * get the chart name from a qualified role string
     * 
     * @param qualifiedRole
     * @return String
     */
    private String getUnqualifiedChartFromString(String qualifiedRole) {
        return qualifiedRole.split(ROLE_STRING_DELIMITER)[1];
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.RoleAttribute#resolveQualifiedRole(edu.iu.uis.eden.routetemplate.attribute.RouteContext,
     *      java.lang.String, java.lang.String)
     */
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        List members = new ArrayList();
        Chart chart = null;
        if (CHART_MANAGER_ROLE_KEY.equals(roleName)) {
            members.add(new AuthenticationUserId(getChart(getUnqualifiedChartFromString(qualifiedRole)).getFinCoaManagerUniversal().getPersonUserIdentifier()));
        }
        else if (UNIVERSITY_CHART_MANAGER_ROLE_KEY.equals(roleName)) {
            members.add(new AuthenticationUserId(SpringServiceLocator.getChartService().getUniversityChart().getFinCoaManagerUniversal().getPersonUserIdentifier()));
        }
        return new ResolvedQualifiedRole(roleName, members);
    }

    public boolean isMatch(DocumentContent documentContent, List rules) {
        return true;
    }

}