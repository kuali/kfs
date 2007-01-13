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
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;

/**
 * This interface defines methods that a CashManagementService implementation must provide.
 * 
 * 
 */
public interface CashManagementService {
    /**
     * Creates and returns a CashManagementDocument, opening the CashDrawer associated with the given verification unit.
     * 
     * @param unitName
     * @param docDescription
     * @param annotation
     * @return properly initialized CashManagementDocument
     */
    public CashManagementDocument createCashManagementDocument(String unitName, String docDescription, String annotation);


    /**
     * Uses the given information to lock the appropriate CashDrawer, create a Deposit, and associate it with the given
     * CashManagementDocument and CashReceipts.
     * 
     * @param cashManagementDoc
     * @param depositTicketNumber
     * @param bankAccount
     * @param selectedCashReceipts
     * @param isFinalDeposit
     */
    public void addDeposit(CashManagementDocument cashManagementDoc, String depositTicketNumber, BankAccount bankAccount, List selectedCashReceipts, boolean isFinalDeposit);


    /**
     * Cancels the given Deposit, updating the related CashManagementDocument, CashReceipts, and CashDrawer as needed
     * 
     * @param deposit
     */
    public void cancelDeposit(Deposit deposit);

    /**
     * Cancels the given CashManagementDocument, cancelling the Deposits it contains and closing the CashDrawer associated with the
     * given verification unit. Called in response to a workflow CANCEL request, so this method doesn't invoke workflow itself.
     * 
     * @param cmDoc
     * @param annotation
     */
    public void cancelCashManagementDocument(CashManagementDocument cmDoc);


    /**
     * Finalizes the given CashManagementDocument, updating the status of the CashReceipt documents in the Deposits it contains and
     * closing the CashDrawer associated with the given verification unit. Called in response to a workflow document status change,
     * so this method doesn't invoke workflow itself.
     * 
     * @param cmDoc
     * @param annotation
     */
    public void finalizeCashManagementDocument(CashManagementDocument cmDoc);


    /**
     * @param documentId
     * @return CashManagementDocument which contains the Deposit which contains the given CashReceipt, or null if the CashReceipt is
     *         not contained in a Deposit
     */
    public CashManagementDocument getCashManagementDocumentForCashReceiptId(String documentId);


    /**
     * Returns a List of all CashReceipts associated with the given Deposit.
     * 
     * @param deposit
     * @return List of CashReceipts
     */
    public List retrieveCashReceipts(Deposit deposit);
}
