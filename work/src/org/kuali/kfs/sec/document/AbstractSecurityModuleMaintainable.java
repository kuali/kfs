/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sec.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.AbstractSecurityModelDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModelMember;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

public abstract class AbstractSecurityModuleMaintainable extends FinancialSystemMaintainable {

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);

        getBusinessObject().refreshNonUpdateableReferences();
        for (PersistableBusinessObject businessObject : newCollectionLines.values() ) {
            businessObject.refreshNonUpdateableReferences();
        }
    }

    protected String getDefaultRoleTypeId() {
        return KimApiServiceLocator.getKimTypeInfoService().findKimTypeByNameAndNamespace(KimConstants.KIM_TYPE_DEFAULT_NAMESPACE, KimConstants.KIM_TYPE_DEFAULT_NAME).getId();
    }
    
    protected Map<String,String> getRoleQualifiersFromSecurityModelDefinition( AbstractSecurityModelDefinition def ) {
        Map<String,String> membershipQualifications = new HashMap<String,String>(4);
        membershipQualifications.put(SecKimAttributes.CONSTRAINT_CODE, def.getConstraintCode());
        membershipQualifications.put(SecKimAttributes.OPERATOR, def.getOperatorCode());
        membershipQualifications.put(SecKimAttributes.PROPERTY_VALUE, def.getAttributeValue());
        membershipQualifications.put(SecKimAttributes.OVERRIDE_DENY, Boolean.toString(def.isOverrideDeny()));
        
        return membershipQualifications;
    }
    
    protected void updateSecurityModelRoleMember( Role modelRole, SecurityModelMember modelMember, String memberTypeCode, String memberId, Map<String,String> roleQualifiers ) {
        RoleService roleService = KimApiServiceLocator.getRoleService();

        RoleMember existingRoleMember = getRoleMembershipForMemberType(modelRole.getId(), memberId, memberTypeCode, roleQualifiers);
        
        if ( existingRoleMember == null ) {
            // new role member
            if ( memberTypeCode.equals( MemberType.PRINCIPAL.getCode() ) ) {
                roleService.assignPrincipalToRole(memberId, modelRole.getNamespaceCode(), modelRole.getName(), roleQualifiers);
            } else if ( memberTypeCode.equals( MemberType.GROUP.getCode() ) ) {
                roleService.assignGroupToRole(memberId, modelRole.getNamespaceCode(), modelRole.getName(), roleQualifiers);
            } else if ( memberTypeCode.equals( MemberType.ROLE.getCode() ) ) {
                roleService.assignRoleToRole(memberId, modelRole.getNamespaceCode(), modelRole.getName(), roleQualifiers);
            } else {
                throw new RuntimeException( "Invalid role member type code: " + memberTypeCode );
            }
            // now, we need to re-retrieve it in order to set the from/to dates
            existingRoleMember = getRoleMembershipForMemberType(modelRole.getId(), memberId, memberTypeCode, roleQualifiers);
            if ( existingRoleMember == null ) {
                throw new RuntimeException( "Role member was not saved properly.  Retrieval of role member after save failed for role: " + modelRole.getId() + " and Member Type/ID: " + memberTypeCode + "/" + memberId );
            }
        } 
        RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(existingRoleMember);
        updatedRoleMember.setAttributes(new HashMap<String,String>(0));
        updatedRoleMember.setType(MemberType.fromCode(memberTypeCode));
        updatedRoleMember.setMemberId(memberId);
        updatedRoleMember.setActiveFromDate( (modelMember.getActiveFromDate()==null)?null:new DateTime( modelMember.getActiveFromDate().getTime() ) );
        updatedRoleMember.setActiveToDate( (modelMember.getActiveToDate()==null)?null:new DateTime( modelMember.getActiveToDate().getTime() ) );
        roleService.updateRoleMember(updatedRoleMember.build());
        
    }

    /**
     * Finds the role membership (if exists) for the given role and member id
     * 
     * @param roleId id of role to find member for
     * @param memberRoleId id of member role
     * @param membershipQualifications Qualifications to match role membership
     * @return RoleMembership containing information on the member record, or null if the member id is not assigned to the role
     */
    protected RoleMember getRoleMembershipForMemberType(String roleId, String memberId, String memberType, Map<String,String> membershipQualifications) {
        RoleService roleService = KimApiServiceLocator.getRoleService();

        RoleMemberQueryResults results = roleService.findRoleMembers( 
                QueryByCriteria.Builder.fromPredicates( 
                        PredicateFactory.equal("roleId", roleId),
                        PredicateFactory.equal("typeCode", memberType),
                        PredicateFactory.equal("memberId", memberId) ) );
        for (RoleMember roleMembershipInfo : results.getResults() ) {
            // no qualifiers - then an automatic match
            if (membershipQualifications == null || membershipQualifications.isEmpty() ) {
                return roleMembershipInfo;
            }
            // otherwise, check the qualifier attributes
            if (doQualificationsMatch(membershipQualifications, roleMembershipInfo.getAttributes())) {
                return roleMembershipInfo;
            }
        }

        return null;
    }

    /**
     * Determines whether an Map<String,String> has the same keys and values as another Map<String,String>
     * 
     * @param qualfiicationToMatch Map<String,String> to match keys and values
     * @param qualfication Map<String,String> for matching
     * @return boolean if second Map<String,String> has same keys and values as first
     */
    protected boolean doQualificationsMatch(Map<String,String> qualificationToMatch, Map<String,String> qualification) {
        for (String key : qualificationToMatch.keySet()) {
            if (qualification.containsKey(key)) {
                String matchValue = qualification.get(key);
                String value = qualificationToMatch.get(key);

                if ( !StringUtils.equals(value, matchValue) ) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether each of the qualifying values in the given qualification (Map<String,String>) match the given corresponding values
     * 
     * @param membershipQualifications Map<String,String> containing qualifying values to check
     * @param constraintCode constraint code value to match
     * @param operator operator value to match
     * @param attributeValue attribute value to match
     * @return boolean true if all qualifying values match the given values, false if at least one qualifying value does not match
     */
    protected boolean doMembershipQualificationsMatchValues(Map<String,String> membershipQualifications, String constraintCode, String operator, String attributeValue) {
        String constraintQualifyValue = membershipQualifications.get(SecKimAttributes.CONSTRAINT_CODE);
        String operatorQualifyValue = membershipQualifications.get(SecKimAttributes.OPERATOR);
        String propertyValueQualifyValue = membershipQualifications.get(SecKimAttributes.PROPERTY_VALUE);

        if (!StringUtils.equals(propertyValueQualifyValue, attributeValue)) {
            return false;
        }

        if (!StringUtils.equals(constraintQualifyValue, constraintCode)) {
            return false;
        }

        if (!StringUtils.equals(operatorQualifyValue, operator)) {
            return false;
        }

        return true;
    }
}
