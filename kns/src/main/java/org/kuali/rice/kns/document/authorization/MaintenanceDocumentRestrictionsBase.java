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

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kns.document.authorization.FieldRestriction;
import org.kuali.rice.kns.document.authorization.InquiryOrMaintenanceDocumentRestrictionsBase;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.web.ui.Field;

public class MaintenanceDocumentRestrictionsBase extends InquiryOrMaintenanceDocumentRestrictionsBase implements MaintenanceDocumentRestrictions {
	private Set<String> readOnlyFields;
	private Set<String> readOnlySectionIds;
	
	public MaintenanceDocumentRestrictionsBase() {
	}
	
	public void addReadOnlyField(String fieldName) {
		readOnlyFields.add(fieldName);
	}

	public void addReadOnlySectionId(String sectionId) {
		readOnlySectionIds.add(sectionId);
	}

	public Set<String> getReadOnlySectionIds() {
		return readOnlySectionIds;
	}

	@Override
	public FieldRestriction getFieldRestriction(String fieldName) {
		FieldRestriction fieldRestriction = super
				.getFieldRestriction(fieldName);
		if (fieldRestriction == null && isReadOnlyField(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName, Field.READONLY);
		}
		// TODO: next block could probably be removed since the superclass would return null for a read-only field 
		if (Field.EDITABLE
				.equals(fieldRestriction.getKualiFieldDisplayFlag())
				&& isReadOnlyField(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName,
					Field.READONLY);
		}
		return fieldRestriction;
	}

	@Override
	public void clearAllRestrictions() {
		super.clearAllRestrictions();
		readOnlyFields = new HashSet<String>();
		readOnlySectionIds = new HashSet<String>();
	}

	protected boolean isReadOnlyField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return readOnlyFields.contains(normalizedFieldName);
	}

	/**
	 * @see org.kuali.rice.krad.authorization.InquiryOrMaintenanceDocumentRestrictionsBase#hasAnyFieldRestrictions()
	 */
	@Override
	public boolean hasAnyFieldRestrictions() {
		return super.hasAnyFieldRestrictions() || !readOnlyFields.isEmpty();
	}

	/**
	 * @see org.kuali.rice.krad.authorization.InquiryOrMaintenanceDocumentRestrictionsBase#hasRestriction(String)
	 */
	@Override
	public boolean hasRestriction(String fieldName) {
		return super.hasRestriction(fieldName) || isReadOnlyField(fieldName);
	}
	
	/**
	 * @see MaintenanceDocumentRestrictions#isReadOnlySectionId(String)
	 */
	public boolean isReadOnlySectionId(String sectionId) {
		return readOnlySectionIds.contains(sectionId);
	}
}
