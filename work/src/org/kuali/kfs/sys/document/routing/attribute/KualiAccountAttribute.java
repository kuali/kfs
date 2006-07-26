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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.UuId;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Delegate;
import org.kuali.workflow.KualiConstants;
import org.kuali.workflow.KualiWorkflowUtils;
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

    private static final String ROLE_STRING_DELIMITER = "~!~!~";

    private static final String NEW_MAINTAINABLE_PREFIX = "//newMaintainableObject/businessObject/";

    private static final String OLD_MAINTAINABLE_PREFIX = "//oldMaintainableObject/businessObject/";

    // TODO - here we need to integrate our AccountLookupable long term...
    // private static final String LOOKUPABLE_CLASS =
    // "AccountLookupableImplService";

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
     * method to validate the routing data, need to determine if this should
     * actually be implemented to throw errors or anything like that.
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
     * method to validate the rule data, which matches the routing data in this
     * attributes case therefore, we should just be able to call into the other
     * implementation validateRoutingData
     * 
     * @param paramMap
     * @return
     */
    public List validateRuleData(Map paramMap) {
        return validateRoutingData(paramMap);
    }

    /**
     * method to actually construct the docContent that will be appended to this
     * documents contents
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
     * return true since this is a rule attribute, and if there are no routing
     * records returned, then there was no valid mapping in the docContent for a
     * given role.
     * 
     * @param docContent
     * @param ruleExtensions
     * @return
     */
    public boolean isMatch(DocumentContent docContent, List ruleExtensions) {
        return true;
    }

    /**
     * This method is used by the workflow report to allow the user to fill in
     * some arbitrary values for the routable contents of an example document,
     * and then to run the report to generate a virtual route log of who the
     * document would route to, etc.
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
        return getNullSafeValue(role.roleName) + ROLE_STRING_DELIMITER + getNullSafeValue(role.chart) + ROLE_STRING_DELIMITER + getNullSafeValue(role.accountNumber) + ROLE_STRING_DELIMITER + getNullSafeValue(role.totalDollarAmount) + ROLE_STRING_DELIMITER + getNullSafeValue(role.fiscalOfficerId);
    }

    private static FiscalOfficerRole getUnqualifiedFiscalOfficerRole(String qualifiedRole) {
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

    private static String getNullSafeValue(String value) {
        return (value == null ? "" : value);
    }

    private static String getNullableString(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return value;
    }

    private static String getQualifiedAccountSupervisorRoleString(String roleName, String accountSupervisorySystemsId) {
        return roleName + ROLE_STRING_DELIMITER + accountSupervisorySystemsId;
    }

    private static String getUnqualifiedAccountSupervisorIdFromString(String qualifiedRole) {
        return qualifiedRole.split(ROLE_STRING_DELIMITER)[1];
    }

    /**
     * Encodes the qualified role names for Fiscal Officer and Account
     * Supervisor routing.
     */
    public List getQualifiedRoleNames(String roleName, DocumentContent docContent) throws EdenUserNotFoundException {
        try {
            Set qualifiedRoleNames = new HashSet();
            XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
            String docTypeName = docContent.getRouteContext().getDocument().getDocumentType().getName();
            if (FISCAL_OFFICER_ROLE_KEY.equals(roleName) || FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY.equals(roleName) || FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY.equals(roleName)) {
                Set fiscalOfficers = new HashSet();
                if (((Boolean) xpath.evaluate("wf:xstreamsafe('/report')", docContent.getDocument(), XPathConstants.BOOLEAN)).booleanValue()) {
                    String chart = xpath.evaluate("wf:xstreamsafe('/report/chart')", docContent.getDocument());
                    String accountNumber = xpath.evaluate("wf:xstreamsafe('/report/accountNumber')", docContent.getDocument());
                    String totalDollarAmount = xpath.evaluate("wf:xstreamsafe('/report/totalDollarAmount')", docContent.getDocument());
                    FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                    role.chart = chart;
                    role.accountNumber = accountNumber;
                    role.totalDollarAmount = totalDollarAmount;
                    fiscalOfficers.add(role);
                } else if (docTypeName.equals(KualiConstants.ACCOUNT_DOC_TYPE)) {
                    // 1) If this is a new account, it routes to the fiscal
                    // officer specified on the new account
                    // 2) If this is an account edit and the fiscal officer
                    // hasn't changed, route to the fiscal officer on the
                    // account
                    // 3) If this is an account edit and the fiscal officer HAS
                    // changed, route to the old fiscal officer and the new
                    // fiscal officer
                    String newFiscalOfficerId = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + PropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER + "')", docContent.getDocument());
                    String oldFiscalOfficerId = xpath.evaluate("wf:xstreamsafe('" + OLD_MAINTAINABLE_PREFIX + PropertyConstants.ACCOUNT_FISCAL_OFFICER_SYSTEM_IDENTIFIER + "')", docContent.getDocument());
                    boolean isNewAccount = oldFiscalOfficerId == null;
                    boolean isFiscalOfficerChanged = !newFiscalOfficerId.equals(oldFiscalOfficerId);
                    // if this is a new account or the fiscal officer has
                    // changed, route to the new fiscal officer
                    if (isNewAccount || isFiscalOfficerChanged) {
                        FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                        role.fiscalOfficerId = newFiscalOfficerId;
                        fiscalOfficers.add(role);
                    }
                    // if this is not a new account than route to the existing
                    // account's fiscal officer
                    if (!isNewAccount) {
                        FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                        role.chart = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + "chartOfAccountsCode')", docContent.getDocument());
                        role.accountNumber = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + "accountNumber')", docContent.getDocument());
                        role.fiscalOfficerId = newFiscalOfficerId;
                        fiscalOfficers.add(role);
                    }
                } else if (docTypeName.equals(KualiConstants.SUB_ACCOUNT_DOC_TYPE) || docTypeName.equals(KualiConstants.SUB_OBJECT_DOC_TYPE)) {
                    FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                    role.chart = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + "chartOfAccountsCode')", docContent.getDocument());
                    role.accountNumber = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + "accountNumber')", docContent.getDocument());
                    fiscalOfficers.add(role);
                } else if (docTypeName.equals(KualiConstants.ACCOUNT_DEL_DOC_TYPE)) {
                    FiscalOfficerRole role = new FiscalOfficerRole(roleName);
                    role.chart = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + "finCoaCd')", docContent.getDocument());
                    role.accountNumber = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + "accountNbr')", docContent.getDocument());
                    fiscalOfficers.add(role);
                } else if (docTypeName.equals(KualiConstants.PROCUREMENT_CARD_DOC_TYPE)) {
                    NodeList targetLineNodes = (NodeList) xpath.evaluate("wf:xstreamsafe('//org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine')", docContent.getDocument(), XPathConstants.NODESET);
                    String totalDollarAmount = String.valueOf(calculateTotalDollarAmount(xpath, targetLineNodes));
                    fiscalOfficers.addAll(getFiscalOfficerCriteria(xpath, targetLineNodes, roleName, totalDollarAmount));
                } else if (docTypeName.equals(KualiConstants.BUDGET_ADJUSTMENT_DOC_TYPE)) {
                    NodeList sourceLineNodes = (NodeList) xpath.evaluate("wf:xstreamsafe('//org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine')", docContent.getDocument(), XPathConstants.NODESET);
                    // TODO: get total amount for BA
                    String totalDollarAmount = String.valueOf(calculateTotalDollarAmount(xpath, sourceLineNodes));
                    fiscalOfficers.addAll(getFiscalOfficerCriteria(xpath, sourceLineNodes, roleName, totalDollarAmount));
                    NodeList targetLineNodes = (NodeList) xpath.evaluate("wf:xstreamsafe('//org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine')", docContent.getDocument(), XPathConstants.NODESET);
                    fiscalOfficers.addAll(getFiscalOfficerCriteria(xpath, targetLineNodes, roleName, totalDollarAmount));
                } else {
                    if (!KualiConstants.isTargetLineOnly(docTypeName)) {
                        NodeList sourceLineNodes = (NodeList) xpath.evaluate("wf:xstreamsafe('//org.kuali.core.bo.SourceAccountingLine')", docContent.getDocument(), XPathConstants.NODESET);
                        String totalDollarAmount = String.valueOf(calculateTotalDollarAmount(xpath, sourceLineNodes));
                        fiscalOfficers.addAll(getFiscalOfficerCriteria(xpath, sourceLineNodes, roleName, totalDollarAmount));
                    }
                    if (!KualiConstants.isSourceLineOnly(docTypeName)) {
                        NodeList targetLineNodes = (NodeList) xpath.evaluate("wf:xstreamsafe('//org.kuali.core.bo.TargetAccountingLine')", docContent.getDocument(), XPathConstants.NODESET);
                        String totalDollarAmount = String.valueOf(calculateTotalDollarAmount(xpath, targetLineNodes));
                        fiscalOfficers.addAll(getFiscalOfficerCriteria(xpath, targetLineNodes, roleName, totalDollarAmount));
                    }
                }
                for (Iterator iterator = fiscalOfficers.iterator(); iterator.hasNext();) {
                    FiscalOfficerRole role = (FiscalOfficerRole) iterator.next();
                    qualifiedRoleNames.add(getQualifiedRoleString(role));
                }
            } else if (ACCOUNT_SUPERVISOR_ROLE_KEY.equals(roleName)) {
                // only route to account supervisor on
                // KualiAccountMaintenanceDocument
                if (docTypeName.equals(KualiConstants.ACCOUNT_DOC_TYPE)) {
                    String accountSupervisorId = xpath.evaluate("wf:xstreamsafe('" + NEW_MAINTAINABLE_PREFIX + "accountsSupervisorySystemsIdentifier')", docContent.getDocument());
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

    private static String calculateTotalDollarAmount(XPath xpath, NodeList targetAccountingLineNodes) throws XPathExpressionException {
        KualiDecimal sum = new KualiDecimal(0);
        for (int index = 0; index < targetAccountingLineNodes.getLength(); index++) {
            KualiDecimal addend = new KualiDecimal((String) xpath.evaluate("./amount/value", targetAccountingLineNodes.item(index), XPathConstants.STRING));
            sum = sum.add(addend);
        }
        return sum.toString();
    }

    private static Set getFiscalOfficerCriteria(XPath xpath, NodeList accountingLineNodes, String roleName, String totalDollarAmount) throws XPathExpressionException {
        Set fiscalOfficers = new HashSet();
        for (int i = 0; i < accountingLineNodes.getLength(); i++) {
            Node accountingLineNode = accountingLineNodes.item(i);
            FiscalOfficerRole role = new FiscalOfficerRole(roleName);
            role.chart = xpath.evaluate("./chartOfAccountsCode", accountingLineNode);
            role.accountNumber = xpath.evaluate("./accountNumber", accountingLineNode);
            role.totalDollarAmount = totalDollarAmount;
            fiscalOfficers.add(role);
        }
        return fiscalOfficers;
    }

    /**
     * Resolves the qualified roles for Fiscal Officers, their delegates, and
     * account supervisors.
     */
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        try {
            List members = new ArrayList();
            String kualiDocumentType = SpringServiceLocator.getDataDictionaryService().getDocumentTypeCodeByTypeName(context.getDocument().getDocumentType().getName());
            String annotation = "";
            if (FISCAL_OFFICER_ROLE_KEY.equals(roleName)) {
                FiscalOfficerRole role = getUnqualifiedFiscalOfficerRole(qualifiedRole);
                annotation = (role.accountNumber == null ? "" : "Routing to account number " + role.accountNumber);
                UserId fiscalOfficerId = getFiscalOfficerId(role);
                if (fiscalOfficerId != null) {
                    members.add(fiscalOfficerId);
                }
            } else if (FISCAL_OFFICER_PRIMARY_DELEGATE_ROLE_KEY.equals(roleName)) {
                FiscalOfficerRole role = getUnqualifiedFiscalOfficerRole(qualifiedRole);
                UserId primaryDelegate = getPrimaryDelegation(role, kualiDocumentType);
                if (primaryDelegate != null) {
                    members.add(primaryDelegate);
                }
            } else if (FISCAL_OFFICER_SECONDARY_DELEGATE_ROLE_KEY.equals(roleName)) {
                FiscalOfficerRole role = getUnqualifiedFiscalOfficerRole(qualifiedRole);
                members.addAll(getSecondaryDelegations(role, kualiDocumentType));
            } else if (ACCOUNT_SUPERVISOR_ROLE_KEY.equals(roleName)) {
                String accountSupervisorId = getUnqualifiedAccountSupervisorIdFromString(qualifiedRole);
                annotation = "Routing to Account Supervisor";
                String supervisorNetworkId = SpringServiceLocator.getKualiUserService().getUniversalUser(new UuId(accountSupervisorId)).getPersonUserIdentifier();
                if (!StringUtils.isEmpty(supervisorNetworkId)) {
                    members.add(new AuthenticationUserId(supervisorNetworkId));
                } else {
                    LOG.info("No active account supervisor found.");
                }
            }
            return new ResolvedQualifiedRole(roleName, members, annotation);
        } catch (Exception e) {
            throw new RuntimeException("KualiAccountAttribute encountered exception while attempting to resolve qualified role", e);
        }
    }

    private static AuthenticationUserId getFiscalOfficerId(FiscalOfficerRole role) throws Exception {
        String fiscalOfficerNetworkId = null;
        if (role.accountNumber == null) {
            fiscalOfficerNetworkId = SpringServiceLocator.getKualiUserService().getUniversalUser(new UuId(role.fiscalOfficerId)).getPersonUserIdentifier();
        } else {
            Account account = SpringServiceLocator.getAccountService().getByPrimaryId(role.chart, role.accountNumber);
            if (account != null) {
                fiscalOfficerNetworkId = account.getAccountFiscalOfficerUser().getPersonUserIdentifier();
            }
        }
        if (StringUtils.isEmpty(fiscalOfficerNetworkId)) {
            LOG.warn("Could not locate the fiscal officer for the given account " + role.accountNumber + " / fiscal officer uid " + role.fiscalOfficerId);
            return null;
        }
        return new AuthenticationUserId(fiscalOfficerNetworkId);
    }

    /**
     * Returns a the UserId of the primary delegation on the given
     * FiscalOfficerRole. If the given role doesn't have an account number or
     * there is no primary delegate, returns null.
     * 
     * @throws RuntimeException if there is more than one primary delegation on
     *             the given account
     */
    private static UserId getPrimaryDelegation(FiscalOfficerRole role, String fisDocumentType) throws Exception {
        UserId primaryDelegateId = null;
        if (role.accountNumber != null) {
            Delegate delegateExample = new Delegate();
            delegateExample.setChartOfAccountsCode(role.chart);
            delegateExample.setAccountNumber(role.accountNumber);
            delegateExample.setFinancialDocumentTypeCode(fisDocumentType);
            Delegate primaryDelegate = SpringServiceLocator.getAccountService().getPrimaryDelegationByExample(delegateExample, role.totalDollarAmount);
            if (primaryDelegate != null) {
                primaryDelegateId = new AuthenticationUserId(primaryDelegate.getAccountDelegate().getPersonUserIdentifier());
            }
        }
        return primaryDelegateId;
    }

    /**
     * Returns a list of UserIds for all secondary delegations on the given
     * FiscalOfficerRole. If the given role doesn't have an account number or
     * there are no delegations, returns an empty list.
     */
    private static List getSecondaryDelegations(FiscalOfficerRole role, String fisDocumentType) throws Exception {
        List members = new ArrayList();
        if (role.accountNumber != null) {
            Delegate delegateExample = new Delegate();
            delegateExample.setChartOfAccountsCode(role.chart);
            delegateExample.setAccountNumber(role.accountNumber);
            delegateExample.setFinancialDocumentTypeCode(fisDocumentType);
            Iterator secondaryDelegations = SpringServiceLocator.getAccountService().getSecondaryDelegationsByExample(delegateExample, role.totalDollarAmount).iterator();
            while (secondaryDelegations.hasNext()) {
                members.add(new AuthenticationUserId(((Delegate) secondaryDelegations.next()).getAccountDelegate().getPersonUserIdentifier()));
            }
        }
        return members;
    }

    /**
     * A helper class which defines a Fiscal Officer role. Implements an
     * equals() and hashCode() method so that it can be used in a Set to prevent
     * the generation of needless duplicate requests.
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
                FiscalOfficerRole role = (FiscalOfficerRole) object;
                return new EqualsBuilder().append(roleName, role.roleName).append(fiscalOfficerId, role.fiscalOfficerId).append(chart, role.chart).append(accountNumber, role.accountNumber).append(totalDollarAmount, role.totalDollarAmount).isEquals();
            }
            return false;
        }

        public int hashCode() {
            return new HashCodeBuilder().append(roleName).append(fiscalOfficerId).append(chart).append(accountNumber).append(totalDollarAmount).hashCode();
        }

    }

}