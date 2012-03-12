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
/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;

public class PaymentGroupHistory extends TimestampedBusinessObjectBase {

    private KualiInteger id; // PMT_GRP_HIST_ID

    private String changeNoteText; // PMT_CHG_NTE_TXT VARCHAR2 250
    private Person changeUser;
    private String changeUserId; // PMT_CHG_USR_ID VARCHAR2 8
    private Timestamp changeTime; // PMT_CHG_TS DATE 7
    private Date origPaymentDate; // ORIG_PMT_DT DATE 7
    private String origAchBankRouteNbr; // ORIG_ACH_BNK_RTNG_NBR VARCHAR2 17 0
    private String origAdviceEmail; // ORIG_ADV_EMAIL_ADDR VARCHAR2 50
    private KualiInteger origDisburseNbr; // ORIG_DISB_NBR NUMBER 9 0
    private Timestamp origDisburseDate; // ORIG_DISB_TS DATE 7
    private Boolean origProcessImmediate; // ORIG_PROC_IMD_IND VARCHAR2 1
    private Boolean origPmtSpecHandling; // ORIG_PMT_SPCL_HANDLG_IND VARCHAR2 1
    private Boolean pmtCancelExtractStat; // PMT_CNCL_EXTRT_STAT_IND VARCHAR2 1
    private Timestamp pmtCancelExtractDate; // PMT_CNCL_EXTRT_TS

    private String disbursementTypeCode;
    private DisbursementType disbursementType;

    private String origBankCode;
    private Bank bank;

    private String paymentStatusCode;
    private PaymentStatus origPaymentStatus; // ORIG_PMT_STAT_CD VARCHAR2 4

    private KualiInteger processId;
    private PaymentProcess paymentProcess;

    private String paymentChangeCode;
    private PaymentChangeCode paymentChange; // PMT_CHG_CD VARCHAR2 4

    private KualiInteger paymentGroupId;
    private PaymentGroup paymentGroup; // PMT_GRP_ID

    public PaymentGroupHistory() {
        super();
    }

    /**
     * @hibernate.id column="PMT_GRP_HIST_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_GRP_HIST_ID_SEQ"
     * @return
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @hibernate.many-to-one column="PMT_GRP_ID" class="edu.iu.uis.pdp.bo.PaymentGroup"
     * @return Returns the paymentGroup.
     */
    public PaymentGroup getPaymentGroup() {
        return paymentGroup;
    }

    /**
     * @return
     * @hibernate.property column="PMT_CNCL_EXTRT_TS"
     */
    public Timestamp getPmtCancelExtractDate() {
        return pmtCancelExtractDate;
    }

    /**
     * @return
     * @hibernate.many-to-one column="PMT_CHG_CD" class="edu.iu.uis.pdp.bo.PaymentChange"
     */
    public PaymentChangeCode getPaymentChange() {
        return paymentChange;
    }

    /**
     * @return
     * @hibernate.property column="PMT_CHG_NTE_TXT" length="250"
     */
    public String getChangeNoteText() {
        return changeNoteText;
    }

