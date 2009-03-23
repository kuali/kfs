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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.service.impl.KEWModuleService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;
import org.kuali.rice.kns.bo.BusinessObject;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OrgReviewRole implements BusinessObject {

	private static final long serialVersionUID = 1L;
	
    protected String orgReviewRoleMemberId;
    protected Chart chart;
    protected Organization organization;
	protected KimRoleImpl role;
	protected Person principal;
	protected KimGroupImpl group;
	protected KimDelegationImpl delegation;
	protected RoleMemberImpl member;

    protected List<KimAttributeDataImpl> attributes;
    protected List<RoleResponsibilityActionImpl> roleRspActions;
    
    protected String memberId;
    protected String memberTypeCode;
    protected String memberName;
    protected String memberNamespaceCode;

    protected String delegationTypeCode;

    private String financialSystemDocumentTypeCode;
    private DocumentTypeEBO financialSystemDocumentType;
	protected String actionTypeCode;
	protected String priorityNumber;
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
     * Gets the actionTypeCode attribute. 
     * @return Returns the actionTypeCode.
     */
    public String getActionTypeCode() {
        if(roleRspActions==null || roleRspActions.size()<1)
            actionTypeCode = "";
        else
            actionTypeCode = roleRspActions.get(0).getActionTypeCode();

        return actionTypeCode;
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
        if(roleRspActions==null || roleRspActions.size()<1)
            priorityNumber = "";
        else
            priorityNumber = roleRspActions.get(0).getPriorityNumber()+"";

        return priorityNumber;
    }
    /**
     * Sets the priorityNumber attribute value.
     * @param priorityNumber The priorityNumber to set.
     */
    public void setPriorityNumber(String priorityNumber) {
        this.priorityNumber = priorityNumber;
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
    public KimDelegationImpl getDelegation() {
        return delegation;
    }
    /**
     * Sets the delegation attribute value.
     * @param delegation The delegation to set.
     */
    public void setDelegation(KimDelegationImpl delegation) {
        this.delegation = delegation;
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
        this.fromAmount = fromAmount;
    }
    /**
     * Gets the group attribute. 
     * @return Returns the group.
     */
    public KimGroupImpl getGroup() {
        return group;
    }
    /**
     * Sets the group attribute value.
     * @param group The group to set.
     */
    public void setGroup(KimGroupImpl group) {
        this.group = group;
    }
    /**
     * Gets the member attribute. 
     * @return Returns the member.
     */
    public RoleMemberImpl getMember() {
        return member;
    }
    /**
     * Sets the member attribute value.
     * @param member The member to set.
     */
    public void setMember(RoleMemberImpl member) {
        this.member = member;
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
        return getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
    }
    /**
     * Sets the overrideCode attribute value.
     * @param overrideCode The overrideCode to set.
     */
    public void setOverrideCode(String overrideCode) {
        this.overrideCode = overrideCode;
    }
    /**
     * Gets the principal attribute. 
     * @return Returns the principal.
     */
    public Person getPrincipal() {
        return principal;
    }
    /**
     * Sets the principal attribute value.
     * @param principal The principal to set.
     */
    public void setPrincipal(Person principal) {
        this.principal = principal;
    }
    /**
     * Gets the role attribute. 
     * @return Returns the role.
     */
    public KimRoleImpl getRole() {
        return role;
    }
    /**
     * Sets the role attribute value.
     * @param role The role to set.
     */
    public void setRole(KimRoleImpl role) {
        this.role = role;
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

    public void prepareForWorkflow(){
        
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
    public String getMemberTypeCode() {
        return memberTypeCode;
    }
    /**
     * Sets the memberTypeCode attribute value.
     * @param memberTypeCode The memberTypeCode to set.
     */
    public void setMemberTypeCode(String memberTypeCode) {
        this.memberTypeCode = memberTypeCode;
    }
    /**
     * Gets the memberId attribute. 
     * @return Returns the memberId.
     */
    public String getMemberId() {
        return memberId;
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

}