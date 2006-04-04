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

import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;


/**
 * This interface defines methods that a CashManagementService implementation must provide.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface CashManagementService {
    /**
     * Creates a CashManagementDocument, creates a Deposit from the given cashReceipts, associates them, and returns the document.
     * 
     * @param documentDescription
     * @param verifiedCashReceipts
     * @param workgroupName
     * @return new CashManagementDocument
     */
    public CashManagementDocument createCashManagementDocument(String documentDescription, List verifiedCashReceipts,
            String workgroupName);

    /**
     * Finalizes the given CashManagementDocument: updates all of the CashReceipts for all of its Deposits to "Approved" status,
     * reopens the CashDrawer, and changes the CashManagementDocument's status to "Approved". Should be called because the
     * document's workflow status changes to PROCESSED (i.e. should *not* invoke workflow directly).
     * 
     * @param cashManagementDoc
     */
    public void finalizeCashManagementDocument(CashManagementDocument cashManagementDoc);

    /**
     * Cancels the given CashManagementDocument: restores all of the CashhReceipts for all of its Deposits to "Verified" status, and
     * reopens the CashDrawer. Should be called if the document's workflow status changes to CANCELLED or DISAPPROVED (i.e. should
     * *not* invoke workflow directly).
     * 
     * @param cashManagementDoc
     */
    public void cancelCashManagementDocument(CashManagementDocument cashManagementDoc);


    /**
     * Verifies that all of the given CashReceipts are of "verified" status, creates a Deposit containing them, and changes their
     * status to "deposited".
     * 
     * @param verifiedCashReceipts
     * @param workgroupName
     * @return new Deposit
     */
    public Deposit createDeposit(CashManagementDocument cashManagementDoc, Integer lineNumber, List verifiedCashReceipts,
            String workgroupName);


    /**
     * @param cashManagementDoc
     * @return List of Deposits associated with the given CashManagementDocument
     */
    public List retrieveDeposits(CashManagementDocument cashManagementDoc);


    /**
     * Returns a List of all CashReceipts associated with the given Deposit.
     * 
     * @param deposit
     * @return List of CashReceipts
     */
    public List retrieveCashReceipts(Deposit deposit);


    /**
     * Deletes this Deposit, changing the status of all of its CashReceipts from "deposited" back to "verified".
     * 
     * @param deposit
     */
    public void cancelDeposit(Deposit deposit);


    /**
     * Iterates through the given list of CashReceipts, verifying that each one has been verified. If any CashReceipt hasn't been
     * verified, this method will return false.
     * 
     * @param cashReceipts
     */
    public boolean validateVerifiedCashReceipts(List cashReceipts);


    /**
     * Returns the count of all verified CashReceipts associated with the given workgroup.
     * 
     * @return number of verified CashReceipts associated with the given workgroup
     */
    public int countVerifiedCashReceiptsByVerificationUnit(String verificationUnitWorkgroupName);


    /**
     * Returns a List of all verified CashReceipts associated with the given workgroup.
     * 
     * @return List of CashReceipts
     */
    public List retrieveVerifiedCashReceiptsByVerificationUnit(String verificationUnitWorkgroupName);


    /**
     * This method will retrieve campus code base on the verification unit workgroup name that is supplied.
     * 
     * @param cashReceiptVerificationUnitWorkgroupName
     * @return String
     */
    public String getCampusCodeByCashReceiptVerificationUnitWorkgroupName(String cashReceiptVerificationUnitWorkgroupName);

    /**
     * This method will retrieve the verification unit workgroup for the CR's campus code.
     * 
     * @param campusCode
     * @return String
     */
    public String getCashReceiptVerificationUnitWorkgroupNameByCampusCode(String campusCode);

    /**
     * This method will retrieve the CashManagementDocument that houses the deposit that the passed in CashReceiptDocument is
     * associated with.
     * 
     * @param cashReceiptDocument
     * @return CashManagementDocument
     */
    public CashManagementDocument getCashManagementDocumentByCashReceiptDocument(CashReceiptDocument cashReceiptDocument);
}