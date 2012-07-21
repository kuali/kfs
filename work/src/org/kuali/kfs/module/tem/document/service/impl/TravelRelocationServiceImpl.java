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
package org.kuali.kfs.module.tem.document.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_COVERSHEET_INSTRUCTIONS;
import static org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters.RELOCATION_DOCUMENTATION_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.sys.KFSConstants.EXTERNALIZABLE_HELP_URL_KEY;

import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelRelocationService;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

public class TravelRelocationServiceImpl implements TravelRelocationService{

    protected static Logger LOG = Logger.getLogger(TravelRelocationServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private KualiConfigurationService kualiConfigurationService;
    private PersonService<Person> personService;
    private TravelDocumentService travelDocumentService;
    private ParameterService parameterService;
    private List<PropertyChangeListener> propertyChangeListeners;
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelRelocationService#findByIdentifier(java.lang.String)
     */
    @Override
    public Collection<TravelRelocationDocument> findByIdentifier(final String travelDocumentIdentifier) {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TRVL_IDENTIFIER_PROPERTY, travelDocumentIdentifier);
        return getBusinessObjectService().findMatching(TravelRelocationDocument.class, criteria);
    }
    
    /**
     * @see org.kuali.kfs.module.tem.document.service.TravelRelocationService#find(java.lang.String)
     */
    @Override
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
    @Override
    public Coversheet generateCoversheetFor(final TravelRelocationDocument document) throws Exception {
        final String docNumber = document.getDocumentNumber();
        final String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        final String instructions = getParameterService().getParameterValue(TemParameterConstants.TEM_DOCUMENT.class, TRAVEL_COVERSHEET_INSTRUCTIONS);
        final String mailTo = travelDocumentService.retrieveAddressFromLocationCode(getParameterService().getParameterValue(TemParameterConstants.TEM_RELOCATION.class, RELOCATION_DOCUMENTATION_LOCATION_CODE));
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
        cover.setTravelerPrincipalName(StringUtils.defaultString(person.getPrincipalName()));
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
    
    public void setBusinessObjectService(final BusinessObjectService businessObjectService){
        this.businessObjectService = businessObjectService;
    }
    
    protected BusinessObjectService getBusinessObjectService(){
        return this.businessObjectService;
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
}
