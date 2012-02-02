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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityDefinitionDocumentType;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;


/**
 * Maintainable implementation for the Security Definition maintenance document. Hooks into Post processing to create the KIM permissions from the definition records
 */
public class SecurityDefinitionMaintainableImpl extends AbstractSecurityModuleMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityDefinitionMaintainableImpl.class);

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
                SecurityDefinition oldSecurityDefinition = (SecurityDefinition) document.getOldMaintainableObject().getBusinessObject();
                SecurityDefinition newSecurityDefinition = (SecurityDefinition) document.getNewMaintainableObject().getBusinessObject();

                oldSecurityDefinition.refreshNonUpdateableReferences();
                newSecurityDefinition.refreshNonUpdateableReferences();

                boolean newMaintenanceAction = getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_NEW_ACTION) || getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_COPY_ACTION);

                createOrUpdateDefinitionRole(oldSecurityDefinition, newSecurityDefinition);

                createOrUpdateDocumentPermissions(oldSecurityDefinition, newSecurityDefinition, newMaintenanceAction);
                createOrUpdateLookupPermission(oldSecurityDefinition, newSecurityDefinition, newMaintenanceAction);
                createOrUpdateInquiryPermissions(oldSecurityDefinition, newSecurityDefinition, newMaintenanceAction);


                SpringContext.getBean(IdentityManagementService.class).flushAllCaches();
            }
            catch (WorkflowException e) {
                LOG.error("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
                throw new RuntimeException("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
            }
        }
    }

    /**
     * Creates a new role for the definition (if the definition is new), then grants to the role any new permissions granted for the definition. Also update role active indicator
     * if indicator changed values on the definition
     *
     * @param oldSecurityDefinition SecurityDefinition record before updates
     * @param newSecurityDefinition SecurityDefinition after updates
     */
    protected void createOrUpdateDefinitionRole(SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition ) { //, List<Permission> permissionsToAssign ) {
        RoleService roleService = SpringContext.getBean(RoleService.class);
        PermissionService permissionService = SpringContext.getBean(PermissionService.class);

        Role oldRole = null;
        if ( StringUtils.isNotBlank(oldSecurityDefinition.getRoleId()) ) {
            oldRole = roleService.getRole(oldSecurityDefinition.getRoleId());
        }

        if ( oldRole == null ) {
            Role.Builder newRole = Role.Builder.create();
            newRole.setNamespaceCode(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY);
            newRole.setName(newSecurityDefinition.getName());
            newRole.setDescription(newSecurityDefinition.getDescription());
            newRole.setActive(newSecurityDefinition.isActive());
            newRole.setKimTypeId(getDefaultRoleTypeId());
            roleService.createRole(newRole.build());
            Role createdRole = roleService.getRoleByNameAndNamespaceCode(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, newSecurityDefinition.getName());
            newSecurityDefinition.setRoleId(createdRole.getId());
        } else {
            // update role active indicator if it has been updated on the definition
            if ( oldSecurityDefinition.isActive() != newSecurityDefinition.isActive() ) {
                Role.Builder updatedRole = Role.Builder.create(oldRole);
                updatedRole.setActive(newSecurityDefinition.isActive());
                roleService.updateRole(updatedRole.build());
            }
        }
        
        // assign all permissions for definition to role (have same name as role)
//        List<Permission> permissions = permissionService.findPermByNamespaceCodeAndName(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, roleName);
//        for (Permission perm : permissionsToAssign) {
//            List<String> permissionRoleIds = permissionService.getRoleIdsForPermission(perm.getNamespaceCode(), perm.getName(), null);
//            if (!permissionRoleIds.contains(newSecurityDefinition.getRoleId())) {
//                roleService.assignPermissionToRole(perm.getId(), newSecurityDefinition.getRoleId());
//            }
//        }
    }

    /**
     * Iterates through the document types and creates any new document permissions necessary or updates old permissions setting inactive if needed
     *
     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
     * @param newMaintenanceAction Indicates whether this is a new maintenance record (old side in empty)
     */
    protected void createOrUpdateDocumentPermissions(SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition, boolean newMaintenanceAction) {
        for (SecurityDefinitionDocumentType definitionDocumentType : newSecurityDefinition.getDefinitionDocumentTypes()) {
            String documentType = definitionDocumentType.getFinancialSystemDocumentTypeCode();
            boolean documentTypePermissionActive = newSecurityDefinition.isActive() && definitionDocumentType.isActive();
            //boolean isNewDocumentType = newMaintenanceAction || !isDocumentTypeInDefinition(documentType, oldSecurityDefinition);

            createOrUpdateDocumentTypePermissions(documentType, documentTypePermissionActive, oldSecurityDefinition, newSecurityDefinition);
        }
    }

    /**
     * First tries to retrieve a lookup permission previously setup for this definition. If old permission found it will be updated with the new details and its active indicator
     * will be set based on the definition active indicator and restrict lookup indicator value. If old permission does not exist but restrict lookup indicator is true on new side
     * then a new permission will be created and will be active if definition is active on new side.
     *
     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
     * @param newMaintenanceAction Indicates whether this is a new maintenance record (old side in empty)
     */
    protected void createOrUpdateLookupPermission(SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition, boolean newMaintenanceAction) {
        Map<String,String> permissionDetails = populateLookupPermissionDetails(newSecurityDefinition);

        String permissionId = "";
        if (!newMaintenanceAction) {
            // find old Lookup permission
            List<Permission> permissions = findSecurityPermissionsByNameAndTemplate(oldSecurityDefinition.getName(), getAccessSecurityService().getLookupWithFieldValueTemplate());
            if (permissions != null && !permissions.isEmpty()) {
                Permission oldPermission = permissions.get(0);
                permissionId = oldPermission.getId();
            }
        }

        // need to save lookup permission if new side indicator is true or already has a permission in which case we need to update details and active indicator
        if (newSecurityDefinition.isRestrictLookup() || StringUtils.isNotBlank(permissionId)) {
            createOrUpdateDocumentTypePermissionAndAssignToRole(newSecurityDefinition, permissionId, getAccessSecurityService().getLookupWithFieldValueTemplate(), newSecurityDefinition.isActive() && newSecurityDefinition.isRestrictLookup(), permissionDetails);
        }
    }

    /**
     * First tries to find inquiry permissions for GL namespace and LD namespace. If old permissions are found they will be updated with the new details and active indicator will
     * be set based on the definition active indicator and restrict gl indicator (for gl inqury permission) and restrict ld inquiry (for ld inquiry permission). If an old
     * permission does not exist for one or both of the namespaces and the corresponding indicators are set to true on new side then new permissions will be created with active
     * indicator set to true if definition is active on new side.
     *
     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
     * @param newMaintenanceAction Indicates whether this is a new maintenance record (old side in empty)
     */
    protected void createOrUpdateInquiryPermissions(SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition) {
        // find old inquiry permissions
        String glPermissionName = newSecurityDefinition.getName() + "/" + getAccessSecurityService().getInquiryWithFieldValueTemplate().getName() + "/" + KFSConstants.CoreModuleNamespaces.GL;
        String ldPermissionName = newSecurityDefinition.getName() + "/" + getAccessSecurityService().getInquiryWithFieldValueTemplate().getName() + "/" + KFSConstants.OptionalModuleNamespaces.LABOR_DISTRIBUTION;
        
        Permission glPermission = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, glPermissionName );
        Permission ldPermission = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, ldPermissionName );

        // need to save gl inquiry permission if new side indicator is true or already has a permission in which case we need to update details and active indicator
        if ( newSecurityDefinition.isRestrictGLInquiry() ) {
            if ( glPermission == null ) {
                createOrUpdateDocumentTypePermissionAndAssignToRole(oldSecurityDefinition, newSecurityDefinition, 
                        true, getAccessSecurityService().getInquiryWithFieldValueTemplate(), 
                        glPermissionName, 
                        populateInquiryPermissionDetails(KFSConstants.CoreModuleNamespaces.GL,newSecurityDefinition));
            } else if ( !glPermission.isActive() ) {
                // reactivate the permission
            }
        } else {
            if ( glPermission != null && glPermission.isActive() ) {
                // inactivate the permission
            }
        }
        // TODO: complete the above and then refactor so that code is not duplicated for LD
        
        
        if (newSecurityDefinition.isRestrictGLInquiry() || StringUtils.isNotBlank(glPermissionId)) {
            Map<String,String> permissionDetails = populateInquiryPermissionDetails(KFSConstants.ParameterNamespaces.GL, newSecurityDefinition);
            createOrUpdateDocumentTypePermissionAndAssignToRole(newSecurityDefinition, glPermissionId, getAccessSecurityService().getInquiryWithFieldValueTemplate(), newSecurityDefinition.isActive() && newSecurityDefinition.isRestrictGLInquiry(), permissionDetails);
        }

        // need to save ld inquiry permission if new side indicator is true or already has a permission in which case we need to update details and active indicator
        if (newSecurityDefinition.isRestrictLaborInquiry() || StringUtils.isNotBlank(ldPermissionId)) {
            Map<String,String> permissionDetails = populateInquiryPermissionDetails(SecConstants.LABOR_MODULE_NAMESPACE_CODE, newSecurityDefinition);
            createOrUpdateDocumentTypePermissionAndAssignToRole(newSecurityDefinition, ldPermissionId, getAccessSecurityService().getInquiryWithFieldValueTemplate(), newSecurityDefinition.isActive() && newSecurityDefinition.isRestrictLaborInquiry(), permissionDetails);
        }
    }

    /**
     * For each of the document templates ids calls helper method to create or update corresponding permission
     *
     * @param documentType workflow document type name for permission detail
     * @param active boolean indicating whether the permissions should be set to active (true) or non-active (false)
     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
     */
    protected void createOrUpdateDocumentTypePermissions(String documentType, boolean active, SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition) {
        Map<String,String> permissionDetails = populateDocumentTypePermissionDetails(documentType, newSecurityDefinition);
        // Permission Names must be unique
        // So - Security Definition Name/template name/document type
        // view document
        Template permissionTemplate = getAccessSecurityService().getViewDocumentWithFieldValueTemplate();
        String permissionName = newSecurityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdateDocumentTypePermissionAndAssignToRole(oldSecurityDefinition, newSecurityDefinition, active && newSecurityDefinition.isRestrictViewDocument(), permissionTemplate, permissionName, permissionDetails);

        // view accounting line
        permissionTemplate = getAccessSecurityService().getViewAccountingLineWithFieldValueTemplate();
        permissionName = newSecurityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdateDocumentTypePermissionAndAssignToRole(oldSecurityDefinition, newSecurityDefinition, active && newSecurityDefinition.isRestrictViewAccountingLine(),  permissionTemplate, permissionName, permissionDetails);

        // view notes/attachments
        permissionTemplate = getAccessSecurityService().getViewNotesAttachmentsWithFieldValueTemplate();
        permissionName = newSecurityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdateDocumentTypePermissionAndAssignToRole(oldSecurityDefinition, newSecurityDefinition, active && newSecurityDefinition.isRestrictViewNotesAndAttachments(),  permissionTemplate, permissionName, permissionDetails);

        // edit accounting line
        permissionTemplate = getAccessSecurityService().getEditAccountingLineWithFieldValueTemplate();
        permissionName = newSecurityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdateDocumentTypePermissionAndAssignToRole(oldSecurityDefinition, newSecurityDefinition, active && newSecurityDefinition.isRestrictEditAccountingLine(),  permissionTemplate, permissionName, permissionDetails);

        // edit document
        permissionTemplate = getAccessSecurityService().getEditDocumentWithFieldValueTemplate();
        permissionName = newSecurityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdateDocumentTypePermissionAndAssignToRole(oldSecurityDefinition, newSecurityDefinition, active && newSecurityDefinition.isRestrictEditDocument(),  permissionTemplate, permissionName, permissionDetails);
    }

