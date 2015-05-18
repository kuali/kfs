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
package org.kuali.rice.kns.bo.authorization;

import org.kuali.rice.kns.authorization.BusinessObjectAuthorizer;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectAuthorizerBase;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.HashMap;
import java.util.Map;

public class BusinessObjectAuthorizerBase extends DataObjectAuthorizerBase implements BusinessObjectAuthorizer {
    private static final long serialVersionUID = -6315759348728853851L;

	private static KualiModuleService kualiModuleService;
	private static DataDictionaryService dataDictionaryService;
	private static PersistenceStructureService persistenceStructureService;

    protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName) {
		return getPermissionService()
				.isPermissionDefinedByTemplate(namespaceCode, permissionTemplateName, new HashMap<String, String>(
                        getPermissionDetailValues(businessObject)));
	}

	protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails) {
		Map<String, String> combinedPermissionDetails = new HashMap<String, String>(
				getPermissionDetailValues(businessObject));
		combinedPermissionDetails.putAll(permissionDetails);
		return getPermissionService()
				.isPermissionDefinedByTemplate(namespaceCode, permissionTemplateName, combinedPermissionDetails);
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId) {
		return getPermissionService().isAuthorized(principalId,
				namespaceCode, permissionName,
				new HashMap<String, String>(getRoleQualification(businessObject, principalId)));
	}

	public final boolean isAuthorizedByTemplate(BusinessObject dataObject,
			String namespaceCode, String permissionTemplateName,
			String principalId) {
		return getPermissionService().isAuthorizedByTemplate(principalId, namespaceCode, permissionTemplateName,
                new HashMap<String, String>(getPermissionDetailValues(dataObject)), new HashMap<String, String>(
                getRoleQualification(dataObject, principalId)));
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		Map<String, String> roleQualifiers = null;
		Map<String, String> permissionDetails = null;
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers = new HashMap<String, String>(
					getRoleQualification(businessObject, principalId));
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		} else {
			roleQualifiers = new HashMap<String, String>(
					getRoleQualification(businessObject, principalId));
		}
		/*if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails = new HashMap<String, String>(
					getPermissionDetailValues(businessObject));
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		} else {
			permissionDetails = new HashMap<String, String>(
					getPermissionDetailValues(businessObject));
		}*/
		
		return getPermissionService().isAuthorized(principalId,
				namespaceCode, permissionName,
				roleQualifiers);
	}


	/**
	 * Returns a role qualification map based off data from the primary business
	 * object or the document. DO NOT MODIFY THE MAP RETURNED BY THIS METHOD
	 * 
	 * @param primaryBusinessObjectOrDocument
	 *            the primary business object (i.e. the main BO instance behind
	 *            the lookup result row or inquiry) or the document
	 * @return a Map containing role qualifications
	 */
	protected final Map<String, String> getRoleQualification(
			BusinessObject primaryBusinessObjectOrDocument) {
		return getRoleQualification(primaryBusinessObjectOrDocument, GlobalVariables
					.getUserSession().getPerson().getPrincipalId());
	}

	/**
	 * @see BusinessObjectAuthorizer#getCollectionItemPermissionDetails(BusinessObject)
	 */
    @Override
	public Map<String, String> getCollectionItemPermissionDetails(
			BusinessObject collectionItemBusinessObject) {
		return new HashMap<String, String>();
	}

	/**
	 * @see BusinessObjectAuthorizer#getCollectionItemRoleQualifications(BusinessObject)
	 */
    @Override
	public Map<String, String> getCollectionItemRoleQualifications(
			BusinessObject collectionItemBusinessObject) {
		return new HashMap<String, String>();
	}

	protected static KualiModuleService getKualiModuleService() {
		if (kualiModuleService == null) {
			kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
		}
		return kualiModuleService;
	}

	protected static DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KRADServiceLocatorWeb
					.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
}
