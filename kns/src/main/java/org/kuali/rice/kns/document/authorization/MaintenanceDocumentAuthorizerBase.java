/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.document.authorization;


import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MaintenanceDocumentAuthorizerBase extends DocumentAuthorizerBase implements MaintenanceDocumentAuthorizer {
	// private static final org.apache.log4j.Logger LOG =
	// org.apache.log4j.Logger.getLogger(MaintenanceDocumentAuthorizerBase.class);

	transient protected static DocumentDictionaryService documentDictionaryService;

	public boolean canCreate(Class boClass, Person user) {
		Map<String, String> permissionDetails = new HashMap<String, String>();
		permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
				getDocumentDictionaryService().getMaintenanceDocumentTypeName(
						boClass));
		permissionDetails.put(KRADConstants.MAINTENANCE_ACTN,
				KRADConstants.MAINTENANCE_NEW_ACTION);
		return !permissionExistsByTemplate(KRADConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
				permissionDetails)
				|| getPermissionService()
						.isAuthorizedByTemplate(user.getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                                KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS, permissionDetails,
                                new HashMap<String, String>());
	}

	public boolean canMaintain(Object dataObject, Person user) {
		Map<String, String> permissionDetails = new HashMap<String, String>(2);
		permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
				getDocumentDictionaryService().getMaintenanceDocumentTypeName(
						dataObject.getClass()));
		permissionDetails.put(KRADConstants.MAINTENANCE_ACTN,
				KRADConstants.MAINTENANCE_EDIT_ACTION);
		return !permissionExistsByTemplate(KRADConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
				permissionDetails)
				|| isAuthorizedByTemplate(
						dataObject,
						KRADConstants.KNS_NAMESPACE,
						KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
						user.getPrincipalId(), permissionDetails, null);
	}

	public boolean canCreateOrMaintain(
			MaintenanceDocument maintenanceDocument, Person user) {
		return !permissionExistsByTemplate(maintenanceDocument,
				KRADConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS)
				|| isAuthorizedByTemplate(
						maintenanceDocument,
						KRADConstants.KNS_NAMESPACE,
						KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
						user.getPrincipalId());
	}

	public Set<String> getSecurePotentiallyHiddenSectionIds() {
		return new HashSet<String>();
	}

	public Set<String> getSecurePotentiallyReadOnlySectionIds() {
		return new HashSet<String>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
		super.addRoleQualification(dataObject, attributes);
		if (dataObject instanceof MaintenanceDocument) {
			MaintenanceDocument maintDoc = (MaintenanceDocument)dataObject;
			if ( maintDoc.getNewMaintainableObject() != null ) {			
				attributes.putAll(
                        KRADUtils.getNamespaceAndComponentSimpleName(maintDoc.getNewMaintainableObject().getDataObjectClass()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addPermissionDetails(Object dataObject, Map<String, String> attributes) {
		super.addPermissionDetails(dataObject, attributes);
		if (dataObject instanceof MaintenanceDocument) {
			MaintenanceDocument maintDoc = (MaintenanceDocument)dataObject;
			if ( maintDoc.getNewMaintainableObject() != null ) {			
				attributes.putAll(
                        KRADUtils.getNamespaceAndComponentSimpleName(maintDoc.getNewMaintainableObject().getDataObjectClass()));
				attributes.put(KRADConstants.MAINTENANCE_ACTN,maintDoc.getNewMaintainableObject().getMaintenanceAction());
			}
		}
	}

    protected static DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

}
