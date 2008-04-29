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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.integration.service.ContractsAndGrantsModuleService;
import org.kuali.workflow.KualiWorkflowUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.util.Utilities;


public class KualiPDAttribute implements RoleAttribute, WorkflowAttribute {

    static final long serialVersionUID = 101;

    private static final String FIN_COA_CD_KEY = "fin_coa_cd";

    private static final String ACCOUNT_NBR_KEY = "account_nbr";

    private static final String ROLE_STRING_DELIMITER = "~!~!~";

    private static Logger LOG = Logger.getLogger(KualiCGAttribute.class);

    private boolean required;
    private static XPath xpath;

    private String finCoaCd;
    private String accountNbr;

    /**
     * Constructs a KualiCGAttribute.java. This class tries to find an entry for a chart/account in the 
     * AwardAccount table. If it     * does, it takes the largest value and looks up the project director 
     * in the ProposalProjectDirector BO..
     */

    public KualiPDAttribute() {
    }

    public KualiPDAttribute(String finCoaCd, String accountNbr) {
        this.finCoaCd = finCoaCd;
        this.accountNbr = accountNbr;

    }

    public List getRoutingDataRows() {
        List rows = new ArrayList();
        rows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY));
        Map fieldConversionMap = new HashMap();
        fieldConversionMap.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, FIN_COA_CD_KEY);
        rows.add(KualiWorkflowUtils.buildTextRowWithLookup(Account.class, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, ACCOUNT_NBR_KEY, fieldConversionMap));

        return rows;
    }

    private String getQualifiedRoleString(ProjectDirectorRole role) {
        return new StringBuffer(getNullSafeValue(role.roleName)).append(ROLE_STRING_DELIMITER).append(getNullSafeValue(role.chart)).append(ROLE_STRING_DELIMITER).append(getNullSafeValue(role.accountNumber)).toString();
    }


    public List validateRoutingData(Map paramMap) {
        List errors = new ArrayList();
        this.finCoaCd = LookupUtils.forceUppercase(Account.class, "chartOfAccountsCode", (String) paramMap.get(FIN_COA_CD_KEY));
        this.accountNbr = LookupUtils.forceUppercase(Account.class, "accountNumber", (String) paramMap.get(ACCOUNT_NBR_KEY));
        validateAccount(errors);
        return errors;
    }

    private void validateAccount(List errors) {
        if (StringUtils.isBlank(this.finCoaCd) || StringUtils.isBlank(this.accountNbr)) {
            errors.add(new WorkflowServiceErrorImpl("Account is required.", "routetemplate.accountattribute.account.required"));
            return;
        }
        Account account = SpringContext.getBean(AccountService.class).getByPrimaryIdWithCaching(finCoaCd, accountNbr);
        if (account == null) {
            errors.add(new WorkflowServiceErrorImpl("Account is invalid.", "routetemplate.accountattribute.account.invalid"));
        }
    }

    public boolean isMatch(DocumentContent documentContent, List rules) {
        return true;
    }

    public String getDocContent() {
        if (Utilities.isEmpty(getFinCoaCd()) || Utilities.isEmpty(getAccountNbr())) {
            return "";
        }
        return new StringBuffer(KFSConstants.WorkflowConstants.GET_GENERIC_ACCOUNT_REPORT_PREFIX).append("<routingChart>").append(getFinCoaCd()).append("</routingChart><routingAccount>").append(getAccountNbr()).append("</routingAccount>").append(KFSConstants.WorkflowConstants.GET_GENERIC_ACCOUNT_REPORT_SUFFIX).toString();

    }

    public List getQualifiedRoleNames(String roleName, DocumentContent docContent) throws EdenUserNotFoundException {
        //Using a set to prevent duplicates
        Set projectDirectors = new HashSet();
        XPath xpath = KualiWorkflowUtils.getXPath(docContent.getDocument());
        String docTypeName = docContent.getRouteContext().getDocument().getDocumentType().getName();
        try {

            NodeList routingNodes = (NodeList) xpath.evaluate(new StringBuffer(KFSConstants.WorkflowConstants.GET_GENERIC_ACCOUNTS_PREFIX).append(this.getClass().getSimpleName()).append(KFSConstants.WorkflowConstants.GET_GENERIC_ACCOUNTS_SUFFIX).toString(), docContent.getDocument(), XPathConstants.NODESET);
            for (int i = 0; i < routingNodes.getLength(); i++) {
                Node routingDataNode = routingNodes.item(i);
                ProjectDirectorRole role = new ProjectDirectorRole(roleName);
                role.chart = xpath.evaluate(KFSConstants.WorkflowConstants.GET_GENERIC_CHART, routingDataNode);
                role.accountNumber = xpath.evaluate(KFSConstants.WorkflowConstants.GET_GENERIC_ACCOUNT, routingDataNode);

                projectDirectors.add(getQualifiedRoleString(role));
            }
            
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ArrayList(projectDirectors);
    }

    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext context, String roleName, String qualifiedRole) {
        
            List<Id> members = new ArrayList();
            String annotation = "";
            ProjectDirectorRole role = getUnqualifiedProjectDirectorRole(qualifiedRole);
            annotation = (role.accountNumber == null ? "" : "Routing to chart/account number " + role.chart + "/" + role.accountNumber);
            UuId projectDirectorUniversalId = getProjectDirectorUniversalId(role);
            if (projectDirectorUniversalId != null) {
                members.add(projectDirectorUniversalId);
            }
            return new ResolvedQualifiedRole(roleName, members, annotation);
        
    }

    private static UuId getProjectDirectorUniversalId(ProjectDirectorRole role) {
        UniversalUser projectDirectorUniversalId = null;
        if (StringUtils.isNotBlank(role.chart) && StringUtils.isNotBlank(role.accountNumber)) {
            projectDirectorUniversalId = SpringContext.getBean(ContractsAndGrantsModuleService.class).getProjectDirectorForAccount(role.chart, role.accountNumber);
        }

        // if we cant find a Project Director, log it.
        if (null==projectDirectorUniversalId) {
            LOG.debug(new StringBuffer("Could not locate the project director for the given account ").append(role.chart).append(" ").append(role.accountNumber).toString());
            return null;
        }

        return new UuId(projectDirectorUniversalId.getPersonUniversalIdentifier());

    }

    private static ProjectDirectorRole getUnqualifiedProjectDirectorRole(String qualifiedRole) {
        String[] values = qualifiedRole.split(ROLE_STRING_DELIMITER, -1);
        if (values.length != 3) {
            throw new RuntimeException("Invalid qualifiedRole, expected 3 encoded values: " + qualifiedRole);
        }
        ProjectDirectorRole role = new ProjectDirectorRole(values[0]);
        role.chart = getNullableString(values[1]);
        role.accountNumber = getNullableString(values[2]);
        return role;
    }

    private static String getNullableString(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return value;
    }

    public List getRoleNames() {
        List roles = new ArrayList();
        roles.add(new Role(this.getClass(), "EFFORT_REPORTING_PROJECT_DIRECTOR", "Effort Reporting Project Director"));

        return roles;
    }

    public boolean isRequired() {
        return required;
    }

    private static String getNullSafeValue(String value) {
        return (value == null ? "" : value);
    }

    public List validateRuleData(Map paramMap) {
        return Collections.EMPTY_LIST;
    }

    public List getRuleExtensionValues() {
        return Collections.EMPTY_LIST;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List getRuleRows() {
        return Collections.EMPTY_LIST;
    }


    public String getAccountNbr() {
        return accountNbr;
    }

    public void setAccountNbr(String accountNbr) {
        this.accountNbr = accountNbr;
    }

    public String getFinCoaCd() {
        return finCoaCd;
    }

    public void setFinCoaCd(String finCoaCd) {
        this.finCoaCd = finCoaCd;
    }

    public String getAttributeLabel() {
        return "";
    }

    private static class ProjectDirectorRole {
        public String roleName;

        public String chart;

        public String accountNumber;

        public ProjectDirectorRole(String roleName) {
            this.roleName = roleName;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof ProjectDirectorRole) {
                ProjectDirectorRole role = (ProjectDirectorRole) object;
                return new EqualsBuilder().append(chart, role.chart).append(accountNumber, role.accountNumber).isEquals();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(chart).append(accountNumber).hashCode();
        }

    }
}
