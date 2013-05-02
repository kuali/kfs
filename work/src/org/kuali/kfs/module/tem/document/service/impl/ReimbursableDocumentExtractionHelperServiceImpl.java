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
package org.kuali.kfs.module.tem.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.ReimbursableDocumentPaymentService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Helper class to help PDP extraction of Reimbursable travel & entertainment documents - namely, the Travel Reimbursement,
 * the entertainment document, and the moving and relocation document
 */
public class ReimbursableDocumentExtractionHelperServiceImpl implements PaymentSourceToExtractService, ReimbursableDocumentPaymentService {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReimbursableDocumentExtractionHelperServiceImpl.class);
    protected DocumentService documentService;
    protected TravelerService travelerService;
    protected PaymentSourceExtractionService paymentSourceExtractionService;
    protected PersonService personService;
    protected WorkflowDocumentService workflowDocumentService;
    protected TravelDocumentDao travelDocumentDao;

    /**
     *
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#retrievePaymentSourcesByCampus(boolean)
     */
    @Override
    public Map<String, List<? extends PaymentSource>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        Map<String, List<? extends PaymentSource>> documentsByCampus = new HashMap<String, List<? extends PaymentSource>>();
        final List<TEMReimbursementDocument> reimbursables = retrieveAllApprovedReimbursableDocuments(immediatesOnly);
        Map<String, String> initiatorCampuses = new HashMap<String, String>();
        for (TEMReimbursementDocument document : reimbursables) {
            final String campusCode = findCampusForDocument(document, initiatorCampuses);
            if (!StringUtils.isBlank(campusCode)) {
                List<TEMReimbursementDocument> documentsForCampus = (List<TEMReimbursementDocument>)documentsByCampus.get(campusCode);
                if (documentsForCampus == null) {
                    documentsForCampus = new ArrayList<TEMReimbursementDocument>();
                    documentsByCampus.put(campusCode, documentsForCampus);
                }
                documentsForCampus.add(document);
            }
        }
        return documentsByCampus;
    }

    /**
     * Retrieves all the TravelReimbursement, TravelRelocation, and TravelEntertainment documents paid by check at approved status in one convenient call
     * @param immediatesOnly true if only those documents marked for immediate payment should be retrieved, false if all qualifying documents should be retrieved
     * @return all of the documents to process in a list
     */
    protected List<TEMReimbursementDocument> retrieveAllApprovedReimbursableDocuments(boolean immediatesOnly) {
        List<TEMReimbursementDocument> allReimbursables = new ArrayList<TEMReimbursementDocument>();
        allReimbursables.addAll(getTravelDocumentDao().getReimbursementDocumentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, immediatesOnly));
        allReimbursables.addAll(getTravelDocumentDao().getRelocationDocumentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, immediatesOnly));
        allReimbursables.addAll(getTravelDocumentDao().getEntertainmentDocumentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, immediatesOnly));
        return allReimbursables;
    }

    /**
     * Retrieves the campus code associated with the initiator of a passed in reimbursable document
     * @param document the reimbursable document to find a campus for
     * @param initiatorCampuses the cache of document initiator principal keys to campus codes
     * @return the campus code associated with the initiator of the given document
     */
    protected String findCampusForDocument(TEMReimbursementDocument document, Map<String, String> initiatorCampuses) {
        try {
            final WorkflowDocument workflowDocument = getWorkflowDocumentService().loadWorkflowDocument(document.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());
            return findCampusForInitiator(workflowDocument.getInitiatorPrincipalId(), initiatorCampuses);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not load document: "+document.getDocumentNumber(), we);
        }
    }

    /**
     * Retrieves and caches the campus code for the given initiator's principal id
     * @param initiatorPrincipalId the principal id of the initiator of a document
     * @param initiatorCampuses the cache of initiator principal keys to campus codes
     * @return the campus code associated with the given principal id
     */
    protected String findCampusForInitiator(String initiatorPrincipalId, Map<String, String> initiatorCampuses) {
        if (!StringUtils.isBlank(initiatorCampuses.get(initiatorPrincipalId))) {
            return initiatorCampuses.get(initiatorPrincipalId);
        }
        final Person initiatorPerson = getPersonService().getPerson(initiatorPrincipalId);
        final String campusCode = initiatorPerson.getCampusCode();
        if (!StringUtils.isBlank(campusCode)) {
            initiatorCampuses.put(initiatorPrincipalId, campusCode);
        }
        return campusCode;
    }

    /**
     * Cancels the reimbursable travel & entertainment document
     * @see org.kuali.kfs.module.tem.document.service.ReimbursableDocumentPaymentService#cancelReimbursableDocument(org.kuali.kfs.module.tem.document.TEMReimbursementDocument, java.sql.Date)
     */
    @Override
    public void cancelReimbursableDocument(TEMReimbursementDocument reimbursableDoc, Date cancelDate) {
        if (reimbursableDoc.getTravelPayment().getCancelDate() == null) {
            try {
                reimbursableDoc.getTravelPayment().setCancelDate(cancelDate);
                // TODO REVERSE THE GLPE ENTRIES!!!
                reimbursableDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
                // save the document
                getDocumentService().saveDocument(reimbursableDoc, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
            catch (WorkflowException we) {
                LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + reimbursableDoc.getDocumentNumber() + " " + we);
                throw new RuntimeException(we);
            }
        }

    }

    /**
     * Creates a payment group for the reimbursable travel & entertainment document
     * @see org.kuali.kfs.module.tem.document.service.ReimbursableDocumentPaymentService#createPaymentGroupForReimbursable(org.kuali.kfs.module.tem.document.TEMReimbursementDocument, java.sql.Date)
     */
    @Override
    public PaymentGroup createPaymentGroupForReimbursable(TEMReimbursementDocument reimbursableDoc, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("createPaymentGroupForReimbursable() started");
        }

        PaymentGroup pg = new PaymentGroup();
        pg.setCombineGroups(Boolean.TRUE);
        pg.setCampusAddress(Boolean.FALSE);

        pg.setCity(reimbursableDoc.getTraveler().getCityName());
        pg.setCountry(reimbursableDoc.getTraveler().getCountryCode());
        pg.setLine1Address(reimbursableDoc.getTraveler().getStreetAddressLine1());
        pg.setLine2Address(reimbursableDoc.getTraveler().getStreetAddressLine2());
        pg.setPayeeName(reimbursableDoc.getTraveler().getFirstName() + " " + reimbursableDoc.getTraveler().getLastName());
        if (travelerService.isEmployee(reimbursableDoc.getTraveler())){
            reimbursableDoc.getTravelPayment().setPayeeTypeCode(KFSConstants.PaymentPayeeTypes.EMPLOYEE); // TODO THIS SHOULD PROBABLY BE SOMEWHERE ELSE, LIKE THE REFRESH FROM TRAVELER LOOKUP
            reimbursableDoc.setProfileId(reimbursableDoc.getTemProfileId());
            pg.setPayeeId(reimbursableDoc.getTemProfile().getEmployeeId());
        }else{
            reimbursableDoc.getTravelPayment().setPayeeTypeCode(KFSConstants.PaymentPayeeTypes.CUSTOMER); // TODO THIS SHOULD PROBABLY BE SOMEWHERE ELSE, LIKE THE REFRESH FROM TRAVELER LOOKUP
            pg.setPayeeId(reimbursableDoc.getTraveler().getCustomerNumber());
        }
        pg.setState(reimbursableDoc.getTraveler().getStateCode());
        pg.setZipCd(reimbursableDoc.getTraveler().getZipCode());
        pg.setPaymentDate(reimbursableDoc.getTravelPayment().getDueDate());
        pg.setProcessImmediate(reimbursableDoc.getTravelPayment().isImmediatePaymentIndicator());
        pg.setPymtAttachment(reimbursableDoc.getTravelPayment().isAttachmentCode());
        pg.setPymtSpecialHandling(reimbursableDoc.getTravelPayment().isSpecialHandlingCode());
        pg.setNraPayment(reimbursableDoc.getTravelPayment().isAlienPaymentCode());

        pg.setBankCode(reimbursableDoc.getFinancialDocumentBankCode());
        pg.setPaymentStatusCode(PdpConstants.PaymentStatusCodes.OPEN);

        // now add the payment detail
        final PaymentDetail paymentDetail = buildPaymentDetail(reimbursableDoc, processRunDate);
        pg.addPaymentDetails(paymentDetail);
        paymentDetail.setPaymentGroup(pg);

        return pg;
    }

    /**
     * Builds the PaymentDetail for the given reimbursable travel & entertainment document
     * @param document the reimbursable travel & entertainment document to create a payment for
     * @param processRunDate the date when the extraction is occurring
     * @return a PaymentDetail to add to the PaymentGroup
     */
    protected PaymentDetail buildPaymentDetail(TEMReimbursementDocument document, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("buildPaymentDetail() started");
        }

        PaymentDetail pd = new PaymentDetail();
        if (StringUtils.isNotEmpty(document.getDocumentHeader().getOrganizationDocumentNumber())) {
            pd.setOrganizationDocNbr(document.getDocumentHeader().getOrganizationDocumentNumber());
        }
        pd.setCustPaymentDocNbr(document.getDocumentNumber());
        pd.setInvoiceDate(new java.sql.Date(processRunDate.getTime()));
        pd.setOrigInvoiceAmount(document.getTravelPayment().getCheckTotalAmount());
        pd.setInvTotDiscountAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherCreditAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherDebitAmount(KualiDecimal.ZERO);
        pd.setInvTotShipAmount(KualiDecimal.ZERO);
        pd.setNetPaymentAmount(document.getTravelPayment().getCheckTotalAmount());
        pd.setPrimaryCancelledPayment(Boolean.FALSE);
        pd.setFinancialDocumentTypeCode(DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);
        pd.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);

        // Handle accounts
        for (SourceAccountingLine sal : (List<? extends SourceAccountingLine>)document.getSourceAccountingLines()) {
            PaymentAccountDetail pad = new PaymentAccountDetail();
            pad.setFinChartCode(sal.getChartOfAccountsCode());
            pad.setAccountNbr(sal.getAccountNumber());
            if (StringUtils.isNotEmpty(sal.getSubAccountNumber())) {
                pad.setSubAccountNbr(sal.getSubAccountNumber());
            }
            else {
                pad.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
            }
            pad.setFinObjectCode(sal.getFinancialObjectCode());
            if (StringUtils.isNotEmpty(sal.getFinancialSubObjectCode())) {
                pad.setFinSubObjectCode(sal.getFinancialSubObjectCode());
            }
            else {
                pad.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            if (StringUtils.isNotEmpty(sal.getOrganizationReferenceId())) {
                pad.setOrgReferenceId(sal.getOrganizationReferenceId());
            }
            if (StringUtils.isNotEmpty(sal.getProjectCode())) {
                pad.setProjectCode(sal.getProjectCode());
            }
            else {
                pad.setProjectCode(KFSConstants.getDashProjectCode());
            }
            pad.setAccountNetAmount(sal.getAmount());
            pd.addAccountDetail(pad);
        }

        // Handle notes
        final Person initiator = getPersonService().getPerson(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        int line = 0;
        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        pnt.setCustomerNoteText("Info: " + initiator.getPrincipalName() + " " + initiator.getPhoneNumber());
        pd.addNote(pnt);

        if (StringUtils.isNotEmpty(document.getTravelPayment().getSpecialHandlingPersonName())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Send Check To: " + document.getTravelPayment().getSpecialHandlingPersonName());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling person name note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(document.getTravelPayment().getSpecialHandlingLine1Addr())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(document.getTravelPayment().getSpecialHandlingLine1Addr());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 1 note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(document.getTravelPayment().getSpecialHandlingLine2Addr())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(document.getTravelPayment().getSpecialHandlingLine2Addr());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 2 note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(document.getTravelPayment().getSpecialHandlingCityName())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(document.getTravelPayment().getSpecialHandlingCityName() + ", " + document.getTravelPayment().getSpecialHandlingStateCode() + " " + document.getTravelPayment().getSpecialHandlingZipCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling city note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (document.getTravelPayment().isAttachmentCode()) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Attachment Included");
            if (LOG.isDebugEnabled()) {
                LOG.debug("create attachment note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }

        // Get the original, raw form, note text from the DV document.
        final String text = document.getTravelPayment().getCheckStubText();
        if (!StringUtils.isBlank(text)) {
            pnt = this.getPaymentSourceExtractionService().buildNoteForCheckStubText(text, line);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating check stub text note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }

        return pd;
    }

    /**
     * @return an implementation of the DocumentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the implementation of the DocumentService for this service to use
     * @param parameterService an implementation of DocumentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @return an implementation of the TravelerService
     */
    public TravelerService getTravelerService() {
        return travelerService;
    }

    /**
     * Sets the implementation of the TravelerService for this service to use
     * @param parameterService an implementation of TravelerService
     */
    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    /**
     * @return an implementation of the PaymentSourceExtractionService
     */
    public PaymentSourceExtractionService getPaymentSourceExtractionService() {
        return paymentSourceExtractionService;
    }

    /**
     * Sets the implementation of the PaymentSourceExtractionService for this service to use
     * @param parameterService an implementation of PaymentSourceExtractionService
     */
    public void setPaymentSourceExtractionService(PaymentSourceExtractionService paymentSourceExtractionService) {
        this.paymentSourceExtractionService = paymentSourceExtractionService;
    }

    /**
     * @return an implementation of the PersonService
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Sets the implementation of the PersonService for this service to use
     * @param parameterService an implementation of PersonService
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * @return an implementation of the DAO for TravelDocuments
     */
    public TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    /**
     * Sets the implementation of the DAO for TravelDocuments for this service to use
     * @param parameterService an implementation of the data access object for travel documents
     */
    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    /**
     * @return an implementation of the WorkflowDocumentService
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the implementation of the WorkflowDocumentService for this service to use
     * @param parameterService an implementation of WorkflowDocumentService
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

}
