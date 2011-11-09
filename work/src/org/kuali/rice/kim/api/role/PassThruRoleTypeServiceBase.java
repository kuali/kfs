/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.api.role;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.framework.role.RoleTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class PassThruRoleTypeServiceBase implements RoleTypeService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PassThruRoleTypeServiceBase.class);
	
	public static final String UNMATCHABLE_QUALIFICATION = "!~!~!~!~!~";

    @Override
	public abstract Map<String, String> convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String, String> qualification);
    
    @Override
	public List<RoleMembership> getMatchingRoleMemberships(Map<String, String> qualification,
            List<RoleMembership> roleMemberList) {

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null");
        }

        if (roleMemberList == null) {
            throw new RiceIllegalArgumentException("roleMemberList was null");
        }
        return Collections.unmodifiableList(new ArrayList<RoleMembership>(roleMemberList));
    }

    @Override
	public boolean doesRoleQualifierMatchQualification(Map<String, String> qualification, Map<String, String> roleQualifier) {
        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null");
        }

        if (roleQualifier == null) {
            throw new RiceIllegalArgumentException("roleQualifier was null");
        }

        return true;
    }
    
    @Override
	public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId was null or blank");
        }

        if (groupIds == null) {
            throw new RiceIllegalArgumentException("groupIds was null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName was null or blank");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null");
        }

        return false;
    }

    @Override
	public boolean isApplicationRoleType() {
        return false;
    }

    public List<String> getAcceptedAttributeNames() {
        return Collections.emptyList();
    }

    @Override
	public List<KimAttributeField> getAttributeDefinitions(String kimTypeId) {
        if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        return Collections.emptyList();
    }

    @Override
	public String getWorkflowDocumentTypeName() {
        return null;
    }

    @Override
	public List<String> getWorkflowRoutingAttributes(String routeLevel) {
    	if (StringUtils.isBlank(routeLevel)) {
            throw new RiceIllegalArgumentException("routeLevel was null or blank");
        }

        return Collections.emptyList();
    }

    public boolean supportsAttributes(List<String> attributeNames) {
        if (attributeNames == null) {
            throw new RiceIllegalArgumentException("attributeNames was null");
        }

        return true;
    }

    public Map<String, String> translateInputAttributes(Map<String, String> inputAttributes) {
        if (inputAttributes == null) {
            throw new RiceIllegalArgumentException("inputAttributes was null");
        }

        return inputAttributes;
    }

    @Override
	public List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String, String> attributes) {
        if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        if (attributes == null) {
            throw new RiceIllegalArgumentException("attributes was null or blank");
        }

        return Collections.emptyList();
    }
    
	@Override
	public List<RemotableAttributeError> validateAttributesAgainstExisting(String kimTypeId, Map<String, String> newAttributes, Map<String, String> oldAttributes){
		if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId was null or blank");
        }

        if (newAttributes == null) {
            throw new RiceIllegalArgumentException("newAttributes was null or blank");
        }

        if (oldAttributes == null) {
            throw new RiceIllegalArgumentException("oldAttributes was null or blank");
        }

        return Collections.emptyList();
	}

	@Override
	public boolean dynamicRoleMembership(String namespaceCode, String roleName) {
	    if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

	    if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName was null or blank");
        }

        return false;
	}

}
