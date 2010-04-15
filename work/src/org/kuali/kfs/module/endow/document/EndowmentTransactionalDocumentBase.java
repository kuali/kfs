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

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSourceType;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSubType;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;

public abstract class EndowmentTransactionalDocumentBase extends FinancialSystemTransactionalDocumentBase implements EndowmentTransactionalDocument {

    private String transactionSubTypeCode;
    private String transactionSourceTypeCode;
    private boolean transactionPosted;

    private EndowmentTransactionSubType  transactionSubType;
    private EndowmentTransactionSourceType transactionSourceType;

    /**
     * Constructs a EndowmentTransactionalDocumentBase.java.
     */
    public EndowmentTransactionalDocumentBase() {
        super();
        this.transactionPosted=false;
    }

    @Override
    public void prepareForSave() 
    {
        super.prepareForSave();
        //Assign Doc header id to the transaction docs number(END_TRAN_DOC_T.FDOC_NBR)
        setDocumentNumber(getDocumentHeader().getDocumentNumber());
    } 

    public String getTransactionSubTypeCode() {
        return transactionSubTypeCode;
    }

    public void setTransactionSubTypeCode(String transactionSubTypeCode) {
        this.transactionSubTypeCode = transactionSubTypeCode;
    }

    public String getTransactionSourceTypeCode() {
        return transactionSourceTypeCode;
    }

    public void setTransactionSourceTypeCode(String transactionSourceTypeCode) {
        this.transactionSourceTypeCode = transactionSourceTypeCode;
    }

    public boolean isTransactionPosted() {
        return transactionPosted;
    }

    public void setTransactionPosted(boolean transactionPosted) {
        this.transactionPosted = transactionPosted;
    }

    public EndowmentTransactionSubType getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(EndowmentTransactionSubType transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public EndowmentTransactionSourceType getTransactionSourceType() {
        return transactionSourceType;
    }

    public void setTransactionSourceType(EndowmentTransactionSourceType transactionSourceType) {
        this.transactionSourceType = transactionSourceType;
    }
}
