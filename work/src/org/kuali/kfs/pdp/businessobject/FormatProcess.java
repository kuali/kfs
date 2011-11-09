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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;

public class FormatProcess extends TimestampedBusinessObjectBase {

    private String physicalCampusProcessCode; // PHYS_CMP_PROC_CD
    private Timestamp beginFormat; // BEG_FMT_TS
    private int paymentProcIdentifier;
    
    private PaymentProcess paymentProcess;

    public PaymentProcess getPaymentProcess() {
        return paymentProcess;
    }

    public void setPaymentProcess(PaymentProcess paymentProcess) {
        this.paymentProcess = paymentProcess;
    }

    public int getPaymentProcIdentifier() {
        return paymentProcIdentifier;
    }

    public void setPaymentProcIdentifier(int paymentProcIdentifier) {
        this.paymentProcIdentifier = paymentProcIdentifier;
    }

    public FormatProcess() {
        super();
    }

    public Timestamp getBeginFormat() {
        return beginFormat;
    }

    public void setBeginFormat(Timestamp beginFormat) {
        this.beginFormat = beginFormat;
    }

    /**
     * @return
     * @hibernate.id column="PHYS_CMP_PROC_CD" length="2" generator-class="assigned"
     */
    public String getPhysicalCampusProcessCode() {
        return physicalCampusProcessCode;
    }

    /**
     * @param string
     */
    public void setPhysicalCampusProcessCode(String string) {
        physicalCampusProcessCode = string;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put(PdpPropertyConstants.PHYS_CAMPUS_PROCESS_CODE, this.physicalCampusProcessCode);
        m.put(PdpPropertyConstants.PAYMENT_PROC_IDENTIFIER, this.paymentProcIdentifier);
        
        return m;
    }

}
