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
/*
 * Created on Jul 12, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

/**
 *
 */

public class PaymentAccountDetail extends TimestampedBusinessObjectBase {

    private KualiInteger id; // PMT_ACCT_DTL_ID
    private String finChartCode; // FIN_COA_CD
    private String accountNbr; // ACCOUNT_NBR
    private String subAccountNbr; // SUB_ACCT_NBR
    private String finObjectCode; // FIN_OBJECT_CD
    private String finSubObjectCode; // FIN_SUB_OBJ_CD
    private String orgReferenceId; // ORG_REFERENCE_ID
    private String projectCode; // PROJECT_CD
    private KualiDecimal accountNetAmount; // ACCT_NET_AMT

    private KualiInteger paymentDetailId;
    private PaymentDetail paymentDetail; // PMT_DTL_ID

    private List<PaymentAccountHistory> accountHistory = new ArrayList<PaymentAccountHistory>();

    public PaymentAccountDetail() {
        super();
    }

    public List<PaymentAccountHistory> getAccountHistory() {
        return accountHistory;
    }

    public void setAccountHistory(List<PaymentAccountHistory> ah) {
        accountHistory = ah;
    }

    public void addAccountHistory(PaymentAccountHistory pah) {
        pah.setPaymentAccountDetail(this);
        accountHistory.add(pah);
    }

    public void deleteAccountDetail(PaymentAccountHistory pah) {
        accountHistory.remove(pah);
    }

    /**
     * @hibernate.id column="PMT_ACCT_DTL_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_ACCT_DTL_ID_SEQ"
     * @return
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @return
     * @hibernate.many-to-one column="PMT_DTL_ID" class="edu.iu.uis.pdp.bo.PaymentDetail"
     */
    public PaymentDetail getPaymentDetail() {
        return this.paymentDetail;
    }

    /**
     * @return
     * @hibernate.property column="ACCOUNT_NBR" length="7"
     */
    public String getAccountNbr() {
        return accountNbr;
    }

    /**
     * @return
     * @hibernate.property column="ACCT_NET_AMT" length="14"
     */
    public KualiDecimal getAccountNetAmount() {
        return accountNetAmount;
    }

    /**
     * @return
     * @hibernate.property column="FIN_COA_CD" length="2"
     */
    public String getFinChartCode() {
        return finChartCode;
    }

    /**
     * @return
     * @hibernate.property column="FIN_OBJECT_CD" length="4"
     */
    public String getFinObjectCode() {
        return finObjectCode;
    }

    /**
     * @return
     * @hibernate.property column="FIN_SUB_OBJ_CD" length="3"
     */
    public String getFinSubObjectCode() {
        return finSubObjectCode;
    }

    /**
     * @return
     * @hibernate.property column="ORG_REFERENCE_ID" length="8"
     */
    public String getOrgReferenceId() {
        return orgReferenceId;
    }

    /**
     * @return
     * @hibernate.property column="PROJECT_CD" length="10"
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * @return
     * @hibernate.property column="SUB_ACCT_NBR" length="5"
     */
    public String getSubAccountNbr() {
        return subAccountNbr;
    }

    /**
     * @param string
     */
    public void setAccountNbr(String string) {
        accountNbr = string;
    }

    /**
     * @param string
     */
    public void setAccountNetAmount(KualiDecimal bigdecimal) {
        accountNetAmount = bigdecimal;
    }

    /**
     * @param integer
     */
    public void setPaymentDetail(PaymentDetail pd) {
        paymentDetail = pd;
    }

    /**
     * @param string
     */
    public void setFinChartCode(String string) {
        finChartCode = string;
    }

    /**
     * @param string
     */
    public void setFinObjectCode(String string) {
        finObjectCode = string;
    }

    /**
     * @param string
     */
    public void setFinSubObjectCode(String string) {
        finSubObjectCode = string;
    }

    /**
     * @param integer
     */
    public void setId(KualiInteger integer) {
        id = integer;
    }

    /**
     * @param string
     */
    public void setOrgReferenceId(String string) {
        orgReferenceId = string;
    }

    /**
     * @param string
     */
    public void setProjectCode(String string) {
        projectCode = string;
    }

    /**
     * @param string
     */
    public void setSubAccountNbr(String string) {
        subAccountNbr = string;
    }

    /**
     * Gets the paymentDetailId attribute.
     * 
     * @return Returns the paymentDetailId.
     */
    public KualiInteger getPaymentDetailId() {
        return paymentDetailId;
    }

    /**
     * Sets the paymentDetailId attribute value.
     * 
     * @param paymentDetailId The paymentDetailId to set.
     */
    public void setPaymentDetailId(KualiInteger paymentDetailId) {
        this.paymentDetailId = paymentDetailId;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PaymentAccountDetail)) {
            return false;
        }
        PaymentAccountDetail o = (PaymentAccountDetail) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(59, 67).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }
}
