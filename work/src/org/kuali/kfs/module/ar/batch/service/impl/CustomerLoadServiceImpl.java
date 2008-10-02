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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.BatchMaintainable;
import org.kuali.kfs.module.ar.batch.CustomerLoadBatchErrors;
import org.kuali.kfs.module.ar.batch.CustomerLoadBatchMaintainable;
import org.kuali.kfs.module.ar.batch.rule.CustomerLoadDDValidator;
import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterAdapter;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.ApcRuleDefinition;
import org.kuali.rice.kns.datadictionary.ReferenceDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.TypedArrayList;

public class CustomerLoadServiceImpl implements CustomerLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadServiceImpl.class);

    private static final Class<Customer> BO_CLASS = Customer.class;
    private static final String DD_ENTRY_NAME = BO_CLASS.getName();
    private static final String DOC_TYPE_NAME = "CustomerMaintenanceDocument";
    
    private BatchInputFileService batchInputFileService;
    private CustomerAddressService customerAddressService;
    private CustomerService customerService;
    private MaintenanceDocumentDictionaryService maintDocDDService;
    private DataDictionaryService ddService;
    private DictionaryValidationService dictionaryValidationService;
    private KualiConfigurationService configService;
    private DocumentService docService;
    
    private BatchInputFileType batchInputFileType;

    private CustomerDigesterAdapter adapter;
    private CustomerLoadDDValidator ddValidator;
    
    public CustomerLoadServiceImpl() {
    }
    
    public boolean loadFiles() {
        
        boolean result = true;
        
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(batchInputFileType);
        
        //  fail if null returned by batchInputFileService
        if (fileNamesToLoad == null) {
            throw new RuntimeException("BatchInputFileService.listInputFileNamesWithDoneFile(" + 
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
        }
        
        List<String> processedFiles = new TypedArrayList(String.class);
        for (String inputFileName : fileNamesToLoad) {
            
            //  filenames returned should never be blank/empty/null
            if (StringUtils.isBlank(inputFileName)) {
                throw new RuntimeException("One of the file names returned as ready to process [" + inputFileName + 
                        "] was blank.  This should not happen, so throwing an error to investigate.");
            }
            
            if (loadFile(inputFileName)) {
                result &= true;
                processedFiles.add(inputFileName);
            }
            else {
                result &= false;
            }
        }

        removeDoneFiles(processedFiles);
        
        return result;
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
        
        boolean result = true;
        
        List<String> routedDocumentNumbers = new ArrayList<String>();
        List<String> failedDocumentNumbers = new ArrayList<String>();
        
        //  load up the file into a byte array 
        byte[] fileByteContent = safelyLoadFileBytes(fileName);
        BatchInputFileType batchInputFileType;
        batchInputFileType = retrieveBatchInputFileTypeImpl(ArConstants.CustomerLoad.CUSTOMER_LOAD_FILE_TYPE_IDENTIFIER);

        //  parse the file against the XSD schema and load it into an object
        Object parsedObject = null;
        try {
            parsedObject = batchInputFileService.parse(batchInputFileType, fileByteContent);
        }
        catch (XMLParseException e) {
            LOG.error("errors parsing xml " + e.getMessage(), e);
            throw new XMLParseException(e.getMessage());
        }
        
        //  make sure we got the type we expected, then cast it
        if (!(parsedObject instanceof CustomerDigesterVO)) {
            throw new RuntimeException("Parsed object was not of the expected type.  " + 
                    "Was: [" + parsedObject.getClass().toString() + "], expected: [" + CustomerDigesterVO.class + "].");
        }
        CustomerDigesterVO customerVO = (CustomerDigesterVO) parsedObject;
        
        //  prepare a list for the regular validate() method
        List<CustomerDigesterVO> customerVOs = new ArrayList<CustomerDigesterVO>();
        customerVOs.add(customerVO);
        
        List<MaintenanceDocument> readyTransientDocs = new ArrayList<MaintenanceDocument>();
        result = validateAndPrepare(customerVOs, readyTransientDocs);
        
        //  send the readyDocs into workflow
        result &= sendDocumentsIntoWorkflow(readyTransientDocs, routedDocumentNumbers, failedDocumentNumbers);
        
        return result;
    }

    private boolean sendDocumentsIntoWorkflow(List<MaintenanceDocument> readyTransientDocs, List<String> routedDocumentNumbers, List<String> failedDocumentNumbers) {
        boolean result = true;
        for (MaintenanceDocument readyTransientDoc : readyTransientDocs) {
            result &= sendDocumentIntoWorkflow(readyTransientDoc, routedDocumentNumbers, failedDocumentNumbers);
        }
        return result;
    }
    
    private boolean sendDocumentIntoWorkflow(MaintenanceDocument readyTransientDoc, List<String> routedDocumentNumbers, List<String> failedDocumentNumbers) {
        boolean result = true;
        
        //  create a real workflow document
        MaintenanceDocument realMaintDoc;
        try {
            realMaintDoc = (MaintenanceDocument) docService.getNewDocument(DOC_TYPE_NAME);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("WorkflowException occurred while trying to create a new MaintenanceDocument.", e);
        }
        
        realMaintDoc.getNewMaintainableObject().setBusinessObject(readyTransientDoc.getNewMaintainableObject().getBusinessObject());
        realMaintDoc.getOldMaintainableObject().setBusinessObject(readyTransientDoc.getOldMaintainableObject().getBusinessObject());
        
        try {
            docService.routeDocument(realMaintDoc, "Routed from CustomerLoad Batch Process", null);
        }
        catch (WorkflowException e) {
            LOG.error("WorkflowException occurred while trying to route a new MaintenanceDocument.");
            result = false;
        }
        return result;
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
     * 
     * Delegates to Spring Context to retrieve an instance of the named BatchInputFileType.
     * @param batchInputTypeName The TypeName of the BatchInputFileType subclass desired.
     * @return An instance of the named type.
     */
    private BatchInputFileType retrieveBatchInputFileTypeImpl(String batchInputTypeName) {
        BatchInputFileType batchInputType = SpringContext.getBeansOfType(BatchInputFileType.class).get(batchInputTypeName);
        if (batchInputType == null) {
            LOG.error("Batch input type implementation not found for id " + batchInputTypeName);
            throw new RuntimeException(("Batch input type implementation not found for id " + batchInputTypeName));
        }
        return batchInputType;
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
        return validateAndPrepare(customerUploads, new ArrayList<MaintenanceDocument>());
    }
    
    public boolean validateAndPrepare(List<CustomerDigesterVO> customerUploads, List<MaintenanceDocument> customerMaintDocs) {
        
        //  fail if empty or null list
        if (customerUploads == null) {
            LOG.error("Null list of Customer upload objects.  This should never happen.");
            throw new IllegalArgumentException("Null list of Customer upload objects.  This should never happen.");
        }
        if (customerUploads.isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, new String[] { "An empty list of Customer uploads was passed in for validation.  As a result, no validation was done." });
            return false;
        }

        boolean groupSucceeded = true;
        boolean docSucceeded = true;
        Customer customer = null;
        CustomerLoadBatchErrors batchErrors = null;
        if (adapter == null) adapter = new CustomerDigesterAdapter();
        for (CustomerDigesterVO customerDigesterVO : customerUploads) {
            
            docSucceeded = true;
            
            //  start a new ErrorMap for each Customer
            batchErrors = new CustomerLoadBatchErrors();
            
            //  convert the VO to a BO
            customer = adapter.convert(customerDigesterVO, batchErrors);
            
            //  if any errors were generated, add them to the GlobalVariables, and return false
            if (!batchErrors.isEmpty()) {
                batchErrors.addError(customer.getCustomerName(), "Global", Object.class, "N/A", "This document was not processed due to errors in uploading and conversion.");
                addBatchErrorsToGlobalVariables(batchErrors);
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
            
            //  create the maint doc
            MaintenanceDocument transientMaintDoc = createTransientMaintDoc(customer);

            //  set the old and new
            transientMaintDoc.getNewMaintainableObject().setBusinessObject(customer);
            transientMaintDoc.getOldMaintainableObject().setBusinessObject(existingCustomer);

            if (!validateSingle(transientMaintDoc, batchErrors, customer.getCustomerName())) {
                groupSucceeded &= false;
                docSucceeded = false;
            }
            
            //  put any errors back in global vars
            addBatchErrorsToGlobalVariables(batchErrors);
            
            if (docSucceeded) customerMaintDocs.add(transientMaintDoc);
            
        }
        
        return groupSucceeded;
    }

    private void processBeforeValidating(Customer customer, Customer existingCustomer, boolean isUpdate) {
        
        
        //  if its an update, but has no customerNumber, then set it from existing record
        if (isUpdate && StringUtils.isBlank(customer.getCustomerNumber())) {
            customer.setCustomerNumber(existingCustomer.getCustomerNumber());
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
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "");
            dontBlankOutFieldsOnUpdate(customer, existingCustomer, "");
        }
        
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
        String errorKeyString;
        String errorString;
        
        for (String errorProperty : errorKeys) {
            errorMessages = (List<ErrorMessage>) errorMap.get(errorProperty);
            for (ErrorMessage errorMessage : errorMessages) {
                errorKeyString = configService.getPropertyString(errorMessage.getErrorKey()); 
                errorString = MessageFormat.format(errorKeyString, (Object[]) errorMessage.getMessageParameters());
                batchErrors.addError(customerName, errorProperty, Object.class, "N/A", errorString);
                result = false;
            }
        }
        
        //  clear the stuff out of globalvars, as we need to reformat it and put it back
        GlobalVariables.getErrorMap().clear();
        return result;
    }
    
    private MaintenanceDocument createTransientMaintDoc(Customer customer) {
        MaintenanceDocument maintDoc = new MaintenanceDocumentBase(DOC_TYPE_NAME);
        return maintDoc;
    }
    
    private boolean validateSingle2(Customer customer, CustomerLoadBatchErrors batchErrors) {
        boolean result = true;
        
        BatchMaintainable customerMaintainable = new CustomerLoadBatchMaintainable(customer);
        
        //  determine whether this is an Update or a New
        Customer existingCustomer = customerAlreadyExists(customer);
        if (existingCustomer != null) {
            customerMaintainable.setUpdate(true);
            //  wire in the customer number from the Customer record already in the db``
            customer.setCustomerNumber(existingCustomer.getCustomerNumber());
        }
        else {
            customerMaintainable.setNew(true);
        }
        
        //  test against the DD rules and restrictions
        result &= applyDataDictionaryRules(customer, batchErrors);
        
        //  make sure all reference fields that are filled have valid values for the reference table/objects
        result &= validateBOReferences(customer, batchErrors);
        
        //  apply all APC rules, if any
        result &= validateAPCRules(customer, batchErrors);
        
        //  apply regular maint doc business rules
        result &= applyMaintDocRules(customerMaintainable, batchErrors);
        
        return true;
    }
    
    private boolean validateAPCRules(Customer customer, CustomerLoadBatchErrors batchErrors) {
        boolean result = true;
        
        List<ApcRuleDefinition> apcRules = (List<ApcRuleDefinition>) maintDocDDService.getApplyApcRules(BO_CLASS);
        
        for (ApcRuleDefinition apcRule : apcRules) {

            String propertyName = apcRule.getAttributeName();
            String propertyValue;
            try {
                propertyValue = BeanUtils.getSimpleProperty(customer, apcRule.getAttributeName());
            }
            catch (Exception e) {
                throw new RuntimeException("Exception thrown while trying to get bean property [" + propertyName + "] from a Customer instance.", e);
            }

            //  only run the apc rule if there's a value present
            if (StringUtils.isNotBlank(propertyValue)) {
                if (!configService.evaluateConstrainedValue(apcRule.getParameterNamespace(), apcRule.getParameterDetailType(), apcRule.getParameterName(), propertyValue)) {
                    result &= false;
                    addError(batchErrors, customer.getCustomerName(), apcRule.getAttributeName(), Object.class, propertyValue, "APC Rule Failure: " + apcRule.getErrorMessage());
                }
            }
        }
        return result;
    }
    
    private boolean validateBOReferences(Customer customer, CustomerLoadBatchErrors batchErrors) {
        boolean result = true;
        
        result &= validateBOReferenceOnObject(customer, batchErrors, customer.getCustomerName());
        
        for (CustomerAddress address : customer.getCustomerAddresses()) {
            result &= validateBOReferenceOnObject(address, batchErrors, customer.getCustomerName());
        }
        return result;
    }
    
    private boolean validateBOReferenceOnObject(PersistableBusinessObject bo, CustomerLoadBatchErrors batchErrors, String customerName) {
        boolean result = true;
        
        String propertyName;
        String propertyValue;
        
        List<ReferenceDefinition> references = (List<ReferenceDefinition>) maintDocDDService.getDefaultExistenceChecks(BO_CLASS);
        for (ReferenceDefinition reference : references) {
            if (!dictionaryValidationService.validateReferenceExistsAndIsActive(bo, reference)) {
                propertyName = reference.getAttributeName();
                try {
                    propertyValue = BeanUtils.getSimpleProperty(bo, propertyName);
                }
                catch (Exception e) {
                    LOG.error("Exception occurred while trying to access bean property for Customer instance with propertyName [" + propertyName + "].");
                    propertyValue = "Unknown";
                }
                result &= false;
                addError(batchErrors, customerName, propertyName, Object.class, propertyValue, "Reference value specified is not valid, does not exist or is not active.");
            }
        }
        return result;
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
    
    /**
     * 
     * Applies DataDictionary maint doc & bo rules & configuration against the BO.  This is things 
     * like required fields, default values, etc etc.
     * 
     * @param customerMaintainable The BatchMaintainable containing the BO.
     * @return True if no errors occurred, False if any errors or validation failures occurred.
     */
    private boolean applyDataDictionaryRules(Customer customer, CustomerLoadBatchErrors batchErrors) {
        if (ddValidator == null) ddValidator = new CustomerLoadDDValidator();
        return ddValidator.validate(customer, batchErrors);
    }
    
    private boolean applyMaintDocRules(BatchMaintainable customerMaintainable, CustomerLoadBatchErrors batchErrors) {
        
        return false;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setMaintDocDDService(MaintenanceDocumentDictionaryService maintDocDDService) {
        this.maintDocDDService = maintDocDDService;
    }

    public void setDdService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
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

}
