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
