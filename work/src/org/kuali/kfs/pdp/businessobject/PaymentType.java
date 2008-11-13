/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.LinkedHashMap;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 * This class represents a payment type code
 */
public class PaymentType extends PersistableBusinessObjectBase {
    
    private String paymentTypeCode;
    private String description;
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PdpPropertyConstants.PAYMENT_TYPE_CODE, this.paymentTypeCode);
        m.put(KFSPropertyConstants.DESCRIPTION, this.description);
        
        return m;
    }
    
    /**
     * gets the payment type description
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * sets payment type description
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * gets payment type code
     * 
     * @return
     */
    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }
    
    /**
     * sets payment type code
     * 
     * @param paymentTypeCode
     */
    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

}
