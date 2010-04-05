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

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;

/**
 * This defines methods common for all endowment transactional documents.
 */
public interface EndowmentTransactionalDocument extends FinancialSystemTransactionalDocument {

    /**
     * Gets the transaction type code
     * 
     * @return transaction type code
     */
    public String getTransactionTypeCode();

    /**
     * Sets the transaction type code
     * 
     * @param transactionTypeCode
     */
    public void setTransactionTypeCode(String transactionTypeCode);

    /**
     * Gets the transaction sub-type code
     * 
     * @return transaction sub-type code
     */
    public String getTransactionSubTypeCode();

    /**
     * Sets the transaction sub-type code
     * 
     * @param transactionSubTypeCode
     */
    public void setTransactionSubTypeCode(String transactionSubTypeCode);

    /**
     * Gets the transaction source type code
     * 
     * @return transaction source type code
     */
    public String getTransactionSourceTypeCode();

    /**
     * Sets the transaction source type code
     * 
     * @param transactionSourceTypeCode
     */
    public void setTransactionSourceTypeCode(String transactionSourceTypeCode);

}
