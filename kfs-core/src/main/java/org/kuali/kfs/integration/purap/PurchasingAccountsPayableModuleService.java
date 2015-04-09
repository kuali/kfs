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
