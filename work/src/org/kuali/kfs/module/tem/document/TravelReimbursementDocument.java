/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementStatusCodeKeys;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Travel Reimbursement Document
 */
@Entity
@Table(name = "TEM_TRVL_REIMB_DOC_T")
public class TravelReimbursementDocument extends TEMReimbursementDocument implements AmountTotaling {

    public static Logger LOG = Logger.getLogger(TravelReimbursementDocument.class);
    private volatile static IdentityService identityService;

    @Column(name = "final_reimb_ind", nullable = false, length = 1)
    private Boolean finalReimbursement = Boolean.FALSE;
    private Boolean employeeCertification = Boolean.FALSE;
    private String contactName;
    private String contactPhoneNum;
    private String contactEmailAddress;
    private String contactCampusCode;
    private KualiDecimal perDiemAdjustment;
    private KualiDecimal travelAdvanceAmount = KualiDecimal.ZERO;
    @Transient
    private KualiDecimal reimbursableAmount = KualiDecimal.ZERO;
    @Transient
    private List<TravelAdvance> travelAdvances;

    public TravelReimbursementDocument() {
    }

    public Boolean getFinalReimbursement() {
        return finalReimbursement;
    }

    public void setFinalReimbursement(final Boolean finalReimbursement) {
        this.finalReimbursement = finalReimbursement;
    }

    @Column(name = "emp_cert_ind", nullable = false, length = 1)
    public Boolean getEmployeeCertification() {
        return employeeCertification;
    }

    public void setEmployeeCertification(final Boolean employeeCertification) {
        this.employeeCertification = employeeCertification;
    }

    public Boolean getNonEmployeeCertification() {
        return !employeeCertification;
    }

    public void setNonEmployeeCertification(final Boolean employeeCertification) {
        this.employeeCertification = !employeeCertification;
    }

    @Column(name = "con_nm", length = 40, nullable = false)
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Column(name = "con_phone_nbr", length = 20, nullable = false)
    public String getContactPhoneNum() {
        return contactPhoneNum;
    }

    public void setContactPhoneNum(String contactPhoneNum) {
        this.contactPhoneNum = contactPhoneNum;
    }

