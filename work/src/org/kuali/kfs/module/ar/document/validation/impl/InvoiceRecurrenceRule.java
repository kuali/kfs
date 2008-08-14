package org.kuali.kfs.module.ar.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.dto.WorkgroupDTO;
import org.kuali.rice.kew.dto.WorkgroupNameIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;

public class InvoiceRecurrenceRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceRecurrenceRule.class);
    private InvoiceRecurrence oldInvoiceRecurrence;
    private InvoiceRecurrence newInvoiceRecurrence;

    @Override
    public void setupConvenienceObjects() {
        oldInvoiceRecurrence = (InvoiceRecurrence) super.getOldBo();
        newInvoiceRecurrence = (InvoiceRecurrence) super.getNewBo();
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success;
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        Date currentDate = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());

       
        success = checkIfInvoiceIsApproved(newInvoiceRecurrence);
/*
        success &= checkIfRecurrenceMaintenanceAlreadyExists(newInvoiceRecurrence);
        success &= validateDocumentRecurrenceBeginDate(newInvoiceRecurrence, new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
*/
        success &= validateDocumentRecurrenceBeginDate(newInvoiceRecurrence);
        success &= validateDocumentRecurrenceEndDate(newInvoiceRecurrence);
        success &= validateIfBothEndDateAndTotalRecurrenceNumberAreEntered(newInvoiceRecurrence);
        success &= validateEndDateOrTotalNumberofRecurrences(newInvoiceRecurrence);   
        success &= validateMaximumNumberOfRecurrences(newInvoiceRecurrence);
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;
    }

    /**
     * Check if an Invoice Recurrence Maintenance document already exists.
     */
    private boolean checkIfRecurrenceMaintenanceAlreadyExists(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("invoiceNumber", newInvoiceRecurrence.getInvoiceNumber());
        InvoiceRecurrence invoiceRecurrence = (InvoiceRecurrence) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(InvoiceRecurrence.class, criteria);
        if (ObjectUtils.isNotNull(invoiceRecurrence) && !(oldInvoiceRecurrence.equals(invoiceRecurrence))) {
            putFieldError(ArConstants.InvoiceRecurrenceFields.RECURRING_INVOICE_NUMBER, ArConstants.ERROR_MAINTENANCE_DOCUMENT_ALREADY_EXISTS);
            success = false;
        }
        return success;
    }
    /**
     * Validate if the invoice has an approved status.
     */
    private boolean checkIfInvoiceIsApproved(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        
        if (ObjectUtils.isNull(newInvoiceRecurrence.getInvoiceNumber())) {
            return success;
        }
        
        /*
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("documentNumber", newInvoiceRecurrence.getDocumentNumber());
        CustomerInvoiceDocument customerInvoiceDocument = (CustomerInvoiceDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CustomerInvoiceDocument.class, criteria);
        */
        CustomerInvoiceDocument customerInvoiceDocument = null;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(newInvoiceRecurrence.getInvoiceNumber());
        } catch (WorkflowException e){
            
        }
        if (ObjectUtils.isNotNull(customerInvoiceDocument)) {
            KualiWorkflowDocument workflowDocument = customerInvoiceDocument.getDocumentHeader().getWorkflowDocument();
            if (!(workflowDocument.stateIsApproved())) {
                putFieldError(ArConstants.InvoiceRecurrenceFields.RECURRING_INVOICE_NUMBER, ArConstants.ERROR_RECURRING_INVOICE_NUMBER_MUST_BE_APPROVED);
                success = false;
            }
        }
        else {
            putFieldError(ArConstants.InvoiceRecurrenceFields.RECURRING_INVOICE_NUMBER, ArConstants.ERROR_INVOICE_DOES_NOT_EXIST);
            success = false;
        }
        return success;
    }
    /**
     * Validate Begin Date.
     */
    private boolean validateDocumentRecurrenceBeginDate(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceBeginDate())) {
            return success;
        }
        Timestamp currentDate = new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
        Timestamp beginDateTimestamp = new Timestamp(newInvoiceRecurrence.getDocumentRecurrenceBeginDate().getTime());
        if (beginDateTimestamp.before(currentDate) || beginDateTimestamp.equals(currentDate)) {
            putFieldError(ArConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_BEGIN_DATE, ArConstants.ERROR_INVOICE_RECURRENCE_BEGIN_DATE_EARLIER_THAN_TODAY);
            return false;
        }
        return success;
    }
    /**
     * Validate End Date.
     */
    private boolean validateDocumentRecurrenceEndDate(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceBeginDate()) || 
            ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceEndDate())) {
            return success;
        }
        Timestamp beginDateTimestamp = new Timestamp(newInvoiceRecurrence.getDocumentRecurrenceBeginDate().getTime());
        Timestamp endDateTimestamp = new Timestamp(newInvoiceRecurrence.getDocumentRecurrenceEndDate().getTime());
        if ((ObjectUtils.isNotNull(endDateTimestamp)) &&
            (endDateTimestamp.before(beginDateTimestamp) || endDateTimestamp.equals(beginDateTimestamp))) {
            putFieldError(ArConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_END_DATE, ArConstants.ERROR_END_DATE_EARLIER_THAN_BEGIN_DATE);
            return false;
        }
        return success;
    }
    /**
     * This method checks that End Date and Total Recurrence Number are valid when both are entered.
     * 
     * @param document the maintenance document
     * @return
     */
    private boolean validateIfBothEndDateAndTotalRecurrenceNumberAreEntered(InvoiceRecurrence newInvoiceRecurrence) {

        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceBeginDate()) || 
            ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceIntervalCode()) ||
            ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceEndDate()) ||
            ObjectUtils.isNull(newInvoiceRecurrence.getDocumentTotalRecurrenceNumber())) {
            return true;
        }

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(newInvoiceRecurrence.getDocumentRecurrenceBeginDate());
        Date beginDate = newInvoiceRecurrence.getDocumentRecurrenceBeginDate();
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(newInvoiceRecurrence.getDocumentRecurrenceEndDate());
        Date endDate = newInvoiceRecurrence.getDocumentRecurrenceEndDate();
        Calendar nextCalendar = Calendar.getInstance();
        Date nextDate = beginDate;
                
        int totalRecurrences = 0;
        int addCounter = 0;
        String intervalCode = newInvoiceRecurrence.getDocumentRecurrenceIntervalCode();
        if (intervalCode.equals("M")) {
            addCounter = 1;
        }
        if (intervalCode.equals("Q")) {
            addCounter = 3;
        }
        /* perform this loop while begin_date is less than or equal to end_date */
        while (!(beginDate.after(endDate))){
            beginCalendar.setTime(beginDate);
            beginCalendar.add(Calendar.MONTH, addCounter);
            beginDate = new Date(beginCalendar.getTime().getTime());
            totalRecurrences++;

            nextDate = beginDate;
            nextCalendar.setTime(nextDate);
            nextCalendar.add(Calendar.MONTH, addCounter);
            nextDate = new Date(nextCalendar.getTime().getTime());
            if (endDate.after(beginDate) && endDate.before(nextDate)) {
                totalRecurrences++;
                break;
            }
        }
        if (totalRecurrences != newInvoiceRecurrence.getDocumentTotalRecurrenceNumber().intValue()) {
            putFieldError(ArConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_END_DATE, ArConstants.ERROR_END_DATE_AND_TOTAL_NUMBER_OF_RECURRENCES_NOT_VALID);
            return false;
        }
        
        return true;
    }
    /**
     * Validate that either End Date or Total Number of Recurrences must be entered.
     */
    private boolean validateEndDateOrTotalNumberofRecurrences(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceEndDate()) && ObjectUtils.isNull(newInvoiceRecurrence.getDocumentTotalRecurrenceNumber())) {
            putFieldError(ArConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_END_DATE, ArConstants.ERROR_END_DATE_OR_TOTAL_NUMBER_OF_RECURRENCES);
            return false;
        }
        return success;
    }

    /**
     * Total number of recurrences may not be greater than the number defined in system parameter MAXIMUM_RECURRENCES_BY_INTERVAL.
     */ 
    private boolean validateMaximumNumberOfRecurrences(InvoiceRecurrence newInvoiceRecurrence) {

        if (ObjectUtils.isNull(newInvoiceRecurrence.getDocumentRecurrenceIntervalCode()) ||
            ObjectUtils.isNull(newInvoiceRecurrence.getDocumentTotalRecurrenceNumber())) {
            return true;
        }
        boolean success = true;
        Integer maximumRecurrencesByInterval;
        if (ObjectUtils.isNotNull(newInvoiceRecurrence.getDocumentRecurrenceIntervalCode())) {
            List<String> maximumRecurrences = SpringContext.getBean(ParameterService.class).getParameterValues(InvoiceRecurrence.class, ArConstants.MAXIMUM_RECURRENCES_BY_INTERVAL, newInvoiceRecurrence.getDocumentRecurrenceIntervalCode());
            if (maximumRecurrences.size() > 0 && StringUtils.isNotBlank(maximumRecurrences.get(0))) {
                maximumRecurrencesByInterval = Integer.valueOf(maximumRecurrences.get(0));
                if (newInvoiceRecurrence.getDocumentTotalRecurrenceNumber() > maximumRecurrencesByInterval) {
                    putFieldError(ArConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_TOTAL_RECURRENCE_NUMBER, ArConstants.ERROR_TOTAL_NUMBER_OF_RECURRENCES_GREATER_THAN_ALLOWED, maximumRecurrences.get(0));
                    return false;
                }
            }
        }
        return success;
    }
    /**
     * Validate WorkGroup. kualiworkflow
     */