//    /**
//     * First tries to find an existing permission for the document type, template, and definition. If found the permission will be updated with the new details and the active
//     * indicator will be updated based on the active parameter. If not found and active parameter is true, then a new permission is created for the given doc type, template, and
//     * definition.
//     *
//     * @param documentType workflow document type name for permission detail
//     * @param active boolean indicating whether the permissions should be set to active (true) or non-active (false)
//     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
//     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
//     * @param templateId KIM template id for the permission record that is should be created or updated
//     */
//    protected void createOrUpdateDocumentTypePermission(String documentType, boolean active, SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition, String templateId) {
//        Map<String,String> permissionDetails = populateDocumentTypePermissionDetails(documentType, newSecurityDefinition);
//
//        Permission oldPermission = findDocumentPermission(oldSecurityDefinition, templateId, documentType);
//        String permissionId = "";
//        if (oldPermission != null) {
//            permissionId = oldPermission.getId();
//        }
//
//        createOrUpdateDocumentTypePermissionAndAssignToRole(newSecurityDefinition, permissionId, templateId, active, permissionDetails);
//    }

    /**
     * Builds an Map<String,String> populated from the given method parameters. Details are set based on the KIM 'Security Document Permission' type.
     *
     * @param documentType workflow document type name
     * @param securityDefinition SecurityDefiniton record
     * @return Map<String,String> populated with document type name, property name, operator, and property value details
     */
    protected Map<String,String> populateDocumentTypePermissionDetails(String documentType, SecurityDefinition securityDefinition) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentType);
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, securityDefinition.getSecurityAttribute().getName());

        return permissionDetails;
    }

    /**
     * Builds an Map<String,String> populated from the given method parameters. Details are set based on the KIM 'Security Lookup Permission' type.
     *
     * @param securityDefinition SecurityDefiniton record
     * @return Map<String,String> populated with property name, operator, and property value details
     */
    protected Map<String,String> populateLookupPermissionDetails(SecurityDefinition securityDefinition) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, securityDefinition.getSecurityAttribute().getName());

        return permissionDetails;
    }

    /**
     * Builds an Map<String,String> populated from the given method parameters. Details are set based on the KIM 'Security Inquiry Permission' type.
     *
     * @param namespaceCode KIM namespace code
     * @param securityDefinition SecurityDefiniton record
     * @return Map<String,String> populated with namespace, property name, operator, and property value details
     */
    protected Map<String,String> populateInquiryPermissionDetails(String namespaceCode, SecurityDefinition securityDefinition) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, securityDefinition.getSecurityAttribute().getName());

        return permissionDetails;
    }

