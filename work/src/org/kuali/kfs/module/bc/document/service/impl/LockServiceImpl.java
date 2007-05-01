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
package org.kuali.module.budget.service.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kuali.kfs.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants.LockStatus;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.service.LockService;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the LockService interface
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
@Transactional
public class LockServiceImpl implements LockService {

    private BudgetConstructionDao budgetConstructionDao;
    
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
    public BudgetConstructionLockStatus lockAccount(BudgetConstructionHeader bcHeader, String personUniversalIdentifier) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus(); 
        if (bcHeader != null){
            if (bcHeader.getBudgetLockUserIdentifier() == null) {
                bcHeader.setBudgetLockUserIdentifier(personUniversalIdentifier);
                try {
                    budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                }
                catch (DataAccessException ex){
                    bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX);
                }
                if (bcLockStatus.getLockStatus() == LockStatus.SUCCESS){
                    //need to check for funding locks for the account
                    bcLockStatus.setFundingLocks(getFundingLocks(bcHeader));
                    if (!bcLockStatus.getFundingLocks().isEmpty()){
                        // get a freshcopy of header incase we lost the optimistic lock
                        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()); 
                        unlockAccount(freshBcHeader);
                        bcLockStatus.setLockStatus(LockStatus.FLOCK_FOUND);
                    }
                }
                return bcLockStatus;
            } else {
                if (bcHeader.getBudgetLockUserIdentifier().equals(personUniversalIdentifier)){
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                    return bcLockStatus;   //the user already has a lock
                } else {
                    bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                    bcLockStatus.setAccountLockOwner(bcHeader.getBudgetLockUserIdentifier());
                    return bcLockStatus;  //someone else has a lock
                }
            }
        } else {
            bcLockStatus.setLockStatus(LockStatus.NO_DOOR);
            return bcLockStatus;  //budget header not found
        }
    }

    /**
     * 
     * This method checks the database for an accountlock.
     * It assumes a valid bcHeader parameter
     * @param bcHeader
     * @return Returns true if locked, false if not locked or not found in the database 
     */
    public boolean isAccountLocked(BudgetConstructionHeader bcHeader) {

        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        if (freshBcHeader != null){
            if (freshBcHeader.getBudgetLockUserIdentifier() != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;  //TODO should return not found or throw exception
        }
    }
      
    /**
     * 
     * This method attempts to unlock the given BudgetConstructionHeader.
     * 
     * @param bcHeader
     * @return LockStatus.SUCCESS, NO_DOOR (not found), OPTIMISTIC_EX (lost optimistic lock)
     */
    public LockStatus unlockAccount(BudgetConstructionHeader bcHeader) {
    
        LockStatus lockStatus;
        
        if (bcHeader != null){
            if (bcHeader.getBudgetLockUserIdentifier() != null) {
                bcHeader.setBudgetLockUserIdentifier(null);
                try {
                    budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                    lockStatus = LockStatus.SUCCESS;
                }
                catch (DataAccessException ex){
                    lockStatus = LockStatus.OPTIMISTIC_EX;
                }
            } else {
                lockStatus = LockStatus.SUCCESS;    // already unlocked
            }
        } else {
            lockStatus = LockStatus.NO_DOOR;    // target not found
        }
        return lockStatus;
    }

    /**
     * This returns the set of BCFundingLocks associated with a BCHeader.  
     * The set is sorted by the UniversalUser personName
     * @param bcHeader
     * @return SortedSet<BudgetConstructionFundingLock>
     */
    public SortedSet<BudgetConstructionFundingLock>getFundingLocks(BudgetConstructionHeader bcHeader) {
        
        Collection<BudgetConstructionFundingLock> fundingLocks = budgetConstructionDao.getFlocksForAccount(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        SortedSet<BudgetConstructionFundingLock> sortedFundingLocks = new TreeSet<BudgetConstructionFundingLock>(new
                Comparator<BudgetConstructionFundingLock>(){
                    public int compare(BudgetConstructionFundingLock aFlock, BudgetConstructionFundingLock bFlock){
                        String nameA = aFlock.getAppointmentFundingLockUser().getPersonName();
                        String nameB = bFlock.getAppointmentFundingLockUser().getPersonName();
                        return nameA.compareTo(nameB);
                    }
                });    // TODO Can probably rip out once KualiUserService gets a comparator
        
        sortedFundingLocks.addAll(fundingLocks);
        return sortedFundingLocks;
    }
    
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
    public BudgetConstructionLockStatus lockFunding(BudgetConstructionHeader bcHeader, String personUniversalIdentifier) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus(); 
        
        if (!isAccountLocked(bcHeader)){
            BudgetConstructionFundingLock budgetConstructionFundingLock = 
                budgetConstructionDao.getByPrimaryId(
                        bcHeader.getChartOfAccountsCode(),
                        bcHeader.getAccountNumber(),
                        bcHeader.getSubAccountNumber(),
                        bcHeader.getUniversityFiscalYear(),
                        personUniversalIdentifier);
            if (budgetConstructionFundingLock != null && budgetConstructionFundingLock.getAppointmentFundingLockUserId().equals(personUniversalIdentifier)){
                bcLockStatus.setLockStatus(LockStatus.SUCCESS);
            } else {
                budgetConstructionFundingLock = new BudgetConstructionFundingLock(); 
                budgetConstructionFundingLock.setAppointmentFundingLockUserId(personUniversalIdentifier);
                budgetConstructionFundingLock.setAccountNumber(bcHeader.getAccountNumber());
                budgetConstructionFundingLock.setSubAccountNumber(bcHeader.getSubAccountNumber());
                budgetConstructionFundingLock.setChartOfAccountsCode(bcHeader.getChartOfAccountsCode());
                budgetConstructionFundingLock.setUniversityFiscalYear(bcHeader.getUniversityFiscalYear());
                budgetConstructionFundingLock.setFill1("L");
                budgetConstructionFundingLock.setFill2("L");
                budgetConstructionFundingLock.setFill3("L");
                budgetConstructionFundingLock.setFill4("L");
                budgetConstructionFundingLock.setFill5("L");
                budgetConstructionDao.saveBudgetConstructionFundingLock(budgetConstructionFundingLock);
                if (isAccountLocked(bcHeader)){     // unlikely, but need to check this
                    bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                    unlockFunding(
                            bcHeader.getChartOfAccountsCode(),
                            bcHeader.getAccountNumber(),
                            bcHeader.getSubAccountNumber(),
                            bcHeader.getUniversityFiscalYear(),
                            personUniversalIdentifier);
                } else {
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                }
            }
        } else {
            bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
        }
        return bcLockStatus; 
    }
        
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
    public LockStatus unlockFunding(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier) {
        
        LockStatus lockStatus;
        
        BudgetConstructionFundingLock budgetConstructionFundingLock = budgetConstructionDao.getByPrimaryId(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, personUniversalIdentifier);
        if (budgetConstructionFundingLock != null){
            budgetConstructionDao.deleteBudgetConstructionFundingLock(budgetConstructionFundingLock);
            lockStatus = LockStatus.SUCCESS;
        } else {
            lockStatus = LockStatus.NO_DOOR;    // target not found
        }
        return lockStatus;
    }

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
    public BudgetConstructionLockStatus lockPosition(String positionNumber, Integer fiscalYear, String personUniversalIdentifier) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        BudgetConstructionPosition bcPosition = budgetConstructionDao.getByPrimaryId(positionNumber, fiscalYear);
        if (bcPosition != null){
            if (bcPosition.getPositionLockUserIdentifier() == null) {
                bcPosition.setPositionLockUserIdentifier(personUniversalIdentifier);
                try {
                    budgetConstructionDao.saveBudgetConstructionPosition(bcPosition);
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                }
                catch (DataAccessException ex){
                    bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX);
                }
                return bcLockStatus;
            } else {
                if (bcPosition.getPositionLockUserIdentifier().equals(personUniversalIdentifier)){
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                    return bcLockStatus;   //the user already has a lock
                } else {
                    bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                    bcLockStatus.setPositionLockOwner(bcPosition.getPositionLockUserIdentifier());
                    return bcLockStatus;  //someone else has a lock
                }
            }
        } else {
            bcLockStatus.setLockStatus(LockStatus.NO_DOOR);
            return bcLockStatus;  //position not found
        }
    }

    /**
     * This checks the database for an existing positionlock 
     *
     * @param positionNumber
     * @param fiscalYear
     * @return true or false (not locked or BudgetConstructionPosition not found) 
     */
    public boolean isPositionLocked(String positionNumber, Integer fiscalYear) {

        BudgetConstructionPosition bcPosition = budgetConstructionDao.getByPrimaryId(positionNumber, fiscalYear);
        if (bcPosition != null){
            if (bcPosition.getPositionLockUserIdentifier() != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;  //TODO should return not found?
        }
    }

    /**
     * This removes an existing positionlock 
     *
     * @param positionNumber
     * @param fiscalYear
     * @return LockStatus.SUCCESS (success or already unlocked), OPTIMISTIC_EX (lost optimistic lock - unlikely),
     * NO_DOOR (BudgetConstructionPosition not found)
     */
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear) {
        
        LockStatus lockStatus;
        
        BudgetConstructionPosition bcPosition = budgetConstructionDao.getByPrimaryId(positionNumber, fiscalYear);
        if (bcPosition != null){
            if (bcPosition.getPositionLockUserIdentifier() != null){
                bcPosition.setPositionLockUserIdentifier(null);
                try {
                    budgetConstructionDao.saveBudgetConstructionPosition(bcPosition);
                    lockStatus = LockStatus.SUCCESS;
                }
                catch (DataAccessException ex){
                    lockStatus = LockStatus.OPTIMISTIC_EX;
                }
            } else {
                lockStatus = LockStatus.SUCCESS;  // already unlocked
            }
        } else {
            lockStatus = LockStatus.NO_DOOR;    // target not found
        }
        return lockStatus;
    }

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
    public BudgetConstructionLockStatus lockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier) {
        
        int lockRetry = 1;
        boolean done = false;

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        while (!done){
            BudgetConstructionHeader bcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
            if (bcHeader != null){
                if (bcHeader.getBudgetTransactionLockUserIdentifier() == null){
                    bcHeader.setBudgetTransactionLockUserIdentifier(personUniversalIdentifier);
                    try {
                        budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                        bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                    }
                    catch (DataAccessException ex){
                        bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX);  // unlikely
                    }
                    done = true;
                } else {
                    if (lockRetry > BudgetConstructionConstants.maxLockRetry){
                        bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                        bcLockStatus.setTransactionLockOwner(bcHeader.getBudgetTransactionLockUserIdentifier());
                        done = true;
                    }
                    lockRetry++;    // someone else has a lock, retry
                }
            } else {
                bcLockStatus.setLockStatus(LockStatus.NO_DOOR);  // target not found, unlikely
                done = true;
            }
        }
        return bcLockStatus;
    }

    /**
     * This checks the database for an existing transactionlock for the BC EDoc (account). 
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return true or false (not locked or BudgetConstructionHeader not found) 
     */
    public boolean isTransactionLocked(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {

        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (freshBcHeader != null){
            if (freshBcHeader.getBudgetTransactionLockUserIdentifier() != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;  //TODO should return not found
        }
    }

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
    public LockStatus unlockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        
        LockStatus lockStatus = LockStatus.NO_DOOR;

        BudgetConstructionHeader bcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (bcHeader != null){
            if (bcHeader.getBudgetTransactionLockUserIdentifier() != null) {
                bcHeader.setBudgetTransactionLockUserIdentifier(null);
                try {
                    budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                    lockStatus = LockStatus.SUCCESS;
                }
                catch (DataAccessException ex){
                    lockStatus = LockStatus.OPTIMISTIC_EX;
                }
            } else {
                lockStatus = LockStatus.SUCCESS;    // already unlocked
            }
        } else {
            lockStatus = LockStatus.NO_DOOR;    // target not found
        }
        return lockStatus;
    }

    public void setBudgetConstructionDao(BudgetConstructionDao bcHeaderDao) {
        this.budgetConstructionDao = bcHeaderDao;
    }

}