    /**
     * @return
     * @hibernate.property column="PMT_CHG_TS"
     */
    public Timestamp getChangeTime() {
        return changeTime;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_ACH_BNK_RTNG_NBR" length="17"
     */
    public String getOrigAchBankRouteNbr() {
        return origAchBankRouteNbr;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_ADV_EMAIL_ADDR" length="50"
     */
    public String getOrigAdviceEmail() {
        return origAdviceEmail;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_DISB_TS"
     */
    public Timestamp getOrigDisburseDate() {
        return origDisburseDate;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_DISB_NBR"
     */
    public KualiInteger getOrigDisburseNbr() {
        return origDisburseNbr;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_PMT_DT"
     */
    public Date getOrigPaymentDate() {
        return origPaymentDate;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_PMT_SPCL_HANDLG_IND" type="yes_no"
     */
    public Boolean getOrigPmtSpecHandling() {
        return origPmtSpecHandling;
    }

    /**
     * @return
     * @hibernate.many-to-one column="ORIG_PMT_STAT_CD" class="edu.iu.uis.pdp.bo.PaymentStatus"
     */
    public PaymentStatus getOrigPaymentStatus() {
        return origPaymentStatus;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_PROC_IMD_IND" type="yes_no"
     */
    public Boolean getOrigProcessImmediate() {
        return origProcessImmediate;
    }

    /**
     * @return
     * @hibernate.property column="PMT_CNCL_EXTRT_STAT_IND" type="yes_no"
     */
    public Boolean getPmtCancelExtractStat() {
        return pmtCancelExtractStat;
    }

    /**
     * @param string
     */
    public void setPaymentChange(PaymentChangeCode pc) {
        paymentChange = pc;
    }

    /**
     * @param string
     */
    public void setChangeNoteText(String string) {
        changeNoteText = string;
    }

    /**
     * @param timestamp
     */
    public void setChangeTime(Timestamp timestamp) {
        changeTime = timestamp;
    }

    /**
     * @param integer
     */
    public void setId(KualiInteger integer) {
        id = integer;
    }

    /**
     * @param integer
     */
    public void setOrigAchBankRouteNbr(String s) {
        origAchBankRouteNbr = s;
    }

    /**
     * @param string
     */
    public void setOrigAdviceEmail(String string) {
        origAdviceEmail = string;
    }

    /**
     * @param timestamp
     */
    public void setOrigDisburseDate(Timestamp timestamp) {
        origDisburseDate = timestamp;
    }

    /**
     * @param integer
     */
    public void setOrigDisburseNbr(KualiInteger integer) {
        origDisburseNbr = integer;
    }

    /**
     * @param timestamp
     */
    public void setOrigPaymentDate(Date timestamp) {
        origPaymentDate = timestamp;
    }

    /**
     * @param boolean1
     */
    public void setOrigPmtSpecHandling(Boolean boolean1) {
        origPmtSpecHandling = boolean1;
    }

    /**
     * @param string
     */
    public void setOrigPaymentStatus(PaymentStatus ps) {
        origPaymentStatus = ps;
    }

    /**
     * @param boolean1
     */
    public void setOrigProcessImmediate(Boolean boolean1) {
        origProcessImmediate = boolean1;
    }

    /**
     * @param timestamp
     */
    public void setPmtCancelExtractDate(Timestamp timestamp) {
        pmtCancelExtractDate = timestamp;
    }

    /**
     * @param boolean1
     */
    public void setPmtCancelExtractStat(Boolean boolean1) {
        pmtCancelExtractStat = boolean1;
    }

    /**
     * @param paymentGroupId The paymentGroupId to set.
     */
    public void setPaymentGroup(PaymentGroup pd) {
        this.paymentGroup = pd;
    }

    /**
     * @param DisbursementType
     */
    public void setDisbursementType(DisbursementType dt) {
        disbursementType = dt;
    }

    /**
     * @return
     * @hibernate.many-to-one column="ORIG_DISB_TYP_CD" class="edu.iu.uis.pdp.bo.DisbursementType"
     */
    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    /**
     * @param Bank
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * @return
     * @hibernate.many-to-one column="ORIG_BNK_ID" class="edu.iu.uis.pdp.bo.Bank"
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Gets the bankCode attribute.
     * 
     * @return Returns the bankCode.
     */
    public String getOrigBankCode() {
        return origBankCode;
    }

    /**
     * Sets the bankCode attribute value.
     * 
     * @param bankCode The bankCode to set.
     */
    public void setOrigBankCode(String bankCode) {
        this.origBankCode = bankCode;
    }

    /**
     * @param PaymentProcess
     */
    public void setProcess(PaymentProcess ppl) {
        paymentProcess = ppl;
    }

    /**
     * @return
     * @hibernate.many-to-one column="ORIG_PROC_ID" class="edu.iu.uis.pdp.bo.PaymentProcess"
     */
    public PaymentProcess getProcess() {
        return paymentProcess;
    }

    /**
     * @hibernate.property column="PMT_CHG_USR_ID" length="11" not-null="true"
     * @return Returns the changeUserId.
     */
    public String getChangeUserId() {
        return changeUserId;
    }

    /**
     * This method gets the change user.
     * @return the changeUser
     */
    public Person getChangeUser() {
        changeUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(changeUserId, changeUser);
        return changeUser;
    }

    /**
     * This method sets the changeUser.
     * @param changeUser
     */
    public void setChangeUser(Person changeUser) {
        if (changeUser != null) {
            changeUserId = changeUser.getPrincipalId();
        }
        this.changeUser = changeUser;
    }

    /**
     * @param changeUserId The changeUserId to set.
     */
    public void setChangeUserId(String changeUserId) {
        this.changeUserId = changeUserId;
    }

    /**
     * Gets the disbursementTypeCode attribute.
     * 
     * @return Returns the disbursementTypeCode.
     */
    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    /**
     * Sets the disbursementTypeCode attribute value.
     * 
     * @param disbursementTypeCode The disbursementTypeCode to set.
     */
    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    /**
     * Gets the paymentStatusCode attribute.
     * 
     * @return Returns the paymentStatusCode.
     */
    public String getPaymentStatusCode() {
        return paymentStatusCode;
    }

    /**
     * Sets the paymentStatusCode attribute value.
     * 
     * @param paymentStatusCode The paymentStatusCode to set.
     */
    public void setPaymentStatusCode(String paymentStatusCode) {
        this.paymentStatusCode = paymentStatusCode;
    }

    /**
     * Gets the paymentProcess attribute.
     * 
     * @return Returns the paymentProcess.
     */
    public PaymentProcess getPaymentProcess() {
        return paymentProcess;
    }

    /**
     * Sets the paymentProcess attribute value.
     * 
     * @param paymentProcess The paymentProcess to set.
     */
    public void setPaymentProcess(PaymentProcess paymentProcess) {
        this.paymentProcess = paymentProcess;
    }

    /**
     * Gets the paymentChangeCode attribute.
     * 
     * @return Returns the paymentChangeCode.
     */
    public String getPaymentChangeCode() {
        return paymentChangeCode;
    }

    /**
     * Sets the paymentChangeCode attribute value.
     * 
     * @param paymentChangeCode The paymentChangeCode to set.
     */
    public void setPaymentChangeCode(String paymentChangeCode) {
        this.paymentChangeCode = paymentChangeCode;
    }

    /**
     * Gets the paymentGroupId attribute.
     * 
     * @return Returns the paymentGroupId.
     */
    public KualiInteger getPaymentGroupId() {
        return paymentGroupId;
    }

    /**
     * Sets the paymentGroupId attribute value.
     * 
     * @param paymentGroupId The paymentGroupId to set.
     */
    public void setPaymentGroupId(KualiInteger paymentGroupId) {
        this.paymentGroupId = paymentGroupId;
    }

    public void updateUser(org.kuali.rice.kim.api.identity.PersonService userService) {
        Person u = userService.getPerson(changeUserId);
        setChangeUser(u);
    }

    public KualiInteger getProcessId() {
        return processId;
    }

    public void setProcessId(KualiInteger processId) {
        this.processId = processId;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }

}
