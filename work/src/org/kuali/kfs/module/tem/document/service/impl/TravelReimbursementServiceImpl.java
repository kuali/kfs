/*
 * Copyright 2010 The Kuali Foundation
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

import static org.kuali.kfs.module.tem.TemConstants.DATE_CHANGED_MESSAGE;
import static org.kuali.kfs.module.tem.TemConstants.DISBURSEMENT_VOUCHER_DOCTYPE;
import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_COVERSHEET_INSTRUCTIONS;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.AR_REFUND_ACCOUNT_NBR;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.AR_REFUND_CHART_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.AR_REFUND_OBJECT_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_LODGING_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_MEAL_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.AIRFARE_EXPENSE_DISABLED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.PER_DIEM_EXPENSE_DISABLED;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.info;
import static org.kuali.kfs.module.tem.util.BufferedLogger.logger;
import static org.kuali.kfs.module.tem.util.BufferedLogger.warn;
import static org.kuali.kfs.sys.KFSConstants.EXTERNALIZABLE_HELP_URL_KEY;

import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.gl.service.EncumbranceService;
import org.kuali.kfs.integration.ar.AccountsReceivableCashControlDetail;
import org.kuali.kfs.integration.ar.AccountsReceivableCashControlDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableInvoicePaidApplied;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableNonInvoiced;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.integration.ar.AccountsReceivablePaymentApplicationDocument;
import org.kuali.kfs.integration.ar.AccountsRecievableDocumentHeader;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

/**
 * Travel Reimbursement Service Implementation
 */
public class TravelReimbursementServiceImpl implements TravelReimbursementService {

    private KualiRuleService kualiRuleService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private ObjectCodeService objectCodeService;
    private DocumentService documentService;
    private KualiConfigurationService kualiConfigurationService;
    private TravelDocumentService travelDocumentService;
    private TravelAuthorizationService travelAuthorizationService;
    private ParameterService parameterService;
    private WorkflowDocumentService workflowDocumentService;
    private PersonService personService;
    private DocumentDao documentDao;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private AccountsReceivableModuleService accountsReceivableModuleService;

    private List<PropertyChangeListener> propertyChangeListeners;

    /**
     * Locate all {@link TravelReimbursementDocument} instances with the same <code>travelDocumentIdentifier</code>
     * 
     * @param travelDocumentIdentifier to locate {@link TravelReimbursementDocument} instances
     * @return {@link Collection} of {@link TravelReimbursementDocument} instances
     */
    public List<TravelReimbursementDocument> findByTravelId(final String travelDocumentIdentifier) throws WorkflowException {
        final List<TravelReimbursementDocument> retval = getTravelDocumentService().find(TravelReimbursementDocument.class, travelDocumentIdentifier);
        for (final TravelReimbursementDocument reimbursement : retval) {
            addListenersTo(reimbursement);
        }
        return retval;
    }

