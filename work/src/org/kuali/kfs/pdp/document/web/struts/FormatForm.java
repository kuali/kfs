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
package org.kuali.kfs.pdp.document.web.struts;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.FormatResult;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This class...
 */
public class FormatForm extends KualiForm {
    
    private String campus;
    private Timestamp paymentDate;
    private Integer procId;
    private String paymentTypes;
    private KualiDecimal totalAmount;
    private int totalPaymentCount;
    
    private List<CustomerProfile> customers;
    private List<DisbursementNumberRange> ranges;
    private List<FormatResult> results;

    /**
     * Constructs a FormatForm.
     */
    public FormatForm() {
        super();
        customers = new ArrayList<CustomerProfile>();
        ranges = new ArrayList<DisbursementNumberRange>();
        results =  new ArrayList<FormatResult>();
    }

    /**
     * This method gets campus
     * 
     * @return campus
     */
    public String getCampus() {
        return campus;
    }

    /**
     * This method sets campus
     * 
     * @param campus
     */
    public void setCampus(String campus) {
        this.campus = campus;
    }

    /**
     * This method gets payment types
     * 
     * @return paymentTypes
     */
    public String getPaymentTypes() {
        return paymentTypes;
    }

    /**
     * This method sets paymentTypes
     * 
     * @param paymentTypes
     */
    public void setPaymentTypes(String paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    /**
     * This method gets customers
     * 
     * @return customers
     */
    public List<CustomerProfile> getCustomers() {
        return customers;
    }

    /**
     * This method sets customers
     * 
     * @param customers
     */
    public void setCustomers(List<CustomerProfile> customers) {
        this.customers = customers;
    }

    /**
     * This method retrieves a specific customer profile from the list, by index
     * 
     * @param index the index of the customers to retrieve the customer profile from
     * @return a CustomerProfile
     */
    public CustomerProfile getCustomer(int index) {
        if (index >= customers.size()) {
            for (int i = customers.size(); i <= index; i++) {
                customers.add(new CustomerProfile());
            }
        }
        return (CustomerProfile) customers.get(index);
    }

    /**
     * This method sets a customer profile.
     * 
     * @param key the index of the value
     * @param value the new value
     */
    public void setCustomer(int key, CustomerProfile value) {
        customers.set(key, value);
    }


    /**
     * This method gets the ranges.
     * 
     * @return ranges list
     */
    public List<DisbursementNumberRange> getRanges() {
        return ranges;
    }

    /**
     * This method sets ranges list.
     * 
     * @param ranges
     */
    public void setRanges(List<DisbursementNumberRange> ranges) {
        this.ranges = ranges;
    }

    /**
     * This method retrieves a specific disbursement number range from the list, by index
     * 
     * @param index the index of the ranges to retrieve the disbursement number range from
     * @return a DisbursementNumberRange
     */
    public DisbursementNumberRange getRange(int index) {
        if (index >= ranges.size()) {
            for (int i = ranges.size(); i <= index; i++) {
                ranges.add(new DisbursementNumberRange());
            }
        }
        return (DisbursementNumberRange) ranges.get(index);
    }

    /**
     * This method gets the paymentDate
     * 
     * @return the paymentDate
     */
    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    /**
     * This method sets the paymentDate
     * 
     * @param paymentDate
     */
    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * This method gets the process id.
     * 
     * @return procId
     */
    public Integer getProcId() {
        return procId;
    }

    /**
     * This method sets the process id.
     * 
     * @param procId
     */
    public void setProcId(Integer procId) {
        this.procId = procId;
    }

    /**
     * This method gets the results list.
     * 
     * @return the results list
     */
    public List<FormatResult> getResults() {
        return results;
    }

    /**
     * This method sets the results list.
     * 
     * @param results
     */
    public void setResults(List<FormatResult> results) {
        this.results = results;
    }

    /**
     * This method retrieves a specific disbursement number range from the list, by index
     * 
     * @param index the index of the results to retrieve the format result from
     * @return a FormatResult
     */
    public FormatResult getResult(int index) {
        if (index >= results.size()) {
            for (int i = results.size(); i <= index; i++) {
                results.add(new FormatResult());
            }
        }
        return (FormatResult) results.get(index);
    }

    /**
     * This method sets a format result value at a given index in the results list.
     * 
     * @param key the index
     * @param value the new value
     */
    public void setResult(int key, FormatResult value) {
        results.set(key, value);
    }

    /**
     * This method gets the total amount
     * 
     * @return
     */
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * This method sets the total amount
     * 
     * @param totalAmount
     */
    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * This method gets the total number of payments
     * 
     * @return totalPaymentCount
     */
    public int getTotalPaymentCount() {
        return totalPaymentCount;
    }

    /**
     * This method sets the total number of payments.
     * 
     * @param totalPaymentCount
     */
    public void setTotalPaymentCount(int totalPaymentCount) {
        this.totalPaymentCount = totalPaymentCount;
    }

    /**
     * This method gets the currency formated value of the total amount.
     * 
     * @return the currency formated value of the total amount
     */
    public String getCurrencyFormattedTotalAmount() {
        return (String) new CurrencyFormatter().format(totalAmount);
    }
}
