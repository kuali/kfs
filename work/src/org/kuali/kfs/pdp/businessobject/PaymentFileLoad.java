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
package org.kuali.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Represents the parsed contents of an incoming payment file.
 */
public class PaymentFileLoad extends TransientBusinessObjectBase {
    // header fields
    private String chart;
    private String unit;
    private String subUnit;
    protected Timestamp creationDate;

    // trailer fields
    private int paymentCount;
    private KualiDecimal paymentTotalAmount;

    // data
    private List<PaymentGroup> paymentGroups;

    // load vars
    private KualiInteger batchId;
    private boolean fileThreshold;
    private boolean detailThreshold;
    private boolean taxEmailRequired;

    private List<PaymentDetail> thresholdPaymentDetails;
    private CustomerProfile customer;

    private boolean passedValidation;

    public PaymentFileLoad() {
        super();
        paymentGroups = new ArrayList<PaymentGroup>();
        fileThreshold = false;
        detailThreshold = false;
        taxEmailRequired = false;
        passedValidation = false;
        thresholdPaymentDetails = new ArrayList<PaymentDetail>();
    }

    /**
     * @return number of detail records loaded
     */
    public int getActualPaymentCount() {
        int count = 0;

        for (PaymentGroup paymentGroup : paymentGroups) {
            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                count++;
            }
        }

