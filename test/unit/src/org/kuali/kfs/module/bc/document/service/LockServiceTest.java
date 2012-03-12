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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;

import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCConstants.LockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the Lock Service
 */
@ConfigureContext
public class LockServiceTest extends KualiTestBase {

    private LockService lockService;
    private BudgetConstructionDao bcHeaderDao;

    private FinancialSystemDocumentHeader docHeader;
    private BudgetConstructionHeader bcHeader;
    private BudgetConstructionHeader bcHeaderTwo;
    private BudgetConstructionPosition bcPosition;
    private PendingBudgetConstructionAppointmentFunding bcAFunding;
    private BudgetConstructionLockStatus bcLockStatus;
    private LockStatus lockStatus;
    private SortedSet<BudgetConstructionFundingLock> fundingLocks;
    Iterator<BudgetConstructionFundingLock> fundingIter;
    private BudgetConstructionFundingLock fundingLock;
    
    /*
     *   these values are filled in from the database, taking the first row that comes along.
     *   these fields are also static, so we don't have to return to the database for each of the tests.
     *   therefore, it follows that deleting data from the database while the tests are running could result
     *   in failed tests, even though nothing is wrong with the code itself.
     */
    private String fdocNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String emplid;
    private Integer universityFiscalYear;
    private String positionNumber;
    private  String pUIdOne = "3670600494"; // MCGUIRE
    private  String pUIdTwo = "6162502038"; // khuntley
    
    // set up some data for the tests.
    // we will run everything in one test method, so this only needs to be done once
    @Override
    public void setUp() throws Exception
    {
      super.setUp();  
      // get the services we need 
      lockService = SpringContext.getBean(LockService.class);
      bcHeaderDao = SpringContext.getBean(BudgetConstructionDao.class);
      if (!runTests())
          return;
      // find a test fiscal year 
      universityFiscalYear = setTestFiscalYear(); 
      assertTrue("Unable to obtain fiscal year",universityFiscalYear != 0);
      System.err.println( "Testing Fiscal Year: " + universityFiscalYear );
      // find a test funding row for this fiscal year. 
      assertTrue( "Unable to set test funding", setTestFunding() );
      // finally, get the parent document for this funding and position
      assertTrue( "Unable to set test document number", setTestDocumentNumber() );
    } 
    
    @Override
    public void tearDown() throws Exception
    {
        clearTestRowLocks();
        super.tearDown();
    }

    private boolean runTests() { // change this to return false to prevent running tests
        return false;
    }

