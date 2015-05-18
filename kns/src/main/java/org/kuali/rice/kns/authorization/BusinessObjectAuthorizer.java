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
package org.kuali.rice.kns.authorization;

import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Map;


public interface BusinessObjectAuthorizer {
	public boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId);

	public boolean isAuthorizedByTemplate(BusinessObject businessObject,
			String namespaceCode, String permissionTemplateName,
			String principalId);

	public boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId,
			Map<String, String> additionalPermissionDetails,
			Map<String, String> additionalRoleQualifiers);

	public boolean isAuthorizedByTemplate(Object dataObject,
			String namespaceCode, String permissionTemplateName,
			String principalId,
			Map<String, String> additionalPermissionDetails,
			Map<String, String> additionalRoleQualifiers);
	
	public Map<String,String> getCollectionItemRoleQualifications(BusinessObject collectionItemBusinessObject);
	
	public Map<String,String> getCollectionItemPermissionDetails(BusinessObject collectionItemBusinessObject);
}
