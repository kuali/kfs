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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.exception.BudgetConstructionLockUnavailableException;
import org.kuali.kfs.module.bc.service.BudgetConstructionPositionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants.LockStatus;
import org.kuali.kfs.sys.service.NonTransactional;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the LockService interface LockServiceImpl consists of methods that manage the various locks used in the
 * Budget module. Locks are needed to serialize user updates since a BC Edoc is potentially editable by many users simultaneously
 * and the default Optimistic locking scheme used by KFS would produce an inconsistent set of data. <B>Accountlock</B> controls
 * exclusive access to the BC Edoc <B>Positionlock</B> controls exclusive access to a BC Position <B>Fundinglock</B> controls
 * shared funding access. An associated Positionlock must exist before attempting to get a Fundinglock. Accountlock and Fundinglock
 * are mutex. <B>Transactionlock</B> controls exclusive access to serialize updates to the accounting lines in the BC Edoc. A
 * Fundinglock must exist before creating a Transactionlock. The Transactionlock lifecycle is short, required only for the duration
 * of the accounting line update.
 */
public class LockServiceImpl implements LockService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LockServiceImpl.class);

    private BudgetConstructionDao budgetConstructionDao;
    private BudgetConstructionLockDao budgetConstructionLockDao;
    private BudgetDocumentService budgetDocumentService;
    private BudgetConstructionPositionService budgetConstructionPositionService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockAccount(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockAccount(BudgetConstructionHeader bcHeader, String personUniversalIdentifier) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        if (bcHeader != null) {
            bcLockStatus.setBudgetConstructionHeader(bcHeader);
            if (bcHeader.getBudgetLockUserIdentifier() == null) {
                bcHeader.setBudgetLockUserIdentifier(personUniversalIdentifier);
                try {
                    budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                }
                catch (DataAccessException ex) {
                    bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX);
                }
                if (bcLockStatus.getLockStatus() == LockStatus.SUCCESS) {
                    // need to check for funding locks for the account
                    bcLockStatus.setFundingLocks(getFundingLocks(bcHeader));
                    if (!bcLockStatus.getFundingLocks().isEmpty()) {
                        // get a freshcopy of header incase we lost the optimistic lock
                        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
                        unlockAccount(freshBcHeader);
                        bcLockStatus.setBudgetConstructionHeader(freshBcHeader);
                        bcLockStatus.setLockStatus(LockStatus.FLOCK_FOUND);
                    }
                }
                return bcLockStatus;
            }
            else {
                if (bcHeader.getBudgetLockUserIdentifier().equals(personUniversalIdentifier)) {
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                    return bcLockStatus; // the user already has a lock
                }
                else {
                    bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                    bcLockStatus.setAccountLockOwner(bcHeader.getBudgetLockUserIdentifier());
                    return bcLockStatus; // someone else has a lock
                }
            }
        }
        else {
            bcLockStatus.setLockStatus(LockStatus.NO_DOOR);
            return bcLockStatus; // budget header not found
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isAccountLocked(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader)
     */
    @Transactional
    public boolean isAccountLocked(BudgetConstructionHeader bcHeader) {

        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        if (freshBcHeader != null) {
            return freshBcHeader.getBudgetLockUserIdentifier() != null;
        }
        else {
            return false; // TODO should return not found or throw exception
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isAccountLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isAccountLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier) {
        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (freshBcHeader != null) {
            if (freshBcHeader.getBudgetLockUserIdentifier() != null && freshBcHeader.getBudgetLockUserIdentifier().equals(personUserIdentifier)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockAccount(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader)
     */
    @Transactional
    public LockStatus unlockAccount(BudgetConstructionHeader bcHeader) {

        LockStatus lockStatus;

        if (bcHeader != null) {
            if (bcHeader.getBudgetLockUserIdentifier() != null) {
                bcHeader.setBudgetLockUserIdentifier(null);
                try {
                    budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                    lockStatus = LockStatus.SUCCESS;
                }
                catch (DataAccessException ex) {
                    lockStatus = LockStatus.OPTIMISTIC_EX;
                }
            }
            else {
                lockStatus = LockStatus.SUCCESS; // already unlocked
            }
        }
        else {
            lockStatus = LockStatus.NO_DOOR; // target not found
        }
        return lockStatus;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#getFundingLocks(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader)
     */
    @Transactional
    public SortedSet<BudgetConstructionFundingLock> getFundingLocks(BudgetConstructionHeader bcHeader) {

        Collection<BudgetConstructionFundingLock> fundingLocks = budgetConstructionDao.getFlocksForAccount(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        SortedSet<BudgetConstructionFundingLock> sortedFundingLocks = new TreeSet<BudgetConstructionFundingLock>(new Comparator<BudgetConstructionFundingLock>() {
            public int compare(BudgetConstructionFundingLock aFlock, BudgetConstructionFundingLock bFlock) {
                String nameA = aFlock.getAppointmentFundingLockUser().getPersonName();
                String nameB = bFlock.getAppointmentFundingLockUser().getPersonName();
                return nameA.compareTo(nameB);
            }
        }); // TODO Can probably rip out once KualiUserService gets a comparator

        sortedFundingLocks.addAll(fundingLocks);
        return sortedFundingLocks;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockFunding(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockFunding(BudgetConstructionHeader bcHeader, String personUniversalIdentifier) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();

        if (!isAccountLocked(bcHeader)) {
            BudgetConstructionFundingLock budgetConstructionFundingLock = budgetConstructionDao.getByPrimaryId(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), personUniversalIdentifier);
            if (budgetConstructionFundingLock != null && budgetConstructionFundingLock.getAppointmentFundingLockUserId().equals(personUniversalIdentifier)) {
                bcLockStatus.setLockStatus(LockStatus.SUCCESS);
            }
            else {
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
                if (isAccountLocked(bcHeader)) { // unlikely, but need to check this
                    bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                    unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), personUniversalIdentifier);
                }
                else {
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                }
            }
        }
        else {
            bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
        }
        return bcLockStatus;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockFunding(java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.Integer, java.lang.String)
     */
    @Transactional
    public LockStatus unlockFunding(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier) {

        LockStatus lockStatus;

        BudgetConstructionFundingLock budgetConstructionFundingLock = budgetConstructionDao.getByPrimaryId(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, personUniversalIdentifier);
        if (budgetConstructionFundingLock != null) {
            budgetConstructionDao.deleteBudgetConstructionFundingLock(budgetConstructionFundingLock);
            lockStatus = LockStatus.SUCCESS;
        }
        else {
            lockStatus = LockStatus.NO_DOOR; // target not found
        }
        return lockStatus;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockFunding(java.util.List, org.kuali.core.bo.user.UniversalUser)
     */
    @Transactional
    public void unlockFunding(List<PendingBudgetConstructionAppointmentFunding> lockedFundings, UniversalUser universalUser) {
        for (PendingBudgetConstructionAppointmentFunding funding : lockedFundings) {
            Integer fiscalYear = funding.getUniversityFiscalYear();
            String chartOfAccountsCode = funding.getChartOfAccountsCode();
            String objectCode = funding.getFinancialObjectCode();
            String accountNumber = funding.getAccountNumber();
            String subAccountNumber = funding.getSubAccountNumber();

            this.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, universalUser.getPersonUserIdentifier());
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isFundingLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isFundingLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier) {
        BudgetConstructionFundingLock budgetConstructionFundingLock = budgetConstructionDao.getByPrimaryId(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, personUserIdentifier);
        if (budgetConstructionFundingLock != null) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockPosition(java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockPosition(String positionNumber, Integer fiscalYear, String personUniversalIdentifier) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        BudgetConstructionPosition bcPosition = budgetConstructionDao.getByPrimaryId(positionNumber, fiscalYear);
        if (bcPosition != null) {
            if (bcPosition.getPositionLockUserIdentifier() == null) {
                bcPosition.setPositionLockUserIdentifier(personUniversalIdentifier);
                try {
                    budgetConstructionDao.saveBudgetConstructionPosition(bcPosition);
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                }
                catch (DataAccessException ex) {
                    bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX);
                }
                return bcLockStatus;
            }
            else {
                if (bcPosition.getPositionLockUserIdentifier().equals(personUniversalIdentifier)) {
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                    return bcLockStatus; // the user already has a lock
                }
                else {
                    bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                    bcLockStatus.setPositionLockOwner(bcPosition.getPositionLockUserIdentifier());
                    return bcLockStatus; // someone else has a lock
                }
            }
        }
        else {
            bcLockStatus.setLockStatus(LockStatus.NO_DOOR);
            return bcLockStatus; // position not found
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isPositionLocked(java.lang.String, java.lang.Integer)
     */
    @Transactional
    public boolean isPositionLocked(String positionNumber, Integer fiscalYear) {

        BudgetConstructionPosition bcPosition = budgetConstructionDao.getByPrimaryId(positionNumber, fiscalYear);
        if (bcPosition != null) {
            if (bcPosition.getPositionLockUserIdentifier() != null) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false; // TODO should return not found?
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isPositionLockedByUser(java.lang.String, java.lang.Integer,
     *      java.lang.String)
     */
    @Transactional
    public boolean isPositionLockedByUser(String positionNumber, Integer fiscalYear, String personUserIdentifier) {
        BudgetConstructionPosition bcPosition = budgetConstructionDao.getByPrimaryId(positionNumber, fiscalYear);
        if (bcPosition != null && bcPosition.getPositionLockUserIdentifier() != null && bcPosition.getPositionLockUserIdentifier().equals(personUserIdentifier)) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isPositionFundingLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isPositionFundingLockedByUser(String positionNumber, String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier) {
        return this.isPositionLockedByUser(positionNumber, fiscalYear, personUserIdentifier) && this.isFundingLockedByUser(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, personUserIdentifier);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockPosition(java.lang.String, java.lang.Integer)
     */
    @Transactional
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear) {

        LockStatus lockStatus;

        BudgetConstructionPosition bcPosition = budgetConstructionDao.getByPrimaryId(positionNumber, fiscalYear);
        if (bcPosition != null) {
            if (bcPosition.getPositionLockUserIdentifier() != null) {
                bcPosition.setPositionLockUserIdentifier(null);
                try {
                    budgetConstructionDao.saveBudgetConstructionPosition(bcPosition);
                    lockStatus = LockStatus.SUCCESS;
                }
                catch (DataAccessException ex) {
                    lockStatus = LockStatus.OPTIMISTIC_EX;
                }
            }
            else {
                lockStatus = LockStatus.SUCCESS; // already unlocked
            }
        }
        else {
            lockStatus = LockStatus.NO_DOOR; // target not found
        }
        return lockStatus;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockPosition(java.lang.String, java.lang.Integer,
     *      java.lang.String)
     */
    @Transactional
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear, String personUniversalIdentifier) {
        BudgetConstructionPosition bcPosition = budgetConstructionPositionService.getLockedPositionByPrimaryId(fiscalYear, positionNumber, personUniversalIdentifier);
        if (bcPosition != null) {
            try {
                bcPosition.setPositionLockUserIdentifier(null);
                budgetConstructionDao.saveBudgetConstructionPosition(bcPosition);

                return LockStatus.SUCCESS;
            }
            catch (DataAccessException ex) {
                return LockStatus.OPTIMISTIC_EX;
            }
        }
        else {
            return LockStatus.NO_DOOR; // target not found
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockPostion(java.util.List, org.kuali.core.bo.user.UniversalUser)
     */
    @Transactional
    public void unlockPostion(List<BudgetConstructionPosition> lockedPositions, UniversalUser universalUser) {
        for (BudgetConstructionPosition position : lockedPositions) {
            Integer fiscalYear = position.getUniversityFiscalYear();
            String positionNumber = position.getPositionNumber();

            this.unlockPosition(positionNumber, fiscalYear, universalUser.getPersonUserIdentifier());
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockTransaction(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier) {

        int lockRetry = 1;
        boolean done = false;

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        while (!done) {
            BudgetConstructionHeader bcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
            if (bcHeader != null) {
                if (bcHeader.getBudgetTransactionLockUserIdentifier() == null) {
                    bcHeader.setBudgetTransactionLockUserIdentifier(personUniversalIdentifier);
                    try {
                        budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                        bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                    }
                    catch (DataAccessException ex) {
                        bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX); // unlikely
                    }
                    done = true;
                }
                else {
                    if (lockRetry > BudgetConstructionConstants.maxLockRetry) {
                        bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                        bcLockStatus.setTransactionLockOwner(bcHeader.getBudgetTransactionLockUserIdentifier());
                        done = true;
                    }
                    lockRetry++; // someone else has a lock, retry
                }
            }
            else {
                bcLockStatus.setLockStatus(LockStatus.NO_DOOR); // target not found, unlikely
                done = true;
            }
        }
        return bcLockStatus;
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockTransaction(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding, org.kuali.core.bo.user.UniversalUser)
     */
    @Transactional
    public BudgetConstructionLockStatus lockTransaction(PendingBudgetConstructionAppointmentFunding appointmentFunding, UniversalUser universalUser) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String personUserIdentifier = universalUser.getPersonUserIdentifier();
        
        return this.lockTransaction(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, personUserIdentifier);        
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isTransactionLocked(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    @Transactional
    public boolean isTransactionLocked(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {

        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (freshBcHeader != null) {
            if (freshBcHeader.getBudgetTransactionLockUserIdentifier() != null) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false; // TODO should return not found
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isTransactionLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isTransactionLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUserIdentifier) {
        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (freshBcHeader != null && freshBcHeader.getBudgetTransactionLockUserIdentifier() != null && freshBcHeader.getBudgetTransactionLockUserIdentifier().equals(personUserIdentifier)) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockTransaction(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    @Transactional
    public LockStatus unlockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {

        LockStatus lockStatus = LockStatus.NO_DOOR;

        BudgetConstructionHeader bcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (bcHeader != null) {
            if (bcHeader.getBudgetTransactionLockUserIdentifier() != null) {
                bcHeader.setBudgetTransactionLockUserIdentifier(null);
                try {
                    budgetConstructionDao.saveBudgetConstructionHeader(bcHeader);
                    lockStatus = LockStatus.SUCCESS;
                }
                catch (DataAccessException ex) {
                    lockStatus = LockStatus.OPTIMISTIC_EX;
                }
            }
            else {
                lockStatus = LockStatus.SUCCESS; // already unlocked
            }
        }
        else {
            lockStatus = LockStatus.NO_DOOR; // target not found
        }
        return lockStatus;
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockTransaction(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding, org.kuali.core.bo.user.UniversalUser)
     */
    public void unlockTransaction(PendingBudgetConstructionAppointmentFunding appointmentFunding, UniversalUser universalUser) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String personUserIdentifier = universalUser.getPersonUserIdentifier();
        
        if(this.isTransactionLockedByUser(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, personUserIdentifier)) {
            this.unlockTransaction(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#getAllAccountLocks(String lockUserId)
     */
    @Transactional
    public List<BudgetConstructionHeader> getAllAccountLocks(String lockUserId) {
        return budgetConstructionLockDao.getAllAccountLocks(lockUserId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#getAllFundLocks(String lockUserId)
     */
    @Transactional
    public List<BudgetConstructionFundingLock> getOrphanedFundingLocks(String lockUserId) {
        return budgetConstructionLockDao.getOrphanedFundingLocks(lockUserId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#getOrphanedPositionLocks(String lockUserId)
     */
    @Transactional
    public List<BudgetConstructionPosition> getOrphanedPositionLocks(String lockUserId) {
        return budgetConstructionLockDao.getOrphanedPositionLocks(lockUserId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#getAllTransactionLocks(String lockUserId)
     */
    @Transactional
    public List<BudgetConstructionHeader> getAllTransactionLocks(String lockUserId) {
        return budgetConstructionLockDao.getAllTransactionLocks(lockUserId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#getAllPositionFundingLocks(java.lang.String)
     */
    @Transactional
    public List<PendingBudgetConstructionAppointmentFunding> getAllPositionFundingLocks(String lockUserId) {
        return budgetConstructionLockDao.getAllPositionFundingLocks(lockUserId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#checkLockExists(org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary)
     */
    @Transactional
    public boolean checkLockExists(BudgetConstructionLockSummary lockSummary) {
        String lockType = lockSummary.getLockType();

        if (BCConstants.LockTypes.ACCOUNT_LOCK.equals(lockType)) {
            return this.isAccountLockedByUser(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPersonUniversalIdentifier());
        }

        if (BCConstants.LockTypes.TRANSACTION_LOCK.equals(lockType)) {
            return this.isTransactionLockedByUser(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPersonUniversalIdentifier());
        }

        if (BCConstants.LockTypes.FUNDING_LOCK.equals(lockType)) {
            return this.isFundingLockedByUser(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPersonUniversalIdentifier());
        }

        if (BCConstants.LockTypes.POSITION_LOCK.equals(lockType)) {
            return this.isPositionLockedByUser(lockSummary.getPositionNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPersonUniversalIdentifier());
        }

        if (BCConstants.LockTypes.POSITION_FUNDING_LOCK.equals(lockType)) {
            return this.isPositionFundingLockedByUser(lockSummary.getPositionNumber(), lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPersonUniversalIdentifier());
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#doUnlock(org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary)
     */
    @Transactional
    public LockStatus doUnlock(BudgetConstructionLockSummary lockSummary) {
        String lockType = lockSummary.getLockType();

        if (BCConstants.LockTypes.ACCOUNT_LOCK.equals(lockType)) {
            BudgetConstructionHeader bcHeader = budgetConstructionDao.getByCandidateKey(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear());
            if (bcHeader != null) {
                return this.unlockAccount(bcHeader);
            }
        }

        if (BCConstants.LockTypes.TRANSACTION_LOCK.equals(lockType)) {
            return this.unlockTransaction(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear());
        }

        if (BCConstants.LockTypes.FUNDING_LOCK.equals(lockType)) {
            return this.unlockFunding(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPersonUniversalIdentifier());
        }

        if (BCConstants.LockTypes.POSITION_LOCK.equals(lockType)) {
            return this.unlockPosition(lockSummary.getPositionNumber(), lockSummary.getUniversityFiscalYear());
        }

        if (BCConstants.LockTypes.POSITION_FUNDING_LOCK.equals(lockType)) {
            BudgetConstructionPosition position = budgetConstructionDao.getByPrimaryId(lockSummary.getPositionNumber(), lockSummary.getUniversityFiscalYear());
            for (PendingBudgetConstructionAppointmentFunding appointmentFunding : position.getPendingBudgetConstructionAppointmentFunding()) {
                this.unlockFunding(appointmentFunding.getChartOfAccountsCode(), appointmentFunding.getAccountNumber(), appointmentFunding.getSubAccountNumber(), appointmentFunding.getUniversityFiscalYear(), lockSummary.getLockUser().getPersonUniversalIdentifier());
            }

            return this.unlockPosition(position.getPositionNumber(), position.getUniversityFiscalYear());
        }

        return LockStatus.NO_DOOR;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isAccountLockedByUser(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Transactional
    public boolean isAccountLockedByUser(BudgetConstructionHeader budgetConstructionHeader, UniversalUser universalUser) {
        String chartOfAccountsCode = budgetConstructionHeader.getChartOfAccountsCode();
        String accountNumber = budgetConstructionHeader.getAccountNumber();
        String subAccountNumber = budgetConstructionHeader.getSubAccountNumber();
        Integer fiscalYear = budgetConstructionHeader.getUniversityFiscalYear();
        return this.isAccountLockedByUser(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, universalUser.getPersonUserIdentifier());
    }

    @NonTransactional
    public void setBudgetConstructionDao(BudgetConstructionDao bcHeaderDao) {
        this.budgetConstructionDao = bcHeaderDao;
    }

    /**
     * Sets the budgetConstructionLockDao attribute value.
     * 
     * @param budgetConstructionLockDao The budgetConstructionLockDao to set.
     */
    @NonTransactional
    public void setBudgetConstructionLockDao(BudgetConstructionLockDao budgetConstructionLockDao) {
        this.budgetConstructionLockDao = budgetConstructionLockDao;
    }

    /**
     * Sets the budgetDocumentService attribute value.
     * 
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    @NonTransactional
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockPendingBudgetConstructionAppointmentFundingRecords(java.util.List,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<PendingBudgetConstructionAppointmentFunding> lockPendingBudgetConstructionAppointmentFundingRecords(List<PendingBudgetConstructionAppointmentFunding> fundingRecords, UniversalUser user) throws BudgetConstructionLockUnavailableException {
        List<PendingBudgetConstructionAppointmentFunding> lockedFundingRecords = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        Map<String, PendingBudgetConstructionAppointmentFunding> lockMap = new HashMap<String, PendingBudgetConstructionAppointmentFunding>();

        for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {
            BudgetConstructionHeader header = budgetDocumentService.getBudgetConstructionHeader(fundingRecord);
            String lockingKey = fundingRecord.getUniversityFiscalYear() + "-" + fundingRecord.getChartOfAccountsCode() + "-" + fundingRecord.getAccountNumber() + "-" + fundingRecord.getSubAccountNumber();
            if (!lockMap.containsKey(lockingKey)) {
                BudgetConstructionLockStatus lockStatus = lockAccount(header, user.getPersonUniversalIdentifier());
                if (lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.BY_OTHER)) {
                    throw new BudgetConstructionLockUnavailableException(lockStatus);
                }
                else if (lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.FLOCK_FOUND)) {
                    throw new BudgetConstructionLockUnavailableException(lockStatus);
                }
                else if (!lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.SUCCESS)) {
                    throw new BudgetConstructionLockUnavailableException(lockStatus);
                }
                else {
                    lockMap.put(lockingKey, fundingRecord);
                    lockedFundingRecords.add(fundingRecord);
                }
            }
        }

        return lockedFundingRecords;
    }

    /**
     * Sets the budgetConstructionPositionService attribute value.
     * 
     * @param budgetConstructionPositionService The budgetConstructionPositionService to set.
     */
    @NonTransactional
    public void setBudgetConstructionPositionService(BudgetConstructionPositionService budgetConstructionPositionService) {
        this.budgetConstructionPositionService = budgetConstructionPositionService;
    }


    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockAccountAndCommit(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      java.lang.String)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BudgetConstructionLockStatus lockAccountAndCommit(BudgetConstructionHeader bcHeader, String personUniversalIdentifier) {
        return lockAccount(bcHeader, personUniversalIdentifier);
    }
}
