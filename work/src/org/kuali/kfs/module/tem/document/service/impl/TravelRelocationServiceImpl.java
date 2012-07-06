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
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.DV_PAYEE_TYPE_CODE_C;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.DV_PAYEE_TYPE_CODE_V;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.RELOCATION_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.RELO_REIMBURSEMENT_DV_REASON_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.VENDOR_PAYMENT_DV_REASON_CODE;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_DV_IN_ACTION_LIST;
import static org.kuali.kfs.module.tem.TemKeyConstants.MESSAGE_RELO_DV_IN_ACTION_LIST;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.sys.KFSConstants.EXTERNALIZABLE_HELP_URL_KEY;

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

@SuppressWarnings("restriction")
public class TravelRelocationServiceImpl implements TravelRelocationService{

    private KualiRuleService kualiRuleService;
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private ObjectCodeService objectCodeService;
    private DocumentService documentService;
    private KualiConfigurationService kualiConfigurationService;
    private PersonService<Person> personService;
    private TravelDocumentService travelDocumentService;
    private ParameterService parameterService;
    private WorkflowDocumentService workflowDocumentService;
    private DocumentDao documentDao;
    private AccountingDocumentRelationshipService accountingDocumentRelationshipService;
    private List<PropertyChangeListener> propertyChangeListeners;
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelRelocationService#findByIdentifier(java.lang.String)
     */
    public Collection<TravelRelocationDocument> findByIdentifier(final String travelDocumentIdentifier) {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TRVL_IDENTIFIER_PROPERTY, travelDocumentIdentifier);
        return getBusinessObjectService().findMatching(TravelRelocationDocument.class, criteria);
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelRelocationService#find(java.lang.String)
     */
    public TravelRelocationDocument find(final String documentNumber) throws WorkflowException {
        final TravelRelocationDocument retval = (TravelRelocationDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
        addListenersTo(retval);
        return retval;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelRelocationService#addListenersTo(org.kuali.kfs.module.tem.document.TravelRelocationDocument)
     */
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
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelRelocationService#createDVReimbursementDocument(org.kuali.kfs.module.tem.document.TravelRelocationDocument)
     */
    public void createDVReimbursementDocument(TravelRelocationDocument document){
        DisbursementVoucherDocument disbursementVoucherDocument = getTravelDocumentService().createDVReimbursementDocument(document);
        String relationDescription = "RELO - DV";
        accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(document.getDocumentNumber(), disbursementVoucherDocument.getDocumentNumber(), relationDescription));
        GlobalVariables.getMessageList().add(MESSAGE_DV_IN_ACTION_LIST, disbursementVoucherDocument.getDocumentNumber());
    }
    

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
    
    public void setPersonService(final PersonService<Person> personService) {
        this.personService = personService;
    }

    protected PersonService<Person> getPersonService() {
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
     * 
     * @return
     */
    public static DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }

}
