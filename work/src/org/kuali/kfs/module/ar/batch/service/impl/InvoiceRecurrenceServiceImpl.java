/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.batch.service.InvoiceRecurrenceService;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.dataaccess.InvoiceRecurrenceDao;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.AdHocRouteWorkgroup;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 *  
 * Lockbox Iterators are sorted by processedInvoiceDate and batchSequenceNumber. 
 * Potentially there could be many batches on the same date. 
 * For each set of records with the same processedInvoiceDate and batchSequenceNumber, 
 * there will be one Cash-Control document. Each record within this set will create one Application document.
 * 
 */

@Transactional
public class InvoiceRecurrenceServiceImpl implements InvoiceRecurrenceService {

    public InvoiceRecurrenceDao invoiceRecurrenceDao;
    private static Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceServiceImpl.class);
    private DocumentService documentService;
    private DateTimeService dateTimeService;
    
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public boolean processInvoiceRecurrence() throws WorkflowException {
        
        Iterator<InvoiceRecurrence> itr = invoiceRecurrenceDao.getAllActiveInvoiceRecurrences();
        CustomerInvoiceDocument customerInvoiceDocument = new CustomerInvoiceDocument();
        while (itr.hasNext()) {
            InvoiceRecurrence invoiceRecurrence = (InvoiceRecurrence)itr.next();
            
            /* Get some dates and calendars  */
            Date currentDate = getDateTimeService().getCurrentSqlDate();
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(getDateTimeService().getCurrentTimestamp());

            Date currentMonthProcessDate;
            Calendar currentMonthProcessCalendar = Calendar.getInstance();

            Date nextProcessDate;
            Calendar nextProcessCalendar = Calendar.getInstance();
            
            Date lastProcessDate;
            Calendar lastProcessCalendar = Calendar.getInstance();

            Date beginDate = invoiceRecurrence.getDocumentRecurrenceBeginDate();
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.setTime(new Timestamp(invoiceRecurrence.getDocumentRecurrenceBeginDate().getTime()));

            Date endDate = invoiceRecurrence.getDocumentRecurrenceEndDate();
            Date lastCreateDate = invoiceRecurrence.getDocumentLastCreateDate();
            String intervalCode = invoiceRecurrence.getDocumentRecurrenceIntervalCode();
            Integer totalRecurrenceNumber = invoiceRecurrence.getDocumentTotalRecurrenceNumber();

            
            /* Calculate currentMonthProcessDate*/
            currentMonthProcessCalendar = currentCalendar;
            int day = beginCalendar.get(Calendar.DAY_OF_MONTH);
            currentMonthProcessCalendar.set(Calendar.DAY_OF_MONTH, day);
            currentMonthProcessDate = DateUtils.convertToSqlDate(currentMonthProcessCalendar.getTime());

            /* Calculate the nextProcessDate */
            if (currentDate.after(currentMonthProcessDate)) {
                nextProcessCalendar = currentMonthProcessCalendar;
                nextProcessCalendar.add(Calendar.MONTH, 1);
            } 
            else {
                /* currentDate is less than or equal to currentMonthProcessDate
                 * so the nextProcessDate is equal to the currentMonthProcessDate */
                nextProcessCalendar = currentMonthProcessCalendar;
            }
            nextProcessDate = DateUtils.convertToSqlDate(nextProcessCalendar.getTime());
            
            /* Calculate the lastProcessDate by subtracting one month from nextProcessingDate */
            lastProcessCalendar = nextProcessCalendar;
            lastProcessCalendar.add(Calendar.MONTH, -1);
            lastProcessDate = DateUtils.convertToSqlDate(lastProcessCalendar.getTime());
            if (lastProcessDate.before(beginDate)) {
                lastProcessCalendar.clear();
            }
            lastProcessDate = DateUtils.convertToSqlDate(lastProcessCalendar.getTime());
            
            /* if nextProcessDate is equal to currentDate create INV document */
            if (nextProcessDate.equals(currentDate)) {
                /* copy INV document to a new INV document */
                String workgroup = invoiceRecurrence.getWorkgroupName();
                String initiator = invoiceRecurrence.getDocumentInitiatorUserPersonUserIdentifier();
                GlobalVariables.setUserSession(new UserSession(initiator));

                customerInvoiceDocument = (CustomerInvoiceDocument)getDocumentService().getByDocumentHeaderId(invoiceRecurrence.getInvoiceNumber());
                customerInvoiceDocument.toCopy();
                List<AdHocRouteRecipient> adHocRouteRecipients = new ArrayList<AdHocRouteRecipient>();
                adHocRouteRecipients.add(buildApprovePersonRecipient(initiator));
                //adHocRouteRecipients.add(buildApproveWorkgroupRecipient(workgroup));
                getDocumentService().routeDocument(customerInvoiceDocument, "This is a recurred Customer Invoice", adHocRouteRecipients);
            }

            /* if nextProcessDate is greater than currentDate BUT less than or equal to endDate */
            if (nextProcessDate.after(currentDate) && (!nextProcessDate.after(endDate))) {
                if ((ObjectUtils.isNotNull(lastCreateDate) && lastProcessDate.after(lastCreateDate))  ||
                    (ObjectUtils.isNull(lastCreateDate))) {
                    /* copy INV document to a new INV document */
                    String workgroup = invoiceRecurrence.getWorkgroupName();
                    String initiator = invoiceRecurrence.getDocumentInitiatorUserPersonUserIdentifier();
                    GlobalVariables.setUserSession(new UserSession(initiator));

                    customerInvoiceDocument = (CustomerInvoiceDocument)getDocumentService().getByDocumentHeaderId(invoiceRecurrence.getInvoiceNumber());
                    customerInvoiceDocument.toCopy();
                    List<AdHocRouteRecipient> adHocRouteRecipients = new ArrayList<AdHocRouteRecipient>();
                    adHocRouteRecipients.add(buildApprovePersonRecipient(initiator));
                    //adHocRouteRecipients.add(buildApproveWorkgroupRecipient(workgroup));
                    getDocumentService().routeDocument(customerInvoiceDocument, "This is a recurred Customer Invoice", adHocRouteRecipients);
                }
            }
            
            /* Check if this is the last recurrence. If yes, inactivate the INVR and send an FYI to the initiator and workgroup.  */
            if (!nextProcessDate.before(endDate)) {
                /* Change the active indicator to 'N' and send an FYI */
                String workgroup = invoiceRecurrence.getWorkgroupName();
                String initiator = invoiceRecurrence.getDocumentInitiatorUserPersonUserIdentifier();
                GlobalVariables.setUserSession(new UserSession(initiator));

                MaintenanceDocument newMaintDoc = (MaintenanceDocument) getDocumentService().getNewDocument(getInvoiceRecurrenceMaintenanceDocumentTypeName());
                newMaintDoc.getOldMaintainableObject().setBusinessObject(invoiceRecurrence);
                InvoiceRecurrence newInvoiceRecurrence = invoiceRecurrence;
                newInvoiceRecurrence.setActive(false);
                newMaintDoc.getDocumentHeader().setDocumentDescription("Generated by Batch process");
                newMaintDoc.getDocumentHeader().setExplanation("Inactivated by the Batch process");
                newMaintDoc.getNewMaintainableObject().setBusinessObject(newInvoiceRecurrence);
                newMaintDoc.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_EDIT_ACTION);
                List<AdHocRouteRecipient> adHocRouteRecipients = new ArrayList<AdHocRouteRecipient>();
                adHocRouteRecipients.add(buildFyiPersonRecipient(initiator));
                //adHocRouteRecipients.add(buildFyiWorkgroupRecipient(workgroup));
                getDocumentService().routeDocument(newMaintDoc, null, adHocRouteRecipients);
            }
        }
        return true;

    }
    
    private String getInvoiceRecurrenceMaintenanceDocumentTypeName() {
        return "INVR";
    }

    /**
     * 
     * This method builds a FYI recipient.
     * @param userId
     * @return
     */
    private AdHocRouteRecipient buildFyiPersonRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KEWConstants.ACTION_REQUEST_FYI_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }
    
    /**
     * 
     * This method builds a recipient for Approval.
     * @param userId
     * @return
     */
    private AdHocRouteRecipient buildApprovePersonRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }
    
    /**
     * 
     * This method builds a FYI workgroup recipient.
     * @param userId
     * @return
     */
    private AdHocRouteRecipient buildFyiWorkgroupRecipient(String workgroupId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRouteWorkgroup();
        adHocRouteRecipient.setActionRequested(KEWConstants.ACTION_REQUEST_FYI_REQ);
        adHocRouteRecipient.setId(workgroupId);
        return adHocRouteRecipient;
    }
    
    /**
     * 
     * This method builds a workgroup recipient for Approval.
     * @param userId
     * @return
     */
    private AdHocRouteRecipient buildApproveWorkgroupRecipient(String workgroupId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRouteWorkgroup();
        adHocRouteRecipient.setActionRequested(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
        adHocRouteRecipient.setId(workgroupId);
        return adHocRouteRecipient;
    }
    
    public InvoiceRecurrenceDao getInvoiceRecurrenceDao() {
        return invoiceRecurrenceDao;
    }

    public void setInvoiceRecurrenceDao(InvoiceRecurrenceDao invoiceRecurrenceDao) {
        this.invoiceRecurrenceDao = invoiceRecurrenceDao;
    }
}
