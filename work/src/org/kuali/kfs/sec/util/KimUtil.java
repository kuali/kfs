/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.businessobject.SecurityModelDefinition;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.RoleManagementService;


public class KimUtil {

    /**
     * Finds the role membership (if exists) for the given role and member id
     * 
     * @param roleId id of role to find member for
     * @param memberRoleId id of member role
     * @param membershipQualifications Qualifications to match role membership
     * @return RoleMembershipInfo containing information on the member record, or null if the member id is not assigned to the role
     */
    public static RoleMembershipInfo getRoleMembershipInfoForMemberType(String roleId, String memberRoleId, String memberType, AttributeSet membershipQualifications) {
        RoleManagementService roleService = SpringContext.getBean(RoleManagementService.class);

        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleId);

        List<RoleMembershipInfo> roleMembers = roleService.getFirstLevelRoleMembers(roleIds);

        RoleMembershipInfo modelMembershipInfo = null;
        for (RoleMembershipInfo roleMembershipInfo : roleMembers) {
            if (roleMembershipInfo.getMemberTypeCode().equals(memberType) && roleMembershipInfo.getMemberId().equals(memberRoleId)) {
                if (membershipQualifications != null) {
                    boolean qualficationsMatch = doQualficationsMatch(membershipQualifications, roleMembershipInfo.getQualifier());

                    if (qualficationsMatch) {
                        modelMembershipInfo = roleMembershipInfo;
                        break;
                    }
                }
                else {
                    modelMembershipInfo = roleMembershipInfo;
                    break;
                }
            }
        }

        return modelMembershipInfo;
    }

    /**
     * Determines whether an AttributeSet has the same keys and values as another AttributeSet
     * 
     * @param qualfiicationToMatch AttributeSet to match keys and values
     * @param qualfication AttributeSet for matching
     * @return boolean if second AttributeSet has same keys and values as first
     */
    public static boolean doQualficationsMatch(AttributeSet qualfiicationToMatch, AttributeSet qualfication) {
        boolean qualficationsMatch = true;

        for (String key : qualfiicationToMatch.keySet()) {
            String value = qualfiicationToMatch.get(key);

            if (qualfication.containsKey(key)) {
                String matchValue = qualfication.get(key);

                if (!StringUtils.equals(value, matchValue)) {
                    qualficationsMatch = false;
                }
            }
            else {
                qualficationsMatch = false;
            }
        }

        return qualficationsMatch;
    }

    /**
     * Determines whether each of the qualifying values in the given qualification (AttributeSet) match the given corresponding values
     * 
     * @param membershipQualifications AttributeSet containing qualifying values to check
     * @param constraintCode constraint code value to match
     * @param operator operator value to match
     * @param attributeValue attribute value to match
     * @return boolean true if all qualifying values match the given values, false if at least one qualifying value does not match
     */
    public static boolean doMembershipQualificationsMatchValues(AttributeSet membershipQualifications, String constraintCode, String operator, String attributeValue) {
        String constraintQualifyValue = membershipQualifications.get(SecKimAttributes.CONSTRAINT_CODE);
        String operatorQualifyValue = membershipQualifications.get(SecKimAttributes.OPERATOR);
        String propertyValueQualifyValue = membershipQualifications.get(SecKimAttributes.PROPERTY_VALUE);

        if (!StringUtils.equals(constraintQualifyValue, constraintCode)) {
            return false;
        }

        if (!StringUtils.equals(operatorQualifyValue, operator)) {
            return false;
        }

        if (!StringUtils.equals(propertyValueQualifyValue, attributeValue)) {
            return false;
        }

        return true;
    }

}
