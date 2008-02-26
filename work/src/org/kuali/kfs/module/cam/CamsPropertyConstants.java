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
        public static final String DOCUMENT_TYPE_CODE           ="documentTypeCode";
        public static final String CAPITAL_ASSET_NUMBER         ="capitalAssetNumber";
        public static final String PRIMARY_DEPRECIATION_METHOD  ="primaryDepreciationMethodCode";
        public static final String ASSET_DATE_OF_SERVICE        ="capitalAssetInServiceDate";
        public static final String ASSET_RETIREMENT_FISCAL_YEAR ="retirementFiscalYear";
        public static final String ASSET_RETIREMENT_FISCAL_MONTH="retirementPeriodCode";
        public static final String ASSET_INVENTORY_STATUS       ="inventoryStatusCode";
    }

    public static class AssetType {    
        public static final String ASSET_DEPRECIATION_LIFE_LIMIT    ="depreciableLifeLimit";
    }
    
    public static class AssetPayment {
        public static final String PRIMARY_DEPRECIATION_BASE_AMOUNT="primaryDepreciationBaseAmount";        
        public static final String TRANSFER_PAYMENT_CODE            ="transferPaymentCode";
        public static final String CAPITAL_ASSET_NUMBER             ="capitalAssetNumber";
        public static final String ORIGINATION_CODE                 ="financialSystemOriginationCode";
        public static final String ACCOUNT_NUMBER                   ="accountNumber";
        public static final String SUB_ACCOUNT_NUMBER               ="subAccountNumber";
        public static final String OBJECT_CODE                      ="financialObjectCode";
        public static final String SUB_OBJECT_CODE                  ="financialSubObjectCode";
        public static final String OBJECT_TYPE_CODE                 ="financialObject.financialObjectTypeCode";
        public static final String PROJECT_CODE                     ="projectCode";
        public static final String PAYMENT_SEQ_NUMBER               ="paymentSequenceNumber";
        public static final String TRANSACTION_DC_CODE              ="transactionDebitCreditCode";
    }

    public static class AssetHeader {    
        public static final String DOCUMENT_NUMBER     = "documentNumber";
        public static final String CAPITAL_ASSET_NUMBER= "capitalAssetNumber";        
    }
    
    public static class AssetObject {
        public static final String UNIVERSITY_FISCAL_YEAR           ="universityFiscalYear";
    }    
}
