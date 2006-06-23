/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service;

import java.util.List;

import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;

/**
 * This interface defines methods that a CashManagementService implementation must provide.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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
