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

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.bo.CashieringItemInProcess;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;

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
    public void addDeposit(CashManagementDocument cashManagementDoc, String depositTicketNumber, BankAccount bankAccount, List selectedCashReceipts, List selectedCashieringChecks, boolean isFinalDeposit);


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
    
    /**
     * Apply a cashiering transaction to a cash management document.  This means:
     * 0. check rules???
     * 1. Updating the cash drawer with any incoming currency and coin
     * 2. Moving any checks from the transaction to the CM document
     * 3. Checking if any items in process were closed; if so, saving that info
     * 4. Saving currency and coin records
     * 5. Saving any new item in process
     * 6. saving any checks
     * @param cmDoc the cash management document to apply the transaction to
     * @param cashieringTransaction the transaction to apply
     */
    public void applyCashieringTransaction(CashManagementDocument cmDoc);
    
    /**
     * Retrieve the open cashiering items in process for the given cash management document
     * @param cmDoc the document to retrieve the items in process for
     * @return the open items in process
     */
    public List<CashieringItemInProcess> getOpenItemsInProcess(CashManagementDocument cmDoc);
    
    /**
     * Returns all items in process associated with this workgroup, closed within the past 30 days
     * @param cmDoc the cash management document which is associated with the workgroup that the closed items in process would have also been associated with 
     * @return a list of any items in process recently closed
     */
    public List<CashieringItemInProcess> getRecentlyClosedItemsInProcess(CashManagementDocument cmDoc);
    
    /**
     * Generates the master currency detail, which sounds bad, but which is really just okay.
     * A master currency detail is the composite effect of all the transactions of a cash
     * management document on a cash drawer
    * @param cmDoc the cash management document to generate the master record for
     * @return the master record, master in the sense of "Platonic ideal" from which
     * all else is a copy
     */
    public CurrencyDetail generateMasterCurrencyDetail(CashManagementDocument cmDoc);
    
    /**
     * This generates the "master" coin detail record - a composite of all the coin detail activity that occurred to the cash drawer
     * @param cmDoc the cash management document to generate the master record for
     * @return the master record
     */
    public CoinDetail generateMasterCoinDetail(CashManagementDocument cmDoc);
    
    /**
     * Verifies if a given cash receipt is deposited as part of the given cash management document
     * @param cmDoc the cash management document to search through
     * @param crDoc the cash receipt to check  the deposited status of
     * @return true if the given cash receipt document is deposited as part of the given cash management document, false if otherwise
     */
    public boolean verifyCashReceiptIsDeposited(CashManagementDocument cmDoc, CashReceiptDocument crDoc);
    
    /**
     * This method verifies that all cash receipts for the document are deposited
     * @param cmDoc the cash management document to verify
     * @return true if all CRs are deposited, false if otherwise
     */
    public boolean allVerifiedCashReceiptsAreDeposited(CashManagementDocument cmDoc);
    
    /**
     * This method turns the last interim deposit into the final deposit and locks the cash drawer 
     * @param cmDoc the cash management document to take deposits from for finalization
     */
    public void finalizeLastInterimDeposit(CashManagementDocument cmDoc);
    
    /**
     * This method creates new cumulative currency and coin details for a document
     * @param cmDoc the cash management document the cumulative details will be associated with
     * @param cashieringSource the cashiering record source for the new details
     */
    public void createNewCashDetails(CashManagementDocument cmDoc, String cashieringSource);
    
    /**
     * Grab the currency and coin detail for final deposits
     * @param cmDoc the cash management document which has deposits to populate
     */
    public void populateCashDetailsForDeposit(CashManagementDocument cmDoc);
    
    /**
     * Retrieves from the database any undeposited cashiering transaction checks associated with the given cash management document
     * @param documentNumber the document number of a cash management document that cashiering transaction checks may be associated with
     * @return a list of checks associated with the document
     */
    public List<Check> selectUndepositedCashieringChecks(String documentNumber);
    
    /**
     * Retrieves from the database all cashiering transaction checks deposited for a given deposit
     * @param documentNumber the document number of a cash management document that cashiering transaction checks have been deposited for
     * @param depositLineNumber the line number of the deposit to find checks deposited for
     * @return a list of checks associated with the given deposit
     */
    public List<Check> selectCashieringChecksForDeposit(String documentNumber, Integer depositLineNumber);
    
    /**
     * Total up the amounts of all checks so far deposited as part of the given cash management document
     * @param documentNumber the id of a cash management document
     * @return the total of cashiering checks deposited so far as part of that document
     */
    public KualiDecimal calculateDepositedCheckTotal(String documentNumber);
    
    /**
     * This method determines whether or not the given cash management document can be cancelled or not.
     * @param cmDoc the cash management document to cancel
     * @return true if cancellation is possible, false if otherwise
     */
    public boolean allowDocumentCancellation(CashManagementDocument cmDoc);
    
    /**
     * Select the next available check line number for the given cash management document
     * @param documentNumber the document number of a cash management document
     * @return the next available check line number for cashiering checks
     */
    public Integer selectNextAvailableCheckLineNumber(String documentNumber);
}
