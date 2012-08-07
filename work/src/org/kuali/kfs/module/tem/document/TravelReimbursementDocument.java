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

import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.ALLOW_TR_WITHOUT_TA_IND;
import static org.kuali.kfs.module.tem.TemKeyConstants.TA_MESSAGE_CLOSE_DOCUMENT_TEXT;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.module.tem.util.BufferedLogger.logger;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementStatusCodeKeys;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Travel Reimbursement Document
 */
@Entity
@Table(name = "TEM_TRVL_REIMB_DOC_T")
public class TravelReimbursementDocument extends TEMReimbursementDocument implements AmountTotaling {

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

    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getReimbursableTotal();
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
     * Perform business rules common to all transactional documents when generating general ledger pending entries. Adds dis
     * encumbering explicit entries on top of existing ones
     * 
     * @see org.kuali.rice.kns.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.rice.kns.document.AccountingDocument,
     *      org.kuali.rice.kns.bo.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - start");

        boolean success = true;
        boolean hasClose = false;
        List<TravelAuthorizationDocument> taDocs = new ArrayList<TravelAuthorizationDocument>();
        try {
            taDocs = getTravelDocumentService().find(TravelAuthorizationDocument.class, travelDocumentIdentifier);
            for (TravelAuthorizationDocument doc : taDocs) {
                if (doc instanceof TravelAuthorizationCloseDocument) {
                    hasClose = true;
                    break;
                }
            }
        }
        catch (Exception ex) {
            if (logger().isDebugEnabled()) {
                ex.printStackTrace();
            }
        }

        success = super.generateGeneralLedgerPendingEntries(glpeSourceDetail, sequenceHelper);
        if (success && !taDocs.isEmpty()) {
            if (getTripType() != null && getTripType().isGenerateEncumbrance()) {
                // Only run disencumberFunds once
                //CLEANUP when does this happen? GLPE = double of Sourc Accounting Line will not work on ommited GLPE through DV 
                //                  **NOTE no GLPE is generated as well when pay to CTS (if there is no change)
                if ((!hasClose) && this.getGeneralLedgerPendingEntries().size() == this.getSourceAccountingLines().size() * 2) {
                    getTravelEncumbranceService().disencumberFunds(this);
                }

            }
        }

        debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - end");
        return success;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        
        debug("Handling route status change");
        if (KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getNewRouteStatus()) || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            if (!(KEWConstants.ROUTE_HEADER_FINAL_CD.equals(statusChangeEvent.getOldRouteStatus()) || KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getOldRouteStatus()))) {            
                // for some reason when it goes to final it never updates to the last status
                updateAppDocStatus(TravelReimbursementStatusCodeKeys.DEPT_APPROVED);
                
                if (getFinalReimbursement()) {
                    // store this so we can reset after we're finished
                    UserSession originalUser = GlobalVariables.getUserSession();
                    try {
                        String message = getConfigurationService().getPropertyString(TA_MESSAGE_CLOSE_DOCUMENT_TEXT);
                        String user = GlobalVariables.getUserSession().getPerson().getLastName() + ", " + GlobalVariables.getUserSession().getPerson().getFirstName();
                        String note = StringUtils.replace(message, "{0}", user);
                        
                        TravelAuthorizationDocument currTADocument = getTravelReimbursementService().getRelatedOpenTravelAuthorizationDocument(this); 
    
                        final Note newNote = getDocumentService().createNoteFromDocument(currTADocument, note);
                        final Note newNoteTAC = getDocumentService().createNoteFromDocument(currTADocument, note);
                        getDocumentService().addNoteToDocument(currTADocument, newNote);
                        currTADocument.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION);
                        getDocumentDao().save(currTADocument);
    
                        String initiatorId = this.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
                        Person initiator = getPersonService().getPerson(initiatorId);
    
                        // setting to initiator
                        GlobalVariables.setUserSession(new UserSession(initiator.getPrincipalName()));
                        final TravelAuthorizationCloseDocument tacDocument = toCopyTAC();
                        getDocumentService().addNoteToDocument(tacDocument, newNoteTAC);
                        tacDocument.setTravelDocumentIdentifier(getTravelDocumentIdentifier());
    
                        // switching to KR user to route
                        GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
                        tacDocument.updateAppDocStatus(TravelAuthorizationStatusCodeKeys.CLOSED);
                        getDocumentService().routeDocument(tacDocument, getTripDescription(), null);
                    }
                    catch (Exception e) {
                        error("Could not create TAC or route it with travel id ", getTravelDocumentIdentifier());
                        error(e.getMessage());
                        if (logger().isDebugEnabled()) {
                            e.printStackTrace();
                        }
                    }
                    finally {
                        // reset user session
                        GlobalVariables.setUserSession(originalUser);
                    }
                }

                try {
                    getTravelReimbursementService().processCustomerReimbursement(this);
                }
                catch (Exception e) {
                    error("Could not spawn CRM or DV on FINAL for travel id ", getTravelDocumentIdentifier());
                    error(e.getMessage());
                    if (logger().isDebugEnabled()) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();

        //TODO: super class already performed the cleanTravelDocument, this part seems to do nothing (unless it is moved to the base)
        final boolean isTaFree = getParameterService().getIndicatorParameter(TemParameterConstants.TEM_REIMBURSEMENT.class, ALLOW_TR_WITHOUT_TA_IND);
        if (isTaFree) {
            cleanTravelDocument();
        }
        setBoNotes(new ArrayList<Note>());
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

    public TravelAuthorizationCloseDocument toCopyTAC() throws WorkflowException {

        TravelAuthorizationCloseDocument tacDocument = (TravelAuthorizationCloseDocument) SpringContext.getBean(DocumentService.class).getNewDocument(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT);
        Long typeID = tacDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocTypeId();
        String documentID = tacDocument.getDocumentNumber();
        try {
            FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();
            BeanUtils.copyProperties(documentHeader, tacDocument.getDocumentHeader());
            BeanUtils.copyProperties(tacDocument, this);
            tacDocument.setDocumentHeader(documentHeader);
            tacDocument.getDocumentHeader().getBoNotes().clear();
            tacDocument.setTravelDocumentIdentifier(getTravelDocumentIdentifier());
            tacDocument.setDocumentNumber(documentID);
            tacDocument.getDocumentHeader().setDocumentDescription(this.getDocumentHeader().getDocumentDescription());
            tacDocument.setGeneralLedgerPendingEntries(new ArrayList<GeneralLedgerPendingEntry>());
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return tacDocument;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        updateAppDocStatus(TemConstants.TravelReimbursementStatusCodeKeys.IN_PROCESS);
        setActualExpenses(new ArrayList<ActualExpense>());
        setPerDiemExpenses(new ArrayList<PerDiemExpense>());

        getDocumentHeader().setDocumentDescription(TemConstants.PRE_FILLED_DESCRIPTION);
        if (this.getTraveler() == null) {
            this.setTraveler(new TravelerDetail());
            this.getTraveler().setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        }

        /*Calendar calendar = getDateTimeService().getCurrentCalendar();
        if (this.getTripBegin() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            setTripBegin(new Timestamp(calendar.getTimeInMillis()));

        }
        if (this.getTripEnd() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            setTripEnd(new Timestamp(calendar.getTimeInMillis()));
        }*/

        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if(!getTravelDocumentService().isTravelArranger(currentUser)) {
            TEMProfile temProfile = getTravelService().findTemProfileByPrincipalId(currentUser.getPrincipalId());
            if (temProfile != null) {
                setTemProfile(temProfile);
            }
        }
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
        if (nodeName.equals(TemWorkflowConstants.ACCTG_APPROVAL_REQUIRED)) {
            return requiresAccountingReviewRouting();
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
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_AWARD)
                || nodeName.equals(TemWorkflowConstants.REQUIRES_SUB_FUND)
                || nodeName.equals(TemWorkflowConstants.REQUIRES_AP_TRAVEL)) {
            return isNotAutomaticReimbursement();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * 
     * @return
     */
    private boolean requiresTravelerApprovalRouting() {
        String initiator = this.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
        String travelerID = this.getTraveler().getPrincipalId();

        return travelerID != null && !initiator.equals(travelerID);
    }

    /**
     * 
     * @return
     */
    private boolean isNotAutomaticReimbursement() {
        boolean enabled = getParameterService().getIndicatorParameter(TemParameterConstants.TEM_REIMBURSEMENT.class, TemConstants.TravelReimbursementParameters.ENABLE_AUTOMATIC_TR_IND);
        if (!enabled) {
            return true;
        }
        if (!getTraveler().getTravelerTypeCode().equals(TemConstants.EMP_TRAVELER_TYP_CD)) {
            return true;
        }
        if (getActualExpenses() != null && getActualExpenses().size() > 0) {
            for (ActualExpense expense : getActualExpenses()) {
                if (expense.getTravelExpenseTypeCode() != null && expense.getTravelExpenseTypeCode().getReceiptRequired()) {
                    return true;
                }
            }
        }
        if (getTravelAdvances() != null && getTravelAdvances().size() > 0) {
            for (TravelAdvance advance : getTravelAdvances()) {
                if (advance.getPaymentMethod().equals(TemConstants.DisbursementVoucherPaymentMethods.WIRE_TRANSFER_PAYMENT_METHOD_CODE)
                        || advance.getPaymentMethod().equals(TemConstants.DisbursementVoucherPaymentMethods.FOREIGN_DRAFT_PAYMENT_METHOD_CODE)) {
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
        fieldValues.put(KNSPropertyConstants.CODE, this.getTripTypeCode());
        TripType tripType = (TripType) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TripType.class, fieldValues);
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
        KualiDecimal divApprovalMax = new KualiDecimal(getParameterService().getParameterValue(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.CUMULATIVE_REIMBURSABLE_AMT_WITHOUT_DIV_APPROVAL));
        return (trTotal.isGreaterThan(divApprovalMax)) && requiresAccountingReviewRouting();
    }

    /**
     * 
     * @return
     */
    private boolean requiresAccountingReviewRouting() {
        // start with getting the TA encumbrance amount
        String percent = getParameterService().getParameterValue(TemParameterConstants.TEM_REIMBURSEMENT.class, TravelReimbursementParameters.REIMBURSEMENT_PERCENT_OVER_ENCUMBRANCE_AMT);

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
            if (percentOver.isGreaterThan(new KualiDecimal(percent))) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * 
     * @return
     */
    public DocumentDao getDocumentDao() {
        return SpringContext.getBean(DocumentDao.class);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateDisbursementVoucherFields(disbursementVoucherDocument);
        
        final String paymentReasonCode = getParameterService().getParameterValue(TemParameterConstants.TEM_REIMBURSEMENT.class,TravelReimbursementParameters.TR_REIMBURSEMENT_DV_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(paymentReasonCode);
        final String paymentLocationCode = getParameterService().getParameterValue(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(paymentLocationCode);

        //check if the reimbursable total and the reimbursable amount (reduced by CRM) is different, which means we need to adjust the DV's accounting line amounts
        if (!getReimbursableTotal().equals(getReimbursableAmount())){
            //change the DV's total to the reimbursable amount set previously
            disbursementVoucherDocument.setDisbVchrCheckTotalAmount(getReimbursableAmount());
            
            //Distribute the DV accounting line to the reimbursable amount
            if (getReimbursableSourceAccountingLines().size() > 1){
                getTravelDisbursementService().redistributeDisbursementAccountingLine(disbursementVoucherDocument, getReimbursableSourceAccountingLines());
            }else{
                //there is only one reimbursable source line, go ahead and assign the reimbursable amount to it directly
                disbursementVoucherDocument.getSourceAccountingLine(0).setAmount(getReimbursableAmount());
            }
        }
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
            try {
                List<TravelAuthorizationDocument> taDocs = (List<TravelAuthorizationDocument>) getTravelAuthorizationService().find(travelDocumentIdentifier);
                if (ObjectUtils.isNotNull(taDocs) && taDocs.size() == 1) {
                    Map<String, List<Document>> relatedDocuments = getTravelDocumentService().getDocumentsRelatedTo(taDocs.get(0));
                    List<Document> trDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
                    if (ObjectUtils.isNotNull(trDocs) && trDocs.size() > 0) {
                        for (Document doc : trDocs) {
                            TravelReimbursementDocument trDoc = (TravelReimbursementDocument) doc;
                            previouslyAppliedAmount = trDoc.getTravelAdvanceAmount().add(previouslyAppliedAmount);
                        }
                    }

                }
            }
            catch (Exception ex) {
                error("Could not get related documents to determine advances total previously applied");
                if (logger().isDebugEnabled()) {
                    ex.printStackTrace();
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
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateRequisitionFields(org.kuali.kfs.module.purap.document.RequisitionDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void populateRequisitionFields(RequisitionDocument reqsDoc, TravelDocument document) {
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
        String locationCode = getParameterService().getParameterValue(TemParameterConstants.TEM_DOCUMENT.class,TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE);
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

    protected PersonService<Person> getPersonService() {
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
}
