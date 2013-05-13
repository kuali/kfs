/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;

/**
 * This defines methods common for all endowment transactional documents.
 */
public interface EndowmentTransactionalDocument extends FinancialSystemTransactionalDocument {

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

    /**
     * Gets the transaction posted flag.
     * 
     * @return true of false depending on whether the transactions on this document have been posted or not
     */
    public boolean isTransactionPosted();

    /**
     * Sets the transaction posted flag.
     * 
     * @param transactionPosted the value to be set
     */
    public void setTransactionPosted(boolean transactionPosted);

    /**
     * This method return true if the document is error corrected.
     * @return
     */
    public boolean isErrorCorrectedDocument();
    
    /**
     * This is a hook into those batch processes that will generate eDocs should call 
     * this method to set the value of noRouteIndicator based on the value of 
     * NO_ROUTE_IND System parameter.
     * 
     * @param noRouteIndicator the value to be set
     */
    public void setNoRouteIndicator(boolean noRouteIndicator);
}