    @Column(name = "con_email_addr", length = 40, nullable = true)
    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress;
    }

    @Column(name = "con_campus_cd", length = 2, nullable = true)
    public String getContactCampusCode() {
        return contactCampusCode;
    }

    public void setContactCampusCode(String contactCampusCode) {
        this.contactCampusCode = contactCampusCode;
    }

    @Override
    public void setPerDiemAdjustment(final KualiDecimal perDiemAdjustment) {
        this.perDiemAdjustment = perDiemAdjustment;
    }

    @Override
    public KualiDecimal getPerDiemAdjustment() {
        return perDiemAdjustment;
    }

    /**
     * Gets the travelAdvanceAmount attribute. This is the travel advance amount applied on this TR.
     * @return Returns the travelAdvanceAmount.
     */
    public KualiDecimal getTravelAdvanceAmount() {
        return travelAdvanceAmount;
    }

    /**
     * Sets the travelAdvanceAmount attribute value. This is the travel advance amount applied on this TR.
     * @param travelAdvanceAmount The travelAdvanceAmount to set.
     */
    public void setTravelAdvanceAmount(KualiDecimal travelAdvanceAmount) {
        this.travelAdvanceAmount = travelAdvanceAmount;
    }

    public KualiDecimal getReimbursableAmount() {
        return reimbursableAmount;
    }

    public void setReimbursableAmount(KualiDecimal reimbursableAmount) {
        this.reimbursableAmount = reimbursableAmount;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        LOG.debug("Handling route status change");

        if (DocumentStatus.PROCESSED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {

            // 1. process the reimbursement on the TR
            try {
                // update the status to Dept approved
                updateAppDocStatus(TravelReimbursementStatusCodeKeys.DEPT_APPROVED);
                getTravelReimbursementService().processCustomerReimbursement(this);
            }
            catch (Exception e) {
                LOG.error("Could not spawn CRM or DV on FINAL for travel id " + getTravelDocumentIdentifier());
                LOG.error(e.getMessage(), e);
            }

            try {
                final TravelAuthorizationDocument openAuthorization = getTravelReimbursementService().getRelatedOpenTravelAuthorizationDocument(this);
                List<Document> authorizations = getTravelDocumentService().getDocumentsRelatedTo(this, TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);

                // 2. if there is an authorization and there isn't a TAC then we process dis-encumbrance
                if (openAuthorization != null) {
                    // check TAC existance
                    List<Document> relatedCloseDocuments = getTravelDocumentService().getDocumentsRelatedTo(openAuthorization, TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);

                    // Trip is encumbrance and no TAC(TAC doc would have handled dis encumbrance)
                    if (relatedCloseDocuments.isEmpty()) {
                        // 3. No TAC - then if it is indicated as final TR - we will spawn the TAC doc
                        if (getFinalReimbursement()) {
                            // store this so we can reset after we're finished
                            final String initiatorId = getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
                            final Principal initiator = getIdentityService().getPrincipal(initiatorId);
                            GlobalVariables.doInNewGlobalVariables(new UserSession(initiator.getPrincipalName()), new Callable<Object>(){
                                @Override
                                public Object call() {
                                    //close the authorization
                                    getTravelAuthorizationService().closeAuthorization(openAuthorization, getTripDescription(), initiator.getPrincipalName());
                                    return null;
                                }
                            });
                        }
                    }
                }
                // if open authorization is null, try to check if there is ANY authorization at all (may be it was closed manually; so its not opened)
                else if (!authorizations.isEmpty()){
                    // authorization that is not opened; note to the TR document
                    Note newNote = getDocumentService().createNoteFromDocument(this, "TA is no longer Open; skip Dis-encumberance process.");
                    addNote(newNote);
                    getDocumentDao().save(this);
                }
            }catch (Exception e) {
                LOG.error("Could Add notes for annotation to TR doc #" + getDocumentNumber());
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();

        getNotes().clear();
        addContactInformation();
    }

    /**
     * Adds contact information of the initiator of the {@link TravelReimbursementDocument} instance
     * to the {@link TravelReimbursementDocument}. Initiator is determined as the current person in the
     * user session
     *
     * @param document to modify
     */
    public void addContactInformation() {
        final String initiatorName = GlobalVariables.getUserSession().getPrincipalName();
        final Person initiator = getPersonService().getPersonByPrincipalName(initiatorName);
        this.setContactName(initiator.getName());
        this.setContactPhoneNum(initiator.getPhoneNumber());
        this.setContactEmailAddress(initiator.getEmailAddress());
        this.setContactCampusCode(initiator.getCampusCode());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        super.initiateDocument();
        setAppDocStatus(TravelReimbursementStatusCodeKeys.IN_PROCESS);
        getTravelPayment().setDocumentationLocationCode(getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, TravelParameters.DOCUMENTATION_LOCATION_CODE,
                getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.DOCUMENTATION_LOCATION_CODE)));
    }

    /**
     * Provides answers to the following splits: PurchaseWasReceived VendorIsEmployeeOrNonResidentAlien
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.ACCOUNT_APPROVAL_REQUIRED)) {
            return requiresAccountApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.DIVISION_APPROVAL_REQUIRED)) {
            return requiresDivisionApprovalRouting() && isNotAutomaticReimbursement();
        }
        if (nodeName.equals(TemWorkflowConstants.SPECIAL_REQUEST)) {
            return requiresSpecialRequestReviewRouting() && isNotAutomaticReimbursement();
        }
        if (nodeName.equals(TemWorkflowConstants.INTL_TRAVEL)) {
            return requiresInternationalTravelReviewRouting() && isNotAutomaticReimbursement();
        }
        if (nodeName.equals(TemWorkflowConstants.TAX_MANAGER_APPROVAL_REQUIRED)) {
            return requiresTaxManagerApprovalRouting() && isNotAutomaticReimbursement();
        }
        if (nodeName.equals(TemWorkflowConstants.SEPARATION_OF_DUTIES)) {
            return requiresSeparationOfDutiesRouting() && isNotAutomaticReimbursement();
        }
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_TRAVELER_REVIEW)){
            return requiresTravelerApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_AWARD) || nodeName.equals(TemWorkflowConstants.REQUIRES_SUB_FUND)) {
            return isNotAutomaticReimbursement();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     *
     * @return
     */
    private boolean isNotAutomaticReimbursement() {
        boolean enabled = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, TemConstants.TravelReimbursementParameters.AUTOMATIC_APPROVALS_IND);
        if (!enabled) {
            return true;
        }
        if (!getTraveler().getTravelerTypeCode().equals(TemConstants.EMP_TRAVELER_TYP_CD)) {
            return true;
        }
        if (getActualExpenses() != null && getActualExpenses().size() > 0) {
            for (ActualExpense expense : getActualExpenses()) {
                if (expense.getExpenseTypeObjectCode() != null && expense.getExpenseTypeObjectCode().isReceiptRequired()
                        && getTravelExpenseService().isTravelExpenseExceedReceiptRequirementThreshold(expense) ) {
                    return true;
                }
            }
        }

        final List<Document> authorizations = getTravelDocumentService().getDocumentsRelatedTo(this, TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT, TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);
        if (authorizations != null && !authorizations.isEmpty()) {
            for (Document doc : authorizations) {
                TravelAuthorizationDocument auth = (TravelAuthorizationDocument)doc;
                if (!ObjectUtils.isNull(auth.getTravelAdvance()) && auth.shouldProcessAdvanceForDocument() && (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(auth.getAdvanceTravelPayment().getPaymentMethodCode()) || KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_DRAFT.equals(auth.getAdvanceTravelPayment().getPaymentMethodCode()))) {
                    return true;
                }
            }
        }

        KualiDecimal trTotal = KualiDecimal.ZERO;
        List<AccountingLine> lines = getSourceAccountingLines();
        for (AccountingLine line : lines) {
            trTotal = trTotal.add(line.getAmount());
        }
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KRADPropertyConstants.CODE, this.getTripTypeCode());
        TripType tripType = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TripType.class, fieldValues);
        KualiDecimal threshold = tripType.getAutoTravelReimbursementLimit();
        if (trTotal.isGreaterEqual(threshold)) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#requiresInternationalTravelReviewRouting()
     */
    @Override
    protected boolean requiresInternationalTravelReviewRouting() {
        return super.requiresInternationalTravelReviewRouting() && requiresDivisionApprovalRouting();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#requiresDivisionApprovalRouting()
     */
    @Override
    protected boolean requiresDivisionApprovalRouting() {
        boolean reqDivApproval = false;
        KualiDecimal trTotal = getTravelDocumentService().getTotalCumulativeReimbursements(this);
        KualiDecimal divApprovalMax = new KualiDecimal(getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.CUMULATIVE_REIMBURSABLE_AMOUNT_WITHOUT_DIVISION_APPROVAL));
        return (trTotal.isGreaterThan(divApprovalMax)) && requiresAccountingReviewRouting();
    }

    /**
     * @return the current open amount of the encumbrance(s) created by the related travel authorizations
     */
    public KualiDecimal getTotalAuthorizedEncumbrance() {
        final KualiDecimal taTotal = getTravelDocumentService().getTotalAuthorizedEncumbrance(this);
        return taTotal;
    }

    /**
     * @return the total cumulative reimbursements so far
     */
    public KualiDecimal getTotalCumulativeReimbursements() {
        final KualiDecimal trTotal = getTravelDocumentService().getTotalCumulativeReimbursements(this);
        return trTotal;
    }

    /**
     *
     * @return
     */
    private boolean requiresAccountingReviewRouting() {
        // start with getting the TA encumbrance amount
        String percent = getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, TravelReimbursementParameters.REIMBURSEMENT_PERCENT_OVER_ENCUMBRANCE_AMOUNT);

        KualiDecimal taTotal = getTravelDocumentService().getTotalAuthorizedEncumbrance(this);
        if (taTotal.isLessEqual(KualiDecimal.ZERO)) {
            return false;
        }

        KualiDecimal trTotal = getTravelDocumentService().getTotalCumulativeReimbursements(this);
        if (trTotal.isLessEqual(KualiDecimal.ZERO)) {
            return false;
        }

        if (trTotal.isGreaterThan(taTotal)) {
            KualiDecimal subAmount = trTotal.subtract(taTotal);
            KualiDecimal percentOver = (subAmount.divide(taTotal)).multiply(new KualiDecimal(100));
            return (percentOver.isGreaterThan(new KualiDecimal(percent)));
        }
        return false;
    }

    /**
     *
     * @return
     */
    public DocumentDao getDocumentDao() {
        return SpringContext.getBean(DocumentDao.class);
    }

    /**
     * Overridden to respect reimbursable amount logic
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getPaymentAmount()
     */
    @Override
    public KualiDecimal getPaymentAmount() {
        if (!getReimbursableTotal().equals(getReimbursableAmount())){
            return getReimbursableAmount();
        }
        return super.getPaymentAmount();
    }

    /**
     * @return all travel advances associated with the trip this document is reimbursing
     */
    public List<TravelAdvance> getTravelAdvances() {
        if (travelAdvances == null) {
            travelAdvances = getTravelDocumentService().getTravelAdvancesForTrip(getTravelDocumentIdentifier());
        }
        return travelAdvances;
    }

    /**
     *
     * @return
     */
    public KualiDecimal getAdvancesTotal() {
        KualiDecimal retval = KualiDecimal.ZERO;
        if (getTravelAdvanceAmount().isZero()) {
            // Add up the total of the travel advances for this trip
            KualiDecimal advanceTotal = KualiDecimal.ZERO;
            if (getTravelAdvances() != null) {
                for (final TravelAdvance advance : getTravelAdvances()) {
                    advanceTotal = advanceTotal.add(advance.getTravelAdvanceRequested());
                }
            }else {
                advanceTotal = getTravelDocumentService().getAdvancesTotalFor(this);
            }

            // Calculate the amount previously applied on the trip (i.e. there are multiple TRs on the TA and the
            // first TR did not use all of the Travel Advance amount)
            KualiDecimal previouslyAppliedAmount = KualiDecimal.ZERO;
            List<TravelAuthorizationDocument> taDocs = (List<TravelAuthorizationDocument>) getTravelAuthorizationService().find(travelDocumentIdentifier);
            if (ObjectUtils.isNotNull(taDocs) && taDocs.size() == 1) {
                List<Document> trDocs = getTravelDocumentService().getDocumentsRelatedTo(taDocs.get(0), TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
                for (Document doc : trDocs) {
                    TravelReimbursementDocument trDoc = (TravelReimbursementDocument) doc;
                    previouslyAppliedAmount = trDoc.getTravelAdvanceAmount().add(previouslyAppliedAmount);
                }
            }
            retval = advanceTotal.subtract(previouslyAppliedAmount);

            // Note that the travelAdvanceAmount is not set here. It is only set when the APP doc is created.
        }
        else
        {
            retval = getTravelAdvanceAmount();
        }
        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getReimbursableGrandTotal()
     */
    @Override
    public KualiDecimal getReimbursableGrandTotal() {
        KualiDecimal grandTotal = super.getReimbursableGrandTotal();
        return grandTotal.subtract(getAdvancesTotal());
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReportPurpose()
     */
    @Override
    public String getReportPurpose() {
        return getTripDescription();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateVendorPayment(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateVendorPayment(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateVendorPayment(disbursementVoucherDocument);
        String locationCode = getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, TravelParameters.DOCUMENTATION_LOCATION_CODE, getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.DOCUMENTATION_LOCATION_CODE));
        String startDate = new SimpleDateFormat("MM/dd/yyyy").format(this.getTripBegin());
        String endDate = new SimpleDateFormat("MM/dd/yyyy").format(this.getTripEnd());
        String checkStubText = this.getTravelDocumentIdentifier() + ", " + this.getPrimaryDestinationName() + ", " + startDate + " - " + endDate;

        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(locationCode);
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#getEncumbranceTotal()
     */
    @Transient
    @Override
    public KualiDecimal getEncumbranceTotal() {
        return getReimbursableTotal();
    }

    /**
     * Returns TRCA
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getAchCheckDocumentType()
     */
    @Override
    public String getAchCheckDocumentType() {
        return TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_CHECK_ACH_DOCUMENT;
    }

    /**
     * Returns TRWF
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getWireTransferOrForeignDraftDocumentType()
     */
    @Override
    public String getWireTransferOrForeignDraftDocumentType() {
        return TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_WIRE_OR_FOREIGN_DRAFT_DOCUMENT;
    }

    /**
     * Generate TR disencumbrance glpe's
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean result = super.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);
        if (isTripGenerateEncumbrance()) {
            getTravelEncumbranceService().disencumberTravelReimbursementFunds(this, sequenceHelper);
        }
        getTravelReimbursementService().generateEntriesForAdvances(this, sequenceHelper);
        return result;
    }

    /**
     * Overridden to handle the explicit entries generated for travel advance clearing and crediting accounting lines
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);
        if (postable instanceof AccountingLine) {
            final AccountingLine accountingLine = (AccountingLine)postable;
            if (TemConstants.TRAVEL_ADVANCE_CLEARING_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode())) {
                explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                explicitEntry.setFinancialDocumentTypeCode(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_TRAVEL_ADVANCES_DOCUMENT);
            } else if (TemConstants.TRAVEL_ADVANCE_CREDITING_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode())) {
                explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                explicitEntry.setFinancialDocumentTypeCode(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_TRAVEL_ADVANCES_DOCUMENT);
            }
        }
    }

    protected PersonService getPersonService() {
        return SpringContext.getBean(PersonService.class);
    }

    protected DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
    }

    protected EncumbranceService getEncumbranceService() {
        return SpringContext.getBean(EncumbranceService.class);
    }

    protected GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return SpringContext.getBean(GeneralLedgerPendingEntryService.class);
    }

    protected AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return SpringContext.getBean(AccountingDocumentRelationshipService.class);
    }

    /**
     * @return the default implementation of IdentityService
     */
    protected IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = SpringContext.getBean(IdentityService.class);
        }
        return identityService;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#getDisapprovedAppDocStatusMap()
     */
    @Override
    public Map<String, String> getDisapprovedAppDocStatusMap() {
        return TravelReimbursementStatusCodeKeys.getDisapprovedAppDocStatusMap();
    }

    /**
     * Checks the check stub text for the payment
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#prepareForSave(org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        getTravelPayment().setCheckStubText(getTravelDocumentIdentifier() + " " + StringUtils.defaultString(getTripDescription()) + " " + getTripBegin());
    }
}
