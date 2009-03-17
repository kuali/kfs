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
package org.kuali.kfs.module.ar.batch;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.batch.service.LockboxService;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomerInvoiceDocumentBatchStep extends AbstractStep {
    
    private static final long MAX_SEQ_NBR_OFFSET = 1000;
    
    CustomerInvoiceDocumentService customerInvoiceDocumentService; 
    BusinessObjectService businessObjectService;
    DocumentService documentService;
    DateTimeService dateTimeService;
    Collection<String> createdInvoices = new ArrayList<String>();

    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentBatchStep.class);

    // parameter constants and logging
    private static final int NUMBER_OF_INVOICES_TO_CREATE = 5;
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_CODE = "KFS-AR";
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_STEP = "CustomerInvoiceDocumentBatchStep";
// ******************* replaced while testing   private static final String RUN_INDICATOR_PARAMETER_VALUE = "N";
    private static final String RUN_INDICATOR_PARAMETER_VALUE = "Y"; // Tells the job framework whether to run this job or not; set to NO because the GenesisBatchJob needs to only be run once after database initialization.
    private static final String RUN_INDICATOR_PARAMETER_ALLOWED = "A";
    private static final String RUN_INDICATOR_PARAMETER_TYPE = "CONFG";

    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        
        GlobalVariables.clear();
        GlobalVariables.setUserSession(new UserSession("khuntley"));
        setDateTimeService(SpringContext.getBean(DateTimeService.class));
        
        Date billingDate = getDateTimeService().getCurrentDate();
        List<String> customernames;
        
        if ((jobName.length() <=8 ) && (jobName.length() >= 4)) {
            setCustomerInvoiceDocumentService(SpringContext.getBean(CustomerInvoiceDocumentService.class));
            setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
            setDocumentService(SpringContext.getBean(DocumentService.class));

            customernames = Arrays.asList(jobName);
        } else {
            customernames = Arrays.asList("ABB2", "3MC17500","ACE21725","ANT7297","CAR23612", "CON19567", "DEL14448", "EAT17609", "GAP17272");
        }

        // create non-random data
        if (customernames.size() > 1) {
            for (int i = 0; i < NUMBER_OF_INVOICES_TO_CREATE; i++) {
    
                billingDate = DateUtils.addDays(billingDate, -30);
    
                createCustomerInvoiceDocumentForFunctionalTesting("HIL22195", billingDate, 1, new KualiDecimal(10), new BigDecimal(1), "2336320", "BL", "BUSCF");  // $10 entries
                createCustomerInvoiceDocumentForFunctionalTesting("IBM2655", billingDate, 2, new KualiDecimal(10), new BigDecimal(1), "2336320", "BL", "IBCE");  // $20 entries
                createCustomerInvoiceDocumentForFunctionalTesting("JAS19572", billingDate, 3, new KualiDecimal(10), new BigDecimal(1), "2336320", "BL", "WRB");  // $30 entries
    
                Thread.sleep(500);
            }
        }

        // easy dynamic data creation
        if (customernames.size() == 1) {
            billingDate = jobRunDate;
            createCustomerInvoiceDocumentForFunctionalTesting(customernames.get(0), billingDate, 1, new KualiDecimal(10), new BigDecimal(1), "1111111", "BA", "MATT");  // $10 entries
            Thread.sleep(500);
        }

        // create lockboxes for the non-random invoices
        Long seqNbr = findAvailableLockboxBaseSeqNbr();
        int scenarioNbr =1;
        for (String createdInvoice : createdInvoices){
          createLockboxesForFunctionalTesting(createdInvoice, seqNbr, scenarioNbr);
          Thread.sleep(500);
          seqNbr++;
          if (scenarioNbr<=6) {
              scenarioNbr++;
          }
          else {
              scenarioNbr = 1;
          }
        }

        // create random data