    @ConfigureContext(shouldCommitTransactions = true)
    public void testOne() {
        if (!runTests())
            return;

        //
        // (the tests below will check that the unlock activity here took effect). 
        clearTestRowLocks();
        
        // trivial account lock/unlock
        assertFalse("test header was unlocked on initialization", lockService.isAccountLocked(bcHeader));
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue("account lock attempt on header succeeded", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue("test header has an account lock", lockService.isAccountLocked(bcHeader));
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue("test header locked successfully on second attempt", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        lockService.unlockAccount(bcHeader);
        assertFalse("unlock attempt on test header succeeded--no lock found", lockService.isAccountLocked(bcHeader));

        // account lock attempt with account lock set by other
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue("initial header lock by "+pUIdOne+" succeeded", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue("header locked by "+pUIdOne, lockService.isAccountLocked(bcHeader));
        // bcHeaderTwo is a different object representing the same budget construction header
        // it must be fetched AFTER the object is locked
        bcHeaderTwo = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        assertTrue("two objects pointing to the same header have the same account", bcHeaderTwo.getAccountNumber().equals(accountNumber));
        bcLockStatus = lockService.lockAccount(bcHeaderTwo, pUIdTwo);
        assertTrue(pUIdTwo+" could not get a lock on header already locked by "+pUIdOne, bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue("lock is owned by "+pUIdOne, bcLockStatus.getAccountLockOwner().equals(pUIdOne));
        assertTrue(pUIdTwo+"'s pointer to the test header row shows a lock", lockService.isAccountLocked(bcHeaderTwo));

        // funding lock attempt with account lock set in previous test
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue("failed funding lock attempt on a header with an existing acccount lock", bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue("no funding locks exist after failed attempt", lockService.getFundingLocks(bcHeader).isEmpty());

        // account unlock by other - needs account lock in previous test
        // this tests opimistic lock exception catch
        // this configuration of the test must run in a test method that
        // is annotated as ShouldCommitTransactions
        lockService.unlockAccount(bcHeaderTwo);
        assertFalse("account was unlocked successfully",lockService.isAccountLocked(bcHeaderTwo));
        assertTrue("unlock with a different object against the same row triggers an OJB optimistic lock exception", lockService.unlockAccount(bcHeader) == LockStatus.OPTIMISTIC_EX);
        assertFalse("the account is still unlocked", lockService.isAccountLocked(bcHeader));

        // trivial funding lock/unlock
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(pUIdOne+" obtained a funding lock", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(pUIdOne+"'s re-attempt to fetch the same lock also succeeded", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdTwo);
        assertTrue(pUIdTwo+" can also get a funding lock for the same accounting key",bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertFalse("the set of funding locks for this accounting key is not empty", lockService.getFundingLocks(bcHeader).isEmpty());
        fundingLocks = lockService.getFundingLocks(bcHeader);
        fundingIter = fundingLocks.iterator();
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue("the first funding lock is by accounting key, not by position",fundingLock.getPositionNumber().equals("NotFnd"));
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue("the second funding lock is also not by position", fundingLock.getPositionNumber().equals("NotFnd"));
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(pUIdOne+"'s funding lock was successfully removed", lockStatus == LockStatus.SUCCESS);
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(pUIdTwo+"'s funding lock was successfully removed", lockStatus == LockStatus.SUCCESS);
        assertTrue("there are no remaining funding locks for this accounting key", lockService.getFundingLocks(bcHeader).isEmpty());

        // account lock attempt with funding locks set
        // one funding lock has an associated position lock, the other is an orphan
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue(pUIdOne+" successfully obtained a position lock",bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(pUIdOne+" successfully obtained a funding lock on an account funding the position",bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdTwo);
        assertTrue(pUIdTwo+" successfully obtained an orphan funding lock--no position lock is involved",bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertFalse("the funding lock table has rows for the account specified by this header",lockService.getFundingLocks(bcHeader).isEmpty());
        bcLockStatus = lockService.lockAccount(bcHeaderTwo, pUIdTwo);
        assertTrue(pUIdTwo+" cannot lock an accounting key for which this user has a funding lock",bcLockStatus.getLockStatus() == LockStatus.FLOCK_FOUND);
        assertFalse("there are no funding locks involving this accounting key",bcLockStatus.getFundingLocks().isEmpty());
        fundingIter = bcLockStatus.getFundingLocks().iterator();
        assertTrue("an orphan funding lock exists",fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue("funding lock is marked as an orphan",fundingLock.getPositionNumber().equals("NotFnd")); // orphan
        assertTrue("a funding lock exists with an associated position",fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue(positionNumber+" has a funding lock",fundingLock.getPositionNumber().equals(positionNumber)); // associated position
        assertFalse(pUIdTwo+" does not have an account lock",lockService.isAccountLocked(bcHeaderTwo));
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(pUIdOne+" successfully released a funding lock", lockStatus == LockStatus.SUCCESS);
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(pUIdTwo+" successfully released a funding lock",lockStatus == LockStatus.SUCCESS);
        assertTrue("no funding locks are left",lockService.getFundingLocks(bcHeader).isEmpty());
        lockStatus = lockService.unlockPosition(positionNumber, universityFiscalYear);
        assertTrue(positionNumber+" lock was released successfully", lockStatus == LockStatus.SUCCESS);

        // trivial position lock/unlock
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue("position lock: lock obtained by "+pUIdOne, bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue("position lock: successful re-lock attempt by "+pUIdOne, bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue("position lock: "+positionNumber+" is locked", lockService.isPositionLocked(positionNumber, universityFiscalYear));
        lockStatus = lockService.unlockPosition(positionNumber, universityFiscalYear);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse("position lock: "+positionNumber+" successfully unlocked", lockService.isPositionLocked(positionNumber, universityFiscalYear));

        // position lock attempt with position lock by other
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue("position lock conflict: position lock obtained by "+pUIdOne,bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdTwo);
        assertTrue("position lock conflict: position lock denied to "+pUIdTwo, bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue("position lock conflict: position is still locked",lockService.isPositionLocked(positionNumber, universityFiscalYear));
        lockStatus = lockService.unlockPosition(positionNumber, universityFiscalYear);
        assertTrue("position lock conflict: position lock successfully released", lockStatus == LockStatus.SUCCESS);
        assertFalse("position lock conflict: no positions locks remain", lockService.isPositionLocked(positionNumber, universityFiscalYear));

        // trivial transaction lock/unlock
        // this test bcHeader, but the application will probably derive the params from BCAppointmentFunding
        lockService.unlockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertFalse("transaction lock: no current locks", lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue("transaction lock: obtained by "+pUIdOne, bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue("transaction lock: in effect", lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        lockStatus = lockService.unlockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertTrue("transaction lock: successfully released by "+pUIdOne, lockStatus == LockStatus.SUCCESS);
        assertFalse("transaction lock: no longer in effect", lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));

        // transaction lock attempt with transaction lock by other
        // this test uses bcHeader, but the application will probably derive the params from BCAppointmentFunding
        assertFalse("conflicting transaction lock: current locks", lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue("conflicting transaction lock: lock obtained by "+pUIdOne, bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue("conflicting transaction lock: transaction lock is in effect", lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue("conflicting transaction lock: "+pUIdTwo+" could not get the same transaction lock", bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue("conflicting transaction lock: lock is owned by "+pUIdOne, bcLockStatus.getTransactionLockOwner().equals(pUIdOne));
        assertTrue("conflicting transaction lock: still locked by "+pUIdOne,lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        lockStatus = lockService.unlockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertTrue("conflicting transaction lock: lock removed by "+pUIdOne, lockStatus == LockStatus.SUCCESS);
        assertFalse("conflicting transaction lock: transaction locks still exist", lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));

    }

    private boolean setTestDocumentNumber()
    {
        boolean returnValue = false;
        // use the accounting key to find the document number associated with this funding
        HashMap<String,Object> fieldValues = new HashMap<String,Object>(4);
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,universityFiscalYear);
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,chartOfAccountsCode);
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER,accountNumber);
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER,subAccountNumber);
        Collection<BudgetConstructionHeader> bcDocuments = SpringContext.getBean(BusinessObjectService.class).findMatching(BudgetConstructionHeader.class,fieldValues);
        // here there should only be one row
        Iterator<BudgetConstructionHeader> bcHeaderRows = bcDocuments.iterator();
        while (bcHeaderRows.hasNext())
        {
            BudgetConstructionHeader bcHeaderRow = bcHeaderRows.next();
            fdocNumber = bcHeaderRow.getDocumentNumber();
            returnValue = true;
            // save the header that we've chosen
            bcHeader = bcHeaderRow;
            while (bcHeaderRows.hasNext())
            {
                bcHeaderRows.next();
            }
        }
        return returnValue;
    }
    
    private Integer setTestFiscalYear()
    {
        Integer fiscalYear = new Integer(0);
        // find a fiscal year for which there is active budget construction data.
        HashMap<String,Object> fieldValues = new HashMap<String,Object>(2);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE,BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_ACTIVE_INDICATOR,Boolean.TRUE);
        Collection<FiscalYearFunctionControl> returnedYears = SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(FiscalYearFunctionControl.class,fieldValues,"universityFiscalYear",false);
        // there should be only one, but who knows with test data involved--we'll take the fiscal year from the first one
        Iterator<FiscalYearFunctionControl> activeYears = returnedYears.iterator();
        if (activeYears.hasNext())
        {
            fiscalYear = activeYears.next().getUniversityFiscalYear();
            // just run the iterator out, to be tidy
            while (activeYears.hasNext())
            {
                activeYears.next();
            }
        }
        return fiscalYear;
    }
    
    private boolean setTestFunding()
    {
        boolean returnValue = false;
        // all we need is a single funding line (not deleted, not vacant) for a real person
        // but we'll apparently have to get them all and just take the first one
        HashMap<String,Object> fieldValues = new HashMap<String,Object>(2);
//        fieldValues.put(BCPropertyConstants.APPOINTMENT_FUNDING_DELETE_INDICATOR,new Boolean(false));
        fieldValues.put(KFSPropertyConstants.ACTIVE,new Boolean(true));
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,universityFiscalYear);
        // get the complete set of rows and look for the first one that does not have a vacant EMPLID
        Collection<PendingBudgetConstructionAppointmentFunding> resultSet = SpringContext.getBean(BusinessObjectService.class).findMatching(PendingBudgetConstructionAppointmentFunding.class,fieldValues);
        Iterator<PendingBudgetConstructionAppointmentFunding> fundingRows = resultSet.iterator();
        while (fundingRows.hasNext())
        {
            PendingBudgetConstructionAppointmentFunding fundingRow = fundingRows.next();
            if (!fundingRow.getEmplid().equals(BCConstants.VACANT_EMPLID))
            {
                returnValue = true;
                // set all the test funding values from this row
                chartOfAccountsCode = fundingRow.getChartOfAccountsCode();
                accountNumber = fundingRow.getAccountNumber();
                subAccountNumber = fundingRow.getSubAccountNumber();
                financialObjectCode = fundingRow.getFinancialObjectCode();
                financialSubObjectCode = fundingRow.getFinancialSubObjectCode();
                emplid = fundingRow.getEmplid();
                positionNumber = fundingRow.getPositionNumber();
                // save the row we've selected
                bcAFunding = fundingRow;
                // run out the iterator
                while (fundingRows.hasNext())
                {
                    fundingRows.next();
                }
            }
        }
        return returnValue;
    }
    
    @ConfigureContext(shouldCommitTransactions = true)
    private void clearTestRowLocks()
    {
        // clear all the locks on the test rows used in this TestCase
        // make sure there are no locks or transaction locks in our test header
        lockService.unlockAccount(bcHeader);
        lockService.unlockTransaction(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        // make sure the position we intend to use is unlocked
        lockService.unlockPosition(positionNumber, universityFiscalYear);
        // make sure funding locks we intend to use aren't there
        lockService.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear, pUIdOne);
        lockService.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear, pUIdTwo);
    }
    
}


