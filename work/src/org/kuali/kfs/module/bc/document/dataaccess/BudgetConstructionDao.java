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
package org.kuali.module.budget.dao;

import java.util.Collection;
import java.util.List;

import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionPosition;


/**
 * This interface defines the methods that a BudgetConstructionDao must provide.
 * 
 * 
 */
public interface BudgetConstructionDao {

    /**
     * This gets a BudgetConstructionHeader using the candidate key
     * chart, account, subaccount, fiscalyear
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return BudgetConstructionHeader
     */
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * This saves a BudgetConstructionHeader object to the database
     *
     * @param bcHeader
     */
    public void saveBudgetConstructionHeader(BudgetConstructionHeader bcHeader);

    /**
     * This gets a BudgetConstructionFundingLock using the primary key
     * chart, account, subaccount, fiscalyear, pUId
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param personUniversalIdentifier
     * @return BudgetConstructionFundingLock
     */
    public BudgetConstructionFundingLock getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier);

    /**
     * This saves a BudgetConstructionFundingLock to the database
     *
     * @param budgetConstructionFundingLock
     */
    public void saveBudgetConstructionFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock);

    /**
     * This deletes a BudgetConstructionFundingLock from the database
     *
     * @param budgetConstructionFundingLock
     */
    public void deleteBudgetConstructionFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock);

    /**
     * This gets the set of BudgetConstructionFundingLocks asssociated with a BC EDoc (account).
     * Each BudgetConstructionFundingLock has the positionNumber dummy attribute set to the
     * associated Position that is locked.  A positionNumber value of "NotFnd" indicates the
     * BudgetConstructionFundingLock is an orphan.
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return Collection<BudgetConstructionFundingLock>
     */
    public Collection<BudgetConstructionFundingLock> getFlocksForAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * Gets a BudgetConstructionPosition from the database by the primary key
     * positionNumber, fiscalYear
     *
     * @param positionNumber
     * @param fiscalYear
     * @return BudgetConstructionPosition
     */
    public BudgetConstructionPosition getByPrimaryId(String positionNumber, Integer fiscalYear);

    /**
     * Saves a BudgetConstructionPosition to the database
     *
     * @param bcPosition
     */
    public void saveBudgetConstructionPosition(BudgetConstructionPosition bcPosition);
    
    /**
     * This method deletes all BudgetConstructionPullup rows associated with a user.
     * 
     * @param personUserIdentifier
     */
    public void deleteBudgetConstructionPullupByUserId (String personUserIdentifier);
    
    /**
     * This method returns a list of BudgetConstructionPullup objects (organizations) ownded by 
     * the user that have the pullflag set
     * 
     * @param personUserIdentifier
     * @return
     */
    public List getBudgetConstructionPullupFlagSetByUserId (String personUserIdentifier);

    /**
     * This returns a list of BudgetConstructionPullup objects (organizations) that are children
     * to the passed in organization for the user
     * 
     * @param personUniversalIdentifier
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List getBudgetConstructionPullupChildOrgs (String personUniversalIdentifier, String chartOfAccountsCode, String organizationCode);

}