//        if (customernames.size() > 1) {
//        for (String customername : customernames) {
//
//            billingDate = getDateTimeService().getCurrentDate();
//
//            for( int i = 0; i < NUMBER_OF_INVOICES_TO_CREATE; i++ ){
//
//                billingDate = DateUtils.addDays(billingDate, -30);
//
//                createCustomerInvoiceDocumentForFunctionalTesting(customername,billingDate, 0, null, null, "1031400", "BL");
//                Thread.sleep(500);
//
//            }
//        }
//        }



        setInitiatedParameter();
        return true;
    }
   
    private long findAvailableLockboxBaseSeqNbr() {
        LockboxService lockboxService = SpringContext.getBean(LockboxService.class);
        return lockboxService.getMaxLockboxSequenceNumber() + MAX_SEQ_NBR_OFFSET;
    }
    
    private boolean dupLockboxRecordExists(Long seqNbr) {
        Map<String,Long> pks = new HashMap<String,Long>();
        pks.put("invoiceSequenceNumber", seqNbr);
        Lockbox dupLockBox = (Lockbox) businessObjectService.findByPrimaryKey(Lockbox.class, pks);
        return (dupLockBox != null);
    }
    
    /**
     * This method sets a parameter that tells the step that it has already run and it does not need to run again.
     */
    private void setInitiatedParameter() {
        // first see if we can find an existing Parameter object with this key
        Parameter runIndicatorParameter = (Parameter) businessObjectService.findByPrimaryKey(Parameter.class, this.buildSearchKeyMap());
        if (runIndicatorParameter == null)
        {
           runIndicatorParameter = new Parameter();
           runIndicatorParameter.setVersionNumber(new Long(1));
           runIndicatorParameter.setParameterNamespaceCode(CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_CODE);
           runIndicatorParameter.setParameterDetailTypeCode(CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_STEP);
           runIndicatorParameter.setParameterName(Job.STEP_RUN_PARM_NM);
           runIndicatorParameter.setParameterConstraintCode(CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_ALLOWED);
           runIndicatorParameter.setParameterTypeCode(CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_TYPE);
        }
        runIndicatorParameter.setParameterValue(CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_VALUE);
        businessObjectService.save(runIndicatorParameter);
    }
    
    private Map<String,Object> buildSearchKeyMap()
    {
       Map<String,Object> pkMapForParameter = new HashMap<String,Object>();
       PersistenceStructureService psService = SpringContext.getBean(PersistenceStructureService.class);

       // set up a list of all the  field names and values of the fields in the Parameter object.
       // the OJB names are nowhere in Kuali properties, apparently.
       // but, since we use set routines above, we know what the names must be.  if they change at some point, we will have to change the set routines anyway.
       // we can change the code here also when we do that.
       Map<String,Object> fieldNamesValuesForParameter = new HashMap<String,Object>();
       fieldNamesValuesForParameter.put("parameterNamespaceCode",CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_CODE);
       fieldNamesValuesForParameter.put("parameterDetailTypeCode",CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_NAMESPACE_STEP);
       fieldNamesValuesForParameter.put("parameterName",Job.STEP_RUN_PARM_NM);
       fieldNamesValuesForParameter.put("parameterConstraintCode",CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_ALLOWED);
       fieldNamesValuesForParameter.put("parameterTypeCode",CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_TYPE);

       // get the primary keys and assign them to values
       List<String> parameterPKFields = psService.getPrimaryKeys(Parameter.class);
       for (String pkFieldName: parameterPKFields)
       {
           pkMapForParameter.put(pkFieldName,fieldNamesValuesForParameter.get(pkFieldName));
       }
       return (pkMapForParameter);
    }

    public void createLockboxesForFunctionalTesting(String invoiceNumber, Long seqNbr, int testtype) throws InterruptedException {
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(invoiceNumber);

        Lockbox newLockbox = new Lockbox();

        // 1) Payment matches customer (CUST_NBR), invoice number (FDOC_REF_INV_NBR), and amount (AR_INV_PD_APLD_AMT). These should auto-approve, the remaining scenarios should not.
        if (testtype == 1) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
            newLockbox.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
            newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
            newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocumentService.getPaidAppliedTotalForInvoice(invoiceNumber));
            newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
            newLockbox.setCustomerPaymentMediumCode("CK");
            newLockbox.setBankCode("1003");
            newLockbox.setBatchSequenceNumber(8004);
            newLockbox.setInvoiceSequenceNumber(seqNbr);
            newLockbox.setLockboxNumber("66249");
        }

        // 2) Payment matches customer and invoice, but the invoice has no outstanding balance (due to a previous payment, a credit memo, or a write-off)
        if (testtype == 2) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber(createPaidOffCustomerInvoice());
            newLockbox.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
            newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
            newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocumentService.getPaidAppliedTotalForInvoice(invoiceNumber));
            newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
            newLockbox.setCustomerPaymentMediumCode("CK");
            newLockbox.setBankCode("1003");
            newLockbox.setBatchSequenceNumber(8004);
            newLockbox.setInvoiceSequenceNumber(seqNbr);
            newLockbox.setLockboxNumber("66249");
        }

        // 3) Payment matches customer and invoice, but the amount of the payment exceeds the outstanding balance on the invoice.
        if (testtype == 3) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
            newLockbox.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
            newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
            newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocument.getTotalDollarAmount().add(new KualiDecimal(100)));
            newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
            newLockbox.setCustomerPaymentMediumCode("CK");
            newLockbox.setBankCode("1003");
            newLockbox.setBatchSequenceNumber(8004);
            newLockbox.setInvoiceSequenceNumber(seqNbr);
            newLockbox.setLockboxNumber("66249");
        }

        // 4) The payment matches customer and invoice, but the amount is short-paid (less than the invoice outstanding balance)
        if (testtype == 4) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
            newLockbox.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
            newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
            newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocumentService.getPaidAppliedTotalForInvoice(invoiceNumber).subtract(new KualiDecimal(1)));
            newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
            newLockbox.setCustomerPaymentMediumCode("CK");
            newLockbox.setBankCode("1003");
            newLockbox.setBatchSequenceNumber(8004);
            newLockbox.setInvoiceSequenceNumber(seqNbr);
            newLockbox.setLockboxNumber("66249");
        }

        // 5) The payment matches a customer number, but the invoice number is missing
        if (testtype == 5) {
          //  newLockbox.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
            newLockbox.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
            newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
            newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocumentService.getPaidAppliedTotalForInvoice(invoiceNumber));
            newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
            newLockbox.setCustomerPaymentMediumCode("CK");
            newLockbox.setBankCode("1003");
            newLockbox.setBatchSequenceNumber(8004);
            newLockbox.setInvoiceSequenceNumber(seqNbr);
            newLockbox.setLockboxNumber("66249");
        }

        // 6) The payment matches a customer number, but the invoice number is invalid
        if (testtype == 6) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber("999999");
            newLockbox.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
            newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
            newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocumentService.getPaidAppliedTotalForInvoice(invoiceNumber));
            newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
            newLockbox.setCustomerPaymentMediumCode("CK");
            newLockbox.setBankCode("1003");
            newLockbox.setBatchSequenceNumber(8004);
            newLockbox.setInvoiceSequenceNumber(seqNbr);
            newLockbox.setLockboxNumber("66249");
        }

        // 7) The payment matches nothing (not even the customer number)
        if (testtype == 7) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber("999999");
            newLockbox.setCustomerNumber("KEY17536");
            newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
            newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocumentService.getPaidAppliedTotalForInvoice(invoiceNumber));
            newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
            newLockbox.setCustomerPaymentMediumCode("CK");
            newLockbox.setBankCode("1003");
            newLockbox.setBatchSequenceNumber(8004);
            newLockbox.setInvoiceSequenceNumber(seqNbr);
            newLockbox.setLockboxNumber("66249");
        }

        LOG.info("Creating customer LOCKBOX [" + seqNbr.toString() + "] for invoice " + invoiceNumber);
        if (dupLockboxRecordExists(seqNbr)) {
            throw new RuntimeException("Trying to enter duplicate Lockbox.invoiceSequenceNumber, which will fail, and should never happen.");
        }
        businessObjectService.save(newLockbox);
    }
    
    public String createPaidOffCustomerInvoice() throws InterruptedException {
        CustomerCreditMemoDetailService customerCreditMemoDetailService = SpringContext.getBean(CustomerCreditMemoDetailService.class);
        CustomerCreditMemoDocument customerCreditMemoDocument;
//        CustomerCreditMemoDetail customerCreditMemoDetail = new CustomerCreditMemoDetail();
        CustomerInvoiceDocument customerInvoiceDocument;

        try {
            customerInvoiceDocument = (CustomerInvoiceDocument) documentService.getNewDocument(CustomerInvoiceDocument.class);
            LOG.info("\nCreated customer invoice document " + customerInvoiceDocument.getDocumentNumber());
        } catch (WorkflowException e) {
            throw new RuntimeException("Customer Invoice Document creation failed.");
        }

        customerInvoiceDocumentService.setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
        customerInvoiceDocument.getDocumentHeader().setDocumentDescription("TEST paid off CUSTOMER INVOICE DOCUMENT");
        customerInvoiceDocument.getAccountsReceivableDocumentHeader().setCustomerNumber("KAT17282");
        customerInvoiceDocument.setBillingDate(getDateTimeService().getCurrentSqlDate());

        CustomerAddress customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress("KAT17282");

        customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
        customerInvoiceDocument.setCustomerBillToAddressIdentifier(1);
        customerInvoiceDocument.setBillingAddressTypeCode("P");
        customerInvoiceDocument.setBillingAddressName(customerBillToAddress.getCustomerAddressName());
        customerInvoiceDocument.setBillingLine1StreetAddress(customerBillToAddress.getCustomerLine1StreetAddress());
        customerInvoiceDocument.setBillingLine2StreetAddress(customerBillToAddress.getCustomerLine2StreetAddress());
        customerInvoiceDocument.setBillingCityName(customerBillToAddress.getCustomerCityName());
        customerInvoiceDocument.setBillingStateCode(customerBillToAddress.getCustomerStateCode());
        customerInvoiceDocument.setBillingZipCode(customerBillToAddress.getCustomerZipCode());
        customerInvoiceDocument.setBillingCountryCode(customerBillToAddress.getCustomerCountryCode());
        customerInvoiceDocument.setBillingAddressInternationalProvinceName(customerBillToAddress.getCustomerAddressInternationalProvinceName());
        customerInvoiceDocument.setBillingInternationalMailCode(customerBillToAddress.getCustomerInternationalMailCode());
        customerInvoiceDocument.setBillingEmailAddress(customerBillToAddress.getCustomerEmailAddress());
        customerInvoiceDocument.addSourceAccountingLine(createCustomerInvoiceDetailForFunctionalTesting(customerInvoiceDocument, new KualiDecimal(1), new BigDecimal(100), "2336320", "BL", "ISRT" ));
// TODO abyrne - stop using DocumentTestUtils from production code
        return "";
//        customerCreditMemoDetail.setCreditMemoItemQuantity(new BigDecimal(100));
//        customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount();
//        try {
//            // the document header is created and set here
//            
//            customerCreditMemoDocument = (CustomerCreditMemoDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CustomerCreditMemoDocument.class);
//        }
//        catch (WorkflowException e) {
//            throw new RuntimeException("Document customerCreditMemoDocument creation failed.");
//        }
//        customerCreditMemoDocument.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDocument.getDocumentNumber());
//        customerCreditMemoDocument.setInvoice(customerInvoiceDocument);
//        customerCreditMemoDocument.populateCustomerCreditMemoDetails();

//        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();
//        for (CustomerCreditMemoDetail customerCreditMemoDetail : customerCreditMemoDetails) {
//            customerCreditMemoDetail.setCreditMemoItemQuantity(new BigDecimal(1));
//            customerCreditMemoDetail.setCreditMemoItemTotalAmount(new KualiDecimal(100));
//            customerCreditMemoDetail.setCreditMemoLineTotalAmount(new KualiDecimal(100));
//        }
//
//        try {
//            documentService.blanketApproveDocument(customerInvoiceDocument, null, null);
//            Thread.sleep(5000);
//            documentService.blanketApproveDocument(customerCreditMemoDocument, null, null);
//            LOG.info("Created customer credit memo " + customerCreditMemoDocument.getDocumentNumber());
//        } catch (WorkflowException e) {
//            throw new RuntimeException("Customer Invoice Document routing failed.");
//        }
//        LOG.info("Created customer credit memo " + customerCreditMemoDocument.getDocumentNumber());
//
//       return customerInvoiceDocument.getDocumentNumber();
    }


    public void createCustomerInvoiceDocumentForFunctionalTesting(String customerNumber, Date billingDate, int numinvoicedetails,  KualiDecimal nonrandomquantity, BigDecimal nonrandomunitprice, String accountnumber, String chartcode, String invoiceitemcode) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        
        CustomerInvoiceDocument customerInvoiceDocument;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument)documentService.getNewDocument(CustomerInvoiceDocument.class);
            LOG.info("Created customer invoice document " + customerInvoiceDocument.getDocumentNumber());
        } catch (WorkflowException e) {
            throw new RuntimeException("Customer Invoice Document creation failed.");
        }
        
        customerInvoiceDocumentService.setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
        //customerInvoiceDocument.getDocumentHeader().setDocumentDescription(customerNumber+" - TEST CUSTOMER INVOICE DOCUMENT");// - BILLING DATE - "+sdf.format(billingDate));
        customerInvoiceDocument.getDocumentHeader().setDocumentDescription("TEST CUSTOMER INVOICE DOCUMENT");
        customerInvoiceDocument.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);
        customerInvoiceDocument.setBillingDate(new java.sql.Date(billingDate.getTime()));

        CustomerAddress customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress(customerNumber);
