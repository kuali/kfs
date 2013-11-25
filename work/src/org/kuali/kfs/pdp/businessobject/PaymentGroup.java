/*
 * Copyright 2007 The Kuali Foundation
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

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class represents the PaymentGroup
 */
public class PaymentGroup extends TimestampedBusinessObjectBase {
    private static KualiDecimal zero = KualiDecimal.ZERO;

    private KualiInteger id; // PMT_GRP_ID
    private String payeeName; // PMT_PAYEE_NM
    private String payeeId; // PAYEE_ID
    private String payeeIdTypeCd; // PAYEE_ID_TYP_CD
    private String alternatePayeeId; // ALTRNT_PAYEE_ID
    private String alternatePayeeIdTypeCd; // ALTRNT_PAYEE_ID_TYP_CD
    private String payeeOwnerCd; // PAYEE_OWNR_CD
    private String line1Address; // PMT_LN1_ADDR
    private String line2Address; // PMT_LN2_ADDR
    private String line3Address; // PMT_LN3_ADDR
    private String line4Address; // PMT_LN4_ADDR
    private String city; // PMT_CTY_NM
    private String state; // PMT_ST_NM
    private String country; // PMT_CNTRY_NM
    private String zipCd; // PMT_ZIP_CD
    private Boolean campusAddress; // CMP_ADDR_IND
    private Date paymentDate; // PMT_DT DATE
    private Boolean pymtAttachment; // PMT_ATTCHMNT_IND
    private Boolean pymtSpecialHandling; // PMT_SPCL_HANDLG_IND
    private Boolean taxablePayment; // PMT_TXBL_IND
    private Boolean nraPayment; // NRA_PMT_IND
    private Boolean processImmediate; // PROC_IMD_IND
    private Boolean combineGroups; // PMT_GRP_CMB_IND
    private String achBankRoutingNbr; // ACH_BNK_RTNG_NBR
    private String adviceEmailAddress; // ADV_EMAIL_ADDR
    private Boolean employeeIndicator; // EMP_IND
    private String creditMemoNbr; // PMT_CRDT_MEMO_NBR
    private KualiDecimal creditMemoAmount; // PMT_CRDT_MEMO_AMT
    private KualiInteger disbursementNbr; // DISB_NBR
    private Date disbursementDate; // DISB_TS
    private String physCampusProcessCd; // PHYS_CMP_PROC_CD
    private String sortValue; // PMT_SORT_ORD_VAL
    private String achAccountType; // CUST_ACCT_TYP_CD
    private Timestamp epicPaymentCancelledExtractedDate; // PDP_EPIC_PMT_CNCL_EXTRT_TS
    private Timestamp epicPaymentPaidExtractedDate; // PDP_EPIC_PMT_PD_EXTRT_TS
    private Timestamp adviceEmailSentDate; // ADV_EMAIL_SNT_TS

    private KualiInteger batchId;
    private Batch batch; // PMT_BATCH_ID

    private KualiInteger processId;
    private PaymentProcess process; // PROC_ID

    private String paymentStatusCode;
    private PaymentStatus paymentStatus; // PMT_STAT_CD

    private String disbursementTypeCode;
    private DisbursementType disbursementType; // DISB_TYP_CD

    private String bankCode;
    private Bank bank; // BNK_ID

    private AchAccountNumber achAccountNumber;

    private List<PaymentGroupHistory> paymentGroupHistory = new ArrayList<PaymentGroupHistory>();
    private List<PaymentDetail> paymentDetails = new ArrayList<PaymentDetail>();

    /**
     * Constructs a PaymentGroup.java.
     */
    public PaymentGroup() {
        super();
    }

    /**
     * This method gets the dailyReportSpecialHandling
     * @return dailyReportSpecialHandling
     */
    public boolean isDailyReportSpecialHandling() {
        return pymtSpecialHandling && !processImmediate;
    }

    /**
     * This method gets the dailyReportAttachment
     * @return dailyReportAttachment
     */
    public boolean isDailyReportAttachment() {
        return !pymtSpecialHandling && !processImmediate && pymtAttachment;
    }

    /**
     * This method gets the paymentStatusCode
     * @return paymentStatusCode
     */
    public String getPaymentStatusCode() {
        return paymentStatusCode;
    }

