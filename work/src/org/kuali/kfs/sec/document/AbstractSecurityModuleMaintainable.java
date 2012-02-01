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

import org.joda.time.DateTime;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.AbstractSecurityModelDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModel;
import org.kuali.kfs.sec.businessobject.SecurityModelMember;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sec.util.KimUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMember;
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
        Map<String,String> membershipQualifications = new HashMap<String,String>();
        membershipQualifications.put(SecKimAttributes.CONSTRAINT_CODE, def.getConstraintCode());
        membershipQualifications.put(SecKimAttributes.OPERATOR, def.getOperatorCode());
        membershipQualifications.put(SecKimAttributes.PROPERTY_VALUE, def.getAttributeValue());
        membershipQualifications.put(SecKimAttributes.OVERRIDE_DENY, Boolean.toString(def.isOverrideDeny()));
        
        return membershipQualifications;
    }
    
    protected void updateSecurityModelRoleMember( SecurityModel securityModel, SecurityModelMember modelMember, String memberTypeCode, String memberId, Map<String,String> roleQualifiers ) {
        RoleService roleService = SpringContext.getBean(RoleService.class);

        RoleMember existingRoleMember = KimUtil.getRoleMembershipForMemberType(securityModel.getRoleId(), memberId, memberTypeCode, roleQualifiers);

        
        if ( existingRoleMember == null ) {
            // new role member
            if ( memberTypeCode.equals( MemberType.PRINCIPAL.getCode() ) ) {
                roleService.assignPrincipalToRole(memberId, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, securityModel.getName(), roleQualifiers);
            } else if ( memberTypeCode.equals( MemberType.GROUP.getCode() ) ) {
                roleService.assignGroupToRole(memberId, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, securityModel.getName(), roleQualifiers);
            } else if ( memberTypeCode.equals( MemberType.ROLE.getCode() ) ) {
                roleService.assignRoleToRole(memberId, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, securityModel.getName(), roleQualifiers);
            } else {
                throw new RuntimeException( "Invalid role member type code: " + memberTypeCode );
            }
            // now, we need to re-retrieve it in order to set the from/to dates
            existingRoleMember = KimUtil.getRoleMembershipForMemberType(securityModel.getRoleId(), memberId, memberTypeCode, roleQualifiers);
            if ( existingRoleMember == null ) {
                throw new RuntimeException( "Role member was not saved properly.  Retrieval of role member after save failed for role: " + securityModel.getRoleId() + " and Member Type/ID: " + memberTypeCode + "/" + memberId );
            }
        } 
        RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(existingRoleMember);
        updatedRoleMember.setAttributes(null);
        updatedRoleMember.setType(MemberType.fromCode(memberTypeCode));
        updatedRoleMember.setMemberId(memberId);
        updatedRoleMember.setActiveFromDate( (modelMember.getActiveFromDate()==null)?null:new DateTime( modelMember.getActiveFromDate().getTime() ) );
        updatedRoleMember.setActiveToDate( (modelMember.getActiveToDate()==null)?null:new DateTime( modelMember.getActiveToDate().getTime() ) );
        roleService.updateRoleMember(updatedRoleMember.build());
        
    }


}
