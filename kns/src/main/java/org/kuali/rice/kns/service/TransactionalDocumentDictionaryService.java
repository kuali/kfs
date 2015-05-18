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

import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

import java.util.Collection;

/**
 * This interface defines methods that a TransactionalDocumentDictionary Service must provide. Defines the API for the interacting
 * with TransactionalDocument-related entries in the data dictionary.
 */
@Deprecated
public interface TransactionalDocumentDictionaryService {
    /**
     * Returns whether or not this document's data dictionary file has flagged it to allow document copies.
     * 
     * @param document
     * @return True if copies are allowed, false otherwise.
     */
    public Boolean getAllowsCopy(TransactionalDocument document);

    /**
     * Retrieves a document instance by it's class name.
     * 
     * @param documentTypeName
     * @return A document instance.
     */
    public Class getDocumentClassByName(String documentTypeName);

    /**
     * Retrieves the full description of the transactional document as described in its data dictionary entry.
     * 
     * @param transactionalDocumentTypeName
     * @return The transactional document's full description.
     */
    public String getDescription(String transactionalDocumentTypeName);

    /**
     * Retrieves the label for the transactional document as described in its data dictionary entry.
     * 
     * @param transactionalDocumentTypeName
     * @return The transactional document's label.
     */
    public String getLabel(String transactionalDocumentTypeName);


    /**
     * The collection of ReferenceDefinition objects defined as DefaultExistenceChecks for the MaintenanceDocument.
     * 
     * @param document
     * @return A Collection of ReferenceDefinitions
     */
    public Collection getDefaultExistenceChecks(TransactionalDocument document);

    /**
     * The collection of ReferenceDefinition objects defined as DefaultExistenceChecks for the MaintenanceDocument.
     * 
     * @param docTypeName
     * @return A Collection of ReferenceDefinitions
     */
    public Collection getDefaultExistenceChecks(String docTypeName);
}
