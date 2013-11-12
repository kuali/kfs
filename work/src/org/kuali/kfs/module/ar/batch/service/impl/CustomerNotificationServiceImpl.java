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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArParameterKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.CustomerAgingReportNotificationStep;
import org.kuali.kfs.module.ar.batch.service.CustomerNotificationService;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.KfsNotificationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * implement the customer notification service
 */
public class CustomerNotificationServiceImpl implements CustomerNotificationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerNotificationServiceImpl.class);

    private String agingReportTemplate;
    private BusinessObjectService businessObjectService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private KfsNotificationService kfsNotificationService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.ar.batch.service.CustomerNotificationService#sendCustomerAgingReport()
     */
    @Override
    public void sendCustomerAgingReport() {
        Collection<CustomerInvoiceDocument> openAgingInvoiceDocuments = getQualifiedAgingInvoiceDocument();
        Timestamp agingReportSentTime = this.getDateTimeService().getCurrentTimestamp();

        for (CustomerInvoiceDocument invoiceDocument : openAgingInvoiceDocuments) {
            this.sendCustomerAgingReport(invoiceDocument, agingReportSentTime);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.CustomerNotificationService#sendCustomerAgingReport(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument, java.sql.Timestamp)
     */
    @Override
    public void sendCustomerAgingReport(CustomerInvoiceDocument invoiceDocument, Timestamp agingReportSentTime) {
        if (ObjectUtils.isNotNull(invoiceDocument)) {
            invoiceDocument.setAgingReportSentTime(agingReportSentTime);
            this.getBusinessObjectService().save(invoiceDocument);
        }

        MailMessage mailMessage = buildAgingReportMailMessage(invoiceDocument);

        this.getKfsNotificationService().sendNotificationByMail(mailMessage);
    }

    /**
     * get qualified aging customer invoice documents
     */
    protected Collection<CustomerInvoiceDocument> getQualifiedAgingInvoiceDocument() {
        Collection<CustomerInvoiceDocument> agingInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();

        Integer invoiceAge = this.getCustomerAgingNotificationOnDays();
        String selectionOption = this.getNotificationSelectionOption();

        if(StringUtils.equals(ArConstants.ArNotificationOptions.PROCESSING_ORG.option, selectionOption)){
            List<String> charts = this.getNotificationChartSelection();
            List<String> organizations = this.getNotificationOrganizationSelection();

            agingInvoiceDocuments = customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByProcessing(charts, organizations, invoiceAge);
        }
        else if(StringUtils.equals(ArConstants.ArNotificationOptions.BILLING_ORG.option, selectionOption)){
            List<String> charts = this.getNotificationChartSelection();
            List<String> organizations = this.getNotificationOrganizationSelection();

            agingInvoiceDocuments = customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByBilling(charts, organizations, invoiceAge);
        }
        else if(StringUtils.equals(ArConstants.ArNotificationOptions.ACCOUNT.option, selectionOption)){
            List<String> charts = this.getNotificationChartSelection();
            List<String> accounts = this.getNotificationAccountSelection();

            agingInvoiceDocuments = customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByAccounts(charts, accounts, invoiceAge);
        }
        else{
            throw new RuntimeException("The given notification option only can be one of the following values: " + ArConstants.ArNotificationOptions.values());
        }

        return agingInvoiceDocuments;
    }

    /**
     * build mail message object from the given invoice document for aging report
     */
    protected MailMessage buildAgingReportMailMessage(CustomerInvoiceDocument invoiceDocument) {
        MailMessage mailMessage = new MailMessage();

        Customer customer = invoiceDocument.getCustomer();

        String senderEmailAddress = this.getNotificationSender();
        mailMessage.setFromAddress(senderEmailAddress);

        String customerEmailAddress = customer.getCustomerEmailAddress();
        mailMessage.addToAddress(customerEmailAddress);

        String notificationSubject = this.getNotificationSubject() + KFSConstants.SQUARE_BRACKET_LEFT + invoiceDocument.getCustomerName() + KFSConstants.SQUARE_BRACKET_RIGHT;
        mailMessage.setSubject(notificationSubject);

        String notificationBody = this.buildNotificationBody(invoiceDocument);
        mailMessage.setMessage(notificationBody);

        return mailMessage;
    }

    /**
     * get the organization option that is associated with the given customer invoice document
     */
    protected OrganizationOptions getOrganizationOptions(CustomerInvoiceDocument invoiceDocument) {
        Map<String, String> criteria = new HashMap<String, String>();

        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invoiceDocument.getBillByChartOfAccountCode());
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, invoiceDocument.getBilledByOrganizationCode());

        return businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);
    }

    /**
     * collect all the information from the given customer invoice document and build the notification body
     */
    protected String buildNotificationBody(CustomerInvoiceDocument invoiceDocument) {
        Map<String, Object> agingReportInformationHolder = new HashMap<String, Object>();

        Customer customer = invoiceDocument.getCustomer();
        String customerEmail = customer.getCustomerEmailAddress();
        OrganizationOptions organizationOptions = this.getOrganizationOptions(invoiceDocument);

        agingReportInformationHolder.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER, customer);
        agingReportInformationHolder.put(ArPropertyConstants.CUSTOMER_INVOICE_DOCUMENT, invoiceDocument);
        agingReportInformationHolder.put(ArPropertyConstants.ORGANIZATION_OPTIONS, organizationOptions);
        agingReportInformationHolder.put(KFSConstants.NOTIFICATION_TEXT_KEY, this.getNotificationText());

        updateAgingAmount(invoiceDocument, agingReportInformationHolder);

        return this.getKfsNotificationService().generateNotificationContent(this.getAgingReportTemplate(), agingReportInformationHolder);
    }

    /**
     * update the aging amount bucket
     */
    protected void updateAgingAmount(CustomerInvoiceDocument invoiceDocument, Map<String, Object> agingReportInformationHolder) {
        agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_0_TO_30, KualiDecimal.ZERO);
        agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_31_TO_60, KualiDecimal.ZERO);
        agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_61_TO_90, KualiDecimal.ZERO);
        agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_91_TO_SYSPR, KualiDecimal.ZERO);

        Integer invoiceAge = invoiceDocument.getAge();
        KualiDecimal openAmount = invoiceDocument.getOpenAmount();
        if(invoiceAge <= 30){
            agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_0_TO_30, openAmount);
        }
        else if(invoiceAge <= 60){
            agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_31_TO_60, openAmount);
        }
        else if(invoiceAge <= 90){
            agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_61_TO_90, openAmount);
        }
        else{
            agingReportInformationHolder.put(ArConstants.CustomerAgingReportFields.TOTAL_91_TO_SYSPR, openAmount);
        }
    }

    /**
     * get the email notification sender from an application parameter
     */
    protected String getNotificationSender() {
        return this.getParameterService().getParameterValueAsString(CustomerAgingReportNotificationStep.class, ArParameterKeyConstants.FROM_EMAIL_ADDRESS_PARAM_NM);
    }

    /**
     * get the timing of when the aging email notification should be sent
     */
    protected Integer getCustomerAgingNotificationOnDays() {
        String daysAsString = this.getParameterService().getParameterValueAsString(CustomerAgingReportNotificationStep.class, ArParameterKeyConstants.NOTIFICATION_DAYS_PARAM_NM);
        if (!StringUtils.isNumeric(daysAsString)) {
            daysAsString = this.getParameterService().getParameterValueAsString(CustomerAgingReportDetail.class, ArConstants.CUSTOMER_INVOICE_AGE);
        }

        return Integer.parseInt(daysAsString);
    }

    /**
     * get the notification selection option from an application parameter
     */
    protected String getNotificationSelectionOption() {
        return this.getParameterService().getParameterValueAsString(CustomerAgingReportNotificationStep.class, ArParameterKeyConstants.NOTIFICATION_SELECTION_TYPE_PARAM_NM);
    }

    /**
     * get the chart values from notification selection criteria.
     */
    protected List<String> getNotificationChartSelection() {
        return this.getNotificationSelection(ArConstants.ArNotificationSelectionField.CHART.fieldName);
    }

    /**
     * get the organization values from notification selection criteria.
     */
    protected List<String> getNotificationOrganizationSelection() {
        return this.getNotificationSelection(ArConstants.ArNotificationSelectionField.ORGANIZATION.fieldName);
    }

    /**
     * get the account values from notification selection criteria.
     */
    protected List<String> getNotificationAccountSelection() {
        return this.getNotificationSelection(ArConstants.ArNotificationSelectionField.ACCOUNT.fieldName);
    }

    /**
     * get the values from notification selection criteria by search option
     */
    protected List<String> getNotificationSelection(String fieldName) {
        return new ArrayList<String>(this.getParameterService().getSubParameterValuesAsString(CustomerAgingReportNotificationStep.class, ArParameterKeyConstants.NOTIFICATION_SELECTION_PARAM_NM, fieldName));
    }

    /**
     * get the notification text from an application parameter
     */
    protected String getNotificationText() {
        return this.getParameterService().getParameterValueAsString(CustomerAgingReportNotificationStep.class, ArParameterKeyConstants.NOTIFICATION_TEXT_PARAM_NM);
    }

    /**
     * get the notification subject from an application parameter
     */
    protected String getNotificationSubject() {
        return this.getParameterService().getParameterValueAsString(CustomerAgingReportNotificationStep.class, ArParameterKeyConstants.NOTIFICATION_SUBJECT_PARAM_NM);
    }

    /**
     * Gets the agingReportTemplate attribute.
     *
     * @return Returns the agingReportTemplate.
     */
    public String getAgingReportTemplate() {
        return agingReportTemplate;
    }

    /**
     * Sets the agingReportTemplate attribute value.
     *
     * @param agingReportTemplate The agingReportTemplate to set.
     */
    public void setAgingReportTemplate(String agingReportTemplate) {
        this.agingReportTemplate = agingReportTemplate;
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
     * Gets the customerInvoiceDocumentService attribute.
     *
     * @return Returns the customerInvoiceDocumentService.
     */
    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    /**
     * Sets the customerInvoiceDocumentService attribute value.
     *
     * @param customerInvoiceDocumentService The customerInvoiceDocumentService to set.
     */
    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
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
