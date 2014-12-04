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
