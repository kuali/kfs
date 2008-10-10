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
 * Created on Jul 9, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * 
 */

public class PaymentNoteText extends TimestampedBusinessObjectBase {

    private KualiInteger id; // PMT_NTE_ID

    private KualiInteger paymentDetailId;
    private PaymentDetail paymentDetail; // PMT_DTL_ID

    private KualiInteger customerNoteLineNbr; // CUST_NTE_LN_NBR
    private String customerNoteText; // CUST_NTE_TXT
    
    public PaymentNoteText() {
        super();
    }

    /**
     * @hibernate.id column="PMT_NTE_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_NTE_ID_SEQ"
     * @return Returns the paymentNoteId.
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @param paymentNoteId The paymentNoteId to set.
     */
    public void setId(KualiInteger paymentNoteId) {
        this.id = paymentNoteId;
    }

    /**
     * @return
     * @hibernate.property column="CUST_NTE_LN_NBR" not-null="true"
     */
    public KualiInteger getCustomerNoteLineNbr() {
        return customerNoteLineNbr;
    }

    /**
     * @return
     * @hibernate.property column="CUST_NTE_TXT" length="60" not-null="true"
     */
    public String getCustomerNoteText() {
        return customerNoteText;
    }

    /**
     * @return
     * @hibernate.many-to-one column="PMT_DTL_ID" class="edu.iu.uis.pdp.bo.PaymentDetail"
     */
    public PaymentDetail getPaymentDetail() {
        return paymentDetail;
    }

    /**
     * @param integer
     */
    public void setCustomerNoteLineNbr(KualiInteger integer) {
        customerNoteLineNbr = integer;
    }

    /**
     * @param string
     */
    public void setCustomerNoteText(String string) {
        customerNoteText = string;
    }

    /**
     * @param integer
     */
    public void setPaymentDetail(PaymentDetail pd) {
        paymentDetail = pd;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PaymentNoteText)) {
            return false;
        }
        PaymentNoteText o = (PaymentNoteText) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 7).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }
  
}