/** Temporarily commneted out.
 *    
    private boolean validateWorkGroup(InvoiceRecurrence newInvoiceRecurrence) {
        boolean success = true;
        WorkgroupDTO workgroupVo = SpringContext.getBean(KualiWorkflowInfo.class).getWorkgroup(new WorkgroupNameIdDTO(newInvoiceRecurrence.getWorkgroupIdentifier() .getWorkgroupIdentifier()));
        
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("documentNumber", newInvoiceRecurrence.getInvoiceNumber());
        InvoiceRecurrence invoiceRecurrence = (InvoiceRecurrence) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(InvoiceRecurrence.class, criteria);
        if (ObjectUtils.isNull(invoiceRecurrence)) {
            putFieldError(ArConstants.InvoiceRecurrenceFields.RECURRING_INVOICE_NUMBER, ArConstants.ERROR_MAINTENANCE_DOCUMENT_ALREADY_EXISTS);
            success = false;
        }
        return success;
    }
*/
    
    /**
     * Checks whether the given workgroup exists and is active.
     * 
     * @param name The name of the workgroup to check.
     * @return Whether the given workgroup exists and is active.
     */
    private static boolean workgroupExistsAndIsActive(String name) {
        try {
            WorkgroupDTO workgroupVo = SpringContext.getBean(KualiWorkflowInfo.class).getWorkgroup(new WorkgroupNameIdDTO(name));
            return workgroupVo != null && workgroupVo.isActiveInd();
        }
        catch (WorkflowException e) {
            return false;
        }
    }

    
}
