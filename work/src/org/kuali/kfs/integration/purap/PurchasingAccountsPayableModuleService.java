/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.integration.purap;

import java.sql.Date;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;


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
     * @param authorId
     * @param noteText
     */
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, String authorId, String noteText);

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
     * @param financialSystemDocumentTypeCode
     * @return
     */
    public boolean isPurchasingBatchDocument(String financialSystemDocumentTypeCode);

    public void handlePurchasingBatchCancels(String documentNumber, String financialSystemDocumentTypeCode, boolean primaryCancel, boolean disbursedPayment);

    public void handlePurchasingBatchPaids(String documentNumber, String financialSystemDocumentTypeCode, Date processDate);

    /**
     * Returns the string value for the b2b that would be used as part of the urls in the
     * Actions url column in vendor search for the "shop" link for B2B vendor. If you
     * don't wish to implement the "shop" feature in KFS then you can set this string to
     * an empty string.
     *
     * @return b2bUrlString
     */
    public String getB2BUrlString();

    /**
     * Calculates the total paid, via payment requests, to all of the requisitions, represented by passed on document numbers
     * !!!NOTE!!! There is no guarantee that every document number passed into this method is actually related to a requisition document; document numbers that do not represent requisitions need to be filtered out
     * @param documentNumbers the document numbers for requisitions to find the total payment requests made for
     * @return the total paid by all of the payment requests
     */
    public KualiDecimal getTotalPaidAmountToRequisitions(List<String> documentNumbers);

}
