/*
 * Copyright 2009 The Kuali Foundation
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

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.document.AccountDelegateGlobalMaintainableImpl;
import org.kuali.kfs.coa.document.AccountDelegateMaintainableImpl;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

/**
 * An interface of services to support account delegate logic
 */
public interface AccountDelegateService {
    
    /**
     * 
     * This method checks for any MaintenanceLocks that would block the creation of this document
     * @param global The AccountDelegateGlobalMaintainableImpl to check against.
     * @param docNumber The document number of the AccountDelegateGlobalMaintainableImpl in question.
     * @return the documentNumber of the locking record or null if none.
     */
    
    public String getLockingDocumentId(AccountDelegateGlobalMaintainableImpl global, String docNumber);
    
    /**
     * 
     * This method checks for any MaintenanceLocks that would block the creation of this document
     * @param delegate The AccountDelegateMaintainableImpl to check against.
     * @param docNumber The document number of the AccountDelegateMaintainableImpl in question.
     * @return the documentNumber of the locking record or null if none.
     */
    public String getLockingDocumentId(AccountDelegateMaintainableImpl delegate, String docNumber);
    
    /**
     * Builds an appropriate maintainable with the given account delegate as the business object
     * @param delegate the account delegate to wrap in a maintainable
     * @return an appropriate maintainable
     */
    public abstract FinancialSystemMaintainable buildMaintainableForAccountDelegate(AccountDelegate delegate);
    
    /**
     * Retrieves all active account delegations which delegate to the given Person
     * @param principalId a principal id of the person to find account delegations for
     * @param primary whether the account delegates returned should be primary or not
     * @return a List of AccountDelegate business objects, representing that person's delegations
     */
    public abstract Iterator<AccountDelegate> retrieveAllActiveDelegationsForPerson(String principalId, boolean primary);

    /**
     * Determines if the given principal is an active delegate for any non-closed account
     * @param principalId the principal ID to check primary account delegations for
     * @return true if the principal is a primary account delegate, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(String principalId);
    
    /**
     * 
     * Determines if the given principal is an active delegate for any non-closed account
     * @param principalId the principal ID to check secondary account delegations for
     * @return true if the principal is a secondary account delegate, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(String principalId);
    
    /**
     * Saves the given account delegate to the persistence store
     * @param accountDelegate the account delegate to save
     */
    public abstract void saveForMaintenanceDocument(AccountDelegate accountDelegate);
    
    /**
     * Persists the given account delegate global maintenance document inactivations
     * @param delegatesToInactivate the List of delegates to inactivate
     */
    public abstract void saveInactivationsForGlobalMaintenanceDocument(List<PersistableBusinessObject> delegatesToInactivate);
    
    /**
     * Persists the given account delegate global maintenance document changes
     * @param delegatesToChange the List of delegates to change
     */
    public abstract void saveChangesForGlobalMaintenanceDocument(List<PersistableBusinessObject> delegatesToChange);
    
    /**
     * Updates the role that this delegate is part of, to account for the changes in this delegate
     */
    public abstract void updateDelegationRole();
    
}
