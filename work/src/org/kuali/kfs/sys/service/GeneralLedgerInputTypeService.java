/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.GeneralLedgerInputType;

/**
 * This class is used to retrieve financial information about certain documents by utilizing the document type name
 */
public interface GeneralLedgerInputTypeService {

    /**
     * Given a documentClass, returns the associated {@link GeneralLedgerInputType} object. Note that this will not always work
     * properly for maintenance documents, because the data dictionary maps most maintenance document types to the same
     * MaintenanceDocumentBase class (which is in most cases not really a base class because it has very few subclasses... mostly
     * only client specific subclasses).
     * 
     * @param documentClass
     * @return a valid General Ledger Input Type
     * @throws IllegalArgumentException if the given documentClass is null
     * @throws IllegalArgumentException if the given documentClass is not assignable to {@link org.kuali.rice.kns.document.Document}
     * @throws UnknownDocumentTypeException if the given documentClass doesn't have a data dictionary entry
     * @throws UnknownGeneralLedgerInputTypeException if the given document class is completely valid with no row in the
     *         {@link GeneralLedgerInputType} table
     */
    public GeneralLedgerInputType getGeneralLedgerInputTypeByDocumentClass(Class documentClass);

    /**
     * Given a documentTypeName, returns from the database the GeneralLedgerInputType which is associated with that documentTypeName
     * in the data dictionary via its documentTypeCode.
     * 
     * @param documentTypeName
     * @return a valid General Ledger Input Type
     * @throws IllegalArgumentException if the given documentTypeName is empty
     * @throws UnknownGeneralLedgerInputTypeException if the given document type name isn't mapped via the GeneralLedgerInputType
     *         table
     */
    public GeneralLedgerInputType getGeneralLedgerInputTypeByDocumentName(String documentTypeName);

    /**
     * Given an generalLedgerInputTypeCode, returns the associated GeneralLedgerInputType from the database.
     * 
     * @param generalLedgerInputTypeCode
     * @return a valid General Ledger Input Type
     * @throws IllegalArgumentException if the given generalLedgerInputTypeCode is empty
     * @throws UnknownGeneralLedgerInputTypeException if the given inputTypeCode does not exist in the database
     */
    public GeneralLedgerInputType getGeneralLedgerInputTypeByInputTypeCode(String generalLedgerInputTypeCode);

}
