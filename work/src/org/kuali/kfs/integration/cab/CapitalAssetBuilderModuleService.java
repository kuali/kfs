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
package org.kuali.kfs.integration.cab;

import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.purap.ExternalPurApItem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.bo.DocumentHeader;

public interface CapitalAssetBuilderModuleService {

    /**
     * Get current Purchase Order Document number for given CAMS Document Number
     * 
     * @param camsDocumentNumber
     * @return
     */
    String getCurrentPurchaseOrderDocumentNumber(String camsDocumentNumber);

    /**
     * validate the capitalAssetManagementAsset data associated with the given accounting document
     * 
     * @param accountingDocument the given accounting document
     * @param capitalAssetManagementAsset data to be validated
     * @return validation succeeded or errors present
     */
    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation);


    public boolean validatePurchasingData(AccountingDocument accountingDocument);

    public boolean validateAccountsPayableData(AccountingDocument accountingDocument);

    public boolean doesAccountingLineFailAutomaticPurchaseOrderRules(AccountingLine accountingLine);

    public boolean doesDocumentFailAutomaticPurchaseOrderRules(AccountingDocument accountingDocument);

    public boolean doesItemNeedCapitalAsset(String itemTypeCode, List accountingLines);

    public boolean validateUpdateCAMSView(AccountingDocument accountingDocumen);

    public boolean validateAddItemCapitalAssetBusinessRules(ItemCapitalAsset asset);

    public boolean warningObjectLevelCapital(AccountingDocument accountingDocument);

    public boolean validateItemCapitalAssetWithErrors(String recurringPaymentTypeCode, ExternalPurApItem item, boolean apoCheck);

    public List<CapitalAssetBuilderAssetTransactionType> getAllAssetTransactionTypes();

    /**
     * External modules can notify CAB if a document changed its route status. CAB Uses this notification to release records or to
     * update other modules about the changes
     * 
     * @param documentHeader DocumentHeader
     */
    public void notifyRouteStatusChange(DocumentHeader documentHeader);


    /**
     * determine whether there is any object code of the given source accounting lines with a capital asset object sub type
     * 
     * @param accountingLines the given source accounting lines
     * @return true if there is at least one object code of the given source accounting lines with a capital asset object sub type;
     *         otherwise, false
     */
    public boolean hasCapitalAssetObjectSubType(AccountingDocument accountingDocument);
    
    public boolean validateAllFieldRequirementsByChart(AccountingDocument accountingDocument);
    
    public boolean validatePurchasingObjectSubType(AccountingDocument accountingDocument);

}
