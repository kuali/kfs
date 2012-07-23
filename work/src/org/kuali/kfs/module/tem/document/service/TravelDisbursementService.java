/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service;

import java.util.List;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.tem.TemConstants.DisburseType;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

/**
 * Travel Disbursement Voucher Service
 */
public interface TravelDisbursementService {
    
    /**
     * Populate disbursement voucher fields for reimbursable expenses
     * 
     * @param disbursementVoucherDocument
     * @param document
     */
    public void populateReimbursableDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument document);

    /**
     * Populate the disbursement voucher fields for imported copr card expenses
     * 
     * @param disbursementVoucherDocument
     * @param document
     */
    public void populateImportedCorpCardDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument document, String cardAgencyType);

    /**
     * Create Disbursement Voucher document from reimbursement and blanket approve.
     * Base on the disbursementType, it will determine how to populate the voucher 
     * 
     * @param document
     * @return
     */
    public DisbursementVoucherDocument createAndApproveDisbursementVoucherDocument(DisburseType type, TravelDocument document, String cardAgencyType);


    /**
     * Wrapper method to default card type to null;
     * 
     * @param document
     * @return
     */
    public DisbursementVoucherDocument createAndApproveDisbursementVoucherDocument(DisburseType type, TravelDocument document);
    
    /**
     * process Disbursement Voucher document for the TEMReimbursementDocument document
     * 
     * It will create the Reimbursement DV document and then add to the travel document as a related doc
     * 
     * @param document
     * @return
     */
    public void processTEMReimbursementDV(TEMReimbursementDocument document);
    
    /**
     * process Disbursement Voucher document for the Travel Authorization document
     * 
     * It will create the Payment DV document according to the advance and then add to the travel document as a related doc
     * 
     * @param document
     * @return
     */
    public void processTravelAdvanceDV(TravelAuthorizationDocument document);
    
    /**
     * save the DV with added notes and annotation
     * 
     * @param disbursementVoucherDocument
     * @param travelDocument
     * @throws Exception
     */
    public void saveErrorDisbursementVoucher(DisbursementVoucherDocument disbursementVoucherDocument, TravelDocument travelDocument) throws Exception;

    /**
     * Redistribute the DV's source accounting line amounts base on the given source accounting lines distribution used as the percentage calculation map.
     * The new total DV distribute amount MUST be set before using this function
     * 
     * @param disbursementVoucherDocument
     * @param sourceAccountingLines
     */
    public void redistributeDisbursementAccountingLine(DisbursementVoucherDocument disbursementVoucherDocument, List<SourceAccountingLine> sourceAccountingLines);
}
