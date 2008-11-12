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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class AssetGlobalAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalAuthorizer.class);
    private static AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);
    private static BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

    /**
     * Hide or set specific fields as non-editable.
     * 
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer#getFieldAuthorizations(org.kuali.rice.kns.document.MaintenanceDocument,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, Person user) {
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        AssetGlobal assetGlobal = (AssetGlobal) document.getNewMaintainableObject().getBusinessObject();

        // "Asset Separate" document functionality
        if (assetGlobalService.isAssetSeparateDocument(assetGlobal)) {
            setAssetGlobalDetailsFieldsReadOnlyAccessMode(auths, user);
            setAssetGlobalPaymentsFieldsReadOnlyAccessMode(assetGlobal, auths, user, false);
        }
        else {
            setAssetGlobalLocationFieldsHidden(assetGlobal, auths, user);
            // If asset global document is created from CAB, disallow add payment to collection.
            boolean allowAddPaymentToCollection = true;
            if (assetGlobal.isCapitalAssetBuilderOriginIndicator()) {
                allowAddPaymentToCollection = false;
            }
            setAssetGlobalPaymentsFieldsReadOnlyAccessMode(assetGlobal, auths, user, allowAddPaymentToCollection);

            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR);
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH);

            if (assetGlobalService.existsInGroup(CamsConstants.AssetGlobal.NON_NEW_ACQUISITION_CODE_GROUP, assetGlobal.getAcquisitionTypeCode())) {
                // Fields in the add section
                auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE);
                auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER);
                auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE);
                auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "." + CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE);

                // Hiding some fields when the status of the document is not final.
                if (!document.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                    setAssetGlobalPaymentsHiddenFields(assetGlobal, auths);
                }
            }

        }
        return auths;
    }

    /**
     * Sets Asset Global Details fields to read only
     * 
     * @param auths
     * @param user
     */
    protected void setAssetGlobalDetailsFieldsReadOnlyAccessMode(MaintenanceDocumentAuthorizations auths, Person user) {
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.AGENCY_NUMBER); // owner
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ACQUISITION_TYPE_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.INVENTORY_STATUS_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.CONDITION_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_DESCRIPTION);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE_CODE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.VENDOR_NAME);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.MANUFACTURER_NAME);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.MANUFACTURER_MODEL_NUMBER);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ORGANIZATION_TEXT);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.REP_USER_AUTH_ID);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAST_INVENTORY_DATE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.CREATE_DATE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE);
        auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_DEPRECIATION_DATE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAND_COUNTRY_NAME);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAND_ACREAGE_SIZE);
        auths.addReadonlyAuthField(CamsPropertyConstants.Asset.LAND_PARCEL_NUMBER);
    }

    /**
     * Hides specific Asset Location fields in relation to the document type.
     * 
     * @param auths
     * @param user
     */
    protected void setAssetGlobalLocationFieldsHidden(AssetGlobal assetGlobal, MaintenanceDocumentAuthorizations auths, Person user) {

        // hide it for the add line
        int i = 0;
        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.REPRESENTATIVE_UNIVERSAL_IDENTIFIER); // representativeUniversalIdentifier
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_TYPE_CODE); // capitalAssetTypeCode
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_DESCRIPTION); // capitalAssetDescription
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_NAME); // manufacturerName
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.ORGANIZATION_TEXT); // organizationText
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_MODEL_NUMBER); // manufacturerModelNumber
            auths.addHiddenAuthField(KFSConstants.ADD_PREFIX + "." + CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "." + CamsPropertyConstants.AssetGlobalDetail.SEPARATE_SOURCE_AMOUNT); // separateSourceAmount
            // (Long)

            // hide it for the existing lines
            int j = 0;
            for (AssetGlobalDetail assetGlobalUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.REPRESENTATIVE_UNIVERSAL_IDENTIFIER); // representativeUniversalIdentifier
                auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_TYPE_CODE); // capitalAssetTypeCode
                auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.CAPITAL_ASSET_DESCRIPTION); // capitalAssetDescription
                auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_NAME); // manufacturerName
                auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.ORGANIZATION_TEXT); // organizationText
                auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.MANUFACTURER_MODEL_NUMBER); // manufacturerModelNumber
                auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS + "[" + j + "]." + CamsPropertyConstants.AssetGlobalDetail.SEPARATE_SOURCE_AMOUNT); // separateSourceAmount
                // (Long)
                j++;
            }

            i++;
        }
    }

    /**
     * Sets Asset Payment Details fields in relation to the document type.
     * 
     * @param auths
     * @param user
     */
    protected void setAssetGlobalPaymentsFieldsReadOnlyAccessMode(AssetGlobal assetGlobal, MaintenanceDocumentAuthorizations auths, Person user, boolean bool) {
        // do not include payment add section within the payment details collection
        MaintainableCollectionDefinition maintCollDef = SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getMaintainableCollection("AssetGlobalMaintenanceDocument", "assetPaymentDetails");
        maintCollDef.setIncludeAddLine(bool);

        // set all payment detail fields to read only
        int i = 0;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.SEQUENCE_NUMBER);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.CHART_OF_ACCOUNTS_CODE);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ACCOUNT_NUMBER);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.SUB_ACCOUNT_NUMBER);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.FINANCIAL_OBJECT_CODE);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.SUB_OBJECT_CODE);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.PROJECT_CODE);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ORGANIZATION_REFERENCE_ID);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.PURCHASE_ORDER);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.REQUISITION_NUMBER);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.AMOUNT);
            auths.addReadonlyAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE);

            i++;
        }
    }

    /**
     * hides the posting year and fiscal month
     * 
     * @param assetGlobal
     * @param auths
     * @param user
     * @param bool
     */
    protected void setAssetGlobalPaymentsHiddenFields(AssetGlobal assetGlobal, MaintenanceDocumentAuthorizations auths) {
        int i = 0;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE);
            auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR);
            auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH);
            auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_NUMBER);
            auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE);
            auths.addHiddenAuthField(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS + "[" + i + "]." + CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE);

            i++;
        }
    }

    /**
     * Checks whether the BA document is active for the year end posting year.
     * 
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public void canInitiate(String documentTypeName, Person user) {
        super.canInitiate(documentTypeName, user);
        KualiForm kualiForm = GlobalVariables.getKualiForm();
        String refreshCaller = kualiForm != null ? kualiForm.getRefreshCaller() : "";
        String acquisitonTypeCode = refreshCaller != null ? StringUtils.substringAfter(refreshCaller, "::") : "";
        Map<String, Object> pkMap = new HashMap<String, Object>();
        pkMap.put(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE, acquisitonTypeCode);

        AssetAcquisitionType assetAcquisitionType = (AssetAcquisitionType) businessObjectService.findByPrimaryKey(AssetAcquisitionType.class, pkMap);
        if (ObjectUtils.isNotNull(assetAcquisitionType) && !assetAcquisitionType.isActive()) {
            throw new DocumentInitiationAuthorizationException(CamsKeyConstants.AssetGlobal.ERROR_INACTIVE_ACQUISITION_TYPE_CODE, new String[] { acquisitonTypeCode, "AssetGlobal" });
        }
    }

}
