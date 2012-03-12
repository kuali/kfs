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
 * Created on Jul 9, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiInteger;

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

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }
  
}
