/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;

/**
 * Business Object for Fee Endowment Transaction.
 */
public class FeeEndowmentTransactionCode extends FeeMethodCodeBase {
    private static final Logger LOG = Logger.getLogger(FeeEndowmentTransactionCode.class);
    
    private String endowmentTransactionCode;
    
    private EndowmentTransactionCode endowmentTransaction;
    
    /**
     * Default constructor.
     */   
    public FeeEndowmentTransactionCode() {
       super();
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, super.getFeeMethodCode());
        m.put(EndowPropertyConstants.FEE_ENDOWMENT_TRANSACTION_CODE, this.getEndowmentTransactionCode());        
        return m;
        
    }
        
    /**
     * This method gets endowmentTransactionCode
     * 
     * @return endowmentTransactionCode
     */
    public String getEndowmentTransactionCode() {
        return endowmentTransactionCode;
    }

    /**
     * This method sets transactionTypeCode.
     * 
     * @param transactionTypeCode
     */
    public void setEndowmentTransactionCode(String endowmentTransactionCode) {
        this.endowmentTransactionCode = endowmentTransactionCode;
    }
        
    /**
     * This method gets the endowmentTransaction.
     * 
     * @return endowmentTransaction
     */
    public EndowmentTransactionCode getEndowmentTransaction() {
        return endowmentTransaction;
    }

    /**
     * This method sets the endowmentTransaction.
     * 
     * @param endowmentTransaction
     */
    public void setEndowmentTransaction(EndowmentTransactionCode endowmentTransaction) {
        this.endowmentTransaction = endowmentTransaction;
    }
}
