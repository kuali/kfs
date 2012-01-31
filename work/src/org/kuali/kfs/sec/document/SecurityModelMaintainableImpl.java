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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModel;
import org.kuali.kfs.sec.businessobject.SecurityModelDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModelMember;
import org.kuali.kfs.sec.businessobject.SecurityPrincipal;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sec.util.KimUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;


/**
 * Maintainable implementation for the Security Model maintenance document. Hooks into Post processing to create a KIM role from
 * Model and assigns users/permissions to role based on Model
 */
public class SecurityModelMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityModelMaintainableImpl.class);

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);

        this.getBusinessObject().refreshNonUpdateableReferences();
        for (Iterator iterator = newCollectionLines.values().iterator(); iterator.hasNext();) {
            PersistableBusinessObject businessObject = (PersistableBusinessObject) iterator.next();
            businessObject.refreshNonUpdateableReferences();
        }
    }

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

                createOrUpdateModelRole(oldSecurityModel, newSecurityModel);
                assignOrUpdateModelMembershipToDefinitionRoles(oldSecurityModel, newSecurityModel, newMaintenanceAction);
                assignOrUpdateModelMembers(newSecurityModel);

                if (!newSecurityModel.isActive()) {
                    inactivateModelRole(newSecurityModel);
                }

                SpringContext.getBean(IdentityManagementService.class).flushAllCaches();
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
    protected void createOrUpdateModelRole(SecurityModel oldSecurityModel, SecurityModel newSecurityModel) {
        RoleService roleService = SpringContext.getBean(RoleService.class);

        String roleName = newSecurityModel.getName();

        String roleId = newSecurityModel.getRoleId();
        if (StringUtils.isBlank(roleId)) {
            // get new id for role
            roleId = roleService.getNextAvailableRoleId();
            newSecurityModel.setRoleId(roleId);
        }

        // always set the role as active so we can add members and definitions, after processing the indicator will be updated to
        // the appropriate value
        roleService.saveRole(roleId, roleName, newSecurityModel.getDescription(), true, SecConstants.SecurityTypes.DEFAULT_ROLE_TYPE, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE);
    }

    /**
     * Saves the given security model setting the active indicator to false
     *
     * @param newSecurityModel SecurityModel to inactivate
     */
    protected void inactivateModelRole(SecurityModel newSecurityModel) {
        RoleService roleService = SpringContext.getBean(RoleService.class);

        Role roleInfo = roleService.getRole(newSecurityModel.getRoleId());

        roleService.saveRole(roleInfo.getId(), roleInfo.getName(), newSecurityModel.getDescription(), false, SecConstants.SecurityTypes.DEFAULT_ROLE_TYPE, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE);
    }

    /**
     * Iterates through the model definition list and assigns the model role to the definition role if necessary or updates the
     * current member assignment
     *
     * @param oldSecurityModel SecurityModel record before updates
     * @param newSecurityModel SecurityModel whose membership should be updated
     * @param newMaintenanceAction boolean indicating whether this is a new record (old side will not contain data)
     */
    protected void assignOrUpdateModelMembershipToDefinitionRoles(SecurityModel oldSecurityModel, SecurityModel newSecurityModel, boolean newMaintenanceAction) {
        RoleService roleService = SpringContext.getBean(RoleService.class);

        Role modelRoleInfo = roleService.getRole(newSecurityModel.getRoleId());

        for (SecurityModelDefinition securityModelDefinition : newSecurityModel.getModelDefinitions()) {
            SecurityDefinition securityDefinition = securityModelDefinition.getSecurityDefinition();

            Role definitionRoleInfo = roleService.getRole(securityDefinition.getRoleId());

            RoleMembership modelMembershipInfo = null;
            if (!newMaintenanceAction) {
                SecurityModelDefinition oldSecurityModelDefinition = null;
                for (SecurityModelDefinition modelDefinition : oldSecurityModel.getModelDefinitions()) {
                    if (modelDefinition.getModelDefinitionId() != null && securityModelDefinition.getModelDefinitionId() != null && modelDefinition.getModelDefinitionId().equals(securityModelDefinition.getModelDefinitionId())) {
                        oldSecurityModelDefinition = modelDefinition;
                    }
                }

                if (oldSecurityModelDefinition != null) {
                    Map<String,String> membershipQualifications = new HashMap<String,String>();
                    membershipQualifications.put(SecKimAttributes.CONSTRAINT_CODE, oldSecurityModelDefinition.getConstraintCode());
                    membershipQualifications.put(SecKimAttributes.OPERATOR, oldSecurityModelDefinition.getOperatorCode());
                    membershipQualifications.put(KimConstants.AttributeConstants.PROPERTY_VALUE, oldSecurityModelDefinition.getAttributeValue());
                    membershipQualifications.put(SecKimAttributes.OVERRIDE_DENY, Boolean.toString(oldSecurityModelDefinition.isOverrideDeny()));

                    if (modelRoleInfo == null || definitionRoleInfo == null) {
                        // this should throw an elegant error if either are null
                        String error = "Apparent data problem with access security. model or definition is null. this should not happen";
                        LOG.error(error);
                        throw new RuntimeException(error);
                    } else {
                        modelMembershipInfo = KimUtil.getRoleMembershipForMemberType(definitionRoleInfo.getId(), modelRoleInfo.getId(), MemberType.ROLE.getCode(), membershipQualifications);
                    }
                }
            }

            // only create membership if model is active and the model definition record is active
            boolean membershipActive = newSecurityModel.isActive() && securityModelDefinition.isActive();

            // if membership already exists, need to remove if the model definition record is now inactive or the qualifications
            // need updated
            String modelMembershipId = "";
            if (modelMembershipInfo != null) {
                modelMembershipId = modelMembershipInfo.getId();
                if (!membershipActive) {
                    roleService.removeRoleFromRole(modelMembershipInfo.getMemberId(), definitionRoleInfo.getNamespaceCode(), definitionRoleInfo.getName(), modelMembershipInfo.getQualifier());
                }
            }

            // create of update role if membership should be active
            if (membershipActive) {
                Map<String,String> membershipQualifications = new HashMap<String,String>();
                membershipQualifications.put(SecKimAttributes.CONSTRAINT_CODE, securityModelDefinition.getConstraintCode());
                membershipQualifications.put(SecKimAttributes.OPERATOR, securityModelDefinition.getOperatorCode());
                membershipQualifications.put(KimConstants.AttributeConstants.PROPERTY_VALUE, securityModelDefinition.getAttributeValue());
                membershipQualifications.put(SecKimAttributes.OVERRIDE_DENY, Boolean.toString(securityModelDefinition.isOverrideDeny()));

                if (modelRoleInfo == null || definitionRoleInfo == null) {
                    // this should throw an elegant error if either are null
                    String error = "Apparent data problem with access security. model or definition is null. this should not happen";
                    LOG.error(error);
                    throw new RuntimeException(error);
                } else {
                    roleService.saveRoleMemberForRole(modelMembershipId, modelRoleInfo.getId(), MemberType.ROLE.getCode(), definitionRoleInfo.getId(), membershipQualifications, null, null);
                }
            }
        }
    }

    /**
     * Iterates through the model member list and assign members to the model role or updates the membership
     *
     * @param securityModel SecurityModel whose member list should be updated
     */
    protected void assignOrUpdateModelMembers(SecurityModel securityModel) {
        RoleService roleService = SpringContext.getBean(RoleService.class);

        Role modelRoleInfo = roleService.getRole(securityModel.getRoleId());

        if (modelRoleInfo == null) {
            // this should throw an elegant error if either are null
            String error = "Apparent data problem with access security. model is null. this should not happen";
            LOG.error(error);
            throw new RuntimeException(error);
        } else {

            for (SecurityModelMember modelMember : securityModel.getModelMembers()) {
                RoleMembership membershipInfo = KimUtil.getRoleMembershipForMemberType(modelRoleInfo.getId(), modelMember.getMemberId(), modelMember.getMemberTypeCode(), null);

                String membershipId = "";
                if (membershipInfo != null) {
                    membershipId = membershipInfo.getId();
                }

                java.sql.Date fromDate = null;
                java.sql.Date toDate = null;
                if ( modelMember.getActiveFromDate() != null ) {
                    fromDate = new java.sql.Date( modelMember.getActiveFromDate().getTime() );
                }
                if ( modelMember.getActiveToDate() != null ) {
                    toDate = new java.sql.Date( modelMember.getActiveToDate().getTime() );
                }
                roleService.saveRoleMemberForRole(membershipId, modelMember.getMemberId(), modelMember.getMemberTypeCode(), modelRoleInfo.getId(), new HashMap<String,String>(), fromDate, toDate);

                createPrincipalSecurityRecords(modelMember.getMemberId(), modelMember.getMemberTypeCode());
            }
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
            Role roleInfo = SpringContext.getBean(RoleService.class).getRole(memberId);
            Collection<String> rolePrincipalIds = SpringContext.getBean(RoleService.class).getRoleMemberPrincipalIds(roleInfo.getNamespaceCode(), roleInfo.getName(), new HashMap<String,String>());
            principalIds.addAll(rolePrincipalIds);
        }
        else if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
            List<String> groupPrincipalIds = SpringContext.getBean(GroupService.class).getMemberPrincipalIds(memberId);
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
