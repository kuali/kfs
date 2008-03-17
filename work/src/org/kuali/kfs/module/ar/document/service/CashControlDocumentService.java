/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.service;

import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.financial.document.CashReceiptDocument;

import edu.iu.uis.eden.exception.WorkflowException;

public interface CashControlDocumentService {

    /**
     * This method creates a new PaymentApplicationDocument
     * 
     * @param cashControlDocument the cash control document
     * @param cashControlDetail the cash control detail
     * @return true if all application document have been approved, false otherwise
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createAndSavePaymentApplicationDocument(CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException;

    /**
     * This method creates a new CashReceiptDocument
     * 
     * @param cashControlDocument the cash control document
     * @return the new CashReceiptDocument number
     * @throws WorkflowException
     */
    public String createCashReceiptDocument(CashControlDocument cashControlDocument) throws WorkflowException;

    /**
     * This method creates a new DistributionOfIncomeAndExpenseDocument
     * 
     * @param cashControlDocument the cash control document
     * @return the new DistributionOfIncomeAndExpenseDocument number
     * @throws WorkflowException
     */
    public String createDistributionOfIncomeAndExpenseDocument(CashControlDocument cashControlDocument) throws WorkflowException;

    /**
     * This method creates a new GeneralErrorCorrectionDocument
     * 
     * @param cashControlDocument the cash control document
     * @return the new GeneralErrorCorrectionDocument number
     * @throws WorkflowException
     */
    public String createGeneralErrorCorrectionDocument(CashControlDocument cashControlDocument) throws WorkflowException;

    /**
     * This method checks if all application documents have been approved
     * 
     * @param cashControlDocument the cash control document
     * @return true if all application documents have been approved
     */
    public boolean hasAllApplicationDocumentsApproved(CashControlDocument cashControlDocument);

}
