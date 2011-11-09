/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.common.assignee.Assignee;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.entity.EntityDefaultQueryResults;
import org.kuali.rice.kim.api.identity.entity.EntityQueryResults;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityAction;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

public class IdentityManagementServiceImpl implements IdentityManagementService {
	private static final Logger LOG = Logger.getLogger( IdentityManagementServiceImpl.class );

	private PermissionService permissionService;
	private ResponsibilityService responsibilityService;
	private IdentityService identityService;
	private GroupService groupService;

    @Override
	public void flushAllCaches() {
	}

    @Override
	public void flushEntityPrincipalCaches() {
	}

    @Override
	public void flushGroupCaches() {
	}

    @Override
	public void flushPermissionCaches() {
	}

    @Override
	public void flushResponsibilityCaches() {
		// nothing currently being cached
	}


    // AUTHORIZATION SERVICE
    @Override
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, Map<String, String> permissionDetails) {
    	if ( LOG.isDebugEnabled() ) {
    		logHasPermissionCheck("Permission", principalId, namespaceCode, permissionName, permissionDetails);
    	}
        boolean hasPerm = getPermissionService().hasPermission(principalId, namespaceCode, permissionName, permissionDetails);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Result: " + hasPerm );
        }
    	return hasPerm;
    }

    @Override
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
    	if ( qualification == null || qualification.isEmpty() ) {
    		return hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
    	}
    	if ( LOG.isDebugEnabled() ) {
    		logAuthorizationCheck("Permission", principalId, namespaceCode, permissionName, permissionDetails, qualification);
    	}
        boolean isAuthorized = getPermissionService().isAuthorized(principalId, namespaceCode, permissionName, permissionDetails, qualification);
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug( "Result: " + isAuthorized );
    	}
    	return isAuthorized;
    }

    @Override
    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails) {
    	if ( LOG.isDebugEnabled() ) {
    		logHasPermissionCheck("Perm Templ", principalId, namespaceCode, permissionTemplateName, permissionDetails);
    	}

		boolean hasPerm = getPermissionService().hasPermissionByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails);

   		if ( LOG.isDebugEnabled() ) {
   			LOG.debug( "Result: " + hasPerm );
   		}
    	return hasPerm;
    }

    @Override
    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
    	if ( qualification == null || qualification.isEmpty() ) {
    		return hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, new HashMap<String, String>(permissionDetails) );
    	}
    	if ( LOG.isDebugEnabled() ) {
    		logAuthorizationCheck("Perm Templ", principalId, namespaceCode, permissionTemplateName, new HashMap<String, String>(permissionDetails), new HashMap<String, String>(qualification));
    	}
    	boolean isAuthorized = getPermissionService().isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, new HashMap<String, String>(permissionDetails), new HashMap<String, String>(qualification) );
   		if ( LOG.isDebugEnabled() ) {
   			LOG.debug( "Result: " + isAuthorized );
   		}
    	return isAuthorized;
    }

    @Override
    public List<Permission> getAuthorizedPermissions(String principalId,
                                                     String namespaceCode, String permissionName, Map<String, String> permissionDetails, Map<String, String> qualification) {
    	return getPermissionService().getAuthorizedPermissions(principalId, namespaceCode, permissionName, permissionDetails, qualification);
    }

    @Override
    public List<Permission> getAuthorizedPermissionsByTemplateName(String principalId,
                                                                   String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails, Map<String, String> qualification) {
    	return getPermissionService().getAuthorizedPermissionsByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
    }

    @Override
    public boolean isPermissionDefinedForTemplateName(String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails) {
        return getPermissionService().isPermissionDefinedByTemplateName(namespaceCode, permissionTemplateName,
                permissionDetails);
    }

    @Override
	public List<Assignee> getPermissionAssignees(String namespaceCode,
			String permissionName, Map<String, String> permissionDetails, Map<String, String> qualification) {
		return this.permissionService.getPermissionAssignees(namespaceCode, permissionName,
                permissionDetails, qualification);
	}

    @Override
	public List<Assignee> getPermissionAssigneesForTemplateName(String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails,
			Map<String, String> qualification) {
		return this.permissionService.getPermissionAssigneesByTemplateName(namespaceCode, permissionTemplateName,
                new HashMap<String, String>(permissionDetails), new HashMap<String, String>(qualification));
	}

    // GROUP SERVICE
    @Override
	public boolean isMemberOfGroup(String principalId, String groupId) {
		return getGroupService().isMemberOfGroup(principalId, groupId);
	}
    @Override
	public boolean isMemberOfGroup(String principalId, String namespaceCode, String groupName) {
		Group group = getGroupByName(namespaceCode, groupName);
		return group == null ? false : isMemberOfGroup(principalId, group.getId());
    }
    @Override
	public boolean isGroupMemberOfGroup(String potentialMemberId, String potentialParentId)
	{
	       return getGroupService()
	                .isGroupMemberOfGroup(potentialMemberId, potentialParentId);
	}
    @Override
	public List<String> getGroupMemberPrincipalIds(String groupId) {
		return getGroupService().getMemberPrincipalIds(groupId);
	}
    @Override
	public List<String> getDirectGroupMemberPrincipalIds(String groupId) {
		return getGroupService().getDirectMemberPrincipalIds(groupId);
	}
    @Override
    public List<String> getGroupIdsForPrincipal(String principalId) {
		return getGroupService().getGroupIdsByPrincipalId(principalId);
	}
    @Override
    public List<String> getGroupIdsForPrincipal(String principalId, String namespaceCode ) {
		return getGroupService().getGroupIdsByPrincipalIdAndNamespaceCode(principalId, namespaceCode);
	}
    @Override
    public List<Group> getGroupsForPrincipal(String principalId) {
		return getGroupService().getGroupsByPrincipalId(principalId);
	}
    @Override
    public List<Group> getGroupsForPrincipal(String principalId, String namespaceCode ) {
		return getGroupService().getGroupsByPrincipalIdAndNamespaceCode(principalId, namespaceCode);
	}
    @Override
    public List<String> getMemberGroupIds(String groupId) {
		return getGroupService().getMemberGroupIds(groupId);
	}
    @Override
    public List<String> getDirectMemberGroupIds(String groupId) {
		return getGroupService().getDirectMemberGroupIds(groupId);
	}
    @Override
    public Group getGroup(String groupId) {
		return getGroupService().getGroup(groupId);
	}
    @Override
    public Group getGroupByName(String namespaceCode, String groupName) {
		return getGroupService().getGroupByNameAndNamespaceCode(namespaceCode, groupName);
    }
    @Override
    public List<String> getParentGroupIds(String groupId) {
		return getGroupService().getParentGroupIds(groupId);
	}
    @Override
    public List<String> getDirectParentGroupIds(String groupId) {
		return getGroupService().getDirectParentGroupIds( groupId );
	}

    @Override
    public boolean addGroupToGroup(String childId, String parentId) {
        return getGroupService().addGroupToGroup(childId, parentId);
    }

    @Override
    public boolean addPrincipalToGroup(String principalId, String groupId) {
        return getGroupService().addPrincipalToGroup(principalId, groupId);
    }

    @Override
    public boolean removeGroupFromGroup(String childId, String parentId) {
        return getGroupService().removeGroupFromGroup(childId, parentId);
    }

    @Override
    public boolean removePrincipalFromGroup(String principalId, String groupId) {
        return getGroupService().removePrincipalFromGroup(principalId, groupId);
    }

    @Override
	public Group createGroup(Group group) {
		return getGroupService().createGroup(group);
	}

    @Override
	public void removeAllMembers(String groupId) {
		getGroupService().removeAllMembers(groupId);
	}

    @Override
	public Group updateGroup(String groupId, Group group) {
		return getGroupService().updateGroup(groupId, group);
	}


    // IDENTITY SERVICE
    @Override
	public Principal getPrincipal(String principalId) {
		return getIdentityService().getPrincipal(principalId);
	}

    @Override
    public Principal getPrincipalByPrincipalName(String principalName) {
		return getIdentityService().getPrincipalByPrincipalName(principalName);
    }

    @Override
    public Principal getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
    	return getIdentityService().getPrincipalByPrincipalNameAndPassword(principalName, password);
    }

    @Override
    public EntityDefault getEntityDefaultInfo(String entityId) {
    		return getIdentityService().getEntityDefault(entityId);
    }

    @Override
    public EntityDefault getEntityDefaultInfoByPrincipalId(
    		String principalId) {
	    	return getIdentityService().getEntityDefaultByPrincipalId(principalId);
    }

    @Override
    public EntityDefault getEntityDefaultInfoByPrincipalName(
    		String principalName) {
	    	return getIdentityService().getEntityDefaultByPrincipalName(principalName);
    }

    @Override
    public EntityDefaultQueryResults findEntityDefaults(QueryByCriteria queryByCriteria) {
        return getIdentityService().findEntityDefaults(queryByCriteria);
    }

    @Override
	public Entity getEntity(String entityId) {
    		return getIdentityService().getEntity(entityId);
	}

    @Override
    public Entity getEntityByPrincipalId(String principalId) {
        return getIdentityService().getEntityByPrincipalId(principalId);
    }

    @Override
    public Entity getEntityByPrincipalName(String principalName) {
        return getIdentityService().getEntityByPrincipalName(principalName);
    }

    @Override
    public EntityQueryResults findEntities(QueryByCriteria queryByCriteria) {
        return getIdentityService().findEntities(queryByCriteria);
    }

    @Override
	public CodedAttribute getAddressType( String code ) {
		return getIdentityService().getAddressType(code);
	}

    @Override
    public CodedAttribute getEmailType( String code ) {
		return getIdentityService().getEmailType(code);
	}

    @Override
	public EntityAffiliationType getAffiliationType( String code ) {
			return getIdentityService().getAffiliationType(code);
	}

    @Override
	public CodedAttribute getCitizenshipStatus( String code ) {
			return CodedAttribute.Builder.create(getIdentityService().getCitizenshipStatus(code)).build();
	}
    @Override
	public CodedAttribute getEmploymentStatus( String code ) {
			return getIdentityService().getEmploymentStatus(code);
	}
    @Override
	public CodedAttribute getEmploymentType( String code ) {
			return getIdentityService().getEmploymentType(code);
	}
    @Override
	public CodedAttribute getEntityNameType( String code ) {
			return getIdentityService().getNameType(code);
	}
    @Override
	public CodedAttribute getEntityType( String code ) {
		return getIdentityService().getEntityType(code);
	}
    @Override
	public EntityExternalIdentifierType getExternalIdentifierType( String code ) {
			return getIdentityService().getExternalIdentifierType(code);
	}
    @Override
	public CodedAttribute getPhoneType( String code ) {
			return getIdentityService().getPhoneType(code);
	}

    // ----------------------
    // Responsibility Methods
    // ----------------------

    @Override
	public Responsibility getResponsibility(String responsibilityId) {
		return getResponsibilityService().getResponsibility( responsibilityId );
	}

    @Override
	public boolean hasResponsibility(String principalId, String namespaceCode,
			String responsibilityName, Map<String, String> qualification,
			Map<String, String> responsibilityDetails) {
		return getResponsibilityService().hasResponsibility( principalId, namespaceCode, responsibilityName,
                qualification, responsibilityDetails );
	}

    @Override
	public Responsibility getResponsibilityByName( String namespaceCode, String responsibilityName) {
		return getResponsibilityService().findRespByNamespaceCodeAndName(namespaceCode, responsibilityName);
	}

    @Override
	public List<ResponsibilityAction> getResponsibilityActions( String namespaceCode, String responsibilityName,
    		Map<String, String> qualification, Map<String, String> responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActions( namespaceCode, responsibilityName, qualification,
                responsibilityDetails );
	}

    @Override
	public List<ResponsibilityAction> getResponsibilityActionsByTemplateName(
			String namespaceCode, String responsibilityTemplateName,
			Map<String, String> qualification, Map<String, String> responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActionsByTemplateName(namespaceCode, responsibilityTemplateName,
                qualification, responsibilityDetails);
	}

    @Override
	public boolean hasResponsibilityByTemplateName(String principalId,
			String namespaceCode, String responsibilityTemplateName,
			Map<String, String> qualification, Map<String, String> responsibilityDetails) {
		return getResponsibilityService().hasResponsibilityByTemplateName(principalId, namespaceCode, responsibilityTemplateName,
                qualification, responsibilityDetails);
	}

    protected void logAuthorizationCheck(String checkType, String principalId, String namespaceCode, String permissionName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
		StringBuilder sb = new StringBuilder();
		sb.append(  '\n' );
		sb.append( "Is AuthZ for " ).append( checkType ).append( ": " ).append( namespaceCode ).append( "/" ).append( permissionName ).append( '\n' );
		sb.append( "             Principal:  " ).append( principalId );
		if ( principalId != null ) {
			Principal principal = getPrincipal( principalId );
			if ( principal != null ) {
				sb.append( " (" ).append( principal.getPrincipalName() ).append( ')' );
			}
		}
		sb.append( '\n' );
		sb.append( "             Details:\n" );
		if ( permissionDetails != null ) {
			sb.append( permissionDetails);
		} else {
			sb.append( "                         [null]\n" );
		}
		sb.append( "             Qualifiers:\n" );
		if ( qualification != null && !qualification.isEmpty() ) {
			sb.append( qualification);
		} else {
			sb.append( "                         [null]\n" );
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace( sb.append(ExceptionUtils.getStackTrace(new Throwable())));
		} else {
			LOG.debug(sb.toString());
		}
    }

    protected void logHasPermissionCheck(String checkType, String principalId, String namespaceCode, String permissionName, Map<String, String> permissionDetails ) {
		StringBuilder sb = new StringBuilder();
		sb.append(  '\n' );
		sb.append( "Has Perm for " ).append( checkType ).append( ": " ).append( namespaceCode ).append( "/" ).append( permissionName ).append( '\n' );
		sb.append( "             Principal:  " ).append( principalId );
		if ( principalId != null ) {
			Principal principal = getPrincipal( principalId );
			if ( principal != null ) {
				sb.append( " (" ).append( principal.getPrincipalName() ).append( ')' );
			}
		}
		sb.append(  '\n' );
		sb.append( "             Details:\n" );
		if ( permissionDetails != null ) {
			sb.append( permissionDetails);
		} else {
			sb.append( "                         [null]\n" );
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace( sb.append( ExceptionUtils.getStackTrace(new Throwable())) );
		} else {
			LOG.debug(sb.toString());
		}
    }

    	// OTHER METHODS

	public IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}

	public GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KimApiServiceLocator.getGroupService();
		}
		return groupService;
	}

	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KimApiServiceLocator.getPermissionService();
		}
		return permissionService;
	}

	public ResponsibilityService getResponsibilityService() {
		if ( responsibilityService == null ) {
			responsibilityService = KimApiServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}
}
