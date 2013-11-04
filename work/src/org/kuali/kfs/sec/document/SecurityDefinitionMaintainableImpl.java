/*
 * Copyright 2009 The Kuali Foundation.
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
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityDefinitionDocumentType;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.role.Role;
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

                createOrUpdateDocumentPermissions(newSecurityDefinition);
                createOrUpdateLookupPermission(newSecurityDefinition);
                createOrUpdateInquiryPermissions(newSecurityDefinition);
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
        Role oldRole = null;
        if ( StringUtils.isNotBlank(oldSecurityDefinition.getRoleId()) ) {
            oldRole = KimApiServiceLocator.getRoleService().getRole(oldSecurityDefinition.getRoleId());
        }

        if ( oldRole == null ) {
            Role.Builder newRole = Role.Builder.create();
            newRole.setNamespaceCode(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY);
            newRole.setName(newSecurityDefinition.getName());
            newRole.setDescription(newSecurityDefinition.getDescription());
            newRole.setActive(newSecurityDefinition.isActive());
            newRole.setKimTypeId(getDefaultRoleTypeId());
            Role createdRole = KimApiServiceLocator.getRoleService().createRole(newRole.build());
            newSecurityDefinition.setRoleId(createdRole.getId());
        } else {
            // update role active indicator if it has been updated on the definition
            if ( oldSecurityDefinition.isActive() != newSecurityDefinition.isActive() ) {
                Role.Builder updatedRole = Role.Builder.create(oldRole);
                updatedRole.setActive(newSecurityDefinition.isActive());
                KimApiServiceLocator.getRoleService().updateRole(updatedRole.build());
            }
        }
    }

    /**
     * Iterates through the document types and creates any new document permissions necessary or updates old permissions setting inactive if needed
     *
     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
     * @param newMaintenanceAction Indicates whether this is a new maintenance record (old side in empty)
     */
    protected void createOrUpdateDocumentPermissions(SecurityDefinition securityDefinition) {
        for (SecurityDefinitionDocumentType definitionDocumentType : securityDefinition.getDefinitionDocumentTypes()) {
            String documentType = definitionDocumentType.getFinancialSystemDocumentTypeCode();
            boolean documentTypePermissionActive = securityDefinition.isActive() && definitionDocumentType.isActive();

            createOrUpdateDocumentTypePermissions(documentType, documentTypePermissionActive, securityDefinition);
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
    protected void createOrUpdateLookupPermission(SecurityDefinition securityDefinition) {
        Template lookupTemplate = getAccessSecurityService().getLookupWithFieldValueTemplate();

        String permissionName = securityDefinition.getName() + "/" + lookupTemplate.getName();

        createOrUpdatePermissionAndAssignToRole(permissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(), securityDefinition.isRestrictLookup(), lookupTemplate, getLookupPermissionDetails(securityDefinition));
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
    protected void createOrUpdateInquiryPermissions(SecurityDefinition securityDefinition) {
        // find old inquiry permissions
        Template inquiryTemplate = getAccessSecurityService().getInquiryWithFieldValueTemplate();
        String glPermissionName = securityDefinition.getName() + "/" + inquiryTemplate.getName() + "/" + KFSConstants.CoreModuleNamespaces.GL;
        String ldPermissionName = securityDefinition.getName() + "/" + inquiryTemplate.getName() + "/" + KFSConstants.OptionalModuleNamespaces.LABOR_DISTRIBUTION;

        Permission glPermission = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, glPermissionName );
        Permission ldPermission = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, ldPermissionName );

        // need to save gl inquiry permission if new side indicator is true or already has a permission in which case we need to update details and active indicator
        createOrUpdatePermissionAndAssignToRole(glPermissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(), securityDefinition.isRestrictGLInquiry(), inquiryTemplate, getInquiryPermissionDetails(KFSConstants.CoreModuleNamespaces.GL,securityDefinition));

        createOrUpdatePermissionAndAssignToRole(ldPermissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(), securityDefinition.isRestrictLaborInquiry(), inquiryTemplate, getInquiryPermissionDetails(KFSConstants.OptionalModuleNamespaces.LABOR_DISTRIBUTION,securityDefinition));
    }

    /**
     * For each of the document templates ids calls helper method to create or update corresponding permission
     *
     * @param documentType workflow document type name for permission detail
     * @param active boolean indicating whether the permissions should be set to active (true) or non-active (false)
     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
     */
    protected void createOrUpdateDocumentTypePermissions(String documentType, boolean active, SecurityDefinition securityDefinition) {
        Map<String,String> permissionDetails = populateDocumentTypePermissionDetails(documentType, securityDefinition);
        // Permission Names must be unique
        // So - Security Definition Name/template name/document type
        // view document
        Template permissionTemplate = getAccessSecurityService().getViewDocumentWithFieldValueTemplate();
        String permissionName = securityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdatePermissionAndAssignToRole(permissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(),
                active && securityDefinition.isRestrictViewDocument(), permissionTemplate, permissionDetails);

        // view accounting line
        permissionTemplate = getAccessSecurityService().getViewAccountingLineWithFieldValueTemplate();
        permissionName = securityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdatePermissionAndAssignToRole(permissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(),
                active && securityDefinition.isRestrictViewAccountingLine(),  permissionTemplate, permissionDetails);

        // view notes/attachments
        permissionTemplate = getAccessSecurityService().getViewNotesAttachmentsWithFieldValueTemplate();
        permissionName = securityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdatePermissionAndAssignToRole(permissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(),
                active && securityDefinition.isRestrictViewNotesAndAttachments(),  permissionTemplate, permissionDetails);

        // edit accounting line
        permissionTemplate = getAccessSecurityService().getEditAccountingLineWithFieldValueTemplate();
        permissionName = securityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdatePermissionAndAssignToRole(permissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(),
                active && securityDefinition.isRestrictEditAccountingLine(),  permissionTemplate, permissionDetails);

        // edit document
        permissionTemplate = getAccessSecurityService().getEditDocumentWithFieldValueTemplate();
        permissionName = securityDefinition.getName() + "/" + permissionTemplate.getName() + "/" + documentType;
        createOrUpdatePermissionAndAssignToRole(permissionName, securityDefinition.getRoleId(), securityDefinition.getDescription(),
                active && securityDefinition.isRestrictEditDocument(),  permissionTemplate, permissionDetails);
    }

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
    protected Map<String,String> getLookupPermissionDetails(SecurityDefinition securityDefinition) {
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
    protected Map<String,String> getInquiryPermissionDetails(String namespaceCode, SecurityDefinition securityDefinition) {
        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, securityDefinition.getSecurityAttribute().getName());

        return permissionDetails;
    }

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
    protected void createOrUpdatePermissionAndAssignToRole(String permissionName, String roleId, String permissionDescription, boolean active, Template permissionTemplate, Map<String,String> permissionDetails) {
        // Get the existing permission
        Permission perm = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, permissionName);

        if ( perm == null ) {
            if ( active ) {
                Permission.Builder newPerm = Permission.Builder.create(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, permissionName);
                newPerm.setTemplate( Template.Builder.create(permissionTemplate) );
                newPerm.setDescription(permissionDescription );
                newPerm.setAttributes(permissionDetails);
                newPerm.setActive(true);
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "About to save new permission: " + newPerm);
                }
                perm = KimApiServiceLocator.getPermissionService().createPermission(newPerm.build());
            }
        } else {
            if ( perm.isActive() != active ) {
                Permission.Builder updatedPerm = Permission.Builder.create(perm);
                updatedPerm.setActive(active);
                perm = KimApiServiceLocator.getPermissionService().updatePermission(updatedPerm.build());
            }
        }

        assignPermissionToRole(perm, roleId);
    }

    protected void assignPermissionToRole( Permission perm, String roleId ) {
        if ( perm != null ) {
            if ( perm.isActive() ) {
                KimApiServiceLocator.getRoleService().assignPermissionToRole(perm.getId(), roleId );
            } else {
                KimApiServiceLocator.getRoleService().revokePermissionFromRole(perm.getId(), roleId );
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