    /**
     * @return String containing the payment status code and indication or cancel/reissued payments or stale payments
     */
    public String getPaymentStatusCodeWithHistory() {
        if (paymentStatus == null) {
            this.refreshReferenceObject(PdpPropertyConstants.PAYMENT_STATUS);
        }

        // check for canceled and reissued
        String paymentStatusWithHistory = "";
        if (paymentStatus != null) {
            paymentStatusWithHistory += paymentStatus.getName();
        }

        boolean isCanceledReissued = false;
        for (PaymentGroupHistory paymentGroupHistory : getPaymentGroupHistory()) {
            if (PdpConstants.PaymentChangeCodes.CANCEL_REISSUE_DISBURSEMENT.equals(paymentGroupHistory.getPaymentChangeCode())) {
                isCanceledReissued = true;
            }
            if (PdpConstants.PaymentChangeCodes.REISSUE_DISBURSEMENT.equals(paymentGroupHistory.getPaymentChangeCode())) {
                isCanceledReissued = true;
            }

        }

        if (isCanceledReissued) {
            paymentStatusWithHistory += " (Reissued)";
        }

        // check for stale payments, if one payment detail is stale then they all are
        PaymentDetail paymentDetail = getPaymentDetails().get(0);
        if (!paymentDetail.isDisbursementActionAllowed()) {
            paymentStatusWithHistory += " (Stale)";
        }

        return paymentStatusWithHistory;
    }

    /**
     * WIDTH MUST BE LESS THAN THE # OF SPACES
     *
     * @param width
     * @param val
     * @return
     */
    private String getWidthString(int width, String val) {
        return (val + "                                        ").substring(0, width - 1);
    }

    /**
     * This method gets the boolean valuse of a Boolean object.
     * @param b the boolean object
     * @return the boolean value
     */
    private boolean booleanValue(Boolean b) {
        boolean bv = false;
        if (b != null) {
            bv = b.booleanValue();
        }
        return bv;
    }

    /**
     * This method gets the notle lines
     * @return the note lines
     */
    public int getNoteLines() {
        int count = 0;
        for (Iterator iter = this.getPaymentDetails().iterator(); iter.hasNext();) {
            PaymentDetail element = (PaymentDetail) iter.next();
            count++; // Add a line for the invoice #
            count = count + element.getNotes().size();
        }
        return count;
    }

    /**
     * Get the total of all the detail items
     *
     * @return
     */
    public KualiDecimal getNetPaymentAmount() {
        KualiDecimal amt = KualiDecimal.ZERO;
        for (Iterator iter = this.getPaymentDetails().iterator(); iter.hasNext();) {
            PaymentDetail element = (PaymentDetail) iter.next();
            amt = amt.add(element.getNetPaymentAmount());
        }
        return amt;
    }

    /**
     * @hibernate.set name="paymentDetail"
     * @hibernate.collection-key column="pmt_grp_id"
     * @hibernate.collection-one-to-many class="edu.iu.uis.pdp.bo.PaymentDetail"
     */
    public List<PaymentDetail> getPaymentDetails() {
        return paymentDetails;
    }

    /**
     * This method sets the payment details list
     * @param paymentDetail
     */
    public void setPaymentDetails(List<PaymentDetail> paymentDetail) {
        this.paymentDetails = paymentDetail;
    }

    /**
     * This method adds a paymentDetail
     * @param pgh the payments detail to be added
     */
    public void addPaymentDetails(PaymentDetail pgh) {
        pgh.setPaymentGroup(this);
        paymentDetails.add(pgh);
    }

    public void deletePaymentDetails(PaymentDetail pgh) {
        paymentDetails.remove(pgh);
    }

    /**
     * @hibernate.set name="paymentGroupHistory"
     * @hibernate.collection-key column="pmt_grp_id"
     * @hibernate.collection-one-to-many class="edu.iu.uis.pdp.bo.PaymentGroupHistory"
     */
    public List<PaymentGroupHistory> getPaymentGroupHistory() {
        return paymentGroupHistory;
    }

    /**
     * This method sets the payment group history list
     * @param paymentGroupHistory
     */
    public void setPaymentGroupHistory(List<PaymentGroupHistory> paymentGroupHistory) {
        this.paymentGroupHistory = paymentGroupHistory;
    }

