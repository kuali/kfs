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
package org.kuali.kfs.module.tem.document;

import static org.kuali.kfs.module.tem.TemConstants.DISBURSEMENT_VOUCHER_DOCTYPE;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.PaymentSourceWireTransfer;
import org.kuali.kfs.sys.businessobject.WireCharge;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class TEMReimbursementDocument extends TravelDocumentBase implements PaymentSource {
    private String paymentMethod = KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_CHECK;
    private TravelPayment travelPayment;
    private PaymentSourceWireTransfer wireTransfer;
    private volatile transient Person initiator;
    private Date corporateCardPaymentExtractDate;
    private Date corporateCardPaymentPaidDate;
    private Date corporateCardPaymentCancelDate;

    private static transient volatile PaymentSourceHelperService paymentSourceHelperService;
    private static transient volatile PaymentSourceExtractionService paymentSourceExtractionService;
    private static transient volatile AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private static transient volatile PurchasingAccountsPayableModuleService purapModuleService;

    @Column(name = "PAYMENT_METHOD", nullable = true, length = 15)
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return the travel payment associated with this document
     */
    public TravelPayment getTravelPayment() {
        return travelPayment;
    }

    /**
     * Sets the travel payment to be associated with this document
     * @param travelPayment a travel payment for this document
     */
    public void setTravelPayment(TravelPayment travelPayment) {
        this.travelPayment = travelPayment;
    }

    /**
     * @return the wire transfer associated with this document
     */
    @Override
    public PaymentSourceWireTransfer getWireTransfer() {
        return wireTransfer;
    }

    /**
     * Sets the wire transfer associated with this document
     * @param wireTransfer the wire transfer for this document
     */
    public void setWireTransfer(PaymentSourceWireTransfer wireTransfer) {
        this.wireTransfer = wireTransfer;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        super.initiateDocument();

        //clear expenses
        setActualExpenses(new ArrayList<ActualExpense>());
        setPerDiemExpenses(new ArrayList<PerDiemExpense>());

        //default dates if null
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        if (getTripBegin() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            setTripBegin(new Timestamp(calendar.getTimeInMillis()));

        }
        if (getTripEnd() == null) {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            setTripEnd(new Timestamp(calendar.getTimeInMillis()));
        }

        // initiate payment and wire transfer
        this.travelPayment = new TravelPayment();
        this.wireTransfer = new PaymentSourceWireTransfer();
        // set due date to the next day
        calendar = getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        travelPayment.setDueDate(new java.sql.Date(calendar.getTimeInMillis()));
        travelPayment.setCheckStubText(getConfigurationService().getPropertyValueAsString(TemKeyConstants.MESSAGE_TEM_REIMBURSEMENT_PAYMENT_HOLD_TEXT));

        // set up the default bank
        setDefaultBankCode();
        updatePayeeTypeForReimbursable();
    }

    /**
     * For reimbursable documents, sets the proper payee type code and profile id after a profile lookup
     */
    public void updatePayeeTypeForReimbursable() {
        if (!ObjectUtils.isNull(getTraveler()) && !ObjectUtils.isNull(getTravelPayment()) && !StringUtils.isBlank(getTraveler().getTravelerTypeCode())) {
            if (getTravelerService().isEmployee(getTraveler())){
                getTravelPayment().setPayeeTypeCode(KFSConstants.PaymentPayeeTypes.EMPLOYEE);
            }else{
                getTravelPayment().setPayeeTypeCode(KFSConstants.PaymentPayeeTypes.CUSTOMER);
            }
        }
    }

    /**
     * change to generate the GLPE on ACTUAL EXPENSE based on Reimbursable minus Advance and its only ONE entry to
     * DV clearning account
     *
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#generateDocumentGeneralLedgerPendingEntries
     * (org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("processDocumentGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - start");

        boolean success = true;
        boolean doGenerate = true;

        success = super.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);

        KualiDecimal reimbursableToTraveler = getAmountForTravelClearing();

        if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(getTravelPayment().getPaymentMethodCode()) && !getWireTransfer().isWireTransferFeeWaiverIndicator()) {
            LOG.debug("generating wire charge gl pending entries.");

            // retrieve wire charge
            WireCharge wireCharge = getPaymentSourceHelperService().retrieveCurrentYearWireCharge();

            // generate debits
            GeneralLedgerPendingEntry chargeEntry = getPaymentSourceHelperService().processWireChargeDebitEntries(this, sequenceHelper, wireCharge);

            // generate credits
            getPaymentSourceHelperService().processWireChargeCreditEntries(this, sequenceHelper, wireCharge, chargeEntry);
        }

        // for wire or drafts generate bank offset entry (if enabled), for ACH and checks offset will be generated by PDP
        if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_WIRE.equals(getTravelPayment().getPaymentMethodCode()) || KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_DRAFT.equals(getTravelPayment().getPaymentMethodCode())) {
            getPaymentSourceHelperService().generateDocumentBankOffsetEntries(this, sequenceHelper, getWireTransferOrForeignDraftDocumentType());
        }

        LOG.info("processDocumentGenerateGeneralLedgerPendingEntries for TEMReimbursementDocument - end");

        return success;
    }

    /**
     * @return the amount that should be used in the travel clearing pending entries
     */
    protected KualiDecimal getAmountForTravelClearing() {
        KualiDecimal reimbursableToTraveler = getTravelReimbursementService().getReimbursableToTraveler(this);
        return reimbursableToTraveler;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        /* change document type based on payment method to pick up different offsets; if payment method code is blank, use the normal doc type name */
        explicitEntry.setFinancialDocumentTypeCode(getPaymentDocumentType());
        if (LOG.isDebugEnabled()) {
            LOG.debug("changing doc type on pending entry " + explicitEntry.getTransactionLedgerEntrySequenceNumber() + " to " + explicitEntry.getFinancialDocumentTypeCode());
        }
    }

    /**
     * @return the document type to use for entries associated with this document, which is based, if possible, on the payment type
     */
    public String getPaymentDocumentType() {
        if (!StringUtils.isBlank(getTravelPayment().getPaymentMethodCode())) {
            if (KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_CHECK.equals(getTravelPayment().getPaymentMethodCode())) {
                return getAchCheckDocumentType();
            }
            return getWireTransferOrForeignDraftDocumentType();
        }
        return getDocumentTypeName();
    }

    /**
     * @return the FSLO document type associated with ach/check entries for this document type
     */
    public abstract String getAchCheckDocumentType();

    /**
     * @return the FSLO document type associated with wire transfer or foreign draft entries for this document type
     */
    public abstract String getWireTransferOrForeignDraftDocumentType();

    /**
     * @return RCCA, the corporate card payment ach/check document type
     */
    public String getCorporateCardPaymentAchCheckDocumentType() {
        return TemConstants.TravelDocTypes.REIMBURSABLE_CORPORATE_CARD_CHECK_ACH_DOCUMENT;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReimbursableTotal()
     */
    @Override
    public KualiDecimal getReimbursableTotal() {
        KualiDecimal eligible = getEligibleAmount();
        final KualiDecimal expenseLimit = getExpenseLimit();

        if (expenseLimit != null && expenseLimit.doubleValue() > 0) {
            return eligible.isGreaterThan(expenseLimit) ? expenseLimit : eligible;
        }

        return eligible;
    }

    /**
     * Get eligible amount
     *
     * @return
     */
    public KualiDecimal getEligibleAmount(){
        return getApprovedAmount().subtract(getCTSTotal()).subtract(getCorporateCardTotal());
    }

    /**
     * Total amount to be paid to Vendor
     *
     * @return
     */
    public KualiDecimal getTotalPaidAmountToVendor() {
        KualiDecimal totalPaidAmountToVendor = KualiDecimal.ZERO;
        List<Document> relatedDisbursementList = getTravelDocumentService().getDocumentsRelatedTo(this, DISBURSEMENT_VOUCHER_DOCTYPE);
        for (Document document : relatedDisbursementList) {
            if (document instanceof DisbursementVoucherDocument) {
                DisbursementVoucherDocument dv = (DisbursementVoucherDocument) document;
                if (dv.getDocumentHeader().getWorkflowDocument().isFinal()) {
                    totalPaidAmountToVendor = totalPaidAmountToVendor.add(dv.getDisbVchrCheckTotalAmount());
                }
            }
        }
        return totalPaidAmountToVendor;
    }

    /**
     * Total requested to be paid
     *
     * @return
     */
    public KualiDecimal getTotalPaidAmountToRequests() {
        KualiDecimal totalPaidAmountToRequests = KualiDecimal.ZERO;

        Set<String> documentNumbers = getAccountingDocumentRelationshipService().huntForRelatedDocumentNumbersWithDocumentType(documentNumber, TemConstants.REQUISITION_DOCTYPE);
        if (documentNumbers != null && !documentNumbers.isEmpty()) {
            List<String> relatedRequisitionDocumentNumbers = new ArrayList<String>();
            relatedRequisitionDocumentNumbers.addAll(documentNumbers);
            totalPaidAmountToRequests = getPurchasingAccountsPayableModuleService().getTotalPaidAmountToRequisitions(relatedRequisitionDocumentNumbers);
        }
        return totalPaidAmountToRequests;
    }

    /**
     *
     * @return
     */
    public KualiDecimal getReimbursableGrandTotal() {
        KualiDecimal grandTotal = KualiDecimal.ZERO;
        grandTotal = getApprovedAmount().add(getTotalPaidAmountToVendor()).add(getTotalPaidAmountToRequests());

        if (!grandTotal.isPositive()) {
            return KualiDecimal.ZERO;
        }

        return grandTotal;
    }

    /**
     * @return the amount that should be disbursed to the traveler for this reimbursement
     */
    public KualiDecimal getPaymentAmount() {
        final KualiDecimal reimbursableExpenseTotal = getEligibleAmount();
        if (reimbursableExpenseTotal.isLessThan(KualiDecimal.ZERO)) {
            return KualiDecimal.ZERO;
        }

        final KualiDecimal reimbursableExpenseTotalWithExpenseLimit = applyExpenseLimit(reimbursableExpenseTotal);
        return reimbursableExpenseTotalWithExpenseLimit;
    }

    /**
     *
     * @return
     */
    public boolean requiresTravelerApprovalRouting() {
        String initiator = getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        String travelerID = getTraveler().getPrincipalId();

        String travelerTypeCode = getTraveler().getTravelerTypeCode();
        if (getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.NON_EMPLOYEE_TRAVELER_TYPE_CODES).contains(travelerTypeCode)) {
            return false;
        }

        boolean routeToTraveler = travelerID != null && !initiator.equals(travelerID);
        return routeToTraveler;
    }


    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getPerDiemAdjustment()
     */
    @Override
    public KualiDecimal getPerDiemAdjustment() {
        //do not use per diem adjustment
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#setPerDiemAdjustment(org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public void setPerDiemAdjustment(KualiDecimal perDiemAdjustment) {
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getExpenseTypeCode()
     */
    @Override
    public String getDefaultCardTypeCode() {
        return TemConstants.ACTUAL_EXPENSE;
    }

    /**
     * @return the value of the travelPayment's attachmentCode
     */
    @Override
    public boolean hasAttachment() {
        return getTravelPayment().isAttachmentCode();
    }

    /**
     * @return the payment method code from the travelPayment
     */
    @Override
    public String getPaymentMethodCode() {
        return getTravelPayment().getPaymentMethodCode();
    }

    public Date getCorporateCardPaymentExtractDate() {
        return corporateCardPaymentExtractDate;
    }

    public void setCorporateCardPaymentExtractDate(Date corporateCardPaymentExtractDate) {
        this.corporateCardPaymentExtractDate = corporateCardPaymentExtractDate;
    }

    public Date getCorporateCardPaymentPaidDate() {
        return corporateCardPaymentPaidDate;
    }

    public void setCorporateCardPaymentPaidDate(Date corporateCardPaymentPaidDate) {
        this.corporateCardPaymentPaidDate = corporateCardPaymentPaidDate;
    }

    public Date getCorporateCardPaymentCancelDate() {
        return corporateCardPaymentCancelDate;
    }

    public void setCorporateCardPaymentCancelDate(Date corporateCardPaymentCancelDate) {
        this.corporateCardPaymentCancelDate = corporateCardPaymentCancelDate;
    }

    /*
     *  Used for searching on ENT and RELO.
     *
     */
    public String getTemProfileName() {
        if (!ObjectUtils.isNull(getTraveler()) && !StringUtils.isBlank(getTraveler().getName())) {
            return getTraveler().getName().toUpperCase();
        }
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Returns the campus code of the initiator
     * @see org.kuali.kfs.sys.document.PaymentSource#getCampusCode()
     */
    @Override
    public String getCampusCode() {
        return getInitiator().getCampusCode();
    }

    /**
     * Caches and returns the Person record of the initiator of this document
     * @return a Person record for the initiator of this document
     */
    protected Person getInitiator() {
        if (initiator == null) {
            initiator = SpringContext.getBean(PersonService.class).getPerson(getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        }
        return initiator;
    }

    /**
     * Overridden to set document number on travel payment and wire transfer
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();

        if (wireTransfer != null) {
            wireTransfer.setDocumentNumber(this.documentNumber);
        }
        if (travelPayment != null) {
            travelPayment.setDocumentNumber(this.documentNumber);
        }
    }

    /**
     * Overridden to extract immediate payment if necerssary
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#doRouteStatusChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            if (getTravelPayment().isImmediatePaymentIndicator()) {
                getPaymentSourceExtractionService().extractSingleImmediatePayment(this);
            }
            if (getImportedExpenses() != null && !getImportedExpenses().isEmpty()) {
                for (ImportedExpense expense : getImportedExpenses()) {
                    if (StringUtils.equals(TemConstants.TRAVEL_TYPE_CTS, expense.getCardType())) {
                        if (expense.getHistoricalTravelExpenseId() != null) {
                            if (ObjectUtils.isNull(expense.getHistoricalTravelExpense())) {
                                expense.refreshReferenceObject(TemPropertyConstants.HISTORICAL_TRAVEL_EXPENSE);
                            }
                            if (!ObjectUtils.isNull(expense.getHistoricalTravelExpense())) {
                                expense.getHistoricalTravelExpense().setReconciled(TemConstants.ReconciledCodes.RECONCILED);
                                expense.getHistoricalTravelExpense().setReconciliationDate(getDateTimeService().getCurrentSqlDate());
                                getBusinessObjectService().save(expense.getHistoricalTravelExpense());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Every reimbursable document debits on entry
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return true;
    }

    /**
     * Determines if any imported expenses on the document have a card type of CORP
     * @return true if there are CORP imported expenses on the document, false otherwise
     */
    public boolean isCorporateCardPayable() {
        if (getImportedExpenses() != null && !getImportedExpenses().isEmpty()) {
            for (ImportedExpense expense : getImportedExpenses()) {
                if (StringUtils.equals(expense.getCardType(), TemConstants.TRAVEL_TYPE_CORP)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Based on which pdp dates are present (extract, paid, canceled), determines a String for the status
     * @return a String representation of the status
     */
    public String getCorporateCardPaymentPdpStatus() {
        if (corporateCardPaymentCancelDate != null) {
            return "Canceled";
        }
        if (corporateCardPaymentPaidDate != null) {
            return "Paid";
        }
        if (corporateCardPaymentExtractDate != null) {
            return "Extracted";
        }
        return "Pre-Extraction";
    }

    /**
     * Reimbursable documents route by profile account if they have no actual expenses, per diem expenses, or reimbursable imported expenses
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#shouldRouteByProfileAccount()
     */
    @Override
    protected boolean shouldRouteByProfileAccount() {
        final boolean shouldRouteByProfileAccount = !hasReimbursableExpenses() || hasOnlyPrepaidExpenses();
        return shouldRouteByProfileAccount;
    }

    /**
     * @return true if there are reimbursable expenses on this document and false if there are not
     */
    public boolean hasReimbursableExpenses() {
        final boolean actualExpenses = hasReimbursableExpenses(getActualExpenses());
        final boolean perDiemExpenses = getPerDiemExpenses() != null && !getPerDiemExpenses().isEmpty();
        final boolean reimbursableImportedExpenses = hasReimbursableExpenses(getImportedExpenses());
        return (actualExpenses || perDiemExpenses || reimbursableImportedExpenses);
    }

    /**
     * @return true if there are reimbursable imported expenses; false otherwise
     */
    protected boolean hasReimbursableExpenses(List<? extends TemExpense> expenses) {
        if (expenses == null || expenses.isEmpty()) {
            return false; // no imported expenses; let's just skip out now...
        }
        for (TemExpense expense : expenses) {
            if (!expense.getNonReimbursable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the expense date if it exists
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getEffectiveDateForMileageRate(org.kuali.kfs.module.tem.businessobject.ActualExpense)
     */
    @Override
    public Date getEffectiveDateForMileageRate(ActualExpense expense) {
        if (expense != null && expense.getExpenseDate() != null) {
            return expense.getExpenseDate();
        }
        return null;
    }

    /**
     * Returns the mileage expense date if it exists
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getEffectiveDateForPerDiem(org.kuali.kfs.module.tem.businessobject.PerDiemExpense)
     */
    @Override
    public Date getEffectiveDateForPerDiem(PerDiemExpense expense) {
        if (expense != null && expense.getMileageDate() != null) {
            return getEffectiveDateForPerDiem(expense.getMileageDate());
        }
        return null;
    }

    /**
     * Just returns the date back
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getEffectiveDateForPerDiem(java.sql.Date)
     */
    @Override
    public Date getEffectiveDateForPerDiem(java.sql.Timestamp expenseDate) {
        return new java.sql.Date(expenseDate.getTime());
    }

    @Override
    public Map<String, String> getDisapprovedAppDocStatusMap() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return the default implementation of the PaymentSourceHelperService
     */
    public static PaymentSourceHelperService getPaymentSourceHelperService() {
       if (paymentSourceHelperService == null) {
           paymentSourceHelperService = SpringContext.getBean(PaymentSourceHelperService.class);
       }
       return paymentSourceHelperService;
    }

    /**
     * @return the default implementation of the PaymentSourceHelperService
     */
    public static PaymentSourceExtractionService getPaymentSourceExtractionService() {
        if (paymentSourceExtractionService == null) {
            paymentSourceExtractionService = SpringContext.getBean(PaymentSourceExtractionService.class, TemConstants.REIMBURSABLE_PAYMENT_SOURCE_EXTRACTION_SERVICE);
        }
        return paymentSourceExtractionService;
    }

    public static AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        if (accountingDocumentRelationshipService == null) {
            accountingDocumentRelationshipService = SpringContext.getBean(AccountingDocumentRelationshipService.class);
        }
        return accountingDocumentRelationshipService;
    }

    public static PurchasingAccountsPayableModuleService getPurchasingAccountsPayableModuleService() {
        if (purapModuleService == null) {
            purapModuleService = SpringContext.getBean(PurchasingAccountsPayableModuleService.class);
        }
        return purapModuleService;
    }

}
