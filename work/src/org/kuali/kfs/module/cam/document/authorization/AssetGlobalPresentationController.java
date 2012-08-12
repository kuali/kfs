/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Presentation Controller for Asset Global Maintenance Documents
 */
public class AssetGlobalPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenPropertyNames(businessObject);
        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();
        MaintainableCollectionDefinition maintCollDef = SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getMaintainableCollection("AA", "assetPaymentDetails");
        boolean isAssetSeparate = SpringContext.getBean(AssetGlobalService.class).isAssetSeparate(assetGlobal);

        if (assetGlobal.isCapitalAssetBuilderOriginIndicator() || isAssetSeparate) {
            // do not include payment add section within the payment details collection
            maintCollDef.setIncludeAddLine(false);
            // No update could be made to payment if it's created from CAB. Here, disable delete button if payment already added into
            // collection.
            for (AssetPaymentDetail payment : assetGlobal.getAssetPaymentDetails()) {
                payment.setNewCollectionRecord(false);
            }
        }
        else {
            // conversely allow add during any other case. This is important because the attribute is set on the DD and the DD is
            // only loaded on project startup. Hence setting is important to avoid state related bugs
            maintCollDef.setIncludeAddLine(true);
        }

        AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);
        if (!assetGlobalService.isAssetSeparateByPayment(assetGlobal)) {
            // Show payment sequence number field only if a separate by payment was selected
            fields.add(CamsPropertyConstants.AssetGlobal.SEPERATE_SOURCE_PAYMENT_SEQUENCE_NUMBER);
        }

        if (!isAssetSeparate) {
            fields.addAll(getAssetGlobalLocationHiddenFields(assetGlobal));

            if (assetGlobalService.existsInGroup(assetGlobalService.getNonNewAcquisitionCodeGroup(), assetGlobal.getAcquisitionTypeCode())) {
                // Fields in the add section
                fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE);
                fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER);
                fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE);
                fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE);

                // Hiding some fields when the status of the document is not final.
                if (!document.getDocumentHeader().getWorkflowDocument().isFinal()) {
                    fields.addAll(getAssetGlobalPaymentsHiddenFields(assetGlobal));
                }
            }
        }
        // if no separate asset is added, hide the button
        if (isAssetSeparate && (assetGlobal.getAssetSharedDetails().isEmpty() || assetGlobal.getAssetSharedDetails().get(0).getAssetGlobalUniqueDetails().isEmpty())) {
            fields.add("calculateEqualSourceAmountsButton");
        }



        return fields;
    }

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> fields = super.getConditionallyReadOnlyPropertyNames(document);

        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();

        // "Asset Separate" document functionality
        if (SpringContext.getBean(AssetGlobalService.class).isAssetSeparate(assetGlobal)) {
            fields.addAll(getAssetGlobalDetailsReadOnlyFields());
            fields.addAll(getAssetGlobalPaymentsReadOnlyFields(assetGlobal));
        }
        else if (assetGlobal.isCapitalAssetBuilderOriginIndicator()) {
            // If asset global document is created from CAB, disallow add payment to collection.
            fields.addAll(getAssetGlobalPaymentsReadOnlyFields(assetGlobal));
        }

        // if accounts can't cross charts, then add the extra chartOfAccountsCode field to be displayed readOnly
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            String COA_CODE_NAME = KRADConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
            fields.add(COA_CODE_NAME);
        }

        return fields;
    }

    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> fields = super.getConditionallyHiddenSectionIds(businessObject);

        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();

        // check account period selection is enabled
        // PERFORMANCE: cache this setting - move call to service
        // check accounting period is enabled for doc type in system parameter
        String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        // PERFORMANCE: cache this setting - move call to service
        ParameterEvaluator evaluator = getParameterEvaluatorService().getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
        if (!evaluator.evaluationSucceeds()) {
            fields.add(KFSConstants.ACCOUNTING_PERIOD_TAB_ID);
        }
        
        // hide "Asset Information", "Recalculate Total Amount" tabs if not "Asset Separate" doc
        if (!SpringContext.getBean(AssetGlobalService.class).isAssetSeparate(assetGlobal)) {
            fields.add(CamsConstants.AssetGlobal.SECTION_ID_ASSET_INFORMATION);
            fields.add(CamsConstants.AssetGlobal.SECTION_ID_RECALCULATE_SEPARATE_SOURCE_AMOUNT);
        }

        return fields;
    }

    /**
     * @param assetGlobal
     * @return Asset Location fields with index that are present on AssetGlobal. Includes add line. Useful for hiding them.
     */
    protected Set<String> getAssetGlobalLocationHiddenFields(AssetGlobal assetGlobal) {
        Set<String> fields = new HashSet<String>();

        // hide it for the add line
        int i = 0;
        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.REPRESENTATIVE_UNIVERSAL_IDENTIFIER); // representativeUniversalIdentifier
            fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_TYPE_CODE); // capitalAssetTypeCode
            fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_DESCRIPTION); // capitalAssetDescription
            fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_NAME); // manufacturerName
            fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.ORGANIZATION_TEXT); // organizationText
            fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_MODEL_NUMBER); // manufacturerModelNumber
            fields.add(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.SEPARATE_SOURCE_AMOUNT); // separateSourceAmount
            // (Long)

            // hide it for the existing lines
            int j = 0;
            for (AssetGlobalDetail assetGlobalUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                fields.add(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.REPRESENTATIVE_UNIVERSAL_IDENTIFIER); // representativeUniversalIdentifier
                fields.add(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_TYPE_CODE); // capitalAssetTypeCode
                fields.add(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_DESCRIPTION); // capitalAssetDescription
                fields.add(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_NAME); // manufacturerName
                fields.add(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.ORGANIZATION_TEXT); // organizationText
                fields.add(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_MODEL_NUMBER); // manufacturerModelNumber
                fields.add(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.SEPARATE_SOURCE_AMOUNT); // separateSourceAmount
                // (Long)
                j++;
            }

            i++;
        }

        return fields;
    }

    /**
     * @param assetGlobal
     * @return posting year and fiscal month on every payment. Useful for hiding them.
     */
    protected Set<String> getAssetGlobalPaymentsHiddenFields(AssetGlobal assetGlobal) {
        Set<String> fields = new HashSet<String>();

        int i = 0;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE);
            i++;
        }

        return fields;
    }

    /**
     * @return Asset Global Detail fields that should be read only
     */
    protected Set<String> getAssetGlobalDetailsReadOnlyFields() {
        Set<String> fields = new HashSet<String>();

        fields.add(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE);
        fields.add(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER);
        fields.add(CamsPropertyConstants.Asset.ORGANIZATION_CODE);
        fields.add(CamsPropertyConstants.Asset.AGENCY_NUMBER); // owner
        fields.add(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
        fields.add(CamsPropertyConstants.Asset.INVENTORY_STATUS_CODE);
        fields.add(CamsPropertyConstants.Asset.CONDITION_CODE);
        fields.add(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_DESCRIPTION);
        fields.add(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE);
        fields.add(CamsPropertyConstants.Asset.VENDOR_NAME);
        fields.add(CamsPropertyConstants.Asset.MANUFACTURER_NAME);
        fields.add(CamsPropertyConstants.Asset.MANUFACTURER_MODEL_NUMBER);
        fields.add(CamsPropertyConstants.AssetGlobal.ORGANIZATION_TEXT);
        fields.add(CamsPropertyConstants.Asset.REP_USER_AUTH_ID);
        fields.add(CamsPropertyConstants.Asset.LAST_INVENTORY_DATE);
        fields.add(CamsPropertyConstants.Asset.CREATE_DATE);
        fields.add(CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE);
        fields.add(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_DEPRECIATION_DATE);
        fields.add(CamsPropertyConstants.Asset.LAND_COUNTRY_NAME);
        fields.add(CamsPropertyConstants.Asset.LAND_ACREAGE_SIZE);
        fields.add(CamsPropertyConstants.Asset.LAND_PARCEL_NUMBER);

        return fields;
    }

    /**
     * @param assetGlobal
     * @return Asset Global Payment lines with index that should be set to read only.
     */
    protected Set<String> getAssetGlobalPaymentsReadOnlyFields(AssetGlobal assetGlobal) {
        Set<String> fields = new HashSet<String>();

        // set all payment detail fields to read only.
        int i = 0;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.SEQUENCE_NUMBER);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.CHART_OF_ACCOUNTS_CODE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ORGANIZATION_REFERENCE_ID);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.PURCHASE_ORDER);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.REQUISITION_NUMBER);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.AMOUNT);
            fields.add(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE);

            i++;
        }

        return fields;
    }

    // CSU 6702 BEGIN
    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);

        if (document instanceof LedgerPostingDocument) {
            // check accounting period is enabled for doc type in system parameter
            String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
            ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
            ParameterEvaluator evaluator = parameterEvaluatorService.getParameterEvaluator(KFSConstants.ParameterNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
            if (evaluator.evaluationSucceeds()) {
                documentActions.add(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
            }
        }

        return documentActions;
    }
    // CSU 6702 END

}
