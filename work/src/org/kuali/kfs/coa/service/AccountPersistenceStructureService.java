/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.service;

import java.util.Map;
import java.util.Set;

import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.PersistenceStructureService;

/**
 * Provides facilities to obtain chartofAccountsCode-accountNumber foreign key fields 
 * and the corresponding account or account-involved reference objects.
 */
public interface AccountPersistenceStructureService extends PersistenceStructureService {
    
    /**
     * Decides if the specified class is an account related class, i.e. whether it contains chartOfAccountsCode and accountNumber as part of the primary keys.
     * @param clazz the specified class
     * @return true if the class contains chartOfAccountsCode and accountNumber as part of the primary keys; false otherwise.
     */
    public boolean isAccountRelatedClass(Class clazz);

    /**
     * Determines the list of collection accounts (or other account-involved BOs) contained within the specified parent object. 
     * For example, an Award object contains awardAccounts.
     * Note: these do not include the following cases
     *      - nested collection accounts
     *      - non-maintainable collection accounts
     *      
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for collection accounts.
     * @return Map containing the collection account names (key) and class type (value) or empty map if the BO contains no collection accounts.
     */
    public Map<String, Class> listCollectionAccountFields(PersistableBusinessObject bo);
    
    /**
     * Determines the list of chartOfAccountsCode fields as one of the primary keys in the collection accounts 
     * (or other account-involved BOs) contained within the specified parent object. 
     * For example, an Award object contains awardAccounts with chartOfAccountsCode as one of the primary keys.
     * Note: these do not include the following cases
     *      - nested collection accounts
     *      - non-maintainable collection accounts
     *       
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for collection accounts.
     * @return Set containing the chartOfAccountsCode field names in collection accounts or empty set if the BO contains no collection accounts.
     */
    public Set<String> listCollectionChartOfAccountsCodeNames(PersistableBusinessObject bo);

    /**
     * Determines the list of reference accounts (or other account-involved BOs) contained within the specified parent object. 
     * For example, an Account object contains reportsToAccount etc.
     * Note: these do not include the following cases:
     *      - nested reference accounts
     *      - reference accounts in collections
     *      - reference accounts whose PKs are also PKs of the BO
     *      - reference accounts whose PKs don't exist in the BO as foreign keys but are referred by the maintenance page in a nested way 
     *      - non-existing reference accounts whose PKs exist in the BO as foreign keys (this should never happen)
     *      - non-maintainable reference accounts
     * 
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for reference accounts.
     * @return Map containing the reference account names (key) and class type (value) or empty map if the BO contains no reference accounts.
     */
    public Map<String, Class> listReferenceAccountFields(PersistableBusinessObject bo);

    /**
     * Determines the list of chartOfAccountsCode-accountNumber pairs as (part of) the foreign keys of the reference accounts 
     * (or other account-involved BOs) contained within the specified parent object. 
     * For example, an Account object contains reportsToAccount with reportsToChartOfAccountsCode-reportsToAccountNumber as the foreign keys.
     * Note: these do not include the following cases:
     *      - nested reference accounts
     *      - reference accounts in collections
     *      - reference accounts whose PKs are also PKs of the BO
     *      - reference accounts whose PKs don't exist in the BO as foreign keys but are referred by the maintainance page in a nested way 
     *      - non-existing reference accounts whose PKs exist in the BO as foreign keys (this should never happen)
     *      - non-maintainable reference accounts
     *      
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for reference accounts.
     * @return Map containing the chartOfAccountsCode-accountNumber (key-value) foreign key pairs for all reference accounts or empty map if the BO contains no reference accounts.
     */
    public Map<String, String> listChartCodeAccountNumberPairs(PersistableBusinessObject bo);

