/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cams;


/**
 * Constants for cams business object property names.
 */
public class CamsPropertyConstants {

    public static class Asset {
        public static final String DOCUMENT_TYPE_CODE = "documentTypeCode";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String PRIMARY_DEPRECIATION_METHOD = "primaryDepreciationMethodCode";
        public static final String SALVAGE_AMOUNT = "salvageAmount";        
        public static final String ASSET_DATE_OF_SERVICE = "capitalAssetInServiceDate";
        public static final String ASSET_RETIREMENT_FISCAL_YEAR = "retirementFiscalYear";
        public static final String ASSET_RETIREMENT_FISCAL_MONTH = "retirementPeriodCode";
        public static final String ASSET_INVENTORY_STATUS = "inventoryStatusCode";
        public static final String ASSET_WARRANTY_WARRANTY_NUMBER = "assetWarranty.warrantyNumber";
        public static final String NATIONAL_STOCK_NUMBER = "nationalStockNumber";
        public static final String GOVERNMENT_TAG_NUMBER = "governmentTagNumber";
        public static final String OLD_TAG_NUMBER = "oldTagNumber";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE = "organizationOwnerChartOfAccountsCode";
        public static final String ORGANIZATION_OWNER_ACCOUNT_NUMBER = "organizationOwnerAccountNumber";
        public static final String VENDOR_NAME = "vendorName";
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static final String CAPITAL_ASSET_DESCRIPTION = "capitalAssetDescription";
        public static final String ASSET_DEPRECIATION_DATE = "depreciationDate";
    }

	public static class AssetHeader {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
    }
    
    public static class AssetObject {
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    }

    public static class AssetPayment {
        public static final String TRANSFER_PAYMENT_CODE = "transferPaymentCode";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String ORIGINATION_CODE = "financialSystemOriginationCode";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String OBJECT_CODE = "financialObjectCode";
        public static final String FINANCIAL_OBJECT = "financialObject";
        public static final String SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String OBJECT_TYPE_CODE = "financialObject.financialObjectTypeCode";
        public static final String PROJECT_CODE = "projectCode";
        public static final String PAYMENT_SEQ_NUMBER = "paymentSequenceNumber";
        public static final String TRANSACTION_DC_CODE = "transactionDebitCreditCode";
        
        public static final String PRIMARY_DEPRECIATION_BASE_AMOUNT = "primaryDepreciationBaseAmount";
        public static final String ACCUMULATED_DEPRECIATION_AMOUNT="accumulatedPrimaryDepreciationAmount";        
        public static final String PREVIOUS_YEAR_DEPRECIATION_AMOUNT="previousYearPrimaryDepreciationAmount";
        public static final String PERIOD_1_DEPRECIATION_AMOUNT="period1Depreciation1Amount";
        public static final String PERIOD_2_DEPRECIATION_AMOUNT="period2Depreciation1Amount";
        public static final String PERIOD_3_DEPRECIATION_AMOUNT="period3Depreciation1Amount";
        public static final String PERIOD_4_DEPRECIATION_AMOUNT="period4Depreciation1Amount";
        public static final String PERIOD_5_DEPRECIATION_AMOUNT="period5Depreciation1Amount";
        public static final String PERIOD_6_DEPRECIATION_AMOUNT="period6Depreciation1Amount";
        public static final String PERIOD_7_DEPRECIATION_AMOUNT="period7Depreciation1Amount";
        public static final String PERIOD_8_DEPRECIATION_AMOUNT="period8Depreciation1Amount";
        public static final String PERIOD_9_DEPRECIATION_AMOUNT="period9Depreciation1Amount";
        public static final String PERIOD_10_DEPRECIATION_AMOUNT="period10Depreciation1Amount";
        public static final String PERIOD_11_DEPRECIATION_AMOUNT="period11Depreciation1Amount";
        public static final String PERIOD_12_DEPRECIATION_AMOUNT="period12Depreciation1Amount";        
    }
    
    public static class AssetType {
        public static final String ASSET_DEPRECIATION_LIFE_LIMIT = "depreciableLifeLimit";
    }

    public static class Pretag {
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String PRETAG_DETAIL_CAMPUS_TAG_NUMBER = "pretagDetail.campusTagNumber";
    }

}
