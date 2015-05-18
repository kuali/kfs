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

import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BusinessObjectRestrictionsBase implements
		BusinessObjectRestrictions {
	private Map<String, MaskFormatter> partiallyMaskedFields;
	private Map<String, MaskFormatter> fullyMaskedFields;

	protected Set<String> allRestrictedFields;

	public BusinessObjectRestrictionsBase() {
		clearAllRestrictions();
	}

	public boolean hasAnyFieldRestrictions() {
		return !partiallyMaskedFields.isEmpty() || !fullyMaskedFields.isEmpty();
	}

	public boolean hasRestriction(String fieldName) {
		return isPartiallyMaskedField(fieldName) || isFullyMaskedField(fieldName);
	}

	public void addFullyMaskedField(String fieldName,
			MaskFormatter maskFormatter) {
		fullyMaskedFields.put(fieldName, maskFormatter);
	}

	public void addPartiallyMaskedField(String fieldName,
			MaskFormatter maskFormatter) {
		partiallyMaskedFields.put(fieldName, maskFormatter);
	}

	/**
	 * 
	 * This method returns the authorization setting for the given field name.
	 * If the field name is not restricted in any way, a default full-editable
	 * value is returned.
	 * 
	 * @param fieldName
	 *            - name of field to get authorization restrictions for.
	 * @return a populated FieldAuthorization class for this field
	 * 
	 */
	public FieldRestriction getFieldRestriction(String fieldName) {
		if (hasRestriction(fieldName)) {
			FieldRestriction fieldRestriction = null;
			if (isPartiallyMaskedField(fieldName)) {
				fieldRestriction = new FieldRestriction(fieldName,
						Field.PARTIALLY_MASKED);
				fieldRestriction.setMaskFormatter(partiallyMaskedFields
						.get(normalizeFieldName(fieldName)));
			}
			if (isFullyMaskedField(fieldName)) {
				fieldRestriction = new FieldRestriction(fieldName, Field.MASKED);
				fieldRestriction.setMaskFormatter(fullyMaskedFields
						.get(normalizeFieldName(fieldName)));
			}
			return fieldRestriction;
		} else {
			return new FieldRestriction(fieldName, Field.EDITABLE);
		}
	}

	public void clearAllRestrictions() {
		partiallyMaskedFields = new HashMap<String, MaskFormatter>();
		fullyMaskedFields = new HashMap<String, MaskFormatter>();
		allRestrictedFields = null;
	}
	
	
	/**
	 * This method is used to convert field names on forms into a format that's compatible with field names
	 * that are registered with a restriction.  The base implementation of this method just returns the string.
	 * 
	 * @param fieldName The field name that would be rendered on a form
	 * @return
	 */
	protected String normalizeFieldName(String fieldName) {
		return fieldName;
	}
	
	protected boolean isFullyMaskedField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return fullyMaskedFields.containsKey(normalizedFieldName);
	}
	
	protected boolean isPartiallyMaskedField(String fieldName) {
		String normalizedFieldName = normalizeFieldName(fieldName);
		return partiallyMaskedFields.containsKey(normalizedFieldName);
	}
}
