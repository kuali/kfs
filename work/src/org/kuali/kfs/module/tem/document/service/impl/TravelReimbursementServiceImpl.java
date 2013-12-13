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
import static org.kuali.kfs.module.tem.TemPropertyConstants.AIRFARE_EXPENSE_DISABLED;
import static org.kuali.kfs.module.tem.TemPropertyConstants.PER_DIEM_EXPENSE_DISABLED;
import static org.kuali.kfs.sys.KFSConstants.EXTERNALIZABLE_HELP_URL_KEY;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerCreditMemo;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableDocumentHeader;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLineTotalPercentage;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.dataaccess.TravelReimbursementDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
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
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TravelReimbursementServiceImpl implements TravelReimbursementService {

    protected static Logger LOG = Logger.getLogger(TravelReimbursementServiceImpl.class);

    protected KualiRuleService kualiRuleService;
    protected BusinessObjectService businessObjectService;
    protected DataDictionaryService dataDictionaryService;
    protected ObjectCodeService objectCodeService;
    protected DocumentService documentService;
    protected ConfigurationService ConfigurationService;
    protected TravelDocumentService travelDocumentService;
    protected TravelAuthorizationService travelAuthorizationService;
    protected ParameterService parameterService;
    protected WorkflowDocumentService workflowDocumentService;
    protected PersonService personService;
    protected DocumentDao documentDao;
    protected AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    protected AccountsReceivableModuleService accountsReceivableModuleService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected OffsetDefinitionService offsetDefinitionService;
    protected OptionsService optionsService;
    protected NoteService noteService;
    protected TravelReimbursementDao travelReimbursementDao;

    protected List<PropertyChangeListener> propertyChangeListeners;

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
        final String mailTo = travelDocumentService.retrieveAddressFromLocationCode(document.getTravelPayment().getDocumentationLocationCode());
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

        boolean mileageReceiptRequired = false;
        boolean lodgingReceiptRequired = false;

        final Collection<Map<String, String>> expenses = new ArrayList<Map<String, String>>();
        if (document.getActualExpenses() != null) {
            for (final ActualExpense expense : document.getActualExpenses()) {
                final Map<String, String> expenseMap = new HashMap<String, String>();
                expense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
                expenseMap.put("expenseType", expense.getExpenseTypeObjectCode().getExpenseType().getName());

                final BigDecimal rate = expense.getCurrencyRate();
                final KualiDecimal amount = expense.getExpenseAmount();

                expenseMap.put("amount", new KualiDecimal(amount.bigDecimalValue().multiply(rate)).toString());

                expenseMap.put("receipt", getFormattedReceiptRequired(expense.getExpenseTypeObjectCode().isReceiptRequired()));
                if (TemConstants.ExpenseTypeMetaCategory.LODGING.getCode().equals(expense.getExpenseType().getExpenseTypeMetaCategoryCode())) {
                    lodgingReceiptRequired |= expense.getExpenseTypeObjectCode().isReceiptRequired();
                } else if (TemConstants.ExpenseTypeMetaCategory.MILEAGE.getCode().equals(expense.getExpenseType().getExpenseTypeMetaCategoryCode())) {
                    mileageReceiptRequired |= expense.getExpenseTypeObjectCode().isReceiptRequired();
                }
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
            lodgingMap.put("receipt", getFormattedReceiptRequired(lodgingReceiptRequired));
            expenses.add(lodgingMap);
            final Map<String, String> mileageMap = new HashMap<String, String>();
            mileageMap.put("expenseType", "Mileage");
            mileageMap.put("amount", document.getMilesGrandTotal().toString());
            mileageMap.put("receipt", getFormattedReceiptRequired(mileageReceiptRequired));
            expenses.add(mileageMap);
        }

        cover.setExpenses(expenses);

        return cover;
    }

    /**
     *
     * @param expenseTypeCode
     * @return
     */
    protected String getFormattedReceiptRequired(boolean receiptRequired) {
        return receiptRequired ? "Yes" : "No";
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
                for (AccountsReceivableCustomerInvoice invoice : orderInvoices(openInvoiceMap.keySet())){
                    spawnCustomerCreditMemoDocument(reimbursement, invoice, openInvoiceMap.get(invoice));
                }
            }

            //set the reimbursable amount in the TR doc
            reimbursement.setReimbursableAmount(reimbursableTotal.subtract(invoicesTotal));
        }
        //reimbursable < invoice (owe more than reimbursable, then all will go into owed invoice)
        else{
            //loop through the invoices, but only spawn CRM up until the reimbursable total
            for (AccountsReceivableCustomerInvoice invoice : orderInvoices(openInvoiceMap.keySet())){

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
     * Orders a Set of Invoices
     * @param invoices a Set of AccountsReceivableCustomerInvoice objects
     * @return a List with all of the elements of the Set in an order
     */
    protected List<AccountsReceivableCustomerInvoice> orderInvoices(Set<AccountsReceivableCustomerInvoice> invoices) {
        List<AccountsReceivableCustomerInvoice> orderedInvoices = new ArrayList<AccountsReceivableCustomerInvoice>();
        orderedInvoices.addAll(invoices);
        Collections.sort(orderedInvoices, getCustomerInvoiceComparator());
        return orderedInvoices;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#getReimbursableToTraveler(org.kuali.kfs.module.tem.document.TEMReimbursementDocument)
     */
    @Override
    public KualiDecimal getReimbursableToTraveler(TEMReimbursementDocument reimbursementDocument){
        final KualiDecimal invoicesTotal = getInvoiceAmount(reimbursementDocument);
        KualiDecimal reimbursableToTraveler = reimbursementDocument.getReimbursableTotal().subtract(invoicesTotal);
        return reimbursableToTraveler;
    }

    /**
     * Calculates the total amount of open invoices for this trip
     * @param reimbursementDocument a reimbursement in the trip to find the open invoice amount for
     * @return the total open invoice amount
     */
    @Override
    public KualiDecimal getInvoiceAmount(TEMReimbursementDocument reimbursementDocument) {
        KualiDecimal invoicesTotal = KualiDecimal.ZERO;
        if (!ObjectUtils.isNull(reimbursementDocument.getTraveler()) && !StringUtils.isBlank(reimbursementDocument.getTravelDocumentIdentifier())) {
            //Calculate the invoice total for customer
            Map<AccountsReceivableCustomerInvoice, KualiDecimal> openInvoiceMap = getInvoicesOpenAmountMapFor (reimbursementDocument.getTraveler().getCustomerNumber(), reimbursementDocument.getTravelDocumentIdentifier());
            //calculate open invoice totals
            for (final KualiDecimal invoiceAmount : openInvoiceMap.values()) {
                invoicesTotal = invoicesTotal.add(invoiceAmount);
            }
        }
        return invoicesTotal;
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

            WorkflowDocument newWorkflowDocument = workflowDocumentService.loadWorkflowDocument(customerCreditMemo.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());
            newWorkflowDocument.setTitle(originalWorkflowDocument.getTitle());

            customerCreditMemo.getFinancialSystemDocumentHeader().setWorkflowDocument(newWorkflowDocument);

            //blanket approve CRM doc
            accountsReceivableModuleService.blanketApproveCustomerCreditMemoDocument(customerCreditMemo, blanketApproveAnnotation);

            //Adding note to the TR document
            final String noteText = String.format("Customer Credit Memo Document %s was system generated.", customerCreditMemo.getDocumentNumber());
            final Note noteToAdd = documentService.createNoteFromDocument(reimbursement, noteText);
            reimbursement.addNote(noteToAdd);
            getNoteService().save(noteToAdd);
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
        arCreditMemoDoc.getFinancialSystemDocumentHeader().setDocumentDescription("Travel Advance - "+reimbursement.getTravelDocumentIdentifier()+" - "+ reimbursement.getTraveler().getFirstName() +" "+ reimbursement.getTraveler().getLastName());
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
    public AccountsReceivableDocumentHeader createAccountsReceivableDocumentHeader(String documentNumber, String customerNumber){

        final AccountsReceivableOrganizationOptions orgOptions = travelDocumentService.getOrgOptions();
        final String processingChart = orgOptions.getProcessingChartOfAccountCode();
        final String processingOrg = orgOptions.getProcessingOrganizationCode();

        final AccountsReceivableDocumentHeader arDocHeader = accountsReceivableModuleService.getNewAccountsReceivableDocumentHeader(processingChart, processingOrg);
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
                if (key.indexOf(TemPropertyConstants.PER_DIEM_EXPENSES) != 0){
                    trDocument.getDisabledProperties().remove(key);
                }
            }
        }
        else{
            boolean canRemove = !expenseStillExists(trDocument.getActualExpenses(), actualExpense);

            if (actualExpense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.AIRFARE) && canRemove){
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
                        if (actualExpense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.HOSTED_BREAKFAST) && canRemove){
                            temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.HostedMeals.HOSTED_BREAKFAST);
                            trDocument.getDisabledProperties().remove(temp);
                        }
                        else if (actualExpense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.HOSTED_LUNCH) && canRemove){
                            temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.HostedMeals.HOSTED_LUNCH);
                            trDocument.getDisabledProperties().remove(temp);
                        }
                        else if (actualExpense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.HOSTED_DINNER) && canRemove){
                            temp = String.format(PER_DIEM_EXPENSE_DISABLED, i, TemConstants.HostedMeals.HOSTED_DINNER);
                            trDocument.getDisabledProperties().remove(temp);
                        }
                        else if (actualExpense.getExpenseTypeObjectCode().getExpenseTypeCode().equals(TemConstants.ExpenseTypes.LODGING) && canRemove){
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
                if (temp.getExpenseTypeObjectCode().getExpenseTypeCode().equals(actualExpense.getExpenseTypeObjectCode().getExpenseTypeCode())){
                    success = true;
                }
            }
        }
        return success;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#generateEntriesForAdvances(org.kuali.kfs.module.tem.document.TravelReimbursementDocument)
     */
    @Override
    public void generateEntriesForAdvances(TravelReimbursementDocument trDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        final Map<AccountsReceivableCustomerInvoice, KualiDecimal> openInvoiceMap = getInvoicesOpenAmountMapFor (trDocument.getTraveler().getCustomerNumber(), trDocument.getTravelDocumentIdentifier());
        KualiDecimal remainingReimbursableTotal = trDocument.getReimbursableTotal();
        for (AccountsReceivableCustomerInvoice invoice : orderInvoices(openInvoiceMap.keySet())) {
            final KualiDecimal invoicePayment = rollReimbursementForInvoiceAmount(invoice, remainingReimbursableTotal);
            if (invoicePayment.isGreaterThan(KualiDecimal.ZERO)) {
                final TravelAdvance advance = getAdvanceForInvoice(invoice);
                if (advance != null) {
                    generatePendingEntriesForAdvanceClearing(trDocument, invoicePayment, sequenceHelper);
                    generatePendingEntriesForAdvanceCrediting(trDocument, advance, invoicePayment, sequenceHelper);
                }
            }
            remainingReimbursableTotal = remainingReimbursableTotal.subtract(invoicePayment);
        }
    }

    /**
     * Figures out how much of the remaining reimbursement amount we can devote to the current invoice
     * @param invoice the invoice to credit out
     * @param remainingReimbursementAmount the remaining amount available on the reimbursement to pay invoices out of
     * @return the amount that will be paid for the current invoice
     */
    protected KualiDecimal rollReimbursementForInvoiceAmount(AccountsReceivableCustomerInvoice invoice, KualiDecimal remainingReimbursementAmount) {
        if (invoice.getOpenAmount().isLessEqual(remainingReimbursementAmount)) {
            return invoice.getOpenAmount();
        }
        return remainingReimbursementAmount;
    }

    /**
     * Retrieves the travel advance associated with an invoice
     * @param invoice the invoice to find an associated travel advance for
     * @return the Travel Advance associated, or null if no advance could be found
     */
    protected TravelAdvance getAdvanceForInvoice(AccountsReceivableCustomerInvoice invoice) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(TemPropertyConstants.AR_INVOICE_DOC_NUMBER, invoice.getDocumentNumber());
        Collection<TravelAdvance> advances = businessObjectService.findMatching(TravelAdvance.class, fieldValues);
        if (advances == null || advances.isEmpty()) {
            return null;
        }
        if (advances.size() > 1) {
            // huh...that should not have happened.  Let's throw an exception
            throw new RuntimeException("Attempted to find advance for AR invoice identified by: "+invoice.getDocumentNumber()+" but multiple advances returned.  That condition should not exist in the system and the advances should be cleaned up.");
        }
        TravelAdvance advance = null;
        for (TravelAdvance adv : advances) {
            advance = adv;
        }
        return advance;
    }

    /**
     * Adds to the travel reimbursement the pending entries for clearing the advance
     * @param reimbursement the reimbursement which is crediting advances
     * @param paymentAmount the amount of the advance we're crediting
     * @param sequenceHelper the sequence helper to assign sequences to pending entries
     */
    protected void generatePendingEntriesForAdvanceClearing(TravelReimbursementDocument reimbursement, KualiDecimal paymentAmount, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        final List<TemSourceAccountingLine> expenseAccountingLines = reimbursement.getSourceAccountingLines();
        if (!ObjectUtils.isNull(expenseAccountingLines) && !expenseAccountingLines.isEmpty()) {
            final List<TemSourceAccountingLineTotalPercentage> expenseAccountingLinesTotalPercentages = getPercentagesForLines(expenseAccountingLines);
            final List<TemSourceAccountingLine> clearingLines = createAccountingLinesFromPercentages(expenseAccountingLinesTotalPercentages, paymentAmount, reimbursement.getDocumentNumber());

            for (TemSourceAccountingLine clearingLine : clearingLines) {
                // create clearing entry and offset
                final OffsetDefinition offsetDefinition = this.getOffsetDefinitionForAdvanceClearing(reimbursement, clearingLine);
                clearingLine.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                clearingLine.setFinancialSubObjectCode(null);
                clearingLine.setFinancialDocumentLineTypeCode(TemConstants.TRAVEL_ADVANCE_CLEARING_LINE_TYPE_CODE); // set the line type code to a special value that will alert customize entry to change the credit/debit codes
                reimbursement.generateGeneralLedgerPendingEntries(clearingLine, sequenceHelper);
                sequenceHelper.increment();
            }
        }
    }

    /**
     * Adds to the travel reimbursement the pending entries for crediting the advance
     * @param reimbursement the reimbursement which is crediting advances
     * @param advance the advance we're crediting
     * @param paymentAmount the amount of the advance we're crediting
     * @param sequenceHelper the sequence helper to assign sequences to pending entries
     */
    protected void generatePendingEntriesForAdvanceCrediting(TravelReimbursementDocument reimbursement, TravelAdvance advance, KualiDecimal paymentAmount, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        final List<TemSourceAccountingLine> advanceAccountingLines = getAccountingLinesForAdvance(advance);
        if (!ObjectUtils.isNull(advanceAccountingLines) && !advanceAccountingLines.isEmpty()) {
            final List<TemSourceAccountingLineTotalPercentage> advanceAccountingLineTotalPercentages = getPercentagesForLines(advanceAccountingLines);
            final List<TemSourceAccountingLine> creditLines = createAccountingLinesFromPercentages(advanceAccountingLineTotalPercentages, paymentAmount, reimbursement.getDocumentNumber());
            takeAPennyLeaveAPenny(creditLines, paymentAmount);

            for (TemSourceAccountingLine creditLine : creditLines) {
                // credit advance
                creditLine.setFinancialDocumentLineTypeCode(TemConstants.TRAVEL_ADVANCE_CREDITING_LINE_TYPE_CODE);
                reimbursement.generateGeneralLedgerPendingEntries(creditLine, sequenceHelper);
                sequenceHelper.increment();
            }
        }
    }

    /**
     * Finds the accounting lines associated with the given advance
     * @param advance the travel advance to find accounting lines for
     * @return the associated accounting lines, ordered by sequence number
     */
    protected List<TemSourceAccountingLine> getAccountingLinesForAdvance(TravelAdvance advance) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, advance.getDocumentNumber());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE);
        List<TemSourceAccountingLine> advanceAccountingLines = new ArrayList<TemSourceAccountingLine>();
        advanceAccountingLines.addAll(businessObjectService.findMatchingOrderBy(TemSourceAccountingLine.class, fieldValues, KFSPropertyConstants.SEQUENCE_NUMBER, true));
        return advanceAccountingLines;
    }

    /**
     * Calculates how much each of the given accounting lines contributes to the total of the accounting lines
     * @param accountingLines the accounting lines to find the percentage contribution of each of
     * @return a List of the accounting lines and their corresponding percentages
     */
    protected List<TemSourceAccountingLineTotalPercentage> getPercentagesForLines(List<TemSourceAccountingLine> accountingLines) {
        final BigDecimal total = calculateLinesTotal(accountingLines).bigDecimalValue();
        List<TemSourceAccountingLineTotalPercentage> linePercentages = new ArrayList<TemSourceAccountingLineTotalPercentage>();
        for (TemSourceAccountingLine accountingLine : accountingLines) {
            BigDecimal percentage = accountingLine.getAmount().bigDecimalValue().divide(total, getDistributionScale(), BigDecimal.ROUND_HALF_UP);
            TemSourceAccountingLineTotalPercentage linePercentage = new TemSourceAccountingLineTotalPercentage(accountingLine, percentage);
            linePercentages.add(linePercentage);
        }
        return linePercentages;
    }

    /**
     * Calculates the sum of a list of AccountingLines
     * @param accountingLines the accounting lines to add together
     * @return the sum of those accounting lines
     */
    protected KualiDecimal calculateLinesTotal(List<TemSourceAccountingLine> accountingLines) {
        KualiDecimal sum = KualiDecimal.ZERO;
        for (TemSourceAccountingLine accountingLine : accountingLines) {
            sum = sum.add(accountingLine.getAmount());
        }
        return sum;
    }

    /**
     * @return the scale of the distribution division
     */
    protected int getDistributionScale() {
        return 5;
    }

    /**
     * Generates accounting lines which will act as source details to generate the crediting glpes to pay back the advance
     * @param linePercentages the accounting lines which paid for the advance and the amount they
     * @param paymentAmount the total amount of the current invoice which is being paid back
     * @param documentNumber the document number of the reimbursement which is crediting the advance we're paying back here
     * @return a List of TemSourceAccountingLines which will be source details to generate GLPEs
     */
    protected List<TemSourceAccountingLine> createAccountingLinesFromPercentages(List<TemSourceAccountingLineTotalPercentage> linePercentages, KualiDecimal paymentAmount, String documentNumber) {
        List<TemSourceAccountingLine> creditLines = new ArrayList<TemSourceAccountingLine>();
        for (TemSourceAccountingLineTotalPercentage linePercentage : linePercentages) {
            final KualiDecimal amountForLine = new KualiDecimal(paymentAmount.bigDecimalValue().multiply(linePercentage.getPercentage()));
            TemSourceAccountingLine creditLine = createAccountingLineForClearing(linePercentage.getTemSourceAccountingLine(), amountForLine, documentNumber);
            creditLine.setSequenceNumber(creditLines.size() + 1);
            creditLines.add(creditLine);
        }
        return creditLines;
    }

    /**
     * Copies the given tem source accounting line to create a glpe source detail that will create the glpe to credit the invoice for this amount
     * @param progenitorLine the line to copy to create new accounting line
     * @param amountForLine the amount on the line
     * @param documentNumber the document number of the reimbursement which is crediting the advance we're paying back here
     * @return the newly created TemSourceAccountingLine
     */
    protected TemSourceAccountingLine createAccountingLineForClearing(TemSourceAccountingLine progenitorLine, KualiDecimal amountForLine, String documentNumber) {
        TemSourceAccountingLine copiedLine = new TemSourceAccountingLine();
        copiedLine.copyFrom(progenitorLine);
        copiedLine.setAmount(amountForLine);
        copiedLine.setDocumentNumber(documentNumber);
        return copiedLine;
    }

    /**
     * Since we're dealing with percentages, we want to make sure that the generated accounting lines equal the payment amount exactly and we'll do that by removing or adding differences to the first accounting line.
     * So named because that difference should never be more than a penny (if getDistributionScale() is set high enough)
     * @param generatedLines the generated accounting lines to pay back the invoice amount
     * @param paymentAmount the amount of the invoice we're paying back on this TR
     */
    protected void takeAPennyLeaveAPenny(List<TemSourceAccountingLine> generatedLines, KualiDecimal paymentAmount) {
        final KualiDecimal linesTotal = calculateLinesTotal(generatedLines);
        if (linesTotal.isLessThan(paymentAmount)) {
            final KualiDecimal delta = paymentAmount.subtract(linesTotal);
            final KualiDecimal updatedAmount = generatedLines.get(0).getAmount().add(delta); // leave a penny!
            generatedLines.get(0).setAmount(updatedAmount);
        } else if (linesTotal.isGreaterThan(paymentAmount)) {
            final KualiDecimal delta = linesTotal.subtract(paymentAmount);
            final KualiDecimal updatedAmount = generatedLines.get(0).getAmount().subtract(delta); // hey...take a penny...sweet!
            generatedLines.get(0).setAmount(updatedAmount);
        }
    }

    /**
     * Looks up the offset definition for the given advance clearing accounting line and travel reimbursement document
     * @param reimbursement the reimbursement document clearing advances
     * @param accountingLine the accounting line representing the amount of advance we are clearing
     * @return the offset definition associated with that amount
     */
    protected OffsetDefinition getOffsetDefinitionForAdvanceClearing(TravelReimbursementDocument reimbursement, TemSourceAccountingLine accountingLine) {
        final String documentType = reimbursement.getPaymentDocumentType();
        final Integer postingYear = reimbursement.getPostingYear();
        final String chart = accountingLine.getChartOfAccountsCode();
        final SystemOptions postingYearOptions = getOptionsService().getOptions(postingYear);
        final String balanceType = postingYearOptions.getActualFinancialBalanceTypeCd();

        final OffsetDefinition result = getOffsetDefinitionService().getByPrimaryId(postingYear, chart, documentType, balanceType);
        return result;
    }

    @Override
    public List<String> findMatchingTrips(Integer temProfileId , Timestamp tripBegin, Timestamp tripEnd,Integer primaryDestinationId) {

        List<TravelReimbursementDocument> documents = travelReimbursementDao.findMatchingTrips(temProfileId , tripBegin, tripEnd, primaryDestinationId);
        List<String> documentIds = new ArrayList<String>();
        for (TravelReimbursementDocument document : documents) {
            documentIds.add(document.getDocumentNumber());
        }
        return documentIds;
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
     * @return a Comparator which will let us order sets of AccountsReceivableCustomerInvoice objects
     */
    protected Comparator<AccountsReceivableCustomerInvoice> getCustomerInvoiceComparator() {
        return new Comparator<AccountsReceivableCustomerInvoice>() {
            /** Compare by document number */
            @Override
            public int compare(AccountsReceivableCustomerInvoice blur, AccountsReceivableCustomerInvoice oasis) {
                return blur.getDocumentNumber().compareTo(oasis.getDocumentNumber());
            }
        };
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelReimbursementService#doAllReimbursementTripTypesRequireTravelAuthorization()
     */
    @Override
    public boolean doAllReimbursementTripTypesRequireTravelAuthorization() {
        Collection<TripType> tripTypes = businessObjectService.findAll(TripType.class);

        //return the first time TA required is false
        boolean requiresAuthorization = true;
        for(TripType tripType : tripTypes) {
            requiresAuthorization = tripType.getTravelAuthorizationRequired();
            if (!requiresAuthorization) {
                return requiresAuthorization;
            }
        }
        return requiresAuthorization;
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

    /**
     * @return the injected implementation of OffsetDefinitionService
     */
    public OffsetDefinitionService getOffsetDefinitionService() {
        return offsetDefinitionService;
    }

    /**
     * Injects an implementation of the OffsetDefinitionService for use
     * @param offsetDefinitionService the implementation of the OffsetDefinitionService to use
     */
    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    /**
     * @return the injected implementation of OptionsService
     */
    public OptionsService getOptionsService() {
        return optionsService;
    }

    /**
     * Injects an implementation of the OptionsService for use
     * @param optionsService the implementation of the OptionsService to use
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setTravelReimbursementDao(TravelReimbursementDao travelReimbursementDao) {
        this.travelReimbursementDao = travelReimbursementDao;
    }



}
