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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCConstants.LockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionLockDao;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.exception.BudgetConstructionLockUnavailableException;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.BusinessObjectService;
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

    protected BudgetConstructionDao budgetConstructionDao;
    protected BudgetConstructionLockDao budgetConstructionLockDao;
    protected BudgetDocumentService budgetDocumentService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockAccount(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockAccount(BudgetConstructionHeader bcHeader, String principalId) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        if (bcHeader != null) {
            bcLockStatus.setBudgetConstructionHeader(bcHeader);
            if (bcHeader.getBudgetLockUserIdentifier() == null) {
                bcHeader.setBudgetLockUserIdentifier(principalId);
                try {
                    SpringContext.getBean(BusinessObjectService.class).save(bcHeader);
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
                if (bcHeader.getBudgetLockUserIdentifier().equals(principalId)) {
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
     * @see org.kuali.kfs.module.bc.document.service.LockService#isAccountLocked(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @Transactional
    public boolean isAccountLocked(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionHeader budgetConstructionHeader = budgetDocumentService.getBudgetConstructionHeader(appointmentFunding);

        return this.isAccountLocked(budgetConstructionHeader);
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
            return false; // unlikely, but not found still means not locked
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isAccountLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isAccountLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId) {
        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (freshBcHeader != null) {
            if (freshBcHeader.getBudgetLockUserIdentifier() != null && freshBcHeader.getBudgetLockUserIdentifier().equals(principalId)) {
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
                    SpringContext.getBean(BusinessObjectService.class).save(bcHeader);
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
                String nameA = aFlock.getAppointmentFundingLockUser().getName();
                String nameB = bFlock.getAppointmentFundingLockUser().getName();
                return nameA.compareTo(nameB);
            }
        });

        sortedFundingLocks.addAll(fundingLocks);
        return sortedFundingLocks;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockFunding(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockFunding(BudgetConstructionHeader bcHeader, String principalId) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();

        if (!isAccountLocked(bcHeader)) {
            Map<String, Object> keys = new HashMap<String, Object>();
            keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bcHeader.getChartOfAccountsCode());
            keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, bcHeader.getAccountNumber());
            keys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, bcHeader.getSubAccountNumber());
            keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, bcHeader.getUniversityFiscalYear());
            keys.put("appointmentFundingLockUserId", principalId);
            BudgetConstructionFundingLock budgetConstructionFundingLock = (BudgetConstructionFundingLock)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionFundingLock.class, keys);
            
            if (budgetConstructionFundingLock != null && budgetConstructionFundingLock.getAppointmentFundingLockUserId().equals(principalId)) {
                bcLockStatus.setLockStatus(LockStatus.SUCCESS);
            }
            else {
                budgetConstructionFundingLock = new BudgetConstructionFundingLock();
                budgetConstructionFundingLock.setAppointmentFundingLockUserId(principalId);
                budgetConstructionFundingLock.setAccountNumber(bcHeader.getAccountNumber());
                budgetConstructionFundingLock.setSubAccountNumber(bcHeader.getSubAccountNumber());
                budgetConstructionFundingLock.setChartOfAccountsCode(bcHeader.getChartOfAccountsCode());
                budgetConstructionFundingLock.setUniversityFiscalYear(bcHeader.getUniversityFiscalYear());
                budgetConstructionFundingLock.setFill1("L");
                budgetConstructionFundingLock.setFill2("L");
                budgetConstructionFundingLock.setFill3("L");
                budgetConstructionFundingLock.setFill4("L");
                budgetConstructionFundingLock.setFill5("L");
                SpringContext.getBean(BusinessObjectService.class).save(budgetConstructionFundingLock);
                if (isAccountLocked(bcHeader)) { // unlikely, but need to check this
                    bcLockStatus.setLockStatus(LockStatus.BY_OTHER);
                    unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), principalId);
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
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @NonTransactional
    public BudgetConstructionLockStatus lockFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person) {
        BudgetConstructionHeader budgetConstructionHeader = budgetDocumentService.getBudgetConstructionHeader(appointmentFunding);

        return this.lockFunding(budgetConstructionHeader, person.getPrincipalId());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockFunding(java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.Integer, java.lang.String)
     */
    @Transactional
    public LockStatus unlockFunding(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId) {

        LockStatus lockStatus;
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        keys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        keys.put("appointmentFundingLockUserId", principalId);
        BudgetConstructionFundingLock budgetConstructionFundingLock = (BudgetConstructionFundingLock)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionFundingLock.class, keys);
        
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
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public LockStatus unlockFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person) {
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String objectCode = appointmentFunding.getFinancialObjectCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();

        return this.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, person.getPrincipalId());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockFunding(java.util.List, org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public void unlockFunding(List<PendingBudgetConstructionAppointmentFunding> lockedFundings, Person person) {
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : lockedFundings) {
            this.unlockFunding(appointmentFunding, person);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isFundingLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isFundingLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        keys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        keys.put("appointmentFundingLockUserId", principalId);
        BudgetConstructionFundingLock budgetConstructionFundingLock = (BudgetConstructionFundingLock)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionFundingLock.class, keys);
        
        if (budgetConstructionFundingLock != null) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockPosition(java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockPosition(String positionNumber, Integer fiscalYear, String principalId) {

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        BudgetConstructionPosition bcPosition = (BudgetConstructionPosition)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, keys);
 
        if (bcPosition != null) {
            if (bcPosition.getPositionLockUserIdentifier() == null) {
                bcPosition.setPositionLockUserIdentifier(principalId);
                try {
                    SpringContext.getBean(BusinessObjectService.class).save(bcPosition);
                    bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                }
                catch (DataAccessException ex) {
                    bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX);
                }
                return bcLockStatus;
            }
            else {
                if (bcPosition.getPositionLockUserIdentifier().equals(principalId)) {
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
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockPosition(org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public BudgetConstructionLockStatus lockPosition(BudgetConstructionPosition position, Person person) {
        String positionNumber = position.getPositionNumber();
        Integer fiscalYear = position.getUniversityFiscalYear();
        String principalId = person.getPrincipalId();

        return this.lockPosition(positionNumber, fiscalYear, principalId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isPositionLocked(java.lang.String, java.lang.Integer)
     */
    @Transactional
    public boolean isPositionLocked(String positionNumber, Integer fiscalYear) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        BudgetConstructionPosition bcPosition = (BudgetConstructionPosition)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, keys);

        if (bcPosition != null) {
            if (bcPosition.getPositionLockUserIdentifier() != null) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false; // unlikely, but still means not locked
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isPositionLockedByUser(java.lang.String, java.lang.Integer,
     *      java.lang.String)
     */
    @Transactional
    public boolean isPositionLockedByUser(String positionNumber, Integer fiscalYear, String principalId) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        BudgetConstructionPosition bcPosition = (BudgetConstructionPosition)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, keys);
        
        if (bcPosition != null && bcPosition.getPositionLockUserIdentifier() != null && bcPosition.getPositionLockUserIdentifier().equals(principalId)) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isPositionFundingLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isPositionFundingLockedByUser(String positionNumber, String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId) {
        return this.isPositionLockedByUser(positionNumber, fiscalYear, principalId) && this.isFundingLockedByUser(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, principalId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockPosition(java.lang.String, java.lang.Integer)
     */
    @Transactional
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear) {

        LockStatus lockStatus;
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        BudgetConstructionPosition bcPosition = (BudgetConstructionPosition)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, keys);
        if (bcPosition != null) {
            if (bcPosition.getPositionLockUserIdentifier() != null) {
                bcPosition.setPositionLockUserIdentifier(null);
                try {
                    SpringContext.getBean(BusinessObjectService.class).save(bcPosition);
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
    public LockStatus unlockPosition(String positionNumber, Integer fiscalYear, String principalId) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        BudgetConstructionPosition bcPosition = (BudgetConstructionPosition)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, keys);

        if (bcPosition == null || !principalId.equals(bcPosition.getPositionLockUserIdentifier())) {
            return LockStatus.NO_DOOR;
        }

        try {
            bcPosition.setPositionLockUserIdentifier(null);
            SpringContext.getBean(BusinessObjectService.class).save(bcPosition);

            return LockStatus.SUCCESS;
        }
        catch (DataAccessException ex) {
            return LockStatus.OPTIMISTIC_EX;
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockPostion(org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public LockStatus unlockPostion(BudgetConstructionPosition position, Person person) {
        Integer fiscalYear = position.getUniversityFiscalYear();
        String positionNumber = position.getPositionNumber();

        return this.unlockPosition(positionNumber, fiscalYear, person.getPrincipalId());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockPostion(java.util.List, org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public void unlockPostion(List<BudgetConstructionPosition> lockedPositions, Person person) {
        for (BudgetConstructionPosition position : lockedPositions) {
            this.unlockPostion(position, person);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockTransaction(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public BudgetConstructionLockStatus lockTransaction(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId) {

        int lockRetry = 1;
        boolean done = false;

        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();

        // gwp - 12/9/2008 Adding extra if test to handle a current transaction lock by the user
        // as a successful lock even though this is not how Uniface handled it.
        // The old FIS kept track of the issued locks and only called this once
        // even when multiple BCAF rows were being updated and saved
        if (this.isTransactionLockedByUser(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, principalId)) {
            bcLockStatus.setLockStatus(LockStatus.SUCCESS);
            done = true;
        }

        while (!done) {
            BudgetConstructionHeader bcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
            if (bcHeader != null) {
                if (bcHeader.getBudgetTransactionLockUserIdentifier() == null) {
                    bcHeader.setBudgetTransactionLockUserIdentifier(principalId);
                    try {
                        SpringContext.getBean(BusinessObjectService.class).save(bcHeader);
                        bcLockStatus.setLockStatus(LockStatus.SUCCESS);
                    }
                    catch (DataAccessException ex) {
                        bcLockStatus.setLockStatus(LockStatus.OPTIMISTIC_EX); // unlikely
                    }
                    done = true;
                }
                else {
                    if (lockRetry > BCConstants.maxLockRetry) {
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
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockTransaction(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public BudgetConstructionLockStatus lockTransaction(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();

        return this.lockTransaction(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, person.getPrincipalId());
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
            return false; // unlikely, but still means not locked
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isTransactionLockedByUser(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Transactional
    public boolean isTransactionLockedByUser(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String principalId) {
        BudgetConstructionHeader freshBcHeader = budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
        if (freshBcHeader != null && freshBcHeader.getBudgetTransactionLockUserIdentifier() != null && freshBcHeader.getBudgetTransactionLockUserIdentifier().equals(principalId)) {
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
                    SpringContext.getBean(BusinessObjectService.class).save(bcHeader);
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
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockTransaction(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public void unlockTransaction(PendingBudgetConstructionAppointmentFunding appointmentFunding, Person person) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String principalId = person.getPrincipalId();

        if (this.isTransactionLockedByUser(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, principalId)) {
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
            return this.isAccountLockedByUser(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPrincipalId());
        }

        if (BCConstants.LockTypes.TRANSACTION_LOCK.equals(lockType)) {
            return this.isTransactionLockedByUser(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPrincipalId());
        }

        if (BCConstants.LockTypes.FUNDING_LOCK.equals(lockType)) {
            return this.isFundingLockedByUser(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPrincipalId());
        }

        if (BCConstants.LockTypes.POSITION_LOCK.equals(lockType)) {
            return this.isPositionLockedByUser(lockSummary.getPositionNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPrincipalId());
        }

        if (BCConstants.LockTypes.POSITION_FUNDING_LOCK.equals(lockType)) {
            return this.isPositionFundingLockedByUser(lockSummary.getPositionNumber(), lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPrincipalId());
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
            return this.unlockFunding(lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getLockUser().getPrincipalId());
        }

        if (BCConstants.LockTypes.POSITION_LOCK.equals(lockType)) {
            return this.unlockPosition(lockSummary.getPositionNumber(), lockSummary.getUniversityFiscalYear());
        }

        if (BCConstants.LockTypes.POSITION_FUNDING_LOCK.equals(lockType)) {
            Map<String, Object> keys = new HashMap<String, Object>();
            keys.put(KFSPropertyConstants.POSITION_NUMBER, lockSummary.getPositionNumber());
            keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, lockSummary.getUniversityFiscalYear());
            BudgetConstructionPosition position = (BudgetConstructionPosition)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, keys);
     
            for (PendingBudgetConstructionAppointmentFunding appointmentFunding : position.getPendingBudgetConstructionAppointmentFunding()) {
                this.unlockFunding(appointmentFunding.getChartOfAccountsCode(), appointmentFunding.getAccountNumber(), appointmentFunding.getSubAccountNumber(), appointmentFunding.getUniversityFiscalYear(), lockSummary.getLockUser().getPrincipalId());
            }

            return this.unlockPosition(position.getPositionNumber(), position.getUniversityFiscalYear());
        }

        return LockStatus.NO_DOOR;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#isAccountLockedByUser(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional
    public boolean isAccountLockedByUser(BudgetConstructionHeader budgetConstructionHeader, Person person) {
        String chartOfAccountsCode = budgetConstructionHeader.getChartOfAccountsCode();
        String accountNumber = budgetConstructionHeader.getAccountNumber();
        String subAccountNumber = budgetConstructionHeader.getSubAccountNumber();
        Integer fiscalYear = budgetConstructionHeader.getUniversityFiscalYear();
        return this.isAccountLockedByUser(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear, person.getPrincipalId());
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
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<PendingBudgetConstructionAppointmentFunding> lockPendingBudgetConstructionAppointmentFundingRecords(List<PendingBudgetConstructionAppointmentFunding> fundingRecords, Person user) throws BudgetConstructionLockUnavailableException {
        List<PendingBudgetConstructionAppointmentFunding> lockedFundingRecords = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        Map<String, PendingBudgetConstructionAppointmentFunding> lockMap = new HashMap<String, PendingBudgetConstructionAppointmentFunding>();

        for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {
            BudgetConstructionHeader header = budgetDocumentService.getBudgetConstructionHeader(fundingRecord);
            String lockingKey = fundingRecord.getUniversityFiscalYear() + "-" + fundingRecord.getChartOfAccountsCode() + "-" + fundingRecord.getAccountNumber() + "-" + fundingRecord.getSubAccountNumber();
            if (!lockMap.containsKey(lockingKey)) {
                BudgetConstructionLockStatus lockStatus = lockAccount(header, user.getPrincipalId());
                if (lockStatus.getLockStatus().equals(BCConstants.LockStatus.BY_OTHER)) {
                    throw new BudgetConstructionLockUnavailableException(lockStatus);
                }
                else if (lockStatus.getLockStatus().equals(BCConstants.LockStatus.FLOCK_FOUND)) {
                    throw new BudgetConstructionLockUnavailableException(lockStatus);
                }
                else if (!lockStatus.getLockStatus().equals(BCConstants.LockStatus.SUCCESS)) {
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
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockAccountAndCommit(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      java.lang.String)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BudgetConstructionLockStatus lockAccountAndCommit(BudgetConstructionHeader bcHeader, String principalId) {
        return lockAccount(bcHeader, principalId);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#lockPositionAndActiveFunding(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BudgetConstructionLockStatus lockPositionAndActiveFunding(Integer universityFiscalYear, String positionNumber, String principalId) {
        // attempt to lock position first
        BudgetConstructionLockStatus lockStatus = lockPosition(positionNumber, universityFiscalYear, principalId);
        if (!lockStatus.getLockStatus().equals(BCConstants.LockStatus.SUCCESS)) {
            return lockStatus;
        }

        // retrieve funding records for the position
        List<PendingBudgetConstructionAppointmentFunding> allPositionFunding = budgetConstructionDao.getAllFundingForPosition(universityFiscalYear, positionNumber);

        // lock funding if not marked as delete
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : allPositionFunding) {
            if (!appointmentFunding.isAppointmentFundingDeleteIndicator()) {
                BudgetConstructionHeader budgetConstructionHeader = budgetDocumentService.getBudgetConstructionHeader(appointmentFunding);
                BudgetConstructionLockStatus fundingLockStatus = lockFunding(budgetConstructionHeader, principalId);

                if (!fundingLockStatus.getLockStatus().equals(BCConstants.LockStatus.SUCCESS)) {
                    return lockStatus;
                }
            }
        }

        // successfully obtained all locks
        BudgetConstructionLockStatus bcLockStatus = new BudgetConstructionLockStatus();
        bcLockStatus.setLockStatus(LockStatus.SUCCESS);

        return bcLockStatus;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.LockService#unlockPositionAndActiveFunding(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LockStatus unlockPositionAndActiveFunding(Integer universityFiscalYear, String positionNumber, String principalId) {
        // unlock position
        LockStatus lockStatus = unlockPosition(positionNumber, universityFiscalYear, principalId);
        if (!lockStatus.equals(BCConstants.LockStatus.SUCCESS)) {
            return lockStatus;
        }

        // retrieve funding records for the position
        List<PendingBudgetConstructionAppointmentFunding> allPositionFunding = budgetConstructionDao.getAllFundingForPosition(universityFiscalYear, positionNumber);

        // unlock funding if not marked as delete
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : allPositionFunding) {
            if (!appointmentFunding.isAppointmentFundingDeleteIndicator()) {
                LockStatus fundingLockStatus = unlockFunding(appointmentFunding.getChartOfAccountsCode(), appointmentFunding.getAccountNumber(), appointmentFunding.getSubAccountNumber(), appointmentFunding.getUniversityFiscalYear(), principalId);

                if (!fundingLockStatus.equals(BCConstants.LockStatus.SUCCESS)) {
                    return lockStatus;
                }
            }
        }

        // successfully completed unlocks
        return LockStatus.SUCCESS;
    }

}
