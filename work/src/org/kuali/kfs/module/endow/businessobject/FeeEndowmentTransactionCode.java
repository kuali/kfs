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
