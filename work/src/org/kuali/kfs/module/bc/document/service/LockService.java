/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.service;

import java.util.List;
import java.util.SortedSet;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.impl.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.exception.BudgetConstructionLockUnavailableException;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants.LockStatus;


/**
 * This interface defines the methods that a LockService must provide. LockServiceImpl consists of methods that manage the various
 * locks used in the Budget module. Locks are needed to serialize user updates since a BC Edoc is potentially editable by many users
 * simultaneously and the default Optimistic locking scheme used by KFS would produce an inconsistent set of data. <B>Accountlock</B>
 * controls exclusive access to the BC Edoc <B>Positionlock</B> controls exclusive access to a BC Position <B>Fundinglock</B>
 * controls shared funding access. An associated Positionlock must exist before attempting to get a Fundinglock. Accountlock and
 * Fundinglock are mutex. <B>Transactionlock</B> controls exclusive access to serialize updates to the accounting lines in the BC
 * Edoc. A Fundinglock must exist before creating a Transactionlock. The Transactionlock lifecycle is short, required only for the
 * duration of the accounting line update.
 */
public interface LockService {

    /**
     * This method attempts to lock the given Account for the passed in uuid. Finding an exising account lock for the uuid returns
     * success without having to relock After setting an accountlock, if any funding locks are found, it releases the accountlock
     * and sets BCLockStatus with LockStatus.FLOCK_FOUND. Accountlocks and Fundinglocks are mutex
     * 
     * @param bcHeader
     * @param personUniversalIdentifier
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock), FLOCK_FOUND (also sets
     *         fundingLocks), BY_OTHER (also sets accountLockOwner), NO_DOOR (null bcHeader)
     */
    public BudgetConstructionLockStatus lockAccount(BudgetConstructionHeader bcHeader, String personUniversalIdentifier);

    /**
     * This method checks the database for an accountlock. It assumes a valid bcHeader parameter
     * 
     * @param bcHeader
     * @return Returns true if locked, false if not locked or not found in the database
     */
    public boolean isAccountLocked(BudgetConstructionHeader bcHeader);

    /**
     * Checks the given user has an account lock for the given document.
     * 
     * @param chartOfAccountsCode - chart code of account lock
     * @param accountNumber - account number of account lock
     * @param subAccountNumber - sub account number of account lock
     * @param fiscalYear - fiscal year of account lock
     * @param personUserIdentifier - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isAccountLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier);

    /**
     * This method attempts to unlock the given BudgetConstructionHeader.
     * 
     * @param bcHeader
     * @return LockStatus.SUCCESS, NO_DOOR (not found), OPTIMISTIC_EX (lost optimistic lock)
     */
    public LockStatus unlockAccount(BudgetConstructionHeader bcHeader);

    /**
     * This returns the set of BCFundingLocks associated with a BCHeader. The set is sorted by the UniversalUser personName
     * 
     * @param bcHeader
     * @return SortedSet<BudgetConstructionFundingLock>
     */
    public SortedSet<BudgetConstructionFundingLock> getFundingLocks(BudgetConstructionHeader bcHeader);