//        CustomerAddress customerShipToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress(customerNumber);

        customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
        customerInvoiceDocument.setCustomerBillToAddressIdentifier(1);
        customerInvoiceDocument.setBillingAddressTypeCode("P");
        customerInvoiceDocument.setBillingAddressName(customerBillToAddress.getCustomerAddressName());
        customerInvoiceDocument.setBillingLine1StreetAddress(customerBillToAddress.getCustomerLine1StreetAddress());
        customerInvoiceDocument.setBillingLine2StreetAddress(customerBillToAddress.getCustomerLine2StreetAddress());
        customerInvoiceDocument.setBillingCityName(customerBillToAddress.getCustomerCityName());
        customerInvoiceDocument.setBillingStateCode(customerBillToAddress.getCustomerStateCode());
        customerInvoiceDocument.setBillingZipCode(customerBillToAddress.getCustomerZipCode());
        customerInvoiceDocument.setBillingCountryCode(customerBillToAddress.getCustomerCountryCode());
        customerInvoiceDocument.setBillingAddressInternationalProvinceName(customerBillToAddress.getCustomerAddressInternationalProvinceName());
        customerInvoiceDocument.setBillingInternationalMailCode(customerBillToAddress.getCustomerInternationalMailCode());
        customerInvoiceDocument.setBillingEmailAddress(customerBillToAddress.getCustomerEmailAddress());

