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
package org.kuali.kfs.module.cab.document.service;

import java.util.List;

import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;


/**
 * This class declares methods used by CAB PurAp Line process
 */
public interface PurApInfoService {
    /**
     * Valid asset number means asset number must exist in Asset table with status active ( a, c, s, u)
     * 
     * @param poId
     * @param capitalAssetSystemTypeCode
     * @param purApItem
     * @return
     */
    List<Long> retrieveValidAssetNumberForLocking(Integer poId, String capitalAssetSystemTypeCode, PurApItem purApItem);

    /**
     * Get the workflow document number for given poId
     * 
     * @param poId
     * @return
     */
    PurchaseOrderDocument getCurrentDocumentForPurchaseOrderIdentifier(Integer poId);

    /**
     * Set Purchasing order email address and contact phone from PurAp.
     * 
     * @param purApLineForm form
     */
    void setPurchaseOrderFromPurAp(PurApLineForm purApLineForm);

    /**
     * Set CAMS transaction type code the user entered in PurAp
     * 
     * @param poId
     */
    void setCamsTransactionFromPurAp(List<PurchasingAccountsPayableDocument> purApDocs);

    /**
     * Set CAB line item information from PurAp PaymentRequestItem or CreditMemoItem.
     * 
     * @param purchasingAccountsPayableItemAsset
     * @param docTypeCode
     */
    void setAccountsPayableItemsFromPurAp(PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset, String docTypeCode);

}
