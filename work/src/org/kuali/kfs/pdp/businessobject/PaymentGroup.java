/*
 * Copyright 2007 The Kuali Foundation.
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;

public class PaymentGroup extends TimestampedBusinessObjectBase {
    private static BigDecimal zero = new BigDecimal(0);

    private Integer id; // PMT_GRP_ID
    private String payeeName; // PMT_PAYEE_NM
    private String payeeId; // PAYEE_ID
    private String payeeIdTypeCd; // PAYEE_ID_TYP_CD
    private String alternatePayeeId; // ALTRNT_PAYEE_ID
    private String alternatePayeeIdTypeCd; // ALTRNT_PAYEE_ID_TYP_CD
    private String payeeOwnerCd; // PAYEE_OWNR_CD
    private String customerInstitutionNumber; // CUST_IU_NBR
    private String line1Address; // PMT_LN1_ADDR
    private String line2Address; // PMT_LN2_ADDR
    private String line3Address; // PMT_LN3_ADDR
    private String line4Address; // PMT_LN4_ADDR
    private String city; // PMT_CTY_NM
    private String state; // PMT_ST_NM
    private String country; // PMT_CNTRY_NM
    private String zipCd; // PMT_ZIP_CD
    private Boolean campusAddress; // CMP_ADDR_IND
    private Timestamp paymentDate; // PMT_DT DATE
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
    private BigDecimal creditMemoAmount; // PMT_CRDT_MEMO_AMT
    private Integer disbursementNbr; // DISB_NBR
    private Timestamp disbursementDate; // DISB_TS
    private String physCampusProcessCd; // PHYS_CMP_PROC_CD
    private String sortValue; // PMT_SORT_ORD_VAL
    private String achAccountType; // CUST_ACCT_TYP_CD
    private Timestamp epicPaymentCancelledExtractedDate; // PDP_EPIC_PMT_CNCL_EXTRT_TS
    private Timestamp epicPaymentPaidExtractedDate; // PDP_EPIC_PMT_PD_EXTRT_TS
    private Timestamp adviceEmailSentDate; // ADV_EMAIL_SNT_TS

    private Integer batchId;
    private Batch batch; // PMT_BATCH_ID

    private Integer processId;
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

    public PaymentGroup() {
        super();
    }

    public boolean isDailyReportSpecialHandling() {
        return pymtSpecialHandling.booleanValue() && !processImmediate.booleanValue();
    }

    public boolean isDailyReportAttachment() {
        return !pymtSpecialHandling && !processImmediate && pymtAttachment;
    }

    public String getPaymentStatusCode() {
        return paymentStatusCode;
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

    private boolean booleanValue(Boolean b) {
        boolean bv = false;
        if (b != null) {
            bv = b.booleanValue();
        }
        return bv;
    }
    
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
    public BigDecimal getNetPaymentAmount() {
        BigDecimal amt = new BigDecimal(0);
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

    public void setPaymentDetails(List<PaymentDetail> paymentDetail) {
        this.paymentDetails = paymentDetail;
    }

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

    public void setPaymentGroupHistory(List<PaymentGroupHistory> paymentGroupHistory) {
        this.paymentGroupHistory = paymentGroupHistory;
    }

    public void addPaymentGroupHistory(PaymentGroupHistory pd) {
        pd.setPaymentGroup(this);
        paymentGroupHistory.add(pd);
    }

    public void deletePaymentGroupHistory(PaymentGroupHistory pd) {
        paymentGroupHistory.remove(pd);
    }

    /**
     * @hibernate.id column="PMT_GRP_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_GRP_ID_SEQ"
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * @hibernate.one-to-one class="edu.iu.uis.pdp.bo.AchAccountNumber"
     * @return
     */
    public AchAccountNumber getAchAccountNumber() {
        return achAccountNumber;
    }

    public void setAchAccountNumber(AchAccountNumber aan) {
        this.achAccountNumber = aan;
    }

    public String getSortValue() {
        return sortValue;
    }

    public void setSortValue(int sortGroupId) {
        String defaultSortOrderParameterName = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PdpKeyConstants.DEFAULT_SORT_GROUP_ID_PARAMETER);
        String defaultSortOrderParameterValue = SpringContext.getBean(ParameterService.class).getParameterValue(PaymentGroup.class, defaultSortOrderParameterName);
        
        StringBuffer sb = new StringBuffer();

        sb.append(sortGroupId);

        CustomerProfile cp = this.getBatch().getCustomerProfile();
        sb.append(cp.getChartCode());
        sb.append(getWidthString(4, cp.getOrgCode()));
        sb.append(getWidthString(4, cp.getSubUnitCode()));

        if ( defaultSortOrderParameterValue.equals(String.valueOf(sortGroupId)) ) {
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
    public BigDecimal getCreditMemoAmount() {
        return creditMemoAmount;
    }

    /**
     * @return
     * @hibernate.property column="PMT_CRDT_MEMO_NBR" length="14"
     */
    public String getCreditMemoNbr() {
        return creditMemoNbr;
    }

    public String getCustomerInstitutionNumber() {
        return customerInstitutionNumber;
    }

    /**
     * @return
     * @hibernate.property column="DISB_TS" length="7"
     */
    public Timestamp getDisbursementDate() {
        return disbursementDate;
    }

    /**
     * @return
     * @hibernate.property column="DISB_NBR" length="9"
     */
    public Integer getDisbursementNbr() {
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
    public Timestamp getPaymentDate() {
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
    public void setCreditMemoAmount(BigDecimal decimal) {
        creditMemoAmount = decimal;
    }

    /**
     * @param string
     */
    public void setCreditMemoNbr(String string) {
        creditMemoNbr = string;
    }

    /**
     * @param string
     */
    public void setCustomerInstitutionNumber(String string) {
        customerInstitutionNumber = string;
    }

    /**
     * @param timestamp
     */
    public void setDisbursementDate(Timestamp timestamp) {
        disbursementDate = timestamp;
    }

    /**
     * @param integer
     */
    public void setDisbursementNbr(Integer integer) {
        disbursementNbr = integer;
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
    public void setId(Integer integer) {
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
    public void setPaymentDate(Timestamp timestamp) {
        paymentDate = timestamp;
    }
    
    /**
     * Takes a <code>String</code> and attempt to format as <code>Timestamp</code for setting the
     * paymentDate field
     * 
     * @param paymentDate Timestamp as string
     */
    public void setPaymentDate(String paymentDate) throws ParseException {
        this.paymentDate = new Timestamp(SpringContext.getBean(DateTimeService.class).convertToSqlDate(paymentDate).getTime());
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

    public boolean equals(Object obj) {
        if (!(obj instanceof PaymentGroup)) {
            return false;
        }
        PaymentGroup o = (PaymentGroup) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(61, 67).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
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

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("id", this.id);

        return m;
    }

    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public void setPaymentStatusCode(String paymentStatusCode) {
        this.paymentStatusCode = paymentStatusCode;
    }
    
    public void setId_type(String idType) {
        this.payeeIdTypeCd = idType;
    }
}
