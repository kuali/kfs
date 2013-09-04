/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.dataaccess;

import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;

/**
 * A DAO which handles ExpenseTypeObjectCode objects
 */
public interface ExpenseTypeObjectCodeDao {
    /**
     * Looks up all matching ExpenseTypeObjectCodes - that includes records for each document type passed in that it can find, as well as finding matching records with "ALL" in either the trip type or the traveler type
     * @param expenseCodeType the expense type code to find records for
     * @param documentTypes the document types to find records for
     * @param tripType the trip type + ALL to find records for
     * @param travelerType the traveler type + ALL to find records for
     * @return a List of matching expense type object code records, ready to be sorted
     */
    public List<ExpenseTypeObjectCode> findMatchingExpenseTypeObjectCodes(String expenseCodeType, Set<String> documentTypes, String tripType, String travelerType);

    /**
     * Finds the distinct expense type object code records associated with the given Set of document types, and potentially trip type and traveler type
     * @param documentTypes the document types
     * @param tripType the trip type to find matching expense type object codes for; if null, then all will be included
     * @param travelerType the traveler type to find matching expense type object codes for; if null, then all will be included
     * @return a List of associated ExpenseTypeObjectCode records
     */
    public List<ExpenseTypeObjectCode> findMatchingExpenseTypesObjectCodes(Set<String> documentTypes, String tripType, String travelerType);
}
