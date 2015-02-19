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
