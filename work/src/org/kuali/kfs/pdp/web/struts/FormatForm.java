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
package org.kuali.kfs.pdp.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberFormatter;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.FormatProcessSummary;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * Struts Action Form for Format Checks/ACH
 */
public class FormatForm extends KualiForm {

    private String campus;
    private String paymentDate;
    private String paymentTypes;
    private String initiatorEmail;

    private FormatProcessSummary formatProcessSummary;

    private List<CustomerProfile> customers;
    private List<DisbursementNumberRange> ranges;

    /**
     * Constructs a FormatForm.
     */
    public FormatForm() {
        super();
        customers = new ArrayList<CustomerProfile>();
        ranges = new ArrayList<DisbursementNumberRange>();

         this.setFormatterType("range.lastAssignedDisbNbr", DisbursementNumberFormatter.class);
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

    public String getInitiatorEmail() {
        return initiatorEmail;
    }

    public void setInitiatorEmail(String initiatorEmail) {
        this.initiatorEmail = initiatorEmail;
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
        return customers.get(index);
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
        return ranges.get(index);
    }

    /**
     * This method gets the currency formated value of the total amount.
     *
     * @return the currency formated value of the total amount
     */
    public String getCurrencyFormattedTotalAmount() {
        return (String) new CurrencyFormatter().format(formatProcessSummary.getTotalAmount());
    }

    /**
     * This method gets the payment date.
     *
     * @return paymentDate
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * This method sets the payment date.
     *
     * @param paymentDate
     */
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * This method gets the format process summary.
     *
     * @return formatProcessSummary
     */
    public FormatProcessSummary getFormatProcessSummary() {
        return formatProcessSummary;
    }

    /**
     * This method sets the format process summary.
     *
     * @param formatProcessSummary
     */
    public void setFormatProcessSummary(FormatProcessSummary formatProcessSummary) {
        this.formatProcessSummary = formatProcessSummary;
    }

    /**
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);

        for (CustomerProfile customer : customers) {
            customer.setSelectedForFormat(false);
        }
    }
}
