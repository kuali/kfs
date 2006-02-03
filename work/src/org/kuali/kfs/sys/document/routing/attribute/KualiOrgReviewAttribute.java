package org.kuali.workflow.attribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.kuali.KualiSpringServiceLocator;
import org.kuali.workflow.beans.KualiFiscalOrganization;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;


/**
 * 
 * @author bmcgough
 *
 * The OrgReviewAttribute is responsible for org based review rollup
 * 
 */
public class KualiOrgReviewAttribute implements WorkflowAttribute {
    
    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiOrgReviewAttribute.class);

    private List rows;
    private static final String FIN_COA_CD_KEY = "fin_coa_cd";
    private static final String ORG_REVIEW_FIN_COA_CD_KEY = "org_review_fin_coa_cd";
    
    private static final String ORG_CD_KEY = "org_cd";
    private static final String ORG_REVIEW_ORG_CD_KEY = "org_review_org_cd";
    
    private static final String ORG_REVIEW_ATTRIBUTE = "KUALI_ORG_REVIEW_ATTRIBUTE";

    private Field chart;
    private Field org;
    
    private String finCoaCd;
    private String orgCd;
    private String totalDollarAmount;
    
    private boolean required;

    /**
     * no arg constructor, which will initialize the fields and rows of the attribute
     *
     */
    public KualiOrgReviewAttribute() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field("Chart", "", Field.TEXT, true, ORG_REVIEW_FIN_COA_CD_KEY, "", null, null, FIN_COA_CD_KEY));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Org", "", Field.TEXT, true, ORG_REVIEW_ORG_CD_KEY, "", null, null, ORG_CD_KEY));
        rows.add(new Row(fields));
        
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

        if (finCoaCd != null && !finCoaCd.equals("")) {
            RuleExtensionValue extensionFinCoaCd = new RuleExtensionValue();
            extensionFinCoaCd.setKey(FIN_COA_CD_KEY);
            extensionFinCoaCd.setValue(this.finCoaCd);
            extensions.add(extensionFinCoaCd);
        }
        if (orgCd != null && !orgCd.equals("")) {
            RuleExtensionValue extensionOrgCd = new RuleExtensionValue();
            extensionOrgCd.setKey(ORG_CD_KEY);
            extensionOrgCd.setValue(this.orgCd);
            extensions.add(extensionOrgCd);
        }
        return extensions;
    }

    public List validateRoutingData(Map paramMap) {
        
        List errors = new ArrayList();
        this.finCoaCd = (String) paramMap.get(ORG_REVIEW_FIN_COA_CD_KEY);
        this.orgCd = (String) paramMap.get(ORG_REVIEW_ORG_CD_KEY);
        if (isRequired() && (this.finCoaCd == null || "".equals(finCoaCd) || (this.orgCd == null || "".equals(orgCd)))) {
            errors.add(new WorkflowServiceErrorImpl("Chart/org is required.", "routetemplate.chartorgattribute.chartorg.required"));
        } else if ((this.finCoaCd != null && !"".equals(finCoaCd) && ((this.orgCd == null || "".equals(orgCd)))) || ((this.finCoaCd == null || "".equals(finCoaCd)) && this.orgCd != null && !"".equals(orgCd))) {
            errors.add(new WorkflowServiceErrorImpl("Chart/org is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
        }
        
        if (this.finCoaCd != null && !"".equals(finCoaCd) && this.orgCd != null && !"".equals(orgCd)) {
            if (!isValidOrg(finCoaCd, orgCd)) {
                errors.add(new WorkflowServiceErrorImpl("Chart/org is invalid.", "routetemplate.chartorgattribute.chartorg.invalid"));
            }
        }
        return errors;
    }

    /**
     * Validates org using database
     * 
     * @param finCoaCd
     * @param orgCd
     * @return true if org is valid
     */
    private boolean isValidOrg(String finCoaCd, String orgCd) {
        boolean isValidOrg = false;
        
        isValidOrg = (getKualiFiscalOrganization(finCoaCd, orgCd) != null);

        return isValidOrg;
    }

    /**
     * @param finCoaCd
     * @param orgCd
     * @param isValidOrg
     * @return
     */
    private KualiFiscalOrganization getKualiFiscalOrganization(String finCoaCd, String orgCd) {
        KualiFiscalOrganization kualiFiscalOrganization = null;
        
        Context ctx = null;
        
        try {
            ctx = new InitialContext();
        } catch (NamingException ne) {
            LOG.warn("naming exception encountered",ne);
            throw new RuntimeException("Could not connect with Database");
        }
        
        Connection conn = null;
        try {
            conn = KualiSpringServiceLocator.getDataSource().getConnection();
            
            String sql = "select FIN_COA_CD, ORG_CD, RPTS_TO_FIN_COA_CD, RPTS_TO_ORG_CD  from CA_ORG_T where FIN_COA_CD = ? and ORG_CD = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, finCoaCd);
            ps.setString(2, orgCd);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                kualiFiscalOrganization = new KualiFiscalOrganization(rs.getString("FIN_COA_CD"), rs.getString("ORG_CD"), rs.getString("RPTS_TO_FIN_COA_CD"), rs.getString("RPTS_TO_ORG_CD"));
            }
        } catch (Exception e) {
            LOG.error("Error getting connection",e);
            throw new RuntimeException("An Error occurred during routing of this document",e);
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
        return kualiFiscalOrganization;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        return validateRoutingData(paramMap);
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
	    if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getOrgCd())) {
	        return "";
	    }
	    return "<" + ORG_REVIEW_ATTRIBUTE + ">" + "<" + FIN_COA_CD_KEY + ">" + getFinCoaCd() + "</" + FIN_COA_CD_KEY + ">" + "<" + ORG_CD_KEY + ">" + getOrgCd() + "</" + ORG_CD_KEY + ">" + "</" + ORG_REVIEW_ATTRIBUTE + ">";
    }
    public String getAttributeLabel(){
        return "";
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#isMatch(java.lang.String, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        for (Iterator iter = ruleExtensions.iterator(); iter.hasNext();) {
            RuleExtension extension = (RuleExtension) iter.next();
            if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(this.getClass().getName())) {
                for (Iterator iterator = extension.getExtensionValues().iterator(); iterator.hasNext();) {
                    RuleExtensionValue value = (RuleExtensionValue) iterator.next();
                    if (value.getKey().equals(FIN_COA_CD_KEY)) {
                        setFinCoaCd(value.getValue());
                    }
                    if (value.getKey().equals(ORG_CD_KEY)) {
                        setOrgCd(value.getValue());
                    }
                }
            }
        }

        Set chartOrgValues = populateFromDocContent(docContent);
        for (Iterator iter = chartOrgValues.iterator(); iter.hasNext();) {
            KualiOrgReviewAttribute attribute = (KualiOrgReviewAttribute) iter.next();
            if (attribute.getFinCoaCd().equals(this.getFinCoaCd()) && attribute.getOrgCd().equals(this.getOrgCd())) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method is a recursive method that will retrive reports to orgs to build up the hierarchy
     * of organizations
     * @param chartOrgList
     * @param chartOrg
     */
    private void buildOrgReviewHierarchy(Set chartOrgSet, KualiOrgReviewAttribute chartOrg) {
        KualiFiscalOrganization org = getKualiFiscalOrganization(chartOrg.getFinCoaCd(), chartOrg.getOrgCd());
        if (org.getReportsToFinCoaCd().equals(chartOrg.getFinCoaCd()) && org.getReportsToOrgCd().equals(chartOrg.getOrgCd())) {
            return;
        }
        KualiOrgReviewAttribute parent = new KualiOrgReviewAttribute();
        parent.setFinCoaCd(org.getReportsToFinCoaCd());
        parent.setOrgCd(org.getReportsToOrgCd());
        chartOrgSet.add(parent);
        buildOrgReviewHierarchy(chartOrgSet, parent);
    }

    /**
     * this method will take the document content, and populate a list of OrgReviewAttribute objects
     * that also contain the rollup in terms of organizational hierarchy as well.
     * 
     * @param docContent
     * @return a list of OrgReviewAttribute objects that are contained in the doc, or roll up to able
     * by one that is contained in the document
     */
    private Set populateFromDocContent(DocumentContent docContent) {
        Set chartOrgValues = new HashSet();
        Document doc = null;
        doc = XmlHelper.buildJDocument(docContent.getDocument());
        List chartOrgElements = XmlHelper.findElements(doc.getRootElement(), ORG_REVIEW_ATTRIBUTE);
        for (Iterator iter = chartOrgElements.iterator(); iter.hasNext();) {
            Element chartOrgElement = (Element) iter.next();
            KualiOrgReviewAttribute addedAttribute = new KualiOrgReviewAttribute(chartOrgElement.getChild(FIN_COA_CD_KEY).getText(), chartOrgElement.getChild(ORG_CD_KEY).getText()); 
            chartOrgValues.add(addedAttribute);
            buildOrgReviewHierarchy(chartOrgValues, addedAttribute);
        }
        return chartOrgValues;
    }

    /**
     * simple getter for the rule rows
     */
    public List getRuleRows() {
        return rows;
    }

    /**
     * simple getter for the routing data rows
     */
    public List getRoutingDataRows() {
        return rows;
    }

    /**
     * simple getter for fincoacd
     * @return
     */
    public String getFinCoaCd() {
        return this.finCoaCd;
    }

    /**
     * simple setter for fincoacd
     * @param finCoaCd
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    /**
     * simple getter for org code
     * @return
     */
    public String getOrgCd() {
        return this.orgCd;
    }

    /**
     * simple setter for org code
     * @param orgCd
     */
    public void setOrgCd(String orgCd) {
        this.orgCd = orgCd;
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