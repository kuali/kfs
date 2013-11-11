/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;

/**
 * This authorizer needs to override the canCreate and canMaintain methods...which are final on the base MaintDocAuthorizer class that every other authorizer simply extends.  Therefore,
 * this uses an "assistant" authorizer to defer most of its calls to (rootDocumentAuthorizer) and overrides the logic for canCreate and canMaintain to perform the very special logic for TemProfiles.
 * It's acknowledged that this whole thing is going to be harder to customize than the regular authorizer.
 */
public class TemProfileAuthorizer implements MaintenanceDocumentAuthorizer, DocumentAuthorizer {
    protected TemProfileAuthorizerAssistant rootDocumentAuthorizer;

    /**
     * Not overridden; we'll just rely on the Create / Maintain Document check for this one
     * @see org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizer#canCreate(java.lang.Class, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canCreate(Class boClass, Person user) {
        return getRootDocumentAuthorizer().canCreate(boClass, user);
    }

    /**
     * Overridden to verify that the user has KFS-TEM Edit Own Tem Profile permission before maintaining their own record, and that the user
     * @see org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizer#canMaintain(java.lang.Object, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canMaintain(Object dataObject, Person user) {
        boolean result = getRootDocumentAuthorizer().canMaintain(dataObject, user);

        TemProfile profile = null;
        if (dataObject instanceof TemProfile) {
            profile = (TemProfile)dataObject;
        } else if (dataObject instanceof MaintenanceDocument) {
            profile = (TemProfile)((MaintenanceDocument)dataObject).getNewMaintainableObject().getBusinessObject();
        }
        result &= ((doesProfileMatchUser(profile, user) && canEditOwnProfile((BusinessObject)dataObject, user)) || canEditAllProfiles((BusinessObject)dataObject, user));

        return result;
    }

    /**
     * Overridden to check that the user has the normal Create / Maintain permission and also, if it's their own profile, the ability to edit their own profile; if it's not their own profile,
     * it checks if they can edit anyone's profile
     * @see org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizer#canCreateOrMaintain(org.kuali.rice.krad.maintenance.MaintenanceDocument, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canCreateOrMaintain(org.kuali.rice.krad.maintenance.MaintenanceDocument maintenanceDocument, Person user) {
        boolean result = getRootDocumentAuthorizer().canCreateOrMaintain(maintenanceDocument, user);

        final TemProfile profile = (TemProfile)maintenanceDocument.getNewMaintainableObject().getDataObject();
        result &= ((doesProfileMatchUser(profile, user) && canEditOwnProfile(maintenanceDocument, user)) || canEditAllProfiles(maintenanceDocument, user));

        return result;
    }

    /**
     * Determines if the given profile matches the given user
     * @param profile the profile to check
     * @return true if the profile is for the current user, false otherwise
     */
    protected boolean doesProfileMatchUser(TemProfile profile, Person user) {
        if (profile != null && !StringUtils.isBlank(profile.getPrincipalId())) {
            final String userPrincipalId = (user == null) ? null : user.getPrincipalId();
            return StringUtils.equals(profile.getPrincipalId(), userPrincipalId);
        }
        return false; // no principal id?  then they couldn't log in and therefore they can't be the current user
    }

    /**
     * Determines if the given user is allowed to create or maintain their own profile
     * @param profileOrDoc the maintenance document maintaining the profile to check, or the profile itself
     * @param user the user asking for permission
     * @return true if the user has permission, false otherwise
     */
    public boolean canEditOwnProfile(BusinessObject profileOrDoc, Person user) {
        Map<String, String> roleQualifications = new HashMap<String, String>();
        if (profileOrDoc != null) {
            getRootDocumentAuthorizer().addRoleQualification(profileOrDoc, roleQualifications);
        }

        return this.isAuthorized(profileOrDoc, TemConstants.NAMESPACE, TemConstants.Permission.EDIT_OWN_PROFILE, user.getPrincipalId(), Collections.<String,String>emptyMap(), roleQualifications);
    }

    /**
     * Determines if the given user is allowed to maintain a profile for any user or customer
     * @param profileOrDoc the maintenance document maintaining the profile to check, or the profile itself
     * @param user the user asking for permission
     * @return true if the user has permission, false otherwise
     */
    public boolean canEditAllProfiles(BusinessObject profileOrDoc, Person user) {
        Map<String, String> roleQualifications = new HashMap<String, String>();
        if (profileOrDoc != null) {
            getRootDocumentAuthorizer().addRoleQualification(profileOrDoc, roleQualifications);
        }

        return this.isAuthorized(profileOrDoc, TemConstants.NAMESPACE, TemConstants.Permission.EDIT_ANY_PROFILE, user.getPrincipalId(), Collections.<String,String>emptyMap(), roleQualifications);
    }

