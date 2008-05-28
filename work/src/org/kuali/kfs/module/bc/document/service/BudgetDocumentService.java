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
package org.kuali.module.budget.service;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.module.budget.BCConstants.MonthSpreadDeleteType;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.document.BudgetConstructionDocument;

import edu.iu.uis.eden.exception.WorkflowException;


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
     * Performs all actions needed to validate and save a Budget Construction document to the database only.
     * Whether or not the monthly RI check is performed during validation is controled using doMonthRICheck.
     * Passing in false is a case used by the monthlySpread deletion functionality.  No need to perform monthly RI
     * check if the action is to remove all the records
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
    public void calculateBenefitsIfNeeded (BudgetConstructionDocument bcDoc);

    /**
     * Explicitly calls both the annual and monthly benefits calculation methods
     * 
     * @param bcDoc
     */
    public void calculateBenefits(BudgetConstructionDocument bcDoc);

    /**
     * Calculates annual benefits for a budget construction document using the persisted data
     * currently stored in the database.
     * 
     * @param bcDoc
     */
    public void calculateAnnualBenefits(BudgetConstructionDocument bcDoc);

    /**
     * Calculates the monthly benefits for a budget construction document using the persisted data
     * currently stored in the database.
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
     * Gets the Budget Construction access mode for the document candidate key and the user. Assumes the Budget Document
     * exists in the database and the Account Organization Hierarchy rows exist for the account.  Checks the special
     * case when the document is at level 0 and the user is either the fiscal officer for the account or an account
     * delegate for the Budget Construction document type or the special 'ALL' document type. All other cases calculate
     * access based on a comparison of the Account Organization Hierarchy and the approval (pointOfView) organizations
     * setup in workflow for the user.
     * 
     * It returns one of the edit mode constants.
     * KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY
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
    
    
}