//        customerInvoiceDocument.setCustomerShipToAddress(customerShipToAddress);
//        customerInvoiceDocument.setShippingAddressTypeCode("P");
//        customerInvoiceDocument.setShippingAddressName(customerShipToAddress.getCustomerAddressName());
//        customerInvoiceDocument.setShippingLine1StreetAddress(customerShipToAddress.getCustomerLine1StreetAddress());
//        customerInvoiceDocument.setShippingLine2StreetAddress(customerShipToAddress.getCustomerLine2StreetAddress());
//        customerInvoiceDocument.setShippingCityName(customerShipToAddress.getCustomerCityName());
//        customerInvoiceDocument.setShippingStateCode(customerShipToAddress.getCustomerStateCode());
//        customerInvoiceDocument.setShippingZipCode(customerShipToAddress.getCustomerZipCode());
//        customerInvoiceDocument.setShippingCountryCode(customerShipToAddress.getCustomerCountryCode());
//        customerInvoiceDocument.setShippingAddressInternationalProvinceName(customerShipToAddress.getCustomerAddressInternationalProvinceName());
//        customerInvoiceDocument.setShippingInternationalMailCode(customerShipToAddress.getCustomerInternationalMailCode());
//        customerInvoiceDocument.setShippingEmailAddress(customerShipToAddress.getCustomerEmailAddress());


        if (ObjectUtils.isNotNull(nonrandomquantity)&&ObjectUtils.isNotNull(nonrandomunitprice)&&numinvoicedetails>=1) {
            for (int i = 0; i < numinvoicedetails; i++) { 
                customerInvoiceDocument.addSourceAccountingLine(createCustomerInvoiceDetailForFunctionalTesting(customerInvoiceDocument, nonrandomquantity, nonrandomunitprice, accountnumber, chartcode, invoiceitemcode));
            }  
        } else {       
            int randomnuminvoicedetails = (int) (Math.random()*9); // add up to 9
            if (randomnuminvoicedetails==0) randomnuminvoicedetails=1; // add at least one
            for (int i = 0; i < randomnuminvoicedetails; i++) { 
                customerInvoiceDocument.addSourceAccountingLine(createCustomerInvoiceDetailForFunctionalTesting(customerInvoiceDocument, null, null, accountnumber, chartcode, invoiceitemcode));
            }              
        }
        try {
            documentService.blanketApproveDocument(customerInvoiceDocument, null, null);
            createdInvoices.add(customerInvoiceDocument.getDocumentNumber());
            LOG.info("Submitted customer invoice document " + customerInvoiceDocument.getDocumentNumber()+" for "+customerNumber+" - "+sdf.format(billingDate)+"\n\n");
        } catch (WorkflowException e){
            throw new RuntimeException("Customer Invoice Document routing failed.");
        }
    }
    
    public CustomerInvoiceDetail createCustomerInvoiceDetailForFunctionalTesting(CustomerInvoiceDocument customerInvoiceDocument, KualiDecimal nonrandomquantity, BigDecimal nonrandomunitprice, String accountnumber, String chartcode, String invoiceitemcode){
        
        KualiDecimal quantity;
        BigDecimal unitprice;
        
        if (ObjectUtils.isNull(nonrandomquantity)) {
            quantity = new KualiDecimal(100*Math.random()); // random number 0 to 100 total items      // TODO FIXME  <-- InvoiceItemQuantities of more than 2 decimal places cause rule errors; BigDecimal values such as 5.3333333333 should be valid InvoiceItemQuantities
        } else {
            quantity = nonrandomquantity;
        }
        if (ObjectUtils.isNull(nonrandomunitprice)) {    
        unitprice = new BigDecimal(1); // 0.00 to 100.00 dollars per item
        } else {
            unitprice = nonrandomunitprice;
        }                
        
        KualiDecimal amount = quantity.multiply(new KualiDecimal(unitprice)); // setAmount has to be set explicitly below; so we calculate it here
        //LOG.info("\n\n\n\n\t\t\t\t quantity="+quantity.toString()+"\t\t\t\tprice="+unitprice.toString()+"\t\t\t\tamount="+amount.toString()+"\t\t\t\t"+customerInvoiceDocument.getCustomerName());
        
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        customerInvoiceDetail.setChartOfAccountsCode(chartcode);
        customerInvoiceDetail.setAccountNumber(accountnumber);    //   other BL account numbers:   2231401   2324601
        customerInvoiceDetail.setFinancialObjectCode("1800");
        customerInvoiceDetail.setAccountsReceivableObjectCode("8118");
        customerInvoiceDetail.setInvoiceItemCode(invoiceitemcode);
        customerInvoiceDetail.setInvoiceItemServiceDate(dateTimeService.getCurrentSqlDate());
        customerInvoiceDetail.setInvoiceItemUnitPrice(unitprice);
        customerInvoiceDetail.setInvoiceItemQuantity(quantity.bigDecimalValue());
        customerInvoiceDetail.setInvoiceItemTaxAmount(new KualiDecimal(100));
        customerInvoiceDetail.setTaxableIndicator(true);
        customerInvoiceDetail.setAmount(amount);
        customerInvoiceDetail.setPostingYear(currentYear);


        return customerInvoiceDetail;
    }  
    
    
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
    
    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }


    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }    

}

