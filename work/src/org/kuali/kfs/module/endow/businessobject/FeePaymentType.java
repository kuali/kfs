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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;

/**
 * Business Object for Fee Payment Type.
 */
public class FeePaymentType extends FeeMethodCodeBase {
    private static final Logger LOG = Logger.getLogger(FeePaymentType.class);
    
    private String paymentTypeCode;
    
    private PaymentTypeCode paymentType;
    
    /**
     * Default constructor.
     */   
    public FeePaymentType() {
       super();
    }    
   
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, super.getFeeMethodCode());
        m.put(EndowPropertyConstants.FEE_PAYMENT_TYPE_CODE, this.getPaymentTypeCode());        
        return m;
        
    }

    /**
     * This method gets paymentType
     * 
     * @return paymentType
     */
    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    /**
     * This method sets paymentType.
     * 
     * @param paymentType
     */
    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }
            
    /**
     * This method gets the paymentType.
     * 
     * @return paymentType
     */
    public PaymentTypeCode getPaymentType() {
        return paymentType;
    }

    /**
     * This method sets the paymentType.
     * 
     * @param paymentType
     */
    public void setPaymentType(PaymentTypeCode paymentType) {
        this.paymentType = paymentType;
    }
}
