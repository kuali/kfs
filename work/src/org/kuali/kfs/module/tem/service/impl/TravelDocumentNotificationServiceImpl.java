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
package org.kuali.kfs.module.tem.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.NotificationPreference;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelDocumentNotificationService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.KfsNotificationService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * implement the notification to the traveler based on the email preferences stored in the traveler profile
 */
public class TravelDocumentNotificationServiceImpl implements TravelDocumentNotificationService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TravelDocumentNotificationServiceImpl.class);

    private String notificationTemplate;
    private ParameterService parameterService;
    private KfsNotificationService kfsNotificationService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    
    private List<String> noNotificationRouteStatusList; 

    /**
     * @see org.kuali.kfs.module.tem.service.TravelDocumentNotificationService#sendNotificationOnChange(org.kuali.kfs.module.tem.document.TravelDocument,
     *      org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void sendNotificationOnChange(TravelDocument travelDocument, DocumentRouteStatusChangeDTO statusChangeDTO) {
        String documentTypeCode = travelDocument.getDocumentHeader().getWorkflowDocument().getDocumentType();
        NotificationPreference preference = null;
        TEMProfile travelProfile = this.getTravelProfile(travelDocument);
        String newRouteStatus = statusChangeDTO.getNewRouteStatus();        
        
        if (!this.isNotificationEnabled()) {
            return;
        }
                
        if (travelProfile == null) {
            LOG.error("travelProfile is null.");
            return;
        }
        
        if (travelDocument instanceof TravelAuthorizationDocument) {
            if (!verifyDocumentTypeCodes(documentTypeCode, this.getEligibleTravelAuthorizationDocumentTypeCodes())) {
                return;
            }
            
            preference = getEmailNotificationPreference(preference, newRouteStatus, travelProfile.getNotifyTAFinal(), travelProfile.getNotifyTAStatusChange());
        }
        else {
            // TR/ENT/RELO
            if (!verifyDocumentTypeCodes(documentTypeCode, this.getEligibleTravelExpenseDocumentTypeCodes())) {
                return;
            }
            
            preference = getEmailNotificationPreference(preference, newRouteStatus, travelProfile.getNotifyTERFinal(), travelProfile.getNotifyTERStatusChange());            
        }
           
        this.sendNotificationByPreference(travelDocument, statusChangeDTO, preference);
    }

    private boolean verifyDocumentTypeCodes(String documentTypeCode, List<String> eligibleDocumentTypeCodes) {
        if (ObjectUtils.isNull(eligibleDocumentTypeCodes) || !eligibleDocumentTypeCodes.contains(documentTypeCode)) {
            return false;
        }
        
        return true;
    }

    private NotificationPreference getEmailNotificationPreference(NotificationPreference preference, String newRouteStatus, boolean notifyOnFinal, boolean notifyOnStatusChange) {       
        if (notifyOnFinal && (KEWConstants.ROUTE_HEADER_FINAL_CD.equals(newRouteStatus) || 
                KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(newRouteStatus))) {
            preference = NotificationPreference.TA_ON_FINAL;
        }
        else if (notifyOnStatusChange && !this.getNoNotificationRouteStatusList().contains(newRouteStatus)) {
            preference = NotificationPreference.TA_ON_CHANGE;
        }
        
        return preference;
    }

    /**
     * get the traveler profile associated with the given travel document
     */
    protected TEMProfile getTravelProfile(TravelDocument travelDocument) {
        Integer travelProfileId = travelDocument.getProfileId();
        
        return this.getBusinessObjectService().findBySinglePrimaryKey(TEMProfile.class, travelProfileId);
    }

    /**
     * send notification based on the given document and preference
     */
    protected void sendNotificationByPreference(TravelDocument travelDocument, DocumentRouteStatusChangeDTO statusChangeDTO, NotificationPreference preference) {
        if (ObjectUtils.isNull(preference)) {
            return;
        }
        
        MailMessage mailMessage = this.buildDocumentStatusChangeMailMessage(travelDocument, statusChangeDTO, preference);
        
        if (mailMessage != null) {
            this.getKfsNotificationService().sendNotificationByMail(mailMessage);
        }
        else {
            LOG.error("mailMessage is null.");
        }
    }

    /**
     * build mail message object from the given travel document
     */
    @SuppressWarnings("null")
    protected MailMessage buildDocumentStatusChangeMailMessage(TravelDocument travelDocument, DocumentRouteStatusChangeDTO statusChangeDTO, NotificationPreference preference) {
        MailMessage mailMessage = new MailMessage();

        String senderEmailAddress = this.getNotificationSender();
        mailMessage.setFromAddress(senderEmailAddress);

        TravelerDetail traveler = travelDocument.getTraveler();
        String travelerEmailAddress = null;
        if (traveler == null && travelDocument.getProfileId() != null) {
            TEMProfile profile = SpringContext.getBean(TemProfileService.class).findTemProfileById(travelDocument.getProfileId());
            travelerEmailAddress = profile.getEmailAddress();
        }else{
            travelerEmailAddress = traveler.getEmailAddress();  
        }
        
        if (senderEmailAddress != null && travelerEmailAddress != null) {
            mailMessage.addToAddress(travelerEmailAddress);
    
            String notificationSubject = this.getNotificationSubject(preference);
            mailMessage.setSubject(notificationSubject);
    
            String notificationBody = this.buildNotificationBody(travelDocument, statusChangeDTO, preference);
            mailMessage.setMessage(notificationBody);
    
            return mailMessage;        
        }
        
        return null;
    }

    /**
     * collect all the information and build the notification body
     */
    protected String buildNotificationBody(TravelDocument travelDocument, DocumentRouteStatusChangeDTO statusChangeDTO, NotificationPreference preference) {
        Map<String, Object> notificationInformationHolder = new HashMap<String, Object>();

        notificationInformationHolder.put(TemConstants.NOTIFICATION_PREFERENCE, preference.code);
        notificationInformationHolder.put(KFSPropertyConstants.DOCUMENT, travelDocument);
        notificationInformationHolder.put(TemConstants.STATUS_CHANGE_DTO, statusChangeDTO);

        String newStatusLabel = KEWConstants.DOCUMENT_STATUSES.get(statusChangeDTO.getNewRouteStatus());
        notificationInformationHolder.put(TemPropertyConstants.NEW_ROUTE_STATUS, newStatusLabel);

        String oldStatusLabel = KEWConstants.DOCUMENT_STATUSES.get(statusChangeDTO.getOldRouteStatus());
        notificationInformationHolder.put(TemPropertyConstants.OLD_ROUTE_STATUS, oldStatusLabel);

        String campusTravelEmailAddress = this.getCampusTravelEmailAddress();
        notificationInformationHolder.put(TemConstants.CAMPUS_TRAVEL_EMAIL_ADDRESS, campusTravelEmailAddress);
        
        notificationInformationHolder.put(DateTimeService.class.getSimpleName(), dateTimeService);
        
        return this.getKfsNotificationService().generateNotificationContent(this.getNotificationTemplate(), notificationInformationHolder);
    }

    /**
     * get the eligible travel expense document type codes for notification from an application parameter
     */
    protected List<String> getEligibleTravelExpenseDocumentTypeCodes() {
        return this.getParameterService().getParameterValues(TemConstants.PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.TravelParameters.TRAVEL_EXPENSE_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION);
    }

    /**
     * get the eligible TA document type codes for notification from an application parameter
     */
    protected List<String> getEligibleTravelAuthorizationDocumentTypeCodes() {
        return this.getParameterService().getParameterValues(TemConstants.PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.TravelParameters.TRAVEL_AUTHORIZATION_DOC_TYPES_FOR_ON_CHANGE_NOTIFICATION);
    }

    /**
     * get the email notification sender from an application parameter
     */
    protected String getNotificationSender() {
        return this.getParameterService().getParameterValue(TemConstants.PARAM_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, TemConstants.TravelParameters.TEM_EMAIL_SENDER_PARAM_NAME);
    }

    /**
     * get the notification subject from an application parameter
     */
    protected String getNotificationSubject(NotificationPreference preference) {
        String preferenceCode = preference.code;
        
        String subjectParameterName = TemConstants.TravelParameters.TRAVEL_DOCUMENT_NOTIFICATION_SUBJECT;        
        subjectParameterName = StringUtils.isBlank(preferenceCode) ? subjectParameterName : subjectParameterName + "_" + preferenceCode;

        return this.getParameterService().getParameterValue(TemConstants.PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, subjectParameterName);
    }

    /**
     * get the Campus Travel Email Address from an application parameter
     */
    protected String getCampusTravelEmailAddress() {
        return this.getParameterService().getParameterValue(TemConstants.PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.TravelParameters.CAMPUS_TRAVEL_EMAIL_ADDRESS);
    }

    /**
     * determine whether the notification is enable or not
     */
    protected boolean isNotificationEnabled() {
        return this.getParameterService().getIndicatorParameter(TemConstants.PARAM_NAMESPACE, TravelParameters.DOCUMENT_DTL_TYPE, TemConstants.TravelParameters.ON_CHANGE_NOTIFICATION_ENABLED_IND);
    }
    
    /**
     * Gets the noNotificationRouteStatusList attribute. 
     * @return Returns the noNotificationRouteStatusList.
     */
    protected List<String> getNoNotificationRouteStatusList() {
        if(ObjectUtils.isNull(noNotificationRouteStatusList)){        
            noNotificationRouteStatusList = new ArrayList<String>();
            
            noNotificationRouteStatusList.add(KEWConstants.ROUTE_HEADER_PROCESSED_CD);
            noNotificationRouteStatusList.add(KEWConstants.ROUTE_HEADER_INITIATED_CD);
            noNotificationRouteStatusList.add(KEWConstants.ROUTE_HEADER_SAVED_CD);
        }
  
        return noNotificationRouteStatusList;
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
}