    /**
     * Determines if the given user is allowed to create a profile for any user or customer
     * @param profileOrDoc the maintenance document maintaining the profile to check, or the profile itself
     * @param user the user asking for permission
     * @return true if the user has permission, false otherwise
     */
    public boolean canCreateAnyProfile(BusinessObject profileOrDoc, Person user) {
        Map<String, String> roleQualifications = new HashMap<String, String>();
        if (profileOrDoc != null) {
            getRootDocumentAuthorizer().addRoleQualification(profileOrDoc, roleQualifications);
        }

        return this.isAuthorized(profileOrDoc, TemConstants.NAMESPACE, TemConstants.Permission.CREATE_ANY_PROFILE, user.getPrincipalId(), Collections.<String,String>emptyMap(), roleQualifications);
    }

    @Override
    public boolean canCopy(Document document, Person user) {
        return getRootDocumentAuthorizer().canCopy(document, user);
    }

    @Override
    public boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName, String principalId) {
        return getRootDocumentAuthorizer().isAuthorized(dataObject, namespaceCode, permissionName, principalId);
    }

    @Override
    public boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName, String principalId) {
        return getRootDocumentAuthorizer().isAuthorizedByTemplate(dataObject, namespaceCode, permissionTemplateName, principalId);
    }

    @Override
    public boolean isAuthorized(Object dataObject, String namespaceCode, String permissionName, String principalId, Map<String, String> additionalPermissionDetails, Map<String, String> additionalRoleQualifiers) {
        return getRootDocumentAuthorizer().isAuthorized(dataObject, namespaceCode, permissionName, principalId, additionalPermissionDetails, additionalRoleQualifiers);
    }

    @Override
    public Set<String> getSecurePotentiallyHiddenSectionIds() {
        return getRootDocumentAuthorizer().getSecurePotentiallyHiddenSectionIds();
    }

    @Override
    public boolean isAuthorized(BusinessObject businessObject, String namespaceCode, String permissionName, String principalId) {
        return getRootDocumentAuthorizer().isAuthorized(businessObject, namespaceCode, permissionName, principalId);
    }

    @Override
    public boolean isAuthorizedByTemplate(BusinessObject businessObject, String namespaceCode, String permissionTemplateName, String principalId) {
        return getRootDocumentAuthorizer().isAuthorizedByTemplate(businessObject, namespaceCode, permissionTemplateName, principalId);
    }

    @Override
    public boolean isAuthorized(BusinessObject businessObject, String namespaceCode, String permissionName, String principalId, Map<String, String> additionalPermissionDetails, Map<String, String> additionalRoleQualifiers) {
        return getRootDocumentAuthorizer().isAuthorized(businessObject, namespaceCode, permissionName, principalId, additionalPermissionDetails, additionalRoleQualifiers);
    }

    @Override
    public boolean isAuthorizedByTemplate(Object dataObject, String namespaceCode, String permissionTemplateName, String principalId, Map<String, String> additionalPermissionDetails, Map<String, String> additionalRoleQualifiers) {
        return getRootDocumentAuthorizer().isAuthorizedByTemplate(dataObject, namespaceCode, permissionTemplateName, principalId, additionalPermissionDetails, additionalRoleQualifiers);
    }

    @Override
    public Map<String, String> getCollectionItemRoleQualifications(BusinessObject collectionItemBusinessObject) {
        return getRootDocumentAuthorizer().getCollectionItemRoleQualifications(collectionItemBusinessObject);
    }

    @Override
    public Map<String, String> getCollectionItemPermissionDetails(BusinessObject collectionItemBusinessObject) {
        return getRootDocumentAuthorizer().getCollectionItemPermissionDetails(collectionItemBusinessObject);
    }

    @Override
    public boolean canInitiate(String documentTypeName, Person user) {
        return getRootDocumentAuthorizer().canInitiate(documentTypeName, user);
    }

    @Override
    public boolean canOpen(Document document, Person user) {
        return getRootDocumentAuthorizer().canOpen(document, user);
    }

    @Override
    public boolean canEdit(Document document, Person user) {
        return getRootDocumentAuthorizer().canEdit(document, user);
    }

    @Override
    public boolean canAnnotate(Document document, Person user) {
        return getRootDocumentAuthorizer().canAnnotate(document, user);
    }

    @Override
    public boolean canReload(Document document, Person user) {
        return getRootDocumentAuthorizer().canReload(document, user);
    }

    @Override
    public boolean canClose(Document document, Person user) {
        return getRootDocumentAuthorizer().canClose(document, user);
    }

    @Override
    public boolean canSave(Document document, Person user) {
        return getRootDocumentAuthorizer().canSave(document, user);
    }

    @Override
    public boolean canRoute(Document document, Person user) {
        return getRootDocumentAuthorizer().canRoute(document, user);
    }

    @Override
    public boolean canCancel(Document document, Person user) {
        return getRootDocumentAuthorizer().canCancel(document, user);
    }

    @Override
    public boolean canPerformRouteReport(Document document, Person user) {
        return getRootDocumentAuthorizer().canPerformRouteReport(document, user);
    }

    @Override
    public boolean canBlanketApprove(Document document, Person user) {
        return getRootDocumentAuthorizer().canBlanketApprove(document, user);
    }

    @Override
    public boolean canApprove(Document document, Person user) {
        return getRootDocumentAuthorizer().canApprove(document, user);
    }

    @Override
    public boolean canDisapprove(Document document, Person user) {
        return getRootDocumentAuthorizer().canDisapprove(document, user);
    }

    @Override
    public boolean canSendNoteFyi(Document document, Person user) {
        return getRootDocumentAuthorizer().canSendNoteFyi(document, user);
    }

    @Override
    public boolean canEditDocumentOverview(Document document, Person user) {
        return getRootDocumentAuthorizer().canEditDocumentOverview(document, user);
    }

    @Override
    public boolean canFyi(Document document, Person user) {
        return getRootDocumentAuthorizer().canFyi(document, user);
    }

    @Override
    public boolean canAcknowledge(Document document, Person user) {
        return getRootDocumentAuthorizer().canAcknowledge(document, user);
    }

    @Override
    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode) {
        return getRootDocumentAuthorizer().canReceiveAdHoc(document, user, actionRequestCode);
    }

    @Override
    public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        return getRootDocumentAuthorizer().canAddNoteAttachment(document, attachmentTypeCode, user);
    }

    @Override
    public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode, String authorUniversalIdentifier, Person user) {
        return getRootDocumentAuthorizer().canDeleteNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier, user);
    }

    @Override
    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, String authorUniversalIdentifier, Person user) {
        return getRootDocumentAuthorizer().canViewNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier, user);
    }

    @Override
    public boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user) {
        return getRootDocumentAuthorizer().canSendAdHocRequests(document, actionRequestCd, user);
    }

    @Override
    public boolean canSendAnyTypeAdHocRequests(Document document, Person user) {
        return getRootDocumentAuthorizer().canSendAnyTypeAdHocRequests(document, user);
    }

    @Override
    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user) {
        return getRootDocumentAuthorizer().canTakeRequestedAction(document, actionRequestCode, user);
    }

    @Override
    public boolean canRecall(Document document, Person user) {
        return getRootDocumentAuthorizer().canRecall(document, user);
    }

    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        return getRootDocumentAuthorizer().getSecurePotentiallyReadOnlySectionIds();
    }

    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActions) {
        return getRootDocumentAuthorizer().getDocumentActions(document, user, documentActions);
    }

    @Override
    public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        return getRootDocumentAuthorizer().canViewNoteAttachment(document, attachmentTypeCode, user);
    }

    /**
     * This is what actually uses the TemProfileAuthorizerAssistant to use as the logic we defer calls to
     * @return a document authorizer composed in, to defer to when we can
     */
    protected TemProfileAuthorizerAssistant getRootDocumentAuthorizer() {
        try {
            if (rootDocumentAuthorizer == null) {
                rootDocumentAuthorizer = TemProfileAuthorizerAssistant.class.newInstance();
            }
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not instantiate instance of "+FinancialSystemMaintenanceDocumentAuthorizerBase.class.getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Access issues while instantiating instance of "+FinancialSystemMaintenanceDocumentAuthorizerBase.class.getName(), iae);
        }
        return rootDocumentAuthorizer;
    }
}
