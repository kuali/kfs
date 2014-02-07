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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.batch.TravelImportedExpenseNotificationStep;
import org.kuali.kfs.module.tem.batch.service.TravelImportedExpenseNotificationService;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.HistoricalTravelExpenseService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.KfsNotificationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

public class TravelImportedExpenseNotificationServiceImpl implements TravelImportedExpenseNotificationService {

    protected DateTimeService dateTimeService;
    protected BusinessObjectService businessObjectService;
    protected TemProfileService temProfileService;
    protected HistoricalTravelExpenseService historicalTravelExpenseService;
    protected TravelDocumentService travelDocumentService;
    protected DocumentService documentService;

    protected KfsNotificationService kfsNotificationService;
    protected ParameterService parameterService;
    protected String notificationTemplate;

    /**
     * @see org.kuali.kfs.module.tem.batch.service.TravelImportedExpenseNotificationService#sendImportedExpenseNotification()
     */
    @Override
    public void sendImportedExpenseNotification() {
       List<HistoricalTravelExpense> travelExpenses = this.getHistoricalTravelExpenseService().getImportedExpesnesToBeNotified();
       Map<Integer, List<HistoricalTravelExpense>> expensesGroupByTraveler = this.groupExpensesByTraveler(travelExpenses);

       for(Integer travelerProfileId : expensesGroupByTraveler.keySet()){
           if (travelerProfileId != null) {
               List<HistoricalTravelExpense> expensesOfTraveler = expensesGroupByTraveler.get(travelerProfileId);

               if(ObjectUtils.isNotNull(expensesOfTraveler) && !expensesOfTraveler.isEmpty()){
                   this.sendImportedExpenseNotification(travelerProfileId, expensesOfTraveler);
               }
           }
       }
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.TravelImportedExpenseNotificationService#sendImportedExpenseNotification(java.lang.Integer)
     */
    @Override
    @Transactional
    public void sendImportedExpenseNotification(Integer travelerProfileId) {
        List<HistoricalTravelExpense> expensesOfTraveler = this.getHistoricalTravelExpenseService().getImportedExpesnesToBeNotified(travelerProfileId);

        this.sendImportedExpenseNotification(travelerProfileId, expensesOfTraveler);
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.TravelImportedExpenseNotificationService#sendImportedExpenseNotification(java.lang.String, java.util.List)
     */
    @Override
    @Transactional
    public void sendImportedExpenseNotification(Integer travelerProfileId, List<HistoricalTravelExpense> expensesOfTraveler) {
        Date notificationDate = this.getDateTimeService().getCurrentSqlDate();

        for(HistoricalTravelExpense expense : expensesOfTraveler){
            expense.setExpenseNotificationDate(notificationDate);
            this.getBusinessObjectService().save(expense);
        }

        MailMessage mailMessage = this.buildExpenseNotificationMailMessage(travelerProfileId, expensesOfTraveler);
        this.getKfsNotificationService().sendNotificationByMail(mailMessage);
    }


    protected MailMessage buildExpenseNotificationMailMessage(Integer travelerProfileId, List<HistoricalTravelExpense> expensesOfTraveler) {
        MailMessage mailMessage = new MailMessage();

        String senderEmailAddress = this.getNotificationSender();
        mailMessage.setFromAddress(senderEmailAddress);

        TemProfile travelerProfile = this.getTemProfileService().findTemProfileById(travelerProfileId);
        String travelerEmailAddress = travelerProfile.getEmailAddress();
        mailMessage.addToAddress(travelerEmailAddress);

        String notificationSubject = this.getNotificationSubject();
        mailMessage.setSubject(this.getNotificationSubject());

        String notificationBody = this.buildNotificationBody(travelerProfile, expensesOfTraveler);
        mailMessage.setMessage(notificationBody);

        return mailMessage;
    }

    /**
     * collect all the information from the given customer invoice document and build the notification body
     */
    protected String buildNotificationBody(TemProfile travelerProfile, List<HistoricalTravelExpense> expensesOfTraveler) {
        Map<String, Object> notificationInformationHolder = new HashMap<String, Object>();

        notificationInformationHolder.put(TemConstants.TRAVELER_PROFILE_KEY, travelerProfile);
        notificationInformationHolder.put(TemConstants.TRAVEL_EXPENSES_KEY, expensesOfTraveler);
        notificationInformationHolder.put(KFSConstants.NOTIFICATION_TEXT_KEY, this.getNotificationText());

        return this.getKfsNotificationService().generateNotificationContent(this.getNotificationTemplate(), notificationInformationHolder);
    }

    protected Map<Integer, List<HistoricalTravelExpense>> groupExpensesByTraveler(List<HistoricalTravelExpense> travelExpenses) {
        Map<Integer, List<HistoricalTravelExpense>> expensesGroupedByTraveler = new HashMap<Integer, List<HistoricalTravelExpense>>();

        for(HistoricalTravelExpense expense : travelExpenses){
            Integer profileId = expense.getProfileId();

            if (profileId == null) {
                profileId = lookupProfileId(expense);
            }

            if(expensesGroupedByTraveler.containsKey(profileId)){
                List<HistoricalTravelExpense> expensesOfTraveler = expensesGroupedByTraveler.get(profileId);
                expensesOfTraveler.add(expense);
            }
            else{
                List<HistoricalTravelExpense> expensesOfTraveler = new ArrayList<HistoricalTravelExpense>();
                expensesOfTraveler.add(expense);

                expensesGroupedByTraveler.put(profileId, expensesOfTraveler);
            }
        }

        return expensesGroupedByTraveler;
    }

    /**
     * Uses the given reconciled expense to look up the profile id (presumably from the trip's current document)
     * @param reconciledExpense the reconciled expense to try to find a profile id for
     * @return the profile id for the expense, or null if one could not be determined
     */
    protected Integer lookupProfileId(HistoricalTravelExpense reconciledExpense) {
        if (reconciledExpense.getProfileId() != null) {
            return reconciledExpense.getProfileId();
        }
        if (!StringUtils.isBlank(reconciledExpense.getDocumentNumber())) {
            try {
                final TravelDocument travelDoc = (TravelDocument)getDocumentService().getByDocumentHeaderIdSessionless(reconciledExpense.getDocumentNumber());
                if (travelDoc != null && !ObjectUtils.isNull(travelDoc.getTemProfileId())) {
                    return travelDoc.getTemProfileId();
                }
            }
            catch (WorkflowException we) {
                // i can't access a document in workflow?  Then let's blow chunks, fun style!
                throw new RuntimeException("Cannot retrieve document #"+reconciledExpense.getDocumentNumber()+"...and really, I'm just trying to look up a traveler is all", we);
            }
        }
        if (!StringUtils.isBlank(reconciledExpense.getTripId())) {
            final TravelDocument travelDoc = getTravelDocumentService().getParentTravelDocument(reconciledExpense.getTripId());
            if (travelDoc != null && !ObjectUtils.isNull(travelDoc.getTemProfileId())) {
                return travelDoc.getTemProfileId();
            }
        }

        return null;
    }

    protected List<HistoricalTravelExpense> getImportedExpesnesToBeNotified() {

        return null;
    }

    /**
     * get the email notification sender from an application parameter
     */
    protected String getNotificationSender() {
        return this.getParameterService().getParameterValueAsString(TemParameterConstants.TEM_ALL.class, TemConstants.TravelParameters.FROM_EMAIL_ADDRESS_PARAM_NAME);
    }

    /**
     * get the notification subject from an application parameter
     */
    protected String getNotificationSubject() {
        return this.getParameterService().getParameterValueAsString(TravelImportedExpenseNotificationStep.class, TemConstants.ImportedExpenseParameter.NOTIFICATION_SUBJECT_PARAM_NAME);
    }

    /**
     * get the notification text from an application parameter
     */
    protected String getNotificationText() {
        return this.getParameterService().getParameterValueAsString(TravelImportedExpenseNotificationStep.class, TemConstants.ImportedExpenseParameter.NOTIFICATION_TEXT_PARAM_NAME);
    }

    /**
     * Gets the temProfileService attribute.
     * @return Returns the temProfileService.
     */
    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    /**
     * Sets the temProfileService attribute value.
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the kfsNotificationService attribute.
     * @return Returns the kfsNotificationService.
     */
    public KfsNotificationService getKfsNotificationService() {
        return kfsNotificationService;
    }

    /**
     * Sets the kfsNotificationService attribute value.
     * @param kfsNotificationService The kfsNotificationService to set.
     */
    public void setKfsNotificationService(KfsNotificationService kfsNotificationService) {
        this.kfsNotificationService = kfsNotificationService;
    }

    /**
     * Gets the parameterService attribute.
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the notificationTemplate attribute.
     * @return Returns the notificationTemplate.
     */
    public String getNotificationTemplate() {
        return notificationTemplate;
    }

    /**
     * Sets the notificationTemplate attribute value.
     * @param notificationTemplate The notificationTemplate to set.
     */
    public void setNotificationTemplate(String notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    /**
     * Gets the historicalTravelExpenseService attribute.
     * @return Returns the historicalTravelExpenseService.
     */
    public HistoricalTravelExpenseService getHistoricalTravelExpenseService() {
        return historicalTravelExpenseService;
    }

    /**
     * Sets the historicalTravelExpenseService attribute value.
     * @param historicalTravelExpenseService The historicalTravelExpenseService to set.
     */
    public void setHistoricalTravelExpenseService(HistoricalTravelExpenseService historicalTravelExpenseService) {
        this.historicalTravelExpenseService = historicalTravelExpenseService;
    }

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

}