    /**
     * This method sets a funding lock associated to the header. It tests for an accountlock before/after to ensure there is no
     * locking conflict. Finding an accountlock after setting a fundinglock causes the fundinglock to be released. account locks and
     * funding locks are mutex. Finding a funding lock for the passed in uuid returns success without having to relock
     * 
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
     * Checks if the given user has a funding lock for the given accounting key.
     * 
     * @param chartOfAccountsCode - chart code of funding lock
     * @param accountNumber - account number of funding lock
     * @param subAccountNumber - sub account number of funding lock
     * @param fiscalYear - fiscal year of funding lock
     * @param personUserIdentifier - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isFundingLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier);

    /**
     * This locks the position, meaning it sets the position lock id field with the puid. Finding the position already locked by the
     * same user simply returns success.
     * 
     * @param positionNumber
     * @param fiscalYear
     * @param personUniversalIdentifier
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock), BY_OTHER (also sets
     *         positionLockOwner), NO_DOOR (BudgetConstructionPosition found)
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
     * Checks the given user has an position lock for the given position number.
     * 
     * @param positionNumber - position number of position record
     * @param fiscalYear - fiscal year of position record
     * @param personUserIdentifier - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isPositionLockedByUser(String positionNumber, Integer fiscalYear, String personUserIdentifier);

    /**
     * Checks the given user has an position/funding lock for the given position number and accounting key.
     * 
     * @param positionNumber - position number of position record
     * @param chartOfAccountsCode - chart code of funding lock
     * @param accountNumber - account number of funding lock
     * @param subAccountNumber - sub account number of funding lock
     * @param fiscalYear - fiscal year of position and funding record
     * @param personUserIdentifier - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isPositionFundingLockedByUser(String positionNumber, String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier);

    /**
     * This removes an existing positionlock
     * 
     * @param positionNumber
     * @param fiscalYear
     * @return LockStatus.SUCCESS (success or already unlocked), OPTIMISTIC_EX (lost optimistic lock - unlikely), NO_DOOR
     *         (BudgetConstructionPosition not found)
     */
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear);

    /**
     * This attempts a transactionlock on a BC Edoc for a pUId. It retries based on the setting of
     * BudgetConstructionConstants.maxLockRetry.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param personUniversalIdentifier
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock - unlikely) BY_OTHER
     *         (retries exhausted, also sets transactionLockOwner), NO_DOOR (BudgetConstructionHeader not found)
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
     * Checks the given user has an transaction lock for the given document.
     * 
     * @param chartOfAccountsCode - chart code of transaction lock
     * @param accountNumber - account number of transaction lock
     * @param subAccountNumber - sub account number of transaction lock
     * @param fiscalYear - fiscal year of transaction lock
     * @param personUserIdentifier - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isTransactionLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier);

    /**
     * This removes an existing transactionlock for a BC EDoc (account).
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return LockStatus.SUCCESS (success or already unlocked), OPTIMISTIC_EX (lost optimistic lock - unlikely), NO_DOOR
     *         (BudgetConstructionHeader not found)
     */
    public LockStatus unlockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * Retrieves all current account locks for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return budget headers that are locked
     */
    public List<BudgetConstructionHeader> getAllAccountLocks(String lockUnivId);

    /**
     * Retrieves all current transaction locks for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return budget headers that are locked
     */
    public List<BudgetConstructionHeader> getAllTransactionLocks(String lockUnivId);

    /**
     * Retrieves all funding locks that do not have a corresponding position lock for the given user (or all locks if user is
     * null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return funding locks records
     */
    public List<BudgetConstructionFundingLock> getOrphanedFundingLocks(String lockUnivId);

    /**
     * Retrieves all current position/funding locks for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId - universal id that will be used in lock query
     * @return position/funding records that are locked.
     */
    public List<PendingBudgetConstructionAppointmentFunding> getAllPositionFundingLocks(String lockUnivId);

    /**
     * Retrieves all current position locks without a funding lock for the given user (or all locks if user is null/empty).
     * 
     * @param lockUnivId universal id that will be used in lock query
     * @return positions that are locked.
     */
    public List<BudgetConstructionPosition> getOrphanedPositionLocks(String lockUnivId);

    /**
     * Helper method to check if a lock exists for the given parameters.
     * 
     * @param lockSummary - contains information about the record to unlock
     * @return boolean true if lock exists, false otherwise
     */
    public boolean checkLockExists(BudgetConstructionLockSummary lockSummary);

    /**
     * Helper method to check the lock type and do the unlock with the lock summary fields.
     * 
     * @param lockSummary - contains information about the record to unlock
     * @return LockStatus.SUCCESS, NO_DOOR (not found), OPTIMISTIC_EX (lost optimistic lock)
     */
    public LockStatus doUnlock(BudgetConstructionLockSummary lockSummary);

    /**
     * determine whether the account lock on the given budget document is held by the the specified user
     * 
     * @param budgetConstructionHeader the given budget document
     * @param personUserIdentifier the specified user
     * @return true if the account lock on the given budget document is held by the the specified user; otherwise, false
     */
    public boolean isAccountLockedByUser(BudgetConstructionHeader budgetConstructionHeader, String personUserIdentifier);
    
    /**
     * Retrieves account locks for funding records, for use in the payrate import process. Throws BudgetConstructionLockUnavailableException if new account lock is unavailable
     * 
     * @param fundingRecords
     * @param user
     * @return
     * @throws BudgetConstructionLockUnavailableException
     */
    public List<PendingBudgetConstructionAppointmentFunding> lockPendingBudgetConstructionAppointmentFundingRecords(List<PendingBudgetConstructionAppointmentFunding> fundingRecords, UniversalUser user) throws BudgetConstructionLockUnavailableException;
}
