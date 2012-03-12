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
 * Created on Jul 8, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;

public class Batch extends TimestampedBusinessObjectBase {
    private KualiInteger id; 
    private KualiInteger customerId;
    private String paymentFileName; 
    private Timestamp customerFileCreateTimestamp; 
    private KualiInteger paymentCount; 
    private KualiDecimal paymentTotalAmount; 
    private String submiterUserId; 
    private Timestamp fileProcessTimestamp; 
    
    private CustomerProfile customerProfile; 
    private Person submiterUser;
    
    public Batch() {
        super();
    }

    public Timestamp getFileProcessTimestamp() {
        return fileProcessTimestamp;
    }

    /**
     * @return
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @return
     */
    public Timestamp getCustomerFileCreateTimestamp() {
        return customerFileCreateTimestamp;
    }

    /**
     * @return
     */
    public KualiInteger getPaymentCount() {
        return paymentCount;
    }

    /**
     * @return
     */
    public String getPaymentFileName() {
        return paymentFileName;
    }

    /**
     * @return
     */
    public KualiDecimal getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    /**
     * @return
     */
    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    /**
     * @param string
     */
    public void setCustomerFileCreateTimestamp(Timestamp t) {
        customerFileCreateTimestamp = t;
    }

    /**
     * @param timestamp
     */
    public void setFileProcessTimestamp(Timestamp timestamp) {
        fileProcessTimestamp = timestamp;
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
    public void setPaymentCount(KualiInteger integer) {
        paymentCount = integer;
    }

    /**
     * @param string
     */
    public void setPaymentFileName(String string) {
        paymentFileName = string;
    }

    /**
     * @param decimal
     */
    public void setPaymentTotalAmount(KualiDecimal decimal) {
        paymentTotalAmount = decimal;
    }

    /**
     * @param integer
     */
    public void setCustomerProfile(CustomerProfile cp) {
        customerProfile = cp;
    }

    public Person getSubmiterUser() {
        submiterUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(submiterUserId, submiterUser);
        return submiterUser;
    }

    public void setSubmiterUser(Person s) {
        this.submiterUser = s;
    }

    /**
     * @return Returns the submiterUserId.
     */
    public String getSubmiterUserId() {
        return submiterUserId;
    }

    /**
     * @param submiterUserId The submiterUserId to set.
     */
    public void setSubmiterUserId(String submiterUserId) {
        this.submiterUserId = submiterUserId;
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

