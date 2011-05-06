/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.dataaccess;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApItem;

/**
 * PurApAccounting DAO Interface.
 */
public interface PurApAccountingDao {

    /**
     * Retrieves the accounting lines for a purap item.
     * 
     * @param item - purap item
     * @return - list of accounting lines
     */
    public List getAccountingLinesForItem(PurApItem item);

    /**
     * Deletes the summary accounts by payment request document id.
     * 
     * @param paymentRequestIdentifier - payment request document id
     */
    public void deleteSummaryAccountsbyPaymentRequestIdentifier(Integer paymentRequestIdentifier);
    
    /**
     * Deletes the summary accounts by credit memo document id.
     * 
     * @param creditMemoIdentifier - credit memo document id
     */
    public void deleteSummaryAccountsbyCreditMemoIdentifier(Integer creditMemoIdentifier);
    
}
