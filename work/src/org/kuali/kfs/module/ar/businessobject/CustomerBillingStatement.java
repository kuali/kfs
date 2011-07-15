/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class CustomerBillingStatement extends PersistableBusinessObjectBase {

    private String customerNumber;
    private KualiDecimal previouslyBilledAmount;
    private Date reportedDate;
        
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("CUSTOMERNUMBER", customerNumber);
        return m;
    }

    /**
     * @return the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * @param customerNumber the customerNumber to set
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * @return the previouslyBilledAmount
     */
    public KualiDecimal getPreviouslyBilledAmount() {
        return previouslyBilledAmount;
    }

    /**
     * @param previouslyBilledAmount the previouslyBilledAmount to set
     */
    public void setPreviouslyBilledAmount(KualiDecimal previouslyBilledAmount) {
        this.previouslyBilledAmount = previouslyBilledAmount;
    }

    /**
     * @return the reportedDate
     */
    public Date getReportedDate() {
        return reportedDate;
    }

    /**
     * @param reportedDate the reportedDate to set
     */
    public void setReportedDate(Date reportedDate) {
        this.reportedDate = reportedDate;
    }
   
}
