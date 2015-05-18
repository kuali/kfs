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

import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.web.ui.Field;

import java.util.HashSet;
import java.util.Set;

public class InquiryOrMaintenanceDocumentRestrictionsBase extends
		BusinessObjectRestrictionsBase implements InquiryOrMaintenanceDocumentRestrictions, InquiryRestrictions {
	private Set<String> hiddenFields;
	private Set<String> hiddenSectionIds;

	public void addHiddenField(String fieldName) {
		hiddenFields.add(fieldName);
	}

	public void addHiddenSectionId(String sectionId) {
		hiddenSectionIds.add(sectionId);
	}

	@Override
	public FieldRestriction getFieldRestriction(String fieldName) {
		FieldRestriction fieldRestriction = super
				.getFieldRestriction(fieldName);
		if (isHiddenField(fieldName)) {
			fieldRestriction = new FieldRestriction(fieldName, Field.HIDDEN);
		}
		return fieldRestriction;			
	}

	/**
	 * @see org.kuali.rice.krad.authorization.BusinessObjectRestrictionsBase#hasRestriction(String)
	 */
	@Override
	public boolean hasRestriction(String fieldName) {
		return super.hasRestriction(fieldName) || isHiddenField(fieldName);
	}
	
	/**
	 * @see org.kuali.rice.krad.authorization.BusinessObjectRestrictionsBase#hasAnyFieldRestrictions()
	 */
	@Override
	public boolean hasAnyFieldRestrictions() {
		return super.hasAnyFieldRestrictions() || !hiddenFields.isEmpty();
	}

	@Override
	public void clearAllRestrictions() {
		super.clearAllRestrictions();
		hiddenFields = new HashSet<String>();
		hiddenSectionIds = new HashSet<String>();
	}

	/**
	 * @see org.kuali.rice.krad.authorization.InquiryOrMaintenanceDocumentRestrictions#isHiddenSectionId(String)
	 */
	public boolean isHiddenSectionId(String sectionId) {
		return hiddenSectionIds.contains(sectionId);
	}

	protected boolean isHiddenField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return hiddenFields.contains(normalizedFieldName);
	}
}
