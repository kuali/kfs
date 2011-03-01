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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityDefinitionDocumentType;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.PermissionUpdateService;
import org.kuali.rice.kim.service.impl.RoleManagementServiceImpl;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Maintainable implementation for the Security Definition maintenance document. Hooks into Post processing to create the KIM permissions from the definition records
 */
public class SecurityDefinitionMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityDefinitionMaintainableImpl.class);

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        this.getBusinessObject().refreshNonUpdateableReferences();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        if (documentHeader.getWorkflowDocument().stateIsProcessed()) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            try {
                MaintenanceDocument document = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentHeader.getDocumentNumber());
                SecurityDefinition oldSecurityDefinition = (SecurityDefinition) document.getOldMaintainableObject().getBusinessObject();
                SecurityDefinition newSecurityDefinition = (SecurityDefinition) document.getNewMaintainableObject().getBusinessObject();

                oldSecurityDefinition.refreshNonUpdateableReferences();
                newSecurityDefinition.refreshNonUpdateableReferences();

                boolean newMaintenanceAction = getMaintenanceAction().equalsIgnoreCase(KNSConstants.MAINTENANCE_NEW_ACTION) || getMaintenanceAction().equalsIgnoreCase(KNSConstants.MAINTENANCE_COPY_ACTION);

                createOrUpdateDocumentPermissions(oldSecurityDefinition, newSecurityDefinition, newMaintenanceAction);
                createOrUpdateLookupPermission(oldSecurityDefinition, newSecurityDefinition, newMaintenanceAction);
                createOrUpdateInquiryPermissions(oldSecurityDefinition, newSecurityDefinition, newMaintenanceAction);

                createOrUpdateDefinitionRole(oldSecurityDefinition, newSecurityDefinition);
                
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
    protected void createOrUpdateDefinitionRole(SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition) {
        RoleManagementServiceImpl roleService = SpringContext.getBean(RoleManagementServiceImpl.class);
        PermissionService permissionService = SpringContext.getBean(PermissionService.class);
        
        String roleId = oldSecurityDefinition.getRoleId();
        String roleName = newSecurityDefinition.getName();
        if (StringUtils.isBlank(roleId)) {
            // create new role for definition
            roleId = roleService.getNextAvailableRoleId();
            newSecurityDefinition.setRoleId(roleId);
            roleService.saveRole(roleId, roleName, newSecurityDefinition.getDescription(), newSecurityDefinition.isActive(), SecConstants.SecurityTypes.SECURITY_DEFINITION_ROLE_TYPE, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE);
        }
        else {
            // update role active indicator if it has been updated on the definition
            if ((oldSecurityDefinition.isActive() && !newSecurityDefinition.isActive()) || (!oldSecurityDefinition.isActive() && newSecurityDefinition.isActive())) {
                roleService.saveRole(roleId, roleName, newSecurityDefinition.getDescription(), newSecurityDefinition.isActive(), SecConstants.SecurityTypes.SECURITY_DEFINITION_ROLE_TYPE, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE);
            }
        }

        // assign all permissions for definition to role (have same name as role)
        List<KimPermissionInfo> permissions = permissionService.getPermissionsByName(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, roleName);
        for (KimPermissionInfo kimPermissionInfo : permissions) {
            List<String> permissionRoleIds = permissionService.getRoleIdsForPermissionId(kimPermissionInfo.getPermissionId());
            if (!permissionRoleIds.contains(roleId)) {
                roleService.assignPermissionToRole(kimPermissionInfo.getPermissionId(), roleId);
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
    protected void createOrUpdateDocumentPermissions(SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition, boolean newMaintenanceAction) {
        for (SecurityDefinitionDocumentType definitionDocumentType : newSecurityDefinition.getDefinitionDocumentTypes()) {
            String documentType = definitionDocumentType.getFinancialSystemDocumentTypeCode();
            boolean documentTypePermissionActive = newSecurityDefinition.isActive() && definitionDocumentType.isActive();
            boolean isNewDocumentType = newMaintenanceAction || !isDocumentTypeInDefinition(documentType, oldSecurityDefinition);

            if (isNewDocumentType) {
                createNewDocumentTypePermissions(documentType, documentTypePermissionActive, newSecurityDefinition);
            }
            else {
                createOrUpdateDocumentTypePermissions(documentType, documentTypePermissionActive, oldSecurityDefinition, newSecurityDefinition);
            }
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
        AttributeSet permissionDetails = populateLookupPermissionDetails(newSecurityDefinition);

        String permissionId = "";
        if (!newMaintenanceAction) {
            // find old Lookup permission
            List<KimPermissionInfo> permissions = findSecurityPermissionsByNameAndTemplate(oldSecurityDefinition.getName(), SecConstants.SecurityTemplateIds.LOOKUP_FIELD_VALUE);
            if (permissions != null && !permissions.isEmpty()) {
                KimPermissionInfo oldPermission = permissions.get(0);
                permissionId = oldPermission.getPermissionId();
            }
        }

        // need to save lookup permission if new side indicator is true or already has a permission in which case we need to update details and active indicator
        if (newSecurityDefinition.isRestrictLookup() || StringUtils.isNotBlank(permissionId)) {
            savePermission(newSecurityDefinition, permissionId, SecConstants.SecurityTemplateIds.LOOKUP_FIELD_VALUE, newSecurityDefinition.isActive() && newSecurityDefinition.isRestrictLookup(), permissionDetails);
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
    protected void createOrUpdateInquiryPermissions(SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition, boolean newMaintenanceAction) {
        String glPermissionId = "";
        String ldPermissionId = "";
        if (!newMaintenanceAction) {
            // find old inquiry permissions
            List<KimPermissionInfo> permissions = findSecurityPermissionsByNameAndTemplate(oldSecurityDefinition.getName(), SecConstants.SecurityTemplateIds.INQUIRY_FIELD_VALUE);
            if (permissions != null) {
                for (KimPermissionInfo permissionInfo : permissions) {
                    String namespaceCode = permissionInfo.getDetails().get(SecKimAttributes.NAMESPACE_CODE);
                    if (StringUtils.equals(KFSConstants.ParameterNamespaces.GL, namespaceCode)) {
                        glPermissionId = permissionInfo.getPermissionId();
                    }
                    else if (StringUtils.equals(SecConstants.LABOR_MODULE_NAMESPACE_CODE, namespaceCode)) {
                        ldPermissionId = permissionInfo.getPermissionId();
                    }
                }
            }
        }

        // need to save gl inquiry permission if new side indicator is true or already has a permission in which case we need to update details and active indicator
        if (newSecurityDefinition.isRestrictGLInquiry() || StringUtils.isNotBlank(glPermissionId)) {
            AttributeSet permissionDetails = populateInquiryPermissionDetails(KFSConstants.ParameterNamespaces.GL, newSecurityDefinition);
            savePermission(newSecurityDefinition, glPermissionId, SecConstants.SecurityTemplateIds.INQUIRY_FIELD_VALUE, newSecurityDefinition.isActive() && newSecurityDefinition.isRestrictGLInquiry(), permissionDetails);
        }

        // need to save ld inquiry permission if new side indicator is true or already has a permission in which case we need to update details and active indicator
        if (newSecurityDefinition.isRestrictLaborInquiry() || StringUtils.isNotBlank(ldPermissionId)) {
            AttributeSet permissionDetails = populateInquiryPermissionDetails(SecConstants.LABOR_MODULE_NAMESPACE_CODE, newSecurityDefinition);
            savePermission(newSecurityDefinition, ldPermissionId, SecConstants.SecurityTemplateIds.INQUIRY_FIELD_VALUE, newSecurityDefinition.isActive() && newSecurityDefinition.isRestrictLaborInquiry(), permissionDetails);
        }
    }

    /**
     * Checks the document restrict flags on the security definition and if true calls helper method to create a new permission
     * 
     * @param documentType workflow document type name for permission detail
     * @param active boolean indicating whether the permissions should be set to active (true) or non-active (false)
     * @param newSecurityDefinition SecurityDefintion which contains values for the permissions
     */
    protected void createNewDocumentTypePermissions(String documentType, boolean active, SecurityDefinition newSecurityDefinition) {
        AttributeSet permissionDetails = populateDocumentTypePermissionDetails(documentType, newSecurityDefinition);

        if (newSecurityDefinition.isRestrictViewDocument()) {
            savePermission(newSecurityDefinition, "", SecConstants.SecurityTemplateIds.VIEW_DOCUMENT_FIELD_VALUE, active, permissionDetails);
        }

        if (newSecurityDefinition.isRestrictViewAccountingLine()) {
            savePermission(newSecurityDefinition, "", SecConstants.SecurityTemplateIds.VIEW_ACCOUNTING_LINE_FIELD_VALUE, active, permissionDetails);
        }

        if (newSecurityDefinition.isRestrictViewNotesAndAttachments()) {
            savePermission(newSecurityDefinition, "", SecConstants.SecurityTemplateIds.VIEW_NOTES_ATTACHMENTS_FIELD_VALUE, active, permissionDetails);
        }

        if (newSecurityDefinition.isRestrictEditAccountingLine()) {
            savePermission(newSecurityDefinition, "", SecConstants.SecurityTemplateIds.EDIT_ACCOUNTING_LINE_FIELD_VALUE, active, permissionDetails);
        }

        if (newSecurityDefinition.isRestrictEditDocument()) {
            savePermission(newSecurityDefinition, "", SecConstants.SecurityTemplateIds.EDIT_DOCUMENT_FIELD_VALUE, active, permissionDetails);
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
        // view document
        createOrUpdateDocumentTypePermission(documentType, active && newSecurityDefinition.isRestrictViewDocument(), oldSecurityDefinition, newSecurityDefinition, SecConstants.SecurityTemplateIds.VIEW_DOCUMENT_FIELD_VALUE);

        // view accounting line
        createOrUpdateDocumentTypePermission(documentType, active && newSecurityDefinition.isRestrictViewAccountingLine(), oldSecurityDefinition, newSecurityDefinition, SecConstants.SecurityTemplateIds.VIEW_ACCOUNTING_LINE_FIELD_VALUE);

        // view notes/attachments
        createOrUpdateDocumentTypePermission(documentType, active && newSecurityDefinition.isRestrictViewNotesAndAttachments(), oldSecurityDefinition, newSecurityDefinition, SecConstants.SecurityTemplateIds.VIEW_NOTES_ATTACHMENTS_FIELD_VALUE);

        // edit accounting line
        createOrUpdateDocumentTypePermission(documentType, active && newSecurityDefinition.isRestrictEditAccountingLine(), oldSecurityDefinition, newSecurityDefinition, SecConstants.SecurityTemplateIds.EDIT_ACCOUNTING_LINE_FIELD_VALUE);

        // edit document
        createOrUpdateDocumentTypePermission(documentType, active && newSecurityDefinition.isRestrictEditDocument(), oldSecurityDefinition, newSecurityDefinition, SecConstants.SecurityTemplateIds.EDIT_DOCUMENT_FIELD_VALUE);
    }

    /**
     * First tries to find an existing permission for the document type, template, and definition. If found the permission will be updated with the new details and the active
     * indicator will be updated based on the active parameter. If not found and active parameter is true, then a new permission is created for the given doc type, template, and
     * definition.
     * 
     * @param documentType workflow document type name for permission detail
     * @param active boolean indicating whether the permissions should be set to active (true) or non-active (false)
     * @param oldSecurityDefinition SecurityDefiniton record before requested changes (old side of maintenance document)
     * @param newSecurityDefinition SecurityDefinition record with requested changes (new side of maintenance document)
     * @param templateId KIM template id for the permission record that is should be created or updated
     */
    protected void createOrUpdateDocumentTypePermission(String documentType, boolean active, SecurityDefinition oldSecurityDefinition, SecurityDefinition newSecurityDefinition, String templateId) {
        AttributeSet permissionDetails = populateDocumentTypePermissionDetails(documentType, newSecurityDefinition);

        KimPermissionInfo oldPermission = findDocumentPermission(oldSecurityDefinition, templateId, documentType);
        String permissionId = "";
        if (oldPermission != null) {
            permissionId = oldPermission.getPermissionId();
        }

        savePermission(newSecurityDefinition, permissionId, templateId, active, permissionDetails);
    }

    /**
     * Builds an AttributeSet populated from the given method parameters. Details are set based on the KIM 'Security Document Permission' type.
     * 
     * @param documentType workflow document type name
     * @param securityDefinition SecurityDefiniton record
     * @return AttributeSet populated with document type name, property name, operator, and property value details
     */
    protected AttributeSet populateDocumentTypePermissionDetails(String documentType, SecurityDefinition securityDefinition) {
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(SecKimAttributes.DOCUMENT_TYPE_NAME, documentType);
        permissionDetails.put(SecKimAttributes.PROPERTY_NAME, securityDefinition.getSecurityAttribute().getName());

        return permissionDetails;
    }

    /**
     * Builds an AttributeSet populated from the given method parameters. Details are set based on the KIM 'Security Lookup Permission' type.
     * 
     * @param securityDefinition SecurityDefiniton record
     * @return AttributeSet populated with property name, operator, and property value details
     */
    protected AttributeSet populateLookupPermissionDetails(SecurityDefinition securityDefinition) {
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(SecKimAttributes.PROPERTY_NAME, securityDefinition.getSecurityAttribute().getName());

        return permissionDetails;
    }

    /**
     * Builds an AttributeSet populated from the given method parameters. Details are set based on the KIM 'Security Inquiry Permission' type.
     * 
     * @param namespaceCode KIM namespace code
     * @param securityDefinition SecurityDefiniton record
     * @return AttributeSet populated with namespace, property name, operator, and property value details
     */
    protected AttributeSet populateInquiryPermissionDetails(String namespaceCode, SecurityDefinition securityDefinition) {
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(SecKimAttributes.NAMESPACE_CODE, namespaceCode);
        permissionDetails.put(SecKimAttributes.PROPERTY_NAME, securityDefinition.getSecurityAttribute().getName());

        return permissionDetails;
    }

    /**
     * Calls helper method to find all permissions for the given template ID and security defintion name (permission name). Iterates through the results to find the permission with
     * matching document type detail
     * 
     * @param securityDefinition SecurityDefiniton record for permission
     * @param templateId KIM template ID for permission
     * @param documentType KEW document type name for permission detail
     * @return KimPermissionInfo provides information on the matching permission
     */
    protected KimPermissionInfo findDocumentPermission(SecurityDefinition securityDefinition, String templateId, String documentType) {
        // get all the permissions for the definition record and template
        List<KimPermissionInfo> permissions = findSecurityPermissionsByNameAndTemplate(securityDefinition.getName(), templateId);

        // iterate through permission list finding permissions that have the document type detail
        KimPermissionInfo foundPermission = null;
        for (KimPermissionInfo permissionInfo : permissions) {
            String permissionDocType = permissionInfo.getDetails().get(SecKimAttributes.DOCUMENT_TYPE_NAME);
            if (StringUtils.equalsIgnoreCase(documentType, permissionDocType)) {
                foundPermission = permissionInfo;
                break;
            }
        }

        return foundPermission;
    }

    /**
     * Calls permission service to find all permissions for the given name. Iterates through results and finds ones that match given template ID as well
     * 
     * @param permissionName name of permission to find
     * @param templateId KIM template ID of permission to find
     * @return List<KimPermissionInfo> List of matching permissions
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionsByName()
     */
    protected List<KimPermissionInfo> findSecurityPermissionsByNameAndTemplate(String permissionName, String templateId) {
        PermissionService permissionService = SpringContext.getBean(PermissionService.class);

        // get all the permissions for the given name
        List<KimPermissionInfo> permissions = permissionService.getPermissionsByNameIncludingInactive(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, permissionName);

        List<KimPermissionInfo> templatePermissions = new ArrayList<KimPermissionInfo>();
        for (KimPermissionInfo permissionInfo : permissions) {
            if (StringUtils.equals(templateId, permissionInfo.getTemplateId())) {
                templatePermissions.add(permissionInfo);
            }
        }

        return templatePermissions;
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
            if (StringUtils.equalsIgnoreCase(documentType, oldDocumentType)) {
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
     * @param permissionDetails AttributeSet representing the permission details
     * @see org.kuali.rice.kim.service.PermissionUpdateService#savePermission()
     */
    protected void savePermission(SecurityDefinition securityDefinition, String permissionId, String permissionTemplateId, boolean active, AttributeSet permissionDetails) {
        LOG.info(String.format("saving permission with id: %s, template ID: %s, name: %s, active: %s", permissionId, permissionTemplateId, securityDefinition.getName(), active));

        PermissionUpdateService permissionUpdateService = SpringContext.getBean(PermissionUpdateService.class);

        if (StringUtils.isBlank(permissionId)) {
            permissionId = permissionUpdateService.getNextAvailablePermissionId();
        }

        permissionUpdateService.savePermission(permissionId, permissionTemplateId, SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, securityDefinition.getName(), securityDefinition.getDescription(), active, permissionDetails);
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

}
