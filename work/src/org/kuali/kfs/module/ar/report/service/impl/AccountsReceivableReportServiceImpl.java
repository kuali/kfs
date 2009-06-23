/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.businessobject.defaultvalue.InstitutionNameValueFinder;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.service.CustomerAgingReportService;
import org.kuali.kfs.module.ar.report.service.CustomerCreditMemoReportService;
import org.kuali.kfs.module.ar.report.service.CustomerInvoiceReportService;
import org.kuali.kfs.module.ar.report.service.CustomerStatementReportService;
import org.kuali.kfs.module.ar.report.service.OCRLineService;
import org.kuali.kfs.module.ar.report.util.CustomerAgingReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoDetailReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerInvoiceReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerStatementDetailReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerStatementReportDataHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.format.PhoneNumberFormatter;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class AccountsReceivableReportServiceImpl implements AccountsReceivableReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableReportServiceImpl.class);

    private DateTimeService dateTimeService;
    private DocumentService documentService;
    private PersonService<Person> personService;
    
    private PhoneNumberFormatter phoneNumberFormatter = new PhoneNumberFormatter();

    private CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateCreditMemo(org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument)
     */
    public File generateCreditMemo(CustomerCreditMemoDocument creditMemo) throws WorkflowException{
        CustomerCreditMemoReportDataHolder reportDataHolder = new CustomerCreditMemoReportDataHolder();
        String invoiceNumber = creditMemo.getFinancialDocumentReferenceInvoiceNumber();
        CustomerInvoiceDocument invoice = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(invoiceNumber);
        String custID = invoice.getAccountsReceivableDocumentHeader().getCustomerNumber();

        CustomerAddressService addrService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress billToAddr = addrService.getPrimaryAddress(custID);

        Map<String, String> creditMemoMap = new HashMap<String, String>();
        creditMemoMap.put("docNumber", creditMemo.getDocumentNumber());
        creditMemoMap.put("refDocNumber", invoice.getDocumentNumber());
        Date date = creditMemo.getDocumentHeader().getDocumentFinalDate();
        if (ObjectUtils.isNotNull(date)) {
            creditMemoMap.put("createDate", dateTimeService.toDateString(date));
        }
        reportDataHolder.setCreditmemo(creditMemoMap);

        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", custID);
        if (billToAddr != null) { 
            customerMap.put("billToName", billToAddr.getCustomerAddressName());
            customerMap.put("billToStreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("billToStreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            String billCityStateZip = "";
            if (billToAddr.getCustomerCountryCode().equals("US")) { 
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerStateCode(), billToAddr.getCustomerZipCode());
            } else {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerAddressInternationalProvinceName(), billToAddr.getCustomerInternationalMailCode());
                customerMap.put("billToCountry", billToAddr.getCustomerCountry().getPostalCountryName());
            }
            customerMap.put("billToCityStateZip", billCityStateZip);
        }

        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        if (ObjectUtils.isNotNull(invoice.getCustomerPurchaseOrderNumber())) {
            invoiceMap.put("poNumber", invoice.getCustomerPurchaseOrderNumber());
        }
        if(invoice.getCustomerPurchaseOrderDate() != null) {
            invoiceMap.put("poDate", dateTimeService.toDateString(invoice.getCustomerPurchaseOrderDate()));
        }

        String initiatorID = invoice.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        Person user = null;
        try {
            user = getPersonService().getPerson(initiatorID);
        } catch (Exception e) {
            LOG.error("Exception thrown from PersonService.getPerson('" + initiatorID + "').", e);
            throw new RuntimeException("Exception thrown from PersonService.getPerson('" + initiatorID + "').", e);
        }
        if (user == null) {
            throw new RuntimeException("User '" + initiatorID + "' could not be retrieved from PersonService.");
        }

        invoiceMap.put("invoicePreparer", user.getFirstName()+" "+user.getLastName() );
        invoiceMap.put("headerField", (ObjectUtils.isNull(invoice.getInvoiceHeaderText())?"":invoice.getInvoiceHeaderText()));
        invoiceMap.put("billingOrgName", invoice.getBilledByOrganization().getOrganizationName());
        invoiceMap.put("pretaxAmount", invoice.getInvoiceItemPreTaxAmountTotal().toString());
        invoiceMap.put("taxAmount", invoice.getInvoiceItemTaxAmountTotal().toString());
        //KualiDecimal taxPercentage = invoice.getStateTaxPercent().add(invoice.getLocalTaxPercent());
        KualiDecimal taxPercentage = new KualiDecimal(6.85);
        invoiceMap.put("taxPercentage", "*** "+taxPercentage.toString()+"%");
        invoiceMap.put("invoiceAmountDue", invoice.getSourceTotal().toString());
        invoiceMap.put("ocrLine", "");
        reportDataHolder.setInvoice(invoiceMap);

        Map<String, String> sysinfoMap = new HashMap<String, String>();
        InstitutionNameValueFinder finder = new InstitutionNameValueFinder();

        Organization billingOrg = invoice.getBilledByOrganization();
        String chart = billingOrg.getChartOfAccountsCode();
        String org = billingOrg.getOrganizationCode();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode",chart );
        criteria.put("organizationCode", org);
        OrganizationOptions orgOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();
        criteria = new HashMap<String, String>();

        Organization processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();

        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = (SystemInformation)businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", StringUtils.upperCase(finder.getValue()));
        String univAddr = processingOrg.getOrganizationCityName() +", "+ 
        processingOrg.getOrganizationStateCode() +" "+ processingOrg.getOrganizationZipCode();
        sysinfoMap.put("univAddr", univAddr);
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #"+sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }

        reportDataHolder.setSysinfo(sysinfoMap);

        invoiceMap.put("billingOrgFax", (String)phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationFaxNumber())));
        invoiceMap.put("billingOrgPhone", (String)phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationPhoneNumber())));

        creditMemo.populateCustomerCreditMemoDetailsAfterLoad();
        List<CustomerCreditMemoDetail> detailsList = creditMemo.getCreditMemoDetails();
        List<CustomerCreditMemoDetailReportDataHolder> details = new ArrayList<CustomerCreditMemoDetailReportDataHolder>();
        CustomerCreditMemoDetailReportDataHolder detailDataHolder;
        for (CustomerCreditMemoDetail detail : detailsList) {
            if(detail.getCreditMemoLineTotalAmount().isGreaterThan(KualiDecimal.ZERO)) {
                detailDataHolder = new CustomerCreditMemoDetailReportDataHolder(detail, detail.getCustomerInvoiceDetail());
                details.add(detailDataHolder);
            }
        }

        reportDataHolder.setDetails(details);

        Date runDate = dateTimeService.getCurrentSqlDate();
        CustomerCreditMemoReportService service = SpringContext.getBean(CustomerCreditMemoReportService.class);
        File report = service.generateReport(reportDataHolder, runDate);

        return report;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public File generateInvoice(CustomerInvoiceDocument invoice) {

        CustomerInvoiceReportDataHolder reportDataHolder = new CustomerInvoiceReportDataHolder();
        String custID = invoice.getAccountsReceivableDocumentHeader().getCustomerNumber();
        CustomerService custService = SpringContext.getBean(CustomerService.class);
        Customer cust = custService.getByPrimaryKey(custID);
        Integer customerBillToAddressIdentifier = invoice.getCustomerBillToAddressIdentifier();
        Integer customerShipToAddressIdentifier = invoice.getCustomerShipToAddressIdentifier();
        CustomerAddressService addrService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress billToAddr = addrService.getByPrimaryKey(custID, customerBillToAddressIdentifier);
        CustomerAddress shipToAddr = addrService.getByPrimaryKey(custID, customerShipToAddressIdentifier);

        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", custID);
        if (billToAddr != null){ 
            customerMap.put("billToName", billToAddr.getCustomerAddressName());
            customerMap.put("billToStreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("billToStreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            String billCityStateZip = "";
            if (billToAddr.getCustomerCountryCode().equals("US")) { 
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerStateCode(), billToAddr.getCustomerZipCode());
            } else {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerAddressInternationalProvinceName(), billToAddr.getCustomerInternationalMailCode());
                customerMap.put("billToCountry", billToAddr.getCustomerCountry().getPostalCountryName());
            }
            customerMap.put("billToCityStateZip", billCityStateZip);
        }
        if (shipToAddr !=null) {
            customerMap.put("shipToName", shipToAddr.getCustomerAddressName());
            customerMap.put("shipToStreetAddressLine1", shipToAddr.getCustomerLine1StreetAddress());
            customerMap.put("shipToStreetAddressLine2", shipToAddr.getCustomerLine2StreetAddress());
            String shipCityStateZip = "";
            if (shipToAddr.getCustomerCountryCode().equals("US")) { 
                shipCityStateZip = generateCityStateZipLine(shipToAddr.getCustomerCityName(), shipToAddr.getCustomerStateCode(), shipToAddr.getCustomerZipCode());
            } else {
                shipCityStateZip = generateCityStateZipLine(shipToAddr.getCustomerCityName(), shipToAddr.getCustomerAddressInternationalProvinceName(), shipToAddr.getCustomerInternationalMailCode());
                customerMap.put("shipToCountry", shipToAddr.getCustomerCountry().getPostalCountryName());
            }
            customerMap.put("shipToCityStateZip", shipCityStateZip);
        }
        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        invoiceMap.put("poNumber", invoice.getCustomerPurchaseOrderNumber());
        if(invoice.getCustomerPurchaseOrderDate() != null) {
            invoiceMap.put("poDate", dateTimeService.toDateString(invoice.getCustomerPurchaseOrderDate()));
        }

        String initiatorID = invoice.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        Person user = null;
        try {
            user = getPersonService().getPerson(initiatorID);
        } catch (Exception e) {
            LOG.error("Exception thrown from PersonService.getPerson('" + initiatorID + "').", e);
            throw new RuntimeException("Exception thrown from PersonService.getPerson('" + initiatorID + "').", e);
        }
        if (user == null) {
            throw new RuntimeException("User '" + initiatorID + "' could not be retrieved from PersonService.");
        }
        
        invoiceMap.put("invoicePreparer", user.getFirstName()+" "+user.getLastName() );
        invoiceMap.put("headerField", invoice.getInvoiceHeaderText());
        invoiceMap.put("customerOrg", invoice.getBilledByOrganizationCode());
        invoiceMap.put("docNumber", invoice.getDocumentNumber());
        invoiceMap.put("invoiceDueDate", dateTimeService.toDateString(invoice.getInvoiceDueDate()));
        invoiceMap.put("createDate", dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getCreateDate()));
        invoiceMap.put("invoiceAttentionLineText", StringUtils.upperCase(invoice.getInvoiceAttentionLineText()));
        invoiceMap.put("billingOrgName", invoice.getBilledByOrganization().getOrganizationName());
        invoiceMap.put("pretaxAmount", currencyFormatter.format(invoice.getInvoiceItemPreTaxAmountTotal()).toString());
        invoiceMap.put("taxAmount", currencyFormatter.format(invoice.getInvoiceItemTaxAmountTotal()).toString());
        invoiceMap.put("taxPercentage", ""); // suppressing this as its useless ... see KULAR-415
        invoiceMap.put("invoiceAmountDue", currencyFormatter.format(invoice.getSourceTotal()).toString());
        invoiceMap.put("invoiceTermsText", invoice.getInvoiceTermsText());

        OCRLineService ocrService = SpringContext.getBean(OCRLineService.class);
        String ocrLine = ocrService.generateOCRLine(invoice.getSourceTotal(), custID, invoice.getDocumentNumber());
        invoiceMap.put("ocrLine", ocrLine);
        CustomerInvoiceDetailService invoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        List<CustomerInvoiceDetail> detailsList = (List<CustomerInvoiceDetail>)invoiceDetailService.getCustomerInvoiceDetailsForInvoice(invoice);
        CustomerInvoiceDetail firstDetail = (CustomerInvoiceDetail)detailsList.get(0);
        String firstChartCode = firstDetail.getChartOfAccountsCode();
        String firstAccount = firstDetail.getAccountNumber();
        invoiceMap.put("chartAndAccountOfFirstItem", firstChartCode+firstAccount);

        Map<String, String> sysinfoMap = new HashMap<String, String>();
        InstitutionNameValueFinder finder = new InstitutionNameValueFinder();

        Organization billingOrg = invoice.getBilledByOrganization();
        String chart = billingOrg.getChartOfAccountsCode();
        String org = billingOrg.getOrganizationCode();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chart);
        criteria.put("organizationCode", org);
        OrganizationOptions orgOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();
        criteria = new HashMap<String, String>();

        Organization processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();

        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = (SystemInformation)businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", StringUtils.upperCase(finder.getValue()));
        sysinfoMap.put("univAddr", generateCityStateZipLine(processingOrg.getOrganizationCityName(), processingOrg.getOrganizationStateCode(), processingOrg.getOrganizationZipCode()));
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #"+sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }
        sysinfoMap.put("checkPayableTo", orgOptions.getOrganizationCheckPayableToName());
        sysinfoMap.put("remitToName", orgOptions.getOrganizationRemitToAddressName());
        sysinfoMap.put("remitToAddressLine1", orgOptions.getOrganizationRemitToLine1StreetAddress());
        sysinfoMap.put("remitToAddressLine2", orgOptions.getOrganizationRemitToLine2StreetAddress());

        sysinfoMap.put("remitToCityStateZip", generateCityStateZipLine(orgOptions.getOrganizationRemitToCityName(), orgOptions.getOrganizationRemitToStateCode(), orgOptions.getOrganizationRemitToZipCode()));
        
        invoiceMap.put("billingOrgFax", (String)phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationFaxNumber())));
        invoiceMap.put("billingOrgPhone", (String)phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationPhoneNumber())));

        invoiceMap.put("orgOptionsMessageText", orgOptions.getOrganizationMessageText());

        reportDataHolder.setSysinfo(sysinfoMap);
        reportDataHolder.setDetails(detailsList);
        reportDataHolder.setInvoice(invoiceMap);

        Date runDate = dateTimeService.getCurrentSqlDate();
        CustomerInvoiceReportService service = SpringContext.getBean(CustomerInvoiceReportService.class);
        File report = service.generateReport(reportDataHolder, runDate);

        invoice.setPrintDate(runDate);
        documentService.updateDocument(invoice);
        return report;
    }

    /**
     * 
     * This method...
     * @param billingChartCode
     * @param billingOrgCode
     * @param customerNumber
     * @param details
     * @return
     */
    public File generateStatement(String billingChartCode, String billingOrgCode, String customerNumber, Organization processingOrg, List<CustomerStatementDetailReportDataHolder> details){

        CustomerStatementReportDataHolder reportDataHolder = new CustomerStatementReportDataHolder();
        CustomerAddressService addrService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress billToAddr = addrService.getPrimaryAddress(customerNumber);

        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", customerNumber);
        if (billToAddr != null){ 
            customerMap.put("billToName", billToAddr.getCustomerAddressName());
            customerMap.put("billToStreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("billToStreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            String billCityStateZip = "";
            if (billToAddr.getCustomerCountryCode().equals("US")) { 
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerStateCode(), billToAddr.getCustomerZipCode());
            } else {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerAddressInternationalProvinceName(), billToAddr.getCustomerInternationalMailCode());
                customerMap.put("billToCountry", billToAddr.getCustomerCountry().getPostalCountryName());
            }
            customerMap.put("billToCityStateZip", billCityStateZip);
        }

        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        invoiceMap.clear();
        invoiceMap.put("createDate", dateTimeService.toDateString(dateTimeService.getCurrentDate()));
        invoiceMap.put("customerOrg", billingOrgCode);
        Organization billingOrg = SpringContext.getBean(OrganizationService.class).getByPrimaryId(billingChartCode, billingOrgCode);
        invoiceMap.put("billingOrgName", billingOrg.getOrganizationName());
        
        KualiDecimal amountDue = new KualiDecimal(0);
        for (CustomerStatementDetailReportDataHolder data : details) {
            if (data.getFinancialDocumentTotalAmountCharge()!=null) {
                amountDue = amountDue.add(data.getFinancialDocumentTotalAmountCharge());
            }
            if (data.getFinancialDocumentTotalAmountCredit()!=null) {
                amountDue = amountDue.subtract(data.getFinancialDocumentTotalAmountCredit());
            }
        }
        invoiceMap.put("amountDue", currencyFormatter.format(amountDue).toString());
        String ocrLine = SpringContext.getBean(OCRLineService.class).generateOCRLine(amountDue, customerNumber, null);
        invoiceMap.put("ocrLine", ocrLine);
        
        Map<String, String> sysinfoMap = new HashMap<String, String>();
        InstitutionNameValueFinder finder = new InstitutionNameValueFinder();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", billingChartCode);
        criteria.put("organizationCode", billingOrgCode);
        OrganizationOptions orgOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        sysinfoMap.put("checkPayableTo", orgOptions.getOrganizationCheckPayableToName());
        sysinfoMap.put("remitToName", orgOptions.getOrganizationRemitToAddressName());
        sysinfoMap.put("remitToAddressLine1", orgOptions.getOrganizationRemitToLine1StreetAddress());
        sysinfoMap.put("remitToAddressLine2", orgOptions.getOrganizationRemitToLine2StreetAddress());
        sysinfoMap.put("remitToCityStateZip", generateCityStateZipLine(orgOptions.getOrganizationRemitToCityName(), orgOptions.getOrganizationRemitToStateCode(), orgOptions.getOrganizationRemitToZipCode()));

        invoiceMap.put("billingOrgFax", (String)phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationFaxNumber())));
        invoiceMap.put("billingOrgPhone", (String)phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationPhoneNumber())));

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();

        criteria.clear();
        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = (SystemInformation)businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);
        
        sysinfoMap.put("univName", StringUtils.upperCase(finder.getValue()));
        sysinfoMap.put("univAddr", generateCityStateZipLine(processingOrg.getOrganizationCityName(), processingOrg.getOrganizationStateCode(), processingOrg.getOrganizationZipCode()));
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #"+sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }

        calculateAgingAmounts(details, invoiceMap); 
        
        reportDataHolder.setSysinfo(sysinfoMap);
        reportDataHolder.setDetails(details);
        reportDataHolder.setInvoice(invoiceMap);

        Date runDate = dateTimeService.getCurrentSqlDate();
        CustomerStatementReportService service = SpringContext.getBean(CustomerStatementReportService.class);
        File f = service.generateReport(reportDataHolder, runDate);
        return f;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByBillingOrg(java.lang.String, java.lang.String, java.sql.Date)
     */
    public List<File> generateInvoicesByBillingOrg(String chartCode, String orgCode, Date date) {
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        List<CustomerInvoiceDocument> invoices = invoiceDocService.getPrintableCustomerInvoiceDocumentsByBillingChartAndOrg(chartCode, orgCode);
        List<File> reports = new ArrayList<File>();

        for (CustomerInvoiceDocument doc : invoices) {
            if (date == null) {
                reports.add(generateInvoice(doc));
            }
            else if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate())!=null) {
                if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate()).equals(dateTimeService.toDateString(date))) {
                    reports.add(generateInvoice(doc));
                }
            }
        }
        return reports;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByProcessingOrg(java.lang.String, java.lang.String, java.sql.Date)
     */
    public List<File> generateInvoicesByProcessingOrg(String chartCode, String orgCode, Date date){
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        List<CustomerInvoiceDocument> invoices = invoiceDocService.getPrintableCustomerInvoiceDocumentsByProcessingChartAndOrg(chartCode, orgCode);
        
        List<File> reports = new ArrayList<File>();
        for (CustomerInvoiceDocument doc : invoices) {
            if (date == null) {
                reports.add(generateInvoice(doc));
            }
            else if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate())!=null) {
                if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate()).equals(dateTimeService.toDateString(date))) {
                    reports.add(generateInvoice(doc));
                }
            }
        }
        return reports;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByInitiator(java.lang.String)
     */
    public List<File> generateInvoicesByInitiator(String initiator, java.sql.Date date) {
        List<File> reports = new ArrayList<File>();
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDocument> invoices = invoiceDocService.getPrintableCustomerInvoiceDocumentsByInitiatorPrincipalName(initiator);
        for (CustomerInvoiceDocument invoice : invoices) {
            if (date == null) {
                reports.add(generateInvoice(invoice));
            }
            else if (dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getCreateDate())!=null) {
                if (dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getCreateDate()).equals(dateTimeService.toDateString(date))) {
                    reports.add(generateInvoice(invoice));
                }
            }
        }
        return reports;
    }

    private List<File> generateStatementReports(Collection<CustomerInvoiceDocument> invoices) {
        List<CustomerInvoiceDocument> invoiceList = new ArrayList<CustomerInvoiceDocument>(invoices);
        Collections.sort(invoiceList);

        List<File> reports = new ArrayList<File>();

        Map<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>> statementDetailsSorted = sortCustomerStatementData(invoiceList);

        Set<String> processingOrgKeys = statementDetailsSorted.keySet();
        
        for(String processingChartAndOrg : processingOrgKeys) {
            String processingChartCode = processingChartAndOrg.substring(0, 2);
            String processingOrgCode = processingChartAndOrg.substring(2);
            Organization processingOrg = SpringContext.getBean(OrganizationService.class).getByPrimaryId(processingChartCode, processingOrgCode);

            Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>> statementDetailsByProcessingOrg = statementDetailsSorted.get(processingChartAndOrg);
            
            Set<String> billingOrgKeys = statementDetailsByProcessingOrg.keySet();
            
            for(String billingChartAndOrg : billingOrgKeys) {
                String billingChartCode = billingChartAndOrg.substring(0, 2);
                String billingOrgCode = billingChartAndOrg.substring(2);

                Map<String, List<CustomerStatementDetailReportDataHolder>> statementDetailsByBillingOrg = statementDetailsByProcessingOrg.get(billingChartAndOrg);

                Set<String> customerKeys = statementDetailsByBillingOrg.keySet();

                for(String customerId : customerKeys) {
                    List<CustomerStatementDetailReportDataHolder> statementDetailsByCustomer = statementDetailsByBillingOrg.get(customerId);
                    
                    reports.add(generateStatement(billingChartCode, billingOrgCode, customerId, processingOrg, statementDetailsByCustomer));
                }
            }
        }
        
        return reports;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByBillingOrg(java.lang.String, java.lang.String)
     */
    public List<File> generateStatementByBillingOrg(String chartCode, String orgCode) {
        return generateStatementReports(SpringContext.getBean(CustomerInvoiceDocumentService.class).getPrintableCustomerInvoiceDocumentsByBillingChartAndOrg(chartCode, orgCode));
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByAccount(java.lang.String)
     */
    public List<File> generateStatementByAccount(String accountNumber) {
        return generateStatementReports(SpringContext.getBean(CustomerInvoiceDocumentService.class).getCustomerInvoiceDocumentsByAccountNumber(accountNumber));
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByCustomer(java.lang.String)
     */
    public List<File> generateStatementByCustomer(String customerNumber) {
        return generateStatementReports(SpringContext.getBean(CustomerInvoiceDocumentService.class).getCustomerInvoiceDocumentsByCustomerNumber(customerNumber));
    }

    /**
     * 
     * This method...
     * @param invoiceList
     * @return
     */
    private Map<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>> sortCustomerStatementData(List<CustomerInvoiceDocument> invoiceList) {
        Map<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>> customerStatementDetailsSorted = new HashMap<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>>();
        CustomerCreditMemoDocumentService service = SpringContext.getBean(CustomerCreditMemoDocumentService.class);

        // This is where all the magic will begin
        for(CustomerInvoiceDocument invoice : invoiceList) {
            
            if(invoice.getDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.APPROVED)) {
                if (invoice.isOpenInvoiceIndicator()) {
            
                    // Break down list into a map based on processing org
                    Organization processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();
        
                    // Retrieve the collection of invoices that already exist for the processing org provided
                    Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>> statementDetailsForGivenProcessingOrg = customerStatementDetailsSorted.get(getChartAndOrgCodesCombined(processingOrg));
                    if(statementDetailsForGivenProcessingOrg==null) {
                        statementDetailsForGivenProcessingOrg = new HashMap<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>();
                    }
        
                    // Then break down that list for billing chart and org codes
                    Organization billedByOrg = invoice.getBilledByOrganization();
                    Map<String, List<CustomerStatementDetailReportDataHolder>> statementDetailsForGivenBillingOrg = statementDetailsForGivenProcessingOrg.get(getChartAndOrgCodesCombined(billedByOrg));
                    if(statementDetailsForGivenBillingOrg==null) {
                        statementDetailsForGivenBillingOrg = new HashMap<String, List<CustomerStatementDetailReportDataHolder>>();
                    }
                    
                    // Then break down that list by customer
                    String customerNumber = invoice.getCustomer().getCustomerNumber();
                    List<CustomerStatementDetailReportDataHolder> statementDetailsByCustomer = statementDetailsForGivenBillingOrg.get(customerNumber);
                    if(statementDetailsByCustomer==null) {
                        statementDetailsByCustomer = new ArrayList<CustomerStatementDetailReportDataHolder>();
                    }

                    // Now add the appropriate invoice and associated memos to the collection
//                    Collection<CustomerCreditMemoDocument> creditMemos = service.getCustomerCreditMemoDocumentByInvoiceDocument(invoice.getDocumentNumber());
//                    for (CustomerCreditMemoDocument doc : creditMemos) {
//                        try {
//                            doc.populateCustomerCreditMemoDetailsAfterLoad();
//                            CustomerCreditMemoDocument creditMemoDoc = (CustomerCreditMemoDocument) documentService.getByDocumentHeaderId(doc.getDocumentNumber());
//                            CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(creditMemoDoc.getDocumentHeader(), creditMemoDoc.getInvoice().getAccountsReceivableDocumentHeader().getProcessingOrganization(), ArConstants.CREDIT_MEMO_DOC_TYPE, creditMemoDoc.getTotalDollarAmount());
//                            CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(doc.getDocumentHeader(), doc.getInvoice().getAccountsReceivableDocumentHeader().getProcessingOrganization(), ArConstants.CREDIT_MEMO_DOC_TYPE, doc.getTotalDollarAmount());
//                            statementDetailsByCustomer.add(detail);
//                        } catch(Exception ex) {
//                            LOG.error(ex);
//                        }
//                    }
                    CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(invoice.getDocumentHeader(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), ArConstants.INVOICE_DOC_TYPE, invoice.getTotalDollarAmount());
                    statementDetailsByCustomer.add(detail);
                    
                    statementDetailsForGivenBillingOrg.put(customerNumber, statementDetailsByCustomer);
                    statementDetailsForGivenProcessingOrg.put(getChartAndOrgCodesCombined(billedByOrg), statementDetailsForGivenBillingOrg);
                    customerStatementDetailsSorted.put(getChartAndOrgCodesCombined(processingOrg), statementDetailsForGivenProcessingOrg);
                }
            }
        }        
        return customerStatementDetailsSorted;
    }
    
    /**
     * 
     * This method...
     * @param city
     * @param state
     * @param zipCode
     * @return
     */
    private String generateCityStateZipLine(String city, String state, String zipCode) {
        StringBuffer cityStateZip = new StringBuffer(city);
        cityStateZip.append(", ").append(state);
        cityStateZip.append("  ").append(zipCode);
        return cityStateZip.toString();
    }

    /**
     * 
     * This method calculates the total aging amounts for a given statement.  This will often be a collection of totals from multiple invoices and/or credit memos.  
     * @param details
     * @param invoiceMap
     */
    private void calculateAgingAmounts(List<CustomerStatementDetailReportDataHolder> details, Map<String, String> invoiceMap) {
        for(CustomerStatementDetailReportDataHolder csdrdh : details) {
            CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
            CustomerInvoiceDocument ci = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(csdrdh.getDocumentNumber());
            
            java.util.Date reportRunDate = dateTimeService.getCurrentDate();
            Collection<CustomerInvoiceDetail> invoiceDetails = customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci);
            CustomerAgingReportDataHolder agingData = SpringContext.getBean(CustomerAgingReportService.class).calculateAgingReportAmounts(invoiceDetails, reportRunDate);
            
            addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_0_TO_30, agingData.getTotal0to30(), invoiceMap);
            addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_31_TO_60, agingData.getTotal31to60(), invoiceMap);
            addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_61_TO_90, agingData.getTotal61to90(), invoiceMap);
            addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_91_TO_SYSPR, agingData.getTotal91toSYSPR(), invoiceMap);
            // it's not necessary to display an extra bucket on the statement, so I'm rolling up the amounts over 90 days into a single bucket for display
            addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_91_TO_SYSPR, agingData.getTotalSYSPRplus1orMore(), invoiceMap);
            addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_AMOUNT_DUE, agingData.getTotalAmountDue(), invoiceMap);
        }
    }

    /**
     * 
     * This method...
     * @param mapKey
     * @param amountToAdd
     * @param invoiceMap
     */
    private void addAgingAmountToInvoiceMap(String mapKey, KualiDecimal amountToAdd, Map<String, String> invoiceMap) {
        BigDecimal amount = BigDecimal.ZERO;
        String currentAmount = invoiceMap.get(mapKey);
        if(StringUtils.isNotEmpty(currentAmount)) {
            try {
                amount = new BigDecimal(StringUtils.remove(currentAmount, ','));
            } catch(NumberFormatException nfe) {
                LOG.error(currentAmount+" is an invalid amount.", nfe);
            }
        }
        if(ObjectUtils.isNull(amountToAdd)) {
            amountToAdd = KualiDecimal.ZERO;
        }
        KualiDecimal newAmount = new KualiDecimal(amount.add(amountToAdd.bigDecimalValue()));
        invoiceMap.put(mapKey, currencyFormatter.format(newAmount).toString());
    }
    
    /**
     * 
     * This method...
     * @param org
     * @return
     */
    private String getChartAndOrgCodesCombined(Organization org) {
        if(org==null) {
            return null;
        }
        StringBuffer chartAndOrg = new StringBuffer(6);
        chartAndOrg.append(org.getChartOfAccountsCode()).append(org.getOrganizationCode());
        return chartAndOrg.toString();
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public DateTimeService getDateTimeService() {
        if(dateTimeService==null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }
    
    /**
     * 
     * This method...
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * @return Returns the personService.
     */
    protected PersonService<Person> getPersonService() {
        if(personService==null)
            personService = SpringContext.getBean(PersonService.class);
        return personService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
    
}
