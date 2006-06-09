/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group. Licensed under the Educational
 * Community License Version 1.0 (the "License"); By obtaining, using and/or
 * copying this Original Work, you agree that you have read, understand, and
 * will comply with the terms and conditions of the Educational Community
 * License. You may obtain a copy of the License at:
 * http://kualiproject.org/license.html THE SOFTWARE IS PROVIDED "AS IS",
 * WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.kuali.workflow.attribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.KualiSpringServiceLocator;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.workflow.KualiConstants;
import org.kuali.workflow.beans.KualiFiscalOrganization;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.util.Utilities;

/**
 * KualiOrgReviewAttribute which should be used when using Orgs and thier inner details to do routing.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class KualiOrgReviewAttribute implements WorkflowAttribute {

    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiOrgReviewAttribute.class);

    private static final String FIN_COA_CD_KEY = "fin_coa_cd";
    private static final String ORG_REVIEW_FIN_COA_CD_KEY = "org_review_fin_coa_cd";
    private static final String ORG_CD_KEY = "org_cd";
    private static final String ORG_REVIEW_ORG_CD_KEY = "org_review_org_cd";
    private static final String FROM_AMOUNT_KEY = "fromAmount";
    private static final String TO_AMOUNT_KEY = "toAmount";
    private static final String TOTAL_AMOUNT_KEY = "totalAmount";
    private static final String OVERRIDE_CD_KEY = "overrideCd";

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
    private List rows;

    /**
     * no arg constructor, which will initialize the fields and rows of the attribute
     */
    public KualiOrgReviewAttribute() {
        rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field("Chart", "", Field.TEXT, true, ORG_REVIEW_FIN_COA_CD_KEY, "", null, null, FIN_COA_CD_KEY));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Org", "", Field.TEXT, true, ORG_REVIEW_ORG_CD_KEY, "", null, null, ORG_CD_KEY));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("From Amount", "", Field.TEXT, true, FROM_AMOUNT_KEY, "", null, null, FROM_AMOUNT_KEY));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("To Amount", "", Field.TEXT, true, TO_AMOUNT_KEY, "", null, null, TO_AMOUNT_KEY));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Override Code", "", Field.TEXT, true, OVERRIDE_CD_KEY, "", null, null, OVERRIDE_CD_KEY));
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
        extensions.add(new RuleExtensionValue(FIN_COA_CD_KEY, this.finCoaCd));
        extensions.add(new RuleExtensionValue(ORG_CD_KEY, this.orgCd));
        extensions.add(new RuleExtensionValue(FROM_AMOUNT_KEY, this.fromAmount));
        if (!StringUtils.isEmpty(this.toAmount)) {
            extensions.add(new RuleExtensionValue(TO_AMOUNT_KEY, this.toAmount));
        }
        if (!StringUtils.isEmpty(this.overrideCd)) {
            extensions.add(new RuleExtensionValue(OVERRIDE_CD_KEY, this.overrideCd));
        }
        // if (!StringUtils.isEmpty(this.finCoaCd)) {
        // RuleExtensionValue extensionFinCoaCd = new RuleExtensionValue();
        // extensionFinCoaCd.setKey(FIN_COA_CD_KEY);
        // extensionFinCoaCd.setValue(this.finCoaCd);
        // extensions.add(extensionFinCoaCd);
        // }
        // if (! StringUtils.isEmpty(this.orgCd)) {
        // RuleExtensionValue extensionOrgCd = new RuleExtensionValue();
        // extensionOrgCd.setKey(ORG_CD_KEY);
        // extensionOrgCd.setValue(this.orgCd);
        // extensions.add(extensionOrgCd);
        // }

        return extensions;
    }
    
    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#validateRuleData(java.util.Map)
     */
    public List validateRuleData(Map paramMap) {
        List errors = new ArrayList();
        this.finCoaCd = (String) paramMap.get(ORG_REVIEW_FIN_COA_CD_KEY);
        this.orgCd = (String) paramMap.get(ORG_REVIEW_ORG_CD_KEY);
        this.fromAmount = (String) paramMap.get(FROM_AMOUNT_KEY);
        this.toAmount = (String) paramMap.get(TO_AMOUNT_KEY);
        this.overrideCd = (String) paramMap.get(OVERRIDE_CD_KEY);

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

        if (isRequired() && !StringUtils.isNumeric(toAmount)) {
            errors.add(new WorkflowServiceErrorImpl("To Amount is invalid.", "routetemplate.dollarrangeattribute.toamount.invalid"));
        } else if (StringUtils.isNotBlank(toAmount) && !StringUtils.isNumeric(toAmount)) {
            errors.add(new WorkflowServiceErrorImpl("To Amount is invalid.", "routetemplate.dollarrangeattribute.toamount.invalid"));
        }

        if (isRequired() && !StringUtils.isNumeric(fromAmount)) {
            errors.add(new WorkflowServiceErrorImpl("From Amount is invalid.", "routetemplate.dollarrangeattribute.fromamount.invalid"));
        } else if (StringUtils.isNotBlank(fromAmount) && !StringUtils.isNumeric(fromAmount)) {
            errors.add(new WorkflowServiceErrorImpl("From Amount is invalid.", "routetemplate.dollarrangeattribute.fromamount.invalid"));
        }
        
        return errors;
    }  

    public List validateRoutingData(Map paramMap) {

        List errors = new ArrayList();
        this.finCoaCd = (String) paramMap.get(ORG_REVIEW_FIN_COA_CD_KEY);
        this.orgCd = (String) paramMap.get(ORG_REVIEW_ORG_CD_KEY);
        this.totalDollarAmount = (String)paramMap.get(TOTAL_AMOUNT_KEY);
        this.overrideCd = (String) paramMap.get(OVERRIDE_CD_KEY);

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

        if (isRequired() && !StringUtils.isNumeric(totalDollarAmount)) {
            errors.add(new WorkflowServiceErrorImpl("Total Amount is invalid.", ""));
        } else if (StringUtils.isNotBlank(totalDollarAmount) && !StringUtils.isNumeric(totalDollarAmount)) {
            errors.add(new WorkflowServiceErrorImpl("Total Amount is invalid.", ""));
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
        if (StringUtils.isBlank(finCoaCd) || StringUtils.isBlank(orgCd)) {
            return false;
        } else {
            return getKualiFiscalOrganization(finCoaCd, orgCd) != null;
        }
    }

    /**
     * @param finCoaCd
     * @param orgCd
     * @param isValidOrg
     * @return
     */
    private KualiFiscalOrganization getKualiFiscalOrganization(String finCoaCd, String orgCd) {
        KualiFiscalOrganization kualiFiscalOrganization = null;
        String key = finCoaCd + orgCd;
        if (ORGS.containsKey(key)) {
            return (KualiFiscalOrganization) ORGS.get(key);
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
        ORGS.put(key, kualiFiscalOrganization);
        return kualiFiscalOrganization;
    }



    /**
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowAttribute#getDocContent()
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getOrgCd())) {
            return "";
        }
        return "<report><chart>" + getFinCoaCd() + "</chart><org>" + getOrgCd() + 
        	"</org><totalDollarAmount>" + getTotalDollarAmount() + "</totalDollarAmount>" +
        	"<overrideCode>" + getOverrideCd() + "</overrideCode></report>";
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
            KualiFiscalOrganization attribute = (KualiFiscalOrganization) iter.next();
            if (attribute.getFinCoaCd().equals(this.getFinCoaCd()) && attribute.getOrgCd().equals(this.getOrgCd())) {
                matchesOrg = true;
                break;
            }
        }

        if (! matchesOrg) {
            return false;    
        }
        
        Float documentAmount = getAmount(documentType, docContent);
        if (documentAmount != null) {
            Float ruleFromAmount = new Float(fromAmount);
            if (! StringUtils.isEmpty(toAmount)) {   
                Float ruleToAmount = new Float(toAmount);
                if (! (ruleFromAmount.floatValue() <= documentAmount.floatValue() && documentAmount.floatValue() >= ruleToAmount.floatValue())) {
                    return false;
                }
            } else if (!(ruleFromAmount.floatValue() <= documentAmount.floatValue())) {
                return false;
            }
        }

        if (this.overrideCd != null) {
            String docOverrideCd = getOverrideCd(documentType, docContent);
            if (! docOverrideCd.equals(this.overrideCd)) {
                return false;
            }
        }
        
        return true;
    }
    /**
     * This method is a recursive method that will retrive reports to orgs to build up the hierarchy of organizations
     * 
     * @param chartOrgList
     * @param chartOrg
     */
    private void buildOrgReviewHierarchy(Set chartOrgSet, KualiFiscalOrganization chartOrg) {
        KualiFiscalOrganization org = getKualiFiscalOrganization(chartOrg.getFinCoaCd(), chartOrg.getOrgCd());
        if (org == null) {
        	throw new WorkflowRuntimeException("Couldn't find organization " + chartOrg.getFinCoaCd() + "-" + chartOrg.getOrgCd());
        }
        if (org.getReportsToFinCoaCd().equals(chartOrg.getFinCoaCd()) && org.getReportsToOrgCd().equals(chartOrg.getOrgCd())) {
            return;
        }
        KualiFiscalOrganization parent = new KualiFiscalOrganization();
        parent.setFinCoaCd(org.getReportsToFinCoaCd());
        parent.setOrgCd(org.getReportsToOrgCd());
        chartOrgSet.add(parent);
        buildOrgReviewHierarchy(chartOrgSet, parent);
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
     * this method will take the document content, and populate a list of OrgReviewAttribute objects that also contain the rollup in terms of organizational hierarchy as well.
     * 
     * @param docContent
     * @return a list of OrgReviewAttribute objects that are contained in the doc, or roll up to able by one that is contained in the document
     */
    private Set populateFromDocContent(DocumentType docType, DocumentContent docContent) {
        Set chartOrgValues = new HashSet();
        NodeList nodes = null;
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
        	String chart = null;
        	String org = null;
        	boolean isReport = ((Boolean)xpath.evaluate("wf:xstreamsafe('//report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
        	if (isReport) {
        		chart = xpath.evaluate("wf:xstreamsafe('//chart')", docContent.getDocument());
        		org = xpath.evaluate("wf:xstreamsafe('//org')", docContent.getDocument());
        	} else if (docType.getName().equals(ACCOUNT_DOC_TYPE) ||
        			docType.getName().equals(FIS_USER_DOC_TYPE) ||
        			docType.getName().equals(PROJECT_CODE_DOC_TYPE)) {
        		chart = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "chartOfAccountsCode')", docContent.getDocument());
        		org = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "organizationCode')", docContent.getDocument());
        	} else if (docType.getName().equals(ORGANIZATION_DOC_TYPE)) {
        		chart = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "finCoaCd')", docContent.getDocument());
        		org = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "orgCd')", docContent.getDocument());
        	} else if (docType.getName().equals(SUB_ACCOUNT_DOC_TYPE) ||
        			docType.getName().equals(ACCOUNT_DEL_DOC_TYPE) ||
        			docType.getName().equals(SUB_OBJECT_DOC_TYPE)) {
        		// these documents don't have the organization code on them so it must be looked up
        		chart = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "chartOfAccountsCode')", docContent.getDocument());
        		String accountNumber = xpath.evaluate("wf:xstreamsafe('" + MAINTAINABLE_PREFIX + "accountNumber')", docContent.getDocument());
        		Account account = SpringServiceLocator.getAccountService().getByPrimaryId(chart, accountNumber);
        		org = account.getOrganizationCode();
        	}
        	if (!StringUtils.isEmpty(chart) && !StringUtils.isEmpty(org)) {
            	KualiFiscalOrganization organization = new KualiFiscalOrganization();
        		organization.setFinCoaCd(chart);
        		organization.setOrgCd(org);
        		chartOrgValues.add(organization);
    			buildOrgReviewHierarchy(chartOrgValues, organization);
        	} else {
        		String xpathExp = null;
        		do {
        			if (docType.getName().equals("KualiMaintenanceDocument")) {
        				xpathExp = "wf:xstreamsafe('//kualiUser')";
        				break;
        			} else if (docType.getName().equals(KualiConstants.PROCUREMENT_CARD_DOC_TYPE)) {
        				xpathExp = "wf:xstreamsafe('//org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine/account')";
        				break;
        			} else if (KualiConstants.isSourceLineOnly(docType.getName())) {
        				xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account')";
        				break;
        			} else if (KualiConstants.isTargetLineOnly(docType.getName())) {
        				xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account')";
        				break;
        			} else if (docType.getName().equals("KualiFinancialDocument")) {
        				xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/account | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/account')";
        				break;
        			} else {
        				docType = docType.getParentDocType();
        			}
        		} while (docType != null);

        		if (xpathExp == null) {
        			throw new RuntimeException("Did not find expected document type.  Doc type used = " + docType.getName());
        		}
        		nodes = (NodeList) xpath.evaluate(xpathExp, docContent.getDocument(), XPathConstants.NODESET);

        		for (int i = 0; i < nodes.getLength(); i++) {
        			Node accountingLineNode = nodes.item(i);
                    
                    //  not needed anymore with the xstreamsafe function
        			//String referenceString = xpath.evaluate("@reference", accountingLineNode);
        			//if (!StringUtils.isEmpty(referenceString)) {
        			//	accountingLineNode = (Node) xpath.evaluate(referenceString, accountingLineNode, XPathConstants.NODE);
        			//}
        			String finCoaCd = xpath.evaluate("wf:xstreamsafe('chartOfAccountsCode')", accountingLineNode);
        			String orgCd = xpath.evaluate("wf:xstreamsafe('organizationCode')", accountingLineNode);
        			KualiFiscalOrganization orgization = new KualiFiscalOrganization();
        			orgization.setFinCoaCd(finCoaCd);
        			orgization.setOrgCd(orgCd);

        			chartOrgValues.add(orgization);
        			buildOrgReviewHierarchy(chartOrgValues, orgization);
        		}
        	}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return chartOrgValues;
    }

    private String getOverrideCd(DocumentType docType, DocumentContent docContent) {
        try {
        	XPath xpath = XPathFactory.newInstance().newXPath();
        	boolean isReport = ((Boolean)xpath.evaluate("wf:xstreamsafe('//report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
        	if (isReport) {
        		return xpath.evaluate("wf:xstreamsafe('//report/overrideCode')", docContent.getDocument());
        	}
            String xpathExp = null;
            do {
                if (docType.getName().equals("KualiMaintenanceDocument")) {
                    return null;
                } else if (KualiConstants.isSourceLineOnly(docType.getName())) {
                	xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/overrideCode')";
                	break; 
                } else if (KualiConstants.isTargetLineOnly(docType.getName())) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/overrideCode')";
                    break;
                } else if (docType.getName().equals("KualiFinancialDocument")) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/overrideCode') | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/overrideCode')";
                    break;
                } else {
                    docType = docType.getParentDocType();
                }

            } while (docType != null);

            return xpath.evaluate(xpathExp, docContent.getDocument());

        } catch (Exception e) {
            LOG.error("Caught excpeption getting document override code", e);
            throw new RuntimeException(e);
        }

    }

    private Float getAmount(DocumentType docType, DocumentContent docContent) {
        try {
        	XPath xpath = XPathFactory.newInstance().newXPath();
        	boolean isReport = ((Boolean)xpath.evaluate("wf:xstreamsafe('//report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue();
        	if (isReport) {
        		String floatVal = xpath.evaluate("wf:xstreamsafe('//report/totalDollarAmount')", docContent.getDocument());
        		if (StringUtils.isNumeric(floatVal) && StringUtils.isNotEmpty(floatVal)) {
        			return new Float(floatVal);	
        		} else {
        			return new Float(0);
        		}
        	}
            String xpathExp = null;
            do {
                if (docType.getName().equals("KualiMaintenanceDocument")) {
                    return null;
                } else if (KualiConstants.isSourceLineOnly(docType.getName())) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/amount/value')";
                    break;
                } else if (KualiConstants.isTargetLineOnly(docType.getName())) {
                	xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/amount/value')";
                	break; 
                } else if (docType.getName().equals("KualiFinancialDocument")) {
                    xpathExp = "wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine/amount/value') | wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine/amount/value')";
                    break;
                } else {
                    docType = docType.getParentDocType();
                }

            } while (docType != null);

            String value = xpath.evaluate("sum(" + xpathExp + ")", docContent.getDocument());
            if (value == null) {
                throw new RuntimeException("Didn't find amount for document " + docContent.getRouteContext().getDocument().getRouteHeaderId());
            }
            return new Float(value);
        } catch (Exception e) {
            LOG.error("Caught excpeption getting document amount", e);
            throw new RuntimeException(e);
        }
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
        List routingRows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field("Chart", "", Field.TEXT, true, ORG_REVIEW_FIN_COA_CD_KEY, "", null, null, FIN_COA_CD_KEY));
        routingRows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Org", "", Field.TEXT, true, ORG_REVIEW_ORG_CD_KEY, "", null, null, ORG_CD_KEY));
        routingRows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Total Amount", "", Field.TEXT, true, TOTAL_AMOUNT_KEY, "", null, null, TOTAL_AMOUNT_KEY));
        routingRows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Override Code", "", Field.TEXT, true, OVERRIDE_CD_KEY, "", null, null, OVERRIDE_CD_KEY));
        routingRows.add(new Row(fields));
        
        return routingRows;
    }

    /**
     * simple getter for fincoacd
     * 
     * @return
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
     * @return
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