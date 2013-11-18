/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public class InvoiceRecurrenceDocumentServiceImpl implements InvoiceRecurrenceDocumentService {

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private CustomerAddressService customerAddressService;
    private DocumentService documentService;
    private DateTimeService dateTimeService;
    
    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService#isCustomerInvoiceDetailTaxable(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument, org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    @Override
    public boolean isCustomerInvoiceDetailTaxable(CustomerInvoiceDocument customerInvoiceDocument, CustomerInvoiceDetail customerInvoiceDetail) {

        //check if sales tax is enabled
        if( !parameterService.getParameterValueAsBoolean(KfsParameterConstants.ACCOUNTS_RECEIVABLE_DOCUMENT.class, ArConstants.ENABLE_SALES_TAX_IND) ){
            return false;
        }

        //check if customer is tax exempt
        if( ObjectUtils.isNotNull(customerInvoiceDocument.getCustomer() ) ){
            if( customerInvoiceDocument.getCustomer().isCustomerTaxExemptIndicator()){
                return false;
            }
        }

        //check item if the taxable indicator is checked
        if (!customerInvoiceDetail.isTaxableIndicator()) {
            return false;
        }

        //check item if item is taxable
        /*
        if( StringUtils.isNotEmpty(customerInvoiceDetail.getInvoiceItemCode()) ){
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("invoiceItemCode", customerInvoiceDetail.getInvoiceItemCode());
            criteria.put("chartOfAccountsCode", customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
            criteria.put("organizationCode", customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());
            CustomerInvoiceItemCode customerInvoiceItemCode = (CustomerInvoiceItemCode) businessObjectService.findByPrimaryKey(CustomerInvoiceItemCode.class, criteria);

            if (ObjectUtils.isNotNull(customerInvoiceItemCode) && !customerInvoiceItemCode.isTaxableIndicator()){
                return false;
            }
        }
        */

        //if address of billing org's postal code isn't the same as shipping address, return false???

        return true;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService#getPostalCodeForTaxation(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
    public String getPostalCodeForTaxation(CustomerInvoiceDocument document) {

        String postalCode = null;
        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();
        Integer shipToAddressIdentifier = document.getCustomerShipToAddressIdentifier();

        //if customer number or ship to address id isn't provided, go to org options
        if (ObjectUtils.isNotNull(shipToAddressIdentifier) && StringUtils.isNotEmpty(customerNumber) ) {

            CustomerAddress customerShipToAddress = customerAddressService.getByPrimaryKey(customerNumber, shipToAddressIdentifier);
            if( ObjectUtils.isNotNull(customerShipToAddress) ){
                postalCode = customerShipToAddress.getCustomerZipCode();
            }
        }
        else {
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("chartOfAccountsCode", document.getBillByChartOfAccountCode());
            criteria.put("organizationCode", document.getBilledByOrganizationCode());
            OrganizationOptions organizationOptions = (OrganizationOptions) businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

            if (ObjectUtils.isNotNull(organizationOptions)) {
                postalCode = organizationOptions.getOrganizationPostalZipCode();
            }


        }
        return postalCode;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceService#isInvoiceApproved(String)
     */
    @Override
    public boolean isInvoiceApproved( String invoiceNumber ) {
        boolean isSuccess = true;

        if (ObjectUtils.isNull(invoiceNumber)) {
            return isSuccess;
        }

        CustomerInvoiceDocument customerInvoiceDocument = null;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument)documentService.getByDocumentHeaderId(invoiceNumber);
        } catch (WorkflowException e){

        }
        if (ObjectUtils.isNotNull(customerInvoiceDocument)) {
            WorkflowDocument workflowDocument = customerInvoiceDocument.getDocumentHeader().getWorkflowDocument();
            if (!(workflowDocument.isApproved())) {
                isSuccess = false;
            }
        }
        else {
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceService#isValidRecurrenceBeginDate(Date)
     */
    @Override
    public boolean isValidRecurrenceBeginDate(Date beginDate) {
        boolean isSuccess = true;
        if (ObjectUtils.isNull(beginDate)) {
            return isSuccess;
        }
        Timestamp currentDate = new Timestamp(dateTimeService.getCurrentDate().getTime());
        Timestamp beginDateTimestamp = new Timestamp(beginDate.getTime());
        if (beginDateTimestamp.before(currentDate) || beginDateTimestamp.equals(currentDate)) {
            return false;
        }
        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceService#isValidRecurrenceEndDate(Date)
     */
    @Override
    public boolean isValidRecurrenceEndDate(Date beginDate, Date endDate) {
        boolean isSuccess = true;
        if (ObjectUtils.isNull(beginDate) ||
            ObjectUtils.isNull(endDate)) {
            return isSuccess;
        }
        Timestamp beginDateTimestamp = new Timestamp(beginDate.getTime());
        Timestamp endDateTimestamp = new Timestamp(endDate.getTime());
        if ((ObjectUtils.isNotNull(endDateTimestamp)) &&
            (endDateTimestamp.before(beginDateTimestamp) || endDateTimestamp.equals(beginDateTimestamp))) {
            return false;
        }
        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceService#isValidEndDateAndTotalRecurrenceNumber(Date,Date,int,String)
     */
    @Override
    public boolean isValidEndDateAndTotalRecurrenceNumber( Date recurrenceBeginDate, Date recurrenceEndDate, Integer totalRecurrenceNumber, String recurrenceIntervalCode ) {

        if (ObjectUtils.isNull(recurrenceBeginDate) ||
            ObjectUtils.isNull(recurrenceIntervalCode) ||
            ObjectUtils.isNull(recurrenceEndDate) ||
            ObjectUtils.isNull(totalRecurrenceNumber)) {
            return true;
        }

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(recurrenceBeginDate);
        Date beginDate = recurrenceBeginDate;
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(recurrenceEndDate);
        Date endDate = recurrenceEndDate;
        Calendar nextCalendar = Calendar.getInstance();
        Date nextDate = beginDate;

        int totalRecurrences = 0;
        int addCounter = 0;
        String intervalCode = recurrenceIntervalCode;
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
            beginDate = KfsDateUtils.convertToSqlDate(beginCalendar.getTime());
            totalRecurrences++;

            nextDate = beginDate;
            nextCalendar.setTime(nextDate);
            nextCalendar.add(Calendar.MONTH, addCounter);
            nextDate = KfsDateUtils.convertToSqlDate(nextCalendar.getTime());
            if (endDate.after(beginDate) && endDate.before(nextDate)) {
                totalRecurrences++;
                break;
            }
        }
        if (totalRecurrences != totalRecurrenceNumber.intValue()) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceService#isValidEndDateOrTotalRecurrenceNumber(Date,int)
     */
    @Override
    public boolean isValidEndDateOrTotalRecurrenceNumber( Date endDate, Integer totalRecurrenceNumber ) {
        boolean isSuccess = true;
        if (ObjectUtils.isNull(endDate) && ObjectUtils.isNull(totalRecurrenceNumber)) {
            return false;
        }
        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceService#isValidMaximumNumberOfRecurrences(int,String)
     */
    @Override
    public boolean isValidMaximumNumberOfRecurrences( Integer totalRecurrenceNumber, String intervalCode ) {

        if (ObjectUtils.isNull(intervalCode) ||
            ObjectUtils.isNull(totalRecurrenceNumber)) {
            return true;
        }
        Integer maximumRecurrencesByInterval;
        if (ObjectUtils.isNotNull(intervalCode)) {
            List<String> maximumRecurrences = new ArrayList<String>( parameterService.getSubParameterValuesAsString(InvoiceRecurrence.class, ArConstants.MAXIMUM_RECURRENCES_BY_INTERVAL, intervalCode) );
            if (maximumRecurrences.size() > 0 && StringUtils.isNotBlank(maximumRecurrences.get(0))) {
                maximumRecurrencesByInterval = Integer.valueOf(maximumRecurrences.get(0));
                if (totalRecurrenceNumber > maximumRecurrencesByInterval) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoiceRecurrenceService#isValidInitiator(String)
     */
    @Override
    public boolean isValidInitiator( String initiator ) {
        return true;
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
    
    public CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }
    
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
