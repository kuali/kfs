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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.kuali.KualiSpringServiceLocator;
import org.kuali.PropertyConstants;
import org.kuali.workflow.KualiConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.util.Utilities;

/**
 * KualiAccountAttribute which should be used when using Accounts to do routing
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class KualiAccountAttribute implements RoleAttribute, WorkflowAttribute {

    static final long serialVersionUID = 1000;

    private static Logger LOG = Logger.getLogger(KualiAccountAttribute.class);

    private static final String FIN_COA_CD_KEY = "fin_coa_cd";

    private static final String ACCOUNT_NBR_KEY = "account_nbr";

    private static final String FDOC_TOTAL_DOLLAR_AMOUNT_KEY = "fdoc_ttl_dlr_amt";

    private static final String FISCAL_OFFICER_ROLE_KEY = "FISCAL-OFFICER";

    private static final String FISCAL_OFFICER_ROLE_LABEL = "Fiscal Officer";

    private static final String FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY = "FISCAL-OFFICER-PRIMARY-DELEGATE";

    private static final String FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_LABEL = "Fiscal Officer Primary Delegate";

    private static final String FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY = "FISCAL-OFFICER-SECONDARY-DELEGATE";

    private static final String FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_LABEL = "Fiscal Officer Secondary Delegate";
    
    private static final String ACCOUNT_SUPERVISOR_ROLE_KEY = "ACCOUNT-SUPERVISOR";
    
    private static final String ACCOUNT_SUPERVISOR_ROLE_LABEL = "Account Supervisor";

    private static final String ACCOUNT_ATTRIBUTE = "KUALI_ACCOUNT_ATTRIBUTE";

    // TODO - here we need to integrate our AccountLookupable long term...
    // private static final String LOOKUPABLE_CLASS = "AccountLookupableImplService";

    private static final String ROLE_STRING_DELIMITER = "~!~!~";
    
    private static final String NEW_MAINTAINABLE_PREFIX = "//newMaintainableObject/businessObject/";
    private static final String OLD_MAINTAINABLE_PREFIX = "//oldMaintainableObject/businessObject/";

    private String finCoaCd;

    private String accountNbr;

    private String totalDollarAmount;

    private boolean required;

    /**
     * No arg constructor
     */
    public KualiAccountAttribute() {
    }

    /**
     * Constructor that takes chart, account, and total dollar amount
     * 
     * @param finCoaCd
     * @param accountNbr
     * @param totalDollarAmount
     */
    public KualiAccountAttribute(String finCoaCd, String accountNbr, String totalDollarAmount) {
        this.finCoaCd = finCoaCd;
        this.accountNbr = accountNbr;
        this.totalDollarAmount = totalDollarAmount;
    }

    /**
     * return the universal set of role names provided by this attribute
     */
    public List getRoleNames() {
        List roles = new ArrayList();
        roles.add(new Role(this.getClass(), FISCAL_OFFICER_ROLE_KEY, FISCAL_OFFICER_ROLE_LABEL));
        roles.add(new Role(this.getClass(), FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY, FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_LABEL));
        roles.add(new Role(this.getClass(), FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY, FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_LABEL));
        roles.add(new Role(this.getClass(), ACCOUNT_SUPERVISOR_ROLE_KEY, ACCOUNT_SUPERVISOR_ROLE_LABEL));
        return roles;
    }

    /**
     * return whether or not this attribute is required
     * 
     * @return
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * simple setter
     * 
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * simple getter for the rule extension values
     * 
     * @return
     */
    public List getRuleExtensionValues() {
        return Collections.EMPTY_LIST;
    }

    /**
     * method to validate the routing data, need to determine if this should actually be implemented to throw errors or anything like that.
     * 
     * @param paramMap
     * @return
     */
    public List validateRoutingData(Map paramMap) {
        // TODO should this actually validate
        // List errors = new ArrayList();
        this.finCoaCd = (String) paramMap.get(FIN_COA_CD_KEY);
        this.accountNbr = (String) paramMap.get(ACCOUNT_NBR_KEY);
        this.totalDollarAmount = (String) paramMap.get(FDOC_TOTAL_DOLLAR_AMOUNT_KEY);
        return new ArrayList();
    }

    /**
     * method to validate the rule data, which matches the routing data in this attributes case therefore, we should just be able to call into the other implementation validateRoutingData
     * 
     * @param paramMap
     * @return
     */
    public List validateRuleData(Map paramMap) {
        return validateRoutingData(paramMap);
    }

    /**
     * method to actually construct the docContent that will be appended to this documents contents
     * 
     * @return
     */
    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getAccountNbr())) {
            return "";
        }
        return "<report><chart>" + getFinCoaCd() + "</chart><accountNumber>" + getAccountNbr() + "</accountNumber><totalDollarAmount>" + getTotalDollarAmount() + "</totalDollarAmount></report>";
    }

    public String getAttributeLabel() {
        return "";
    }

    /**
     * return true since this is a rule attribute, and if there are no routing records returned, then there was no valid mapping in the docContent for a given role.
     * 
     * @param docContent
     * @param ruleExtensions
     * @return
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        return true;
    }

    /**
     * This method is used by the workflow report to allow the user to fill in some arbitrary values for the routable contents of an example document, and then to run the report to generate a virtual route log of who the document would route to, etc.
     * 
     * @return
     */
    public List getRoutingDataRows() {
        List rows = new ArrayList();

        List fields = new ArrayList();
        fields.add(new Field("Chart", "", Field.TEXT, false, FIN_COA_CD_KEY, "", null, null));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Account", "", Field.TEXT, false, ACCOUNT_NBR_KEY, "", null, null, "accountNbr"));
        rows.add(new Row(fields));

        fields = new ArrayList();
        fields.add(new Field("Total Dollar Amount", "", Field.TEXT, false, FDOC_TOTAL_DOLLAR_AMOUNT_KEY, "", null, null));
        rows.add(new Row(fields));
        return rows;
    }

    /**
     * simple getter which returns empty
     * 
     * @return
     */
    public List getRuleRows() {
        return Collections.EMPTY_LIST;
    }

    /**
     * simple getter which returns the account number
     * 
     * @return
     */
    public String getAccountNbr() {
        return accountNbr;
    }

    /**
     * simple setter that takes the account number
     * 
     * @param accountNbr
     */
    public void setAccountNbr(String accountNbr) {
        this.accountNbr = accountNbr;
    }

    /**
     * simple getter which returns the chart
     * 
     * @return
     */
    public String getFinCoaCd() {
        return finCoaCd;
    }

    /**
     * simple setter which takes the chart
     * 
     * @param finCoaCd
     */
    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    /**
     * simple getter which returns the total dollar amount
     * 
     * @return
     */
    public String getTotalDollarAmount() {
        return totalDollarAmount;
    }

    /**
     * simple setter which takes the total dollar amount
     * 
     * @param totalDollarAmount
     */
    public void setTotalDollarAmount(String totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }

    private String getQualifiedRoleString(FiscalOfficerRole role) {
    	return getNullSafeValue(role.roleName) + 
    		ROLE_STRING_DELIMITER +
    		getNullSafeValue(role.chart) +
    		ROLE_STRING_DELIMITER +
    		getNullSafeValue(role.accountNumber) +
    		ROLE_STRING_DELIMITER +
    		getNullSafeValue(role.totalDollarAmount) +
    		ROLE_STRING_DELIMITER +
    		getNullSafeValue(role.fiscalOfficerId);
    }
    
    private FiscalOfficerRole getUnqualifiedFiscalOfficerRole(String qualifiedRole) {
    	String[] values = qualifiedRole.split(ROLE_STRING_DELIMITER, -1);
    	if (values.length != 5) {
    		throw new RuntimeException("Invalid qualifiedRole, expected 5 encoded values: " + qualifiedRole);
    	}    	
    	FiscalOfficerRole role = new FiscalOfficerRole(values[0]);
    	role.chart = getNullableString(values[1]);
    	role.accountNumber = getNullableString(values[2]);
    	role.totalDollarAmount = getNullableString(values[3]);
    	role.fiscalOfficerId = getNullableString(values[4]);
    	return role;
    }
    
    private String getNullSafeValue(String value) {
    	return (value == null ? "" : value);
    }
    
    private String getNullableString(String value) {
    	if (StringUtils.isEmpty(value)) {
    		return null;
    	}
    	return value;
    }
    
    private String getQualifiedAccountSupervisorRoleString(String roleName, String accountSupervisorySystemsId) {
    	return roleName + ROLE_STRING_DELIMITER + accountSupervisorySystemsId;
    }

    private String getUnqualifiedAccountSupervisorIdFromString(String qualifiedRole) {
    	return qualifiedRole.split(ROLE_STRING_DELIMITER)[1];
    }
            
    /**
     * This should be a mapping between the FIS document type and the Workflow document type, 
     * and should give back the FIS document type name so that we can lookup delegations based on the 
     * FIS name instead of the workflow name.
     */
    private String getFisDocumentTypeNameFromWorkflowDocumentTypeName(String documentTypeName) {
        // TODO need to look at the document type table for
        return "ALL";
    }

    /**
     * Encodes the qualified role names for Fiscal Officer and Account Supervisor routing.
     */
    public List getQualifiedRoleNames(String roleName, DocumentContent docContent) throws EdenUserNotFoundException {
        try {
        	Set qualifiedRoleNames = new HashSet();
            XPath xpath = XPathFactory.newInstance().newXPath();
            String docTypeName = docContent.getRouteContext().getDocument().getDocumentType().getName();
            if (FISCAL_OFFICER_ROLE_KEY.equals(roleName) || FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY.equals(roleName) || FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY.equals(roleName)) {
            	Set fiscalOfficers = new HashSet();
                if (((Boolean)xpath.evaluate("/report", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue()) {
                	String chart = xpath.evaluate("/report/chart", docContent.getDocument());
                	String accountNumber = xpath.evaluate("/report/accountNumber", docContent.getDocument());
                	String totalDollarAmount = xpath.evaluate("/report/totalDollarAmount", docContent.getDocument());
                	FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                	role.chart = chart;
                	role.accountNumber = accountNumber;
                	role.totalDollarAmount = totalDollarAmount;
                	fiscalOfficers.add(role);
                } else if (docTypeName.equals(KualiConstants.ACCOUNT_DOC_TYPE)) {
                	String newFiscalOfficerId = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+PropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER, docContent.getDocument());;
                	String oldFiscalOfficerId = xpath.evaluate(OLD_MAINTAINABLE_PREFIX+PropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER, docContent.getDocument());
                	boolean isNewAccount = oldFiscalOfficerId == null;
                	boolean isFiscalOfficerChanged = !newFiscalOfficerId.equals(oldFiscalOfficerId);
                	// if this is a new account or the fiscal officer has changed, route to the new fiscal officer
                	if (isNewAccount || isFiscalOfficerChanged) {
                		FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                		role.fiscalOfficerId = newFiscalOfficerId;
                		fiscalOfficers.add(role);
                	}
                	// if this is not a new account than route to the existing account's fiscal officer
                	if (!isNewAccount) {
                		FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                		role.chart = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+"chartOfAccountsCode", docContent.getDocument());
                		role.accountNumber = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+"accountNumber", docContent.getDocument());
                		role.fiscalOfficerId = newFiscalOfficerId;
                		fiscalOfficers.add(role);
                	}
                } else if (docTypeName.equals(KualiConstants.SUB_ACCOUNT_DOC_TYPE) || docTypeName.equals(KualiConstants.SUB_OBJECT_DOC_TYPE)) {
                	FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                	role.chart = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+"chartOfAccountsCode", docContent.getDocument());
                	role.accountNumber = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+"accountNumber", docContent.getDocument());
                	fiscalOfficers.add(role);
                } else if (docTypeName.equals(KualiConstants.ACCOUNT_DEL_DOC_TYPE)) {
                	FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                	role.chart = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+"finCoaCd", docContent.getDocument());
                	role.accountNumber = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+"accountNbr", docContent.getDocument());
                	fiscalOfficers.add(role);
                } else {
                	if (!KualiConstants.isTargetLineOnly(docTypeName)) {
                		NodeList sourceLineNodes = (NodeList) xpath.evaluate("//org.kuali.core.bo.SourceAccountingLine", docContent.getDocument(), XPathConstants.NODESET);
                		String totalDollarAmount = String.valueOf(calculateTotalDollarAmount(xpath, sourceLineNodes));
                		fiscalOfficers.addAll(getFiscalOfficers(xpath, sourceLineNodes, roleName, totalDollarAmount));
                	}
                	if (!KualiConstants.isSourceLineOnly(docTypeName)) {
                		NodeList targetLineNodes = (NodeList) xpath.evaluate("//org.kuali.core.bo.TargetAccountingLine", docContent.getDocument(), XPathConstants.NODESET);
                		String totalDollarAmount = String.valueOf(calculateTotalDollarAmount(xpath, targetLineNodes));
                		fiscalOfficers.addAll(getFiscalOfficers(xpath, targetLineNodes, roleName, totalDollarAmount));
                	}
                }
            	for (Iterator iterator = fiscalOfficers.iterator(); iterator.hasNext();) {
					FiscalOfficerRole role = (FiscalOfficerRole) iterator.next();
					qualifiedRoleNames.add(getQualifiedRoleString(role));
				}
            } else if (ACCOUNT_SUPERVISOR_ROLE_KEY.equals(roleName)) {
            	// only route to account supervisor on KualiAccountMaintenanceDocument
            	if (docTypeName.equals(KualiConstants.ACCOUNT_DOC_TYPE)) {
            		String accountSupervisorId = xpath.evaluate(NEW_MAINTAINABLE_PREFIX+"accountsSupervisorySystemsIdentifier", docContent.getDocument());
            		if (!StringUtils.isEmpty(accountSupervisorId)) {
            			qualifiedRoleNames.add(getQualifiedAccountSupervisorRoleString(roleName, accountSupervisorId));
            		}
            	}
            }
            return new ArrayList(qualifiedRoleNames);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int calculateTotalDollarAmount(XPath xpath, NodeList targetAccountingLineNodes) throws XPathExpressionException {
    	int sum = 0;
    	for (int index = 0; index < targetAccountingLineNodes.getLength(); index++) {
    		sum += ((Number)xpath.evaluate("amount/value", targetAccountingLineNodes.item(index), XPathConstants.NUMBER)).intValue();
    	}
    	return sum;
    }
    
    private Set getFiscalOfficers(XPath xpath, NodeList accountingLineNodes, String roleName, String totalDollarAmount) throws XPathExpressionException {
    	Set fiscalOfficers = new HashSet();
    	for (int i = 0; i < accountingLineNodes.getLength(); i++) {
    		Node accountingLineNode = accountingLineNodes.item(i);
    		FiscalOfficerRole role = new FiscalOfficerRole(roleName);
    		role.chart = xpath.evaluate("chartOfAccountsCode", accountingLineNode);
    		role.accountNumber = xpath.evaluate("accountNumber", accountingLineNode);
    		role.totalDollarAmount = totalDollarAmount;
    		fiscalOfficers.add(role);
    	}
    	return fiscalOfficers;
    }
    
    /**
     * Resolves the qualified roles for Fiscal Officers, their delegates, and account supervisors.
     */
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        List members = new ArrayList();
        String fisDocumentType = getFisDocumentTypeNameFromWorkflowDocumentTypeName(context.getDocument().getDocumentType().getName());
        String annotation = "";
        Connection conn = null;
        try {
        	conn = KualiSpringServiceLocator.getDataSource().getConnection();
        	if (FISCAL_OFFICER_ROLE_KEY.equals(roleName)) {
        		FiscalOfficerRole role = getUnqualifiedFiscalOfficerRole(qualifiedRole);
        		annotation = (role.accountNumber == null ? "" : "Routing to account number " + role.accountNumber);
        		UserId fiscalOfficerId = getFiscalOfficerId(conn, role);
        		if (fiscalOfficerId != null) {
        			members.add(fiscalOfficerId);
        		}
        	} else if (FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY.equals(roleName)) {
        		FiscalOfficerRole role = getUnqualifiedFiscalOfficerRole(qualifiedRole);
        		UserId primaryDelegate = getPrimaryDelegation(conn, role, fisDocumentType);
        		if (primaryDelegate != null) {
        			members.add(primaryDelegate);
        		}
        	} else if (FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY.equals(roleName)) {
        		FiscalOfficerRole role = getUnqualifiedFiscalOfficerRole(qualifiedRole);
        		members.addAll(getSecondaryDelegations(conn, role, fisDocumentType));
        	} else if (ACCOUNT_SUPERVISOR_ROLE_KEY.equals(roleName)) {
        		String accountSupervisorId = getUnqualifiedAccountSupervisorIdFromString(qualifiedRole);
        		annotation = "Routing to Account Supervisor";
        		String supervisorNetworkId = KualiConstants.getActiveNetworkId(conn, accountSupervisorId);
        		if (!StringUtils.isEmpty(supervisorNetworkId)) {
            		members.add(new AuthenticationUserId(supervisorNetworkId));
        		} else {
        			LOG.info("No active account supervisor found.");
        		}
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

        return new ResolvedQualifiedRole(roleName, members, annotation);
    }
    
    private AuthenticationUserId getFiscalOfficerId(Connection connection, FiscalOfficerRole role) throws Exception {
    	String kualiSystemId = null;
    	// if the account is null, then use the fiscal officer id
    	if (role.accountNumber == null) {
    		kualiSystemId = role.fiscalOfficerId;
    	} else {
    		String sql = "select ACCT_FSC_OFC_UID from CA_ACCOUNT_T where FIN_COA_CD = ? and ACCOUNT_NBR = ?";
    		PreparedStatement ps = connection.prepareStatement(sql);
    		ps.setString(1, role.chart);
    		ps.setString(2, role.accountNumber);
    		ResultSet rs = ps.executeQuery();
    		if (rs.next()) {
    			kualiSystemId = rs.getString("ACCT_FSC_OFC_UID");
    		}
    	}
		if (kualiSystemId == null) {
			LOG.warn("No Fiscal Officer retrieved for " + 
					(role.accountNumber == null ? "fiscal officer id="+role.fiscalOfficerId : "account=" + role.accountNumber));
			return null;
		}
		String fiscalOfficerNetworkId = KualiConstants.getNetworkId(connection, kualiSystemId);
		if (StringUtils.isEmpty(fiscalOfficerNetworkId)) {
			throw new RuntimeException("Could not locate the fiscal officer for the given id " + kualiSystemId);
		}
    	return new AuthenticationUserId(fiscalOfficerNetworkId);
    }

    /**
     * Returns a the UserId of the primary delegation on the given FiscalOfficerRole.  If the given role
     * doesn't have an account number or there is no primary delegate, returns null.
     * 
     * @throws RuntimeException if there is more than one primary delegation on the given account
     */
    private UserId getPrimaryDelegation(Connection connection, FiscalOfficerRole role, String fisDocumentType) throws Exception {
    	UserId primaryDelegate = null;
    	// if there is no account number then there are no delegations
    	if (role.accountNumber == null) {
    		return primaryDelegate;
    	}
		String sql = "select ACCT_DLGT_UNVL_ID from CA_ACCT_DELEGATE_T " + "where FIN_COA_CD = ? and ACCOUNT_NBR = ? and FDOC_TYP_CD = ? and ACCT_DLGT_ACTV_IND = 'Y' " + "and ACCT_DLGT_START_DT <= SYSDATE and ACCT_DLGT_PRMRT_IND = 'Y' " + (totalDollarAmount == null ? "" : "and ((FDOC_APRV_FROM_AMT <= ? and FDOC_APRV_TO_AMT >= ?) " + "		OR FDOC_APRV_TO_AMT = 0)");
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.setString(1, role.chart);
		ps.setString(2, role.accountNumber);
		ps.setString(3, fisDocumentType);
		if (role.totalDollarAmount != null) {
			ps.setString(4, role.totalDollarAmount);
			ps.setString(5, role.totalDollarAmount);
		}
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String kualiSystemId = rs.getString("ACCT_DLGT_UNVL_ID");
			if (rs.next()) {
				throw new RuntimeException("There was more than one primary account delegate for account " + role.accountNumber);
			}
			primaryDelegate = new AuthenticationUserId(KualiConstants.getNetworkId(connection, kualiSystemId));
		}
		return primaryDelegate;
    }

    /**
     * Returns a list of UserIds for all secondary delegations on the given FiscalOfficerRole.  If the given role
     * doesn't have an account number or there are no delegations, returns an empty list.
     */
    private List getSecondaryDelegations(Connection connection, FiscalOfficerRole role, String fisDocumentType) throws Exception {
    	List members = new ArrayList();
    	// if there is no account number then there are no delegations
    	if (role.accountNumber == null) {
    		return members;
    	}
    	String sql = "select ACCT_DLGT_UNVL_ID from CA_ACCT_DELEGATE_T " + 
			"where FIN_COA_CD = ? and ACCOUNT_NBR = ? and FDOC_TYP_CD = ? and ACCT_DLGT_ACTV_IND = 'Y' " + 
			"and ACCT_DLGT_START_DT <= SYSDATE and ACCT_DLGT_PRMRT_IND = 'N' " + 
			(role.totalDollarAmount == null ? "" : "and ((FDOC_APRV_FROM_AMT <= ? and FDOC_APRV_TO_AMT >= ?) " + 
			"		OR FDOC_APRV_TO_AMT = 0)");
    	PreparedStatement ps = connection.prepareStatement(sql);
    	ps.setString(1, role.chart);
    	ps.setString(2, role.accountNumber);
    	ps.setString(3, fisDocumentType);
    	if (totalDollarAmount != null) {
    		ps.setString(4, totalDollarAmount);
    		ps.setString(5, totalDollarAmount);
    	}
    	ResultSet rs = ps.executeQuery();
    	while (rs.next()) {
    		members.add(new AuthenticationUserId(KualiConstants.getNetworkId(connection, rs.getString("ACCT_DLGT_UNVL_ID"))));
    	}
    	return members;
    }
    
    /**
     * A helper class which defines a Fiscal Officer role.  Implements an equals() and hashCode() method so that it
     * can be used in a Set to prevent the generation of needless duplicate requests.
     * 
     * @author ewestfal
     */
    private static class FiscalOfficerRole {
    	
    	public String roleName;
    	public String fiscalOfficerId;
    	public String chart;
    	public String accountNumber;
    	public String totalDollarAmount;
    	
    	public FiscalOfficerRole(String roleName) {
    		this.roleName = roleName;
    	}

		public boolean equals(Object object) {
			if (object instanceof FiscalOfficerRole) {
				FiscalOfficerRole role = (FiscalOfficerRole)object;
				return new EqualsBuilder().append(roleName, role.roleName).
					append(fiscalOfficerId, role.fiscalOfficerId).
					append(chart, role.chart).
					append(accountNumber, role.accountNumber).
					append(totalDollarAmount, role.totalDollarAmount).isEquals();
			}
			return false;
		}

		public int hashCode() {
			return new HashCodeBuilder().append(roleName).
				append(fiscalOfficerId).
				append(chart).
				append(accountNumber).
				append(totalDollarAmount).hashCode();
		}
    	
    	
    	
    }
    
}
