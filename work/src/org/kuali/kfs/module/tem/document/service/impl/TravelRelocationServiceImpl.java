/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_COVERSHEET_INSTRUCTIONS;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.DV_DEFAULT_PAYMENT_METHOD_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.DV_PAYEE_TYPE_CODE_C;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.DV_PAYEE_TYPE_CODE_V;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.RELOCATION_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.RELO_REIMBURSEMENT_DV_REASON_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.VENDOR_PAYMENT_DV_REASON_CODE;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_RELO_DV_IN_ACTION_LIST;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.sys.KFSConstants.EXTERNALIZABLE_HELP_URL_KEY;
import static org.kuali.rice.kns.util.GlobalVariables.getMessageList;

import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelRelocationService;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class TravelRelocationServiceImpl implements TravelRelocationService{

    private KualiRuleService kualiRuleService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private ObjectCodeService objectCodeService;
    private DocumentService documentService;
    private KualiConfigurationService kualiConfigurationService;
    private PersonService personService;
    private TravelDocumentService travelDocumentService;
    private ParameterService parameterService;
    private WorkflowDocumentService workflowDocumentService;
    private DocumentDao documentDao;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private List<PropertyChangeListener> propertyChangeListeners;
    
    public void setRuleService(final KualiRuleService kualiRuleService){
        this.kualiRuleService = kualiRuleService;
    }
    
    protected KualiRuleService getRuleService(){
        return this.kualiRuleService;
    }
    
    public void setBusinessObjectService(final BusinessObjectService businessObjectService){
        this.businessObjectService = businessObjectService;
    }
    
    protected BusinessObjectService getBusinessObjectService(){
        return this.businessObjectService;
    }
    
    public void setDataDictionaryService(final DataDictionaryService dataDictionaryService){
        this.dataDictionaryService = dataDictionaryService;
    }
    
    protected DataDictionaryService getDataDictionaryService(){
        return this.dataDictionaryService;
    }
    
    public void setObjectCodeService(final ObjectCodeService objectCodeService){
        this.objectCodeService = objectCodeService;
    }
    
    protected ObjectCodeService getObjectCodeService(){
        return this.objectCodeService;
    }
    
    public void setDocumentService(final DocumentService documentService){
        this.documentService = documentService;
    }
    
    protected DocumentService getDocumentService(){
        return this.documentService;
    }
    
    public void setConfigurationService(final KualiConfigurationService kualiConfigurationService){
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
    protected KualiConfigurationService getConfigurationService(){
        return this.kualiConfigurationService;
    }
    
    public void setPersonService(final PersonService personService) {
        this.personService = personService;
    }

    protected PersonService getPersonService() {
        return personService;
    }
    
    public void setTravelDocumentService(final TravelDocumentService travelDocumentService){
        this.travelDocumentService = travelDocumentService;
    }
    
    protected TravelDocumentService getTravelDocumentService(){
        return this.travelDocumentService;
    }
    
    public void setParameterService(final ParameterService parameterService){
        this.parameterService = parameterService;
    }
    
    protected ParameterService getParameterService(){
        return this.parameterService;
    }
    
    public void setWorkflowDocumentService(final WorkflowDocumentService workflowDocumentService){
        this.workflowDocumentService = workflowDocumentService;
    }
    
    protected WorkflowDocumentService getWorkflowDocumentService(){
        return this.workflowDocumentService;
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
    
    public AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return accountingDocumentRelationshipService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
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
     * Locate all {@link TravelRelocationDocument} instances with the same <code>travelDocumentIdentifier</code>
     * 
     * @param travelDocumentIdentifier to locate {@link TravelRelocationDocument} instances
     * @return {@link Collection} of {@link TravelRelocationDocument} instances
     */
    public Collection<TravelRelocationDocument> findByIdentifier(final String travelDocumentIdentifier) {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TRVL_IDENTIFIER_PROPERTY, travelDocumentIdentifier);
        return getBusinessObjectService().findMatching(TravelRelocationDocument.class, criteria);
    }
    
    public TravelRelocationDocument find(final String documentNumber) throws WorkflowException {
        final TravelRelocationDocument retval = (TravelRelocationDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
        addListenersTo(retval);
        return retval;
    }

    @Override
    public void addListenersTo(final TravelRelocationDocument relocation) {
        if (relocation != null) {
            relocation.setPropertyChangeListeners(getPropertyChangeListeners());
        }      
    }
    
    /**
     * @see org.kuali.kfs.module.tem.service.TravelRelocationService#generateCoversheetFor(java.lang.String, java.lang.String,
     *      org.kuali.kfs.module.tem.document.TravelRelocationDocument, java.io.OutputStream)
     */
    public Coversheet generateCoversheetFor(final TravelRelocationDocument document) throws Exception {
        final String docNumber = document.getDocumentNumber();
        final String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        final String instructions = getParameterService().getParameterValue(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, TRAVEL_COVERSHEET_INSTRUCTIONS);
        final String mailTo = travelDocumentService.retrieveAddressFromLocationCode(getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, RELOCATION_DOCUMENTATION_LOCATION_CODE));
        final String destination = document.getToCity();

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
        cover.setTravelerName(traveler.getCustomer().getCustomerName());
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

                final KualiDecimal rate = ((ActualExpense) expense).getCurrencyRate();
                final KualiDecimal amount = expense.getExpenseAmount();

                expenseMap.put("amount", amount.multiply(rate) + "");
                expenseMap.put("receipt", "");
                expenses.add(expenseMap);
            }
        }
        cover.setExpenses(expenses);

        return cover;
    }
    
    public void createDVForVendor(TravelRelocationDocument relocation){
        DisbursementVoucherDocument dvDocument = null;
        try {
         // change current user to be the submitter of the original doc
            String principalName = SpringContext.getBean(PersonService.class).getPerson(relocation.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName();
            GlobalVariables.setUserSession(new UserSession(principalName));
            dvDocument = (DisbursementVoucherDocument) documentService.getNewDocument(DisbursementVoucherDocument.class);
        }
        catch (Exception e) {
            error("Error creating new disbursement voucher document: ", e.getMessage());
            throw new RuntimeException("Error creating new disbursement voucher document: " + e.getMessage(), e);
        }
        
        populateDisbursementVoucherFieldsForVendor(dvDocument, relocation);
        try{            
            dvDocument.prepareForSave();
            businessObjectService.save(dvDocument);
            
         // add relationship
            String relationDescription = "RELO - DV";
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(relocation.getDocumentNumber(), dvDocument.getDocumentNumber(), relationDescription));
            getMessageList().add(MESSAGE_RELO_DV_IN_ACTION_LIST, dvDocument.getDocumentNumber());
        }
        catch (Exception ex1) {
            // if we can't save DV, need to stop processing
            error("cannot save Relationship ", dvDocument.getDocumentNumber(), ex1);
            throw new RuntimeException("cannot save Relationship " + dvDocument.getDocumentNumber(), ex1);
        }
    }
    
    public void createDVForReimbursement(TravelRelocationDocument relocation){
     // change current user to be the submitter of the original doc
        String principalName = SpringContext.getBean(PersonService.class).getPerson(relocation.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName();
        GlobalVariables.setUserSession(new UserSession(principalName));
        
        DisbursementVoucherDocument dvDocument = null;
        try {
            dvDocument = (DisbursementVoucherDocument) documentService.getNewDocument(DisbursementVoucherDocument.class);
        }
        catch (Exception e) {
            error("Error creating new disbursement voucher document: ", e.getMessage());
            throw new RuntimeException("Error creating new disbursement voucher document: " + e.getMessage(), e);
        }
        
        populateDisbursementVoucherFieldsForReimbursement(dvDocument, relocation);
        try{
            dvDocument.prepareForSave();
            businessObjectService.save(dvDocument);
            
            String annotation = "Saved by system in relation to Travel Relocation Document: " + relocation.getDocumentNumber();
            getWorkflowDocumentService().save(dvDocument.getDocumentHeader().getWorkflowDocument(), annotation);
            
            getMessageList().add(MESSAGE_RELO_DV_IN_ACTION_LIST, dvDocument.getDocumentNumber());
        }
        catch (Exception ex1) {
            // if we can't save DV, need to stop processing
            error("cannot save DV ", dvDocument.getDocumentNumber(), ex1);
            throw new RuntimeException("cannot save DV " + dvDocument.getDocumentNumber(), ex1);
        }
    }
    
    protected void populateCommonDVFields(final DisbursementVoucherDocument dvDocument, TravelRelocationDocument reloDocument){
     // init document
        dvDocument.initiateDocument();
        Person initiator = SpringContext.getBean(PersonService.class).getPerson(reloDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initiator == null) {
            throw new RuntimeException("Initiator could not be found in KIM!");
        }
        
        TEMProfile travelerProfile = reloDocument.getTemProfile();
        
        if(travelerProfile == null){
            travelerProfile = reloDocument.retrieveTravelerProfile();
        }
        
        dvDocument.getDocumentHeader().setOrganizationDocumentNumber(reloDocument.getTravelDocumentIdentifier());
        dvDocument.getDvPayeeDetail().setDocumentNumber(dvDocument.getDocumentNumber());                
        dvDocument.getDvPayeeDetail().setDisbVchrPayeeIdNumber(reloDocument.getTraveler().getPrincipalId());
        dvDocument.getDvPayeeDetail().setDisbVchrPayeePersonName(reloDocument.getTraveler().getFirstName() + " " + reloDocument.getTraveler().getLastName());
        
        dvDocument.getDvPayeeDetail().setDisbVchrPayeeLine1Addr(reloDocument.getTraveler().getStreetAddressLine1());
        dvDocument.getDvPayeeDetail().setDisbVchrPayeeLine2Addr(reloDocument.getTraveler().getStreetAddressLine2());
        dvDocument.getDvPayeeDetail().setDisbVchrPayeeCityName(reloDocument.getTraveler().getCityName());
        dvDocument.getDvPayeeDetail().setDisbVchrPayeeStateCode(reloDocument.getTraveler().getStateCode());
        dvDocument.getDvPayeeDetail().setDisbVchrPayeeZipCode(reloDocument.getTraveler().getZipCode());
        dvDocument.getDvPayeeDetail().setDisbVchrPayeeCountryCode(reloDocument.getTraveler().getCountryCode());
        dvDocument.setDisbVchrCheckStubText(reloDocument.getTravelDocumentIdentifier() + ", " + reloDocument.getTripBegin().toString() +", " + " - " + reloDocument.getTripEnd().toString() +reloDocument.getToCity() +", "+ reloDocument.getToStateCode());
        
        dvDocument.setDisbVchrContactPersonName(initiator.getPrincipalName());
        dvDocument.setDisbVchrContactPhoneNumber(initiator.getPhoneNumber());
        dvDocument.setDisbVchrPaymentMethodCode(DV_DEFAULT_PAYMENT_METHOD_CODE);
        dvDocument.setDisbursementVoucherDocumentationLocationCode(getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, RELOCATION_DOCUMENTATION_LOCATION_CODE));
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        dvDocument.setDisbursementVoucherDueDate(new java.sql.Date(calendar.getTimeInMillis()));
        
    }
    
    public void populateDisbursementVoucherFieldsForVendor(final DisbursementVoucherDocument dvDocument, TravelRelocationDocument reloDocument){
        populateCommonDVFields(dvDocument, reloDocument);
        
        Person initiator = SpringContext.getBean(PersonService.class).getPerson(reloDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initiator == null) {
            throw new RuntimeException("Initiator could not be found in KIM!");
        }
        
        TEMProfile travelerProfile = reloDocument.getTemProfile();
        
        if(travelerProfile == null){
            travelerProfile = reloDocument.retrieveTravelerProfile();
        }
        
        dvDocument.getDocumentHeader().setDocumentDescription("Created by RELO document");        
        final String dvReasonCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, VENDOR_PAYMENT_DV_REASON_CODE);
        dvDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(dvReasonCode);
        dvDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DV_PAYEE_TYPE_CODE_V);
        dvDocument.setDisbVchrCheckTotalAmount(reloDocument.getDocumentGrandTotal());
        
        for(SourceAccountingLine userEnteredAccountingLine: (List<SourceAccountingLine>)reloDocument.getSourceAccountingLines()){
            SourceAccountingLine accountingLine = new SourceAccountingLine();
            if(travelerProfile.getDefaultChartCode() == null){
                accountingLine.setChartOfAccountsCode(userEnteredAccountingLine.getChartOfAccountsCode());
            }
            else{
                accountingLine.setChartOfAccountsCode(travelerProfile.getDefaultChartCode());
            }
            if(travelerProfile.getAccount() == null){
                accountingLine.setAccountNumber(userEnteredAccountingLine.getAccountNumber());
            }
            else{
                accountingLine.setAccountNumber(travelerProfile.getAccount().getAccountNumber());
            }            
            
            accountingLine.setFinancialObjectCode(userEnteredAccountingLine.getFinancialObjectCode());
            accountingLine.setAmount(userEnteredAccountingLine.getAmount());
            accountingLine.setOrganizationReferenceId("");
            dvDocument.addSourceAccountingLine(accountingLine);
        }        

    }
    
    protected void populateDisbursementVoucherFieldsForReimbursement(final DisbursementVoucherDocument dvDocument, TravelRelocationDocument reloDocument){
        populateCommonDVFields(dvDocument, reloDocument);

        Person initiator = SpringContext.getBean(PersonService.class).getPerson(reloDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        if (initiator == null) {
            throw new RuntimeException("Initiator could not be found in KIM!");
        }
        
        TEMProfile travelerProfile = reloDocument.getTemProfile();
        
        if(travelerProfile == null){
            travelerProfile = reloDocument.retrieveTravelerProfile();
        }

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        dvDocument.getDocumentHeader().setDocumentDescription(reloDocument.getTraveler().getFirstName() + " " + reloDocument.getTraveler().getLastName() + "-" + format.format(reloDocument.getTripBegin()) + " "+ format.format(reloDocument.getTripEnd()) + "-" + reloDocument.getToCity());
        if (dvDocument.getDocumentHeader().getDocumentDescription().length() >= 40) {
            String truncatedDocumentDescription = dvDocument.getDocumentHeader().getDocumentDescription().substring(0, 39);
            dvDocument.getDocumentHeader().setDocumentDescription(truncatedDocumentDescription);
        }
        
        
        final String dvReasonCode = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, RELO_REIMBURSEMENT_DV_REASON_CODE);
        dvDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(dvReasonCode);
        dvDocument.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(DV_PAYEE_TYPE_CODE_C);
        
        dvDocument.setDisbVchrCheckTotalAmount(reloDocument.getDocumentGrandTotal());
        
        for(SourceAccountingLine userEnteredAccountingLine: (List<SourceAccountingLine>)reloDocument.getSourceAccountingLines()){
            SourceAccountingLine accountingLine = new SourceAccountingLine();
            
            accountingLine.setChartOfAccountsCode(userEnteredAccountingLine.getChartOfAccountsCode());            
            accountingLine.setAccountNumber(userEnteredAccountingLine.getAccountNumber());
            accountingLine.setFinancialObjectCode(userEnteredAccountingLine.getFinancialObjectCode());
            accountingLine.setAmount(userEnteredAccountingLine.getAmount());
            accountingLine.setOrganizationReferenceId("");
            dvDocument.addSourceAccountingLine(accountingLine);
        }       
    }
    
    public void createREQS(TravelRelocationDocument relocation){
        RequisitionDocument reqsDocument = null;
        try {
            reqsDocument = (RequisitionDocument) documentService.getNewDocument(RequisitionDocument.class);
        }
        catch (Exception e) {
            error("Error creating new Requisition document: ", e.getMessage());
            throw new RuntimeException("Error creating new Requisition document: " + e.getMessage(), e);
        }
        
        populateRequisitionFields(reqsDocument, relocation);
        try{
            reqsDocument.prepareForSave();
            businessObjectService.save(reqsDocument);
            
         // add relationship
            String relationDescription = "RELO - DV";
            accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(relocation.getDocumentNumber(), reqsDocument.getDocumentNumber(), relationDescription));
            getMessageList().add(MESSAGE_RELO_DV_IN_ACTION_LIST, reqsDocument.getDocumentNumber());
        }
        catch (Exception ex1) {
            // if we can't save REQS, need to stop processing
            error("cannot save REQS ", reqsDocument.getDocumentNumber(), ex1);
            throw new RuntimeException("cannot save DV " + reqsDocument.getDocumentNumber(), ex1);
        }
    }
    
    public void populateRequisitionFields(final RequisitionDocument reqsDocument, TravelRelocationDocument reloDocument){
        reqsDocument.getDocumentHeader().setDocumentDescription("Requisition for Moving And Relocation");
        reqsDocument.getDocumentHeader().setOrganizationDocumentNumber(reloDocument.getTravelDocumentIdentifier());
        
        
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.setTime(reloDocument.getTripBegin());
        reqsDocument.setPostingYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
        
        
     // init document
        reqsDocument.initiateDocument();
    }
    
    public static DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }

}
