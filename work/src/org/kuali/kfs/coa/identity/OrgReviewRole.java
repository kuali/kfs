/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.identity;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OrgReviewRole extends KimRoleImpl {

    //Dummy variable
    private String organizationTypeCode = "99";
	private static final long serialVersionUID = 1L;
	
    public static final String REVIEW_ROLES_INDICATOR_FIELD_NAME = "reviewRolesIndicator";
    public static final String ROLE_NAME_FIELD_NAMESPACE_CODE = "roleMemberRoleNamespaceCode";
    public static final String ROLE_NAME_FIELD_NAME = "roleMemberRoleName";
    public static final String GROUP_NAME_FIELD_NAMESPACE_CODE = "groupMemberGroupNamespaceCode";
    public static final String GROUP_NAME_FIELD_NAME = "groupMemberGroupName";
    public static final String PRINCIPAL_NAME_FIELD_NAME = "principalMemberPrincipalName";
    public static final String CHART_CODE_FIELD_NAME = "chart.chartOfAccountsCode";
    public static final String ORG_CODE_FIELD_NAME = "organization.organizationCode";
    public static final String DOC_TYPE_NAME_FIELD_NAME = "financialSystemDocumentTypeCode";
    public static final String DELEGATE_FIELD_NAME = "delegate";
    public static final String FROM_AMOUNT_FIELD_NAME = "fromAmount";
    public static final String TO_AMOUNT_FIELD_NAME = "toAmount";
    public static final String OVERRIDE_CODE_FIELD_NAME = "overrideCode";

    public static final String ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY = "ODelMId";
    public static final String ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY = "ORMId";

    
    String methodToCall;
    
    protected String orgReviewRoleMemberId;
    protected Chart chart = new Chart();
    protected Organization organization = new Organization();
    private boolean edit;
    private boolean copy;
    List<KimDelegationImpl> dlgns = new TypedArrayList(KimDelegationImpl.class);
    List<RoleMemberImpl> members = new TypedArrayList(RoleMemberImpl.class);
    
    protected RoleDocumentDelegation delegation = new RoleDocumentDelegation();
    protected RoleDocumentDelegationMember delegationMemberRole = new RoleDocumentDelegationMember();
    protected RoleDocumentDelegationMember delegationMemberGroup = new RoleDocumentDelegationMember();
    protected RoleDocumentDelegationMember delegationMemberPerson = new RoleDocumentDelegationMember();

	protected KimDocumentRoleMember memberRole = new KimDocumentRoleMember();
    protected KimDocumentRoleMember memberGroup = new KimDocumentRoleMember();
    protected KimDocumentRoleMember memberPerson = new KimDocumentRoleMember();

    protected Role role = new RoleImpl();
    protected Group group = new GroupImpl();
    protected Person person = new PersonImpl();
    
    protected List<KimAttributeDataImpl> attributes = new TypedArrayList(KimAttributeDataImpl.class);
    protected List<RoleResponsibilityActionImpl> roleRspActions = new TypedArrayList(RoleResponsibilityActionImpl.class);

    //Identifying information for the 3 kinds of role members this document caters to 
    protected String roleMemberRoleId;
    protected String roleMemberRoleNamespaceCode;
    protected String roleMemberRoleName;
    
    protected String groupMemberGroupId;
    protected String groupMemberGroupNamespaceCode;
    protected String groupMemberGroupName;
    
    protected String principalMemberPrincipalId;
    protected String principalMemberPrincipalName;

    //The role id this object corresponds to
    protected String roleId;
    
    //Identifying information for a single member (of any type)
    protected String memberId;
    protected String memberTypeCode;
    protected String memberName;
    protected String memberNamespaceCode;

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
    protected boolean ignorePrevious;
    protected String chartOfAccountsCode;
    protected String organizationCode;
	protected String fromAmount;
	protected String toAmount;
	protected String overrideCode;
	protected boolean active;
	protected boolean delegate;
    
    protected Timestamp activeFromDate;
    protected Timestamp activeToDate;

	/**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
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
    /**
     * Gets the delegation attribute. 
     * @return Returns the delegation.
     */
    public RoleDocumentDelegation getDelegation() {
        return delegation;
    }
    /**
     * Sets the delegation attribute value.
     * @param delegation The delegation to set.
     */
    public void setDelegation(RoleDocumentDelegation delegation) {
        this.delegation = delegation;
    }
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
    }
    /**
     * Gets the principalMemberPrincipalName attribute. 
     * @return Returns the principalMemberPrincipalName.
     */
    public String getPrincipalMemberPrincipalName() {
        return principalMemberPrincipalName;
    }
    /**
     * Sets the principalMemberPrincipalName attribute value.
     * @param principalMemberPrincipalName The principalMemberPrincipalName to set.
     */
    public void setPrincipalMemberPrincipalName(String principalMemberPrincipalName) {
        this.principalMemberPrincipalName = principalMemberPrincipalName;
        memberPerson.setMemberName(principalMemberPrincipalName);
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
     * Gets the fromAmount attribute. 
     * @return Returns the fromAmount.
     */
    public String getFromAmount() {
        return getAttributeValue(KfsKimAttributes.FROM_AMOUNT);
    }
    /**
     * Sets the fromAmount attribute value.
     * @param fromAmount The fromAmount to set.
     */
    public void setFromAmount(String fromAmount) {
        updateAttributeValue(KfsKimAttributes.FROM_AMOUNT, fromAmount);
        this.fromAmount = fromAmount;
    }

    private void updateAttributeValue(String attributeName, String attributeValue){
        if(StringUtils.isNotEmpty(attributeName)){
            KimAttributeDataImpl attributeData = getAttribute(attributeName);
            if(attributeData==null){
                attributeData = new KimAttributeDataImpl();
                KimAttributeImpl attribute = new KimAttributeImpl();
                attribute.setAttributeName(attributeName);
                attributeData.setKimAttribute(attribute);
            }
            attributeData.setAttributeValue(attributeValue);
            attributes.add(attributeData);
        }
    }
    /**
     * Gets the overrideCode attribute. 
     * @return Returns the overrideCode.
     */
    public String getOverrideCode() {
        return getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
    }
    /**
     * Sets the overrideCode attribute value.
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCode(String overrideCode) {
        updateAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, overrideCode);
        this.overrideCode = overrideCode;
    }
    /**
     * Gets the toAmount attribute. 
     * @return Returns the toAmount.
     */
    public String getToAmount() {
        return getAttributeValue(KfsKimAttributes.TO_AMOUNT);
    }
    /**
     * Sets the toAmount attribute value.
     * @param toAmount The toAmount to set.
     */
    public void setToAmount(String toAmount) {
        updateAttributeValue(KfsKimAttributes.TO_AMOUNT, toAmount);
        this.toAmount = toAmount;
    }
    /**
     * Gets the memberName attribute. 
     * @return Returns the memberName.
     */
    public String getMemberName() {
        return memberName;
    }
    /**
     * Sets the memberName attribute value.
     * @param memberName The memberName to set.
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    /**
     * Gets the activeFromDate attribute. 
     * @return Returns the activeFromDate.
     */
    public Timestamp getActiveFromDate() {
        return activeFromDate;
    }
    /**
     * Sets the activeFromDate attribute value.
     * @param activeFromDate The activeFromDate to set.
     */
    public void setActiveFromDate(Timestamp activeFromDate) {
        this.activeFromDate = activeFromDate;
    }
    /**
     * Gets the activeToDate attribute. 
     * @return Returns the activeToDate.
     */
    public Timestamp getActiveToDate() {
        return activeToDate;
    }
    /**
     * Sets the activeToDate attribute value.
     * @param activeToDate The activeToDate to set.
     */
    public void setActiveToDate(Timestamp activeToDate) {
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
	
    public void refresh(){
        
    }

    /**
     * Gets the financialSystemDocumentTypeCode attribute. 
     * @return Returns the financialSystemDocumentTypeCode.
     */
    public DocumentTypeEBO getFinancialSystemDocumentType() {
        return financialSystemDocumentType = SpringContext.getBean(KEWModuleService.class).retrieveExternalizableBusinessObjectIfNecessary(this, financialSystemDocumentType, "financialSystemDocumentType");
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
        this.financialSystemDocumentTypeCode = financialSystemDocumentTypeCode;
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
     * Gets the memberNamespaceCode attribute. 
     * @return Returns the memberNamespaceCode.
     */
    public String getMemberNamespaceCode() {
        return memberNamespaceCode;
    }
    /**
     * Sets the memberNamespaceCode attribute value.
     * @param memberNamespaceCode The memberNamespaceCode to set.
     */
    public void setMemberNamespaceCode(String memberNamespaceCode) {
        this.memberNamespaceCode = memberNamespaceCode;
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
     * Sets the memberId attribute value.
     * @param memberId The memberId to set.
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    /**
     * Gets the attributes attribute. 
     * @return Returns the attributes.
     */
    public List<KimAttributeDataImpl> getAttributes() {
        return attributes;
    }
    /**
     * Sets the attributes attribute value.
     * @param attributes The attributes to set.
     */
    public void setAttributes(List<KimAttributeDataImpl> attributes) {
        this.attributes = attributes;
    }
    
    private String getAttributeValue(String attributeName){
        String attributeValue = "";
        if(StringUtils.isEmpty(attributeName)) 
            attributeValue = "";
        for(KimAttributeDataImpl attribute: attributes){
            if(attribute.getKimAttribute()!=null && attribute.getKimAttribute().getAttributeName()!=null && 
                    attribute.getKimAttribute().getAttributeName().equals(attributeName))
                attributeValue = attribute.getAttributeValue();
        }
        return attributeValue;
    }

    private KimAttributeDataImpl getAttribute(String attributeName){
        KimAttributeDataImpl attributeData = null;
        if(StringUtils.isNotEmpty(attributeName)) 
            for(KimAttributeDataImpl attribute: attributes){
                if(attribute.getKimAttribute()!=null && attribute.getKimAttribute().getAttributeName()!=null && 
                        attribute.getKimAttribute().getAttributeName().equals(attributeName))
                    attributeData = attribute;
            }
        return attributeData;
    }

    /**
     * Gets the chartCode attribute. 
     * @return Returns the chartCode.
     */
    public String getChartOfAccountsCode() {
        return getAttributeValue(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
    }
    /**
     * Gets the organizationCode attribute. 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return getAttributeValue(KfsKimAttributes.ORGANIZATION_CODE);
    }
    /**
     * Sets the organizationCode attribute value.
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    /**
     * Gets the organizationCode attribute. 
     * @return Returns the organizationCode.
     */
    public String getDocumentTypeName() {
        return getAttributeValue(KfsKimAttributes.DOCUMENT_TYPE_NAME);
    }
    /**
     * Gets the roleRspActions attribute. 
     * @return Returns the roleRspActions.
     */
    public List<RoleResponsibilityActionImpl> getRoleRspActions() {
        return roleRspActions;
    }
    /**
     * Sets the roleRspActions attribute value.
     * @param roleRspActions The roleRspActions to set.
     */
    public void setRoleRspActions(List<RoleResponsibilityActionImpl> roleRspActions) {
        this.roleRspActions = roleRspActions;
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
        if(roleNamesToConsider==null){
            OrgReviewRoleLookupableHelperServiceImpl orrLHSI = new OrgReviewRoleLookupableHelperServiceImpl();
            roleNamesToConsider = orrLHSI.getRolesToConsider(getFinancialSystemDocumentTypeCode());
        }
        return roleNamesToConsider;
    }
    /**
     * Sets the roleNamesToConsider attribute value.
     * @param roleNamesToConsider The roleNamesToConsider to set.
     */
    public void setRoleNamesToConsider(List<String> roleNamesToConsider) {
        this.roleNamesToConsider = roleNamesToConsider;
    }
    /**
     * Gets the accountingOrgReviewRoleIndicator attribute. 
     * @return Returns the accountingOrgReviewRoleIndicator.
     */
    public boolean isAccountingOrgReviewRoleIndicator() {
        return getRoleNamesToConsider()!=null && 
            getRoleNamesToConsider().contains(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
    }
    /**
     * Gets the bothReviewRolesIndicator attribute. 
     * @return Returns the bothReviewRolesIndicator.
     */
    public boolean isBothReviewRolesIndicator() {
        return getRoleNamesToConsider()!=null && 
            getRoleNamesToConsider().contains(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME) &&
            getRoleNamesToConsider().contains(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
    }
    /**
     * Gets the orgReviewRoleIndicator attribute. 
     * @return Returns the orgReviewRoleIndicator.
     */
    public boolean isOrgReviewRoleIndicator() {
        return getRoleNamesToConsider()!=null && 
            getRoleNamesToConsider().contains(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
    }
    /**
     * Gets the actionTypeCode attribute. 
     * @return Returns the actionTypeCode.
     */
    public String getActionTypeCode() {
        /*if(StringUtils.isNotEmpty(actionTypeCode) && (roleRspActions==null || roleRspActions.size()<1 || StringUtils.isEmpty(roleRspActions.get(0).getActionTypeCode()))){
            setActionTypeCode(actionTypeCode);
        }
        if(roleRspActions==null || roleRspActions.size()<1)
            actionTypeCode = "";
        else
            actionTypeCode = roleRspActions.get(0).getActionTypeCode();
         */
        return actionTypeCode;
    }

    public String getActionTypeCodeToDisplay() {
        if(roleRspActions==null || roleRspActions.size()<1)
            return "";
        else
            return roleRspActions.get(0).getActionTypeCode();
    }

    /**
     * 
     * This method fore readonlyalterdisplay
     * 
     * @return
     */
    public String getActionTypeCodeDescription() {
        String actionTypeCodeDesc = (String)CodeTranslator.approvePolicyLabels.get(getActionTypeCodeToDisplay());
        return actionTypeCodeDesc==null?"":actionTypeCodeDesc;
    }

    /**
     * Sets the actionTypeCode attribute value.
     * @param actionTypeCode The actionTypeCode to set.
     */
    public void setActionTypeCode(String actionTypeCode) {
        /*if(roleRspActions==null || roleRspActions.size()<1){
            RoleResponsibilityActionImpl roleRespAction = new RoleResponsibilityActionImpl();
            roleRespAction.setActionTypeCode(actionTypeCode);
            roleRspActions.add(roleRespAction);
        } else{
            roleRspActions.get(0).setActionTypeCode(actionTypeCode);
        }*/
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
        if(roleRspActions==null || roleRspActions.size()<1)
            return "";
        else
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
    public boolean isIgnorePrevious() {
        return ignorePrevious;
    }
    /**
     * Sets the ignorePrevious attribute value.
     * @param ignorePrevious The ignorePrevious to set.
     */
    public void setIgnorePrevious(boolean ignorePrevious) {
        this.ignorePrevious = ignorePrevious;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);

        return m;
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
        this.roleId = roleId;
    }
    /**
     * Gets the reviewRolesIndicator attribute. 
     * @return Returns the reviewRolesIndicator.
     */
    public String getReviewRolesIndicator() {
        if(isBothReviewRolesIndicator())
            reviewRolesIndicator = KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE;
        else if(isAccountingOrgReviewRoleIndicator())
            reviewRolesIndicator = KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE;
        else if(isOrgReviewRoleIndicator())
            reviewRolesIndicator = KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE;
        return reviewRolesIndicator;
    }
    /**
     * Sets the reviewRolesIndicator attribute value.
     * @param reviewRolesIndicator The reviewRolesIndicator to set.
     */
    public void setReviewRolesIndicator(String reviewRolesIndicator) {
        this.reviewRolesIndicator = reviewRolesIndicator;
    }


    public boolean hasRole(){
        return memberRole!=null && StringUtils.isNotEmpty(memberRole.getMemberNamespaceCode()) && StringUtils.isNotEmpty(memberRole.getMemberName());
    }

    public boolean hasGroup(){
        return memberGroup!=null && StringUtils.isNotEmpty(memberGroup.getMemberNamespaceCode()) && StringUtils.isNotEmpty(memberGroup.getMemberName());
    }
    
    public boolean hasPrincipal(){
        return memberPerson!=null && StringUtils.isNotEmpty(memberPerson.getMemberName());
    }
    
    public boolean hasAnyMember(){
        return hasRole() || hasGroup() || hasPrincipal();
    }
    
    public KimDocumentRoleMember getMemberOfType(String memberTypeCode){
        if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
            return memberRole;
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
            return memberGroup;
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
            return memberPerson;
        return null;
    }
    
    public String getMemberId(String memberTypeCode){
        KimDocumentRoleMember member = getMemberOfType(memberTypeCode);
        return member!=null?member.getMemberId():null;
    }

    public String getMemberFieldName(KimDocumentRoleMember member){
        String memberFieldName = "";
        if(member!=null){
            if(member.isRole())
                memberFieldName = "role.roleName";
            else if(member.isGroup())
                memberFieldName = "group.groupName";
            else if(member.isPrincipal()){
                memberFieldName = "principal.principalName";
            }
        }
        return memberFieldName;
    }
    /**
     * Gets the memberId attribute. 
     * @return Returns the memberId.
     */
    public String getMemberId() {
        return memberId;
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
    public Group getGroup() {
        return group;
    }
    /**
     * Sets the group attribute value.
     * @param group The group to set.
     */
    public void setGroup(Group group) {
        this.group = group;
    }
    /**
     * Gets the person attribute. 
     * @return Returns the person.
     */
    public Person getPerson() {
        return person;
    }
    /**
     * Sets the person attribute value.
     * @param person The person to set.
     */
    public void setPerson(Person person) {
        this.person = person;
    }
    /**
     * Gets the role attribute. 
     * @return Returns the role.
     */
    public Role getRole() {
        return role;
    }
    /**
     * Sets the role attribute value.
     * @param role The role to set.
     */
    public void setRole(Role role) {
        this.role = role;
    }
    /**
     * Gets the delegationMemberGroup attribute. 
     * @return Returns the delegationMemberGroup.
     */
    public RoleDocumentDelegationMember getDelegationMemberGroup() {
        return delegationMemberGroup;
    }
    /**
     * Sets the delegationMemberGroup attribute value.
     * @param delegationMemberGroup The delegationMemberGroup to set.
     */
    public void setDelegationMemberGroup(RoleDocumentDelegationMember delegationMemberGroup) {
        this.delegationMemberGroup = delegationMemberGroup;
        if(delegationMemberGroup!=null){
            GroupImpl groupImpl = new GroupImpl();
            groupImpl.setGroupId(delegationMemberGroup.getMemberId());
            groupImpl.setNamespaceCode(delegationMemberGroup.getMemberNamespaceCode());
            groupImpl.setGroupName(delegationMemberGroup.getMemberName());
            setGroup(groupImpl);
        }
    }
    /**
     * Gets the delegationMemberPerson attribute. 
     * @return Returns the delegationMemberPerson.
     */
    public RoleDocumentDelegationMember getDelegationMemberPerson() {
        return delegationMemberPerson;
    }
    /**
     * Sets the delegationMemberPerson attribute value.
     * @param delegationMemberPerson The delegationMemberPerson to set.
     */
    public void setDelegationMemberPerson(RoleDocumentDelegationMember delegationMemberPerson) {
        this.delegationMemberPerson = delegationMemberPerson;
        if(delegationMemberPerson!=null){
            PersonImpl personImpl = new PersonImpl(delegationMemberPerson.getMemberId(), "");
            personImpl.setPrincipalName(delegationMemberPerson.getMemberName());
            setPerson(personImpl);
        }
    }
    /**
     * Gets the delegationMemberRole attribute. 
     * @return Returns the delegationMemberRole.
     */
    public RoleDocumentDelegationMember getDelegationMemberRole() {
        return delegationMemberRole;
    }
    /**
     * Sets the delegationMemberRole attribute value.
     * @param delegationMemberRole The delegationMemberRole to set.
     */
    public void setDelegationMemberRole(RoleDocumentDelegationMember delegationMemberRole) {
        this.delegationMemberRole = delegationMemberRole;
        if(delegationMemberRole!=null){
            RoleImpl roleImpl = new RoleImpl();
            roleImpl.setRoleId(delegationMemberRole.getMemberId());
            roleImpl.setNamespaceCode(delegationMemberRole.getMemberNamespaceCode());
            roleImpl.setRoleName(delegationMemberRole.getMemberName());
            setRole(roleImpl);
        }
    }
    /**
     * Gets the memberGroup attribute. 
     * @return Returns the memberGroup.
     */
    public KimDocumentRoleMember getMemberGroup() {
        return memberGroup;
    }
    /**
     * Sets the memberGroup attribute value.
     * @param memberGroup The memberGroup to set.
     */
    protected void setMemberGroup(KimDocumentRoleMember memberGroup) {
        this.memberGroup = memberGroup;
        if(memberGroup!=null){
            GroupImpl groupImpl = new GroupImpl();
            groupImpl.setGroupId(memberGroup.getMemberId());
            groupImpl.setNamespaceCode(memberGroup.getMemberNamespaceCode());
            groupImpl.setGroupName(memberGroup.getMemberName());
            setGroup(groupImpl);
        }
    }
    /**
     * Gets the memberPerson attribute. 
     * @return Returns the memberPerson.
     */
    public KimDocumentRoleMember getMemberPerson() {
        return memberPerson;
    }
    /**
     * Sets the memberPerson attribute value.
     * @param memberPerson The memberPerson to set.
     */
    protected void setMemberPerson(KimDocumentRoleMember memberPerson) {
        this.memberPerson = memberPerson;
        if(memberPerson!=null){
            PersonImpl personImpl = new PersonImpl(memberPerson.getMemberId(), "");
            personImpl.setPrincipalName(memberPerson.getMemberName());
            setPerson(personImpl);
        }
    }
    /**
     * Gets the memberRole attribute. 
     * @return Returns the memberRole.
     */
    public KimDocumentRoleMember getMemberRole() {
        return memberRole;
    }
    /**
     * Sets the memberRole attribute value.
     * @param memberRole The memberRole to set.
     */
    protected void setMemberRole(KimDocumentRoleMember memberRole) {
        this.memberRole = memberRole;
        if(memberRole!=null){
            RoleImpl roleImpl = new RoleImpl();
            roleImpl.setRoleId(memberRole.getMemberId());
            roleImpl.setNamespaceCode(memberRole.getMemberNamespaceCode());
            roleImpl.setRoleName(memberRole.getMemberName());
            setRole(roleImpl);
        }
    }

    public void setRoleDocumentDelegationMember(RoleDocumentDelegationMember delegationMember, String memberTypeCode){
        if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
            setDelegationMemberRole(delegationMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
            setDelegationMemberGroup(delegationMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
            setDelegationMemberPerson(delegationMember);
        setActiveFromDate(delegationMember.getActiveFromDate());
        setActiveToDate(delegationMember.getActiveToDate());
    }
    
    public void setKimDocumentRoleMember(KimDocumentRoleMember roleMember, String memberTypeCode){
        if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
            setMemberRole(roleMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
            setMemberGroup(roleMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
            setMemberPerson(roleMember);
        setActiveFromDate(roleMember.getActiveFromDate());
        setActiveToDate(roleMember.getActiveToDate());
    }

    /**
     * Gets the dlgns attribute. 
     * @return Returns the dlgns.
     */
    public List<KimDelegationImpl> getDlgns() {
        return dlgns;
    }
    /**
     * Sets the dlgns attribute value.
     * @param dlgns The dlgns to set.
     */
    public void setDlgns(List<KimDelegationImpl> dlgns) {
        this.dlgns = dlgns;
    }
    /**
     * Gets the members attribute. 
     * @return Returns the members.
     */
    public List<RoleMemberImpl> getMembers() {
        return members;
    }
    /**
     * Sets the members attribute value.
     * @param members The members to set.
     */
    public void setMembers(List<RoleMemberImpl> members) {
        this.members = members;
    }

    public RoleMemberImpl getOriginalRoleMemberOfType(String memberTypeCode){
        for(RoleMemberImpl roleMember: getMembers()){
            if(roleMember.getMemberTypeCode().equals(memberTypeCode))
                return roleMember;
        }
        return null;
    }
    
    public KimDelegationMemberImpl getOriginalRoleMemberOfType(String delegationTypeCode, String memberTypeCode){
        if(getDlgns()!=null && getDlgns().size()>0){
            for(KimDelegationImpl delegation: getDlgns()){
                if(delegation.getDelegationTypeCode().equals(delegationTypeCode) && delegation.getMembers()!=null){
                    for(KimDelegationMemberImpl delegationMember: getDlgns().get(0).getMembers()){
                        if(delegationMember.getMemberTypeCode().equals(memberTypeCode))
                            return delegationMember;
                    }
                }
            }
        }
        return null;
    }
    
    public static void main(String[] args){
        System.out.println("is writeable: "+PropertyUtils.isWriteable(new OrgReviewRole(), "members.memberId"));
        System.out.println("is writeable: "+PropertyUtils.isWriteable(new OrgReviewRole(), "oRMId"));
        System.out.println("is writeable ORMId: "+PropertyUtils.isWriteable(new OrgReviewRole(), "ORMId"));
        System.out.println("is writeable: "+PropertyUtils.isWriteable(new OrgReviewRole(), "methodToCall"));
        System.out.println("is writeable: "+PropertyUtils.isWriteable(new OrgReviewRole(), "orgReviewRoleMemberId"));
    }
    /**
     * Gets the copy attribute. 
     * @return Returns the copy.
     */
    public boolean isCopy() {
        return copy;
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
        return edit;
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
    /**
     * Gets the organizationTypeCode attribute. 
     * @return Returns the organizationTypeCode.
     */
    public String getOrganizationTypeCode() {
        return organizationTypeCode;
    }
    /**
     * Sets the organizationTypeCode attribute value.
     * @param organizationTypeCode The organizationTypeCode to set.
     */
    public void setOrganizationTypeCode(String organizationTypeCode) {
    }
}