//    /**
//     * Calls helper method to find all permissions for the given template ID and security defintion name (permission name). Iterates through the results to find the permission with
//     * matching document type detail
//     *
//     * @param securityDefinition SecurityDefiniton record for permission
//     * @param templateId KIM template ID for permission
//     * @param documentType KEW document type name for permission detail
//     * @return Permission provides information on the matching permission
//     */
//    protected Permission findDocumentPermission(SecurityDefinition securityDefinition, String templateId, String documentType) {
//        // get all the permissions for the definition record and template
//        List<Permission> permissions = findSecurityPermissionsByNameAndTemplate(securityDefinition.getName(), templateId);
//
//        // iterate through permission list finding permissions that have the document type detail
//        Permission foundPermission = null;
//        for (Permission permissionInfo : permissions) {
//            String permissionDocType = permissionInfo.getAttributes().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
//            if (StringUtils.equalsIgnoreCase(documentType, permissionDocType)) {
//                foundPermission = permissionInfo;
//                break;
//            }
//        }
//
//        return foundPermission;
//    }

//    /**
//     * Calls permission service to find all permissions for the given name. Iterates through results and finds ones that match given template ID as well
//     *
//     * @param permissionName name of permission to find
//     * @param templateId KIM template ID of permission to find
//     * @return List<Permission> List of matching permissions
//     * @see org.kuali.rice.kim.service.PermissionService#getPermissionsByName()
//     */
//    protected List<Permission> findSecurityPermissionsByNameAndTemplate(String permissionName, String templateId) {
//        PermissionService permissionService = SpringContext.getBean(PermissionService.class);
//
//        // get all the permissions for the given name
//        throw new UnsupportedOperationException("Implmentation not correct for Rice 2.0 - needs to use a criteria to and a prefix string");
//        List<Permission> permissions = permissionService.findPermsByNamespaceCodeTemplateName(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, permissionName);
//
//        List<Permission> templatePermissions = new ArrayList<Permission>();
//        for (Permission permissionInfo : permissions) {
//            if (StringUtils.equals(templateId, permissionInfo.getTemplate().getId())) {
//                templatePermissions.add(permissionInfo);
//            }
//        }
//
//        return templatePermissions;
//    }

    /**
     * Determines whether a given document type name is included in the document type list for the given security definition
     *
     * @param documentType KEW document type name
     * @param oldSecurityDefinition SecurityDefinition record
     * @return boolean indicating whether the document type is associated with the given security definition
     */
    protected boolean isDocumentTypeInDefinition(String documentType, SecurityDefinition oldSecurityDefinition) {
        for (SecurityDefinitionDocumentType definitionDocumentType : oldSecurityDefinition.getDefinitionDocumentTypes()) {
            String oldDocumentType = definitionDocumentType.getFinancialSystemDocumentTypeCode();
            if (StringUtils.equals(documentType, oldDocumentType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calls PermissionUpdateService to save a permission.
     *
     * @param securityDefinition SecurityDefinition record
     * @param permissionId ID for the permission being saved, or empty for new permission
     * @param permissionTemplateId KIM template ID for permission to save
     * @param active boolean indicating whether the permission should be set to active (true) or non-active (false)
     * @param permissionDetails Map<String,String> representing the permission details
     * @see org.kuali.rice.kim.service.PermissionUpdateService#savePermission()
     */
    protected void createOrUpdateDocumentTypePermissionAndAssignToRole(SecurityDefinition oldSecurityDefinition, SecurityDefinition securityDefinition, boolean active, Template permissionTemplate, String permissionName, Map<String,String> permissionDetails) {
//        String permissionName = securityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + permissionDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        
        // Ensure it does not already exist
        Permission perm = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, permissionName);
        
        if ( perm == null ) {
            if ( active ) {
                Permission.Builder newPerm = Permission.Builder.create(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, permissionName);
                newPerm.setTemplate( Template.Builder.create(permissionTemplate) );
                newPerm.setDescription(securityDefinition.getDescription() );
                newPerm.setAttributes(permissionDetails);
                newPerm.setActive(true);
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "About to save new permission: " + newPerm);
                }        
                KimApiServiceLocator.getPermissionService().createPermission(newPerm.build());
                // now, reload to get the permission ID
                perm = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, permissionName);
            }
        } else {
            if ( perm.isActive() != active ) {
                Permission.Builder updatedPerm = Permission.Builder.create(perm);            
                updatedPerm.setActive(active);
                KimApiServiceLocator.getPermissionService().updatePermission(updatedPerm.build());
            }
        }
        
        if ( perm != null ) {
            if ( active ) {
                KimApiServiceLocator.getRoleService().assignPermissionToRole(perm.getId(), securityDefinition.getRoleId());
            } else {
                // RICE20: We need this API to exist on Rice 2.0
//                KimApiServiceLocator.getRoleService().removePermissionFromRole(perm.getId(), securityDefinition.getRoleId());
            }
        }
    }

    /**
     * Override to clear out KIM role id on copy
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        SecurityDefinition securityDefinition = (SecurityDefinition) document.getNewMaintainableObject().getBusinessObject();
        securityDefinition.setRoleId("");

        super.processAfterCopy(document, parameters);
    }

    private static AccessSecurityService accessSecurityService;

    public static AccessSecurityService getAccessSecurityService() {
        if ( accessSecurityService == null ) {
            accessSecurityService = SpringContext.getBean(AccessSecurityService.class);
        }
        return accessSecurityService;
    }
}
