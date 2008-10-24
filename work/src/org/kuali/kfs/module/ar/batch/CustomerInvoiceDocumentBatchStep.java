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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.collections.list.AbstractLinkedList;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceStep;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.bo.user.UniversalUser;

import com.sun.xml.internal.fastinfoset.util.StringArray;

public class CustomerInvoiceDocumentBatchStep extends AbstractStep {
    
    CustomerInvoiceDocumentService customerInvoiceDocumentService; 
    BusinessObjectService businessObjectService;
    DocumentService documentService;
    DateTimeService dateTimeService;
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentBatchStep.class);

    // parameter constants and logging
    private static final int NUMBER_OF_INVOICES_TO_CREATE = 5;
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_CODE = "KFS-AR";
    private static final String RUN_INDICATOR_PARAMETER_NAMESPACE_STEP = "CustomerInvoiceDocumentBatchStep";
// ******************* replaced while testing   private static final String RUN_INDICATOR_PARAMETER_VALUE = "N";
    private static final String RUN_INDICATOR_PARAMETER_VALUE = "Y"; // doesn't seem to matter that i changed it - it still skips this value
    private static final String RUN_INDICATOR_PARAMETER_ALLOWED = "A";
    private static final String RUN_INDICATOR_PARAMETER_DESCRIPTION = "Tells the job framework whether to run this job or not; set to NO because the GenesisBatchJob needs to only be run once after database initialization.";
    private static final String RUN_INDICATOR_PARAMETER_TYPE = "CONFG";
    private static final String RUN_INDICATOR_PARAMETER_WORKGROUP = "FP_OPERATIONS";

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        
        GlobalVariables.clear();
        try {
            GlobalVariables.setUserSession(new UserSession("KHUNTLEY"));
        }
        catch (WorkflowException wfex) {
        }
        catch (UserNotFoundException nfex) {
        } 
        
        Date billingDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        List<String> customernames;
        
        if ((jobName.length() <=8 ) && (jobName.length() >= 4)) {
            setCustomerInvoiceDocumentService(SpringContext.getBean(CustomerInvoiceDocumentService.class)); 
            setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
            setDocumentService(SpringContext.getBean(DocumentService.class));
            setDateTimeService(SpringContext.getBean(DateTimeService.class));
            
            customernames = Arrays.asList(jobName);
        }
        else {
            customernames = Arrays.asList("ABB2","3MC17500","ACE21725","ANT7297","CAR23612", "CON19567", "DEL14448", "EAT17609", "GAP17272"); 
        }
        
        for (String customername : customernames) {
            
            billingDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();
            
            for( int i = 0; i < NUMBER_OF_INVOICES_TO_CREATE; i++ ){    
                  
                billingDate = DateUtils.addDays(billingDate, -30);
                  
                createCustomerInvoiceDocumentForFunctionalTesting(customername,billingDate);
                Thread.sleep(5000);
    
            }
        }
        setInitiatedParameter();
        return true;
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
           runIndicatorParameter.setParameterWorkgroupName(CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_WORKGROUP);
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
       fieldNamesValuesForParameter.put("parameterWorkgroupName",CustomerInvoiceDocumentBatchStep.RUN_INDICATOR_PARAMETER_WORKGROUP);

       // get the primary keys and assign them to values
       List<String> parameterPKFields = psService.getPrimaryKeys(Parameter.class);
       for (String pkFieldName: parameterPKFields)
       {
           pkMapForParameter.put(pkFieldName,fieldNamesValuesForParameter.get(pkFieldName));
       }
       return (pkMapForParameter);
    }
    
    public void createCustomerInvoiceDocumentForFunctionalTesting(String customerNumber, Date billingDate) {
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
        
        int accountlines = (int) (Math.random()*9); // add up to 9
        if (accountlines==0) accountlines=1; // add at least one
        for (int i = 0; i < accountlines; i++) { 
            customerInvoiceDocument.addSourceAccountingLine(createCustomerInvoiceDetailForFunctionalTesting(customerInvoiceDocument));
        }              
        
        try {
            documentService.blanketApproveDocument(customerInvoiceDocument, null, null);
            LOG.info("Submitted customer invoice document " + customerInvoiceDocument.getDocumentNumber()+" for "+customerNumber+" - "+sdf.format(billingDate)+"\n\n");
        } catch (WorkflowException e){
            throw new RuntimeException("Customer Invoice Document routing failed.");
        }
    }
    
    protected CustomerInvoiceDetail createCustomerInvoiceDetailForFunctionalTesting(CustomerInvoiceDocument customerInvoiceDocument){
        KualiDecimal quantity = new KualiDecimal(100*Math.random()); // random number 0 to 100 total items      // TODO FIXME  <-- InvoiceItemQuantities of more than 2 decimal places cause rule errors; BigDecimal values such as 5.3333333333 should be valid InvoiceItemQuantities
        BigDecimal unitprice = new BigDecimal(1); // 0.00 to 100.00 dollars per item
        KualiDecimal amount = quantity.multiply(new KualiDecimal(unitprice)); // setAmount has to be set explicitly below; so we calculate it here
        LOG.info("\n\n\n\n\t\t\t\t randomquantity="+quantity.toString()+"\t\t\t\tunitprice="+unitprice.toString()+"\t\t\t\tamount="+amount.toString()+"\t\t\t\t"+customerInvoiceDocument.getCustomerName());
        
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        customerInvoiceDetail.setChartOfAccountsCode("BL");
        customerInvoiceDetail.setAccountNumber("1031400");
        customerInvoiceDetail.setFinancialObjectCode("1500");
        customerInvoiceDetail.setAccountsReceivableObjectCode("8118");
        customerInvoiceDetail.setInvoiceItemServiceDate(dateTimeService.getCurrentSqlDate());
        customerInvoiceDetail.setInvoiceItemUnitPrice(unitprice);
        customerInvoiceDetail.setInvoiceItemQuantity(quantity.bigDecimalValue());
        customerInvoiceDetail.setInvoiceItemTaxAmount(new KualiDecimal(100));
        customerInvoiceDetail.setTaxableIndicator(true);
        customerInvoiceDetail.setAmount(amount);
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
