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
package org.kuali.module.budget.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import static org.kuali.kfs.util.SpringServiceLocator.getBusinessObjectService;
import static org.kuali.kfs.util.SpringServiceLocator.getLockService;

import org.kuali.Constants.BudgetConstructionConstants.LockStatus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.dao.ojb.BudgetConstructionDaoOjb;
import org.kuali.module.budget.service.impl.BudgetConstructionLockStatus;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;

/**
 * 
 * This class tests the Lock Service
 */
@WithTestSpringContext
public class LockServiceTest extends KualiTestBase {

    private boolean runTests() { // change this to return false to prevent running tests
        return false;
    }

    @TestsWorkflowViaDatabase
    public void testOne() {

        LockService lockService;
        DocumentHeader docHeader;
        BudgetConstructionDaoOjb bcHeaderDao;
        BudgetConstructionHeader bcHeader;
        BudgetConstructionHeader bcHeaderTwo;
        BudgetConstructionPosition bcPosition;
        PendingBudgetConstructionAppointmentFunding bcAFunding;
        BudgetConstructionLockStatus bcLockStatus;
        LockStatus lockStatus;
        SortedSet<BudgetConstructionFundingLock> fundingLocks;
        Iterator<BudgetConstructionFundingLock> fundingIter;
        BudgetConstructionFundingLock fundingLock;
        
        String fdocNumber = "1111111111";
        Integer orgLevelCode = new Integer(0);
        String chartOfAccountsCode = "UA";
        String accountNumber = "1912201" ;
        String subAccountNumber = "-----";
        String financialObjectCode = "2400";
        String financialSubObjectCode = "---";
        String emplid = "1111111111";
        Integer universityFiscalYear = new Integer(2007);
        String positionNumber = "00013304";
        String positionDesc = "DATABASE MGR.";
        String pUIdOne = "3670600494"; //MCGUIRE
        String pUIdTwo = "6162502038"; //KHUNTLEY
        boolean posExist = false;
        boolean hdrExist = false;
        boolean docHdrExist = false;
        boolean bcafExist = false;

        
        if (!runTests()) return;
        
        // do some setup and initialize the state of things
        lockService = getLockService();
        bcHeaderDao = new BudgetConstructionDaoOjb();
        
        docHeader = null;
        Map dockey = new HashMap();
        dockey.put("documentNumber", fdocNumber);
        docHeader = (DocumentHeader) getBusinessObjectService().findByPrimaryKey(DocumentHeader.class, dockey);
        if (docHeader == null){
            docHeader = new DocumentHeader();
            docHeader.setDocumentNumber(fdocNumber);
            getBusinessObjectService().save(docHeader);
        } else {
            docHdrExist = true;
        }

        bcHeader = null;
        bcHeader = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        if (bcHeader == null){
            bcHeader = new BudgetConstructionHeader();
            bcHeader.setDocumentNumber(fdocNumber);
            bcHeader.setAccountNumber(accountNumber);
            bcHeader.setSubAccountNumber(subAccountNumber);
            bcHeader.setChartOfAccountsCode(chartOfAccountsCode);
            bcHeader.setOrganizationLevelCode(orgLevelCode);
            bcHeader.setUniversityFiscalYear(universityFiscalYear);
            bcHeaderDao.saveBudgetConstructionHeader(bcHeader);
        } else {
            hdrExist = true;
            lockService.unlockAccount(bcHeader);
            lockService.unlockTransaction(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        }
        bcHeader = null;
        bcHeader = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        assertTrue(bcHeader.getAccountNumber().equals(accountNumber));
        

        bcPosition = null;
        bcPosition = bcHeaderDao.getByPrimaryId(positionNumber, universityFiscalYear);
        if (bcPosition == null){
            bcPosition = new BudgetConstructionPosition();
            bcPosition.setPositionNumber(positionNumber);
            bcPosition.setUniversityFiscalYear(universityFiscalYear);
            bcPosition.setPositionDescription(positionDesc);
            bcHeaderDao.saveBudgetConstructionPosition(bcPosition);
        } else {
            posExist = true;
            lockService.unlockPosition(positionNumber, universityFiscalYear);
        }
        bcPosition = null;
        bcPosition = bcHeaderDao.getByPrimaryId(positionNumber, universityFiscalYear);
        assertTrue(bcPosition.getPositionNumber().equals(positionNumber));
        
        
        bcAFunding = null;
        HashMap map = new HashMap();
        map.put("universityFiscalYear", universityFiscalYear);
        map.put("chartOfAccountsCode", chartOfAccountsCode);
        map.put("accountNumber", accountNumber);
        map.put("subAccountNumber", subAccountNumber);
        map.put("financialObjectCode", financialObjectCode);
        map.put("financialSubObjectCode", financialSubObjectCode);
        map.put("positionNumber", positionNumber);
        map.put("emplid", emplid);
        bcAFunding = (PendingBudgetConstructionAppointmentFunding) getBusinessObjectService().findByPrimaryKey(PendingBudgetConstructionAppointmentFunding.class, map);
        if (bcAFunding == null){
            bcAFunding = new PendingBudgetConstructionAppointmentFunding();
            bcAFunding.setUniversityFiscalYear(universityFiscalYear);
            bcAFunding.setChartOfAccountsCode(chartOfAccountsCode);
            bcAFunding.setAccountNumber(accountNumber);
            bcAFunding.setSubAccountNumber(subAccountNumber);
            bcAFunding.setFinancialObjectCode(financialObjectCode);
            bcAFunding.setFinancialSubObjectCode(financialSubObjectCode);
            bcAFunding.setPositionNumber(positionNumber);
            bcAFunding.setEmplid(emplid);
            getBusinessObjectService().save(bcAFunding);
        } else {
            bcafExist = true;    
        }
        
        //make sure funding locks we intend to use aren't there
        lockService.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear, pUIdOne);
        lockService.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear, pUIdTwo);

