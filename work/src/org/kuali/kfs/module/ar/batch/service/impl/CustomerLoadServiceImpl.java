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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.BatchMaintainable;
import org.kuali.kfs.module.ar.batch.CustomerLoadBatchErrors;
import org.kuali.kfs.module.ar.batch.CustomerLoadBatchMaintainable;
import org.kuali.kfs.module.ar.batch.rule.CustomerLoadDDValidator;
import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterAdapter;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;

public class CustomerLoadServiceImpl implements CustomerLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadServiceImpl.class);

    private BatchInputFileService batchInputFileService;
    private CustomerAddressService customerAddressService;
    private CustomerService customerService;
    private MaintenanceDocumentDictionaryService maintDocDDService;
    private DataDictionaryService ddService;
    
    private CustomerDigesterAdapter adapter;
    private CustomerLoadDDValidator ddValidator;
    
    public CustomerLoadServiceImpl() {
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.batch.service.CustomerLoadService#loadFile(java.lang.String)
     */
    public boolean loadFile(String fileName) {
        
        boolean result = true;
        
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
        
        //  prepare empty batch errors list
        CustomerLoadBatchErrors batchErrors = new CustomerLoadBatchErrors();
        
        //  convert the digested object into a real Customer object
        if (adapter == null) adapter = new CustomerDigesterAdapter();
        Customer customer = adapter.convert(customerVO, batchErrors);
        
        //  validate the object against business rules, only if the convert passed with no errors
        if (batchErrors.isEmpty()) {
            result &= validateSingle(customer, batchErrors);
        }
        
        //  if we failed the validation and conversion, then dont even try to submit the maint doc
        if (!batchErrors.isEmpty()) {
            addBatchErrorsToGlobalVariables(batchErrors);
            return false;
        }

        //  create a new maint document, submit it
        
        return result;
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
        
        //  fail if empty or null list
        if (customerUploads == null) {
            LOG.error("Null list of Customer upload objects.  This should never happen.");
            throw new IllegalArgumentException("Null list of Customer upload objects.  This should never happen.");
        }
        if (customerUploads.isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, new String[] { "An empty list of Customer uploads was passed in for validation.  As a result, no validation was done." });
            return false;
        }

        boolean result = true;
        Customer customer = null;
        CustomerLoadBatchErrors batchErrors = null;
        if (adapter == null) adapter = new CustomerDigesterAdapter();
        for (CustomerDigesterVO customerDigesterVO : customerUploads) {
            
            //  start a new ErrorMap for each Customer
            batchErrors = new CustomerLoadBatchErrors();
            
            //  convert the VO to a BO
            customer = adapter.convert(customerDigesterVO, batchErrors);
            
            //  only run the validation if there were no errors in the conversion
            if (batchErrors.isEmpty()) {
                result &= validateSingle(customer, batchErrors);
            }
            
            //  if any errors were generated, add them to the GlobalVariables, and return false
            if (!batchErrors.isEmpty()) {
                addBatchErrorsToGlobalVariables(batchErrors);
                result &= false;
            }
        }
        
        return result;
    }

    private boolean validateSingle(Customer customer, CustomerLoadBatchErrors batchErrors) {
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
        result &= applyDataDictionaryRules(customerMaintainable, batchErrors);
        
        if (batchErrors.isEmpty()) {
            //  validate the reference values, that they exist in the DB
            result &= validateBOReferences(customerMaintainable, batchErrors);
            
            //  only run the business rules if the DD stuff passed and produced no errors
            result &= applyMaintDocRules(customerMaintainable, batchErrors);
        }
        
        return true;
    }
    
    private boolean validateBOReferences(BatchMaintainable customerMaintainable, CustomerLoadBatchErrors batchErrors) {
        boolean result = true;
        
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
    private boolean applyDataDictionaryRules(BatchMaintainable customerMaintainable, CustomerLoadBatchErrors batchErrors) {
        Customer customer = (Customer) customerMaintainable.getBusinessObject();
        if (ddValidator == null) ddValidator = new CustomerLoadDDValidator();
        return ddValidator.validate(customer, batchErrors);
    }
    
    private void addError(CustomerLoadBatchErrors batchErrors, String customerName, String propertyName, Class<?> propertyClass, String origValue, String description) {
        batchErrors.addError(customerName, propertyName, propertyClass, origValue, description);
    }
    
    private boolean applyMaintDocRules(BatchMaintainable customerMaintainable, CustomerLoadBatchErrors batchErrors) {
        return false;
    }

    //  delegates to the CustomerAddressService
    private Integer getNewAddressIdentifier() {
        return customerAddressService.getNextCustomerAddressIdentifier();
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
    
}