    public TravelReimbursementDocument find(final String documentNumber) throws WorkflowException {
        final TravelReimbursementDocument retval = (TravelReimbursementDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
        addListenersTo(retval);
        return retval;
    }

    public void addListenersTo(final TravelReimbursementDocument reimbursement) {
        if (reimbursement != null) {
            reimbursement.setPropertyChangeListeners(getPropertyChangeListeners());
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelReimbursementService#generateCoversheetFor(java.lang.String, java.lang.String,
     *      org.kuali.kfs.module.tem.document.TravelReimbursementDocument, java.io.OutputStream)
     */
    public Coversheet generateCoversheetFor(final TravelReimbursementDocument document) throws Exception {
        final String docNumber = document.getDocumentNumber();
        final String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        final String instructions = getParameterService().getParameterValue(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, TRAVEL_COVERSHEET_INSTRUCTIONS);
        final String mailTo = travelDocumentService.retrieveAddressFromLocationCode(getParameterService().getParameterValue(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, TRAVEL_DOCUMENTATION_LOCATION_CODE));
        final String destination  = document.getPrimaryDestination().getPrimaryDestinationName();

        final String directory = getConfigurationService().getPropertyString(EXTERNALIZABLE_HELP_URL_KEY);

        final Person initiator = getPersonService().getPerson(initiatorId);
        final TravelerDetail traveler = document.getTraveler();
        traveler.refreshReferenceObject(TemPropertyConstants.CUSTOMER);

        final Coversheet cover = new Coversheet();

        cover.setInstructions(instructions);
        cover.setMailTo(mailTo);
        cover.setTripId(document.getTravelDocumentIdentifier() + "");
        cover.setDate(new SimpleDateFormat("MM/dd/yyyy").format(document.getTripBegin()));
        cover.setInitiatorName(initiator.getFirstName() + " " + initiator.getLastName());
        cover.setInitiatorPrincipalName(initiator.getPrincipalName());
        cover.setInitiatorPhone(initiator.getPhoneNumber());
        cover.setInitiatorEmail(initiator.getEmailAddress());
        cover.setTravelerName(traveler.getFirstName() + " " + traveler.getLastName());
        
        Person person = getPersonService().getPerson(traveler.getPrincipalId());
        cover.setTravelerPrincipalName(person != null ? person.getPrincipalName() : "");
        cover.setTravelerPhone(traveler.getPhoneNumber());
        cover.setTravelerEmail(traveler.getEmailAddress());
        cover.setDestination(destination);
        cover.setDocumentNumber(docNumber);

        final Collection<Map<String, String>> expenses = new ArrayList<Map<String, String>>();
        if (document.getActualExpenses() != null) {
            for (final ActualExpense expense : document.getActualExpenses()) {
                final Map<String, String> expenseMap = new HashMap<String, String>();
                ((org.kuali.rice.kns.bo.PersistableBusinessObject) expense).refreshReferenceObject("travelExpenseTypeCode");
                expenseMap.put("expenseType", expense.getTravelExpenseTypeCode().getName());

                final KualiDecimal rate = expense.getCurrencyRate();
                final KualiDecimal amount = expense.getExpenseAmount();

                expenseMap.put("amount", amount.multiply(rate) + "");

                expenseMap.put("receipt", getReceiptRequired(expense.getTravelExpenseTypeCode()));
                expenses.add(expenseMap);
            }
        }
        
        if(document.getPerDiemExpenses()!=null&&document.getPerDiemExpenses().size()>0){
            final Map<String, String> mealsIncidentialsMap = new HashMap<String, String>();
            mealsIncidentialsMap.put("expenseType", "Meals & Incidentals");
            mealsIncidentialsMap.put("amount", document.getMealsAndIncidentalsGrandTotal().toString());
            mealsIncidentialsMap.put("receipt", "-");
            expenses.add(mealsIncidentialsMap);
            final Map<String, String> lodgingMap = new HashMap<String, String>();
            lodgingMap.put("expenseType", "Lodging");
            lodgingMap.put("amount", document.getLodgingGrandTotal().toString());
            lodgingMap.put("receipt", getReceiptRequired(TravelParameters.EXPENSE_TYPE_FOR_LODGING, document));
            expenses.add(lodgingMap);
            final Map<String, String> mileageMap = new HashMap<String, String>();
            mileageMap.put("expenseType", "Mileage");
            mileageMap.put("amount", document.getMilesGrandTotal().toString());
            mileageMap.put("receipt", getReceiptRequired(TravelParameters.EXPENSE_TYPE_FOR_MILEAGE, document));
            expenses.add(mileageMap);
        }
        
        cover.setExpenses(expenses);

        return cover;
    }
    
    protected String getReceiptRequired(String expenseType, final TravelDocument document) {
        final String expenseTypeCode = getParameterService().getParameterValue(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, expenseType);
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.CODE, expenseTypeCode);
        primaryKeys.put(TemPropertyConstants.TRIP_TYPE, document.getTripTypeCode());
        primaryKeys.put(TemPropertyConstants.TRAVELER_TYPE, document.getTraveler().getTravelerTypeCode());
        primaryKeys.put(KFSPropertyConstants.DOCUMENT_TYPE, document.getDocumentTypeName());

        return getReceiptRequired((TemTravelExpenseTypeCode) businessObjectService.findByPrimaryKey(TemTravelExpenseTypeCode.class, primaryKeys));
    }
    
    protected String getReceiptRequired(TemTravelExpenseTypeCode expenseTypeCode) {
        String receipt = "-";
        if(ObjectUtils.isNotNull(expenseTypeCode) && ObjectUtils.isNotNull(expenseTypeCode.getReceiptRequired())) {
        	if(expenseTypeCode.getReceiptRequired()) {
        		receipt = "Yes";
        	} else {
        		receipt = "No";
        	}
        }
        
        return receipt;
    }

    public void addDateChangedNote(TravelReimbursementDocument travelReqDoc, TravelAuthorizationDocument taDoc) {
        // get original dates
        final Date startDateIn = taDoc.getTripBegin();
        final Date endDateIn = taDoc.getTripEnd();

        // check new dates against original dates
        final Date currentStart = travelReqDoc.getTripBegin();
        final Date currentEnd = travelReqDoc.getTripEnd();
        if (areDatesNull(startDateIn, currentStart, endDateIn, currentEnd)) {
            // skip the try and keep rolling
        }
        else if (haveDatesChanged(startDateIn, currentStart, endDateIn, currentEnd)) {
            try {
                notifyDateChangedOn(travelReqDoc, startDateIn, endDateIn);
            }
            catch (Exception e) {
                warn("Could not add a note to reimbursement with document number: ", travelReqDoc.getDocumentHeader().getDocumentNumber());
                warn(e.getMessage());
                if (logger().isDebugEnabled()) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected Boolean areDatesNull(Date startDateIn, Date currentStart, Date endDateIn, Date currentEnd) {
        Boolean nullDates = new Boolean(false);
        if (startDateIn == null) {
            nullDates = true;
        }
        else if (currentStart == null) {
            nullDates = true;
        }
        else if (endDateIn == null) {
            nullDates = true;
        }
        else if (currentEnd == null) {
            nullDates = true;
        }
        return nullDates;
    }

    protected Boolean haveDatesChanged(Date startDateIn, Date currentStart, Date endDateIn, Date currentEnd) {
        Boolean diffDates = new Boolean(false);
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        final String origStartDateStr = formatter.format(startDateIn);
        final String origEndDateStr = formatter.format(endDateIn);
        final String currStartDateStr = formatter.format(currentStart);
        final String currEndDateStr = formatter.format(currentEnd);

        if (origStartDateStr.equals(currStartDateStr)) {
            // starts are good
        }
        else {
            diffDates = true;
        }
        if (origEndDateStr.equals(currEndDateStr)) {
            // ends are good
        }
        else {
            diffDates = true;
        }
        return diffDates;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelReimbursementService#notifyDateChangedOn(TravelReimbursementDocument, Date, Date)
     */
    public void notifyDateChangedOn(final TravelReimbursementDocument reimbursement, final Date start, final Date end) throws Exception {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        final String origStartDateStr = formatter.format(start);
        final String origEndDateStr = formatter.format(end);
        final String newStartDateStr = formatter.format(reimbursement.getTripBegin());
        final String newEndDateStr = formatter.format(reimbursement.getTripEnd());

        final String noteText = String.format(DATE_CHANGED_MESSAGE, origStartDateStr, origEndDateStr, newStartDateStr, newEndDateStr);

        final Note noteToAdd = getDocumentService().createNoteFromDocument(reimbursement, noteText);
        getDocumentService().addNoteToDocument(reimbursement, noteToAdd);
        getDocumentDao().save(reimbursement);
    }

    /**
     * Gets the {@link OrganizationOptions} to create a {@link AccountsReceivableDocumentHeader} for
     * {@link PaymentApplicationDocument}
     * 
     * @return OrganizationOptions
     */
    protected AccountsReceivableOrganizationOptions getOrgOptionsForPayment() {
        final Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", getParameterService().getParameterValue(PARAM_NAMESPACE,
                TravelAuthorizationParameters.PARAM_DTL_TYPE,
                TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_CHART_CODE));
        criteria.put("organizationCode", getParameterService().getParameterValue(PARAM_NAMESPACE,
                TravelAuthorizationParameters.PARAM_DTL_TYPE,
                TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_ORG_CODE));
        
        return getAccountsReceivableModuleService().getOrganizationOptionsByPrimaryKey(criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelReimbursementService#spawnCashConrolDocument(TravelReimbursementDocument)
     */
    public void spawnCashControlDocumentFrom(final TravelReimbursementDocument reimbursement) throws WorkflowException {
        final AccountsReceivablePaymentApplicationDocument paymentApplication = createPaymentApplicationFor(reimbursement);

        info("Blanket Approving APP with travelDocumentIdentifier ", reimbursement.getTravelDocumentIdentifier());

        UserSession originalUser = GlobalVariables.getUserSession();
        KualiWorkflowDocument originalWorkflowDocument = paymentApplication.getDocumentHeader().getWorkflowDocument();
        
        try {
            // original initiator may not have permission to blanket approve the APP
            GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));

            KualiWorkflowDocument newWorkflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(paymentApplication.getDocumentNumber()), GlobalVariables.getUserSession().getPerson());
            newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());

            paymentApplication.getDocumentHeader().setWorkflowDocument(newWorkflowDocument);

            getAccountsReceivableModuleService().blanketApprovePaymentApplicationDocument(paymentApplication, reimbursement.getTravelDocumentIdentifier());

            final String noteText = String.format("Application Document %s was system generated.", paymentApplication.getDocumentNumber());
            final Note noteToAdd = getDocumentService().createNoteFromDocument(reimbursement, noteText);
            getDocumentService().addNoteToDocument(reimbursement, noteToAdd);
        }
        catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        finally {
            GlobalVariables.setUserSession(originalUser);
            paymentApplication.getDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
        }
        
        // add relationship
        String relationDescription = "TR - Payment Application";
        accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(reimbursement.getDocumentNumber(), paymentApplication.getDocumentNumber(), relationDescription));
        
        //relationDescription = "TR - Cash Control";
        //accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(reimbursement.getDocumentNumber(), paymentApplication.getAccountsReceivableCashControlDetail().getAccountsReceivableCashControlDocument().getDocumentNumber(), relationDescription));
    }

