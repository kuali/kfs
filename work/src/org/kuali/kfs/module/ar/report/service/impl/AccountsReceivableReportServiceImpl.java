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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.businessobject.defaultvalue.InstitutionNameValueFinder;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.service.CustomerInvoiceReportService;
import org.kuali.kfs.module.ar.report.util.CustomerInvoiceReportDataHolder;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.user.AuthenticationUserId;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is an implemeation of Effort Certification Extract process, which extracts Labor Ledger records of the employees who were
 * paid on a grant or cost shared during the selected reporting period, and generates effort certification build/temporary document.
 * Its major tasks include:
 * 
 * <li>Identify employees who were paid on a grant or cost shared;</li>
 * <li>Select qualified Labor Ledger records for each identified employee;</li>
 * <li>Generate effort certification build document from the selected Labor Ledger records for each employee.</li>
 */
@Transactional
public class AccountsReceivableReportServiceImpl implements AccountsReceivableReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableReportServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    private UniversityDateService universityDateService;
    CustomerInvoiceReportService service;// = SpringContext.getBean(CustomerInvoiceReportService.class);
    DateTimeService dateTimeService; //= SpringContext.getBean(DateTimeService.class);
    DocumentService documentService;// = SpringContext.getBean(DocumentService.class);


    public void generateCreditMemo(String creditMemoNumber) {
        // TODO Auto-generated method stub
        
    }
    public void generateInvoice(String invoiceNumber) throws WorkflowException {
        documentService = SpringContext.getBean(DocumentService.class);
        CustomerInvoiceDocument invoice = (CustomerInvoiceDocument)documentService.getByDocumentHeaderId(invoiceNumber);
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
        customerMap.put("billToCity", billToAddr.getCustomerCityName());
        
        if (billToAddr.getCustomerCountryCode().equals("US")) { 
            customerMap.put("billToState", billToAddr.getCustomerStateCode());
            customerMap.put("billToZipcode", billToAddr.getCustomerZipCode());
        } else {
            customerMap.put("billToState", billToAddr.getCustomerAddressInternationalProvinceName());
            customerMap.put("billToZipcode", billToAddr.getCustomerInternationalMailCode());
            customerMap.put("billToCountry", billToAddr.getCustomerCountry().getPostalCountryName());

        } 
        }
        if (shipToAddr !=null) {
        customerMap.put("shipToName", shipToAddr.getCustomerAddressName());
        customerMap.put("shipToStreetAddressLine1", shipToAddr.getCustomerLine1StreetAddress());
        customerMap.put("shipToStreetAddressLine2", shipToAddr.getCustomerLine2StreetAddress());
        customerMap.put("shipToCity", shipToAddr.getCustomerCityName());
        
        if (shipToAddr.getCustomerCountryCode().equals("US")) { 
            customerMap.put("shipToState", shipToAddr.getCustomerStateCode());
            customerMap.put("shipToZipcode", shipToAddr.getCustomerZipCode());
        } else {
            customerMap.put("shipToState", shipToAddr.getCustomerAddressInternationalProvinceName());
            customerMap.put("shipToZipcode", shipToAddr.getCustomerInternationalMailCode());
            customerMap.put("shipToCountry", shipToAddr.getCustomerCountry().getPostalCountryName());

        }
        }
        reportDataHolder.setCustomer(customerMap);
    
        Map<String, String> invoiceMap = new HashMap<String, String>();
        invoiceMap.put("poNumber", invoice.getCustomerPurchaseOrderNumber());
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        if(invoice.getCustomerPurchaseOrderDate() != null)
        invoiceMap.put("poDate", dateTimeService.toDateString(invoice.getCustomerPurchaseOrderDate()));
        UniversalUserService userService = SpringContext.getBean(UniversalUserService.class);
        
        String initiatorID = invoice.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        
        String id = StringUtils.upperCase(initiatorID);
        UniversalUser user = new UniversalUser();
        try {
           user = KNSServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(id));
        } catch (Exception e) {
            e.printStackTrace();
        }

        invoiceMap.put("invoicePreparer", user.getPersonFirstName()+" "+user.getPersonLastName() );
        invoiceMap.put("headerField", invoice.getInvoiceHeaderText());
        invoiceMap.put("customerOrg", invoice.getBilledByOrganizationCode());
        invoiceMap.put("docNumber", invoice.getDocumentNumber());
        invoiceMap.put("invoiceDueDate", dateTimeService.toDateString(invoice.getInvoiceDueDate()));
        invoiceMap.put("createDate", dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getCreateDate()));
        invoiceMap.put("invoiceAttentionLineText", invoice.getInvoiceAttentionLineText());
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
        
        Org billingOrg = invoice.getBilledByOrganization();
        String chart = billingOrg.getChartOfAccountsCode();
        String org = billingOrg.getOrganizationCode();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode",chart );
        criteria.put("organizationCode", org);
        OrganizationOptions orgOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);
      
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();
        criteria = new HashMap<String, String>();
        
       Org processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();
        
        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        System.out.println(fiscalYear+"/"+processingOrg.getChartOfAccountsCode()+"/"+processingOrg.getOrganizationCode());
        SystemInformation sysinfo = (SystemInformation)businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", finder.getValue());
        //Org processingOrg = orgOptions.getProcessingOrganization();
        String univAddr = processingOrg.getOrganizationCityName() +", "+ 
        processingOrg.getOrganizationStateCode() +" "+ processingOrg.getOrganizationZipCode();
        sysinfoMap.put("univAddr", univAddr);
        if (sysinfo != null)
        sysinfoMap.put("FEIN", "FED ID# "+sysinfo.getUniversityFederalEmployerIdentificationNumber());
        sysinfoMap.put("checkPayableTo", orgOptions.getOrganizationCheckPayableToName());
        sysinfoMap.put("remitToName", orgOptions.getOrganizationRemitToAddressName());
        sysinfoMap.put("remitToAddressLine1", orgOptions.getOrganizationRemitToLine1StreetAddress());
        sysinfoMap.put("remitToAddressLine2", orgOptions.getOrganizationRemitToLine2StreetAddress());
        sysinfoMap.put("remitToCity", orgOptions.getOrganizationRemitToCityName());
        sysinfoMap.put("remitToState", orgOptions.getOrganizationRemitToState().toString());
        sysinfoMap.put("remitToZip", orgOptions.getOrganizationRemitToZipCode());
        
        invoiceMap.put("billingOrgFax", orgOptions.getOrganizationFaxNumber());
        invoiceMap.put("billingOrgPhone", orgOptions.getOrganizationPhoneNumber());
       
        CustomerInvoiceDetailService invoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        List<CustomerInvoiceDetail> detailsList = (List<CustomerInvoiceDetail>)invoiceDetailService.getCustomerInvoiceDetailsForInvoice(invoice);
        for (int i = 0; i < 50; i++) {
            CustomerInvoiceDetail detail = new CustomerInvoiceDetail();
            detail.setInvoiceItemQuantity(new BigDecimal(i));
            detail.setInvoiceItemUnitOfMeasureCode("EA");
            detail.setInvoiceItemDescription("stuff "+i );
            detail.setInvoiceItemCode("jj"+i);
            detail.setInvoiceItemUnitPrice(new KualiDecimal(55000000));
            detail.setInvoiceItemTaxAmount(new KualiDecimal(4.25));
            detail.setAmount(new KualiDecimal(4));
            detailsList.add(detail);
        }
        
        reportDataHolder.setSysinfo(sysinfoMap);
        reportDataHolder.setDetails(detailsList);
        
        Date runDate = dateTimeService.getCurrentSqlDate();
        service = SpringContext.getBean(CustomerInvoiceReportService.class);
        service.generateReport(reportDataHolder, runDate);
     
        
    }
    public void generateInvoices() {
        // TODO Auto-generated method stub
        
    }
    public void generateStatement(String customerName) {
        
        
        
    }
    public boolean isEmployeeEligibleForEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition) {
        // TODO Auto-generated method stub
        return false;
    }
    
    

   



}