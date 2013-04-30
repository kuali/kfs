/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.dataaccess.DisbursementVoucherDao;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public class DisbursementVoucherExtractionHelperServiceImpl implements DisbursementVoucherPaymentService, PaymentSourceToExtractService {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherExtractionHelperServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private DisbursementVoucherDao disbursementVoucherDao;

    @Override
    public void cancelDisbursementVoucher(DisbursementVoucherDocument dv, Date cancelDate) {
        if (dv.getCancelDate() == null) {
            try {
                // set the canceled date
                dv.setCancelDate(cancelDate);
                dv.refreshReferenceObject("generalLedgerPendingEntries");
                if (ObjectUtils.isNull(dv.getGeneralLedgerPendingEntries()) || dv.getGeneralLedgerPendingEntries().size() == 0) {
                    // generate all the pending entries for the document
                    getGeneralLedgerPendingEntryService().generateGeneralLedgerPendingEntries(dv);
                    // for each pending entry, opposite-ify it and reattach it to the document
                    GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper = new GeneralLedgerPendingEntrySequenceHelper();
                    for (GeneralLedgerPendingEntry glpe : dv.getGeneralLedgerPendingEntries()) {
                        oppositifyEntry(glpe, getBusinessObjectService(), glpeSeqHelper);
                    }
                }
                else {
                    List<GeneralLedgerPendingEntry> newGLPEs = new ArrayList<GeneralLedgerPendingEntry>();
                    GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper = new GeneralLedgerPendingEntrySequenceHelper(dv.getGeneralLedgerPendingEntries().size() + 1);
                    for (GeneralLedgerPendingEntry glpe : dv.getGeneralLedgerPendingEntries()) {
                        glpe.refresh();
                        if (glpe.getFinancialDocumentApprovedCode().equals(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED)) {
                            // damn! it got processed! well, make a copy, oppositify, and save
                            GeneralLedgerPendingEntry undoer = new GeneralLedgerPendingEntry(glpe);
                            oppositifyEntry(undoer, getBusinessObjectService(), glpeSeqHelper);
                            newGLPEs.add(undoer);
                        }
                        else {
                            // just delete the GLPE before anything happens to it
                            getBusinessObjectService().delete(glpe);
                        }
                    }
                    dv.setGeneralLedgerPendingEntries(newGLPEs);
                }
                // set the financial document status to canceled
                dv.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
                // save the document
                SpringContext.getBean(DocumentService.class).saveDocument(dv, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
            catch (WorkflowException we) {
                LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + dv.getDocumentNumber() + " " + we);
                throw new RuntimeException(we);
            }
        }

    }

    /**
     * Updates the given general ledger pending entry so that it will have the opposite effect of what it was created to do; this,
     * in effect, undoes the entries that were already posted for this document
     *
     * @param glpe the general ledger pending entry to undo
     */
    protected void oppositifyEntry(GeneralLedgerPendingEntry glpe, BusinessObjectService boService, GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper) {
        if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
            glpe.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
            glpe.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        glpe.setTransactionLedgerEntrySequenceNumber(glpeSeqHelper.getSequenceCounter());
        glpeSeqHelper.increment();
        glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        boService.save(glpe);
    }

    /**
     * Retrieve disbursement vouchers for extraction
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#retrievePaymentSourcesByCampus(boolean)
     */
    @Override
    public Map<String, List<? extends PaymentSource>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        if (immediatesOnly) {
            throw new UnsupportedOperationException("DisbursementVoucher PDP does immediates extraction through normal document processing; immediates for DisbursementVoucher should not be run through batch.");
        }

        Map<String, List<? extends PaymentSource>> documentsByCampus = new HashMap<String, List<? extends PaymentSource>>();

        Collection<DisbursementVoucherDocument> docs = disbursementVoucherDao.getDocumentsByHeaderStatus(DisbursementVoucherConstants.DocumentStatusCodes.APPROVED, false);
        for (DisbursementVoucherDocument element : docs) {
            String dvdCampusCode = element.getCampusCode();
            if (StringUtils.isNotBlank(dvdCampusCode)) {
                if (documentsByCampus.containsKey(dvdCampusCode)) {
                    List<DisbursementVoucherDocument> documents = (List<DisbursementVoucherDocument>)documentsByCampus.get(dvdCampusCode);
                    documents.add(element);
                }
                else {
                    List<DisbursementVoucherDocument> documents = new ArrayList<DisbursementVoucherDocument>();
                    documents.add(element);
                    documentsByCampus.put(dvdCampusCode, documents);
                }
            }
        }

        return documentsByCampus;
    }

    /**
     * @return the injected implementation of the BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Injects an implementation of the BusinessObjectService
     * @param businessObjectService the implementation of the BusinessObjectService to inject
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @return the injected implementation of the GeneralLedgerPendingEntryService
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    /**
     * Injects an implementation of the GeneralLedgerPendingEntryService
     * @param generalLedgerPendingEntryService the implementation of GeneralLedgerPendingEntryService to inject and use
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    /**
     * This method sets the disbursementVoucherDao instance.
     *
     * @param disbursementVoucherDao The DisbursementVoucherDao to be set.
     */
    public void setDisbursementVoucherDao(DisbursementVoucherDao disbursementVoucherDao) {
        this.disbursementVoucherDao = disbursementVoucherDao;
    }

}
