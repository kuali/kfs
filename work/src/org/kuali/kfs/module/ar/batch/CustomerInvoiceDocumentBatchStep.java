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
package org.kuali.kfs.module.ar.batch;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.LockboxService;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.Lockbox;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.TestingStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.api.parameter.EvaluationOperator;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterType;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDocumentBatchStep extends AbstractStep implements TestingStep {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentBatchStep.class);

    protected static final long MAX_SEQ_NBR_OFFSET = 1000;

    protected CustomerInvoiceDocumentService customerInvoiceDocumentService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected DateTimeService dateTimeService;
    protected Collection<String> createdInvoices = new ArrayList<String>();



    // parameter constants and logging
    protected static final int NUMBER_OF_INVOICES_TO_CREATE = 5;
    protected static final String RUN_INDICATOR_PARAMETER_NAMESPACE_CODE = ArConstants.AR_NAMESPACE_CODE;
    protected static final String RUN_INDICATOR_PARAMETER_APPLICATION_NAMESPACE_CODE = KFSConstants.APPLICATION_NAMESPACE_CODE;
    protected static final String RUN_INDICATOR_PARAMETER_NAMESPACE_STEP = CustomerInvoiceDocumentBatchStep.class.getSimpleName();
    protected static final String RUN_INDICATOR_PARAMETER_VALUE = "N"; // Tells the job framework whether to run this job or not; set to N because the CustomerInvoiceDocumentBatchStep needs to only be run once after database initialization.
    protected static final String RUN_INDICATOR_PARAMETER_ALLOWED = "A";
    protected static final String RUN_INDICATOR_PARAMETER_DESCRIPTION = "Tells the job framework whether to run this job or not; because the CustomerInvoiceDocumentBatchStep needs to only be run once after database initialization.";
    protected static final String RUN_INDICATOR_PARAMETER_TYPE = "CONFG";
    protected static final String INITIATOR_PRINCIPAL_NAME = "khuntley";

    protected final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {


        Parameter runIndicatorParameter = getParameterService().getParameter(RUN_INDICATOR_PARAMETER_NAMESPACE_CODE, RUN_INDICATOR_PARAMETER_NAMESPACE_STEP, Job.STEP_RUN_PARM_NM);
        if (runIndicatorParameter == null || StringUtils.equals("Y", runIndicatorParameter.getValue())) {

            GlobalVariables.clear();
            GlobalVariables.setUserSession(new UserSession(INITIATOR_PRINCIPAL_NAME));
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
//            if (customernames.size() > 1) {
//                for (String customername : customernames) {
//
//                    billingDate = getDateTimeService().getCurrentDate();
//
//                    for( int i = 0; i < NUMBER_OF_INVOICES_TO_CREATE; i++ ){
//
//                        billingDate = KfsDateUtils.addDays(billingDate, -30);
//
//                        createCustomerInvoiceDocumentForFunctionalTesting(customername,billingDate, 0, null, null, "1031400", "BL");
//                        Thread.sleep(500);
//
//                    }
//                }
//            }



            // save runParameter as "N" so that the job won't run until DB has been cleared
            setInitiatedParameter();
        }
        return true;
    }

    private long findAvailableLockboxBaseSeqNbr() {
        LockboxService lockboxService = SpringContext.getBean(LockboxService.class);
        return lockboxService.getMaxLockboxSequenceNumber() + MAX_SEQ_NBR_OFFSET;
    }

    private boolean dupLockboxRecordExists(Long seqNbr) {
        Map<String,Long> pks = new HashMap<String,Long>();
        pks.put("invoiceSequenceNumber", seqNbr);
        Lockbox dupLockBox = businessObjectService.findByPrimaryKey(Lockbox.class, pks);
        return (dupLockBox != null);
    }

    /**
     * This method sets a parameter that tells the step that it has already run and it does not need to run again.
     */
    private void setInitiatedParameter() {
        // first see if we can find an existing Parameter object with this key
        Parameter runIndicatorParameter = getParameterService().getParameter(RUN_INDICATOR_PARAMETER_NAMESPACE_CODE, RUN_INDICATOR_PARAMETER_NAMESPACE_STEP, Job.STEP_RUN_PARM_NM);
        if (runIndicatorParameter == null) {
            Parameter.Builder newParm = Parameter.Builder.create(RUN_INDICATOR_PARAMETER_APPLICATION_NAMESPACE_CODE, RUN_INDICATOR_PARAMETER_NAMESPACE_CODE, RUN_INDICATOR_PARAMETER_NAMESPACE_STEP, Job.STEP_RUN_PARM_NM, ParameterType.Builder.create(RUN_INDICATOR_PARAMETER_TYPE));
            newParm.setEvaluationOperator(EvaluationOperator.ALLOW);
            newParm.setDescription(RUN_INDICATOR_PARAMETER_DESCRIPTION);
            newParm.setValue("N");
            getParameterService().createParameter(newParm.build());
        }
        else {
            Parameter.Builder newParm = Parameter.Builder.create(runIndicatorParameter);
            newParm.setValue("N");
            getParameterService().updateParameter(newParm.build());
        }
    }

    private Lockbox populateLockbox(String invoiceNumber, Long seqNbr) {

        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(invoiceNumber);

        Lockbox newLockbox = new Lockbox();
        newLockbox.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
        newLockbox.setCustomerNumber(customerInvoiceDocument.getCustomer().getCustomerNumber());
        newLockbox.setInvoiceTotalAmount(customerInvoiceDocument.getTotalDollarAmount());
        newLockbox.setInvoicePaidOrAppliedAmount(customerInvoiceDocument.getOpenAmount());
        newLockbox.setBillingDate(customerInvoiceDocument.getBillingDate());
        newLockbox.setCustomerPaymentMediumCode("CK");
        newLockbox.setBankCode("1003");
        newLockbox.setBatchSequenceNumber(8004);
        newLockbox.setInvoiceSequenceNumber(seqNbr);
        newLockbox.setLockboxNumber("66249");

        return newLockbox;
    }

    private void createLockboxesForFunctionalTesting(String invoiceNumber, Long seqNbr, int testtype) throws InterruptedException {

        Lockbox newLockbox = populateLockbox(invoiceNumber, seqNbr);

        // 1) Payment matches customer (CUST_NBR), invoice number (FDOC_REF_INV_NBR), and amount (AR_INV_PD_APLD_AMT). These should auto-approve, the remaining scenarios should not.
        if (testtype == 1) {
            // dont need to do anything, its auto-set to pay the OpenAmount on the invoice
        }

        // 2) Payment matches customer and invoice, but the invoice has no outstanding balance (due to a previous payment, a credit memo, or a write-off)
        if (testtype == 2) {
            newLockbox.setInvoicePaidOrAppliedAmount(newLockbox.getInvoiceTotalAmount());
            writeoffInvoice(invoiceNumber);
        }

        // 3) Payment matches customer and invoice, but the amount of the payment exceeds the outstanding balance on the invoice.
        if (testtype == 3) {
            newLockbox.setInvoicePaidOrAppliedAmount(newLockbox.getInvoicePaidOrAppliedAmount().add(new KualiDecimal("100.00")));
        }

        // 4) The payment matches customer and invoice, but the amount is short-paid (less than the invoice outstanding balance)
        if (testtype == 4) {
            newLockbox.setInvoicePaidOrAppliedAmount(newLockbox.getInvoicePaidOrAppliedAmount().subtract(new KualiDecimal("1.00")));
        }

        // 5) The payment matches a customer number, but the invoice number is missing
        if (testtype == 5) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber(null);
        }

        // 6) The payment matches a customer number, but the invoice number is invalid
        if (testtype == 6) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber("999999");
        }

        // 7) The payment matches nothing (not even the customer number)
        if (testtype == 7) {
            newLockbox.setFinancialDocumentReferenceInvoiceNumber("999999");
            newLockbox.setCustomerNumber("KEY17536");
        }

        LOG.info("Creating customer LOCKBOX [" + seqNbr.toString() + "] for invoice " + invoiceNumber);
        if (dupLockboxRecordExists(seqNbr)) {
            throw new RuntimeException("Trying to enter duplicate Lockbox.invoiceSequenceNumber, which will fail, and should never happen.");
        }
        businessObjectService.save(newLockbox);
    }

    public void writeoffInvoice(String invoiceNumberToWriteOff) {
        CustomerInvoiceWriteoffDocumentService writeoffService = SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class);

        //  have the service create us a new writeoff doc
        String writeoffDocNumber;
        try {
            writeoffDocNumber = writeoffService.createCustomerInvoiceWriteoffDocument(invoiceNumberToWriteOff,
                    "Created by CustomerInvoiceDocumentBatch process.");
        }
        catch (WorkflowException e) {
            throw new RuntimeException("A WorkflowException was thrown when trying to create a new Invoice Writeoff document.", e);
        }

        //  load the newly created writeoff doc from the db
        CustomerInvoiceWriteoffDocument writeoff;
        try {
            writeoff = (CustomerInvoiceWriteoffDocument) documentService.getByDocumentHeaderId(writeoffDocNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("A WorkflowException was thrown when trying to load Invoice Writeoff doc #" + writeoffDocNumber + ".", e);
        }

        boolean wentToFinalInd = false;
        try {
            wentToFinalInd = waitForStatusChange(60, writeoff.getDocumentHeader().getWorkflowDocument(), new String[] {"F", "P"});
        }
        catch (Exception e) {
            throw new RuntimeException("An Exception was thrown when trying to monitor writeoff doc #" + writeoffDocNumber +" going to FINAL.", e);
        }

        //  if the doc didnt go to final, then blanket approve the doc, bypassing all rules
        if (!wentToFinalInd) {
            try {
                if (writeoff.getDocumentHeader().getWorkflowDocument().isFinal()) {

                }
                writeoff.getDocumentHeader().getWorkflowDocument().blanketApprove("BlanketApproved by CustomerInvoiceDocumentBatch process.");
            }
            catch (Exception e) {
                throw new RuntimeException("A WorkflowException was thrown when trying to blanketApprove Invoice Writeoff doc #" + writeoffDocNumber + ".", e);
            }

            //  wait for it to go to final
            wentToFinalInd = false;
            try {
                wentToFinalInd = waitForStatusChange(60, writeoff.getDocumentHeader().getWorkflowDocument(), new String[] {"F", "P"});
            }
            catch (Exception e) {
                throw new RuntimeException("An Exception was thrown when trying to monitor writeoff doc #" + writeoffDocNumber +" going to FINAL.", e);
            }
        }

        if (!wentToFinalInd) {
            throw new RuntimeException("InvoiceWriteoff document #" + writeoffDocNumber + " failed to route to FINAL.");
        }
    }

    public boolean waitForStatusChange(int numSeconds, WorkflowDocument document, String[] desiredStatus) throws Exception {
        DocWorkflowStatusMonitor monitor = new DocWorkflowStatusMonitor(SpringContext.getBean(DocumentService.class), "" + document.getDocumentId(), desiredStatus);
        return waitUntilChange(monitor, numSeconds, 5);
    }

    /**
     * Iterates, with pauseSeconds seconds between iterations, until either the given ChangeMonitor's valueChanged method returns
     * true, or at least maxWaitSeconds seconds have passed.
     *
     * @param monitor ChangeMonitor instance which watches for whatever change your test is waiting for
     * @param maxWaitSeconds
     * @param pauseSeconds
     * @return true if the the ChangeMonitor's valueChanged method returned true before time ran out
     */
    public boolean waitUntilChange(DocWorkflowStatusMonitor monitor, int maxWaitSeconds, int pauseSeconds) throws Exception {
        long maxWaitMs = maxWaitSeconds * 1000;
        long pauseMs = pauseSeconds * 1000;

        boolean valueChangedInd = false;
        boolean interruptedInd = false;
        long startTimeMs = System.currentTimeMillis();
        long endTimeMs = startTimeMs + maxWaitMs;

        Thread.sleep(pauseMs / 10); // the first time through, sleep a fraction of the specified time
        valueChangedInd = monitor.valueChanged();
        while (!interruptedInd && !valueChangedInd && (System.currentTimeMillis() < endTimeMs)) {
            try {
                Thread.sleep(pauseMs);
            }
            catch (InterruptedException e) {
                interruptedInd = true;
            }
            valueChangedInd = monitor.valueChanged();
        }
        return valueChangedInd;
    }

