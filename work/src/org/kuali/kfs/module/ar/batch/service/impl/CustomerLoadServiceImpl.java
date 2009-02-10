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

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.CustomerLoadStep;
import org.kuali.kfs.module.ar.batch.report.CustomerLoadBatchErrors;
import org.kuali.kfs.module.ar.batch.report.CustomerLoadFileResult;
import org.kuali.kfs.module.ar.batch.report.CustomerLoadResult;
import org.kuali.kfs.module.ar.batch.report.CustomerLoadResult.ResultCode;
import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterAdapter;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class CustomerLoadServiceImpl implements CustomerLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadServiceImpl.class);

    private static final String MAX_RECORDS_PARM_NAME = "MAX_NUMBER_OF_RECORDS_PER_DOCUMENT";
    private static final String NA = "-- N/A --";
    private static final String WORKFLOW_DOC_ID_PREFIX = " - WITH WORKFLOW DOCID: ";
    
    private BatchInputFileService batchInputFileService;
    private CustomerService customerService;
    private KualiConfigurationService configService;
    private DocumentService docService;
    private ParameterService parameterService;
    private OrganizationService orgService;
    private SystemInformationService sysInfoService;
    private BusinessObjectService boService;
    private DateTimeService dateTimeService;
    
    private BatchInputFileType batchInputFileType;
    private CustomerDigesterAdapter adapter;
    private String reportsDirectory;
    
    public CustomerLoadServiceImpl() {
    }
    
    public boolean loadFiles() {
        
        LOG.info("Beginning processing of all available files for AR Customer Batch Upload.");
        
        boolean result = true;
        List<CustomerLoadFileResult> fileResults = new ArrayList<CustomerLoadFileResult>();
        CustomerLoadFileResult reporter = null;
        
        //  create a list of the files to process
        List<String> fileNamesToLoad = getListOfFilesToProcess();
        LOG.info("Found " + fileNamesToLoad.size() + " file(s) to process.");
        
        //  process each file in turn
        List<String> processedFiles = new ArrayList<String>();
        for (String inputFileName : fileNamesToLoad) {
            
            LOG.info("Beginning processing of filename: " + inputFileName + ".");
            
            //  setup the results reporting
            reporter = new CustomerLoadFileResult(inputFileName);
            fileResults.add(reporter);
            
            if (loadFile(inputFileName, reporter)) {
                result &= true;
                reporter.addFileInfoMessage("File successfully completed processing.");
                processedFiles.add(inputFileName);
            }
            else {
                reporter.addFileErrorMessage("File failed to process successfully.");
                result &= false;
            }
        }

        //  remove done files
        removeDoneFiles(processedFiles);
        
        //  write report PDF
        writeReportPDF(fileResults);
        
        return result;
    }
    
    private List<String> getListOfFilesToProcess() {
        
        //  create a list of the files to process
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(batchInputFileType);
        
        if (fileNamesToLoad == null) {
            LOG.error("BatchInputFileService.listInputFileNamesWithDoneFile(" + 
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
            throw new RuntimeException("BatchInputFileService.listInputFileNamesWithDoneFile(" + 
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
        }
        
        //  filenames returned should never be blank/empty/null
        for (String inputFileName : fileNamesToLoad) {
            if (StringUtils.isBlank(inputFileName)) {
                LOG.error("One of the file names returned as ready to process [" + inputFileName + 
                        "] was blank.  This should not happen, so throwing an error to investigate.");
                throw new RuntimeException("One of the file names returned as ready to process [" + inputFileName + 
                        "] was blank.  This should not happen, so throwing an error to investigate.");
            }
        }
        
        return fileNamesToLoad;
    }
    
    /**
     * Clears out associated .done files for the processed data files.
     */
    private void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            File doneFile = new File(StringUtils.substringBeforeLast(dataFileName, ".") + ".done");
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.batch.service.CustomerLoadService#loadFile(java.lang.String)
     */
    public boolean loadFile(String fileName) {
        return loadFile(fileName, new CustomerLoadFileResult(fileName));
    }
    
    public boolean loadFile(String fileName, CustomerLoadFileResult reporter) {
        
        boolean result = true;
        
        //TODO move up to the loadFiles() method
        List<String> routedDocumentNumbers = new ArrayList<String>();
        List<String> failedDocumentNumbers = new ArrayList<String>();
        
        //  load up the file into a byte array 
        byte[] fileByteContent = safelyLoadFileBytes(fileName);

        //  parse the file against the XSD schema and load it into an object
        LOG.info("Attempting to parse the file using Apache Digester.");
        Object parsedObject = null;
        try {
            parsedObject = batchInputFileService.parse(batchInputFileType, fileByteContent);
        }
        catch (XMLParseException e) {
            LOG.error("Error parsing batch file: " + e.getMessage());
            reporter.addFileErrorMessage("Error parsing batch file: " + e.getMessage());
            throw new XMLParseException(e.getMessage());
        }
        
        //  make sure we got the type we expected, then cast it
        if (!(parsedObject instanceof List)) {
            LOG.error("Parsed file was not of the expected type.  Expected [" + List.class + "] but got [" + parsedObject.getClass() + "].");
            reporter.addFileErrorMessage("Parsed file was not of the expected type.  Expected [" + List.class + "] but got [" + parsedObject.getClass() + "].");
            throw new RuntimeException("Parsed file was not of the expected type.  Expected [" + List.class + "] but got [" + parsedObject.getClass() + "].");
        }
        
        //  prepare a list for the regular validate() method
        List<CustomerDigesterVO> customerVOs = (List<CustomerDigesterVO>) parsedObject;
        
        List<MaintenanceDocument> readyTransientDocs = new ArrayList<MaintenanceDocument>();
        LOG.info("Beginning validation and preparation of batch file.");
        result = validateAndPrepare(customerVOs, readyTransientDocs, reporter, false);
        
        //  send the readyDocs into workflow
        result &= sendDocumentsIntoWorkflow(readyTransientDocs, routedDocumentNumbers, failedDocumentNumbers, reporter);
        
        return result;
    }

    private boolean sendDocumentsIntoWorkflow(List<MaintenanceDocument> readyTransientDocs, List<String> routedDocumentNumbers, 
            List<String> failedDocumentNumbers, CustomerLoadFileResult reporter) {
        boolean result = true;
        for (MaintenanceDocument readyTransientDoc : readyTransientDocs) {
            result &= sendDocumentIntoWorkflow(readyTransientDoc, routedDocumentNumbers, failedDocumentNumbers, reporter);
        }
        return result;
    }
    
    private boolean sendDocumentIntoWorkflow(MaintenanceDocument readyTransientDoc, List<String> routedDocumentNumbers, 
            List<String> failedDocumentNumbers, CustomerLoadFileResult reporter) {
        boolean result = true;
        
        String customerName = ((Customer) readyTransientDoc.getNewMaintainableObject().getBusinessObject()).getCustomerName();
        
        //  create a real workflow document
        MaintenanceDocument realMaintDoc;
        try {
            realMaintDoc = (MaintenanceDocument) docService.getNewDocument(getCustomerMaintenanceDocumentTypeName());
        }
        catch (WorkflowException e) {
            LOG.error("WorkflowException occurred while trying to create a new MaintenanceDocument.", e);
            throw new RuntimeException("WorkflowException occurred while trying to create a new MaintenanceDocument.", e);
        }
        
        realMaintDoc.getNewMaintainableObject().setBusinessObject(readyTransientDoc.getNewMaintainableObject().getBusinessObject());
        realMaintDoc.getOldMaintainableObject().setBusinessObject(readyTransientDoc.getOldMaintainableObject().getBusinessObject());
        realMaintDoc.getNewMaintainableObject().setMaintenanceAction(readyTransientDoc.getNewMaintainableObject().getMaintenanceAction());
        realMaintDoc.getDocumentHeader().setDocumentDescription(readyTransientDoc.getDocumentHeader().getDocumentDescription());
        
        Customer customer = (Customer) realMaintDoc.getNewMaintainableObject().getBusinessObject();
        LOG.info("Routing Customer Maintenance document for [" + customer.getCustomerNumber() + "] " + customer.getCustomerName());
        
        try {
            docService.routeDocument(realMaintDoc, "Routed Edit/Update Customer Maintenance from CustomerLoad Batch Process", null);
        }
        catch (WorkflowException e) {
            LOG.error("WorkflowException occurred while trying to route a new MaintenanceDocument.", e);
            reporter.addCustomerErrorMessage(customerName, "WorkflowException occurred while trying to route a new MaintenanceDocument: " + e.getMessage());
            result = false;
        }
        
        if (result == true) {
            reporter.setCustomerSuccessResult(customerName);
            reporter.setCustomerWorkflowDocId(customerName, realMaintDoc.getDocumentNumber());
            routedDocumentNumbers.add(realMaintDoc.getDocumentNumber());
        }
        else {
            reporter.setCustomerFailureResult(customerName);
            failedDocumentNumbers.add(realMaintDoc.getDocumentNumber());
        }
        return result;
    }
    
    private String getCustomerMaintenanceDocumentTypeName() {
        return "CUS";
    }
    
    private void addError(CustomerLoadBatchErrors batchErrors, String customerName, String propertyName, Class<?> propertyClass, String origValue, String description) {
        batchErrors.addError(customerName, propertyName, propertyClass, origValue, description);
    }
    
    private void addBatchErrorsToGlobalVariables(CustomerLoadBatchErrors batchErrors) {
        Set<String> errorMessages = batchErrors.getErrorStrings();
        for (String errorMessage : errorMessages) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, 
                    KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, errorMessage);
        }
    }
    
    private void addBatchErrorstoCustomerLoadResult(CustomerLoadBatchErrors batchErrors, CustomerLoadResult result) {
        Set<String> errorMessages = batchErrors.getErrorStrings();
        for (String errorMessage : errorMessages) {
            result.addErrorMessage(errorMessage);
        }
    }
    
    /**
     * 
     * Accepts a file name and returns a byte-array of the file name contents, if possible.
     * 
     * Throws RuntimeExceptions if FileNotFound or IOExceptions occur.
     * 
     * @param fileName String containing valid path & filename (relative or absolute) of file to load.
     * @return A Byte Array of the contents of the file.
     */
    private byte[] safelyLoadFileBytes(String fileName) {
        
        InputStream fileContents;
        byte[] fileByteContent;
        try {
            fileContents = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e1) {
            LOG.error("Batch file not found [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("Batch File not found [" + fileName + "]. " + e1.getMessage());
        }
        try {
            fileByteContent = IOUtils.toByteArray(fileContents);
        }
        catch (IOException e1) {
            LOG.error("IO Exception loading: [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("IO Exception loading: [" + fileName + "]. " + e1.getMessage());
        }
        return fileByteContent;
    }
    
    /**
     * The results of this method follow the same rules as the batch step result rules:
     * 
     * The execution of this method may have 3 possible outcomes:
     * 
     * 1. returns true, meaning that everything has succeeded, and dependent steps can continue running. No 
     * errors should be added to GlobalVariables.getErrorMap().
     * 
     * 2. returns false, meaning that some (but not necessarily all) steps have succeeded, and dependent 
     * steps can continue running.  Details can be found in the GlobalVariables.getErrorMap().
     * 
     * 3. throws an exception, meaning that the step has failed, that the rest of the steps in a job should 
     * not be run, and that the job has failed.  There may be errors in the GlobalVariables.getErrorMap().
     * 
     * @see org.kuali.kfs.module.ar.batch.service.CustomerLoadService#validate(java.util.List)
     */
    public boolean validate(List<CustomerDigesterVO> customerUploads) {
        return validateAndPrepare(customerUploads, new ArrayList<MaintenanceDocument>(), true);
    }
    
    public boolean validateAndPrepare(List<CustomerDigesterVO> customerUploads, List<MaintenanceDocument> customerMaintDocs, boolean useGlobalErrorMap) {
        return validateAndPrepare(customerUploads, customerMaintDocs, new CustomerLoadFileResult(), useGlobalErrorMap);
    }
    
    private boolean validateAndPrepare(List<CustomerDigesterVO> customerUploads, List<MaintenanceDocument> customerMaintDocs, CustomerLoadFileResult reporter, boolean useGlobalErrorMap) {
        
        //  fail if empty or null list
        if (customerUploads == null) {
            LOG.error("Null list of Customer upload objects.  This should never happen.");
            throw new IllegalArgumentException("Null list of Customer upload objects.  This should never happen.");
        }
        if (customerUploads.isEmpty()) {
            reporter.addFileErrorMessage("An empty list of Customer uploads was passed in for validation.  As a result, no validation can be done.");
            if (useGlobalErrorMap) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, new String[] { "An empty list of Customer uploads was passed in for validation.  As a result, no validation was done." });
            }
            return false;
        }

        boolean groupSucceeded = true;
        boolean docSucceeded = true;
        
        //  check to make sure the input file doesnt have more docs than we allow in one batch file
        String maxRecordsString = parameterService.getParameterValue(CustomerLoadStep.class, MAX_RECORDS_PARM_NAME);
        if (StringUtils.isBlank(maxRecordsString) || !StringUtils.isNumeric(maxRecordsString)) {
            LOG.error("Expected 'Max Records Per Document' System Parameter is not available.");
            throw new RuntimeException("Expected 'Max Records Per Document' System Parameter is not available.");
        }
        Integer maxRecords = new Integer(maxRecordsString);
        if (customerUploads.size() > maxRecords.intValue()) {
            LOG.error("Too many records passed in for this file.  " + customerUploads.size() + " were passed in, and the limit is " + maxRecords + ".  As a result, no validation was done.");
            reporter.addFileErrorMessage("Too many records passed in for this file.  " + customerUploads.size() + " were passed in, and the limit is " + maxRecords + ".  As a result, no validation was done.");
            if (useGlobalErrorMap) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, new String[] { "Too many records passed in for this file.  " + customerUploads.size() + " were passed in, and the limit is " + maxRecords + ".  As a result, no validation was done." });
            }
            return false;
        }
        
        //  we have to create one real maint doc for the whole thing to pass the maintainable.checkAuthorizationRestrictions 
        MaintenanceDocument oneRealMaintDoc = null;
        
        Customer customer = null;
        CustomerLoadBatchErrors fileBatchErrors = new CustomerLoadBatchErrors();
        CustomerLoadBatchErrors customerBatchErrors;
        String customerName;
        if (adapter == null) adapter = new CustomerDigesterAdapter();
        for (CustomerDigesterVO customerDigesterVO : customerUploads) {
            
            docSucceeded = true;
            customerName = customerDigesterVO.getCustomerName();
            
            //  setup logging and reporting
            LOG.info("Beginning conversion and validation for [" + customerName + "].");
            reporter.addCustomerInfoMessage(customerName, "Beginning conversion and validation.");
            CustomerLoadResult result = reporter.getCustomer(customerName);
            customerBatchErrors = new CustomerLoadBatchErrors();
            
            //  convert the VO to a BO
            LOG.info("Beginning conversion from VO to BO.");
            customer = adapter.convert(customerDigesterVO, customerBatchErrors);
            
            //  if any errors were generated, add them to the GlobalVariables, and return false
            if (!customerBatchErrors.isEmpty()) {
                LOG.info("The customer [" + customerName + "] was not processed due to errors in uploading and conversion.");
                customerBatchErrors.addError(customerName, "Global", Object.class, "", "This document was not processed due to errors in uploading and conversion.");
                addBatchErrorstoCustomerLoadResult(customerBatchErrors, result);
                reporter.setCustomerFailureResult(customerName);
                docSucceeded = false;
                groupSucceeded &= false;
                continue;
            }

            //  determine whether this is an Update or a New
            Customer existingCustomer = customerAlreadyExists(customer);
            boolean isNew = (existingCustomer == null);
            boolean isUpdate = !isNew;
            
            //  do some housekeeping
            processBeforeValidating(customer, existingCustomer, isUpdate);
            
            //  create the transient maint doc
            MaintenanceDocument transientMaintDoc = createTransientMaintDoc();
            
            //  make sure we have the one real maint doc (to steal its document id)
            oneRealMaintDoc = createRealMaintDoc(oneRealMaintDoc);
            
            //  steal the doc id from the real doc
            transientMaintDoc.setDocumentNumber(oneRealMaintDoc.getDocumentNumber());
            transientMaintDoc.setDocumentHeader(oneRealMaintDoc.getDocumentHeader());
            transientMaintDoc.getDocumentHeader().setDocumentDescription("AR Customer Load Batch Transient");
            
            //  set the old and new
            transientMaintDoc.getNewMaintainableObject().setBusinessObject(customer);
            transientMaintDoc.getOldMaintainableObject().setBusinessObject((existingCustomer == null ? new Customer() : existingCustomer ));

            //  set the maintainable actions, so isNew and isEdit on the maint doc return correct values
            if (isNew) {
                transientMaintDoc.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
            }
            else {
                transientMaintDoc.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_EDIT_ACTION);
            }

            //  report whether the customer is an Add or an Edit
            if (isNew) {
                reporter.addCustomerInfoMessage(customerName, "Customer record batched is a New Customer.");
            }
            else {
                reporter.addCustomerInfoMessage(customerName, "Customer record batched is an Update to an existing Customer.");
            }
            
            //  validate the batched customer
            if (!validateSingle(transientMaintDoc, customerBatchErrors, customerName)) {
                groupSucceeded &= false;
                docSucceeded = false;
                reporter.setCustomerFailureResult(customerName);
            }
            addBatchErrorstoCustomerLoadResult(customerBatchErrors, result);
            
            //  if the doc succeeded then add it to the list to be routed, and report it as successful
            if (docSucceeded) {
                customerMaintDocs.add(transientMaintDoc);
                Customer customer2 = (Customer) transientMaintDoc.getNewMaintainableObject().getBusinessObject();
                reporter.addCustomerInfoMessage(customerName, "Customer Number is: " + customer2.getCustomerNumber());
                reporter.addCustomerInfoMessage(customerName, "Customer Name is:   " + customer2.getCustomerName());
                reporter.setCustomerSuccessResult(customerName);
            }
            
            fileBatchErrors.addAll(customerBatchErrors);
        }
        
        //  put any errors back in global vars
        if (useGlobalErrorMap) {
            addBatchErrorsToGlobalVariables(fileBatchErrors);
        }

        return groupSucceeded;
    }

    private void processBeforeValidating(Customer customer, Customer existingCustomer, boolean isUpdate) {
        
        
        //  if its an update, but has no customerNumber, then set it from existing record
        if (isUpdate && StringUtils.isBlank(customer.getCustomerNumber())) {
            customer.setCustomerNumber(existingCustomer.getCustomerNumber());
        }
        
        //  if its an update, then carry forward the version number
        if (isUpdate) {
            customer.setVersionNumber(existingCustomer.getVersionNumber());
        }
        
        //  dont let the batch zero out certain key fields on an update
        if (isUpdate) {
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerTypeCode");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerTaxTypeCode");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerTaxNbr");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerCreditLimitAmount");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerCreditApprovedByName");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerParentCompanyNumber");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerPhoneNumber");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customer800PhoneNumber");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerContactName");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerContactPhoneNumber");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerFaxNumber");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "customerBirthDate");
        }
        
        //  upper case important fields
        upperCaseKeyFields(customer);
        
        //  determine whether the batch has a primary address, and which one it is
        boolean batchHasPrimaryAddress = false;
        CustomerAddress batchPrimaryAddress = null;
        for (CustomerAddress address : customer.getCustomerAddresses()) {
            if (ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY.equalsIgnoreCase(address.getCustomerAddressTypeCode())) {
                batchHasPrimaryAddress = true;
                batchPrimaryAddress = address;
            }
        }
        
        //  if its an update, merge the address records (ie, only add or update, dont remove all 
        // addresses not imported).
        if (isUpdate) {
            boolean addressInBatchCustomer = false;
            List<CustomerAddress> existingAddresses = new ArrayList<CustomerAddress>();
            for (CustomerAddress existingAddress : existingCustomer.getCustomerAddresses()) {
                addressInBatchCustomer = false;
                for (CustomerAddress batchAddress : customer.getCustomerAddresses()) {
                    if (existingAddress.compareTo(batchAddress) == 0) {
                        addressInBatchCustomer = true;
                    }
                }
                if (!addressInBatchCustomer) {
                    //  make sure we dont add a second Primary address, if the batch specifies a primary address, it wins
                    if (ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY.equalsIgnoreCase(existingAddress.getCustomerAddressTypeCode())) {
                        existingAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
                    }
                    customer.getCustomerAddresses().add(existingAddress);
                }
            }
        }
        
        //  set parent customer number to null if blank (otherwise foreign key rule fails)
        if (StringUtils.isBlank(customer.getCustomerParentCompanyNumber())) {
            customer.setCustomerParentCompanyNumber(null);
        }
        
    }
    
    private void upperCaseKeyFields(Customer customer) {
        
        //  customer name
        if (StringUtils.isNotBlank(customer.getCustomerName())) { 
            customer.setCustomerName(customer.getCustomerName().toUpperCase());
        }
        
        //  customer number
        if (StringUtils.isNotBlank(customer.getCustomerNumber())) { 
            customer.setCustomerNumber(customer.getCustomerNumber().toUpperCase());
        }
        
        //  parent company number
        if (StringUtils.isNotBlank(customer.getCustomerParentCompanyNumber())) { 
            customer.setCustomerParentCompanyNumber(customer.getCustomerParentCompanyNumber().toUpperCase());
        }
        
        //  customer tax type code
        if (StringUtils.isNotBlank(customer.getCustomerTaxTypeCode())) { 
            customer.setCustomerTaxTypeCode(customer.getCustomerTaxTypeCode().toUpperCase());
        }
        
        //  customer tax number
        if (StringUtils.isNotBlank(customer.getCustomerTaxNbr())) { 
            customer.setCustomerTaxNbr(customer.getCustomerTaxNbr().toUpperCase());
        }
        
        //  customer contact name
        if (StringUtils.isNotBlank(customer.getCustomerContactName())) { 
            customer.setCustomerContactName(customer.getCustomerContactName().toUpperCase());
        }
        
        //  customer credit approved by name
        if (StringUtils.isNotBlank(customer.getCustomerCreditApprovedByName())) { 
            customer.setCustomerCreditApprovedByName(customer.getCustomerCreditApprovedByName().toUpperCase());
        }
        
        //  customer email address
        if (StringUtils.isNotBlank(customer.getCustomerEmailAddress())) { 
            customer.setCustomerEmailAddress(customer.getCustomerEmailAddress().toUpperCase());
        }
        
        for (CustomerAddress address : customer.getCustomerAddresses()) {
            
            if (address == null) continue;
            
            //  customer number
            if (StringUtils.isNotBlank(address.getCustomerNumber())) {
                address.setCustomerNumber(address.getCustomerNumber().toUpperCase());
            }
            
            //  customer address name
            if (StringUtils.isNotBlank(address.getCustomerAddressName())) {
                address.setCustomerAddressName(address.getCustomerAddressName().toUpperCase());
            }
            
            //  customerLine1StreetAddress
            if (StringUtils.isNotBlank(address.getCustomerLine1StreetAddress())) {
                address.setCustomerLine1StreetAddress(address.getCustomerLine1StreetAddress().toUpperCase());
            }
            
            //  customerLine2StreetAddress
            if (StringUtils.isNotBlank(address.getCustomerLine2StreetAddress())) {
                address.setCustomerLine2StreetAddress(address.getCustomerLine2StreetAddress().toUpperCase());
            }
            
            //  customerCityName
            if (StringUtils.isNotBlank(address.getCustomerCityName())) {
                address.setCustomerCityName(address.getCustomerCityName().toUpperCase());
            }
            
            //  customerStateCode
            if (StringUtils.isNotBlank(address.getCustomerStateCode())) {
                address.setCustomerStateCode(address.getCustomerStateCode().toUpperCase());
            }
            
            //  customerZipCode
            if (StringUtils.isNotBlank(address.getCustomerZipCode())) {
                address.setCustomerZipCode(address.getCustomerZipCode().toUpperCase());
            }
            
            //  customerCountryCode
            if (StringUtils.isNotBlank(address.getCustomerNumber())) {
                address.setCustomerNumber(address.getCustomerNumber().toUpperCase());
            }
            
            //  customerAddressInternationalProvinceName
            if (StringUtils.isNotBlank(address.getCustomerAddressInternationalProvinceName())) {
                address.setCustomerAddressInternationalProvinceName(address.getCustomerAddressInternationalProvinceName().toUpperCase());
            }
            
            //  customerInternationalMailCode
            if (StringUtils.isNotBlank(address.getCustomerInternationalMailCode())) {
                address.setCustomerInternationalMailCode(address.getCustomerInternationalMailCode().toUpperCase());
            }
            
            //  customerEmailAddress
            if (StringUtils.isNotBlank(address.getCustomerEmailAddress())) {
                address.setCustomerEmailAddress(address.getCustomerEmailAddress().toUpperCase());
            }
            
            //  customerAddressTypeCode
            if (StringUtils.isNotBlank(address.getCustomerAddressTypeCode())) {
                address.setCustomerAddressTypeCode(address.getCustomerAddressTypeCode().toUpperCase());
            }
            
        }
    }
    
    /**
     * 
     * This messy thing attempts to compare a property on the batch customer (new) and existing customer, and if 
     * the new is blank, but the old is there, to overwrite the new-value with the old-value, thus preventing 
     * batch uploads from blanking out certain fields.
     * 
     * @param batchCustomer
     * @param existingCustomer
     * @param propertyName
     */
    private void dontBlankOutFieldsOnUpdate(Customer batchCustomer, Customer existingCustomer, String propertyName) {
        String batchValue;
        String existingValue;
        Class<?> propertyClass = null;
        
        //  try to retrieve the property type to see if it exists at all
        try {
            propertyClass = PropertyUtils.getPropertyType(batchCustomer, propertyName);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not access properties on the Customer object.", e);
        }
        
        //  if the property doesnt exist, then throw an exception
        if (propertyClass == null) {
            throw new IllegalArgumentException("The propertyName specified [" + propertyName + "] doesnt exist on the Customer object.");
        }
        
        //  get the String values of both batch and existing, to compare
        try {
            batchValue = BeanUtils.getSimpleProperty(batchCustomer, propertyName);
            existingValue = BeanUtils.getSimpleProperty(existingCustomer, propertyName);
        }
        catch (Exception e) {
            throw new RuntimeException("Could not access properties on the Customer object.", e);
        }
        
        //  if the existing is non-blank, and the new is blank, then over-write the new with the existing value
        if (StringUtils.isBlank(batchValue) && StringUtils.isNotBlank(existingValue)) {

            //  get the real typed value, and then try to set the property value 
            try {
                Object typedValue = PropertyUtils.getProperty(existingCustomer, propertyName);
                BeanUtils.setProperty(batchCustomer, propertyName, typedValue);
            }
            catch (Exception e) {
                throw new RuntimeException("Could not set properties on the Customer object.", e);
            }
        }
    }
    
    private boolean validateSingle(MaintenanceDocument maintDoc, CustomerLoadBatchErrors batchErrors, String customerName) {
        boolean result = true;
        
        //  get an instance of the business rule 
        CustomerRule rule = new CustomerRule();
        
        //  run the business rules
        result &= rule.processRouteDocument(maintDoc);
        
        extractGlobalVariableErrors(batchErrors, customerName);
        
        return result;
    }
    
    private boolean extractGlobalVariableErrors(CustomerLoadBatchErrors batchErrors, String customerName) {
        boolean result = true;
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        Set<String> errorKeys = errorMap.keySet();
        List<ErrorMessage> errorMessages = null;
        Object[] messageParams;
        String errorKeyString;
        String errorString;
        
        for (String errorProperty : errorKeys) {
            errorMessages = (List<ErrorMessage>) errorMap.get(errorProperty);
            for (ErrorMessage errorMessage : errorMessages) {
                errorKeyString = configService.getPropertyString(errorMessage.getErrorKey()); 
                messageParams = errorMessage.getMessageParameters();
                
                // MessageFormat.format only seems to replace one 
                // per pass, so I just keep beating on it until all are gone.
                if (StringUtils.isBlank(errorKeyString)) {
                    errorString = errorMessage.getErrorKey();
                }
                else {
                    errorString = errorKeyString;
                }
                while (errorString.matches("^.*\\{\\d\\}.*$")) {
                    errorString = MessageFormat.format(errorString, messageParams);
                }
                batchErrors.addError(customerName, errorProperty, Object.class, "", errorString);
                result = false;
            }
        }
        
        //  clear the stuff out of globalvars, as we need to reformat it and put it back
        GlobalVariables.getErrorMap().clear();
        return result;
    }
    
    private MaintenanceDocument createTransientMaintDoc() {
        MaintenanceDocument maintDoc = new MaintenanceDocumentBase(getCustomerMaintenanceDocumentTypeName());
        return maintDoc;
    }
    
    private MaintenanceDocument createRealMaintDoc(MaintenanceDocument document) {
        if (document == null) {
            try {
                document = (MaintenanceDocument) docService.getNewDocument(getCustomerMaintenanceDocumentTypeName());
            }
            catch (WorkflowException e) {
                throw new RuntimeException("WorkflowException thrown when trying to create new MaintenanceDocument.", e);
            }
        }
        return document;
    }
    
    /**
     */
    private Customer customerAlreadyExists(Customer customer) {
        
        Customer existingCustomer = null;
        
        //  test existence by customerNumber, if one is passed in
        if (StringUtils.isNotBlank(customer.getCustomerNumber())) {
            existingCustomer = customerService.getByPrimaryKey(customer.getCustomerNumber());
            if (existingCustomer != null) {
                return existingCustomer;
            }
        }
        
        //  test existence by TaxNumber, if one is passed in
        if (StringUtils.isNotBlank(customer.getCustomerTaxNbr())) {
            existingCustomer = customerService.getByTaxNumber(customer.getCustomerTaxNbr());
            if (existingCustomer != null) {
                return existingCustomer;
            }
        }
        
        //  test existence by Customer Name.  this is looking for an exact match, so isnt terribly effective
        if (StringUtils.isNotBlank(customer.getCustomerName())) {
            existingCustomer = customerService.getCustomerByName(customer.getCustomerName());
            if (existingCustomer != null) {
                return existingCustomer;
            }
        }
        
        //  return a null Customer if no matches were found
        return existingCustomer;
    }
    
    private void writeReportPDF(List<CustomerLoadFileResult> fileResults) {
        
        if (fileResults.isEmpty()) {
            return;
        }
        
        //  setup the PDF business
        Document pdfDoc = new Document(PageSize.LETTER, 54, 54, 72, 72);
        getPdfWriter(pdfDoc);
        pdfDoc.open();
        
        if (fileResults.isEmpty()) {
            writeFileNameSectionTitle(pdfDoc, "NO DOCUMENTS FOUND TO PROCESS");
            return;
        }
        
        CustomerLoadResult result;
        String customerResultLine;
        for (CustomerLoadFileResult fileResult : fileResults) { 
            
            //  file name title
            String fileNameOnly = fileResult.getFilename().toUpperCase();
            fileNameOnly = fileNameOnly.substring(fileNameOnly.lastIndexOf("\\") + 1);
            writeFileNameSectionTitle(pdfDoc, fileNameOnly);
            
            //  write any file-general messages
            writeMessageEntryLines(pdfDoc, fileResult.getMessages());
            
            //  walk through each customer included in this file
            for (String customerName : fileResult.getCustomerNames()) {
                result = fileResult.getCustomer(customerName);
                
                //  write the customer title
                writeCustomerSectionTitle(pdfDoc, customerName.toUpperCase());
                
                //  write a success/failure results line for this customer
                customerResultLine = result.getResultString() + (ResultCode.SUCCESS.equals(result.getResult()) ? WORKFLOW_DOC_ID_PREFIX + result.getWorkflowDocId() : "");
                writeCustomerSectionResult(pdfDoc, customerResultLine);
                
                //  write any customer messages 
                writeMessageEntryLines(pdfDoc, result.getMessages());
            }
        }
        
        pdfDoc.close();
    }
    
    private void writeFileNameSectionTitle(Document pdfDoc, String filenameLine) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);
        
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_LEFT);
        Chunk chunk = new Chunk(filenameLine, font);
        chunk.setBackground(Color.LIGHT_GRAY, 5, 5, 5, 5);
        paragraph.add(chunk);
        
        //  blank line
        paragraph.add(new Chunk("", font));
        
        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }
    
    private void writeCustomerSectionTitle(Document pdfDoc, String customerNameLine) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD + Font.UNDERLINE);
        
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(new Chunk(customerNameLine, font));

        //  blank line
        paragraph.add(new Chunk("", font));
        
        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }
    
    private void writeCustomerSectionResult(Document pdfDoc, String resultLine) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
        
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(new Chunk(resultLine, font));

        //  blank line
        paragraph.add(new Chunk("", font));
        
        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }
    
    private void writeMessageEntryLines(Document pdfDoc, List<String[]> messageLines) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
        
        Paragraph paragraph;
        String messageEntry;
        for (String[] messageLine : messageLines) {
            paragraph = new Paragraph();
            paragraph.setAlignment(Element.ALIGN_LEFT);
            messageEntry = StringUtils.rightPad(messageLine[0], (12 - messageLine[0].length()), " ") + " - " + messageLine[1].toUpperCase();
            paragraph.add(new Chunk(messageEntry, font));

            //  blank line
            paragraph.add(new Chunk("", font));
            
            try {
                pdfDoc.add(paragraph);
            }
            catch (DocumentException e) {
                LOG.error("iText DocumentException thrown when trying to write content.", e);
                throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
            }
        }
    }
    
    private void getPdfWriter(Document pdfDoc) {
        
        String reportDropFolder = reportsDirectory + "/" + ArConstants.CustomerLoad.CUSTOMER_LOAD_REPORT_SUBFOLDER + "/";
        String fileName = ArConstants.CustomerLoad.BATCH_REPORT_BASENAME + "_" +  
            new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(dateTimeService.getCurrentDate()) + ".pdf";
       
        //  setup the writer
        File reportFile = new File(reportDropFolder + fileName);
        FileOutputStream fileOutStream;
        try {
            fileOutStream = new FileOutputStream(reportFile);
        }
        catch (IOException e) {
            LOG.error("IOException thrown when trying to open the FileOutputStream.", e);
            throw new RuntimeException("IOException thrown when trying to open the FileOutputStream.", e);
        }
        BufferedOutputStream buffOutStream = new BufferedOutputStream(fileOutStream);
        
        try {
            PdfWriter.getInstance(pdfDoc, buffOutStream);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to start a new instance of the PdfWriter.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to start a new instance of the PdfWriter.", e);
        }
        
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

    public void setDocService(DocumentService docService) {
        this.docService = docService;
    }
    
    public void setBatchInputFileType(BatchInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }

    public void setSysInfoService(SystemInformationService sysInfoService) {
        this.sysInfoService = sysInfoService;
    }

    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }
    
}

