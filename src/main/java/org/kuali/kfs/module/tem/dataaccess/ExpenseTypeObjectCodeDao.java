/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
