/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.TransactionArchive;
import org.kuali.kfs.module.endow.businessobject.FeeMethodCodeBase;

/**
 * Business Object for Fee Transaction table.
 */
public class FeeTransaction extends FeeMethodCodeBase {
    private static final Logger LOG = Logger.getLogger(FeeTransaction.class);
    
    private String transactionTypeCode;
    
 //   private TransactionArchive transactionArchive;
    
    /**
     * Default constructor.
     */   
    public FeeTransaction() {
       super();
    }
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, super.getFeeMethodCode());
        m.put(EndowPropertyConstants.FEE_TRANSACTION_TYPE_CODE, this.getTransactionTypeCode());        
        return m;
        
    }
       
    /**
     * This method gets transactionTypeCode
     * 
     * @return transactionTypeCode
     */
    public String getTransactionTypeCode() {
        return transactionTypeCode;
    }

    /**
     * This method sets transactionTypeCode.
     * 
     * @param transactionTypeCode
     */
    public void setTransactionTypeCode(String transactionTypeCode) {
        this.transactionTypeCode = transactionTypeCode;
    }
            
    /**
     * This method gets the transactionArchive.
     * 
     * @return transactionArchive
     */
 //   public TransactionArchive getTransactionArchive() {
 //       return transactionArchive;
 //   }

    /**
     * This method sets the transactionArchive.
     * 
     * @param transactionArchive
     */
 //   public void setTransactionArchive(TransactionArchive transactionArchive) {
 //       this.transactionArchive = transactionArchive;
 //   }
}
