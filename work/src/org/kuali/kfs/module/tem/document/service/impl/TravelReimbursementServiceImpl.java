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
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_COVERSHEET_INSTRUCTIONS;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_LODGING_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_TR_MEAL_ALREADY_CLAIMED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.AIRFARE_EXPENSE_DISABLED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.PER_DIEM_EXPENSE_DISABLED;
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

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerCreditMemo;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.integration.ar.AccountsRecievableDocumentHeader;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.DisburseType;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDisbursementService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public class TravelReimbursementServiceImpl implements TravelReimbursementService {

    protected static Logger LOG = Logger.getLogger(TravelReimbursementServiceImpl.class);

    private KualiRuleService kualiRuleService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private ObjectCodeService objectCodeService;
    private DocumentService documentService;
    private ConfigurationService ConfigurationService;
    private TravelDocumentService travelDocumentService;
    private TravelDisbursementService travelDisbursementService;
    private TravelAuthorizationService travelAuthorizationService;
    private ParameterService parameterService;
    private WorkflowDocumentService workflowDocumentService;
    private PersonService personService;
    private DocumentDao documentDao;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;

    private List<PropertyChangeListener> propertyChangeListeners;

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#findByTravelId(java.lang.String)
     */
    @Override
    public List<TravelReimbursementDocument> findByTravelId(final String travelDocumentIdentifier) throws WorkflowException {
        final List<TravelReimbursementDocument> retval = travelDocumentService.findReimbursementDocuments(travelDocumentIdentifier);
        for (final TravelReimbursementDocument reimbursement : retval) {
            addListenersTo(reimbursement);
        }
        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#find(java.lang.String)
     */
    @Override
    public TravelReimbursementDocument find(final String documentNumber) throws WorkflowException {
        final TravelReimbursementDocument retval = (TravelReimbursementDocument) documentService.getByDocumentHeaderId(documentNumber);
        addListenersTo(retval);
        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#addListenersTo(org.kuali.kfs.module.tem.document.TravelReimbursementDocument)
     */
    @Override
    public void addListenersTo(final TravelReimbursementDocument reimbursement) {
        if (reimbursement != null) {
            reimbursement.setPropertyChangeListeners(propertyChangeListeners);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelReimbursementService#generateCoversheetFor(java.lang.String, java.lang.String,
     *      org.kuali.kfs.module.tem.document.TravelReimbursementDocument, java.io.OutputStream)
     */
    @Override
    public Coversheet generateCoversheetFor(final TravelReimbursementDocument document) throws Exception {
        final String docNumber = document.getDocumentNumber();
        final String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        final String instructions = parameterService.getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TRAVEL_COVERSHEET_INSTRUCTIONS);
        final String mailTo = travelDocumentService.retrieveAddressFromLocationCode(parameterService.getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TRAVEL_DOCUMENTATION_LOCATION_CODE));
        final String destination  = document.getPrimaryDestination().getPrimaryDestinationName();

        final String directory = ConfigurationService.getPropertyValueAsString(EXTERNALIZABLE_HELP_URL_KEY);

        final Person initiator = personService.getPerson(initiatorId);
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

        Person person = personService.getPerson(traveler.getPrincipalId());
        cover.setTravelerPrincipalName(person != null ? person.getPrincipalName() : "");
        cover.setTravelerPhone(traveler.getPhoneNumber());
        cover.setTravelerEmail(traveler.getEmailAddress());
        cover.setDestination(destination);
        cover.setDocumentNumber(docNumber);

        final Collection<Map<String, String>> expenses = new ArrayList<Map<String, String>>();
        if (document.getActualExpenses() != null) {
            for (final ActualExpense expense : document.getActualExpenses()) {
                final Map<String, String> expenseMap = new HashMap<String, String>();
                expense.refreshReferenceObject("travelExpenseTypeCode");
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

    /**
     *
     * @param expenseType
     * @param document
     * @return
     */
    protected String getReceiptRequired(String expenseType, final TravelDocument document) {
        final String expenseTypeCode = parameterService.getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, expenseType);
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.CODE, expenseTypeCode);
        primaryKeys.put(TemPropertyConstants.TRIP_TYPE, document.getTripTypeCode());
        primaryKeys.put(TemPropertyConstants.TRAVELER_TYPE, document.getTraveler().getTravelerTypeCode());
        primaryKeys.put(KFSPropertyConstants.DOCUMENT_TYPE, document.getDocumentTypeName());

        return getReceiptRequired(businessObjectService.findByPrimaryKey(TemTravelExpenseTypeCode.class, primaryKeys));
    }

    /**
     *
     * @param expenseTypeCode
     * @return
     */
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

    @Override
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
                LOG.warn("Could not add a note to reimbursement with document number: " + travelReqDoc.getDocumentHeader().getDocumentNumber());
                LOG.warn(e.getMessage());
                if (LOG.isDebugEnabled()) {
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
    @Override
    public void notifyDateChangedOn(final TravelReimbursementDocument reimbursement, final Date start, final Date end) throws Exception {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        final String origStartDateStr = formatter.format(start);
        final String origEndDateStr = formatter.format(end);
        final String newStartDateStr = formatter.format(reimbursement.getTripBegin());
        final String newEndDateStr = formatter.format(reimbursement.getTripEnd());

        final String noteText = String.format(DATE_CHANGED_MESSAGE, origStartDateStr, origEndDateStr, newStartDateStr, newEndDateStr);

        final Note noteToAdd = documentService.createNoteFromDocument(reimbursement, noteText);
        reimbursement.addNote(noteToAdd);
        documentDao.save(reimbursement);
    }

    /**
     * Gets the {@link OrganizationOptions} to create a {@link AccountsReceivableDocumentHeader} for
     * {@link PaymentApplicationDocument}
     *
     * @return OrganizationOptions
     */
    protected AccountsReceivableOrganizationOptions getOrgOptions() {
        final Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", parameterService.getParameterValueAsString(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_CHART_CODE));
        criteria.put("organizationCode", parameterService.getParameterValueAsString(TemParameterConstants.TEM_AUTHORIZATION.class, TravelAuthorizationParameters.TRAVEL_ADVANCE_BILLING_ORG_CODE));

        return accountsReceivableModuleService.getOrganizationOptionsByPrimaryKey(criteria);
    }

    /**
     *     TA may have the above information related to reimbursable amount and (invoice?)
     *
     *     Search for the INV associated with the Travel Authorization from AR (Org Doc Number = Trip ID)
     *
     *     If any amount is left in the invoice - determine CRM spawn by
     *     TA -  Less Non-Reimbursable ??
     *
     *     Compute the reimbursable amount = total year-to-date amount reimbursed for this trip plus reimbursable amount for this TR
     *     (possibly in TA?)
     *
     *         1. reimbursable amount >= INV
     *              Spawn a customer credit memo (CRM) up to the Invoice amount
     *              The traveler will be reimbursed for the difference by (DV)
     *
     *         2. reimbursable amount < INV
     *              Spawn a customer credit memo (CRM) for the reimbursable amount
     *              The traveler will not receive any reimbursement - No DV necessary
     *
     *        3. If there is no reimbursement for this travel $0
     *              No CRM & No DV ?? TR w/ no reimbursement?
     *
     *        4. There is no INV, then do not spawn a credit memo - under case 1
     *
     * @param reimbursement
     * @throws WorkflowException
     */
    @Override
    public void processCustomerReimbursement(final TravelReimbursementDocument reimbursement) throws WorkflowException{
        //Calculate the invoice total for customer
        Map<AccountsReceivableCustomerInvoice, KualiDecimal> openInvoiceMap = getInvoicesOpenAmountMapFor (reimbursement.getTraveler().getCustomerNumber(), reimbursement.getTravelDocumentIdentifier());

        KualiDecimal invoicesTotal = KualiDecimal.ZERO;
        //calculate open invoice totals
        for (final KualiDecimal invoiceAmount : openInvoiceMap.values()) {
            invoicesTotal = invoicesTotal.add(invoiceAmount);
        }
        KualiDecimal reimbursableTotal = reimbursement.getReimbursableTotal();

        LOG.info(String.format("Invoice Total $%f - Reimbursable Total $%f", invoicesTotal.bigDecimalValue(), reimbursableTotal.bigDecimalValue()));

        //reimbursable >= invoice
        if (reimbursableTotal.isGreaterEqual(invoicesTotal)){

            //if there is invoice to pay, we will pay the invoice first and then reimburse the rest
            if (invoicesTotal.isNonZero()){
                //loop through invoices and spawn credit memos for each
                for (AccountsReceivableCustomerInvoice invoice : openInvoiceMap.keySet()){
                    spawnCustomerCreditMemoDocument(reimbursement, invoice, openInvoiceMap.get(invoice));
                }
            }

            //set the reimbursable amount in the TR doc
            reimbursement.setReimbursableAmount(reimbursableTotal.subtract(invoicesTotal));
            DisbursementVoucherDocument disbursementVoucherDocument = travelDisbursementService.createAndApproveDisbursementVoucherDocument(DisburseType.reimbursable, reimbursement);

            String relationDescription = reimbursement.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() + " - DV";
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(reimbursement.getDocumentNumber(), disbursementVoucherDocument.getDocumentNumber(), relationDescription));
        }
        //reimbursable < invoice (owe more than reimbursable, then all will go into owed invoice)
        else{
            //loop through the invoices, but only spawn CRM up until the reimbursable total
            for (AccountsReceivableCustomerInvoice invoice : openInvoiceMap.keySet()){

                KualiDecimal invoiceAmount = openInvoiceMap.get(invoice);
                if (invoiceAmount.isGreaterEqual(reimbursableTotal)){
                    spawnCustomerCreditMemoDocument(reimbursement, invoice, reimbursableTotal);
                    break;
                }else{
                    spawnCustomerCreditMemoDocument(reimbursement, invoice, invoiceAmount);
                    //decreased the credit by the invoice amount reduced by this CRM
                    reimbursableTotal = reimbursableTotal.subtract(invoiceAmount);
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#getReimbursableToTraveler(org.kuali.kfs.module.tem.document.TEMReimbursementDocument)
     */
    @Override
    public KualiDecimal getReimbursableToTraveler(TEMReimbursementDocument reimbursementDocument){

        //Calculate the invoice total for customer
        Map<AccountsReceivableCustomerInvoice, KualiDecimal> openInvoiceMap = getInvoicesOpenAmountMapFor (reimbursementDocument.getTraveler().getCustomerNumber(), reimbursementDocument.getTravelDocumentIdentifier());
        KualiDecimal invoicesTotal = KualiDecimal.ZERO;
        //calculate open invoice totals
        for (final KualiDecimal invoiceAmount : openInvoiceMap.values()) {
            invoicesTotal = invoicesTotal.add(invoiceAmount);
        }

        KualiDecimal reimbursableToTraveler = reimbursementDocument.getReimbursableTotal().subtract(invoicesTotal);
        return reimbursableToTraveler;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#spawnCustomerCreditMemoDocument(org.kuali.kfs.module.tem.document.TravelReimbursementDocument, org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice, org.kuali.rice.kns.util.KualiDecimal)
     */
    @Override
    public void spawnCustomerCreditMemoDocument(final TravelReimbursementDocument reimbursement, AccountsReceivableCustomerInvoice invoice, KualiDecimal creditAmount) throws WorkflowException {

        final AccountsReceivableCustomerCreditMemo customerCreditMemo = createCustomerCreditMemo(reimbursement, invoice, creditAmount);

        final String blanketApproveAnnotation= String.format("Blanket Approved CRM Doc # %s by system TR Document: %s TEM Doc # %s", customerCreditMemo.getDocumentNumber(), reimbursement.getDocumentNumber(), reimbursement.getTravelDocumentIdentifier());
        LOG.info(blanketApproveAnnotation);

        UserSession originalUser = GlobalVariables.getUserSession();
        WorkflowDocument originalWorkflowDocument = customerCreditMemo.getFinancialSystemDocumentHeader().getWorkflowDocument();

        try {

            // original initiator may not have permission to blanket approve the credit memo document, switch to sys user
            GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));

            WorkflowDocument newWorkflowDocument = workflowDocumentService.createWorkflowDocument(customerCreditMemo.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());
            newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());

            customerCreditMemo.getFinancialSystemDocumentHeader().setWorkflowDocument(newWorkflowDocument);

            //blanket approve CRM doc
            accountsReceivableModuleService.blanketApproveCustomerCreditMemoDocument(customerCreditMemo, blanketApproveAnnotation);

            //Adding note to the TR document
            final String noteText = String.format("Customer Credit Memo Document %s was system generated.", customerCreditMemo.getDocumentNumber());
            final Note noteToAdd = documentService.createNoteFromDocument(reimbursement, noteText);
            reimbursement.addNote(noteToAdd);
            documentService.saveDocument(reimbursement);
        }
        catch (Exception ex) {
            //log the error and continue to link CRM to TR
            LOG.error("Encountered error on the CRM document with travelDocumentIdentifier " + reimbursement.getTravelDocumentIdentifier(), ex);
        }
        finally {
            GlobalVariables.setUserSession(originalUser);
            customerCreditMemo.getFinancialSystemDocumentHeader().setWorkflowDocument(originalWorkflowDocument);
        }

        // add relationship
        String relationDescription = "TR - Customer Credit Memo";
        accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(reimbursement.getDocumentNumber(), customerCreditMemo.getDocumentNumber(), relationDescription));
    }

    /**
     * Create the CRM document and set the document headers
     *
     * @param reimbursement
     * @param creditAmount
     * @return
     * @throws WorkflowException
     */
    protected AccountsReceivableCustomerCreditMemo createCustomerCreditMemo(TravelReimbursementDocument reimbursement, AccountsReceivableCustomerInvoice invoice, KualiDecimal creditAmount) throws WorkflowException {

        // create a new CustomerCreditMemoDocument
        final AccountsReceivableCustomerCreditMemo arCreditMemoDoc = accountsReceivableModuleService.createCustomerCreditMemoDocument();

        //pre-populate the AR DocHeader so it will be bypassed in the CRM populate CRM details
        arCreditMemoDoc.setAccountsReceivableDocumentHeader(createAccountsReceivableDocumentHeader(arCreditMemoDoc.getDocumentNumber(), reimbursement.getTraveler().getCustomerNumber()));
        arCreditMemoDoc.getFinancialSystemDocumentHeader().setDocumentDescription(reimbursement.getDocumentHeader().getDocumentDescription());
        arCreditMemoDoc.getFinancialSystemDocumentHeader().setOrganizationDocumentNumber(reimbursement.getTravelDocumentIdentifier());

        //populate detail of CRM doc by invoice number and the amount to credit
        accountsReceivableModuleService.populateCustomerCreditMemoDocumentDetails(arCreditMemoDoc, invoice.getDocumentNumber(), creditAmount);

        //travel advance amount
        reimbursement.setTravelAdvanceAmount(creditAmount);

        return arCreditMemoDoc;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#getRelatedOpenTravelAuthorizationDocument(org.kuali.kfs.module.tem.document.TravelReimbursementDocument)
     */
    @Override
    public TravelAuthorizationDocument getRelatedOpenTravelAuthorizationDocument(final TravelReimbursementDocument reimbursement) {
        TravelAuthorizationDocument travelAuthorizationDocument = null;

        List<Document> travelAuthDocs = travelDocumentService.getDocumentsRelatedTo(reimbursement,
                TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT, TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT);

        for (Document document : travelAuthDocs) {
            // Find the doc that is the open to perform actions against - only one of the TAA/TA should be found
            TravelAuthorizationDocument travelDocument = (TravelAuthorizationDocument) document;
            if (travelDocumentService.isTravelAuthorizationOpened(travelDocument)) {
                travelAuthorizationDocument = travelDocument;
                break;
            }
        }

        return travelAuthorizationDocument;
    }

    /**
     * Create {@link org.kuali.kfs.integration.ar.AccountsRecievableDocumentHeader} for AR documents used by TEM
     *
     * @param document
     * @param customerNumber
     * @return
     */
    public AccountsRecievableDocumentHeader createAccountsReceivableDocumentHeader(String documentNumber, String customerNumber){

        final AccountsReceivableOrganizationOptions orgOptions = getOrgOptions();
        final String processingChart = orgOptions.getProcessingChartOfAccountCode();
        final String processingOrg = orgOptions.getProcessingOrganizationCode();

        final AccountsRecievableDocumentHeader arDocHeader = accountsReceivableModuleService.getNewAccountsReceivableDocumentHeader(processingChart, processingOrg);
        arDocHeader.setDocumentNumber(documentNumber);
        arDocHeader.setCustomerNumber(customerNumber);

        return arDocHeader;
    }

    /**
     * Look up the open invoice(s) for customer for a particular trip; Return the open amount for each of the invoice.
     *
     * @param customerNumber
     * @param travelDocId
     * @return
     */
    protected Map<AccountsReceivableCustomerInvoice, KualiDecimal> getInvoicesOpenAmountMapFor(final String customerNumber, final String travelDocId) {
        final Collection<AccountsReceivableCustomerInvoice> invoices = accountsReceivableModuleService.getOpenInvoiceDocumentsByCustomerNumberForTrip(customerNumber, travelDocId);

        Map<AccountsReceivableCustomerInvoice, KualiDecimal> invoiceOpenAmountMap = new HashMap<AccountsReceivableCustomerInvoice, KualiDecimal>();
        LOG.debug("Invoices for customer " + customerNumber + " " + invoices);
        for (final AccountsReceivableCustomerInvoice invoice : invoices) {
            KualiDecimal openAmountForCustomerInvoiceDocument = accountsReceivableModuleService.getOpenAmountForCustomerInvoiceDocument(invoice);
            invoiceOpenAmountMap.put(invoice, openAmountForCustomerInvoiceDocument);
        }
        return invoiceOpenAmountMap;
    }

//    /**
//     * Calculates the value to apply for payment with. If the reimbursementTotal exceeds the amount of the invoices, then a nonAr
//     * record is created that becomes a refund.
//     *
//     * @param invoicesTotal
//     * @param reimbursementTotal
//     */
//    protected KualiDecimal calculateApplicationTotal(final KualiDecimal invoicesTotal, final KualiDecimal reimbursementTotal) {
//        if (invoicesTotal.equals(reimbursementTotal)
//                || invoicesTotal.isGreaterThan(reimbursementTotal)) {
//            return reimbursementTotal;
//        }
//
//        return reimbursementTotal.subtract(invoicesTotal);
//    }
//
//    /**
//     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#disencumberFunds(org.kuali.kfs.module.tem.document.TravelReimbursementDocument)
//     */
//    @Override
//    public void disencumberFunds(TravelReimbursementDocument trDocument) {
//        if (trDocument.getTripType().isGenerateEncumbrance()) {
//
//            final Map<String, Object> criteria = new HashMap<String, Object>();
//
//            KualiDecimal totalAmount = new KualiDecimal(0);
//
//            //criteria.put("referenceFinancialDocumentNumber", trDocument.getTravelDocumentIdentifier());
//            //criteria.put("financialDocumentTypeCode", TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
//            //List<GeneralLedgerPendingEntry> tripPendingEntryList = (List<GeneralLedgerPendingEntry>) this.getBusinessObjectService().findMatching(GeneralLedgerPendingEntry.class, criteria);
//            KualiDecimal trTotal = new KualiDecimal(0);
//            KualiDecimal taEncTotal = new KualiDecimal(0);
//            Map<String, List<Document>> relatedDocuments = null;
//            TravelAuthorizationDocument taDocument = new TravelAuthorizationDocument();
//            //Find the document that this TR is for
//            try {
//                relatedDocuments = travelDocumentService.getDocumentsRelatedTo(trDocument);
//                List<Document> trDocs = relatedDocuments.get(TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
//                taDocument = (TravelAuthorizationDocument) travelDocumentService.findCurrentTravelAuthorization(trDocument);
//                for (int i=0;i<taDocument.getSourceAccountingLines().size();i++){
//                    taEncTotal = taEncTotal.add(taDocument.getSourceAccountingLine(i).getAmount());
//                }
//
//                //Get total of all TR's that aren't disapproved not including this one.
//                if (trDocs != null) {
//                    for (Document tempDocument : trDocs) {
//                        if (!tempDocument.getDocumentNumber().equals(trDocument.getDocumentNumber())) {
//                            if (!travelDocumentService.isUnsuccessful((TravelDocument) tempDocument)) {
//                                TravelReimbursementDocument tempTR = (TravelReimbursementDocument) tempDocument;
//                                KualiDecimal temp = tempTR.getReimbursableTotal();
//                                trTotal = trTotal.add(temp);
//                            }
//                        }
//                    }
//                }
//            }
//            catch (WorkflowException ex) {
//                ex.printStackTrace();
//            }
//
//            KualiDecimal factor = new KualiDecimal(1);
//            KualiDecimal totalReimbursement = trDocument.getReimbursableTotal();
//            if (!trDocument.getFinalReimbursement()){
//                if (totalReimbursement.isGreaterThan(taEncTotal.subtract(trTotal))){
//                    factor = taEncTotal.subtract(trTotal);
//                    factor = factor.divide(totalReimbursement);
//                }
//            }
//            else{
//                //in the case of the final reimbursement, total reimbursement becomes the remaining amount
//                totalReimbursement = taEncTotal.subtract(trTotal);
//            }
//
//            int counter = trDocument.getPendingLedgerEntriesForSufficientFundsChecking().size() + 1;
//            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper(counter);
//
//            /*
//             * factor becomes 0 when then encumbrance is equal to the total reimbursed from all TR doc's
//             * factor will never be < 0
//             */
//            if (factor.isGreaterThan(KualiDecimal.ZERO)){
//                //Create disencumbering GLPE's for the TA document
//                  for (int i=0;i<taDocument.getSourceAccountingLines().size();i++){
//                      GeneralLedgerPendingEntry pendingEntry = null;
//                      GeneralLedgerPendingEntry offsetEntry = null;
//
//                      pendingEntry = setupPendingEntry((AccountingLineBase) taDocument.getSourceAccountingLine(i), sequenceHelper, trDocument);
//                      pendingEntry.setReferenceFinancialDocumentTypeCode(taDocument.getFinancialDocumentTypeCode());
//                      sequenceHelper.increment();
//                      offsetEntry = setupOffsetEntry(sequenceHelper, trDocument, pendingEntry);
//                      offsetEntry.setReferenceFinancialDocumentTypeCode(taDocument.getFinancialDocumentTypeCode());
//                      sequenceHelper.increment();
//
//                      KualiDecimal tempAmount = new KualiDecimal(0);
//                      KualiDecimal calculatedTotal = new KualiDecimal(0);
//                      KualiDecimal tempTRTotal = totalReimbursement.multiply(factor);
//
//                      if (i == taDocument.getSourceAccountingLines().size()-1){
//                          tempAmount = totalReimbursement.subtract(calculatedTotal);
//                      }
//                      else {
//                          tempAmount = tempTRTotal.divide(taEncTotal);
//                          tempAmount = tempAmount.multiply(taDocument.getSourceAccountingLine(i).getAmount());
//                          calculatedTotal = calculatedTotal.add(tempTRTotal);
//                      }
//
//                      pendingEntry.setTransactionLedgerEntryAmount(tempAmount);
//                      offsetEntry.setTransactionLedgerEntryAmount(tempAmount);
//                      trDocument.addPendingEntry(pendingEntry);
//                      trDocument.addPendingEntry(offsetEntry);
//                  }
//              }
//
//          }
//      }
//
//    /**
//     * This method creates the pending entry based on the document and encumbrance
//     *
//     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing
//     *        LOG.info
//     * @param sequenceHelper The current sequence
//     * @param taDocument The document the entries are added to.
//     * @return pendingEntry The completed pending entry.
//     */
//    public GeneralLedgerPendingEntry setupPendingEntry(AccountingLineBase line, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document) {
//        final GeneralLedgerPendingEntrySourceDetail sourceDetail = line;
//        GeneralLedgerPendingEntry pendingEntry = new GeneralLedgerPendingEntry();
//
//        String balanceType = "";
//        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
//        TripType tripType = document.getTripType();
//        if (ObjectUtils.isNotNull(tripType)) {
//            balanceType = tripType.getEncumbranceBalanceType();
//        }
//        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(document, sourceDetail, sequenceHelper, pendingEntry);
//        pendingEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
//        pendingEntry.setReferenceFinancialDocumentNumber(document.getTravelDocumentIdentifier());
//        pendingEntry.setFinancialBalanceTypeCode(balanceType);
//        pendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
//        pendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
//        pendingEntry.setReferenceFinancialSystemOriginationCode("01");
//
//        return pendingEntry;
//    }
//
//    /**
//     * This method creates the offset entry based on the pending entry, document, and encumbrance
//     *
//     * @param encumbrance The encumbrance record that will be updated. This object never gets persisted, but is used for passing info
//     * @param sequenceHelper The current sequence
//     * @param taDocument The document the entries are added to.
//     * @param pendingEntry The pending entry that will accompany the offset entry.
//     * @return offsetEntry The completed offset entry.
//     */
//    public GeneralLedgerPendingEntry setupOffsetEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, TravelDocument document, GeneralLedgerPendingEntry pendingEntry) {
//        String balanceType = "";
//        document.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
//        TripType tripType = document.getTripType();
//        if (ObjectUtils.isNotNull(tripType)) {
//            balanceType = tripType.getEncumbranceBalanceType();
//        }
//
//        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(pendingEntry);
//        generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(pendingEntry.getUniversityFiscalYear(), pendingEntry, sequenceHelper, offsetEntry);
//        offsetEntry.setTransactionEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
//        offsetEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
//        offsetEntry.setFinancialBalanceTypeCode(balanceType);
//        offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
//        offsetEntry.setReferenceFinancialSystemOriginationCode(pendingEntry.getReferenceFinancialSystemOriginationCode());
//
//        return offsetEntry;
//    }

    //unused
//    private Map<String, KualiDecimal> calculateEncumbranceRecentages(TravelAuthorizationDocument taDocument) {
//        Map<String, KualiDecimal> percentageMap = new HashMap<String, KualiDecimal>();
//        Iterator lines = taDocument.getSourceAccountingLines().iterator();
//
//        while (lines.hasNext()){
//            SourceAccountingLine line = (SourceAccountingLine) lines.next();
//            StringBuffer key = new StringBuffer();
//            key.append(line.getAccountNumber());
//            key.append(line.getSubAccountNumber());
//            key.append(line.getObjectCode());
//            key.append(line.getSubObjectCode());
//            key.append(taDocument.getTravelDocumentIdentifier());
//            KualiDecimal percentage = line.getAmount().divide(taDocument.getEncumbranceTotal());
//            percentageMap.put(key.toString(), percentage);
//        }
//        return percentageMap;
//    }

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
                    String message = travelDocumentService.getMessageFrom(MESSAGE_TR_MEAL_ALREADY_CLAIMED, expenseDate, meal);
                    trDocument.getDisabledProperties().put(temp,message);
                }

                // KUALITEM-483 add in check for lodging
                if (perDiemExpense.getLodging().isGreaterThan(KualiDecimal.ZERO) && otherExpenseLineCode != null && otherExpenseLineCode.equals(TemConstants.ExpenseTypes.LODGING)) {
                    String temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.LODGING.toLowerCase());
                    String message = travelDocumentService.getMessageFrom(MESSAGE_TR_LODGING_ALREADY_CLAIMED, expenseDate);
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

                    LOG.debug("Comparing " + mileageDate + " to " + expenseDate);
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


    /**
     * Sets the propertyChangeListener attribute value.
     *
     * @param propertyChangeListener The propertyChangeListener to set.
     */
    public void setPropertyChangeListeners(final List<PropertyChangeListener> propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
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

    public void setDocumentService(final DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setPersonService(final PersonService personService) {
        this.personService = personService;
    }

    public void setWorkflowDocumentService(final WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDataDictionaryService(final DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setRuleService(final KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    /**
     * Sets the ConfigurationService attribute.
     *
     * @return Returns the ConfigurationService.
     */
    public void setConfigurationService(final ConfigurationService ConfigurationService) {
        this.ConfigurationService = ConfigurationService;
    }

    /**
     * Sets the documentDao attribute value.
     *
     * @param documentDao The documentDao to set.
     */
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }

    /**
     * Sets the accountsReceivableModuleService attribute value.
     *
     * @param accountsReceivableModuleService The accountsReceivableModuleService to set.
     */
    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }

    /**
     *
     * @param generalLedgerPendingEntryService
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setTravelDisbursementService(TravelDisbursementService travelDisbursementService) {
        this.travelDisbursementService = travelDisbursementService;
    }
}
