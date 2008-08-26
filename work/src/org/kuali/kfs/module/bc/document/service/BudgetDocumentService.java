/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;

import org.kuali.rice.kew.exception.WorkflowException;


/**
 * This defines the methods a BudgetDocumentService must implement
 */
public interface BudgetDocumentService {

    /**
     * Gets a BudgetConstructionHeader by the candidate key instead of primary key
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return
     */
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear);

    /**
     * Performs all actions needed to validate and save a Budget Construction document to the database and workflow.
     * 
     * @param document
     * @return
     * @throws WorkflowException
     * @throws ValidationException
     */
    public Document saveDocument(BudgetConstructionDocument budgetConstructionDocument) throws WorkflowException, ValidationException;

    /**
     * Performs all actions needed to validate and save a Budget Construction document to the database only.
     * 
     * @param document
     * @return
     * @throws ValidationException
     */
    public Document saveDocumentNoWorkflow(BudgetConstructionDocument budgetConstructionDocument) throws ValidationException;

    /**
     * Performs all actions needed to validate and save a Budget Construction document to the database only. Whether or not the
     * monthly RI check is performed during validation is controled using doMonthRICheck. Passing in false is a case used by the
     * monthlySpread deletion functionality. No need to perform monthly RI check if the action is to remove all the records
     * 
     * @param bcDoc
     * @param doMonthRICheck
     * @return
     * @throws ValidationException
     */
    public Document saveDocumentNoWorkFlow(BudgetConstructionDocument bcDoc, MonthSpreadDeleteType monthSpreadDeleteType, boolean doMonthRICheck) throws ValidationException;

    /**
     * Checks if annual and/or monthly benefits need calculated and calls the associated calculation method
     * 
     * @param bcDoc
     */
    public void calculateBenefitsIfNeeded(BudgetConstructionDocument bcDoc);

    /**
     * Explicitly calls both the annual and monthly benefits calculation methods
     * 
     * @param bcDoc
     */
    public void calculateBenefits(BudgetConstructionDocument bcDoc);

    /**
     * Calculates annual benefits for a budget construction document using the persisted data currently stored in the database.
     * 
     * @param bcDoc
     */
    public void calculateAnnualBenefits(BudgetConstructionDocument bcDoc);

    /**
     * Calculates the monthly benefits for a budget construction document using the persisted data currently stored in the database.
     * 
     * @param bcDoc
     */
    public void calculateMonthlyBenefits(BudgetConstructionDocument bcDoc);

    /**
     * Gets the salary detail lines request sum for a budget document expenditure accounting line
     * 
     * @param salaryDetailLine
     * @return
     */
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine);

    /**
     * Version of getAccessMode that uses a passed in BudgetConstructionHeader, instead of getting it directly from the database.
     * Cases of using this method include testing what the access mode would be at another level without actually moving (setting
     * new level and storing) the document in the database.
     * 
     * @param bcHeader
     * @param u
     * @return
     */
    public String getAccessMode(BudgetConstructionHeader bcHeader, UniversalUser u);

    /**
     * Gets the Budget Construction access mode for the document candidate key and the user. Assumes the Budget Document exists in
     * the database and the Account Organization Hierarchy rows exist for the account. Checks the special case when the document is
     * at level 0 and the user is either the fiscal officer for the account or an account delegate for the Budget Construction
     * document type or the special 'ALL' document type. All other cases calculate access based on a comparison of the Account
     * Organization Hierarchy and the approval (pointOfView) organizations setup in workflow for the user. It returns one of the
     * edit mode constants. KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY
     * KfsAuthorizationConstants.BudgetConstructionEditMode.UNVIEWABLE
     * KfsAuthorizationConstants.BudgetConstructionEditMode.VIEW_ONLY
     * KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_ORG_APPROVER
     * KfsAuthorizationConstants.BudgetConstructionEditMode.USER_BELOW_DOC_LEVEL
     * KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_IN_ACCOUNT_HIER
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param u
     * @return
     */
    public String getAccessMode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, UniversalUser u);


    public List<BudgetConstructionAccountOrganizationHierarchy> getPushPullLevelList(BudgetConstructionDocument bcDoc, UniversalUser u);

    /**
     * update the pending budget construction GL record assocating with the given appointment funding. If there exists any pbgl
     * record, a new one will be created and stored
     * 
     * @param appointmentFunding the given appointment funding
     * @param updateAmount the amount that can be used to update the amounts of the pending budget construction GL record
     */
    public void updatePendingBudgetGeneralLedger(PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount);

    /**
     * update the pending budget construction GL plug record assocating with the given appointment funding. If there exists any pbgl
     * plug record, a new one will be created and stored
     * 
     * @param appointmentFunding the given appointment funding
     * @param updateAmount the amount that can be used to update the amounts of the pending budget construction GL plug record
     */
    public void updatePendingBudgetGeneralLedgerPlug(PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount);

    /**
     * get the budget document with the information provided by the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the budget document with the information provided by the given appointment funding
     */
    public BudgetConstructionHeader getBudgetConstructionHeader(PendingBudgetConstructionAppointmentFunding appointmentFunding);
    
    /**
     * get the budget document with the information provided by the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the budget document with the information provided by the given appointment funding
     */
    public BudgetConstructionDocument getBudgetConstructionDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * determine whether the given document is budgetable
     * 
     * @param bcHeader the given budget document
     * @return true if the given document is budgetable; otherwise, false
     */
    public boolean isBudgetableDocument(BudgetConstructionHeader bcHeader);
    
    /**
     * determine whether the given document is budgetable skipping the
     * wages allowed check
     * 
     * @param bcHeader the given budget document
     * @return true if the given document is budgetable; otherwise, false
     */
    public boolean isBudgetableDocumentNoWagesCheck(BudgetConstructionHeader bcHeader);
    
    /**
     * determine whether the given document is budgetable
     * 
     * @param document the given budget document
     * @return true if the given document is budgetable; otherwise, false
     */
    public boolean isBudgetableDocument(BudgetConstructionDocument document);

    /**
     * determine whether the given document is budgetable skipping the
     * wages allowed check
     * 
     * @param document the given budget document
     * @return true if the given document is budgetable; otherwise, false
     */
    public boolean isBudgetableDocumentNoWagesCheck(BudgetConstructionDocument document);

    /**
     * determine whether the given appointment funding is associated with a budgetable document
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the given appointment funding is associated with a budgetable document; otherwise, false
     */
    public boolean isAssociatedWithBudgetableDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * determine whether the given account is budgetable for the specified budget year
     * 
     * @param budgetYear the specified budget year
     * @param account the given account
     * @param isWagesCheck whether or not to include the no wages check
     * @return true if the given account is budgetable for the specified budget year; otherwise, false
     */
    public boolean isBudgetableAccount(Integer budgetYear, Account account, boolean isWagesCheck);

    /**
     * determine whether the given subaccount is budgetable
     * 
     * @param subAccount the given subaccount
     * @param subAccountNumber the sub account number associated with the given sub account. If sub account is null, the number can
     *        be empty or the default.
     * @return true if the given subaccount is budgetable; otherwise, false
     */
    public boolean isBudgetableSubAccount(SubAccount subAccount, String subAccountNumber);

    /**
     * retrieve all pending budget construction GL records associated with the given budget contruction header
     * 
     * @param budgetConstructionHeader the budget construction header associated with the pending budget construction GL records to
     *        be retrieved
     * @return all pending budget construction GL records associated with the given budget contruction header
     */
    public List<PendingBudgetConstructionGeneralLedger> retrievePendingBudgetConstructionGeneralLedger(BudgetConstructionHeader budgetConstructionHeader);

    /**
     * Returns a list of Pending Budget GL rows from the DB for the BudgetConstructionDocument that are associated with Salary
     * Setting including any 2PLG rows.
     * 
     * @param bcDocument
     * @return
     */
    public List<PendingBudgetConstructionGeneralLedger> getPBGLSalarySettingRows(BudgetConstructionDocument bcDocument);

    /**
     * Adds or Updates a Pending Budget GL row to a BudgetConstruction document with the passed in Pending Budget GL object. Any
     * inserts are added in the proper order in the list based on object code, sub-object code sort order
     * 
     * @param bcDoc
     * @param sourceRow
     * @return
     */
    public BudgetConstructionDocument addOrUpdatePBGLRow(BudgetConstructionDocument bcDoc, PendingBudgetConstructionGeneralLedger sourceRow);

    /**
     * Adds or updates the 2PLG row in a BudgetConstructionDocument adding the updateAmount into any existing request amount
     * 
     * @param bcDoc
     * @param updateAmount
     * @return
     */
    public PendingBudgetConstructionGeneralLedger updatePendingBudgetGeneralLedgerPlug(BudgetConstructionDocument bcDoc, KualiInteger updateAmount);

    /**
     * Performs Budgetconstructiondocument validation as if saving, but does not perform the actual save. This is used to
     * immediately give feedback to the user when returning from Salary Setting in the event there are Monthly RI issues.
     * 
     * @param document
     * @throws ValidationException
     */
    public void validateDocument(Document document) throws ValidationException;

    /**
     * Populates references for a given Pending Budget GL row.
     * 
     * @param line
     */
    public void populatePBGLLine(PendingBudgetConstructionGeneralLedger line);
}
