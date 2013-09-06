/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.integration.cab;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.purap.ExternalPurApItem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.bo.DocumentHeader;

public class CapitalAssetBuilderModuleServiceNoOp implements CapitalAssetBuilderModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    @Override
    public boolean doesAccountingLineFailAutomaticPurchaseOrderRules(AccountingLine accountingLine) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public boolean doesDocumentFailAutomaticPurchaseOrderRules(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public boolean doesItemNeedCapitalAsset(String itemTypeCode, List accountingLines) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public List<CapitalAssetBuilderAssetTransactionType> getAllAssetTransactionTypes() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    @Override
    public String getCurrentPurchaseOrderDocumentNumber(String camsDocumentNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    @Override
    public boolean hasCapitalAssetObjectSubType(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public boolean hasCapitalAssetObjectSubType(AccountingLine accountingLine) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public void notifyRouteStatusChange(DocumentHeader documentHeader) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    @Override
    public boolean validateAccountsPayableData(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateAddItemCapitalAssetBusinessRules(ItemCapitalAsset asset) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateAllFieldRequirementsByChart(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation, int index) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateItemCapitalAssetWithErrors(String recurringPaymentTypeCode, ExternalPurApItem item, boolean apoCheck) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validatePurchasingData(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validatePurchasingObjectSubType(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateUpdateCAMSView(AccountingDocument accountingDocumen) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean warningObjectLevelCapital(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    @Override
    public boolean isAssetTypeExisting(String assetTypeCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    //capital accounting lines validations called from fp documents side..
    @Override
    public boolean validateAllCapitalAccountingLinesProcessed(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateTotalAmountMatch(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateCapitlAssetsAmountToAccountingLineAmount(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateCapitalAccountingLines(AccountingDocument accountingDocument) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean markProcessedGLEntryLine(String documentNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public boolean validateAssetTags(AccountingDocument accountingDocumen) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return true;
    }

    @Override
    public void reactivatePretagDetails(List<AssetGlobalDetail> assetGlobalDetailsList) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }
}
