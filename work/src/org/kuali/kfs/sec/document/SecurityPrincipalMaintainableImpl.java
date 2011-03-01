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

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModelMember;
import org.kuali.kfs.sec.businessobject.SecurityPrincipal;
import org.kuali.kfs.sec.businessobject.SecurityPrincipalDefinition;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sec.util.KimUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Maintainable implementation for the Security Principal maintenance document. Hooks into Post processing to create the KIM permissions for the principal and assign security role
 * members
 */
public class SecurityPrincipalMaintainableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityPrincipalMaintainableImpl.class);

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
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
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        if (documentHeader.getWorkflowDocument().stateIsProcessed()) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            try {
                MaintenanceDocument document = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentHeader.getDocumentNumber());
                SecurityPrincipal oldSecurityPrincipal = (SecurityPrincipal) document.getOldMaintainableObject().getBusinessObject();
                SecurityPrincipal newSecurityPrincipal = (SecurityPrincipal) document.getNewMaintainableObject().getBusinessObject();

                boolean newMaintenanceAction = getMaintenanceAction().equalsIgnoreCase(KNSConstants.MAINTENANCE_NEW_ACTION) || getMaintenanceAction().equalsIgnoreCase(KNSConstants.MAINTENANCE_COPY_ACTION);

                assignOrUpdatePrincipalMembershipToDefinitionRoles(oldSecurityPrincipal, newSecurityPrincipal, newMaintenanceAction);
                assignOrUpdatePrincipalModelRoles(newSecurityPrincipal);
                
                SpringContext.getBean(IdentityManagementService.class).flushAllCaches();
            }
            catch (WorkflowException e) {
                LOG.error("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
                throw new RuntimeException("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
            }
        }
    }

    /**
     * Iterates through the principal definition list and assigns the principal to the definition role if necessary or updates the current member assignment
     * 
     * @param oldSecurityPrincipal SecurityPrincipal before updates
     * @param newSecurityPrincipal SecurityPrincipal which contains the definition list and principal
     * @param newMaintenanceAction boolean indicating whether this is a new record (old side will not contain data)
     */
    protected void assignOrUpdatePrincipalMembershipToDefinitionRoles(SecurityPrincipal oldSecurityPrincipal, SecurityPrincipal newSecurityPrincipal, boolean newMaintenanceAction) {
        RoleManagementService roleService = SpringContext.getBean(RoleManagementService.class);

        String principalId = newSecurityPrincipal.getPrincipalId();

        for (SecurityPrincipalDefinition securityPrincipalDefinition : newSecurityPrincipal.getPrincipalDefinitions()) {
            SecurityDefinition securityDefinition = securityPrincipalDefinition.getSecurityDefinition();

            KimRoleInfo definitionRoleInfo = roleService.getRole(securityDefinition.getRoleId());
            
            RoleMembershipInfo principalMembershipInfo = null;
            if (!newMaintenanceAction) {
                SecurityPrincipalDefinition oldPrincipalDefinition = null;
                for (SecurityPrincipalDefinition principalDefinition : oldSecurityPrincipal.getPrincipalDefinitions()) {
                   if ((principalDefinition.getPrincipalDefinitionId() != null) && principalDefinition.getPrincipalDefinitionId().equals(securityPrincipalDefinition.getPrincipalDefinitionId())) {
                       oldPrincipalDefinition = principalDefinition;
                   }
                }
                
                if (oldPrincipalDefinition != null) {
                    AttributeSet membershipQualifications = new AttributeSet();
                    membershipQualifications.put(SecKimAttributes.CONSTRAINT_CODE, oldPrincipalDefinition.getConstraintCode());
                    membershipQualifications.put(SecKimAttributes.OPERATOR, oldPrincipalDefinition.getOperatorCode());
                    membershipQualifications.put(SecKimAttributes.PROPERTY_VALUE, oldPrincipalDefinition.getAttributeValue());
                    membershipQualifications.put(SecKimAttributes.OVERRIDE_DENY, Boolean.toString(oldPrincipalDefinition.isOverrideDeny()));

                    principalMembershipInfo = KimUtil.getRoleMembershipInfoForMemberType(definitionRoleInfo.getRoleId(), principalId, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE, membershipQualifications);
                }
            }

            // only create membership if principal definition record is active
            boolean membershipActive = securityPrincipalDefinition.isActive();

            // if membership already exists, need to remove if the principal record is now inactive or the qualifications need updated
            String principalMembershipId = "";
            if (principalMembershipInfo != null) {
                principalMembershipId = principalMembershipInfo.getRoleMemberId();
                boolean qualificationsMatch = KimUtil.doMembershipQualificationsMatchValues(principalMembershipInfo.getQualifier(), securityPrincipalDefinition.getConstraintCode(), securityPrincipalDefinition.getOperatorCode(), securityPrincipalDefinition.getAttributeValue());
                if (!membershipActive || !qualificationsMatch) {
                    roleService.removeRoleFromRole(principalMembershipInfo.getMemberId(), definitionRoleInfo.getNamespaceCode(), definitionRoleInfo.getRoleName(), principalMembershipInfo.getQualifier());
                }
            }

            // create of update role if membership should be active
            if (membershipActive) {
                AttributeSet membershipQualifications = new AttributeSet();
                membershipQualifications.put(SecKimAttributes.CONSTRAINT_CODE, securityPrincipalDefinition.getConstraintCode());
                membershipQualifications.put(SecKimAttributes.OPERATOR, securityPrincipalDefinition.getOperatorCode());
                membershipQualifications.put(SecKimAttributes.PROPERTY_VALUE, securityPrincipalDefinition.getAttributeValue());
                membershipQualifications.put(SecKimAttributes.OVERRIDE_DENY, Boolean.toString(securityPrincipalDefinition.isOverrideDeny()));

                roleService.saveRoleMemberForRole(principalMembershipId, principalId, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE, definitionRoleInfo.getRoleId(), membershipQualifications, null, null);
            }
        }
    }

    /**
     * Iterates through the principal model list and assigns the principal to the model role or updates the membership
     * 
     * @param securityPrincipal SecurityPrincipal which contains the model list and principal
     */
    protected void assignOrUpdatePrincipalModelRoles(SecurityPrincipal securityPrincipal) {
        RoleManagementService roleService = SpringContext.getBean(RoleManagementService.class);

        String principalId = securityPrincipal.getPrincipalId();

        for (SecurityModelMember principalModel : securityPrincipal.getPrincipalModels()) {
            KimRoleInfo modelRoleInfo = roleService.getRole(principalModel.getSecurityModel().getRoleId());

            RoleMembershipInfo membershipInfo = KimUtil.getRoleMembershipInfoForMemberType(modelRoleInfo.getRoleId(), principalId, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE, null);

            String membershipId = "";
            if (membershipInfo != null) {
                membershipId = membershipInfo.getRoleMemberId();
            }

            java.sql.Date fromDate = null;
            java.sql.Date toDate = null;
            if ( principalModel.getActiveFromDate() != null ) {
                fromDate = new java.sql.Date( principalModel.getActiveFromDate().getTime() ); 
            }
            if ( principalModel.getActiveToDate() != null ) {
                toDate = new java.sql.Date( principalModel.getActiveToDate().getTime() ); 
            }
            roleService.saveRoleMemberForRole(membershipId, principalId, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE, modelRoleInfo.getRoleId(), new AttributeSet(), fromDate, toDate);
        }
    }

}
