/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.workflow.attribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.kuali.KualiSpringServiceLocator;
import org.kuali.workflow.KualiConstants;
import org.kuali.workflow.beans.KualiFiscalChart;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.RouteContext;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * This class is responsible for 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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

    private String finCoaCd;

    private boolean required;

    private List rows;

    /**
     * 
     * Constructs a KualiChartAttribute.java.
     *
     */
    public KualiChartAttribute() {

        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field("Chart", "", Field.TEXT, true, CHART_REVIEW_FIN_COA_CD_KEY, "", null, null));
        rows.add(new Row(fields));

    }

    /**
     * 
     * Constructs a KualiChartAttribute.java.
     * @param finCoaCd - the chart code
     */
    public KualiChartAttribute(String finCoaCd) {

        this();
        this.finCoaCd = finCoaCd;

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
        return "<" + CHART_ATTRIBUTE + ">" + "<" + FIN_COA_CD_KEY + ">" + getFinCoaCd() + "</" + FIN_COA_CD_KEY + ">" + "</" + CHART_ATTRIBUTE + ">";
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
        return (getKualiFiscalChart(finCoaCd) != null);
    }

    /**
     * Returns a KualiFiscalChart object from the code
     * @param finCoaCd
     * @return
     */
    private KualiFiscalChart getKualiFiscalChart(String finCoaCd) {
        KualiFiscalChart kualiFiscalChart = null;
        Connection conn = null;
        try {
            conn = KualiSpringServiceLocator.getDataSource().getConnection();

            String sql = "select * from CA_CHART_T where FIN_COA_CD = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, finCoaCd);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                kualiFiscalChart = new KualiFiscalChart(rs.getString("FIN_COA_CD"), rs.getString("RPTS_TO_FIN_COA_CD"), rs.getString("FIN_COA_MGR_SYS_ID"));
            }
        } catch (Exception e) {
            LOG.error("Error getting connection", e);
            throw new RuntimeException("An Error occurred during routing of this document", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    // Ignore
                }
                conn = null;
            }
        }
        return kualiFiscalChart;
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.WorkflowAttribute#validateRoutingData(java.util.Map)
     */
    public List validateRoutingData(Map paramMap) {

        List errors = new ArrayList();
        this.finCoaCd = (String)paramMap.get(CHART_REVIEW_FIN_COA_CD_KEY);
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
     * This method will build a string representation of a qualified role
     * a qualified role is the role, with the corresponding string values that
     * further qualify the role to apply for a given object.
     * 
     * @param roleName
     * @param chart
     * @return
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
            Document doc = null;
            doc = XmlHelper.buildJDocument(docContent.getDocument());
            List chartElements = XmlHelper.findElements(doc.getRootElement(), CHART_ATTRIBUTE);
            for (Iterator iter = chartElements.iterator(); iter.hasNext();) {
                Element chartElement = (Element)iter.next();
                qualifiedRoleNames.add(getQualifiedRoleString(roleName, chartElement.getChild(FIN_COA_CD_KEY).getText()));
            }
        } else if (UNIVERSITY_CHART_MANAGER_ROLE_KEY.equals(roleName)) {
            qualifiedRoleNames.add(UNIVERSITY_CHART_MANAGER_ROLE_KEY);
        }
        return new ArrayList(qualifiedRoleNames);
    }

    /**
     * get the chart name from a qualified role string
     * @param qualifiedRole
     * @return
     */
    private String getUnqualifiedChartFromString(String qualifiedRole) {
        return qualifiedRole.split(ROLE_STRING_DELIMITER)[1];
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.RoleAttribute#resolveQualifiedRole(edu.iu.uis.eden.routetemplate.attribute.RouteContext, java.lang.String, java.lang.String)
     */
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) throws EdenUserNotFoundException {

        List members = new ArrayList();

        Connection conn = null;
        try {
            conn = KualiSpringServiceLocator.getDataSource().getConnection();

            if (CHART_MANAGER_ROLE_KEY.equals(roleName)) {
                String chart = getUnqualifiedChartFromString(qualifiedRole);
                String kualiSystemId = null;
                String sql = "select FIN_COA_MGRUNVL_ID from CA_CHART_T where FIN_COA_CD = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, chart);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    kualiSystemId = rs.getString("FIN_COA_MGRUNVL_ID");
                } else {
                    LOG.warn("No Chart Manaqer retrieved for chart " + chart);
                }
                members.add(new AuthenticationUserId(KualiConstants.getNetworkId(conn, kualiSystemId)));
            } else if (UNIVERSITY_CHART_MANAGER_ROLE_KEY.equals(roleName)) {
                String kualiSystemId = null;
                String sql = "select FIN_COA_MGRUNVL_ID from CA_CHART_T where RPTS_TO_FIN_COA_CD = FIN_COA_CD";
                PreparedStatement ps = conn.prepareStatement(sql);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    kualiSystemId = rs.getString("FIN_COA_MGRUNVL_ID");
                } else {
                    LOG.warn("No University Chart Manager found.");
                }
                members.add(new AuthenticationUserId(KualiConstants.getNetworkId(conn, kualiSystemId)));
            }
        } catch (Exception e) {
            LOG.error("Error getting connection", e);
            throw new RuntimeException("An Error occurred during routing of this document", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e1) {
                    // Ignore
                }
                conn = null;
            }
        }
        return new ResolvedQualifiedRole(roleName, members);
    }

    public boolean isMatch(DocumentContent documentContent, List rules) {
        return true;
    }

}