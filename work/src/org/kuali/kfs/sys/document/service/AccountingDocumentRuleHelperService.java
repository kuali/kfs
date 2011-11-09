/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;

/**
 * A collection of methods that assist in rule validation for accounting documents
 */
public interface AccountingDocumentRuleHelperService {
    /**
     * This method checks for the existence of the provided balance type, in the system and also checks to see if it is active.
     * 
     * @param balanceType
     * @param errorPropertyName also used as the BalanceTyp DD attribute name
     * @return True if the balance type is valid, false otherwise.
     */
    public abstract boolean isValidBalanceType(BalanceType balanceType, String errorPropertyName);
    
    /**
     * This method checks for the existence of the provided balance type, in the system and also checks to see if it is active.
     * 
     * @param balanceType
     * @param entryClass the Class of the DataDictionary entry containing the attribute with the label for the error message
     * @param attributeName the name of the attribute in the DataDictionary entry
     * @param errorPropertyName
     * @return True if the balance type is valid, false otherwise.
     */
    public abstract boolean isValidBalanceType(BalanceType balanceType, Class entryClass, String attributeName, String errorPropertyName);
    
    /**
     * This method checks for the existence of the accounting period in the system and also makes sure that the accounting period is
     * open for posting.
     * 
     * @param accountingPeriod
     * @param entryClass
     * @param attribueName
     * @param errorPropertyName
     * @return True if the accounting period exists in the system and is open for posting, false otherwise.
     */
    public abstract boolean isValidOpenAccountingPeriod(AccountingPeriod accountingPeriod, Class entryClass, String attribueName, String errorPropertyName);
    
    /**
     * Some documents have reversal dates. This method represents the common implementation that transactional documents follow for
     * reversal dates - that they must not be before the current date.
     * 
     * @param reversalDate
     * @param errorPropertyName
     * @return boolean True if the reversal date is not earlier than the current date, false otherwise.
     */
    public abstract boolean isValidReversalDate(java.sql.Date reversalDate, String errorPropertyName);
    
    /**
     * Determines whether an accounting line is an income line or not. This goes agains the configurable object type code list in
     * the ApplicationParameter mechanism. This list can be configured externally.
     * 
     * @param accountingLine
     * @return boolean True if the line is an income line.
     */
    public abstract boolean isIncome(GeneralLedgerPendingEntrySourceDetail postable);
    
    /**
     * Check object code type to determine whether the accounting line is expense.
     * 
     * @param accountingLine
     * @return boolean True if the line is an expense line.
     */
    public abstract boolean isExpense(GeneralLedgerPendingEntrySourceDetail postable);
    
    /**
     * Makes sure that the objectCode attribute is fully populated b/c we are using proxying in our persistence layer.
     * 
     * @param accountingLine
     * @return the object type code of the object code of the given accounting line
     */
    public abstract String getObjectCodeTypeCodeWithoutSideEffects(GeneralLedgerPendingEntrySourceDetail postable);
    
    /**
     * Gets the named property from ConfigurationService (i.e., from ApplicationResources.properties) and formats it with the
     * given arguments (if any).
     * 
     * @param propertyName
     * @param arguments
     * @return the formatted property (i.e., message), with any {@code {0}} replaced with the first argument, {@code {1}} with the
     *         second argument, etc.
     */
    public abstract String formatProperty(String propertyName, Object... arguments);
}