    protected Collection<DisbursementVoucherDocument> getDisbursementVouchersFor(final Integer trip) throws WorkflowException {
        final Collection<DisbursementVoucherDocument> retval = new ArrayList<DisbursementVoucherDocument>();
        final Collection<DocumentHeader> headers = getHeadersWith(trip);
        for (final DocumentHeader header : headers) {

            boolean valid = false;
            while (!valid) {
                try {
                    if (DISBURSEMENT_VOUCHER_DOCTYPE.equals(header.getWorkflowDocument().getDocumentType())
                            && header.getWorkflowDocument().stateIsSaved()) {
                        retval.add((DisbursementVoucherDocument) getDocumentService().getByDocumentHeaderId(header.getDocumentNumber()));
                    }
                }
                catch (Exception e) {

                }
                valid = true;
            }
        }
        return retval;
    }

    /**
     * Creates a {@link CashControlDetail} instance for a {@link CashControlDocument}. It's necessary to do this because it will
     * generate an APP or {@link PaymentApplicationDocument}.
     * 
     * @param cashControl
     * @param reimbursement
     * @return CashControlDetail
     */
    protected void createDetailsFor(final AccountsReceivablePaymentApplicationDocument payment, final TravelReimbursementDocument reimbursement) throws WorkflowException {
        final AccountsReceivableCashControlDetail newCashControlDetail = getAccountsReceivableModuleService().createCashControlDetail();
        final AccountsReceivableCashControlDocument cashControl = getAccountsReceivableModuleService().createCashControlDocument();

        final String processingChart = payment.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        final String processingOrg = payment.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();

        final AccountsRecievableDocumentHeader arDocHeader = getAccountsReceivableModuleService().getNewAccountsReceivableDocumentHeader(processingChart, processingOrg);
        arDocHeader.setDocumentNumber(reimbursement.getDocumentNumber());
        arDocHeader.setCustomerNumber(reimbursement.getTraveler().getCustomerNumber());
        cashControl.setAccountsReceivableDocumentHeader(arDocHeader);

        newCashControlDetail.setDocumentNumber(cashControl.getDocumentNumber());
        newCashControlDetail.setCustomerNumber(reimbursement.getTraveler().getCustomerNumber());
        newCashControlDetail.setFinancialDocumentLineAmount(reimbursement.getReimbursableTotal());
        newCashControlDetail.setCustomerPaymentMediumIdentifier(reimbursement.getDocumentNumber());

        // add cash control detail. implicitly saves the cash control document
        debug("Adding a new cash control detail");
        newCashControlDetail.setCashControlDocument(cashControl);
        List<AccountsReceivableCashControlDetail> accountsReceivableCashControlDetails = cashControl.getAccountsReceivableCashControlDetails();
        accountsReceivableCashControlDetails.add(newCashControlDetail);
        cashControl.setAccountsReceivableCashControlDetails(accountsReceivableCashControlDetails);
        payment.setCashControlDetail(newCashControlDetail);
    }

