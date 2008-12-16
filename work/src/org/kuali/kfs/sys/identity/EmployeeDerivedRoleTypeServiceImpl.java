/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.bo.entity.EntityAffiliation;
import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

public class EmployeeDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private static IdentityManagementService identityManagementService;
    
    protected static final String A_EMPLOYEE_STATUS_CODE = "A";
    protected static final String STAFF_AFFILIATION_TYPE_CODE = "STAFF";
    protected static final String FCLTY_AFFILIATION_TYPE_CODE = "FCLTY";
    
    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KimConstants.KIM_ATTRIB_PRINCIPAL_ID);
    }
    
    
    /**
     *  Requirements:
     *  Derived Role: Employee - EmployeeDerivedRoleTypeService
     *  - KFS-SYS Active Faculty or Staff: 
     *      principals where EMP_STAT_CD = A in KRIM_ENTITY_EMP_INFO_T and AFLTN_TYP_CD = STAFF or AFLTN_TYP_CD = FCLTY 
     *      in KRIM_ENTITY_AFLTN_T
     *  - KFS-SYS Active Professional Employee: 
     *      principals where EMP_STAT_CD = A & EMP_TYPE_CD = P in KRIM_ENTITY_EMP_INFO_T
     *   
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        
        String principalId = qualification.get(KimConstants.KIM_ATTRIB_PRINCIPAL_ID);
        KimEntity kimEntity = getIdentityManagementService().getEntityByPrincipalName(
                getIdentityManagementService().getPrincipal(principalId).getPrincipalName());
        List<String> principalIds = new ArrayList<String>();
        if(KFSConstants.SysKimConstants.ACTIVE_FACULTY_OR_STAFF_KIM_ROLE_NAME.equals(roleName)){
            for(EntityEmploymentInformation entityEmployeeInformation: kimEntity.getEmploymentInformation()){
                if(A_EMPLOYEE_STATUS_CODE.equals(entityEmployeeInformation.getEmployeeStatusCode())){
                    for(EntityAffiliation affiliation: kimEntity.getAffiliations()){
                        if(STAFF_AFFILIATION_TYPE_CODE.equalsIgnoreCase(affiliation.getAffiliationTypeCode()) 
                                || FCLTY_AFFILIATION_TYPE_CODE.equalsIgnoreCase(affiliation.getAffiliationTypeCode())){
                            principalIds.add(entityEmployeeInformation.getEmployeeId());
                        }
                    }
                }
            }
        } else if(KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME.equals(roleName)){
            for(EntityEmploymentInformation entityEmployeeInformation: kimEntity.getEmploymentInformation()){
                if("A".equals(entityEmployeeInformation.getEmployeeStatusCode()) 
                        || "P".equals(entityEmployeeInformation.getEmployeeStatusCode())){
                    principalIds.add(entityEmployeeInformation.getEmployeeId());
                }
            }
        }
        return principalIds;
    }

    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(
            String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        
        boolean hasApplicationRole = false;
        KimEntity kimEntity = getIdentityManagementService().getEntityByPrincipalName(
                getIdentityManagementService().getPrincipal(principalId).getPrincipalName());
        if(KFSConstants.SysKimConstants.ACTIVE_FACULTY_OR_STAFF_KIM_ROLE_NAME.equals(roleName)){
            for(EntityEmploymentInformation entityEmployeeInformation: kimEntity.getEmploymentInformation()){
                if(A_EMPLOYEE_STATUS_CODE.equals(entityEmployeeInformation.getEmployeeStatusCode())){
                    for(EntityAffiliation affiliation: kimEntity.getAffiliations()){
                        if(STAFF_AFFILIATION_TYPE_CODE.equalsIgnoreCase(affiliation.getAffiliationTypeCode()) 
                                || FCLTY_AFFILIATION_TYPE_CODE.equalsIgnoreCase(affiliation.getAffiliationTypeCode())){
                            hasApplicationRole = principalId.equals(entityEmployeeInformation.getEmployeeId());
                            break;
                        }
                    }
                }
            }
        } else if(KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME.equals(roleName)){
            for(EntityEmploymentInformation entityEmployeeInformation: kimEntity.getEmploymentInformation()){
                if("A".equals(entityEmployeeInformation.getEmployeeStatusCode()) 
                        || "P".equals(entityEmployeeInformation.getEmployeeStatusCode())){
                    hasApplicationRole = principalId.equals(entityEmployeeInformation.getEmployeeId());
                }
            }
        }
        return hasApplicationRole;
    }
    
    /**
     * @return the IdentityManagementService
     */
     protected static IdentityManagementService getIdentityManagementService(){
        if (identityManagementService == null ) {
            identityManagementService = KIMServiceLocator.getIdentityManagementService();
        }
        return identityManagementService;
    }

}