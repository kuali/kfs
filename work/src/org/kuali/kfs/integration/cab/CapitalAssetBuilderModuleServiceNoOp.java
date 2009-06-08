/*
 * Copyright 2009 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.purap.ExternalPurApItem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.bo.DocumentHeader;

public class CapitalAssetBuilderModuleServiceNoOp implements CapitalAssetBuilderModuleService {

    private Logger LOG = Logger.getLogger(getClass()); 
    
    public boolean doesAccountingLineFailAutomaticPurchaseOrderRules(AccountingLine accountingLine) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public boolean doesDocumentFailAutomaticPurchaseOrderRules(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public boolean doesItemNeedCapitalAsset(String itemTypeCode, List accountingLines) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public List<CapitalAssetBuilderAssetTransactionType> getAllAssetTransactionTypes() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    public String getCurrentPurchaseOrderDocumentNumber(String camsDocumentNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    public boolean hasCapitalAssetObjectSubType(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public void notifyRouteStatusChange(DocumentHeader documentHeader) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public boolean validateAccountsPayableData(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean validateAddItemCapitalAssetBusinessRules(ItemCapitalAsset asset) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean validateAllFieldRequirementsByChart(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean validateItemCapitalAssetWithErrors(String recurringPaymentTypeCode, ExternalPurApItem item, boolean apoCheck) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean validatePurchasingData(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean validatePurchasingObjectSubType(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean validateUpdateCAMSView(AccountingDocument accountingDocumen) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    public boolean warningObjectLevelCapital(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

}
