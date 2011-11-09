/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import java.util.List;
import java.util.SortedSet;

import org.kuali.kfs.module.bc.BCConstants.LockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.exception.BudgetConstructionLockUnavailableException;
import org.kuali.rice.kim.api.identity.Person;


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
     * @param principalId
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock), FLOCK_FOUND (also sets
     *         fundingLocks), BY_OTHER (also sets accountLockOwner), NO_DOOR (null bcHeader)
     */
    public BudgetConstructionLockStatus lockAccount(BudgetConstructionHeader bcHeader, String principalId);

    /**
     * This method checks the database for an accountlock. It assumes a valid bcHeader parameter
     * 
     * @param bcHeader
     * @return Returns true if locked, false if not locked or not found in the database
     */
    public boolean isAccountLocked(BudgetConstructionHeader bcHeader);

    /**
     * This method checks the database for an accountlock according to the given appointment funding. It assumes a valid
     * appointmentFunding parameter
     * 
     * @param appointmentFunding the given appointment funding
     * @return Returns true if locked, false if not locked or not found in the database
     */
    public boolean isAccountLocked(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * Checks the given user has an account lock for the given document.
     * 
     * @param chartOfAccountsCode - chart code of account lock
     * @param accountNumber - account number of account lock
     * @param subAccountNumber - sub account number of account lock
     * @param fiscalYear - fiscal year of account lock
     * @param principalId - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isAccountLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId);

    /**
     * This method attempts to unlock the given BudgetConstructionHeader.
     * 
     * @param bcHeader
     * @return LockStatus.SUCCESS, NO_DOOR (not found), OPTIMISTIC_EX (lost optimistic lock)
     */
    public LockStatus unlockAccount(BudgetConstructionHeader bcHeader);

    /**
     * This returns the set of BCFundingLocks associated with a BCHeader. The set is sorted by the Person name
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
     * @param principalId
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, BY_OTHER (accountlock found)
     */
    public BudgetConstructionLockStatus lockFunding(BudgetConstructionHeader bcHeader, String principalId);

    /**
     * acquire a lock for the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @param person the specified user
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, BY_OTHER (accountlock found)
     */
    public BudgetConstructionLockStatus lockFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person);

    /**
     * This removes the fundinglock for the account and user
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param principalId
     * @return LockStatus.SUCCESS, NO_DOOR (no fundinglock found)
     */
    public LockStatus unlockFunding(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId);

    /**
     * release the lock for the given appointment funding if any
     * 
     * @param appointmentFunding the given appointment funding that could have lock
     * @param person the user who owns the lock on the given appointment funding
     */
    public LockStatus unlockFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person);

    /**
     * release the locks for the given appointment fundings if any
     * 
     * @param lockedFundings the given appointment fundings that could have locks
     * @param person the user who owns the locks on the given appointment fundings
     */
    public void unlockFunding(List<PendingBudgetConstructionAppointmentFunding> lockedFundings, Person person);

    /**
     * Checks if the given user has a funding lock for the given accounting key.
     * 
     * @param chartOfAccountsCode - chart code of funding lock
     * @param accountNumber - account number of funding lock
     * @param subAccountNumber - sub account number of funding lock
     * @param fiscalYear - fiscal year of funding lock
     * @param principalId - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isFundingLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId);

    /**
     * This locks the position, meaning it sets the position lock id field with the puid. Finding the position already locked by the
     * same user simply returns success.
     * 
     * @param positionNumber
     * @param fiscalYear
     * @param principalId
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock), BY_OTHER (also sets
     *         positionLockOwner), NO_DOOR (BudgetConstructionPosition found)
     */
    public BudgetConstructionLockStatus lockPosition(String positionNumber, Integer fiscalYear, String principalId);

    /**
     * acquire a lock for the given budget position
     * 
     * @param position the given position
     * @param person the specified user
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock), BY_OTHER (also sets
     *         positionLockOwner), NO_DOOR (BudgetConstructionPosition found)
     */
    public BudgetConstructionLockStatus lockPosition(BudgetConstructionPosition position, Person person);

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
     * @param principalId - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isPositionLockedByUser(String positionNumber, Integer fiscalYear, String principalId);

    /**
     * Checks the given user has an position/funding lock for the given position number and accounting key.
     * 
     * @param positionNumber - position number of position record
     * @param chartOfAccountsCode - chart code of funding lock
     * @param accountNumber - account number of funding lock
     * @param subAccountNumber - sub account number of funding lock
     * @param fiscalYear - fiscal year of position and funding record
     * @param principalId - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isPositionFundingLockedByUser(String positionNumber, String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId);

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
     * release the locks on a positions with the given information
     * 
     * @param positionNumber the given position number of a position
     * @param fiscalYear the given fiscal year of a position
     * @param person the specified user who owns the locks on the position
     * @return LockStatus.SUCCESS (success or already unlocked), OPTIMISTIC_EX (lost optimistic lock - unlikely), NO_DOOR
     *         (BudgetConstructionPosition not found)
     */
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear, String principalId);

    /**
     * release the lock for the given position if any
     * 
     * @param position the given budget construction position that could have locks
     * @param person the specified user who owns the lock on the given position
     */
    public LockStatus unlockPostion(BudgetConstructionPosition position, Person person);

    /**
     * release the locks for the given positions if any
     * 
     * @param lockedPositions the given budget construction positions that could have locks
     * @param person the specified user who owns the locks on the given positions
     */
    public void unlockPostion(List<BudgetConstructionPosition> lockedPositions, Person person);

    /**
     * This attempts a transactionlock on a BC Edoc for a pUId. It retries based on the setting of
     * BCConstants.maxLockRetry.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param principalId
     * @return BudgetConstructionLockStatus with lockStatus.SUCCESS, OPTIMISTIC_EX (lost optimistic lock - unlikely) BY_OTHER
     *         (retries exhausted, also sets transactionLockOwner), NO_DOOR (BudgetConstructionHeader not found)
     */
    public BudgetConstructionLockStatus lockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId);

    /**
     * attemps to have a transaction lock based on the information provided by the given funding line
     * 
     * @param appointmentFunding the given appointment funding
     * @param person the specified user
     */
    public BudgetConstructionLockStatus lockTransaction(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person);

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
     * @param principalId - lock user id
     * @return true if locked, false if not locked or not found in the database
     */
    public boolean isTransactionLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId);

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
     * attemps to unlock a transaction based on the information provided by the given funding line
     * 
     * @param appointmentFunding the given appointment funding
     * @param person the specified user
     */
    public void unlockTransaction(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person);

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
     * @param person the specified user
     * @return true if the account lock on the given budget document is held by the the specified user; otherwise, false
     */
    public boolean isAccountLockedByUser(BudgetConstructionHeader budgetConstructionHeader, Person person);

    /**
     * Retrieves account locks for funding records, for use in the payrate import process. Throws
     * BudgetConstructionLockUnavailableException if new account lock is unavailable
     * 
     * @param fundingRecords
     * @param user
     * @return
     * @throws BudgetConstructionLockUnavailableException
     */
    public List<PendingBudgetConstructionAppointmentFunding> lockPendingBudgetConstructionAppointmentFundingRecords(List<PendingBudgetConstructionAppointmentFunding> fundingRecords, Person user) throws BudgetConstructionLockUnavailableException;

    /**
     * Retrives an account lock (@see
     * org.kuali.kfs.module.bc.document.service.LockService#lockAccount(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     * java.lang.String) and commits the lock. Used by the request import process.
     * 
     * @param bcHeader
     * @param principalId
     * @return
     */
    public BudgetConstructionLockStatus lockAccountAndCommit(BudgetConstructionHeader bcHeader, String principalId);

    /**
     * Locks the position record for the given key if not already locked. Then retrieves all active funding lines for the position
     * that are not marked as delete and attempts to lock each one.
     * 
     * @param universityFiscalYear budget fiscal year, primary key field for position record
     * @param positionNumber position number, primary key field for position record
     * @param principalId current user requesting the lock
     * @return <code>BudgetConstructionLockStatus</code> indicating the status of the lock attempt. Success is returned if all lock attempts were successful, else one of the Failure status codes are returned
     */
    public BudgetConstructionLockStatus lockPositionAndActiveFunding(Integer universityFiscalYear, String positionNumber, String principalId);
    
    /**
     * Unlocks the position and all associated funding lines not marked as delete.
     * 
     * @param universityFiscalYear budget fiscal year, primary key field for position record
     * @param positionNumber position number, primary key field for position record
     * @param principalId current user requesting the unlock
     * @return <code>LockStatus</code> indicating the status of the unlock attempt.
     */
    public LockStatus unlockPositionAndActiveFunding(Integer universityFiscalYear, String positionNumber, String principalId);
}