    /**
     * Creates a payment application based on a {@link CashControLDocument}. This requires a {@link CashControlDetail} which is
     * created within. A {@link TravelReimbursementDocument} is used as metadata
     * 
     * @param cashControl
     * @param reimbursement
     * @return {@link PaymentApplicationDocument}
     */
    protected AccountsReceivablePaymentApplicationDocument createPaymentApplicationFor(final TravelReimbursementDocument reimbursement) throws WorkflowException {
        // create a new PaymentApplicationdocument
        final AccountsReceivablePaymentApplicationDocument retval = getAccountsReceivableModuleService().createPaymentApplicationDocument();

        // set a description to say that this application document has been created by the CashControldocument
        retval.getDocumentHeader().setDocumentDescription(reimbursement.getDocumentHeader().getDocumentDescription());

        // the line amount for the new PaymentApplicationDocument should be the line amount in the new cash control detail
        final KualiDecimal cashControlTotal = reimbursement.getTotalDollarAmount();
        retval.getDocumentHeader().setFinancialDocumentTotalAmount(cashControlTotal);

        final AccountsReceivableOrganizationOptions orgOptions = getOrgOptionsForPayment();
        final String processingChart = orgOptions.getProcessingChartOfAccountCode();
        final String processingOrg = orgOptions.getProcessingOrganizationCode();

        final AccountsRecievableDocumentHeader arDocHeader = getAccountsReceivableModuleService().getNewAccountsReceivableDocumentHeader(processingChart, processingOrg);
        arDocHeader.setDocumentNumber(retval.getDocumentNumber());
        arDocHeader.setCustomerNumber(reimbursement.getTraveler().getCustomerNumber());
        retval.setAccountsReceivableDocumentHeader(arDocHeader);
        
        //String[] travelDocumentIdentifier = reimbursement.getTravelDocumentIdentifier().split("-");        
        retval.getDocumentHeader().setOrganizationDocumentNumber(reimbursement.getTravelDocumentIdentifier());
        // refresh nonupdatable references and save the PaymentApplicationDocument
        retval.refreshNonUpdateableReferences();

        debug("Created payment with customer number ", retval.getAccountsReceivableDocumentHeader().getCustomerNumber());

        createDetailsFor(retval, reimbursement);

        final KualiDecimal invoicesTotal = getInvoicesTotalFor(reimbursement.getTraveler().getCustomerNumber());

        debug("Invoice total ", invoicesTotal);
        debug("Cash Control Total is ", retval.getTotalFromControl());
        KualiDecimal noninvoiced = KualiDecimal.ZERO;
        for (final AccountsReceivableNonInvoiced item : retval.getAccountsReceivableNonInvoiceds()) {
            noninvoiced = noninvoiced.add(item.getFinancialDocumentLineAmount());
        }
        debug("Non Invoiced Total is ", noninvoiced);

        KualiDecimal refundTotal = KualiDecimal.ZERO;
        if (cashControlTotal.isGreaterThan(invoicesTotal)) {
            refundTotal = cashControlTotal.subtract(invoicesTotal);
        }
        debug("Expecting refund of ", refundTotal);
        if (KualiDecimal.ZERO.isLessThan(refundTotal)) {
            createNonInvoicedItemFor(retval, refundTotal);
        }

        debug("NonInvoice Total is ", retval.getSumOfNonInvoiceds());

        if (KualiDecimal.ZERO.isLessThan(invoicesTotal)) {
            applyPaymentsOn(retval, cashControlTotal);
        }

        KualiDecimal appliedTotal = KualiDecimal.ZERO;
        List<AccountsReceivableInvoicePaidApplied> accountsReceivableInvoicePaidApplieds = retval.getAccountsReceivableInvoicePaidApplieds();
        for (final AccountsReceivableInvoicePaidApplied invoicePaidApplied : accountsReceivableInvoicePaidApplieds) {
            invoicePaidApplied.refreshReferenceObject("invoiceDetail");
            appliedTotal = appliedTotal.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        retval.setAccountsReceivableInvoicePaidApplieds(accountsReceivableInvoicePaidApplieds);
        
        debug("The applied total is now ", appliedTotal);
        reimbursement.setTravelAdvanceAmount(appliedTotal);

        return retval;
    }

    /**
     * Applies payments on the {@link PaymentApplicationDocument} according to the <code>cashControlTotal</code>. Iterates over
     * invoices. Each {@link CustomerInvoiceDocument} gets its own {@link InvoicePaidApplied} instance added to the
     * {@link PaymentApplicationDocument}. As more is invoices are applied, the <code>cashControlTotal</code> is decrimented by the
     * invoice amount until it reaches 0.
     * 
     * @param payment
     * @param cashControlTotal
     * @return remaining amount from applying reimbursement to invoices
     */
    protected void applyPaymentsOn(final AccountsReceivablePaymentApplicationDocument payment, final KualiDecimal cashControlTotal) {
        final String customerNumber = payment.getAccountsReceivableDocumentHeader().getCustomerNumber();
        KualiDecimal remaining = KualiDecimal.ZERO.add(cashControlTotal);

        Integer sequence = 1;

        final Collection<AccountsReceivableCustomerInvoice> invoices = getAccountsReceivableModuleService().getOpenInvoiceDocumentsByCustomerNumber(customerNumber);
        debug("There are ", invoices.size(), " invoices");
        for (final AccountsReceivableCustomerInvoice invoice : invoices) {
            debug("Remaining cash control total is ", remaining);
            if (remaining.isGreaterThan(KualiDecimal.ZERO)) {
                KualiDecimal applyAmount = invoice.getOpenAmount();
                debug("Got invoice with open amount ", applyAmount, " docNbr: ", invoice.getDocumentNumber());

                if (applyAmount.isGreaterThan(remaining)) {
                    applyAmount = KualiDecimal.ZERO.add(remaining);
                }

                debug("Applying ", applyAmount);
                final AccountsReceivableInvoicePaidApplied toApply = getAccountsReceivableModuleService().createInvoicePaidApplied();
                toApply.setDocumentNumber(payment.getDocumentNumber());
                toApply.setFinancialDocumentReferenceInvoiceNumber(invoice.getDocumentNumber());
                toApply.setInvoiceItemNumber(sequence++);
                toApply.setPaidAppliedItemNumber(0);
                toApply.setInvoiceItemAppliedAmount(applyAmount);
                
                List<AccountsReceivableInvoicePaidApplied> accountsReceivableInvoicePaidApplieds = payment.getAccountsReceivableInvoicePaidApplieds();
                accountsReceivableInvoicePaidApplieds.add(toApply);
                payment.setAccountsReceivableInvoicePaidApplieds(accountsReceivableInvoicePaidApplieds);
                remaining = remaining.subtract(applyAmount);
                debug("Remaining is ", remaining);
            }
        }

        // return remaining;
    }

    /**
     * Creates a {@link NonInvoiced} instance or NonAr line (they're the same) for a given <code>amount</code>, then adds it to the
     * given {@link PaymentApplication}
     * 
     * @param payment
     * @param amount
     */
    protected void createNonInvoicedItemFor(final AccountsReceivablePaymentApplicationDocument payment, final KualiDecimal amount) {
        final String refundChartCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, AR_REFUND_CHART_CODE);
        final String refundAccount = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, AR_REFUND_ACCOUNT_NBR);
        final String refundObject = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, AR_REFUND_OBJECT_CODE);

        final AccountsReceivableNonInvoiced nonInvoiced = getAccountsReceivableModuleService().createNonInvoiced();

        // If we got past the above conditional, wire it up for adding
        nonInvoiced.setChartOfAccountsCode(refundChartCode);
        nonInvoiced.setAccountNumber(refundAccount);
        nonInvoiced.setFinancialObjectCode(refundObject);
        nonInvoiced.setFinancialDocumentLineAmount(amount);
        nonInvoiced.setFinancialDocumentPostingYear(payment.getPostingYear());
        nonInvoiced.setDocumentNumber(payment.getDocumentNumber());
        nonInvoiced.setFinancialDocumentLineNumber(1);
        nonInvoiced.setRefundIndicator(true);

        debug("Adding NonInvoiced item with amount ", nonInvoiced.getFinancialDocumentLineAmount());
        List<AccountsReceivableNonInvoiced> accountsReceivableNonInvoiceds = payment.getAccountsReceivableNonInvoiceds();
        accountsReceivableNonInvoiceds.add(nonInvoiced);
        payment.setAccountsReceivableNonInvoiceds(accountsReceivableNonInvoiceds);
        debug("NonInvoice Total is ", payment.getSumOfNonInvoiceds());
    }

    /**
     * Tallies up all the invoice amounts for all invoices a customer has
     * 
     * @param customerNumber used to identify the {@link Customer} to get invoices for
     * @return total {@link KualiDecimal} of all invoices
     */
    protected KualiDecimal getInvoicesTotalFor(final String customerNumber) {
        KualiDecimal retval = KualiDecimal.ZERO;
        final Collection<AccountsReceivableCustomerInvoice> invoices = getAccountsReceivableModuleService().getOpenInvoiceDocumentsByCustomerNumber(customerNumber);
        debug("Invoices for customer ", customerNumber, " ", invoices);
        for (final AccountsReceivableCustomerInvoice invoice : invoices) {           
            KualiDecimal openAmountForCustomerInvoiceDocument = getAccountsReceivableModuleService().getOpenAmountForCustomerInvoiceDocument(invoice);
            debug("Accumulating ", openAmountForCustomerInvoiceDocument);
            retval = retval.add(openAmountForCustomerInvoiceDocument);
        }
        return retval;
    }

    protected Collection<DocumentHeader> getHeadersWith(final Integer orgDocId) throws WorkflowException {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("organizationDocumentNumber", "" + orgDocId);
        return getBusinessObjectService().findMatching(FinancialSystemDocumentHeader.class, criteria);
    }

    /**
     * Calculates the value to apply for payment with. If the reimbursementTotal exceeds the amount of the invoices, then a nonAr
     * record is created that becomes a refund.
     * 
     * @param invoicesTotal
     * @param reimbursementTotal
     */
    protected KualiDecimal calculateApplicationTotal(final KualiDecimal invoicesTotal, final KualiDecimal reimbursementTotal) {
        if (invoicesTotal.equals(reimbursementTotal)
                || invoicesTotal.isGreaterThan(reimbursementTotal)) {
            return reimbursementTotal;
        }

        return reimbursementTotal.subtract(invoicesTotal);
    }

    /**
     * Sets the propertyChangeListener attribute value.
     * 
     * @param propertyChangeListener The propertyChangeListener to set.
     */
    public void setPropertyChangeListeners(final List<PropertyChangeListener> propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
    }

    /**
     * Gets the propertyChangeListeners attribute.
     * 
     * @return Returns the propertyChangeListenerDetailId.
     */
    public List<PropertyChangeListener> getPropertyChangeListeners() {
        return this.propertyChangeListeners;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the objectCodeService attribute.
     * 
     * @return Returns the objectCodeService.
     */
    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    /**
     * Sets the objectCodeService attribute value.
     * 
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    /**
     * Gets a {@link TravelAuthorizationDocument} for times when {@link TravelReimbursementDocument} is dependent upon data therein.
     */
    public void setTravelAuthorizationService(final TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return this.travelAuthorizationService;
    }

    public void setDocumentService(final DocumentService documentService) {
        this.documentService = documentService;
    }

    protected DocumentService getDocumentService() {
        return documentService;
    }

    public void setPersonService(final PersonService personService) {
        this.personService = personService;
    }

    protected PersonService getPersonService() {
        return personService;
    }

    public void setWorkflowDocumentService(final WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    protected WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setDataDictionaryService(final DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setRuleService(final KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    protected KualiRuleService getRuleService() {
        return kualiRuleService;
    }

    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    protected TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    /**
     * Sets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public void setConfigurationService(final KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    protected KualiConfigurationService getConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Gets the documentDao attribute.
     * 
     * @return Returns the documentDao.
     */
    public DocumentDao getDocumentDao() {
        return documentDao;
    }

    /**
     * Sets the documentDao attribute value.
     * 
     * @param documentDao The documentDao to set.
     */
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    @Override
    public void disencumberFunds(TravelReimbursementDocument trDocument) {
        if (trDocument.getTripType().isGenerateEncumbrance()) {
       
            final Map<String, Object> criteria = new HashMap<String, Object>();
   
            KualiDecimal totalAmount = new KualiDecimal(0);
            
            //criteria.put("referenceFinancialDocumentNumber", trDocument.getTravelDocumentIdentifier());
            //criteria.put("financialDocumentTypeCode", TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
            //List<GeneralLedgerPendingEntry> tripPendingEntryList = (List<GeneralLedgerPendingEntry>) this.getBusinessObjectService().findMatching(GeneralLedgerPendingEntry.class, criteria);
            KualiDecimal trTotal = new KualiDecimal(0);
            KualiDecimal taEncTotal = new KualiDecimal(0);
            Map<String, List<Document>> relatedDocuments = null;
            TravelAuthorizationDocument taDocument = new TravelAuthorizationDocument();
            //Find the document that this TR is for
            try {
                relatedDocuments = getTravelDocumentService().getDocumentsRelatedTo(trDocument);
                List<Document> trDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
                taDocument = (TravelAuthorizationDocument) getTravelDocumentService().findCurrentTravelAuthorization(trDocument);
                for (int i=0;i<taDocument.getSourceAccountingLines().size();i++){
                    taEncTotal = taEncTotal.add(taDocument.getSourceAccountingLine(i).getAmount());
                }
                
                //Get total of all TR's that aren't disapproved not including this one.
                if (trDocs != null) {
                    for (Document tempDocument : trDocs) {
                        if (!tempDocument.getDocumentNumber().equals(trDocument.getDocumentNumber())) {
                            if (!getTravelDocumentService().isUnsuccessful((TravelDocument) tempDocument)) {
                                TravelReimbursementDocument tempTR = (TravelReimbursementDocument) tempDocument;
                                KualiDecimal temp = tempTR.getReimbursableTotal();
                                trTotal = trTotal.add(temp);
                            }
                        }
                    }
                }
            }
            catch (WorkflowException ex) {
                ex.printStackTrace();
            }
                      
            KualiDecimal factor = new KualiDecimal(1);
            KualiDecimal totalReimbursement = trDocument.getReimbursableTotal();
            if (!trDocument.getFinalReimbursement()){
                if (totalReimbursement.isGreaterThan(taEncTotal.subtract(trTotal))){
                    factor = taEncTotal.subtract(trTotal);
                    factor = factor.divide(totalReimbursement);
                }
            }
            else{
                //in the case of the final reimbursement, total reimbursement becomes the remaining amount
                totalReimbursement = taEncTotal.subtract(trTotal); 
            }
                                
            int counter = trDocument.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);

            /*
             * factor becomes 0 when then encumbrance is equal to the total reimbursed from all TR doc's
             * factor will never be < 0
             */
            if (factor.isGreaterThan(KualiDecimal.ZERO)){
                //Create disencumbering GLPE's for the TA document 
                  for (int i=0;i<taDocument.getSourceAccountingLines().size();i++){
                      GeneralLedgerPendingEntry pendingEntry = null;
                      GeneralLedgerPendingEntry offsetEntry = null;

                      pendingEntry = setupPendingEntry((AccountingLineBase) taDocument.getSourceAccountingLine(i), sequenceHelper, trDocument);
                      pendingEntry.setReferenceFinancialDocumentTypeCode(taDocument.getFinancialDocumentTypeCode());
                      sequenceHelper.increment();
                      offsetEntry = setupOffsetEntry(sequenceHelper, trDocument, pendingEntry);
                      offsetEntry.setReferenceFinancialDocumentTypeCode(taDocument.getFinancialDocumentTypeCode());
                      sequenceHelper.increment();
                      
                      KualiDecimal tempAmount = new KualiDecimal(0);
                      KualiDecimal calculatedTotal = new KualiDecimal(0);
                      KualiDecimal tempTRTotal = totalReimbursement.multiply(factor);
                      
                      if (i == taDocument.getSourceAccountingLines().size()-1){
                          tempAmount = totalReimbursement.subtract(calculatedTotal);
                      }
                      else {
                          tempAmount = tempTRTotal.divide(taEncTotal);
                          tempAmount = tempAmount.multiply(taDocument.getSourceAccountingLine(i).getAmount());
                          calculatedTotal = calculatedTotal.add(tempTRTotal);
                      }
                      
                      pendingEntry.setTransactionLedgerEntryAmount(tempAmount);
                      offsetEntry.setTransactionLedgerEntryAmount(tempAmount);
                      trDocument.addPendingEntry(pendingEntry);
                      trDocument.addPendingEntry(offsetEntry);                   
                  }
              }
              
          }
      }
    
    /**
     * This method creates the pending entry based on the document and encumbrance
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @return pendingEntry The completed pending entry.
     */
    public GeneralLedgerPendingEntry setupPendingEntry(AccountingLineBase line, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
        final GeneralLedgerPendingEntrySourceDetail sourceDetail = line;
        GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry();

        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }
        getGeneralLedgerPendingEntryService().populateExplicitGeneralLedgerPendingEntry(document, sourceDetail, sequenceHelper, pendingEntry);
        pendingEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        pendingEntry.setReferenceFinancialDocumentNumber(document.getTravelDocumentIdentifier());
        pendingEntry.setFinancialBalanceTypeCode(balanceType);
        pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        pendingEntry.setReferenceFinancialSystemOriginationCode("01");

        return pendingEntry;
    }  
    
    /**
     * This method creates the offset entry based on the pending entry, document, and encumbrance
     * 
     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
     *        info
     * @param sequenceHelper The current sequence
     * @param taDocument The document the entries are added to.
     * @param pendingEntry The pending entry that will accompany the offset entry.
     * @return offsetEntry The completed offset entry.
     */
    public GeneralLedgerPendingEntry setupOffsetEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry) {
        String balanceType = "";
        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
        TripType tripType = document.getTripType();
        if (ObjectUtils.isNotNull(tripType)) {
            balanceType = tripType.getEncumbranceBalanceType();
        }

        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(pendingEntry);
        getGeneralLedgerPendingEntryService().populateOffsetGeneralLedgerPendingEntry(pendingEntry.getUniversityFiscalYear(), pendingEntry, sequenceHelper, offsetEntry);
        offsetEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        offsetEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        offsetEntry.setFinancialBalanceTypeCode(balanceType);
        offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        offsetEntry.setReferenceFinancialSystemOriginationCode(pendingEntry.getReferenceFinancialSystemOriginationCode());

        return offsetEntry;
    } 
    
    private Map<String, KualiDecimal> calculateEncumbranceRecentages(TravelAuthorizationDocument taDocument) {
        Map<String, KualiDecimal> percentageMap = new HashMap<String, KualiDecimal>();
        Iterator lines = taDocument.getSourceAccountingLines().iterator();
        
        while (lines.hasNext()){
            SourceAccountingLine line = (SourceAccountingLine) lines.next();
            StringBuffer key = new StringBuffer();
            key.append(line.getAccountNumber());
            key.append(line.getSubAccountNumber());
            key.append(line.getObjectCode());
            key.append(line.getSubObjectCode());
            key.append(taDocument.getTravelDocumentIdentifier());
            KualiDecimal percentage = line.getAmount().divide(taDocument.getEncumbranceTotal());
            percentageMap.put(key.toString(), percentage);
        }
        return percentageMap;
    }
    
    
    
    
    
    protected EncumbranceService getEncumbranceService() {
        return SpringContext.getBean(EncumbranceService.class);
    }
    
    protected GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return SpringContext.getBean(GeneralLedgerPendingEntryService.class);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#disableDuplicateExpenses(org.kuali.kfs.module.tem.document.TravelReimbursementDocument, org.kuali.kfs.module.tem.businessobject.ActualExpense)
     */
    @Override
    public void disableDuplicateExpenses(TravelReimbursementDocument trDocument, ActualExpense actualExpense) {
        int i = 0;
        TemTravelExpenseTypeCode travelExpenseTypeCode = actualExpense.getTravelExpenseTypeCode();
        String otherExpenseLineCode = travelExpenseTypeCode != null ? travelExpenseTypeCode.getCode() : null;

        for (final PerDiemExpense perDiemExpense : trDocument.getPerDiemExpenses()) {
            final String mileageDate = new SimpleDateFormat("MM/dd/yyyy").format(perDiemExpense.getMileageDate());
            if (actualExpense.getExpenseDate() == null){
                return;
            }
            final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(actualExpense.getExpenseDate());
            String meal = "";
            boolean valid = true;

            if (mileageDate.equals(expenseDate)) {
                if (perDiemExpense.getBreakfast() && actualExpense.isHostedBreakfast()) {
                    meal = TemConstants.HostedMeals.HOSTED_BREAKFAST;
                    perDiemExpense.setBreakfast(false);
                    perDiemExpense.setBreakfastValue(KualiDecimal.ZERO);
                    valid = false;
                }
                else if (perDiemExpense.getLunch() && actualExpense.isHostedLunch()) {
                    meal = TemConstants.HostedMeals.HOSTED_LUNCH;
                    perDiemExpense.setLunch(false);
                    perDiemExpense.setLunchValue(KualiDecimal.ZERO);
                    valid = false;
                }
                else if (perDiemExpense.getDinner() && actualExpense.isHostedDinner()) {
                    meal = TemConstants.HostedMeals.HOSTED_DINNER;
                    perDiemExpense.setDinner(false);
                    perDiemExpense.setDinnerValue(KualiDecimal.ZERO);
                    valid = false;
                }

                if (!valid) {
                    String temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, meal);
                    String message = this.getTravelDocumentService().getMessageFrom(MESSAGE_TR_MEAL_ALREADY_CLAIMED, expenseDate, meal);
                    trDocument.getDisabledProperties().put(temp,message);
                }

                // KUALITEM-483 add in check for lodging
                if (perDiemExpense.getLodging().isGreaterThan(KualiDecimal.ZERO) && otherExpenseLineCode != null && otherExpenseLineCode.equals(TemConstants.ExpenseTypes.LODGING)) {
                    String temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.LODGING.toLowerCase());
                    String message = this.getTravelDocumentService().getMessageFrom(MESSAGE_TR_LODGING_ALREADY_CLAIMED, expenseDate);
                    perDiemExpense.setLodging(KualiDecimal.ZERO);
                    trDocument.getDisabledProperties().put(temp, message);
                }
                i++;
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#enableDuplicateExpenses(org.kuali.kfs.module.tem.document.TravelReimbursementDocument, org.kuali.kfs.module.tem.businessobject.ActualExpense)
     */
    @Override
    public void enableDuplicateExpenses(TravelReimbursementDocument trDocument, ActualExpense actualExpense) {
        if (actualExpense == null){
          //Remove all per diem disabled fields
            Iterator<String> it = trDocument.getDisabledProperties().keySet().iterator();
            while (it.hasNext()){
                String key = it.next();
                if (key.indexOf(TemPropertyConstants.PER_DIEM_EXP) != 0){
                    trDocument.getDisabledProperties().remove(key);
                }
            }
        }
        else{
            boolean canRemove = !expenseStillExists(trDocument.getActualExpenses(), actualExpense);
            
            if (actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.AIRFARE) && canRemove){
                trDocument.getDisabledProperties().remove(AIRFARE_EXPENSE_DISABLED);
            }
            else{
                int i = 0;
                for (final PerDiemExpense perDiem : trDocument.getPerDiemExpenses()) {
                    final String mileageDate = new SimpleDateFormat("MM/dd/yyyy").format(perDiem.getMileageDate());
                    if (actualExpense.getExpenseDate() == null){
                        return;
                    }
                    final String expenseDate = new SimpleDateFormat("MM/dd/yyyy").format(actualExpense.getExpenseDate());

                    debug("Comparing ", mileageDate, " to ", expenseDate);
                    if (mileageDate.equals(expenseDate)) {
                        String temp = "";
                        if (actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.HOSTED_BREAKFAST) && canRemove){
                            temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.HostedMeals.HOSTED_BREAKFAST);
                            trDocument.getDisabledProperties().remove(temp);
                        }
                        else if (actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.HOSTED_LUNCH) && canRemove){
                            temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.HostedMeals.HOSTED_LUNCH);
                            trDocument.getDisabledProperties().remove(temp);
                        }
                        else if (actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.HOSTED_DINNER) && canRemove){
                            temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.HostedMeals.HOSTED_DINNER);
                            trDocument.getDisabledProperties().remove(temp);
                        }
                        else if (actualExpense.getTravelExpenseTypeCode().getCode().equals(TemConstants.ExpenseTypes.LODGING) && canRemove){
                            temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.LODGING.toLowerCase());
                            trDocument.getDisabledProperties().remove(temp);
                        }
                        
                    }
                    i++;
                }
            }
        }
        
    }
    
    /**
     * This method checks an expense against the list of expenses in the document to see if it's the last of it's type
     * @param actualExpenses
     *          list of expenses
     * @param actualExpense
     *          the expense in question
     * @return
     *          true - if there is another expense of the same type
     *          false - if it is the only one of it's type
     */
    private boolean expenseStillExists(List<ActualExpense> actualExpenses, ActualExpense actualExpense){
        boolean success = false;
        for (ActualExpense temp : actualExpenses){
            if (!temp.equals(actualExpense)){
                if (temp.getTravelExpenseTypeCode().getCode().equals(actualExpense.getTravelExpenseTypeCode().getCode())){
                    success = true;
                }
            }
        }
        return success;
    }

    public AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return accountingDocumentRelationshipService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }

    /**
     * Gets the accountsReceivableModuleService attribute.
     * 
     * @return Returns the accountsReceivableModuleService.
     */
    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {
        if (accountsReceivableModuleService == null) {
            this.accountsReceivableModuleService = SpringContext.getBean(AccountsReceivableModuleService.class);
        }
        
        return accountsReceivableModuleService;
    }

    /**
     * Sets the accountsReceivableModuleService attribute value.
     * 
     * @param accountsReceivableModuleService The accountsReceivableModuleService to set.
     */
    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }    
}