    /**
     * Determines the list of accountNumber-chartOfAccountsCode pairs as (part of) the foreign keys of the reference accounts 
     * (or other account-involved BOs) contained within the specified parent object. 
     * For example, an Account object contains reportsToAccount with reportsToAccountNumber-reportsToChartOfAccountsCode as the foreign keys.
     * Note: these do not include the following cases:
     *      - nested reference accounts
     *      - reference accounts in collections
     *      - reference accounts whose PKs are also PKs of the BO
     *      - reference accounts whose PKs don't exist in the BO as foreign keys but are referred by the maintainance page in a nested way 
     *      - non-existing reference accounts whose PKs exist in the BO as foreign keys (this should never happen)
     *      - non-maintainable reference accounts
     * 
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for reference accounts.
     * @return Map containing the accountNumber-chartOfAccountsCode (key-value) foreign key pairs for all reference accounts or empty map if the BO contains no reference accounts.
     */
    public Map<String, String> listAccountNumberChartCodePairs(PersistableBusinessObject bo);

    /**
     * Determines the list of chartOfAccountsCode fields as one of the foreign keys of the reference accounts 
     * (or other account-involved BOs) contained within the specified parent object. 
     * For example, an Account object contains reportsToAccount with reportsToChartOfAccountsCode as one of the foreign keys.
     * Note: these do not include the following cases:
     *      - nested reference accounts
     *      - reference accounts in collections
     *      - reference accounts whose PKs are also PKs of the BO
     *      - reference accounts whose PKs don't exist in the BO as foreign keys but are referred by the maintainance page in a nested way 
     *      - non-existing reference accounts whose PKs exist in the BO as foreign keys (this should never happen)
     *      - non-maintainable reference accounts
     * 
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for reference accounts.
     * @return Set containing the chartOfAccountsCode foreign key names for all reference accounts or empty set if the BO contains no reference accounts.
     */
    public Set<String> listChartOfAccountsCodeNames(PersistableBusinessObject bo);
    
    /**
     * Determines the list of accountNumber fields as one of the foreign keys of the reference accounts 
     * (or other account-involved BOs) contained within the specified parent object. 
     * For example, an Account object contains reportsToAccount with reportsToAccountNumber as one of the foreign keys.
     * Note: these do not include the following cases:
     *      - nested reference accounts
     *      - reference accounts in collections
     *      - reference accounts whose PKs are also PKs of the BO
     *      - reference accounts whose PKs don't exist in the BO as foreign keys but are referred by the maintainance page in a nested way 
     *      - non-existing reference accounts whose PKs exist in the BO as foreign keys (this should never happen)
     *      - non-maintainable reference accounts
     * 
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for reference accounts.
     * @return Set containing the accountNumber foreign key names for all reference accounts or empty set if the BO contains no reference accounts.
     */
    public Set<String> listAccountNumberNames(PersistableBusinessObject bo);

    /**
     * Gets the name of the chartOfAccountsCode field as one foreign key, paired with the specified accountNumber field 
     * as the other, of the reference account (or other account-involved BO) contained within the specified parent object. 
     * For example, an Account object contains reportsToAccount with reportsToChartOfAccountsCode and reportsToAccountNumber as the foreign key pair.
     * 
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for reference accounts.
     * @param accountNumberFieldName the name of the foreign key corresponding to the primary key accountNumber of the reference account. 
     * @return the chartOfAccountsCode field name of the reference account identified by foreign key accountNumberFieldName, or empty string if the BO contains no such reference account.
     */
    public String getChartOfAccountsCodeName(PersistableBusinessObject bo, String accountNumberFieldName);

    /**
     * Gets the name of the accountNumber field as one foreign key, paired with the specified chartOfAccountsCode field 
     * as the other, of the reference account (or other account-involved BO) contained within the specified parent object. 
     * For example, an Account object contains reportsToAccount with reportsToAccountNumber and reportsToChartOfAccountsCode as the foreign key pair.
     * 
     * @param bo BusinessObject (or subclass) instance that would like to be analyzed for reference accounts.
     * @param chartOfAccountsCodeFieldName the name of the foreign key corresponding to the primary key chartOfAccountsCode of the reference account. 
     * @return the accountNumber field name of the reference account identified by foreign key chartOfAccountsCode, or empty string if the BO contains no such reference account.
     */
    public String getAccountNumberName(PersistableBusinessObject bo, String chartOfAccountsCodeFieldName);

}

