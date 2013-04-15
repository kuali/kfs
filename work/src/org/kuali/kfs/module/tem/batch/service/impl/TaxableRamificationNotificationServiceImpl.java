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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.batch.TaxableRamificationNotificationStep;
import org.kuali.kfs.module.tem.batch.service.TaxableRamificationNotificationService;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TaxableRamificationDocument;
import org.kuali.kfs.module.tem.document.service.TaxableRamificationDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.KfsNotificationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * implement the taxable ramification notification service, which also generates taxable ramification document
 */
public class TaxableRamificationNotificationServiceImpl implements TaxableRamificationNotificationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxableRamificationNotificationServiceImpl.class);

    private String notificationTemplate;
    private BusinessObjectService businessObjectService;
    private TaxableRamificationDocumentService taxableRamificationDocumentService;
    private KfsNotificationService kfsNotificationService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.tem.batch.service.TaxRamificationNotificationService#sendTaxRamificationReport()
     */
    @Override
    public void sendTaxableRamificationReport() {
        Date taxableRamificationNotificationDate = this.getDateTimeService().getCurrentSqlDate();
        List<TravelAdvance> travelAdvances = this.getTaxableRamificationDocumentService().getAllQualifiedOutstandingTravelAdvance();

        for (TravelAdvance advance : travelAdvances) {
            try{
                TaxableRamificationDocument taxableRamificationDocument = this.createTaxableRamificationDocument(advance, taxableRamificationNotificationDate);
                this.sendTaxableRamificationReport(taxableRamificationDocument);
            }
            catch(Exception ex){
                LOG.error("Failed to send taxable ramification document for the travel advance: " + advance, ex);
            }
        }

        LOG.info("The total outstanding travel advance being processed is " + travelAdvances.size());
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.TaxableRamificationNotificationService#sendTaxableRamificationReport(org.kuali.kfs.module.tem.businessobject.TravelAdvance,
     *      java.sql.Date)
     */
    @Override
    public void sendTaxableRamificationReport(TaxableRamificationDocument taxableRamificationDocument) {
        if (ObjectUtils.isNotNull(taxableRamificationDocument)) {
            MailMessage mailMessage = this.buildTaxRamificationReportMailMessage(taxableRamificationDocument);
            this.getKfsNotificationService().sendNotificationByMail(mailMessage);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.TaxableRamificationNotificationService#sendTaxableRamificationReport(org.kuali.kfs.module.tem.businessobject.TravelAdvance,
     *      java.sql.Date)
     */
    @Override
    @Transactional
    public TaxableRamificationDocument createTaxableRamificationDocument(TravelAdvance travelAdvance, Date taxableRamificationNotificationDate) {
        if (ObjectUtils.isNull(travelAdvance)) {
            throw new RuntimeException("The given travel advance cannot be null.");
        }

        TaxableRamificationDocument taxRamificationDocument = this.getTaxableRamificationDocumentService().createAndBlanketApproveRamificationDocument(travelAdvance);
        if (ObjectUtils.isNotNull(taxRamificationDocument)) {
            travelAdvance.setTaxRamificationNotificationDate(taxableRamificationNotificationDate);
            this.getBusinessObjectService().save(travelAdvance);
        }

        return taxRamificationDocument;
    }

    /**
     * build mail message object from the given taxable ramification document
     */
    protected MailMessage buildTaxRamificationReportMailMessage(TaxableRamificationDocument taxableRamificationDocument) {
        MailMessage mailMessage = new MailMessage();

        String senderEmailAddress = this.getNotificationSender();
        mailMessage.setFromAddress(senderEmailAddress);

        TravelerDetail travelerDetail = taxableRamificationDocument.getTravelerDetail();

        String travelerEmailAddress = travelerDetail.getEmailAddress();
        mailMessage.addToAddress(travelerEmailAddress);

        String travelerName = travelerDetail.getLastName() + ", " + travelerDetail.getFirstName();
        String notificationSubject = this.getNotificationSubject() + KFSConstants.SQUARE_BRACKET_LEFT + travelerName + KFSConstants.SQUARE_BRACKET_RIGHT;
        mailMessage.setSubject(notificationSubject);

        String notificationBody = this.buildNotificationBody(taxableRamificationDocument);
        mailMessage.setMessage(notificationBody);

        return mailMessage;
    }

    /**
     * collect all the information from the given customer invoice document and build the notification body
     */
    protected String buildNotificationBody(TaxableRamificationDocument taxRamificationDocument) {
        Map<String, Object> taxRamificationInformationHolder = new HashMap<String, Object>();

        taxRamificationInformationHolder.put(KFSPropertyConstants.DOCUMENT, taxRamificationDocument);

        String campusTravelEmailAddress = this.getCampusTravelEmailAddress();
        taxRamificationInformationHolder.put(TemConstants.CAMPUS_TRAVEL_EMAIL_ADDRESS, campusTravelEmailAddress);

        return this.getKfsNotificationService().generateNotificationContent(this.getNotificationTemplate(), taxRamificationInformationHolder);
    }

    /**
     * get the email notification sender from an application parameter
     */
    protected String getNotificationSender() {
        return this.getParameterService().getParameterValueAsString(TaxableRamificationNotificationStep.class, TemConstants.TravelParameters.FROM_EMAIL_ADDRESS_PARAM_NAME);
    }

    /**
     * get the notification text from an application parameter
     */
    protected String getNotificationText() {
        return this.getParameterService().getParameterValueAsString(TaxableRamificationNotificationStep.class, TemConstants.TaxRamificationParameter.NOTIFICATION_TEXT_PARAM_NAME);
    }

    /**
     * get the notification subject from an application parameter
     */
    protected String getNotificationSubject() {
        return this.getParameterService().getParameterValueAsString(TaxableRamificationNotificationStep.class, TemConstants.TaxRamificationParameter.NOTIFICATION_SUBJECT_PARAM_NAME);
    }

    /**
     * get the Campus Travel Email Address from an application parameter
     */
    protected String getCampusTravelEmailAddress() {
        return this.getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.TRAVEL_EMAIL_ADDRESS);
    }

    /**
     * Gets the kfsNotificationService attribute.
     *
     * @return Returns the kfsNotificationService.
     */
    public KfsNotificationService getKfsNotificationService() {
        return kfsNotificationService;
    }

    /**
     * Sets the kfsNotificationService attribute value.
     *
     * @param kfsNotificationService The kfsNotificationService to set.
     */
    public void setKfsNotificationService(KfsNotificationService kfsNotificationService) {
        this.kfsNotificationService = kfsNotificationService;
    }

    /**
     * Gets the notificationTemplate attribute.
     *
     * @return Returns the notificationTemplate.
     */
    public String getNotificationTemplate() {
        return notificationTemplate;
    }

    /**
     * Sets the notificationTemplate attribute value.
     *
     * @param notificationTemplate The notificationTemplate to set.
     */
    public void setNotificationTemplate(String notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the taxableRamificationDocumentService attribute.
     * @return Returns the taxableRamificationDocumentService.
     */
    public TaxableRamificationDocumentService getTaxableRamificationDocumentService() {
        return taxableRamificationDocumentService;
    }

    /**
     * Sets the taxableRamificationDocumentService attribute value.
     * @param taxableRamificationDocumentService The taxableRamificationDocumentService to set.
     */
    public void setTaxableRamificationDocumentService(TaxableRamificationDocumentService taxableRamificationDocumentService) {
        this.taxableRamificationDocumentService = taxableRamificationDocumentService;
    }
}
