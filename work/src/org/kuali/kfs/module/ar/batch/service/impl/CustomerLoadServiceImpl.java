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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.CustomerLoadBatchErrors;
import org.kuali.kfs.module.ar.batch.CustomerLoadStep;
import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterAdapter;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.module.ar.document.validation.impl.CustomerRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.businessobject.FinancialSystemUserOrganizationSecurity;
import org.kuali.kfs.sys.businessobject.FinancialSystemUserPrimaryOrganization;
import org.kuali.kfs.sys.exception.XMLParseException;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.TypedArrayList;

public class CustomerLoadServiceImpl implements CustomerLoadService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadServiceImpl.class);

    private static final String DOC_TYPE_NAME = "CustomerMaintenanceDocument";
    private static final String MAX_RECORDS_PARM_NAME = "MAX_NUMBER_OF_RECORDS_PER_DOCUMENT";
    
    private BatchInputFileService batchInputFileService;
    private CustomerService customerService;
    private KualiConfigurationService configService;
    private DocumentService docService;
    private ParameterService parameterService;
    private OrganizationService orgService;
    private FinancialSystemUserService fsUserService;
    private SystemInformationService sysInfoService;
    private BusinessObjectService boService;
    
    private BatchInputFileType batchInputFileType;
    private CustomerDigesterAdapter adapter;
    
    public CustomerLoadServiceImpl() {
    }
    
    public boolean checkAuthorization(UniversalUser user, File batchFile) {
        FinancialSystemUser fsUser = fsUserService.getFinancialSystemUser(user.getPersonUniversalIdentifier());
        return isUserInArBillingOrProcessingOrg(fsUser);
    }

    private boolean isUserInArBillingOrProcessingOrg(FinancialSystemUser fsUser) {
        
        Org fsUserOrg = fsUser.getOrganization();
        List<FinancialSystemUserPrimaryOrganization> primaryOrgs = fsUser.getPrimaryOrganizations();
        List<FinancialSystemUserOrganizationSecurity> securityOrgs = fsUser.getOrganizationSecurity();
        
        //  gather up all chart/org combos we want to search for
        String userChart, userOrg; 
        Map<String,String> pkMap;
        Map<String,Map<String,String>> searchOrgs = new HashMap<String,Map<String,String>>();
        for (FinancialSystemUserPrimaryOrganization userPrimaryOrg : primaryOrgs) {
            userChart = userPrimaryOrg.getChartOfAccountsCode();
            userOrg = userPrimaryOrg.getOrganizationCode();
            if (!searchOrgs.containsKey(userChart + userOrg)) {
                pkMap = new HashMap<String,String>();
                pkMap.put("chartOfAccountsCode", userChart);
                pkMap.put("organizationCode", userOrg);
                searchOrgs.put(userChart + userOrg, pkMap);
            }
        }
        for (FinancialSystemUserOrganizationSecurity userOrgSecurity : securityOrgs) {
            userChart = userOrgSecurity.getChartOfAccountsCode();
            userOrg = userOrgSecurity.getOrganizationCode();
            if (!searchOrgs.containsKey(userChart + userOrg)) {
                pkMap = new HashMap<String,String>();
                pkMap.put("chartOfAccountsCode", userChart);
                pkMap.put("organizationCode", userOrg);
                searchOrgs.put(userChart + userOrg, pkMap);
            }
        }
        
        OrganizationOptions orgOpts = null;
        SystemInformation sysInfo = null;
        for (String searchOrgKey : searchOrgs.keySet()) {
            pkMap = searchOrgs.get(searchOrgKey);
            userChart = pkMap.get("chartOfAccountsCode");
            userOrg = pkMap.get("organizationCode");

            //  see if there is an OrgOpt (Billing Org) belonging to this person's orgs
            orgOpts = (OrganizationOptions) boService.findByPrimaryKey(OrganizationOptions.class, pkMap);
            if (orgOpts != null) return true; 
            
            //  see if there is a SystemInformation (ProcessingOrg) belonging to this person's orgs
            sysInfo = sysInfoService.getByProcessingChartAndOrg(userChart, userOrg);
            if (sysInfo != null) return true;
        }
        return false;
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
        
        //  check to make sure the input file doesnt have more docs than we allow in one batch file
        String maxRecordsString = parameterService.getParameterValue(CustomerLoadStep.class, MAX_RECORDS_PARM_NAME);
        if (StringUtils.isBlank(maxRecordsString) || !StringUtils.isNumeric(maxRecordsString)) {
            throw new RuntimeException("Expected 'Max Records Per Document' System Parameter is not available.");
        }
        Integer maxRecords = new Integer(maxRecordsString);
        if (customerUploads.size() > maxRecords.intValue()) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_BATCH_UPLOAD_SAVE, new String[] { "Too many records passed in for this file.  " + customerUploads.size() + " were passed in, and the limit is " + maxRecords + ".  As a result, no validation was done." });
            return false;
        }
        
        //  we have to create one real maint doc for the whole thing to pass the maintainable.checkAuthorizationRestrictions 
        MaintenanceDocument oneRealMaintDoc = null;
        
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
    
    private MaintenanceDocument createTransientMaintDoc() {
        MaintenanceDocument maintDoc = new MaintenanceDocumentBase(DOC_TYPE_NAME);
        return maintDoc;
    }
    
    private MaintenanceDocument createRealMaintDoc(MaintenanceDocument document) {
        if (document == null) {
            try {
                document = (MaintenanceDocument) docService.getNewDocument(DOC_TYPE_NAME);
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

    public void setFsUserService(FinancialSystemUserService fsUserService) {
        this.fsUserService = fsUserService;
    }

    public void setSysInfoService(SystemInformationService sysInfoService) {
        this.sysInfoService = sysInfoService;
    }

    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

}