//    public void writeoffInvoice(String invoiceToWriteOff) {
//        CustomerCreditMemoDetailService customerCreditMemoDetailService = SpringContext.getBean(CustomerCreditMemoDetailService.class);
//        CustomerCreditMemoDocument customerCreditMemoDocument;
//        CustomerCreditMemoDetail customerCreditMemoDetail = new CustomerCreditMemoDetail();
//        CustomerInvoiceDocument customerInvoiceDocument;
//
//        try {
//            customerInvoiceDocument = (CustomerInvoiceDocument) documentService.getNewDocument(CustomerInvoiceDocument.class);
//            LOG.info("\nCreated customer invoice document " + customerInvoiceDocument.getDocumentNumber());
//        } catch (WorkflowException e) {
//            throw new RuntimeException("Customer Invoice Document creation failed.");
//        }
//
//        customerInvoiceDocumentService.setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
//        customerInvoiceDocument.getDocumentHeader().setDocumentDescription("TEST paid off CUSTOMER INVOICE DOCUMENT");
//        customerInvoiceDocument.getAccountsReceivableDocumentHeader().setCustomerNumber("KAT17282");
//        customerInvoiceDocument.setBillingDate(getDateTimeService().getCurrentSqlDate());
//
//        CustomerAddress customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress("KAT17282");
//
//        customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
//        customerInvoiceDocument.setCustomerBillToAddressIdentifier(1);
//        customerInvoiceDocument.setBillingAddressTypeCode("P");
//        customerInvoiceDocument.setBillingAddressName(customerBillToAddress.getCustomerAddressName());
//        customerInvoiceDocument.setBillingLine1StreetAddress(customerBillToAddress.getCustomerLine1StreetAddress());
//        customerInvoiceDocument.setBillingLine2StreetAddress(customerBillToAddress.getCustomerLine2StreetAddress());
//        customerInvoiceDocument.setBillingCityName(customerBillToAddress.getCustomerCityName());
//        customerInvoiceDocument.setBillingStateCode(customerBillToAddress.getCustomerStateCode());
//        customerInvoiceDocument.setBillingZipCode(customerBillToAddress.getCustomerZipCode());
//        customerInvoiceDocument.setBillingCountryCode(customerBillToAddress.getCustomerCountryCode());
//        customerInvoiceDocument.setBillingAddressInternationalProvinceName(customerBillToAddress.getCustomerAddressInternationalProvinceName());
//        customerInvoiceDocument.setBillingInternationalMailCode(customerBillToAddress.getCustomerInternationalMailCode());
//        customerInvoiceDocument.setBillingEmailAddress(customerBillToAddress.getCustomerEmailAddress());
//        customerInvoiceDocument.addSourceAccountingLine(createCustomerInvoiceDetailForFunctionalTesting(customerInvoiceDocument, new KualiDecimal(1), new BigDecimal(100), "2336320", "BL", "ISRT" ));
//
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
//
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
//    }


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
            if (randomnuminvoicedetails==0)
             {
                randomnuminvoicedetails=1; // add at least one
            }
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


    @Override
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }


    @Override
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

    private class DocWorkflowStatusMonitor {
        final DocumentService documentService;
        final private String docHeaderId;
        final private String[] desiredWorkflowStates;

        public DocWorkflowStatusMonitor(DocumentService documentService, String docHeaderId, String desiredWorkflowStatus) {
            this.documentService = documentService;
            this.docHeaderId = docHeaderId;
            this.desiredWorkflowStates = new String[] { desiredWorkflowStatus };
        }

        public DocWorkflowStatusMonitor(DocumentService documentService, String docHeaderId, String[] desiredWorkflowStates) {
            this.documentService = documentService;
            this.docHeaderId = docHeaderId;
            this.desiredWorkflowStates = desiredWorkflowStates;
        }

        public boolean valueChanged() throws Exception {
            Document d = documentService.getByDocumentHeaderId(docHeaderId.toString());

            String currentStatus = d.getDocumentHeader().getWorkflowDocument().getStatus().getCode();

            for (int i = 0; i < desiredWorkflowStates.length; i++) {
                if (StringUtils.equals(desiredWorkflowStates[i], currentStatus)) {
                    return true;
                }
            }
            return false;
        }
    }
}

