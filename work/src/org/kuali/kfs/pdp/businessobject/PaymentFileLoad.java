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
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Represents the parsed contents of an incoming payment file.
 */
public class PaymentFileLoad extends TransientBusinessObjectBase {
    // header fields
    private String chart;
    private String org;
    private String subUnit;
    private Date creationDate;

    // trailer fields
    private int paymentCount;
    private BigDecimal paymentTotalAmount;

    // data
    private List<PaymentGroup> paymentGroups;

    // load vars
    private Integer batchId;
    private boolean fileThreshold;
    private boolean detailThreshold;
    private boolean taxEmailRequired;

    private List<PaymentDetail> thresholdPaymentDetails;

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
    public BigDecimal getCalculatedPaymentTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(0);

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
     * Gets the org attribute.
     * 
     * @return Returns the org.
     */
    public String getOrg() {
        return org;
    }

    /**
     * Sets the org attribute value.
     * 
     * @param org The org to set.
     */
    public void setOrg(String org) {
        this.org = org;
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
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creationDate attribute value.
     * 
     * @param creationDate The creationDate to set.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Takes <code>String</code> input and attempts to format as a Date for setting the creationDate field
     * 
     * @param creationDate date in string format
     */
    public void setCreationDate(String creationDate) throws ParseException {
        // date string contains a T separating the date and time part, need to remove before format
        creationDate = StringUtils.replace(creationDate, PdpConstants.PAYMENT_LOAD_CREATE_DATE_SEPARATOR, " ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PdpConstants.PAYMENT_LOAD_CREATE_DATE_FORMAT, Locale.US);
        this.creationDate = simpleDateFormat.parse(creationDate);
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
     * Gets the paymentTotalAmount attribute.
     * 
     * @return Returns the paymentTotalAmount.
     */
    public BigDecimal getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    /**
     * Sets the paymentTotalAmount attribute value.
     * 
     * @param paymentTotalAmount The paymentTotalAmount to set.
     */
    public void setPaymentTotalAmount(BigDecimal paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
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
    public Integer getBatchId() {
        return batchId;
    }

    /**
     * Sets the batchId attribute value.
     * 
     * @param batchId The batchId to set.
     */
    public void setBatchId(Integer batchId) {
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

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.CHART, this.chart);
        m.put(KFSPropertyConstants.ORG, this.org);
        m.put(PdpPropertyConstants.SUB_UNIT, this.subUnit);
        m.put(PdpPropertyConstants.CREATION_DATE, this.creationDate);

        return m;
    }

}
