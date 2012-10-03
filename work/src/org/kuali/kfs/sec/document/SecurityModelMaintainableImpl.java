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
package org.kuali.kfs.sec.document;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModel;
import org.kuali.kfs.sec.businessobject.SecurityModelDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModelMember;
import org.kuali.kfs.sec.businessobject.SecurityPrincipal;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.springframework.util.ObjectUtils;


/**
 * Maintainable implementation for the Security Model maintenance document. Hooks into Post processing to create a KIM role from
 * Model and assigns users/permissions to role based on Model
 */
public class SecurityModelMaintainableImpl extends AbstractSecurityModuleMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityModelMaintainableImpl.class);

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        if (documentHeader.getWorkflowDocument().isProcessed()) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            try {
                MaintenanceDocument document = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentHeader.getDocumentNumber());
                SecurityModel oldSecurityModel = (SecurityModel) document.getOldMaintainableObject().getBusinessObject();
                SecurityModel newSecurityModel = (SecurityModel) document.getNewMaintainableObject().getBusinessObject();

                boolean newMaintenanceAction = getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_NEW_ACTION) || getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_COPY_ACTION);

                Role modelRole = createOrUpdateModelRole(newSecurityModel);
                assignOrUpdateModelMembershipToDefinitionRoles(modelRole, oldSecurityModel, newSecurityModel, newMaintenanceAction);
                assignOrUpdateModelMembers(modelRole, newSecurityModel);

                if (!newSecurityModel.isActive()) {
                    inactivateModelRole(modelRole);
                }
            }
            catch (WorkflowException e) {
                LOG.error("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
                throw new RuntimeException("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
            }
        }
    }

    /**
     * Creates a new role for the model (if the model is new), otherwise updates the role
     *
     * @param oldSecurityModel SecurityModel record before updates
     * @param newSecurityModel SecurityModel after updates
     */
    protected Role createOrUpdateModelRole(SecurityModel newSecurityModel) {
        RoleService roleService = KimApiServiceLocator.getRoleService();

        // the roles are created in the KFS-SEC namespace with the same name as the model
        Role modelRole = roleService.getRoleByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, newSecurityModel.getName());

        if ( modelRole != null ) {
            // always set the role as active so we can add members and definitions, after processing the indicator will be updated to
            // the appropriate value
            Role.Builder updatedRole = Role.Builder.create(modelRole);
            updatedRole.setActive(true);
            updatedRole.setDescription(newSecurityModel.getDescription());
            modelRole = roleService.updateRole(updatedRole.build());
        } else {
            String roleId = KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY+"-"+newSecurityModel.getId();
            Role.Builder newRole = Role.Builder.create();
            newRole.setId(roleId);
            newRole.setName(newSecurityModel.getName());
            newRole.setNamespaceCode(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY);
            newRole.setDescription(newSecurityModel.getDescription());
            newRole.setKimTypeId(getDefaultRoleTypeId());
            newRole.setActive(true);
            modelRole = roleService.createRole(newRole.build());
        }
        newSecurityModel.setRoleId(modelRole.getId());
        return modelRole;
    }

    /**
     * Saves the given security model setting the active indicator to false
     *
     * @param newSecurityModel SecurityModel to inactivate
     */
    protected void inactivateModelRole(Role modelRole) {
        RoleService roleService = KimApiServiceLocator.getRoleService();

        if ( modelRole != null ) {
            Role.Builder updatedRole = Role.Builder.create(modelRole);
            updatedRole.setActive(false);
            KimApiServiceLocator.getRoleService().updateRole(updatedRole.build());
        }
    }

    /**
     * Iterates through the model definition list and assigns the model role to the definition role if necessary or updates the
     * current member assignment
     *
     * @param oldSecurityModel SecurityModel record before updates
     * @param newSecurityModel SecurityModel whose membership should be updated
     * @param newMaintenanceAction boolean indicating whether this is a new record (old side will not contain data)
     */
    protected void assignOrUpdateModelMembershipToDefinitionRoles(Role modelRole, SecurityModel oldSecurityModel, SecurityModel newSecurityModel, boolean newMaintenanceAction) {
        RoleService roleService = KimApiServiceLocator.getRoleService();

        if ( modelRole == null ) {
            LOG.error( "Model Role does not exist for SecurityModel: " + newSecurityModel );
            throw new RuntimeException("Model Role does not exist for SecurityModel: " + newSecurityModel );
        }

        for (SecurityModelDefinition securityModelDefinition : newSecurityModel.getModelDefinitions()) {
            SecurityDefinition securityDefinition = securityModelDefinition.getSecurityDefinition();

            Role definitionRole = roleService.getRole(securityDefinition.getRoleId());

            if ( definitionRole == null ) {
                LOG.error( "Definition Role does not exist for SecurityModelDefinition: " + securityDefinition );
                throw new RuntimeException("Definition Role does not exist for SecurityModelDefinition: " + securityDefinition );
            }

            RoleMember modelRoleMembership = null;
            if (!newMaintenanceAction) {
                SecurityModelDefinition oldSecurityModelDefinition = null;
                for (SecurityModelDefinition modelDefinition : oldSecurityModel.getModelDefinitions()) {
                    if ( ObjectUtils.nullSafeEquals(modelDefinition.getModelDefinitionId(), securityModelDefinition.getModelDefinitionId()) ) {
                        oldSecurityModelDefinition = modelDefinition;
                        break;
                    }
                }

                if (oldSecurityModelDefinition != null) {
                    modelRoleMembership = getRoleMembershipForMemberType(definitionRole.getId(),
                            modelRole.getId(), MemberType.ROLE.getCode(),
                            getRoleQualifiersFromSecurityModelDefinition(oldSecurityModelDefinition));
                }
            }

            // only create membership if model is active and the model definition record is active
            boolean membershipActive = newSecurityModel.isActive() && securityModelDefinition.isActive();

            // if membership already exists, need to remove if the model definition record is now inactive or the qualifications
            // need updated
            if (modelRoleMembership != null) {
                if (!membershipActive) {
                    roleService.removeRoleFromRole(modelRoleMembership.getMemberId(), definitionRole.getNamespaceCode(), definitionRole.getName(), modelRoleMembership.getAttributes());
                }
            }

            // create of update role if membership should be active
            if (membershipActive) {
                if ( modelRoleMembership == null ) {
                    modelRoleMembership = roleService.assignRoleToRole(modelRole.getId(), definitionRole.getNamespaceCode(), definitionRole.getName(), getRoleQualifiersFromSecurityModelDefinition(securityModelDefinition));
                } else {
                    RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(modelRoleMembership);
                    updatedRoleMember.setActiveToDate(null);
                    updatedRoleMember.setAttributes(getRoleQualifiersFromSecurityModelDefinition(securityModelDefinition));
                    modelRoleMembership = roleService.updateRoleMember(updatedRoleMember.build());
                }
            }
        }
    }

    /**
     * Iterates through the model member list and assign members to the model role or updates the membership
     *
     * @param securityModel SecurityModel whose member list should be updated
     */
    protected void assignOrUpdateModelMembers( Role modelRole, SecurityModel securityModel) {
        if (modelRole == null) {
            // this should throw an elegant error if either are null
            String error = "Data problem with access security. KIM Role backing the security model is missing.  SecurityModel: " + securityModel;
            LOG.error(error);
            throw new RuntimeException(error);
        }

        for (SecurityModelMember modelMember : securityModel.getModelMembers()) {
            updateSecurityModelRoleMember(modelRole, modelMember, modelMember.getMemberTypeCode(), modelMember.getMemberId(), new HashMap<String, String>(0));

            createPrincipalSecurityRecords(modelMember.getMemberId(), modelMember.getMemberTypeCode());
        }
    }

    /**
     * Creates security principal records for model members (if necessary) so that they will appear on security principal lookup for
     * editing
     *
     * @param memberId String member id of model role
     * @param memberTypeCode String member type code for member
     */
    protected void createPrincipalSecurityRecords(String memberId, String memberTypeCode) {
        Collection<String> principalIds = new HashSet<String>();

        if (MemberType.PRINCIPAL.getCode().equals(memberTypeCode)) {
            principalIds.add(memberId);
        }
        else if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
            Role roleInfo = KimApiServiceLocator.getRoleService().getRole(memberId);
            Collection<String> rolePrincipalIds = KimApiServiceLocator.getRoleService().getRoleMemberPrincipalIds(roleInfo.getNamespaceCode(), roleInfo.getName(), null);
            principalIds.addAll(rolePrincipalIds);
        }
        else if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
            List<String> groupPrincipalIds = KimApiServiceLocator.getGroupService().getMemberPrincipalIds(memberId);
            principalIds.addAll(groupPrincipalIds);
        }

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        for (String principalId : principalIds) {
            SecurityPrincipal securityPrincipal = businessObjectService.findBySinglePrimaryKey(SecurityPrincipal.class, principalId);
            if (securityPrincipal == null) {
                SecurityPrincipal newSecurityPrincipal = new SecurityPrincipal();
                newSecurityPrincipal.setPrincipalId(principalId);

                businessObjectService.save(newSecurityPrincipal);
            }
        }
    }

    /**
     * Determines whether the given definition is part of the SecurityModel associated definitions
     *
     * @param definitionName name of definition to look for
     * @param securityModel SecurityModel to check
     * @return boolean true if the definition is in the security model, false if not
     */
    protected boolean isDefinitionInModel(String definitionName, SecurityModel securityModel) {
        for (SecurityModelDefinition securityModelDefinition : securityModel.getModelDefinitions()) {
            if (StringUtils.equalsIgnoreCase(definitionName, securityModelDefinition.getSecurityDefinition().getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Override to clear out KIM role id on copy
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        SecurityModel securityModel = (SecurityModel) document.getNewMaintainableObject().getBusinessObject();
        securityModel.setRoleId("");

        super.processAfterCopy(document, parameters);
    }


}
