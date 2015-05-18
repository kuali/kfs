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
package org.kuali.rice.kns.service;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DataObjectAuthorizationService;

/**
 * Responsible for using AttributeSecurity on
 * AttributeDefinitions, InquirableField the data dictionary business object and
 * maintenance document entries
 * 
 * TODO: refactor for general objects
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface BusinessObjectAuthorizationService extends DataObjectAuthorizationService {
	public BusinessObjectRestrictions getLookupResultRestrictions(
			Object dataObject, Person user);

	public InquiryRestrictions getInquiryRestrictions(
			BusinessObject businessObject, Person user);

	public MaintenanceDocumentRestrictions getMaintenanceDocumentRestrictions(
			MaintenanceDocument maintenanceDocument, Person user);

	public boolean canFullyUnmaskField(Person user,
			Class<?> dataObjectClass, String fieldName, Document document);

	public boolean canPartiallyUnmaskField(
			Person user, Class<?> businessObjectClass, String fieldName, Document document);

}