        return count;
    }

    /**
     * @return total amount of all payments
     */
    public KualiDecimal getCalculatedPaymentTotalAmount() {
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        for (PaymentGroup paymentGroup : paymentGroups) {
            for (PaymentDetail paymentDetail : paymentGroup.getPaymentDetails()) {
                totalAmount = totalAmount.add(paymentDetail.getAccountTotal());
            }
        }

        return totalAmount;
    }

    /**
     * Gets the chart attribute.
     * 
     * @return Returns the chart.
     */
    public String getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute value.
     * 
     * @param chart The chart to set.
     */
    public void setChart(String chart) {
        this.chart = chart;
    }

    /**
     * Gets the unit attribute.
     * 
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit attribute value.
     * 
     * @param unit The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Gets the subUnit attribute.
     * 
     * @return Returns the subUnit.
     */
    public String getSubUnit() {
        return subUnit;
    }

    /**
     * Sets the subUnit attribute value.
     * 
     * @param subUnit The subUnit to set.
     */
    public void setSubUnit(String subUnit) {
        this.subUnit = subUnit;
    }

    /**
     * Gets the creationDate attribute.
     * 
     * @return Returns the creationDate.
     */
    public Timestamp getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creationDate attribute value.
     * 
     * @param creationDate The creationDate to set.
     */
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    
    /**
     * Takes a <code>String</code> and attempt to format as <code>Timestamp</code for setting the
     * creationDate field
     * 
     * @param creationDate Timestamp as string
     */
    public void setCreationDate(String creationDate) {
        try {
            this.creationDate = SpringContext.getBean(DateTimeService.class).convertToSqlTimestamp(creationDate);
        }
        catch (ParseException e) {
            throw new RuntimeException("Unable to convert create timestamp value " + creationDate + " :" + e.getMessage(), e);
        }
    }

    /**
     * Gets the paymentCount attribute.
     * 
     * @return Returns the paymentCount.
     */
    public int getPaymentCount() {
        return paymentCount;
    }

    /**
     * Sets the paymentCount attribute value.
     * 
     * @param paymentCount The paymentCount to set.
     */
    public void setPaymentCount(int paymentCount) {
        this.paymentCount = paymentCount;
    }

    /**
     * Helper method to set the paymentCount int from a string.
     * 
     * @param paymentCount String payment count
     */
    public void setPaymentCount(String paymentCount) {
        this.paymentCount = Integer.parseInt(paymentCount);
    }

    /**
     * Gets the paymentTotalAmount attribute.
     * 
     * @return Returns the paymentTotalAmount.
     */
    public KualiDecimal getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    /**
     * Sets the paymentTotalAmount attribute value.
     * 
     * @param paymentTotalAmount The paymentTotalAmount to set.
     */
    public void setPaymentTotalAmount(KualiDecimal paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }
    
    public void setPaymentTotalAmount(String paymentTotalAmount) {
        this.paymentTotalAmount = new KualiDecimal(paymentTotalAmount);
    }

    /**
     * Gets the paymentGroups attribute.
     * 
     * @return Returns the paymentGroups.
     */
    public List<PaymentGroup> getPaymentGroups() {
        return paymentGroups;
    }

    /**
     * Sets the paymentGroups attribute value.
     * 
     * @param paymentGroups The paymentGroups to set.
     */
    public void setPaymentGroups(List<PaymentGroup> paymentGroups) {
        this.paymentGroups = paymentGroups;
    }

    /**
     * Adds a <code>PaymentGroup</code> to the group <code>List</code>
     * 
     * @param paymentGroup <code>PaymentGroup</code> to add
     */
    public void addPaymentGroup(PaymentGroup paymentGroup) {
        this.paymentGroups.add(paymentGroup);
    }

    /**
     * Gets the fileThreshold attribute.
     * 
     * @return Returns the fileThreshold.
     */
    public boolean isFileThreshold() {
        return fileThreshold;
    }

    /**
     * Sets the fileThreshold attribute value.
     * 
     * @param fileThreshold The fileThreshold to set.
     */
    public void setFileThreshold(boolean fileThreshold) {
        this.fileThreshold = fileThreshold;
    }

    /**
     * Gets the detailThreshold attribute.
     * 
     * @return Returns the detailThreshold.
     */
    public boolean isDetailThreshold() {
        return detailThreshold;
    }

    /**
     * Sets the detailThreshold attribute value.
     * 
     * @param detailThreshold The detailThreshold to set.
     */
    public void setDetailThreshold(boolean detailThreshold) {
        this.detailThreshold = detailThreshold;
    }


    /**
     * Gets the batchId attribute.
     * 
     * @return Returns the batchId.
     */
    public KualiInteger getBatchId() {
        return batchId;
    }

    /**
     * Sets the batchId attribute value.
     * 
     * @param batchId The batchId to set.
     */
    public void setBatchId(KualiInteger batchId) {
        this.batchId = batchId;
    }

    /**
     * Gets the taxEmailRequired attribute.
     * 
     * @return Returns the taxEmailRequired.
     */
    public boolean isTaxEmailRequired() {
        return taxEmailRequired;
    }

    /**
     * Sets the taxEmailRequired attribute value.
     * 
     * @param taxEmailRequired The taxEmailRequired to set.
     */
    public void setTaxEmailRequired(boolean taxEmailRequired) {
        this.taxEmailRequired = taxEmailRequired;
    }

    /**
     * Gets the thresholdPaymentDetails attribute.
     * 
     * @return Returns the thresholdPaymentDetails.
     */
    public List<PaymentDetail> getThresholdPaymentDetails() {
        return thresholdPaymentDetails;
    }

    /**
     * Sets the thresholdPaymentDetails attribute value.
     * 
     * @param thresholdPaymentDetails The thresholdPaymentDetails to set.
     */
    public void setThresholdPaymentDetails(List<PaymentDetail> thresholdPaymentDetails) {
        this.thresholdPaymentDetails = thresholdPaymentDetails;
    }

    /**
     * Gets the passedValidation attribute.
     * 
     * @return Returns the passedValidation.
     */
    public boolean isPassedValidation() {
        return passedValidation;
    }

    /**
     * Sets the passedValidation attribute value.
     * 
     * @param passedValidation The passedValidation to set.
     */
    public void setPassedValidation(boolean passedValidation) {
        this.passedValidation = passedValidation;
    }


    /**
     * Gets the customer attribute.
     * 
     * @return Returns the customer.
     */
    public CustomerProfile getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * 
     * @param customer The customer to set.
     */
    public void setCustomer(CustomerProfile customer) {
        this.customer = customer;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.CHART, this.chart);
        m.put(PdpPropertyConstants.UNIT, this.unit);
        m.put(PdpPropertyConstants.SUB_UNIT, this.subUnit);
        m.put(PdpPropertyConstants.CREATION_DATE, this.creationDate);

        return m;
    }

}