        // trivial account lock/unlock
        assertFalse(lockService.isAccountLocked(bcHeader));
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isAccountLocked(bcHeader));
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        lockService.unlockAccount(bcHeader);
        assertFalse(lockService.isAccountLocked(bcHeader));

        // account lock attempt with account lock set by other
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isAccountLocked(bcHeader));
        bcHeaderTwo = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        assertTrue(bcHeaderTwo.getAccountNumber().equals(accountNumber));
        bcLockStatus = lockService.lockAccount(bcHeaderTwo, pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue(bcLockStatus.getAccountLockOwner().equals(pUIdOne));
        assertTrue(lockService.isAccountLocked(bcHeaderTwo));

        // funding lock attempt with account lock set in previous test
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue(lockService.getFundingLocks(bcHeader).isEmpty());

        // account unlock by other - needs account lock in previous test
        // this tests opimistic lock exception catch
        // this configuration of the test must run in a test method that
        // is annotated as TestsWorkflowViaDatabase 
        lockService.unlockAccount(bcHeaderTwo);
        assertFalse(lockService.isAccountLocked(bcHeaderTwo));
        assertTrue(lockService.unlockAccount(bcHeader) == LockStatus.OPTIMISTIC_EX);
        assertFalse(lockService.isAccountLocked(bcHeader));
        
        // trivial funding lock/unlock
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertFalse(lockService.getFundingLocks(bcHeader).isEmpty());
        fundingLocks = lockService.getFundingLocks(bcHeader);
        fundingIter = fundingLocks.iterator();
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue(fundingLock.getPositionNumber().equals("NotFnd"));
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue(fundingLock.getPositionNumber().equals("NotFnd"));
        lockStatus = lockService.unlockFunding(
                bcHeader.getChartOfAccountsCode(),
                bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(),
                bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        lockStatus = lockService.unlockFunding(
                bcHeader.getChartOfAccountsCode(),
                bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(),
                bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertTrue(lockService.getFundingLocks(bcHeader).isEmpty());

        // account lock attempt with funding locks set
        // one funding lock has an associated position lock, the other is an orphan
        bcLockStatus = lockService.lockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertFalse(lockService.getFundingLocks(bcHeader).isEmpty());
        bcLockStatus = lockService.lockAccount(bcHeaderTwo, pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.FLOCK_FOUND);
        assertFalse(bcLockStatus.getFundingLocks().isEmpty());
        fundingIter = bcLockStatus.getFundingLocks().iterator();
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue(fundingLock.getPositionNumber().equals("NotFnd"));        //orphan
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue(fundingLock.getPositionNumber().equals(positionNumber));  //associated position
        assertFalse(lockService.isAccountLocked(bcHeaderTwo));
        lockStatus = lockService.unlockFunding(
                bcHeader.getChartOfAccountsCode(),
                bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(),
                bcHeader.getUniversityFiscalYear(),
                pUIdOne);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        lockStatus = lockService.unlockFunding(
                bcHeader.getChartOfAccountsCode(),
                bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(),
                bcHeader.getUniversityFiscalYear(),
                pUIdTwo);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertTrue(lockService.getFundingLocks(bcHeader).isEmpty());
        lockStatus = lockService.unlockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear());
        assertTrue(lockStatus == LockStatus.SUCCESS);
        
        // trivial position lock/unlock
        bcLockStatus = lockService.lockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isPositionLocked(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear()));
        lockStatus = lockService.unlockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear());
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isPositionLocked(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear()));
        
        // position lock attempt with position lock by other
        bcLockStatus = lockService.lockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue(lockService.isPositionLocked(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear()));
        lockStatus = lockService.unlockPosition(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear());
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isPositionLocked(bcPosition.getPositionNumber(),bcPosition.getUniversityFiscalYear()));
        
        // trivial transaction lock/unlock
        // this test bcHeader, but the application will probably derive the params from BCAppointmentFunding
        lockService.unlockTransaction(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertFalse(lockService.isTransactionLocked(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isTransactionLocked(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        lockStatus = lockService.unlockTransaction(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isTransactionLocked(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        
        // transaction lock attempt with transaction lock by other
        // this test uses bcHeader, but the application will probably derive the params from BCAppointmentFunding
        assertFalse(lockService.isTransactionLocked(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isTransactionLocked(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue(bcLockStatus.getTransactionLockOwner().equals(pUIdOne));
        assertTrue(lockService.isTransactionLocked(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        lockStatus = lockService.unlockTransaction(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isTransactionLocked(
                bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(),
                bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        
        // remove test objects from the database if they didn't exist
        if (!hdrExist) {
            bcHeader = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
            bcHeaderDao.getPersistenceBrokerTemplate().delete(bcHeader);
        }
        if (!docHdrExist){
            getBusinessObjectService().delete(docHeader);
        }
        if (!posExist) {
            bcPosition = bcHeaderDao.getByPrimaryId(positionNumber, universityFiscalYear);
            bcHeaderDao.getPersistenceBrokerTemplate().delete(bcPosition);
        }
        if (!bcafExist) {
            getBusinessObjectService().delete(bcAFunding);
        }
    }
}
