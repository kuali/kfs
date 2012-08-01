/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.util.CodeTranslator;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateMemberContract;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberContract;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.framework.group.GroupEbo;
import org.kuali.rice.kim.framework.role.RoleEbo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OrgReviewRole extends PersistableBusinessObjectBase implements MutableInactivatable {

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "OrgReviewRole";

    protected static final String ORR_INQUIRY_TITLE_PROPERTY = "message.inquiry.org.review.role.title";
    protected static String INQUIRY_TITLE_VALUE = null;

    protected static transient OrgReviewRoleService orgReviewRoleService;

    //Dummy variable
    protected String organizationTypeCode = "99";
    private static final long serialVersionUID = 1L;

    public static final String REVIEW_ROLES_INDICATOR_FIELD_NAME = "reviewRolesIndicator";
    public static final String ROLE_NAME_FIELD_NAMESPACE_CODE = "roleMemberRoleNamespaceCode";
    public static final String ROLE_NAME_FIELD_NAME = "roleMemberRoleName";
    public static final String GROUP_NAME_FIELD_NAMESPACE_CODE = "groupMemberGroupNamespaceCode";
    public static final String GROUP_NAME_FIELD_NAME = "groupMemberGroupName";
    public static final String PRINCIPAL_NAME_FIELD_NAME = "principalMemberPrincipalName";
    public static final String CHART_CODE_FIELD_NAME = "chartOfAccountsCode";
    public static final String ORG_CODE_FIELD_NAME = "organizationCode";
    public static final String DOC_TYPE_NAME_FIELD_NAME = "financialSystemDocumentTypeCode";
    public static final String DELEGATE_FIELD_NAME = "delegate";
    public static final String DELEGATION_TYPE_CODE = "delegationTypeCode";
    public static final String FROM_AMOUNT_FIELD_NAME = "fromAmount";
    public static final String TO_AMOUNT_FIELD_NAME = "toAmount";
    public static final String OVERRIDE_CODE_FIELD_NAME = "overrideCode";
    public static final String ACTION_TYPE_CODE_FIELD_NAME = "actionTypeCode";
    public static final String PRIORITY_CODE_FIELD_NAME = "priorityNumber";
    public static final String ACTION_POLICY_CODE_FIELD_NAME = "actionPolicyCode";
    public static final String FORCE_ACTION_FIELD_NAME = "forceAction";
    public static final String ACTIVE_FROM_DATE = "activeFromDate";
    public static final String ACTIVE_TO_DATE = "activeToDate";

    public static final String ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY = "ODelMId";
    public static final String ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY = "ORMId";

    public static final String NEW_DELEGATION_ID_KEY_VALUE = "New";

    protected String methodToCall;
    protected String kimTypeId;

    protected String orgReviewRoleMemberId;
    protected Chart chart;
    protected Organization organization;
    protected boolean edit;
    protected boolean copy;

//    protected KfsKimDocDelegateType delegation = new KfsKimDocDelegateType();
    protected KfsKimDocDelegateMember delegationMemberRole = new KfsKimDocDelegateMember( "", MemberType.ROLE );
    protected KfsKimDocDelegateMember delegationMemberGroup = new KfsKimDocDelegateMember( "", MemberType.GROUP );
    protected KfsKimDocDelegateMember delegationMemberPerson = new KfsKimDocDelegateMember( "", MemberType.PRINCIPAL );

    protected KfsKimDocRoleMember memberRole = new KfsKimDocRoleMember( "", MemberType.ROLE );
    protected KfsKimDocRoleMember memberGroup = new KfsKimDocRoleMember( "", MemberType.GROUP );
    protected KfsKimDocRoleMember memberPerson = new KfsKimDocRoleMember( "", MemberType.PRINCIPAL );

    protected RoleEbo role;
    protected GroupEbo group;
    protected Person person;

    protected List<KfsKimDocumentAttributeData> attributes = new ArrayList<KfsKimDocumentAttributeData>();
    protected List<RoleResponsibilityAction> roleRspActions = new ArrayList<RoleResponsibilityAction>();

    //Identifying information for the 3 kinds of role members this document caters to
    protected String roleMemberRoleId;
    protected String roleMemberRoleNamespaceCode;
    protected String roleMemberRoleName;

    protected String groupMemberGroupId;
    protected String groupMemberGroupNamespaceCode;
    protected String groupMemberGroupName;

    protected String principalMemberPrincipalId;
    protected String principalMemberPrincipalName;
    protected String principalMemberName;

    //The role id this object corresponds to ( org review / acct review )
    protected String roleId;
    protected String namespaceCode;
    protected String roleName;

    //Identifying information for a single member (of any type)
    protected String memberTypeCode;

    //In case the document is dealing with delegations
    protected String delegationTypeCode;

    protected String delegationMemberId;
    protected String roleMemberId;

    protected String oDelMId;
    protected String oRMId;

    protected String financialSystemDocumentTypeCode;
    protected DocumentTypeEBO financialSystemDocumentType;
    protected List<String> roleNamesToConsider;
    protected String reviewRolesIndicator;

    protected String actionTypeCode;
    protected String priorityNumber;
    protected String actionPolicyCode;
    protected boolean forceAction;
    protected String chartOfAccountsCode;
    protected String organizationCode;
    protected KualiDecimal fromAmount;
    protected KualiDecimal toAmount;
    protected String overrideCode;
    protected boolean active = true;
    protected boolean delegate;

    protected Date activeFromDate;
    protected Date activeToDate;

    public String getReportsToChartOfAccountsCode() {
        if ( organization != null ) {
            return organization.getReportsToChartOfAccountsCode();
        }
        return null;
    }

    public String getReportsToOrganizationCode() {
        if ( organization != null ) {
            return organization.getReportsToOrganizationCode();
        }
        return null;
    }
    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Gets the delegate attribute.
     * @return Returns the delegate.
     */
    public boolean isDelegate() {
        return delegate;
    }
    /**
     * Sets the delegate attribute value.
     * @param delegate The delegate to set.
     */
    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }
    /**
     * Gets the chart attribute.
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }
    /**
     * Sets the chart attribute value.
     * @param chart The chart to set.
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }
//    /**
//     * Gets the delegation attribute.
//     * @return Returns the delegation.
//     */
//    public KfsKimDocDelegateType getDelegation() {
//        return delegation;
//    }
//    /**
//     * Sets the delegation attribute value.
//     * @param delegation The delegation to set.
//     */
//    public void setDelegation(KfsKimDocDelegateType delegation) {
//        this.delegation = delegation;
//    }
    /**
     * Gets the groupMemberGroupId attribute.
     * @return Returns the groupMemberGroupId.
     */
    public String getGroupMemberGroupId() {
        return groupMemberGroupId;
    }
    /**
     * Sets the groupMemberGroupId attribute value.
     * @param groupMemberGroupId The groupMemberGroupId to set.
     */
    public void setGroupMemberGroupId(String groupMemberGroupId) {
        this.groupMemberGroupId = groupMemberGroupId;
        memberGroup.setMemberId(groupMemberGroupId);
        delegationMemberGroup.setMemberId(groupMemberGroupId);
    }
    /**
     * Gets the groupMemberGroupName attribute.
     * @return Returns the groupMemberGroupName.
     */
    public String getGroupMemberGroupName() {
        return groupMemberGroupName;
    }
    /**
     * Sets the groupMemberGroupName attribute value.
     * @param groupMemberGroupName The groupMemberGroupName to set.
     */
    public void setGroupMemberGroupName(String groupMemberGroupName) {
        this.groupMemberGroupName = groupMemberGroupName;
        memberGroup.setMemberName(groupMemberGroupName);
        delegationMemberGroup.setMemberName(groupMemberGroupName);
    }
    /**
     * Gets the groupMemberGroupNamespaceCode attribute.
     * @return Returns the groupMemberGroupNamespaceCode.
     */
    public String getGroupMemberGroupNamespaceCode() {
        return groupMemberGroupNamespaceCode;
    }
    /**
     * Sets the groupMemberGroupNamespaceCode attribute value.
     * @param groupMemberGroupNamespaceCode The groupMemberGroupNamespaceCode to set.
     */
    public void setGroupMemberGroupNamespaceCode(String groupMemberGroupNamespaceCode) {
        this.groupMemberGroupNamespaceCode = groupMemberGroupNamespaceCode;
        memberGroup.setMemberNamespaceCode(groupMemberGroupNamespaceCode);
        delegationMemberGroup.setMemberNamespaceCode(groupMemberGroupNamespaceCode);
    }
    /**
     * Gets the principalMemberPrincipalId attribute.
     * @return Returns the principalMemberPrincipalId.
     */
    public String getPrincipalMemberPrincipalId() {
        return principalMemberPrincipalId;
    }
    /**
     * Sets the principalMemberPrincipalId attribute value.
     * @param principalMemberPrincipalId The principalMemberPrincipalId to set.
     */
    public void setPrincipalMemberPrincipalId(String principalMemberPrincipalId) {
        this.principalMemberPrincipalId = principalMemberPrincipalId;
        memberPerson.setMemberId(principalMemberPrincipalId);
        delegationMemberPerson.setMemberId(principalMemberPrincipalId);
    }
    /**
     * Gets the principalMemberPrincipalName attribute.
     * @return Returns the principalMemberPrincipalName.
     */
    public String getPrincipalMemberPrincipalName() {
        if ( StringUtils.isBlank(principalMemberPrincipalName) ) {
            getPerson();
        }
        return principalMemberPrincipalName;
    }

    public String getPrincipalMemberName() {
        if ( StringUtils.isBlank(principalMemberName) ) {
            getPerson();
        }
        return principalMemberName;
    }

    /**
     * Sets the principalMemberPrincipalName attribute value.
     * @param principalMemberPrincipalName The principalMemberPrincipalName to set.
     */
    public void setPrincipalMemberPrincipalName(String principalMemberPrincipalName) {
        this.principalMemberPrincipalName = principalMemberPrincipalName;
        memberPerson.setMemberName(principalMemberPrincipalName);
        delegationMemberPerson.setMemberName(principalMemberPrincipalName);
    }
    /**
     * Gets the roleMemberRoleId attribute.
     * @return Returns the roleMemberRoleId.
     */
    public String getRoleMemberRoleId() {
        return roleMemberRoleId;
    }
    /**
     * Sets the roleMemberRoleId attribute value.
     * @param roleMemberRoleId The roleMemberRoleId to set.
     */
    public void setRoleMemberRoleId(String roleMemberRoleId) {
        this.roleMemberRoleId = roleMemberRoleId;
        memberRole.setMemberId(roleMemberRoleId);
        delegationMemberRole.setMemberId(roleMemberRoleId);
    }
    /**
     * Gets the roleMemberRoleName attribute.
     * @return Returns the roleMemberRoleName.
     */
    public String getRoleMemberRoleName() {
        return roleMemberRoleName;
    }
    /**
     * Sets the roleMemberRoleName attribute value.
     * @param roleMemberRoleName The roleMemberRoleName to set.
     */
    public void setRoleMemberRoleName(String roleMemberRoleName) {
        this.roleMemberRoleName = roleMemberRoleName;
        memberRole.setMemberName(roleMemberRoleName);
        delegationMemberRole.setMemberName(roleMemberRoleName);
    }
    /**
     * Gets the roleMemberRoleNamespaceCode attribute.
     * @return Returns the roleMemberRoleNamespaceCode.
     */
    public String getRoleMemberRoleNamespaceCode() {
        return roleMemberRoleNamespaceCode;
    }
    /**
     * Sets the roleMemberRoleNamespaceCode attribute value.
     * @param roleMemberRoleNamespaceCode The roleMemberRoleNamespaceCode to set.
     */
    public void setRoleMemberRoleNamespaceCode(String roleMemberRoleNamespaceCode) {
        this.roleMemberRoleNamespaceCode = roleMemberRoleNamespaceCode;
        memberRole.setMemberNamespaceCode(roleMemberRoleNamespaceCode);
        delegationMemberRole.setMemberNamespaceCode(roleMemberRoleNamespaceCode);
    }
    /**
     * Gets the organization attribute.
     * @return Returns the organization.
     */
    public Organization getOrganization() {
        return organization;
    }
    /**
     * Sets the organization attribute value.
     * @param organization The organization to set.
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the overrideCode attribute.
     * @return Returns the overrideCode.
     */
    public String getOverrideCode() {
        return this.overrideCode;
    }
    /**
     * Sets the overrideCode attribute value.
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCode(String overrideCode) {
        //updateAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, overrideCode);
        this.overrideCode = overrideCode;
    }
    /**
     * Gets the fromAmount attribute.
     * @return Returns the fromAmount.
     */
    public KualiDecimal getFromAmount() {
        return fromAmount;
    }

    public String getFromAmountStr() {
        return fromAmount==null?null:fromAmount.toString();
    }

    /**
     * Sets the fromAmount attribute value.
     * @param fromAmount The fromAmount to set.
     */
    public void setFromAmount(KualiDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public void setFromAmount(String fromAmount) {
        if(StringUtils.isNotEmpty(fromAmount) && StringUtils.isNumeric(fromAmount) ) {
            this.fromAmount = new KualiDecimal(fromAmount);
        }
        else {
            this.fromAmount = null;
        }
    }

    /**
     * Gets the toAmount attribute.
     * @return Returns the toAmount.
     */
    public KualiDecimal getToAmount() {
        return toAmount;
    }

    public String getToAmountStr() {
        return toAmount==null?null:toAmount.toString();
    }

    /**
     * Sets the toAmount attribute value.
     * @param toAmount The toAmount to set.
     */
    public void setToAmount(KualiDecimal toAmount) {
        this.toAmount = toAmount;
    }

    public void setToAmount(String toAmount) {
        if(StringUtils.isNotEmpty(toAmount) && StringUtils.isNumeric(toAmount) ) {
            this.toAmount = new KualiDecimal(toAmount);
        }
        else {
            this.toAmount = null;
        }
    }

    /**
     * Gets the activeFromDate attribute.
     * @return Returns the activeFromDate.
     */
    public Date getActiveFromDate() {
        return activeFromDate;
    }
    /**
     * Sets the activeFromDate attribute value.
     * @param activeFromDate The activeFromDate to set.
     */
    public void setActiveFromDate(java.util.Date activeFromDate) {
        this.activeFromDate = activeFromDate;
    }

    /**
     * Gets the activeToDate attribute.
     * @return Returns the activeToDate.
     */
    public Date getActiveToDate() {
        return activeToDate;
    }
    /**
     * Sets the activeToDate attribute value.
     * @param activeToDate The activeToDate to set.
     */
    public void setActiveToDate(java.util.Date activeToDate) {
        this.activeToDate = activeToDate;
    }

    /**
     * Gets the orgReviewRoleMemberId attribute.
     * @return Returns the orgReviewRoleMemberId.
     */
    public String getOrgReviewRoleMemberId() {
        return orgReviewRoleMemberId;
    }
    /**
     * Sets the orgReviewRoleMemberId attribute value.
     * @param orgReviewRoleMemberId The orgReviewRoleMemberId to set.
     */
    public void setOrgReviewRoleMemberId(String orgReviewRoleMemberId) {
        this.orgReviewRoleMemberId = orgReviewRoleMemberId;
    }

    @Override
    public void refresh() {}

    /**
     * Gets the financialSystemDocumentTypeCode attribute.
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentType() {
        if ( StringUtils.isBlank( financialSystemDocumentTypeCode ) ) {
            financialSystemDocumentType = null;
        } else {
            if ( financialSystemDocumentType == null || !StringUtils.equals(financialSystemDocumentTypeCode, financialSystemDocumentType.getName() ) ) {
                org.kuali.rice.kew.api.doctype.DocumentType temp = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(financialSystemDocumentTypeCode);
                if ( temp != null ) {
                    financialSystemDocumentType = DocumentType.from( temp );
                } else {
                    financialSystemDocumentType = null;
                }
            }
        }
        return financialSystemDocumentType;
    }

    /**
     * Gets the financialDocumentTypeCode attribute.
     * @return Returns the financialDocumentTypeCode.
     */
    public String getFinancialSystemDocumentTypeCode() {
        return financialSystemDocumentTypeCode;
    }
    /**
     * Sets the financialDocumentTypeCode attribute value.
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialSystemDocumentTypeCode(String financialSystemDocumentTypeCode) {
        boolean isChanged = !StringUtils.equals(this.financialSystemDocumentTypeCode, financialSystemDocumentTypeCode);
        this.financialSystemDocumentTypeCode = financialSystemDocumentTypeCode;
        setRoleNamesAndReviewIndicator(isChanged);
    }

    private void setRoleNamesAndReviewIndicator(boolean hasFinancialSystemDocumentTypeCodeChanged){
        if(hasFinancialSystemDocumentTypeCodeChanged){
            //If role id is populated role names to consider have already been narrowed down
            if(StringUtils.isNotBlank(getRoleId()) && StringUtils.isNotBlank(getRoleName())){
                setRoleNamesToConsider(Collections.singletonList(getRoleName()));
            } else {
                setRoleNamesToConsider();
            }
            if(isBothReviewRolesIndicator()) {
                setReviewRolesIndicatorOnDocTypeChange(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE);
            } else if(isAccountingOrgReviewRoleIndicator()) {
                setReviewRolesIndicatorOnDocTypeChange(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE);
            } else if(isOrgReviewRoleIndicator()) {
                setReviewRolesIndicatorOnDocTypeChange(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE);
            }
        }
    }

    /**
     * Sets the financialSystemDocumentTypeCode attribute value.
     * @param financialSystemDocumentTypeCode The financialSystemDocumentTypeCode to set.
     */
    public void setFinancialSystemDocumentType(DocumentTypeEBO financialSystemDocumentType) {
        this.financialSystemDocumentType = financialSystemDocumentType;
    }
    /**
     * Gets the delegationTypeCode attribute.
     * @return Returns the delegationTypeCode.
     */
    public String getDelegationTypeCode() {
        return delegationTypeCode;
    }

    public String getDelegationTypeCodeDescription() {
        return KimConstants.KimUIConstants.DELEGATION_TYPES.get(delegationTypeCode);
    }

    /**
     * Sets the delegationTypeCode attribute value.
     * @param delegationTypeCode The delegationTypeCode to set.
     */
    public void setDelegationTypeCode(String delegationTypeCode) {
        this.delegationTypeCode = delegationTypeCode;
    }

    /**
     * Gets the memberTypeCode attribute.
     * @return Returns the memberTypeCode.
     */
    public String getMemberTypeCodeDescription() {
        return KimConstants.KimUIConstants.KIM_MEMBER_TYPES_MAP.get(memberTypeCode);
    }
    /**
     * Sets the memberTypeCode attribute value.
     * @param memberTypeCode The memberTypeCode to set.
     */
    public void setMemberTypeCode(String memberTypeCode) {
        this.memberTypeCode = memberTypeCode;
    }
    /**
     * Sets the attributes attribute value.
     * @param attributes The attributes to set.
     */
    public void setAttributes(List<KfsKimDocumentAttributeData> attributes) {
        this.attributes = attributes;
    }

    public String getAttributeValue(String attributeName){
        KfsKimDocumentAttributeData attributeData = getAttribute(attributeName);
        return attributeData==null?"":attributeData.getAttrVal();
    }

    protected KfsKimDocumentAttributeData getAttribute(String attributeName){
        if(StringUtils.isNotBlank(attributeName)) {
            for(KfsKimDocumentAttributeData attribute: attributes){
                if( attribute.getKimAttribute()!=null
                        && StringUtils.equals(attribute.getKimAttribute().getAttributeName(),attributeName)){
                    return attribute;
                }
            }
        }
        return null;
    }

    /**
     * Gets the chartCode attribute.
     * @return Returns the chartCode.
     */
    public String getChartOfAccountsCode() {
        return this.chartOfAccountsCode;
    }
    /**
     * Gets the organizationCode attribute.
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return this.organizationCode;
    }
    /**
     * Sets the organizationCode attribute value.
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    /**
     * Gets the roleNamesToConsider attribute.
     * @return Returns the roleNamesToConsider.
     */
    public List<String> getRoleNamesToConsider() {
        if(roleNamesToConsider==null && getFinancialSystemDocumentTypeCode()!=null) {
            setRoleNamesToConsider();
        }
        return roleNamesToConsider;
    }
    public void setRoleNamesToConsider(List<String> narrowedDownRoleNames) {
        roleNamesToConsider = new ArrayList<String>( narrowedDownRoleNames );
    }
    /**
     * Sets the roleNamesToConsider attribute value.
     * @param roleNamesToConsider The roleNamesToConsider to set.
     */
    public void setRoleNamesToConsider() {
        roleNamesToConsider = getOrgReviewRoleService().getRolesToConsider(getFinancialSystemDocumentTypeCode());
    }
    /**
     * Gets the accountingOrgReviewRoleIndicator attribute.
     * @return Returns the accountingOrgReviewRoleIndicator.
     */
    public boolean isAccountingOrgReviewRoleIndicator() {
        return getRoleNamesToConsider()!=null &&
            getRoleNamesToConsider().contains(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
    }
    /**
     * Gets the bothReviewRolesIndicator attribute.
     * @return Returns the bothReviewRolesIndicator.
     */
    public boolean isBothReviewRolesIndicator() {
        return getRoleNamesToConsider()!=null &&
            getRoleNamesToConsider().contains(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME) &&
            getRoleNamesToConsider().contains(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
    }
    /**
     * Gets the orgReviewRoleIndicator attribute.
     * @return Returns the orgReviewRoleIndicator.
     */
    public boolean isOrgReviewRoleIndicator() {
        return getRoleNamesToConsider()!=null &&
            getRoleNamesToConsider().contains(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
    }
    /**
     * Gets the actionTypeCode attribute.
     * @return Returns the actionTypeCode.
     */
    public String getActionTypeCode() {
        return actionTypeCode;
    }

    public String getActionTypeCodeToDisplay() {
        if(roleRspActions==null || roleRspActions.isEmpty()) {
            return "";
        }
        return roleRspActions.get(0).getActionTypeCode();
    }

    /**
     *
     * This method fore readonlyalterdisplay
     *
     * @return
     */
    public String getActionTypeCodeDescription() {
        String actionTypeCodeDesc = CodeTranslator.arLabels.get(getActionTypeCodeToDisplay());
        return actionTypeCodeDesc==null?"":actionTypeCodeDesc;
    }

    /**
     * Sets the actionTypeCode attribute value.
     * @param actionTypeCode The actionTypeCode to set.
     */
    public void setActionTypeCode(String actionTypeCode) {
        this.actionTypeCode = actionTypeCode;
    }
    /**
     * Gets the priorityNumber attribute.
     * @return Returns the priorityNumber.
     */
    public String getPriorityNumber() {
        return priorityNumber;
    }

    public String getPriorityNumberToDisplay() {
        if(roleRspActions==null || roleRspActions.isEmpty() ) {
            return "";
        }
        return roleRspActions.get(0).getPriorityNumber()==null?"":roleRspActions.get(0).getPriorityNumber()+"";
    }

    /**
     * Sets the priorityNumber attribute value.
     * @param priorityNumber The priorityNumber to set.
     */
    public void setPriorityNumber(String priorityNumber) {
        this.priorityNumber = priorityNumber;
    }
    /**
     * Gets the actionPolicyCode attribute.
     * @return Returns the actionPolicyCode.
     */
    public String getActionPolicyCode() {
        return actionPolicyCode;
    }
    /**
     * Sets the actionPolicyCode attribute value.
     * @param actionPolicyCode The actionPolicyCode to set.
     */
    public void setActionPolicyCode(String actionPolicyCode) {
        this.actionPolicyCode = actionPolicyCode;
    }
    /**
     * Gets the ignorePrevious attribute.
     * @return Returns the ignorePrevious.
     */
    public boolean isForceAction() {
        return forceAction;
    }
    /**
     * Sets the ignorePrevious attribute value.
     * @param ignorePrevious The ignorePrevious to set.
     */
    public void setForceAction(boolean forceAction) {
        this.forceAction = forceAction;
    }

    /**
     * Gets the roleId attribute.
     * @return Returns the roleId.
     */
    public String getRoleId() {
        return roleId;
    }
    /**
     * Sets the roleId attribute value.
     * @param roleId The roleId to set.
     */
    public void setRoleId(String roleId) {
        Role roleInfo = KimApiServiceLocator.getRoleService().getRole(roleId);
        if ( roleInfo != null ) {
            setNamespaceCode(roleInfo.getNamespaceCode());
            setRoleName(roleInfo.getName());
            setKimTypeId(roleInfo.getKimTypeId());
        }
        this.roleId = roleId;
    }
    /**
     * Gets the reviewRolesIndicator attribute.
     * @return Returns the reviewRolesIndicator.
     */
    public String getReviewRolesIndicator() {
        return reviewRolesIndicator;
    }
    /**
     * Sets the reviewRolesIndicator attribute value.
     * @param reviewRolesIndicator The reviewRolesIndicator to set.
     */
    public void setReviewRolesIndicator(String reviewRolesIndicator) {
        this.reviewRolesIndicator = reviewRolesIndicator;
    }
    /**
     * Sets the reviewRolesIndicator attribute value.
     * @param reviewRolesIndicator The reviewRolesIndicator to set.
     */
    private void setReviewRolesIndicatorOnDocTypeChange(String reviewRolesIndicator) {
        this.reviewRolesIndicator = reviewRolesIndicator;
    }


    public boolean hasRole(){
        return StringUtils.isNotBlank(roleMemberRoleNamespaceCode) && StringUtils.isNotBlank(roleMemberRoleName);
    }

    public boolean hasGroup(){
        return StringUtils.isNotBlank(groupMemberGroupNamespaceCode) && StringUtils.isNotBlank(groupMemberGroupName);
    }

    public boolean hasPrincipal(){
        return StringUtils.isNotBlank(principalMemberPrincipalName);
    }

    public boolean hasAnyMember(){
        return hasRole() || hasGroup() || hasPrincipal();
    }

    public KfsKimDocDelegateMember getDelegationMemberOfType(String memberTypeCode){
        if(MemberType.ROLE.getCode().equals(memberTypeCode)){
            delegationMemberRole.setMemberId(roleMemberRoleId);
            delegationMemberRole.setMemberName(roleMemberRoleName);
            delegationMemberRole.setMemberNamespaceCode(roleMemberRoleNamespaceCode);
            delegationMemberRole.setRoleMemberId(roleMemberId);
            return delegationMemberRole;
        } else if(MemberType.GROUP.getCode().equals(memberTypeCode)){
            delegationMemberGroup.setMemberId(groupMemberGroupId);
            delegationMemberGroup.setMemberName(groupMemberGroupName);
            delegationMemberGroup.setMemberNamespaceCode(groupMemberGroupNamespaceCode);
            delegationMemberRole.setRoleMemberId(roleMemberId);
            return delegationMemberGroup;
        } else if(MemberType.PRINCIPAL.getCode().equals(memberTypeCode)){
            delegationMemberPerson.setMemberId(principalMemberPrincipalId);
            delegationMemberPerson.setMemberName(principalMemberPrincipalName);
            delegationMemberRole.setRoleMemberId(roleMemberId);
            return delegationMemberPerson;
        }
        return null;
    }

    public KfsKimDocRoleMember getRoleMemberOfType(String memberTypeCode){
        if(MemberType.ROLE.getCode().equals(memberTypeCode)){
            memberRole.setMemberId(roleMemberRoleId);
            memberRole.setMemberName(roleMemberRoleName);
            memberRole.setMemberNamespaceCode(roleMemberRoleNamespaceCode);
            return memberRole;
        } else if(MemberType.GROUP.getCode().equals(memberTypeCode)){
            memberGroup.setMemberId(groupMemberGroupId);
            memberGroup.setMemberName(groupMemberGroupName);
            memberGroup.setMemberNamespaceCode(groupMemberGroupNamespaceCode);
            return memberGroup;
        } else if(MemberType.PRINCIPAL.getCode().equals(memberTypeCode)){
            memberPerson.setMemberId(principalMemberPrincipalId);
            memberPerson.setMemberName(principalMemberPrincipalName);
            return memberPerson;
        }
        return null;
    }

    public void setRoleMember( RoleMemberContract roleMember ) {
        memberTypeCode = roleMember.getType().getCode();
        if(MemberType.ROLE.equals(roleMember.getType())){
            roleMemberRoleId = roleMember.getMemberId();
            roleMemberRoleNamespaceCode = roleMember.getMemberNamespaceCode();
            roleMemberRoleName = roleMember.getMemberName();
        } else if(MemberType.GROUP.equals(roleMember.getType())){
            groupMemberGroupId = roleMember.getMemberId();
            groupMemberGroupNamespaceCode = roleMember.getMemberNamespaceCode();
            groupMemberGroupName = roleMember.getMemberName();
        } else if(MemberType.PRINCIPAL.equals(roleMember.getType())){
            principalMemberPrincipalId = roleMember.getMemberId();
            principalMemberPrincipalName = roleMember.getMemberName();
        }

        if ( roleMember.getActiveFromDate() != null ) {
            setActiveFromDate(roleMember.getActiveFromDate().toDate());
        } else {
            setActiveFromDate( null );
        }
        if ( roleMember.getActiveToDate() != null ) {
            setActiveToDate(roleMember.getActiveToDate().toDate());
        } else {
            setActiveToDate( null );
        }
        setActive(roleMember.isActive());

        setRoleMemberId(roleMember.getId());
        setDelegate(false);
        setRoleId(roleMember.getRoleId());

        setRoleRspActions(KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(roleMember.getId()));

        extractAttributesFromMap(roleMember.getAttributes());
    }

    public void extractAttributesFromMap( Map<String,String> attributes ) {
        setAttributes(getAttributeSetAsQualifierList(attributes));


        setChartOfAccountsCode(getAttributeValue(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        setOrganizationCode(getAttributeValue(KfsKimAttributes.ORGANIZATION_CODE));
        setOverrideCode(getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE));
        setFromAmount(getAttributeValue(KfsKimAttributes.FROM_AMOUNT));
        setToAmount(getAttributeValue(KfsKimAttributes.TO_AMOUNT));
        setFinancialSystemDocumentTypeCode(getAttributeValue(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
    }

    public void setDelegateMember( RoleMemberContract roleMember, DelegateMemberContract delegateMember ) {
        if ( roleMember == null ) {
            roleMember = getRoleMemberFromKimRoleService( delegateMember.getRoleMemberId() );
        }
        setRoleId( roleMember.getRoleId() );
        memberTypeCode = delegateMember.getType().getCode();
        if(MemberType.ROLE.equals(delegateMember.getType())){
            roleMemberRoleId = delegateMember.getMemberId();
            getRole();
        } else if(MemberType.GROUP.equals(delegateMember.getType())){
            groupMemberGroupId = delegateMember.getMemberId();
            getGroup();
        } else if(MemberType.PRINCIPAL.equals(delegateMember.getType())){
            principalMemberPrincipalId = delegateMember.getMemberId();
            getPerson();
        }

        if ( delegateMember.getActiveFromDate() != null ) {
            setActiveFromDate(delegateMember.getActiveFromDate().toDate());
        }
        if ( delegateMember.getActiveToDate() != null ) {
            setActiveToDate(delegateMember.getActiveToDate().toDate());
        }
        setActive(delegateMember.isActive());
        setDelegate(true);
        setDelegationMemberId(delegateMember.getDelegationMemberId());
        setRoleMemberId(roleMember.getId());

        extractAttributesFromMap(delegateMember.getAttributes());
    }

    protected RoleMember getRoleMemberFromKimRoleService( String roleMemberId ) {
        RoleMemberQueryResults roleMembers = KimApiServiceLocator.getRoleService().findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ID, roleMemberId))));
        if ( roleMembers == null || roleMembers.getResults() == null || roleMembers.getResults().isEmpty() ) {
            throw new IllegalArgumentException( "Unknown role member ID passed in - nothing returned from KIM RoleService: " + roleMemberId );
        }
        return roleMembers.getResults().get(0);
    }
    
//    public String getMemberIdForDelegationMember(String memberTypeCode){
//        KfsKimDocDelegateMember member = getDelegationMemberOfType(memberTypeCode);
//        return member!=null?member.getMemberId():null;
//    }
//
//    public String getMemberIdForRoleMember(String memberTypeCode){
//        KfsKimDocRoleMember member = getRoleMemberOfType(memberTypeCode);
//        return member!=null?member.getMemberId():null;
//    }


    public String getMemberId() {
        if(MemberType.ROLE.getCode().equals(memberTypeCode)){
            return getRoleMemberRoleId();
        } else if(MemberType.GROUP.getCode().equals(memberTypeCode)){
            return getGroupMemberGroupId();
        } else if(MemberType.PRINCIPAL.getCode().equals(memberTypeCode)){
            return getPrincipalMemberPrincipalId();
        }
        return "";
    }

    public String getMemberName() {
        if(MemberType.ROLE.getCode().equals(memberTypeCode)){
            return getRoleMemberRoleName();
        } else if(MemberType.GROUP.getCode().equals(memberTypeCode)){
            return getGroupMemberGroupName();
        } else if(MemberType.PRINCIPAL.getCode().equals(memberTypeCode)){
            return getPrincipalMemberName();
        }
        return "";
    }

    public String getMemberNamespaceCode() {
        if(MemberType.ROLE.getCode().equals(memberTypeCode)){
            return getRoleMemberRoleNamespaceCode();
        } else if(MemberType.GROUP.getCode().equals(memberTypeCode)){
            return getGroupMemberGroupNamespaceCode();
        } else if(MemberType.PRINCIPAL.getCode().equals(memberTypeCode)){
            return "";
        }
        return "";
    }

    public String getMemberFieldName( MemberType memberType ){
        if(MemberType.ROLE.equals(memberType)) {
            return ROLE_NAME_FIELD_NAME;
        } else if(MemberType.GROUP.equals(memberType)) {
            return GROUP_NAME_FIELD_NAME;
        } else if(MemberType.PRINCIPAL.equals(memberType)) {
            return PRINCIPAL_NAME_FIELD_NAME;
        }
        return null;
    }

    /**
     * Gets the memberTypeCode attribute.
     * @return Returns the memberTypeCode.
     */
    public String getMemberTypeCode() {
        return memberTypeCode;
    }
    /**
     * Gets the group attribute.
     * @return Returns the group.
     */
    public GroupEbo getGroup() {
        if ( group == null || !StringUtils.equals(group.getId(), groupMemberGroupId)) {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(GroupEbo.class);
            if ( moduleService != null ) {
                Map<String,Object> keys = new HashMap<String, Object>(1);
                keys.put(KimConstants.PrimaryKeyConstants.ID, groupMemberGroupId);
                group = moduleService.getExternalizableBusinessObject(GroupEbo.class, keys);
                groupMemberGroupNamespaceCode = group.getNamespaceCode();
                groupMemberGroupName = group.getName();
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }
        }
        return group;
    }
    /**
     * Sets the group attribute value.
     * @param group The group to set.
     */
    public void setGroup(GroupEbo group) {
        this.group = group;
    }
    /**
     * Gets the person attribute.
     * @return Returns the person.
     */
    public Person getPerson() {
        if( (StringUtils.isNotEmpty(principalMemberPrincipalId)
                || StringUtils.isNotEmpty(principalMemberPrincipalName))
                &&
                (person==null || !StringUtils.equals(person.getPrincipalId(), principalMemberPrincipalId) ) ) {
            if ( StringUtils.isNotEmpty(principalMemberPrincipalId) ) {
                person = KimApiServiceLocator.getPersonService().getPerson(principalMemberPrincipalId);
            } else if ( StringUtils.isNotEmpty(principalMemberPrincipalName) ) {
                person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalMemberPrincipalName);
            }
            if ( person != null ) {
                principalMemberPrincipalId = person.getPrincipalId();
                principalMemberPrincipalName = person.getPrincipalName();
                principalMemberName = person.getName();
            } else {
                principalMemberPrincipalId = "";
            }
        }
        return person;
    }
    /**
     * Sets the person attribute value.
     * @param person The person to set.
     */
    public void setPerson(Person person) {
        this.person = person;
        if ( person != null ) {
            principalMemberPrincipalName = person.getPrincipalName();
            principalMemberPrincipalId = person.getPrincipalId();
            principalMemberName = person.getName();
        }
    }

    /**
     * Gets the role attribute.
     * @return Returns the role.
     */
    public RoleEbo getRole() {
        if ( role == null || !StringUtils.equals(role.getId(), roleMemberRoleId)) {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(RoleEbo.class);
            if ( moduleService != null ) {
                Map<String,Object> keys = new HashMap<String, Object>(1);
                keys.put(KimConstants.PrimaryKeyConstants.ID, roleMemberRoleId);
                role = moduleService.getExternalizableBusinessObject(RoleEbo.class, keys);
                roleMemberRoleNamespaceCode = role.getNamespaceCode();
                roleMemberRoleName = role.getName();
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }
        }
        return role;
    }

    /**
     * Gets the delegationMemberGroup attribute.
     * @return Returns the delegationMemberGroup.
     */
    public KfsKimDocDelegateMember getDelegationMemberGroup() {
        return delegationMemberGroup;
    }
    /**
     * Sets the delegationMemberGroup attribute value.
     * @param delegationMemberGroup The delegationMemberGroup to set.
     */
    public void setDelegationMemberGroup(DelegateMemberContract delegationMemberGroup) {
        this.delegationMemberGroup = new KfsKimDocDelegateMember( delegationMemberGroup );
//        if(delegationMemberGroup!=null){
//            Group groupInfo = KimApiServiceLocator.getGroupService().getGroup(memberGroup.getMemberId());
//            setGroup(groupInfo);
//        }
    }
    /**
     * Gets the delegationMemberPerson attribute.
     * @return Returns the delegationMemberPerson.
     */
    public KfsKimDocDelegateMember getDelegationMemberPerson() {
        return delegationMemberPerson;
    }
    /**
     * Sets the delegationMemberPerson attribute value.
     * @param delegationMemberPerson The delegationMemberPerson to set.
     */
    public void setDelegationMemberPerson(DelegateMemberContract delegationMemberPerson) {
        this.delegationMemberPerson = new KfsKimDocDelegateMember( delegationMemberPerson );
//        if(delegationMemberPerson!=null){
//            Person person = KimApiServiceLocator.getPersonService().getPerson(delegationMemberPerson.getMemberId());
//            setPerson(person);
//        }
    }
    /**
     * Gets the delegationMemberRole attribute.
     * @return Returns the delegationMemberRole.
     */
    public KfsKimDocDelegateMember getDelegationMemberRole() {
        return delegationMemberRole;
    }
    /**
     * Sets the delegationMemberRole attribute value.
     * @param delegationMemberRole The delegationMemberRole to set.
     */
    public void setDelegationMemberRole(DelegateMemberContract delegationMemberRole) {
        this.delegationMemberRole = new KfsKimDocDelegateMember( delegationMemberRole );
//        if(delegationMemberRole!=null){
//            RoleEbo roleInfo = RoleEbo.from(KimApiServiceLocator.getRoleService().getRole(delegationMemberRole.getMemberId()));
//            setRole(roleInfo);
//        }
    }
    /**
     * Gets the memberGroup attribute.
     * @return Returns the memberGroup.
     */
    public KfsKimDocRoleMember getMemberGroup() {
        return memberGroup;
    }
    /**
     * Sets the memberGroup attribute value.
     * @param memberGroup The memberGroup to set.
     */
    protected void setMemberGroup(RoleMemberContract memberGroup) {
        this.memberGroup = new KfsKimDocRoleMember( memberGroup );
//        if(memberGroup!=null){
//            Group groupInfo = KimApiServiceLocator.getGroupService().getGroup(memberGroup.getMemberId());
//            setGroup(groupInfo);
//        }
    }
    /**
     * Gets the memberPerson attribute.
     * @return Returns the memberPerson.
     */
    public KfsKimDocRoleMember getMemberPerson() {
        return memberPerson;
    }
    /**
     * Sets the memberPerson attribute value.
     * @param memberPerson The memberPerson to set.
     */
    protected void setMemberPerson(RoleMemberContract memberPerson) {
        this.memberPerson = new KfsKimDocRoleMember( memberPerson );
//        if(memberPerson!=null){
//            Person personImpl = getPersonFromService(memberPerson.getMemberId());
//            setPerson(personImpl);
//        }
    }
    /**
     * Gets the memberRole attribute.
     * @return Returns the memberRole.
     */
    public KfsKimDocRoleMember getMemberRole() {
        return memberRole;
    }
    /**
     * Sets the memberRole attribute value.
     * @param memberRole The memberRole to set.
     */
    protected void setMemberRole(KfsKimDocRoleMember memberRole) {
        this.memberRole = memberRole;
//        if(memberRole!=null){
//            Role roleInfo = KimApiServiceLocator.getRoleService().getRole(memberRole.getMemberId());
////            setRole(roleInfo);
//        }
    }

    public void setRoleDocumentDelegationMember(DelegateMember delegationMember){
        if ( delegationMember != null ) {
            if(MemberType.ROLE.equals(delegationMember.getType())) {
                setDelegationMemberRole(delegationMember);
            }
            else if(MemberType.GROUP.equals(delegationMember.getType())) {
                setDelegationMemberGroup(delegationMember);
            }
            else if(MemberType.PRINCIPAL.equals(delegationMember.getType())) {
                setDelegationMemberPerson(delegationMember);
            }
            if ( delegationMember.getActiveFromDate() != null ) {
                setActiveFromDate(delegationMember.getActiveFromDate().toDate());
            } else {
                setActiveFromDate( null );
            }
            if ( delegationMember.getActiveToDate() != null ) {
                setActiveToDate(delegationMember.getActiveToDate().toDate());
            } else {
                setActiveToDate( null );
            }
        } else {
            setDelegationMemberRole( new KfsKimDocDelegateMember( roleId, MemberType.ROLE ) );
            setDelegationMemberGroup( new KfsKimDocDelegateMember( roleId, MemberType.GROUP ) );
            setDelegationMemberPerson( new KfsKimDocDelegateMember( roleId, MemberType.PRINCIPAL ) );
            setActiveFromDate(null);
            setActiveToDate(null);
        }
    }

    public void setKimDocumentRoleMember(RoleMember roleMember){
        if ( roleMember != null ) {
            if(MemberType.ROLE.equals(roleMember.getType())) {
                setMemberRole( new KfsKimDocRoleMember(roleMember));
            }
            else if(MemberType.GROUP.equals(roleMember.getType())) {
                setMemberGroup(roleMember);
            }
            else if(MemberType.PRINCIPAL.equals(roleMember.getType())) {
                setMemberPerson(roleMember);
            }
            if ( roleMember.getActiveFromDate() != null ) {
                setActiveFromDate(roleMember.getActiveFromDate().toDate());
            } else {
                setActiveFromDate( null );
            }
            if ( roleMember.getActiveToDate() != null ) {
                setActiveToDate(roleMember.getActiveToDate().toDate());
            } else {
                setActiveToDate( null );
            }
        } else {
            setMemberRole( new KfsKimDocRoleMember(roleId, MemberType.ROLE) );
            setMemberGroup( new KfsKimDocRoleMember(roleId, MemberType.GROUP) );
            setMemberPerson( new KfsKimDocRoleMember(roleId, MemberType.PRINCIPAL) );
            setActiveFromDate(null);
            setActiveToDate(null);
        }
    }

    /**
     * Gets the copy attribute.
     * @return Returns the copy.
     */
    public boolean isCopy() {
        return copy || KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equalsIgnoreCase(methodToCall);
    }
    /**
     * Sets the copy attribute value.
     * @param copy The copy to set.
     */
    public void setCopy(boolean copy) {
        this.copy = copy;
    }
    /**
     * Gets the edit attribute.
     * @return Returns the edit.
     */
    public boolean isEdit() {
        return edit || KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL.equalsIgnoreCase(methodToCall);
    }
    /**
     * Sets the edit attribute value.
     * @param edit The edit to set.
     */
    public void setEdit(boolean edit) {
        this.edit = edit;
    }
    /**
     * Gets the oDelMId attribute.
     * @return Returns the oDelMId.
     */
    public String getODelMId() {
        return oDelMId;
    }
    /**
     * Sets the oDelMId attribute value.
     * @param delMId The oDelMId to set.
     */
    public void setODelMId(String delMId) {
        oDelMId = delMId;
    }
    /**
     * Gets the oRMId attribute.
     * @return Returns the oRMId.
     */
    public String getORMId() {
        return oRMId;
    }
    /**
     * Sets the oRMId attribute value.
     * @param id The oRMId to set.
     */
    public void setORMId(String id) {
        oRMId = id;
    }
    /**
     * Gets the delegationMemberId attribute.
     * @return Returns the delegationMemberId.
     */
    public String getDelegationMemberId() {
        return delegationMemberId;
    }
    /**
     * Sets the delegationMemberId attribute value.
     * @param delegationMemberId The delegationMemberId to set.
     */
    public void setDelegationMemberId(String delegationMemberId) {
        this.delegationMemberId = delegationMemberId;
    }
    /**
     * Gets the roleMemberId attribute.
     * @return Returns the roleMemberId.
     */
    public String getRoleMemberId() {
        return roleMemberId;
    }
    /**
     * Sets the roleMemberId attribute value.
     * @param roleMemberId The roleMemberId to set.
     */
    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }
    /**
     * Gets the methodToCall attribute.
     * @return Returns the methodToCall.
     */
    public String getMethodToCall() {
        return methodToCall;
    }
    /**
     * Sets the methodToCall attribute value.
     * @param methodToCall The methodToCall to set.
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public boolean isEditDelegation(){
        return isEdit() && isDelegate();
    }

    public boolean isEditRoleMember(){
        return isEdit() && !isDelegate();
    }

    public boolean isCopyDelegation(){
        return isCopy() && isDelegate();
    }

    public boolean isCopyRoleMember(){
        return isCopy() && !isDelegate();
    }

    public boolean isCreateDelegation(){
        return NEW_DELEGATION_ID_KEY_VALUE.equals(getODelMId());
    }

    public boolean isCreateRoleMember(){
        return StringUtils.isEmpty(methodToCall);
    }

    public String getOrganizationTypeCode() {
        return "99";
    }
    public void setOrganizationTypeCode(String organizationTypeCode) {
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
        setRoleNamesToConsider( Collections.singletonList(roleName) );
    }
    public String getNamespaceCode() {
        return namespaceCode;
    }
    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    @Override
    public Long getVersionNumber(){
        return 1L;
    }

    public String getKimTypeId() {
        return kimTypeId;
    }
    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    public Map<String,String> getQualifierAsAttributeSet(List<KfsKimDocumentAttributeData> qualifiers) {
        Map<String,String> m = new HashMap<String,String>();
        for(KfsKimDocumentAttributeData data: qualifiers){
            m.put(data.getKimAttribute().getAttributeName(), data.getAttrVal());
        }
        return m;
    }

    public List<KfsKimDocumentAttributeData> getAttributeSetAsQualifierList( Map<String,String> qualifiers) {
        KimType kimTypeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
        List<KfsKimDocumentAttributeData> attributesList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attribData;
        for(String key: qualifiers.keySet()){
            KimTypeAttribute attribInfo = kimTypeInfo.getAttributeDefinitionByName(key);
            attribData = new KfsKimDocumentAttributeData();
            attribData.setKimAttribute(attribInfo.getKimAttribute());
            attribData.setKimTypId(kimTypeInfo.getId());
            attribData.setKimAttrDefnId(attribInfo.getId());
            //attribData.setAttrDataId(attrDataId) - Not Available
            attribData.setAttrVal(qualifiers.get(key));
            attributesList.add(attribData);
        }
        return attributesList;
    }
    /**
     * Gets the roleRspActions attribute.
     * @return Returns the roleRspActions.
     */
    public List<RoleResponsibilityAction> getRoleRspActions() {
        return roleRspActions;
    }
    /**
     * Sets the roleRspActions attribute value.
     * @param roleRspActions The roleRspActions to set.
     */
    public void setRoleRspActions(List<RoleResponsibilityAction> roleRspActions) {
        this.roleRspActions = roleRspActions;
    }

    public String getOrgReviewRoleInquiryTitle() {
        if ( INQUIRY_TITLE_VALUE == null ) {
            INQUIRY_TITLE_VALUE = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ORR_INQUIRY_TITLE_PROPERTY);
        }
        return INQUIRY_TITLE_VALUE;
    }

    @Override
    public void refreshNonUpdateableReferences() {
        // do nothing
    }

    @Override
    public void refreshReferenceObject(String referenceObjectName) {
        // do nothing
    }

    public static OrgReviewRoleService getOrgReviewRoleService() {
        if ( orgReviewRoleService == null ) {
            orgReviewRoleService = SpringContext.getBean(OrgReviewRoleService.class);
        }
        return orgReviewRoleService;
    }
}