    /**
     * This method adds a paymentGroupHistory
     * @param pd the paymentGroupHistory to be added
     */
    public void addPaymentGroupHistory(PaymentGroupHistory pd) {
        pd.setPaymentGroup(this);
        paymentGroupHistory.add(pd);
    }

    /**
     * This method deletes a paymentGroupHistory
     * @param pd the paymentGroupHistory to be deleted
     */
    public void deletePaymentGroupHistory(PaymentGroupHistory pd) {
        paymentGroupHistory.remove(pd);
    }

    /**
     * @hibernate.id column="PMT_GRP_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_GRP_ID_SEQ"
     * @return
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @hibernate.one-to-one class="edu.iu.uis.pdp.bo.AchAccountNumber"
     * @return
     */
    public AchAccountNumber getAchAccountNumber() {
        return achAccountNumber;
    }

    /**
     * This method sets the achAccountNumber
     * @param aan
     */
    public void setAchAccountNumber(AchAccountNumber aan) {
        this.achAccountNumber = aan;
    }

    /**
     * This method gets the sortValue
     * @return sortValue
     */
    public String getSortValue() {
        return sortValue;
    }

    /**
     * This method sets the sort value
     * @param sortGroupId
     */
    public void setSortValue(int sortGroupId) {
        String defaultSortOrderParameterName = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PdpKeyConstants.DEFAULT_SORT_GROUP_ID_PARAMETER);
        String defaultSortOrderParameterValue = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PaymentGroup.class, defaultSortOrderParameterName);

        StringBuffer sb = new StringBuffer();

        sb.append(sortGroupId);

        CustomerProfile cp = this.getBatch().getCustomerProfile();
        sb.append(cp.getChartCode());
        sb.append(getWidthString(4, cp.getUnitCode()));
        sb.append(getWidthString(4, cp.getSubUnitCode()));

        if (defaultSortOrderParameterValue.equals(String.valueOf(sortGroupId))) {
            sb.append(this.getPayeeId());
            sb.append(this.getPayeeIdTypeCd());
        }
        else {
            sb.append(this.getPayeeName());
        }
        this.sortValue = sb.toString();
    }

    /**
     * @hibernate.property column="PMT_CTY_NM" length="30"
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getCombineGroups() {
        return combineGroups;
    }

    public void setCombineGroups(Boolean combineGroups) {
        this.combineGroups = combineGroups;
    }

    /**
     * @hibernate.property column="PMT_CNTRY_NM" length="30"
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @hibernate.property column="PMT_ST_NM" length="30"
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return
     * @hibernate.property column="ACH_BNK_RTNG_NBR" length="9"
     */
    public String getAchBankRoutingNbr() {
        return achBankRoutingNbr;
    }

    /**
     * @return
     * @hibernate.property column="ADV_EMAIL_ADDR" length="50"
     */
    public String getAdviceEmailAddress() {
        return adviceEmailAddress;
    }

    /**
     * @return
     * @hibernate.property column="ALTRNT_PAYEE_ID" length="25"
     */
    public String getAlternatePayeeId() {
        return alternatePayeeId;
    }

    /**
     * @return
     * @hibernate.property column="ALTRNT_PAYEE_ID_TYP_CD" length="2"
     */
    public String getAlternatePayeeIdTypeCd() {
        return alternatePayeeIdTypeCd;
    }

    /**
     * @return
     * @hibernate.many-to-one column="BNK_ID" class="edu.iu.uis.pdp.bo.Bank" not-null="false"
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * @return
     * @hibernate.many-to-one column="PMT_BATCH_ID" class="edu.iu.uis.pdp.bo.Batch" not-null="true"
     */
    public Batch getBatch() {
        return batch;
    }

    /**
     * Gets the bankCode attribute.
     *
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bankCode attribute value.
     *
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * @return
     * @hibernate.property column="CMP_ADDR_IND" type="yes_no"
     */
    public Boolean getCampusAddress() {
        return campusAddress;
    }

    /**
     * @return
     * @hibernate.property column="PMT_CRDT_MEMO_AMT" length="14"
     */
    public KualiDecimal getCreditMemoAmount() {
        return creditMemoAmount;
    }

    /**
     * @return
     * @hibernate.property column="PMT_CRDT_MEMO_NBR" length="14"
     */
    public String getCreditMemoNbr() {
        return creditMemoNbr;
    }

    /**
     * This method gets the disbursementDate.
     * @return disbursementDate
     */
    public Date getDisbursementDate() {
        return disbursementDate;
    }

    /**
     * @return
     * @hibernate.property column="DISB_NBR" length="9"
     */
    public KualiInteger getDisbursementNbr() {
        return disbursementNbr;
    }

    /**
     * @return
     * @hibernate.many-to-one column="DISB_TYP_CD" class="edu.iu.uis.pdp.bo.DisbursementType" not-null="false"
     */
    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    public Boolean getEmployeeIndicator() {
        return employeeIndicator;
    }

    /**
     * @return
     * @hibernate.property column="PMT_LN1_ADDR" length="45"
     */
    public String getLine1Address() {
        return line1Address;
    }

    /**
     * @return
     * @hibernate.property column="PMT_LN2_ADDR" length="45"
     */
    public String getLine2Address() {
        return line2Address;
    }

    /**
     * @return
     * @hibernate.property column="PMT_LN3_ADDR" length="45"
     */
    public String getLine3Address() {
        return line3Address;
    }

    /**
     * @return
     * @hibernate.property column="PMT_LN4_ADDR" length="45"
     */
    public String getLine4Address() {
        return line4Address;
    }

    /**
     * @return
     * @hibernate.property column="NRA_PMT_IND" type="yes_no"
     */
    public Boolean getNraPayment() {
        return nraPayment;
    }

    /**
     * @return
     * @hibernate.property column="PAYEE_ID" length="25"
     */
    public String getPayeeId() {
        return payeeId;
    }

    /**
     * @return
     * @hibernate.property column="PAYEE_ID_TYP_CD" length="1"
     */
    public String getPayeeIdTypeCd() {
        return payeeIdTypeCd;
    }

    /**
     * @return
     * @hibernate.property column="PMT_PAYEE_NM" length="40"
     */
    public String getPayeeName() {
        return payeeName;
    }

    /**
     * @return
     * @hibernate.property column="PAYEE_OWNR_CD" length="2"
     */
    public String getPayeeOwnerCd() {
        return payeeOwnerCd;
    }

    /**
     * @return
     * @hibernate.property column="PMT_DT"
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * @return
     * @hibernate.many-to-one column="PMT_STAT_CD" class="edu.iu.uis.pdp.bo.PaymentStatus" not-null="true"
     */
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * @return
     * @hibernate.property column="PHYS_CMP_PROC_CD" length="2"
     */
    public String getPhysCampusProcessCd() {
        return physCampusProcessCd;
    }

    /**
     * @return
     * @hibernate.many-to-one column="PROC_ID" class="edu.iu.uis.pdp.bo.PaymentProcess" not-null="false"
     */
    public PaymentProcess getProcess() {
        return process;
    }

    /**
     * @return
     * @hibernate.property column="PROC_IMD_IND" type="yes_no" length="1"
     */
    public Boolean getProcessImmediate() {
        return processImmediate;
    }

    /**
     * @return
     * @hibernate.property column="PMT_ATTCHMNT_IND" type="yes_no" length="1"
     */
    public Boolean getPymtAttachment() {
        return pymtAttachment;
    }

    /**
     * @return
     * @hibernate.property column="PMT_SPCL_HANDLG_IND" type="yes_no" length="1"
     */
    public Boolean getPymtSpecialHandling() {
        return pymtSpecialHandling;
    }

    /**
     * @return
     * @hibernate.property column="PMT_TXBL_IND" type="yes_no" length="1"
     */
    public Boolean getTaxablePayment() {
        return taxablePayment;
    }

    /**
     * @return
     * @hibernate.property column="PMT_ZIP_CD" length="2"
     */
    public String getZipCd() {
        return zipCd;
    }

    /**
     * @param integer
     */
    public void setAchBankRoutingNbr(String s) {
        achBankRoutingNbr = s;
    }

    /**
     * @param string
     */
    public void setAdviceEmailAddress(String string) {
        adviceEmailAddress = string;
    }

    /**
     * @param string
     */
    public void setAlternatePayeeId(String string) {
        alternatePayeeId = string;
    }

    /**
     * @param string
     */
    public void setAlternatePayeeIdTypeCd(String string) {
        alternatePayeeIdTypeCd = string;
    }

    /**
     * @param integer
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * @param integer
     */
    public void setBatch(Batch b) {
        batch = b;
    }

    /**
     * @param boolean1
     */
    public void setCampusAddress(Boolean boolean1) {
        campusAddress = boolean1;
    }

    /**
     * @param decimal
     */
    public void setCreditMemoAmount(KualiDecimal decimal) {
        creditMemoAmount = decimal;
    }

    public void setCreditMemoAmount(String decimal) {
        creditMemoAmount = new KualiDecimal(decimal);
    }

    /**
     * @param string
     */
    public void setCreditMemoNbr(String string) {
        creditMemoNbr = string;
    }

    /**
     * @param timestamp
     */
    public void setDisbursementDate(Date timestamp) {
        disbursementDate = timestamp;
    }

    /**
     * This method sets disbursementDate.
     * @param disbursementDate a string representing the disbursementDate
     * @throws ParseException
     */
    public void setDisbursementDate(String disbursementDate) throws ParseException {
        this.disbursementDate = SpringContext.getBean(DateTimeService.class).convertToSqlDate(disbursementDate);
    }

    /**
     * @param integer
     */
    public void setDisbursementNbr(KualiInteger integer) {
        disbursementNbr = integer;
    }

    public void setDisbursementNbr(String integer) {
        disbursementNbr = new KualiInteger(integer);
    }

    /**
     * @param string
     */
    public void setDisbursementType(DisbursementType dt) {
        disbursementType = dt;
    }

    /**
     * @param integer
     */
    public void setId(KualiInteger integer) {
        id = integer;
    }

    /**
     * @param boolean1
     */
    public void setEmployeeIndicator(Boolean boolean1) {
        employeeIndicator = boolean1;
    }

    /**
     * @param string
     */
    public void setLine1Address(String string) {
        line1Address = string;
    }

    /**
     * @param string
     */
    public void setLine2Address(String string) {
        line2Address = string;
    }

    /**
     * @param string
     */
    public void setLine3Address(String string) {
        line3Address = string;
    }

    /**
     * @param string
     */
    public void setLine4Address(String string) {
        line4Address = string;
    }

    /**
     * @param boolean1
     */
    public void setNraPayment(Boolean boolean1) {
        nraPayment = boolean1;
    }

    /**
     * @param string
     */
    public void setPayeeId(String string) {
        payeeId = string;
    }

    /**
     * @param string
     */
    public void setPayeeIdTypeCd(String string) {
        payeeIdTypeCd = string;
    }

    /**
     * @param string
     */
    public void setPayeeName(String string) {
        payeeName = string;
    }

    /**
     * @param string
     */
    public void setPayeeOwnerCd(String string) {
        payeeOwnerCd = string;
    }

    /**
     * @param timestamp
     */
    public void setPaymentDate(Date timestamp) {
        paymentDate = timestamp;
    }

    /**
     * Takes a <code>String</code> and attempt to format as <code>Timestamp</code for setting the
     * paymentDate field
     *
     * @param paymentDate Timestamp as string
     */
    public void setPaymentDate(String paymentDate) throws ParseException {
        this.paymentDate = SpringContext.getBean(DateTimeService.class).convertToSqlDate(paymentDate);
    }

    /**
     * @param string
     */
    public void setPaymentStatus(PaymentStatus stat) {
        paymentStatus = stat;
    }

    /**
     * @param string
     */
    public void setPhysCampusProcessCd(String string) {
        physCampusProcessCd = string;
    }

    /**
     * @param integer
     */
    public void setProcess(PaymentProcess p) {
        if (p != null) {
            processId = p.getId();
        }
        else {
            processId = null;
        }
        this.process = p;
    }

    /**
     * @param boolean1
     */
    public void setProcessImmediate(Boolean boolean1) {
        processImmediate = boolean1;
    }

    /**
     * @param boolean1
     */
    public void setPymtAttachment(Boolean boolean1) {
        pymtAttachment = boolean1;
    }

    /**
     * @param boolean1
     */
    public void setTaxablePayment(Boolean boolean1) {
        taxablePayment = boolean1;
    }

    /**
     * @param string
     */
    public void setZipCd(String string) {
        zipCd = string;
    }

    /**
     * @param string
     */
    public void setPymtSpecialHandling(Boolean pymtSpecialHandling) {
        this.pymtSpecialHandling = pymtSpecialHandling;
    }

    public String toStringKey() {
        StringBuffer buffer = new StringBuffer();
        CustomerProfile customerProfile = batch.getCustomerProfile();

        buffer.append(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE);
        buffer.append("=");
        buffer.append(customerProfile.getChartCode());
        buffer.append(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE);
        buffer.append("=");
        buffer.append(customerProfile.getUnitCode());
        buffer.append(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE);
        buffer.append("=");
        buffer.append(customerProfile.getSubUnitCode());
        buffer.append(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_NAME);
        buffer.append("=");
        buffer.append(payeeName);
        buffer.append(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_LINE1_ADDRESS);
        buffer.append("=");
        buffer.append(line1Address);
        buffer.append(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_ID);
        buffer.append("=");
        buffer.append(payeeId);
        buffer.append(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_ID_TYPE_CODE);
        buffer.append("=");
        buffer.append(payeeIdTypeCd);
        buffer.append(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BANK_CODE);
        buffer.append("=");
        buffer.append(bankCode);

        return buffer.toString();
    }

    /**
     * @return Returns the achAccountType.
     */
    public String getAchAccountType() {
        return achAccountType;
    }

    /**
     * @param achAccountType The achAccountType to set.
     */
    public void setAchAccountType(String achAccountType) {
        this.achAccountType = achAccountType;
    }

    public Timestamp getEpicPaymentCancelledExtractedDate() {
        return epicPaymentCancelledExtractedDate;
    }

    public void setEpicPaymentCancelledExtractedDate(Timestamp epicPaymentCancelledExtractedDate) {
        this.epicPaymentCancelledExtractedDate = epicPaymentCancelledExtractedDate;
    }

    public Timestamp getEpicPaymentPaidExtractedDate() {
        return epicPaymentPaidExtractedDate;
    }

    public void setEpicPaymentPaidExtractedDate(Timestamp epicPaymentPaidExtractedDate) {
        this.epicPaymentPaidExtractedDate = epicPaymentPaidExtractedDate;
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


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }

    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    public KualiInteger getProcessId() {
        return processId;
    }

    public void setProcessId(KualiInteger processId) {
        this.processId = processId;
    }

    public void setPaymentStatusCode(String paymentStatusCode) {
        this.paymentStatusCode = paymentStatusCode;
    }

    public void setId_type(String idType) {
        this.payeeIdTypeCd = idType;
    }

    /**
     * Gets the adviceEmailSentDate attribute.
     *
     * @return Returns the adviceEmailSentDate.
     */
    public Timestamp getAdviceEmailSentDate() {
        return adviceEmailSentDate;
    }

    /**
     * Sets the adviceEmailSentDate attribute value.
     *
     * @param adviceEmailSentDate The adviceEmailSentDate to set.
     */
    public void setAdviceEmailSentDate(Timestamp adviceEmailSentDate) {
        this.adviceEmailSentDate = adviceEmailSentDate;
    }

    /**
     * This method gets a string representation of the address lines
     * @return the street as a combined representation of the address lines
     */
    public String getStreet() {
        StringBuffer street = new StringBuffer();

        street.append(StringUtils.isNotBlank(line1Address) ? (line1Address + KFSConstants.NEWLINE) : KFSConstants.EMPTY_STRING);
        street.append(StringUtils.isNotBlank(line2Address) ? (line2Address + KFSConstants.NEWLINE) : KFSConstants.EMPTY_STRING);
        street.append(StringUtils.isNotBlank(line3Address) ? (line3Address + KFSConstants.NEWLINE) : KFSConstants.EMPTY_STRING);
        street.append(StringUtils.isNotBlank(line4Address) ? (line4Address + KFSConstants.NEWLINE) : KFSConstants.EMPTY_STRING);

        return street.toString();
    }

    /**
     * This method gets the payeeIdTypeDesc
     * @return the payeeIdTypeDesc
     */
    public String getPayeeIdTypeDesc() {
        String payeeIdTypeCd = getPayeeIdTypeCd();
        List<PayeeType> boList = (List) SpringContext.getBean(KeyValuesService.class).findAll(PayeeType.class);
        for (PayeeType payeeType : boList) {
            if (payeeType.getCode().equalsIgnoreCase(payeeIdTypeCd)) {
                return payeeType.getName();
            }
        }
        return KFSConstants.EMPTY_STRING;
    }
}
