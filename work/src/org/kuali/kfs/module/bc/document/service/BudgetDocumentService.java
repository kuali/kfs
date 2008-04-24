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

import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.KualiInteger;
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
     * Gets the salary detail lines request sum for a budget document expenditure accounting line
     *  
     * @param salaryDetailLine
     * @return
     */
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine);

    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao);
    
}