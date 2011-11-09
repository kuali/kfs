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
package org.kuali.kfs.module.ar.batch.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Contains a set of errors while attempting to run a batch of 
 * AR Customer Load entries.
 * 
 */
public class CustomerLoadBatchErrors {

    //  key = CustomerName
    private Map<String, List<CustomerLoadBatchError>> batchErrors;
    private int errorCount;
    
    /**
     * 
     * Creates a new CustomerLoadBatchErrors object, with empty internal content.
     */
    public CustomerLoadBatchErrors() {
        //  initialize the internal storage
        batchErrors = new TreeMap<String, List<CustomerLoadBatchError>>();
        errorCount = 0;
    }
    
    public CustomerLoadBatchErrors(CustomerLoadBatchError error) {

        //  initialize the internal storage
        batchErrors = new TreeMap<String, List<CustomerLoadBatchError>>();
        errorCount = 0;

        //  short circuit if no content is passed in
        if (error == null) return;
        
        //  stick the constructor-passed entry in
        batchErrors.put(error.getCustomerName(), new ArrayList<CustomerLoadBatchError>());
        batchErrors.get(error.getCustomerName()).add(error);
        errorCount++;
    }

    /**
     * 
     * Constructs a CustomerLoadBatchErrors with the specified List of CustomerLoadBatchError 
     * objects as the starting content.
     * 
     * @param errors A List of CustomerLoadBatchError objects, which handles nulls and 
     */
    public CustomerLoadBatchErrors(List<CustomerLoadBatchError> errors) {

        //  initialize the internal storage
        batchErrors = new TreeMap<String, List<CustomerLoadBatchError>>();

        //  short circuit if no content is passed in
        if (errors == null) return;
        if (errors.isEmpty()) return;
        
        addErrors(errors);
    }

    public void addAll(CustomerLoadBatchErrors otherErrors) {
        if (otherErrors == null) {
            throw new IllegalArgumentException("Parameter otherErrors passed in was null.");
        }
        for (String customerName : otherErrors.getCompanyNames()) {
            List<CustomerLoadBatchError> customerErrors = otherErrors.getErrorsByCompany(customerName);
            for (CustomerLoadBatchError customerLoadBatchError : customerErrors) {
                addError(customerLoadBatchError);
            }
        }
    }
    
    public void addError(CustomerLoadBatchError error) {
        if (error == null) {
            throw new IllegalArgumentException("Parameter 'error' passed in was null.");
        }
        if (!batchErrors.containsKey(error.getCustomerName())) {
            batchErrors.put(error.getCustomerName(), new ArrayList<CustomerLoadBatchError>());
        }
        batchErrors.get(error.getCustomerName()).add(error);
        errorCount++;
    }

    public void addError(String customerName, String propertyName, Class<?> propertyClass, String value, String description) {
        if (StringUtils.isBlank(customerName)) {
            throw new IllegalArgumentException("Parameter customerName was empty or null.");
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("Parameter propertyName was empty or null.");
        }
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("Parameter description was empty or null.");
        }
        CustomerLoadBatchError error = new CustomerLoadBatchError(customerName, propertyName, propertyClass, value, description);
        addError(error);
    }
    
    public void addErrors(List<CustomerLoadBatchError> errors) {
        if (errors == null) { 
            throw new IllegalArgumentException("Parameter 'error' passed in was null.");
        }
        for (CustomerLoadBatchError error : errors) {
            addError(error);
        }
    }
    
    /**
     * 
     * Returns the companyName's that have errors in this list.
     * 
     * Note that the set of CompanyNames returned should be in ascending order 
     * for String ordering.
     * 
     * Note that the underlying implementation's return is undefined where there 
     * are no elements in the storage yet, so test for null first.
     * 
     * @return The set of companyName's that have errors.  
     */
    public Set<String> getCompanyNames() {
        return batchErrors.keySet();
    }
    
    /**
     * 
     * Returns the list of CustomerLoadBatchError objects for the given 
     * companyName.
     * 
     * Note that null will be returned if there are no errors for that companyName.
     * 
     * Note that ordering of CustomerLoadBatchError objects for a given companyName is 
     * undefined.
     * 
     * @param companyName The companyName you want errors for.
     * @return The (possibly null or empty) List of CustomerLoadBatchError objects for the given companyName.
     */
    public List<CustomerLoadBatchError> getErrorsByCompany(String companyName) {
        if (!batchErrors.containsKey(companyName)) {
            return null;
        }
        return batchErrors.get(companyName);
    }
    
    /**
     * 
     * Returns true if there are no elements (errors) in this object.
     * @return True if no errors have been added, False if any have been.
     */
    public boolean isEmpty() {
        return batchErrors.isEmpty();
    }
    
    /**
     * 
     * Returns a string error for each error, suitable to go into some other error container.
     * 
     * The returned Set is sorted by natural order, and the String constructed should naturally order 
     * by companyName, then propertyName, though this is not absolutely guaranteed in all cases.
     * 
     * @return A set of error messages.
     */
    public Set<String> getErrorStrings() {
        Set<String> errors = new TreeSet<String>();
        for (String companyName : batchErrors.keySet()) {
            for (CustomerLoadBatchError error : batchErrors.get(companyName)) {
                errors.add(error.toString());
            }
        }
        return errors;
    }
    
    public int getCompaniesWithErrors() {
        return batchErrors.size();
    }
    
    public int getTotalErrors() {
        return errorCount;
    }
}
