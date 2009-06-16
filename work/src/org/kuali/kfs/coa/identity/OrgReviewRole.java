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

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateTypeInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.web.comparator.StringValueComparator;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OrgReviewRole extends PersistableBusinessObjectBase implements Inactivateable {

    //Dummy variable
    private String organizationTypeCode = "99";
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

    public static final String ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY = "ODelMId";
    public static final String ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY = "ORMId";
    
    public static final String NEW_DELEGATION_ID_KEY_VALUE = "New";

    String methodToCall;
    protected String kimTypeId;
    
    protected String orgReviewRoleMemberId;
    protected Chart chart = new Chart();
    protected Organization organization = new Organization();
    private boolean edit;
    private boolean copy;
    
    protected DelegateTypeInfo delegation = new DelegateTypeInfo();
    protected DelegateMemberCompleteInfo delegationMemberRole = new DelegateMemberCompleteInfo();
    protected DelegateMemberCompleteInfo delegationMemberGroup = new DelegateMemberCompleteInfo();
    protected DelegateMemberCompleteInfo delegationMemberPerson = new DelegateMemberCompleteInfo();

    protected RoleMemberCompleteInfo memberRole = new RoleMemberCompleteInfo();
    protected RoleMemberCompleteInfo memberGroup = new RoleMemberCompleteInfo();
    protected RoleMemberCompleteInfo memberPerson = new RoleMemberCompleteInfo();

    protected Role role;
    protected Group group;
    protected Person person;
    
    protected List<KfsKimDocumentAttributeData> attributes = new TypedArrayList(KfsKimDocumentAttributeData.class);
    protected List<RoleResponsibilityActionInfo> roleRspActions = new TypedArrayList(RoleResponsibilityActionInfo.class);

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
    protected String namespaceCode;
    protected String roleName;
    
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
    private String reviewRolesIndicator;
    
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
    public DelegateTypeInfo getDelegation() {
        return delegation;
    }
    /**
     * Sets the delegation attribute value.
     * @param delegation The delegation to set.
     */
    public void setDelegation(DelegateTypeInfo delegation) {
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
        return principalMemberPrincipalName;
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
    private void updateAttributeValue(String attributeName, String attributeValue){
        if(StringUtils.isNotEmpty(attributeName)){
            KfsKimDocumentAttributeData attributeData = getAttribute(attributeName);
            if(attributeData==null){
                attributeData = new KfsKimDocumentAttributeData();
                KimTypeAttributeInfo attribute = new KimTypeAttributeInfo();
                attribute.setAttributeName(attributeName);
                attributeData.setKimAttribute(attribute);
                attributeData.setAttrVal(attributeValue);
                attributes.add(attributeData);
            } else
                attributeData.setAttrVal(attributeValue);
        }
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
        if(StringUtils.isNotEmpty(fromAmount))
            this.fromAmount = new KualiDecimal(fromAmount);
        else
            this.fromAmount = null;
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
        if(StringUtils.isNotEmpty(toAmount))
            this.toAmount = new KualiDecimal(toAmount);
        else
            this.toAmount = null;
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
    public Date getActiveFromDate() {
        return activeFromDate;
    }
    /**
     * Sets the activeFromDate attribute value.
     * @param activeFromDate The activeFromDate to set.
     */
    public void setActiveFromDate(Date activeFromDate) {
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
    public void setActiveToDate(Date activeToDate) {
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
        String oldFinancialSystemDocumentTypeCode = this.financialSystemDocumentTypeCode;
        boolean isChanged = false;
        if(StringValueComparator.getInstance().compare(this.financialSystemDocumentTypeCode, financialSystemDocumentTypeCode)!=0){
            isChanged = true;
        }
        this.financialSystemDocumentTypeCode = financialSystemDocumentTypeCode;
        setRoleNamesAndReviewIndicator(isChanged);
    }
    
    private void setRoleNamesAndReviewIndicator(boolean hasFinancialSystemDocumentTypeCodeChanged){
        if(hasFinancialSystemDocumentTypeCodeChanged){
            //If role id is populated role names to consider have already been narrowed down 
            if(StringUtils.isNotEmpty(getRoleId()) && StringUtils.isNotEmpty(getRoleName())){
                List<String> narrowedDownRoleNames = new ArrayList<String>();
                narrowedDownRoleNames.add(getRoleName());
                setRoleNamesToConsider(roleNamesToConsider);
            } else{
                setRoleNamesToConsider();
            }
            if(isBothReviewRolesIndicator())
                setReviewRolesIndicatorOnDocTypeChange(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_BOTH_CODE);
            else if(isAccountingOrgReviewRoleIndicator())
                setReviewRolesIndicatorOnDocTypeChange(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE);
            else if(isOrgReviewRoleIndicator())
                setReviewRolesIndicatorOnDocTypeChange(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE);

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
    public List<KfsKimDocumentAttributeData> getAttributes() {
        return attributes;
    }
    /**
     * Sets the attributes attribute value.
     * @param attributes The attributes to set.
     */
    public void setAttributes(List<KfsKimDocumentAttributeData> attributes) {
        this.attributes = attributes;
    }
    
    public String getAttributeValue(String attributeName){
        String attributeValue = "";
        if(StringUtils.isEmpty(attributeName)) 
            attributeValue = "";
        KfsKimDocumentAttributeData attributeData = getAttribute(attributeName);
        return attributeData==null?"":attributeData.getAttrVal();
    }

    private KfsKimDocumentAttributeData getAttribute(String attributeName){
        KfsKimDocumentAttributeData attributeData = null;
        if(StringUtils.isNotEmpty(attributeName)) 
            for(KfsKimDocumentAttributeData attribute: attributes){
                if(attribute.getKimAttribute()!=null && attribute.getKimAttribute().getAttributeName()!=null && 
                        attribute.getKimAttribute().getAttributeName().equals(attributeName)){
                    attributeData = attribute;
                    break;
                }
            }
        return attributeData;
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
        if(roleNamesToConsider==null && getFinancialSystemDocumentTypeCode()!=null)
            setRoleNamesToConsider();
        return roleNamesToConsider;
    }
    public void setRoleNamesToConsider(List<String> narrowedDownRoleNames) {
        roleNamesToConsider = new ArrayList<String>();
        roleNamesToConsider.addAll(narrowedDownRoleNames);
    }
    /**
     * Sets the roleNamesToConsider attribute value.
     * @param roleNamesToConsider The roleNamesToConsider to set.
     */
    public void setRoleNamesToConsider() {
        OrgReviewRoleLookupableHelperServiceImpl orrLHSI = new OrgReviewRoleLookupableHelperServiceImpl();
        roleNamesToConsider = orrLHSI.getRolesToConsider(getFinancialSystemDocumentTypeCode());
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
            KfsKimDocumentRoleResponsibilityAction roleRespAction = new KfsKimDocumentRoleResponsibilityAction();
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
        return StringUtils.isNotEmpty(roleMemberRoleNamespaceCode) && StringUtils.isNotEmpty(roleMemberRoleName);
    }

    public boolean hasGroup(){
        return StringUtils.isNotEmpty(groupMemberGroupNamespaceCode) && StringUtils.isNotEmpty(groupMemberGroupName);
    }
    
    public boolean hasPrincipal(){
        return StringUtils.isNotEmpty(principalMemberPrincipalName);
    }
    
    public boolean hasAnyMember(){
        return hasRole() || hasGroup() || hasPrincipal();
    }
    
    public DelegateMemberCompleteInfo getDelegationMemberOfType(String memberTypeCode){
        if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            delegationMemberRole.setMemberId(roleMemberRoleId);
            delegationMemberRole.setMemberName(roleMemberRoleName);
            delegationMemberRole.setMemberNamespaceCode(roleMemberRoleNamespaceCode);
            delegationMemberRole.setRoleMemberId(roleMemberId);
            return delegationMemberRole;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            delegationMemberGroup.setMemberId(groupMemberGroupId);
            delegationMemberGroup.setMemberName(groupMemberGroupName);
            delegationMemberGroup.setMemberNamespaceCode(groupMemberGroupNamespaceCode);
            delegationMemberRole.setRoleMemberId(roleMemberId);
            return delegationMemberGroup;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            delegationMemberPerson.setMemberId(principalMemberPrincipalId);
            delegationMemberPerson.setMemberName(principalMemberPrincipalName);
            delegationMemberRole.setRoleMemberId(roleMemberId);
            return delegationMemberPerson;
        }
        return null;
    }
    
    public RoleMemberCompleteInfo getRoleMemberOfType(String memberTypeCode){
        if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            memberRole.setMemberId(roleMemberRoleId);
            memberRole.setMemberName(roleMemberRoleName);
            memberRole.setMemberNamespaceCode(roleMemberRoleNamespaceCode);
            return memberRole;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            memberGroup.setMemberId(groupMemberGroupId);
            memberGroup.setMemberName(groupMemberGroupName);
            memberGroup.setMemberNamespaceCode(groupMemberGroupNamespaceCode);
            return memberGroup;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            memberPerson.setMemberId(principalMemberPrincipalId);
            memberPerson.setMemberName(principalMemberPrincipalName);
            return memberPerson;
        }
        return null;
    }
    
    public String getMemberIdForDelegationMember(String memberTypeCode){
        DelegateMemberCompleteInfo member = getDelegationMemberOfType(memberTypeCode);
        return member!=null?member.getMemberId():null;
    }
    
    public String getMemberIdForRoleMember(String memberTypeCode){
        RoleMemberCompleteInfo member = getRoleMemberOfType(memberTypeCode);
        return member!=null?member.getMemberId():null;
    }

    public String getDelegationMemberFieldName(DelegateMemberCompleteInfo member){
        String memberFieldName = "";
        if(member!=null){
            if(isRole(member.getMemberTypeCode()))
                memberFieldName = OrgReviewRole.ROLE_NAME_FIELD_NAME;
            else if(isGroup(member.getMemberTypeCode()))
                memberFieldName = OrgReviewRole.GROUP_NAME_FIELD_NAME;
            else if(isPrincipal(member.getMemberTypeCode())){
                memberFieldName = OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME;
            }
        }
        return memberFieldName;
    }
    
    public boolean isRole(String memberTypeCode){
        return memberTypeCode!=null && memberTypeCode.equals(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
    }
    
    public boolean isGroup(String memberTypeCode){
        return memberTypeCode!=null && memberTypeCode.equals(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
    }

    public boolean isPrincipal(String memberTypeCode){
        return memberTypeCode!=null && memberTypeCode.equals(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
    }

    public String getMemberFieldName(RoleMemberCompleteInfo member){
        String memberFieldName = "";
        if(member!=null){
            if(isRole(member.getMemberTypeCode()))
                memberFieldName = OrgReviewRole.ROLE_NAME_FIELD_NAME;
            else if(isGroup(member.getMemberTypeCode()))
                memberFieldName = OrgReviewRole.GROUP_NAME_FIELD_NAME;
            else if(isPrincipal(member.getMemberTypeCode())){
                memberFieldName = OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME;
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
        if(StringUtils.isNotEmpty(principalMemberPrincipalId) && 
          (person==null || StringUtils.isEmpty(person.getPrincipalId()) || StringUtils.isEmpty(person.getPrincipalName()))){
            person = getPersonFromService(principalMemberPrincipalId);
        }
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
        return this.role;
    }

    public Role getRole(String roleId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
        return (Role) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Role.class).getExternalizableBusinessObject(Role.class, fieldValues);
    }

    public Role getRole(Map<String, Object> fieldValues) {
        return (Role) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Role.class).getExternalizableBusinessObject(Role.class, fieldValues);
    }

    public Group getGroup(String groupId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KimConstants.PrimaryKeyConstants.GROUP_ID, groupId);
        return (Group) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Group.class).getExternalizableBusinessObject(Group.class, fieldValues);
    }

    public Group getGroup(Map<String, Object> fieldValues) {
        return (Group) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(Group.class).getExternalizableBusinessObject(Group.class, fieldValues);
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
    public DelegateMemberCompleteInfo getDelegationMemberGroup() {
        return delegationMemberGroup;
    }
    /**
     * Sets the delegationMemberGroup attribute value.
     * @param delegationMemberGroup The delegationMemberGroup to set.
     */
    public void setDelegationMemberGroup(DelegateMemberCompleteInfo delegationMemberGroup) {
        this.delegationMemberGroup = delegationMemberGroup;
        if(delegationMemberGroup!=null){
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KimConstants.PrimaryKeyConstants.GROUP_ID, memberGroup.getMemberId());
            Group groupInfo = getGroup(fieldValues);
            setGroup(groupInfo);
        }
    }
    /**
     * Gets the delegationMemberPerson attribute. 
     * @return Returns the delegationMemberPerson.
     */
    public DelegateMemberCompleteInfo getDelegationMemberPerson() {
        return delegationMemberPerson;
    }
    /**
     * Sets the delegationMemberPerson attribute value.
     * @param delegationMemberPerson The delegationMemberPerson to set.
     */
    public void setDelegationMemberPerson(DelegateMemberCompleteInfo delegationMemberPerson) {
        this.delegationMemberPerson = delegationMemberPerson;
        if(delegationMemberPerson!=null){
            Person person = getPersonFromService(delegationMemberPerson.getMemberId());
            //person.setPrincipalId(delegationMemberPerson.getMemberId());
            //person.setPrincipalName(delegationMemberPerson.getMemberName());
            setPerson(person);
        }
    }
    /**
     * Gets the delegationMemberRole attribute. 
     * @return Returns the delegationMemberRole.
     */
    public DelegateMemberCompleteInfo getDelegationMemberRole() {
        return delegationMemberRole;
    }
    /**
     * Sets the delegationMemberRole attribute value.
     * @param delegationMemberRole The delegationMemberRole to set.
     */
    public void setDelegationMemberRole(DelegateMemberCompleteInfo delegationMemberRole) {
        this.delegationMemberRole = delegationMemberRole;
        if(delegationMemberRole!=null){
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KimConstants.PrimaryKeyConstants.ROLE_ID, delegationMemberRole.getMemberId());
            Role roleInfo = getRole(fieldValues);
            setRole(roleInfo);
        }
    }
    /**
     * Gets the memberGroup attribute. 
     * @return Returns the memberGroup.
     */
    public RoleMemberCompleteInfo getMemberGroup() {
        return memberGroup;
    }
    /**
     * Sets the memberGroup attribute value.
     * @param memberGroup The memberGroup to set.
     */
    protected void setMemberGroup(RoleMemberCompleteInfo memberGroup) {
        this.memberGroup = memberGroup;
        if(memberGroup!=null){
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KimConstants.PrimaryKeyConstants.GROUP_ID, memberGroup.getMemberId());
            Group groupInfo = getGroup(fieldValues);
            setGroup(groupInfo);
        }
    }
    /**
     * Gets the memberPerson attribute. 
     * @return Returns the memberPerson.
     */
    public RoleMemberCompleteInfo getMemberPerson() {
        return memberPerson;
    }
    /**
     * Sets the memberPerson attribute value.
     * @param memberPerson The memberPerson to set.
     */
    protected void setMemberPerson(RoleMemberCompleteInfo memberPerson) {
        this.memberPerson = memberPerson;
        if(memberPerson!=null){
            Person personImpl = getPersonFromService(memberPerson.getMemberId());
            //personImpl.setPrincipalId(memberPerson.getMemberId());
            //personImpl.setPrincipalName(memberPerson.getMemberName());
            setPerson(personImpl);
        }
    }
    /**
     * Gets the memberRole attribute. 
     * @return Returns the memberRole.
     */
    public RoleMemberCompleteInfo getMemberRole() {
        return memberRole;
    }
    /**
     * Sets the memberRole attribute value.
     * @param memberRole The memberRole to set.
     */
    protected void setMemberRole(RoleMemberCompleteInfo memberRole) {
        this.memberRole = memberRole;
        if(memberRole!=null){
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, memberRole.getMemberId());
            Role roleInfo = getRole(criteria);
            setRole(roleInfo);
        }
    }

    public void setRoleDocumentDelegationMember(DelegateMemberCompleteInfo delegationMember){
        if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(delegationMember.getMemberTypeCode()))
            setDelegationMemberRole(delegationMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(delegationMember.getMemberTypeCode()))
            setDelegationMemberGroup(delegationMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(delegationMember.getMemberTypeCode()))
            setDelegationMemberPerson(delegationMember);
        setActiveFromDate(delegationMember.getActiveFromDate());
        setActiveToDate(delegationMember.getActiveToDate());
    }
    
    public void setKimDocumentRoleMember(RoleMemberCompleteInfo roleMember){
        if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(roleMember.getMemberTypeCode()))
            setMemberRole(roleMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(roleMember.getMemberTypeCode()))
            setMemberGroup(roleMember);
        else if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(roleMember.getMemberTypeCode()))
            setMemberPerson(roleMember);
        setActiveFromDate(roleMember.getActiveFromDate());
        setActiveToDate(roleMember.getActiveToDate());
    }
    
    /**
     * Gets the copy attribute. 
     * @return Returns the copy.
     */
    public boolean isCopy() {
        return copy || "Copy".equalsIgnoreCase(methodToCall);
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
        return edit || "Edit".equalsIgnoreCase(methodToCall);
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

    /**
     * Gets the organizationTypeCode attribute. 
     * @return Returns the organizationTypeCode.
     */
    public String getOrganizationTypeCode() {
        return "99";
    }
    /**
     * Sets the organizationTypeCode attribute value.
     * @param organizationTypeCode The organizationTypeCode to set.
     */
    public void setOrganizationTypeCode(String organizationTypeCode) {
    }
    /**
     * Gets the roleName attribute. 
     * @return Returns the roleName.
     */
    public String getRoleName() {
        return roleName;
    }
    /**
     * Sets the roleName attribute value.
     * @param roleName The roleName to set.
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
        List<String> narrowedDownRoleNames = new ArrayList<String>();
        narrowedDownRoleNames.add(getRoleName());
        setRoleNamesToConsider(narrowedDownRoleNames);
    }
    /**
     * Gets the roleNamespaceCode attribute. 
     * @return Returns the roleNamespaceCode.
     */
    public String getNamespaceCode() {
        return namespaceCode;
    }
    /**
     * Sets the roleNamespaceCode attribute value.
     * @param roleNamespaceCode The roleNamespaceCode to set.
     */
    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    @Override
    public Long getVersionNumber(){
        return 1L;
    }

    private Person getPersonFromService(String principalId){
        return KIMServiceLocator.getPersonService().getPerson(principalId);
    }
    /**
     * Gets the kimTypeId attribute. 
     * @return Returns the kimTypeId.
     */
    public String getKimTypeId() {
        return kimTypeId;
    }
    /**
     * Sets the kimTypeId attribute value.
     * @param kimTypeId The kimTypeId to set.
     */
    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }
    
    public AttributeSet getQualifierAsAttributeSet(List<KfsKimDocumentAttributeData> qualifiers) {
        AttributeSet m = new AttributeSet();
        for(KfsKimDocumentAttributeData data: qualifiers){
            m.put(data.getKimAttribute().getAttributeName(), data.getAttrVal());
        }
        return m;
    }

    public List<KfsKimDocumentAttributeData> getAttributeSetAsQualifierList(
            KimTypeInfo typeInfo, AttributeSet qualifiers) {
        List<KfsKimDocumentAttributeData> attributesList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attribData;
        KimTypeAttributeInfo attribInfo;
        for(String key: qualifiers.keySet()){
            attribInfo = typeInfo.getAttributeDefinitionByName(key);
            attribData = new KfsKimDocumentAttributeData();
            attribData.setKimAttribute(attribInfo);
            attribData.setKimTypId(typeInfo.getKimTypeId());
            attribData.setKimAttrDefnId(attribInfo.getKimAttributeId());
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
    public List<RoleResponsibilityActionInfo> getRoleRspActions() {
        return roleRspActions;
    }
    /**
     * Sets the roleRspActions attribute value.
     * @param roleRspActions The roleRspActions to set.
     */
    public void setRoleRspActions(List<RoleResponsibilityActionInfo> roleRspActions) {
        this.roleRspActions = roleRspActions;
    }

}