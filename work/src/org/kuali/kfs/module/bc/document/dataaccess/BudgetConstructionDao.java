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
package org.kuali.kfs.module.bc.document.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionFundingLock;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.rice.core.api.util.type.KualiInteger;


/**
 * This interface defines the methods that a BudgetConstructionDao must provide.
 */
public interface BudgetConstructionDao {

    /**
     * This gets a BudgetConstructionHeader using the candidate key chart, account, subaccount, fiscalyear
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return BudgetConstructionHeader
     */
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * This deletes a BudgetConstructionFundingLock from the database
     *
     * @param budgetConstructionFundingLock
     */
    public void deleteBudgetConstructionFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock);

    /**
     * This gets the set of BudgetConstructionFundingLocks asssociated with a BC EDoc (account). Each BudgetConstructionFundingLock
     * has the positionNumber dummy attribute set to the associated Position that is locked. A positionNumber value of "NotFnd"
     * indicates the BudgetConstructionFundingLock is an orphan.
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return Collection<BudgetConstructionFundingLock>
     */
    public Collection<BudgetConstructionFundingLock> getFlocksForAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * Returns the position number associated with a funding lock or the not found string if the lock is an orphan.
     *
     * @param budgetConstructionFundingLock - funding lock to get position for
     * @return position number associated with lock
     */
    public String getPositionAssociatedWithFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock);

    /**
     * This method deletes all BudgetConstructionPullup rows associated with a user.
     *
     * @param principalName
     */
    public void deleteBudgetConstructionPullupByUserId(String principalName);

    /**
     * This method returns a list of BudgetConstructionPullup objects (organizations) ownded by the user that have the pullflag set
     *
     * @param principalName
     * @return
     */
    public List<BudgetConstructionPullup> getBudgetConstructionPullupFlagSetByUserId(String principalName);

    /**
     * This returns a list of BudgetConstructionPullup objects (organizations) that are children to the passed in organization for
     * the user
     *
     * @param principalId
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public List<BudgetConstructionPullup> getBudgetConstructionPullupChildOrgs(String principalId, String chartOfAccountsCode, String organizationCode);

    /**
     * Returns the sum of the salary detail request amounts for an accounting line
     *
     * @param salaryDetailLine
     * @return
     */
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine);

    /**
     * returns a list of fringe benefit accounting lines for a document
     *
     * @param documentNumber
     * @param fringeObjects
     * @return
     */
    public List getDocumentPBGLFringeLines(String documentNumber, List fringeObjects);

    /**
     * Returns a list of account organization hierarchy levels for an account
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param universityFiscalYear
     * @return
     */
    public List<BudgetConstructionAccountOrganizationHierarchy> getAccountOrgHierForAccount(String chartOfAccountsCode, String accountNumber, Integer universityFiscalYear);

    /**
     * Returns a list of Pending Budget GL rows that are Salary Setting detail related, based on the set of salarySettingObjects
     * passed in
     *
     * @param documentNumber
     * @param salarySettingObjects
     * @return
     */
    public List getPBGLSalarySettingRows(String documentNumber, List salarySettingObjects);

    /**
     * Retrieves all <code>PendingBudgetConstructionAppointmentFunding</code> records for the given position key.
     *
     * @param universityFiscalYear budget fiscal year, primary key field for position record
     * @param positionNumber position number, primary key field for position record
     * @return List of PendingBudgetConstructionAppointmentFunding objects
     */
    public List<PendingBudgetConstructionAppointmentFunding> getAllFundingForPosition(Integer universityFiscalYear, String positionNumber);

    /**
     * Returns a <code>BudgetConstructionAccountReports</code> object for the given key.
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return
     */
    public BudgetConstructionAccountReports getAccountReports(String chartOfAccountsCode, String accountNumber);

    /**
     * Returns a <code>BudgetConstructionOrganizationReports</code> object for the given key.
     *
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public BudgetConstructionOrganizationReports getOrganizationReports(String chartOfAccountsCode, String organizationCode);

    /**
     * Builds an Account Organization Hierarchy by recursively calling itself inserting each Hierarchy row (<code>BudgetConstructionAccountOrganizationHierarchy</code>)
     * while walking the organization reports to structure (<code>BudgetConstructionOrganizationReports</code>) stopping at the
     * root of the organization tree or overflow value, whatever comes first.
     *
     * @param rootChart
     * @param rootOrganization
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param currentLevel
     * @param organizationChartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public boolean insertAccountIntoAccountOrganizationHierarchy(String rootChart, String rootOrganization, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, Integer currentLevel, String organizationChartOfAccountsCode, String organizationCode);

    /**
     * deletes an existing set of Account Organization Hierarchy objects from the DB for the account key passed in
     *
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     */
    public void deleteExistingAccountOrganizationHierarchy(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber);
}

