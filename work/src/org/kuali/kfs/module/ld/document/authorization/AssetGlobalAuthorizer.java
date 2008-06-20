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
package org.kuali.module.cams.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.kfs.authorization.FinancialSystemDocumentActionFlags;
import org.kuali.kfs.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.rules.AssetLocationGlobalRule;

public class AssetGlobalAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetLocationGlobalRule.class);
    
    /**
     * READ ONLY
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer#getFieldAuthorizations(org.kuali.core.document.MaintenanceDocument, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        LOG.info("AssetGlobalAuthorizer.getFieldAuthorizations CALLED....");
        
        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        AssetGlobal assetGlobal = (AssetGlobal)document.getNewMaintainableObject().getBusinessObject();
        
        // get AssetPaymentDetail BO
        //AssetPaymentDetail assetPaymentDetails = (AssetPaymentDetail) assetGlobal.getAssetPaymentDetails();

        //setFieldsReadOnlyAccessMode(auths, user);
        
        LOG.info("AssetGlobalAuthorizer.getFieldAuthorizations FINISHED....");
        return auths;
    }
    
    
    private void setFieldsReadOnlyAccessMode(MaintenanceDocumentAuthorizations auths, UniversalUser user) {
        LOG.info("  AssetGlobalAuthorizer.setFieldsReadOnlyAccessMode CALLED....");
        // Global Details
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS);     // owner
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCT_NUMBER); 
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_CHART);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.VENDOR_NAME);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.INVENTORY_STATUS_CODE);
        
        // Location Details
        //auths.addReadonlyAuthField(CamsPropertyConstants.AssetLocationGlobal.);
        
        // Payment Details
        LOG.info("  AssetGlobalAuthorizer.setFieldsReadOnlyAccessMode paymentDetail = '" + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.CHART_OF_ACCOUNTS_CODE + "'");
        LOG.info("  AssetGlobalAuthorizer.setFieldsReadOnlyAccessMode paymentDetail = '" + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER + "'");
        LOG.info("  AssetGlobalAuthorizer.setFieldsReadOnlyAccessMode paymentDetail = '" + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER + "'");
        
        /*
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.CHART_OF_ACCOUNTS_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.ORGANIZATION_REFERENCE_ID);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.PURCHASE_ORDER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.REQUISITION_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.AMOUNT);
        */
        
        LOG.info("  AssetGlobalAuthorizer.setFieldsReadOnlyAccessMode FINISHED....");
    }
    
    /**
     * BUTTON ACTIONS
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        FinancialSystemDocumentActionFlags actionFlags = super.getDocumentActionFlags(document, user);
        AssetGlobal assetGlobal = (AssetGlobal) document.getDocumentBusinessObject();

        return actionFlags;
    }

}
