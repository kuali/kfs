/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
