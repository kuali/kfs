/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.integration.purap;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentTypeCode;


/**
 * Methods needed to interface with a Purchasing/Accounts Payable module
 */
public interface PurchasingAccountsPayableModuleService {
    /**
     * Provides the inquiry Url for a purchase order. Used by the PurAp / CAMs document to show user further information about the
     * PO.
     * 
     * @param purchaseOrderNumber
     * @return
     */
    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber);

    /**
     * Adds asset numbers that were created to a Purchase Order that caused the creation.
     * 
     * @param purchaseOrderNumber
     * @param assetNumbers
     * @param authorId
     * @param documentType
     */
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, List<Long> assetNumbers, String authorId, String documentType);

    /**
     * Returns a sensitive data record associated with the given code
     * 
     * @param sensitiveDataCode the code of the sensitive data
     * @return a record of sensitive data information
     */
    public PurchasingAccountsPayableSensitiveData getSensitiveDataByCode(String sensitiveDataCode);

    /**
     * Returns all sensitive data records known to the module
     * 
     * @return a List of all sensitive data known to the module
     */
    public List<PurchasingAccountsPayableSensitiveData> getAllSensitiveDatas();

    /**
     * This method...
     * 
     * @param generalLedgerInputType
     * @return
     */
    public boolean isPurchasingBatchDocument(String generalLedgerInputType);

    public void handlePurchasingBatchCancels(String documentNumber, String generalLedgerInputType, boolean primaryCancel, boolean disbursedPayment);

    public void handlePurchasingBatchPaids(String documentNumber, String generalLedgerInputType, Date processDate);

}
