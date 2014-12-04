/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
