/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSourceType;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSubType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public abstract class EndowmentTransactionalDocumentBase extends FinancialSystemTransactionalDocumentBase implements EndowmentTransactionalDocument {

    protected static final String NON_CASH_SUB_TYPE = "N";
    protected static final String CASH_SUB_TYPE = "C";
    
    protected static final String NON_MANUAL_SRC_TYPE = "?"; //Do no know the value yet 
    protected static final String MANUAL_SRC_TYPE = "M";
    
    private String transactionSubTypeCode;
    private String transactionSourceTypeCode;
    private boolean transactionPosted;

    private EndowmentTransactionSubType transactionSubType;
    private EndowmentTransactionSourceType transactionSourceType;

    /**
     * Constructs a EndowmentTransactionalDocumentBase.java.
     */
    public EndowmentTransactionalDocumentBase() {
        super();
        this.transactionPosted = false;
        this.setTransactionSourceTypeCode(EndowConstants.TransactionSourceTypeCode.MANUAL);
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("code", this.getTransactionSourceTypeCode());
        EndowmentTransactionSourceType endowmentTransactionSourceType = (EndowmentTransactionSourceType) businessObjectService.findByPrimaryKey(EndowmentTransactionSourceType.class, primaryKeys);
        this.setTransactionSourceType(endowmentTransactionSourceType);
    }

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#getTransactionSubTypeCode()
     */
    public String getTransactionSubTypeCode() {
        return transactionSubTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#setTransactionSubTypeCode(java.lang.String)
     */
    public void setTransactionSubTypeCode(String transactionSubTypeCode) {
        this.transactionSubTypeCode = transactionSubTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#getTransactionSourceTypeCode()
     */
    public String getTransactionSourceTypeCode() {
        return transactionSourceTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#setTransactionSourceTypeCode(java.lang.String)
     */
    public void setTransactionSourceTypeCode(String transactionSourceTypeCode) {
        this.transactionSourceTypeCode = transactionSourceTypeCode;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#isTransactionPosted()
     */
    public boolean isTransactionPosted() {
        return transactionPosted;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionalDocument#setTransactionPosted(boolean)
     */
    public void setTransactionPosted(boolean transactionPosted) {
        this.transactionPosted = transactionPosted;
    }

    /**
     * Gets the transactionSubType.
     * 
     * @return transactionSubType
     */
    public EndowmentTransactionSubType getTransactionSubType() {
        return transactionSubType;
    }

    /**
     * Sets the transactionSubType.
     * 
     * @param transactionSubType
     */
    public void setTransactionSubType(EndowmentTransactionSubType transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    /**
     * Gets the transactionSourceType.
     * 
     * @return transactionSourceType
     */
    public EndowmentTransactionSourceType getTransactionSourceType() {
        return transactionSourceType;
    }

    /**
     * Sets the transactionSourceType.
     * 
     * @param transactionSourceType
     */
    public void setTransactionSourceType(EndowmentTransactionSourceType transactionSourceType) {
        this.transactionSourceType = transactionSourceType;
    }
}
