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

import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface DictionaryValidationService extends org.kuali.rice.krad.service.DictionaryValidationService {

    /**
     * Validates the contents of a document (i.e. attributes within a document) against the data dictionary.
     * Recursively
     * checks
     * business objects of the document.
     *
     * @param document - document to validate
     * @param depth - Specify how deep the recrusion should go (0 based). If a negative number is supplied, it's
     * infinite.
     * @deprecated Use {@link #validateDocumentAndUpdatableReferencesRecursively(Document,
     *             int, boolean)}
     */
    @Deprecated
    public void validateDocumentRecursively(Document document, int depth);

    @Deprecated
    public void validateBusinessObjectOnMaintenanceDocument(BusinessObject businessObject, String docTypeName);

    /**
     * Validates the business object against the dictionary, uses reflection to get any child business objects, and
     * recursively
     * calls back. Adds errors to the map as they are encountered.
     *
     * @param businessObject - business object to validate
     * @param depth - Specify how deep the recrusion should go (0 based). If a negative number is supplied, it's
     * infinite.
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateBusinessObjectsRecursively(BusinessObject businessObject, int depth);

    /**
     * Validates an attribute of a given class for proper min, max length, syntax, and required.
     *
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param errorKey - key to place the errors under
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateAttributeFormat(String entryName, String attributeName, String attributeValue, String errorKey);

    /**
     * Validates an attribute of a given class for proper min, max length, syntax, and required. The attribute will be
     * validated
     * according to the specified data type.
     *
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param attributeDataType - data type that this attribute should be treated as for validation purposes
     * @param errorKey - key to place the errors under
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateAttributeFormat(String entryName, String attributeName, String attributeValue,
                                        String attributeDataType, String errorKey);

    /**
     * Validates an attribute of a given class for required check.
     *
     * @param entryName - name of the dd entry
     * @param attributeName - name of attribute in the bo class
     * @param attributeValue - current value to validate
     * @param errorKey - key to place to errors under
     * @deprecated since 1.1
     */
    @Deprecated
    public void validateAttributeRequired(String entryName, String attributeName, Object attributeValue,
                                          Boolean forMaintenance, String errorKey);
}
