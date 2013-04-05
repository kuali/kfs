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
import static org.kuali.kfs.module.tem.TemConstants.TravelEntertainmentParameters.DOCUMENT_LOCATION_CODE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.TRAVEL_COVERSHEET_INSTRUCTIONS;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.sys.KFSConstants.EXTERNALIZABLE_HELP_URL_KEY;
import static org.kuali.kfs.sys.context.SpringContext.getBean;

import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelEntertainmentDocumentService;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;


public class TravelEntertainmentDocumentServiceImpl implements TravelEntertainmentDocumentService{
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private PersonService personService;
    private ConfigurationService ConfigurationService;
    private List<PropertyChangeListener> propertyChangeListeners;
    private TravelDocumentService travelDocumentService;

    @Override
    public Collection<TravelEntertainmentDocument> findByTravelId(String travelDocumentIdentifier) {
        final Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TRVL_IDENTIFIER_PROPERTY, travelDocumentIdentifier);
        return getBusinessObjectService().findMatching(TravelEntertainmentDocument.class, criteria);
    }

    @Override
    public TravelEntertainmentDocument find(String documentNumber) throws WorkflowException {
        final TravelEntertainmentDocument retval = (TravelEntertainmentDocument) getDocumentService().getByDocumentHeaderId(documentNumber);
        addListenersTo(retval);
        return retval;
    }

    @Override
    public void addListenersTo(final TravelEntertainmentDocument entertainment) {
        if (entertainment != null) {
            entertainment.setPropertyChangeListeners(getPropertyChangeListeners());
        }
    }
    public List<PropertyChangeListener> getPropertyChangeListeners() {
        return this.propertyChangeListeners;
    }

    @Override
    public Coversheet generateCoversheetFor(TravelEntertainmentDocument document) throws Exception {
        final String docNumber = document.getDocumentNumber();
        final String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        final String instructions = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TRAVEL_COVERSHEET_INSTRUCTIONS);
        final String mailTo = travelDocumentService.retrieveAddressFromLocationCode(getParameterService().getParameterValueAsString(TravelEntertainmentDocument.class, DOCUMENT_LOCATION_CODE));
        final String destination = document.getEventTitle();

        final String directory = getConfigurationService().getPropertyValueAsString(EXTERNALIZABLE_HELP_URL_KEY);

        final Person initiator = getPersonService().getPerson(initiatorId);
        final TravelerDetail traveler = document.getTraveler();
        traveler.refreshReferenceObject(TemPropertyConstants.CUSTOMER);

        final Coversheet cover = new Coversheet();

        cover.setInstructions(instructions);
        cover.setMailTo(mailTo);
        cover.setTripId(document.getTravelDocumentIdentifier());
        cover.setDate(new SimpleDateFormat("MM/dd/yyyy").format(document.getTripBegin()));
        cover.setInitiatorName(initiator.getFirstName() + " " + initiator.getLastName());
        cover.setInitiatorPrincipalName(initiator.getPrincipalName());
        cover.setInitiatorPhone(initiator.getPhoneNumber());
        cover.setInitiatorEmail(initiator.getEmailAddress());
        if(traveler.getCustomer()!=null) {
            cover.setTravelerName(traveler.getCustomer().getCustomerName());
        }
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
                expense.refreshReferenceObject(TemPropertyConstants.TRAVEL_EXEPENSE_TYPE_CODE);
                expenseMap.put("expenseType", expense.getTravelExpenseTypeCode().getName());

                final KualiDecimal rate = expense.getCurrencyRate();
                final KualiDecimal amount = expense.getExpenseAmount();

                expenseMap.put("amount", amount.multiply(rate) + "");
                expenseMap.put("receipt", getReceiptRequired(expense.getTravelExpenseTypeCode()));
                expenses.add(expenseMap);
            }
        }
        cover.setExpenses(expenses);

        return cover;
    }

    protected String getReceiptRequired(String expenseType, final TravelDocument document) {
        final String expenseTypeCode = getParameterService().getParameterValueAsString(PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, expenseType);
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.CODE, expenseTypeCode);
        primaryKeys.put(TemPropertyConstants.TRIP_TYPE, document.getTripTypeCode());
        primaryKeys.put(TemPropertyConstants.TRAVELER_TYPE, document.getTraveler().getTravelerTypeCode());
        primaryKeys.put(KFSPropertyConstants.DOCUMENT_TYPE, document.getDocumentTypeName());

        return getReceiptRequired(businessObjectService.findByPrimaryKey(TemTravelExpenseTypeCode.class, primaryKeys));
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

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setPropertyChangeListeners(List<PropertyChangeListener> propertyChangeListeners) {
        this.propertyChangeListeners = propertyChangeListeners;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ConfigurationService getConfigurationService() {
        return ConfigurationService;
    }

    public void setConfigurationService(ConfigurationService ConfigurationService) {
        this.ConfigurationService = ConfigurationService;
    }

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    @Override
    public void handleNewAttendee(Attendee newAttendeeLine) {
        getDictionaryValidationService().validate(newAttendeeLine);
    }

    protected DictionaryValidationService getDictionaryValidationService() {
        return getBean(DictionaryValidationService.class);
    }

}
