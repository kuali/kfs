/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.budget.service;

import java.util.SortedSet;

import org.kuali.kfs.KFSConstants.BudgetConstructionConstants.LockStatus;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.service.impl.BudgetConstructionLockStatus;


/**
 * This interface defines the methods that a LockService must provide.
 * 
 *  
 * LockServiceImpl consists of methods that manage the various locks used in the Budget module. Locks
 * are needed to serialize user updates since a BC Edoc is potentially editable by many users simultaneously
 * and the default Optimistic locking scheme used by KFS would produce an inconsistent set of data.
 * 
 * <B>Accountlock</B> controls exclusive access to the BC Edoc
 * <B>Positionlock</B> controls exclusive access to a BC Position
 * <B>Fundinglock</B> controls shared funding access. An associated Positionlock must exist before attempting
 * to get a Fundinglock.  Accountlock and Fundinglock are mutex.
 * <B>Transactionlock</B> controls exclusive access to serialize updates to the accounting lines in the BC Edoc.
 * A Fundinglock must exist before creating a Transactionlock.  The Transactionlock lifecycle is short, required
 * only for the duration of the accounting line update.
 * 
 */
public interface LockService {

    /**
     * 
     * This method attempts to lock the given Account for the passed in uuid.
     * Finding an exising account lock for the uuid returns success without having to relock
     * After setting an accountlock, if any funding locks are found, it releases the accountlock
     * and sets BCLockStatus with LockStatus.FLOCK_FOUND.  Accountlocks and Fundinglocks are mutex
     * @param bcHeader
     * @param personUniversalIdentifier
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock),
     * FLOCK_FOUND (also sets fundingLocks), BY_OTHER (also sets accountLockOwner), NO_DOOR (null bcHeader)
     */
    public BudgetConstructionLockStatus lockAccount(BudgetConstructionHeader bcHeader, String personUniversalIdentifier);

    /**
     * 
     * This method checks the database for an accountlock.
     * It assumes a valid bcHeader parameter
     * @param bcHeader
     * @return Returns true if locked, false if not locked or not found in the database 
     */
    public boolean isAccountLocked(BudgetConstructionHeader bcHeader);

    /**
     * 
     * This method attempts to unlock the given BudgetConstructionHeader.
     * 
     * @param bcHeader
     * @return LockStatus.SUCCESS, NO_DOOR (not found), OPTIMISTIC_EX (lost optimistic lock)
     */
    public LockStatus unlockAccount(BudgetConstructionHeader bcHeader);

    /**
     * This returns the set of BCFundingLocks associated with a BCHeader.  
     * The set is sorted by the UniversalUser personName
     * @param bcHeader
     * @return SortedSet<BudgetConstructionFundingLock>
     */
    public SortedSet<BudgetConstructionFundingLock> getFundingLocks(BudgetConstructionHeader bcHeader);

    /**
     * 
     * This method sets a funding lock associated to the header.  It tests for an accountlock before/after
     * to ensure there is no locking conflict. Finding an accountlock after setting a fundinglock causes
     * the fundinglock to be released.  account locks and funding locks are mutex.
     * Finding a funding lock for the passed in uuid returns success without having to relock
     * @param bcHeader
     * @param personUniversalIdentifier
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, BY_OTHER (accountlock found)
     */
    public BudgetConstructionLockStatus lockFunding(BudgetConstructionHeader bcHeader, String personUniversalIdentifier);

    /**
     * This removes the fundinglock for the account and user
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param personUniversalIdentifier
     * @return LockStatus.SUCCESS, NO_DOOR (no fundinglock found)
     */
    public LockStatus unlockFunding(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier);

    /**
     * 
     * This locks the position, meaning it sets the position lock id field with the puid.
     * Finding the position already locked by the same user simply returns success.
     * @param positionNumber
     * @param fiscalYear
     * @param personUniversalIdentifier
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock),
     * BY_OTHER (also sets positionLockOwner), NO_DOOR (BudgetConstructionPosition found) 
     */
    public BudgetConstructionLockStatus lockPosition(String positionNumber, Integer fiscalYear, String personUniversalIdentifier);

    /**
     * This checks the database for an existing positionlock 
     *
     * @param positionNumber
     * @param fiscalYear
     * @return true or false (not locked or BudgetConstructionPosition not found) 
     */
    public boolean isPositionLocked(String positionNumber, Integer fiscalYear);

    /**
     * This removes an existing positionlock 
     *
     * @param positionNumber
     * @param fiscalYear
     * @return LockStatus.SUCCESS (success or already unlocked), OPTIMISTIC_EX (lost optimistic lock - unlikely),
     * NO_DOOR (BudgetConstructionPosition not found)
     */
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear);

    /**
     * This attempts a transactionlock on a BC Edoc for a pUId.  It retries based on the setting of
     * BudgetConstructionConstants.maxLockRetry.
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param personUniversalIdentifier
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock - unlikely)
     * BY_OTHER (retries exhausted, also sets transactionLockOwner), NO_DOOR (BudgetConstructionHeader not found)
     */
    public BudgetConstructionLockStatus lockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier);

    /**
     * This checks the database for an existing transactionlock for the BC EDoc (account). 
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return true or false (not locked or BudgetConstructionHeader not found) 
     */
    public boolean isTransactionLocked(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * This removes an existing transactionlock for a BC EDoc (account). 
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return LockStatus.SUCCESS (success or already unlocked), OPTIMISTIC_EX (lost optimistic lock - unlikely),
     * NO_DOOR (BudgetConstructionHeader not found)
     */
    public LockStatus unlockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    public void setBudgetConstructionDao(BudgetConstructionDao bcHeaderDao);

}