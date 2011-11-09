/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.kew.api.exception.WorkflowException;

public interface CashControlDocumentService {

    /**
     * This method add a new cash control detail to the cash control document
     * 
     * @param descritpion the description tells whether the detail was added by cash controo doc or by lockbox
     * @param cashControlDocument the cash control document
     * @param cashControlDetail the cash control detail to be added
     * @throws WorkflowException
     */
    public void addNewCashControlDetail(String descritpion, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException;

    /**
     * This method creates a new PaymentApplicationDocument
     * 
     * @param description the description tells whether the detail was added by cash controo doc or by lockbox
     * @param cashControlDocument the cash control document
     * @param cashControlDetail the cash control detail
     * @return true if all application document have been approved, false otherwise
     * @throws WorkflowException
     */
    public PaymentApplicationDocument createAndSavePaymentApplicationDocument(String description, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException;

    /**
     * This method creates the GLPEs with document type CR
     * 
     * @param cashControlDocument
     * @param sequenceHelper
     * @return true if glpes successfuly created false otherwise
     */
    public boolean createCashReceiptGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * This method creates the GLPEs with document type DI
     * 
     * @param cashControlDocument
     * @param sequenceHelper
     * @return true if glpes successfuly created false otherwise
     */
    public boolean createDistributionOfIncomeAndExpenseGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * This method creates the GLPEs with document type GEC
     * 
     * @param cashControlDocument
     * @param sequenceHelper
     * @return true if glpes successfuly created false otherwise
     */
    public boolean createGeneralErrorCorrectionGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

    /**
     * This method saves CashControl document GLPEs in the database
     * 
     * @param cashControlDocument
     */
    public void saveGLPEs(CashControlDocument cashControlDocument);
    
    /**
     * This method gets the lockbox number for the current CashControl document
     * 
     * @return the lockbox number
     */
    public String getLockboxNumber(CashControlDocument cashControlDocument); 
    
    /**
     * Creates bank offset GLPEs for the cash control document
     * @param cashControlDocument the document to create cash control GLPEs for
     * @param sequenceHelper the sequence helper which will sequence the new GLPEs
     * @return true if the new bank offset GLPEs were created successfully, false otherwise
     */
    public abstract boolean createBankOffsetGLPEs(CashControlDocument cashControlDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);

}
