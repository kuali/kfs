/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.coa.service.OrganizationService;
import edu.arizona.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import edu.arizona.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kim.api.KimConstants;
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

    private static transient OrgReviewRoleService orgReviewRoleService;
    private static transient OrganizationService organizationService;
    private static transient ChartService chartService;

    //Dummy variable
    protected String organizationTypeCode = "99";
    private static final long serialVersionUID = 1L;

    public static final String REVIEW_ROLES_INDICATOR_FIELD_NAME = "reviewRolesIndicator";
    public static final String ROLE_NAME_FIELD_NAMESPACE_CODE = "roleMemberRoleNamespaceCode";
    public static final String ROLE_NAME_FIELD_NAME = "roleMemberRoleName";
    public static final String GROUP_NAME_FIELD_NAMESPACE_CODE = "groupMemberGroupNamespaceCode";
    public static final String GROUP_NAME_FIELD_NAME = "groupMemberGroupName";
    public static final String PRINCIPAL_NAME_FIELD_NAME = "principalMemberPrincipalName";
    public static final String CHART_CODE_FIELD_NAME = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
    public static final String ORG_CODE_FIELD_NAME = KFSPropertyConstants.ORGANIZATION_CODE;
    public static final String DOC_TYPE_NAME_FIELD_NAME = "financialSystemDocumentTypeCode";
    public static final String DELEGATE_FIELD_NAME = "delegate";
    public static final String DELEGATION_TYPE_CODE = "delegationTypeCode";
    public static final String FROM_AMOUNT_FIELD_NAME = "fromAmount";
    public static final String TO_AMOUNT_FIELD_NAME = "toAmount";
    public static final String FUND_GROUP_FIELD_NAME = KfsKimAttributes.FUND_GROUP_CODE;
    public static final String SUB_FUND_GROUP_FIELD_NAME = KfsKimAttributes.SUB_FUND_GROUP_CODE;
    public static final String OBJECT_SUB_TYPE_FIELD_NAME = KfsKimAttributes.OBJECT_SUB_TYPE_CODE;
    public static final String OVERRIDE_CODE_FIELD_NAME = KFSPropertyConstants.OVERRIDE_CODE;
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
    protected FundGroup fundGroup;
    protected SubFundGroup subFundGroup;
    protected ObjectSubType objectSubType;
    protected boolean edit;
    protected boolean copy;

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
    protected String fundGroupCode;
    protected String subFundGroupCode;
    protected String financialObjectSubTypeCode;
    protected KualiDecimal fromAmount;
    protected KualiDecimal toAmount;
    protected String overrideCode;
    protected boolean active = true;
    protected boolean delegate;

    protected Date activeFromDate;
    protected Date activeToDate;

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
        if ( StringUtils.isBlank(getChartOfAccountsCode() ) ) {
            chart = null;
        } else {
            if ( chart == null || !StringUtils.equals(getChartOfAccountsCode(), chart.getChartOfAccountsCode()) ) {
                chart = getChartService().getByPrimaryId(getChartOfAccountsCode());
            }
        }
        return chart;
    }

    /**
     * Gets the groupMemberGroupId attribute.
     * @return Returns the groupMemberGroupId.
     */
    public String getGroupMemberGroupId() {
        if ( StringUtils.isBlank(groupMemberGroupId) ) {
            if ( StringUtils.isNotBlank(groupMemberGroupNamespaceCode) && StringUtils.isNotBlank(groupMemberGroupName) ) {
                getGroup();
            }
        }
        return groupMemberGroupId;
    }
    /**
     * Sets the groupMemberGroupId attribute value.
     * @param groupMemberGroupId The groupMemberGroupId to set.
     */
    public void setGroupMemberGroupId(String groupMemberGroupId) {
        this.groupMemberGroupId = groupMemberGroupId;
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
    }
    /**
     * Gets the principalMemberPrincipalId attribute.
     * @return Returns the principalMemberPrincipalId.
     */
    public String getPrincipalMemberPrincipalId() {
        if ( StringUtils.isBlank(principalMemberPrincipalId) ) {
            if ( StringUtils.isNotBlank(principalMemberPrincipalName) ) {
                getPerson();
            }
        }
        return principalMemberPrincipalId;
    }
    /**
     * Sets the principalMemberPrincipalId attribute value.
     * @param principalMemberPrincipalId The principalMemberPrincipalId to set.
     */
    public void setPrincipalMemberPrincipalId(String principalMemberPrincipalId) {
        this.principalMemberPrincipalId = principalMemberPrincipalId;
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
    }
    /**
     * Gets the roleMemberRoleId attribute.
     * @return Returns the roleMemberRoleId.
     */
    public String getRoleMemberRoleId() {
        if ( StringUtils.isBlank(roleMemberRoleId) ) {
            if ( StringUtils.isNotBlank(roleMemberRoleName) && StringUtils.isNotBlank(roleMemberRoleName) ) {
                getRole();
            }
        }
        return roleMemberRoleId;
    }
    /**
     * Sets the roleMemberRoleId attribute value.
     * @param roleMemberRoleId The roleMemberRoleId to set.
     */
    public void setRoleMemberRoleId(String roleMemberRoleId) {
        this.roleMemberRoleId = roleMemberRoleId;
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
    }
    /**
     * Gets the organization attribute.
     * @return Returns the organization.
     */
    public Organization getOrganization() {
        if ( StringUtils.isBlank(getChartOfAccountsCode() ) || StringUtils.isBlank(getOrganizationCode()) ) {
            organization = null;
        } else {
            if ( organization == null || !StringUtils.equals(getChartOfAccountsCode(), chart.getChartOfAccountsCode()) || !StringUtils.equals(getOrganizationCode(), organization.getOrganizationCode()) ) {
                organization = getOrganizationService().getByPrimaryIdWithCaching(getChartOfAccountsCode(), getOrganizationCode());
            }
        }
        return organization;
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
        if(StringUtils.isNotEmpty(fromAmount) && NumberUtils.isNumber( fromAmount ) ) {
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
        if(StringUtils.isNotEmpty(toAmount) && NumberUtils.isNumber( toAmount ) ) {
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
            } else if(isOrgFundReviewRoleIndicator()) {
            	setReviewRolesIndicatorOnDocTypeChange(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_FUND_ONLY_CODE);
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
        if ( getDelegationType() != null ) {
            return getDelegationType().getLabel();
        }
        return "";
    }

    public DelegationType getDelegationType() {
        return DelegationType.parseCode(delegationTypeCode);
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
        return KimConstants.KimUIConstants.KIM_MEMBER_TYPES_MAP.get(getMemberTypeCode());
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

    public List<KfsKimDocumentAttributeData> getAttributes() {
        return attributes;
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
            getRoleNamesToConsider().contains(KFSConstants.SysKimApiConstants.ORGANIZATION_FUND_REVIEWER_ROLE_NAME) &&
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
     * Gets the orgFundReviewRoleIndicator attribute.
     * @return Returns the orgFundReviewRoleIndicator
     */
    public boolean isOrgFundReviewRoleIndicator() {
    	return getRoleNamesToConsider() != null &&
    			getRoleNamesToConsider().contains(KFSConstants.SysKimApiConstants.ORGANIZATION_FUND_REVIEWER_ROLE_NAME);
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
        ActionType at = ActionType.fromCode(getActionTypeCodeToDisplay(), true);
        return (at==null)?"":at.getLabel();
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
        getRole();
        return StringUtils.isNotBlank(roleMemberRoleName);
    }

    public boolean hasGroup(){
        getGroup();
        return StringUtils.isNotBlank(groupMemberGroupName);
    }

    public boolean hasPrincipal(){
        getPerson();
        return StringUtils.isNotBlank(principalMemberPrincipalName);
    }

    public boolean hasAnyMember(){
        return hasRole() || hasGroup() || hasPrincipal();
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

    public String getMemberId() {
        if(MemberType.ROLE.getCode().equals(getMemberTypeCode())){
            return getRoleMemberRoleId();
        } else if(MemberType.GROUP.getCode().equals(getMemberTypeCode())){
            return getGroupMemberGroupId();
        } else if(MemberType.PRINCIPAL.getCode().equals(getMemberTypeCode())){
            return getPrincipalMemberPrincipalId();
        }
        return "";
    }

    public String getMemberName() {
        if(MemberType.ROLE.getCode().equals(getMemberTypeCode())){
            return getRoleMemberRoleName();
        } else if(MemberType.GROUP.getCode().equals(getMemberTypeCode())){
            return getGroupMemberGroupName();
        } else if(MemberType.PRINCIPAL.getCode().equals(getMemberTypeCode())){
            return getPrincipalMemberName();
        }
        return "";
    }

    public String getMemberNamespaceCode() {
        if(MemberType.ROLE.getCode().equals(getMemberTypeCode())){
            return getRoleMemberRoleNamespaceCode();
        } else if(MemberType.GROUP.getCode().equals(getMemberTypeCode())){
            return getGroupMemberGroupNamespaceCode();
        } else if(MemberType.PRINCIPAL.getCode().equals(getMemberTypeCode())){
            return "";
        }
        return "";
    }

    public String getMemberFieldName(){
        if(MemberType.ROLE.equals(getMemberType())) {
            return ROLE_NAME_FIELD_NAME;
        } else if(MemberType.GROUP.equals(getMemberType())) {
            return GROUP_NAME_FIELD_NAME;
        } else if(MemberType.PRINCIPAL.equals(getMemberType())) {
            return PRINCIPAL_NAME_FIELD_NAME;
        }
        return null;
    }

    /**
     * Gets the memberTypeCode attribute.
     * @return Returns the memberTypeCode.
     */
    public String getMemberTypeCode() {
        if ( StringUtils.isBlank(memberTypeCode) ) {
            if ( StringUtils.isNotBlank(principalMemberPrincipalId) ) {
                memberTypeCode = MemberType.PRINCIPAL.getCode();
            } else if ( StringUtils.isNotBlank(groupMemberGroupId) ) {
                memberTypeCode = MemberType.GROUP.getCode();
            } else if ( StringUtils.isNotBlank(roleMemberRoleId) ) {
                memberTypeCode = MemberType.ROLE.getCode();
            }
        }
        return memberTypeCode;
    }

    public MemberType getMemberType() {
        if ( StringUtils.isBlank(getMemberTypeCode()) ) {
            return null;
        }
        return MemberType.fromCode(getMemberTypeCode());
    }

    /**
     * Gets the group attribute.
     * @return Returns the group.
     */
    public GroupEbo getGroup() {
        if ( (group == null || !StringUtils.equals(group.getId(), groupMemberGroupId)) && StringUtils.isNotBlank(groupMemberGroupId) ) {
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
        } else if ( StringUtils.isNotBlank(groupMemberGroupName) ) {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(GroupEbo.class);
            if ( moduleService != null ) {
                // if we have both a namespace and a name
                if ( StringUtils.isNotBlank(groupMemberGroupNamespaceCode) ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, groupMemberGroupNamespaceCode);
                    keys.put(KimConstants.UniqueKeyConstants.GROUP_NAME, groupMemberGroupName);
                    List<GroupEbo> groups = moduleService.getExternalizableBusinessObjectsList(GroupEbo.class, keys);
                    // this *should* only retrieve a single record
                    if ( groups != null && !groups.isEmpty() ) {
                        group = groups.get(0);
                        groupMemberGroupId = group.getId();
                    } else {
                        group = null;
                        groupMemberGroupId = "";
                    }
                } else { // if we only have the name - see if it's unique
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(KimConstants.UniqueKeyConstants.GROUP_NAME, groupMemberGroupName);
                    List<GroupEbo> groups = moduleService.getExternalizableBusinessObjectsList(GroupEbo.class, keys);
                    // if retrieves a single record, then it's unique, we set it and the namespace
                    if ( groups != null && groups.size() == 1 ) {
                        group = groups.get(0);
                        groupMemberGroupId = group.getId();
                        groupMemberGroupNamespaceCode = group.getNamespaceCode();
                    } else {
                        group = null;
                        groupMemberGroupId = "";
                    }
                }
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }
        } else {
            group = null;
        }
        return group;
    }
    /**
     * Sets the group attribute value.
     * @param group The group to set.
     */
    public void setGroup(GroupEbo group) {
        this.group = group;
        if ( group != null ) {
            groupMemberGroupNamespaceCode = group.getNamespaceCode();
            groupMemberGroupName = group.getName();
            groupMemberGroupId = group.getId();
        } else {
            groupMemberGroupNamespaceCode = "";
            groupMemberGroupName = "";
            groupMemberGroupId = "";
        }
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
            } else {
                person = null;
            }
            if ( person != null ) {
                principalMemberPrincipalId = person.getPrincipalId();
                principalMemberPrincipalName = person.getPrincipalName();
                principalMemberName = person.getName();
            } else {
                principalMemberPrincipalId = "";
                principalMemberName = "";
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
        } else {
            principalMemberPrincipalId = "";
            principalMemberPrincipalName = "";
            principalMemberName = "";
        }
    }

    /**
     * Gets the role attribute.
     * @return Returns the role.
     */
    public RoleEbo getRole() {
        if ( (role == null || !StringUtils.equals(role.getId(), roleMemberRoleId)) && StringUtils.isNotBlank(roleMemberRoleId) ) {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(RoleEbo.class);
            if ( moduleService != null ) {
                Map<String,Object> keys = new HashMap<String, Object>(1);
                keys.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleMemberRoleId);
                role = moduleService.getExternalizableBusinessObject(RoleEbo.class, keys);
                roleMemberRoleNamespaceCode = role.getNamespaceCode();
                roleMemberRoleName = role.getName();
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }
        } else if ( StringUtils.isNotBlank(roleMemberRoleName) ) {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(RoleEbo.class);
            if ( moduleService != null ) {
                // if we have both a namespace and a name
                if ( StringUtils.isNotBlank(roleMemberRoleNamespaceCode) ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, roleMemberRoleNamespaceCode);
                    keys.put(KimConstants.UniqueKeyConstants.NAME, roleMemberRoleName);
                    List<RoleEbo> roles = moduleService.getExternalizableBusinessObjectsList(RoleEbo.class, keys);
                    // this *should* only retrieve a single record
                    if ( roles != null && !roles.isEmpty() ) {
                        role = roles.get(0);
                        roleMemberRoleId = role.getId();
                    } else {
                        role = null;
                        roleMemberRoleId = "";
                    }
                } else { // if we only have the name - see if it's unique
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(KimConstants.UniqueKeyConstants.NAME, roleMemberRoleName);
                    List<RoleEbo> roles = moduleService.getExternalizableBusinessObjectsList(RoleEbo.class, keys);
                    // if retrieves a single record, then it's unique, we set it and the namespace
                    if ( roles != null && roles.size() == 1 ) {
                        role = roles.get(0);
                        roleMemberRoleId = role.getId();
                        roleMemberRoleNamespaceCode = role.getNamespaceCode();
                    } else {
                        role = null;
                        roleMemberRoleId = "";
                    }
                }
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }
        } else {
            role = null;
        }
        return role;
    }

    public void setRole( RoleEbo role ) {
        this.role = role;
        if ( role != null ) {
            roleMemberRoleNamespaceCode = role.getNamespaceCode();
            roleMemberRoleName = role.getName();
            roleMemberRoleId = role.getId();
        } else {
            roleMemberRoleNamespaceCode = "";
            roleMemberRoleName = "";
            roleMemberRoleId = "";
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
        return NEW_DELEGATION_ID_KEY_VALUE.equals(getODelMId()) || (isEditDelegation() && StringUtils.isBlank(getDelegationMemberId()));
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
    
    public SubFundGroup getSubFundGroup() {
    	return subFundGroup;
    }
    public void setSubFundGroup(SubFundGroup subFundGroup) {
    	this.subFundGroup = subFundGroup;
    }
    public ObjectSubType getObjectSubType() {
    	return objectSubType;
    }
    public void setObjectSubType(ObjectSubType objectSubType) {
    	this.objectSubType = objectSubType;
    }
    public String getSubFundGroupCode() {
    	return subFundGroupCode;
    }
    public void setSubFundGroupCode(String subFundGroupCode) {
    	this.subFundGroupCode = subFundGroupCode;
    }
    public String getFinancialObjectSubTypeCode() {
    	return financialObjectSubTypeCode;
    }
    public void setFinancialObjectSubTypeCode(String financialObjectSubTypeCode) {
    	this.financialObjectSubTypeCode = financialObjectSubTypeCode;
    }
    public FundGroup getFundGroup() {
    	return fundGroup;
    }
    public void setFundGroup(FundGroup fundGroup) {
    	this.fundGroup = fundGroup;
    }
    public String getFundGroupCode() {
    	return fundGroupCode;
    }
    public void setFundGroupCode(String fundGroupCode) {
    	this.fundGroupCode = fundGroupCode;
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
        if ( roleRspActions == null ) {
            roleRspActions = new ArrayList<RoleResponsibilityAction>(1);
        }
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

    protected static OrgReviewRoleService getOrgReviewRoleService() {
        if ( orgReviewRoleService == null ) {
            orgReviewRoleService = SpringContext.getBean(OrgReviewRoleService.class);
        }
        return orgReviewRoleService;
    }

    protected static ChartService getChartService() {
        if ( chartService == null ) {
            chartService = SpringContext.getBean(ChartService.class);
        }
        return chartService;
    }

    protected static OrganizationService getOrganizationService() {
        if ( organizationService == null ) {
            organizationService = SpringContext.getBean(OrganizationService.class);
        }
        return organizationService;
    }
}
