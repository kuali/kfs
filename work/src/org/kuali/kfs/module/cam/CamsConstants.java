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

import org.kuali.core.authorization.AuthorizationConstants.EditMode;


/**
 * Global constants for CAMS.
 */
public class CamsConstants {
    // public static final String SYSTEM_NAME = "Capital Assets Management System";

    public static final String DEPRECIATION_METHOD_SALVAGE_VALUE_CODE = "SV";
    public static final String DEPRECIATION_METHOD_STRAIGHT_LINE_CODE = "SL";
    public static final String TRANSFER_PAYMENT_CODE_N = "N";
    public static final String TRANSFER_PAYMENT_CODE_Y = "Y";
    public static final String ASSET_LOOKUPABLE_ID = "assetLookupable";

    public static class AssetActions {
        public static final String LOAN = "loan";
        public static final String LOAN_RETURN = "return";
        public static final String LOAN_RENEW = "renew";
        public static final String LOAN_TYPE = "loanType";
        public static final String MERGE = "merge";
        public static final String PAYMENT = "payment";
        public static final String RETIRE = "retire";
        public static final String SEPARATE = "separate";
        public static final String TRANSFER = "transfer";
    }

    public static class DocumentTypeCodes {
        public static final String ASSET_SEPERATE = "ASEP";
    }

    public static final String PRE_ASSET_TAGGING_FILE_TYPE_INDENTIFIER = "preAssetTaggingFileType";

    public static class Parameters {
        public static final String DEPRECIATION_RUN_DATE_PARAMETER = "DEPRECIATION_RUN_DATE";
        public static final String DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES = "DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPE";
        public static final String DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES = "DEPRECIATION_ORGANIZATON_PLANT_FUND_OBJECT_SUB_TYPE";
        public static final String NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES = "NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES";
        public static final String NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES = "NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES";
        public static final String DEPARTMENT_VIEWABLE_FIELDS = "DEPARTMENT_VIEWABLE_FIELDS";
        public static final String FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES = "FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES";
        public static final String VALID_INVENTROY_STATUS_CODE_CHANGE = "VALID_INVENTORY_STATUS_CODES_BY_PRIOR_INVENTORY_STATUS_CODE";
        public static final String INVALID_INVENTROY_STATUS_CODE_CHANGE = "INVALID_INVENTORY_STATUS_CODES_BY_PRIOR_INVENTORY_STATUS_CODE";
        public static final String CAPITAL_ASSET_STATUS_CODES = "CAPITAL_ASSET_STATUS_CODES";
        public static final String RETIRED_STATUS_CODES = "RETIRED_STATUS_CODES";
        public static final String MERGE_SEPARATE_VIEWABLE_FIELDS = "MERGE_SEPARATE_VIEWABLE_FIELDS";
        public static final String MERGE_SEPARATE_EDITABLE_FIELDS = "MERGE_SEPARATE_EDITABLE_FIELDS";
        public static final String DEPARTMENT_EDITABLE_FIELDS = "DEPARTMENT_EDITABLE_FIELDS";

        // Indiana university parameter
        public static final String OBJECT_SUB_TYPE_GROUPS = "OBJECT_SUB_TYPE_GROUPS";

    }

    public static class Report {
        public static final String REPORT_EXTENSION = "PDF";
        public static final String FILE_PREFIX = "CAMS";
    }

    public static class Depreciation {
        public static final String DEPRECIATION_ORIGINATION_CODE = "01"; // 01 -> Transaction Processing
        public static final String TRANSACTION_DESCRIPTION = "Batch Depreciation Asset ";
        public static final String DOCUMENT_DESCRIPTION = "Batch Depreciation Entry";
        public static final String REPORT_FILE_NAME = "DEPRECIATION_REPORT";
        public static final String DEPRECIATION_REPORT_TITLE = "Asset Depreciation Report - Statistics";
        public static final String DEPRECIATION_BATCH = "DEPRECIATION BATCH - ";
    }

    public static class NotPendingDocumentStatuses {
        public static final String APPROVED = "A";
        public static final String CANCELED = "C";
    }

    public static final String[] MONTHS = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };


    public static class Dispositon {
        public static final String ASSET_MERGE_CODE = "M";
        public static final String ASSET_SEPARATE_CODE = "S";
    }

    public static class AssetPaymentErrors {
        public static final String INFORMATION_TAB_ERRORS = "AssetInfoErrors,document.capitalAssetNumber,document.organizationOwnerChartOfAccountsCode,document.campusCode," + "document.representativeUniversalIdentifier";

    }

    public static class Workgroups {
        public static final String WORKGROUP_CM_ADMINISTRATORS = "CM_ADMINISTRATORS";
        public static final String WORKGROUP_CM_SECURITY_ADMINISTRATORS = "CM_SECURITY_ADMINISTRATORS";
        public static final String WORKGROUP_CM_ADD_PAYMENT_USERS = "CM_ADD_PAYMENT_USERS";
        public static final String WORKGROUP_CM_ASSET_MERGE_SEPARATE_USERS = "CM_ASSET_MERGE_SEPARATE_USERS";
        public static final String WORKGROUP_CM_SUPER_USERS = "CM_SUPER_USERS";
        public static final String WORKGROUP_CM_BARCODE_USERS = "CM_BARCODE_USERS";
        public static final String WORKGROUP_CM_CAPITAL_ASSET_BUILDER_ADMINISTRATORS = "CM_CAPITAL_ASSET_BUILDER_ADMINISTRATORS";
    }

    public static class InventoryStatusCode {
        public static final String CAPITAL_ASSET_ACTIVE_IDENTIFIABLE = "A";
        public static final String CAPITAL_ASSET_ACTIVE_NON_ACCESSIBLE = "C";
        public static final String CAPITAL_ASSET_UNDER_CONSTRUCTION = "U";
        public static final String CAPITAL_ASSET_SURPLUS_EQUIPEMENT = "S";
        public static final String CAPITAL_ASSET_RETIRED = "R";
        public static final String NON_CAPITAL_ASSET_ACTIVE = "N";
        public static final String NON_CAPITAL_ASSET_RETIRED = "O";
        public static final String NON_CAPITAL_ASSET_ACTIVE_2003 = "D";
        public static final String NON_CAPITAL_ASSET_RETIRED_2003 = "E";
    }

    public static final String NON_TAGGABLE_ASSET = "N";

    public static class AssetLocationTypeCode {
        public static final String OFF_CAMPUS = "O";
        public static final String BORROWER = "B";
        public static final String BORROWER_STORAGE = "BS";
    }

    public static final String RETIREMENT_REASON_CODE_M = "M";
    public static final String GL_BALANCE_TYPE_CDE_AC = "AC";

    public static class AssetRetirementGlobal {
        public static final String DOCUMENT_HEADER = "documentHeader";
        public static final String SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT = "ExternalTransferOrGift";
        public static final String SECTION_ID_AUCTION_OR_SOLD = "AuctionOrSold";
        public static final String SECTION_ID_THEFT = "Theft";
        public static final String FIS_DOC_TYPE_CODE = "AR";
        public static final String LINE_DESCRIPTION_PLANT_FUND_FOR_FMS = "Plant Fund for FMS";
        public static final String LINE_DESCRIPTION_ACCUMULATED_DEPRECIATION = "Accumulated Depreciation";
        public static final String LINE_DESCRIPTION_GAIN_LOSS_DISPOSITION = "Gain/Loss Disposition of Assets";
    }

    public static class AssetRetirementReasonCodeGroup {
        // TODO: system parameters??
        public static final String ALLOWED_FOR_ANY_KUALI_USERS = "1;2;;3;4;5;6;7;8;9;A";
        public static final String ALLOWED_FOR_CM_ASSET_MERGE_SEPARATE_USERS = "M;S";
        public static final String DISALLOWED_FOR_CM_ADD_PAYMENT_USERS = "R";
        public static final String DISALLOWED_FOR_ANY_USERS = "B;D;F;S";
        public static final String SINGLE_RETIRED_ASSET = "1;2;;3;4;5;6;7;8;9;A";
    }

    public static class AssetRetirementReasonCode {
        public static final String SOLD = "1";
        public static final String GIFT = "6";
        public static final String THEFT = "7";
        public static final String EXTERNAL_TRANSFER = "9";
        public static final String AUCTION = "A";
        public static final String MERGED = "M";
    }

    public static class RetirementLabel {
        public static final String BUYER_DESCRIPTION = "Buyer Description";
        public static final String SALE_PRICE = "Sale Price";
        public static final String RETIREMENT_INSTITUTION_NAME = "Retirement Institution Name";
        public static final String PAID_CASE_NUMBER = "Police Case Number";
    }

    public static class Asset {
        public static final String SECTION_ID_FABRICATION_INFORMATION = "fabricationInformation";
        public static final String SECTION_ID_LAND_INFORMATION = "landInformation";
        public static final String SECTION_ID_PAYMENT_INFORMATION = "paymentInformation";
        public static final String SECTION_ID_DEPRECIATION_INFORMATION = "depreciationInformation";
        public static final String SECTION_ID_HISTORY = "history";
        public static final String SECTION_ID_RETIREMENT_INFORMATION = "retirementInformation";
        public static final String SECTION_ID_EQUIPMENT_LOAN_INFORMATION = "equipmentLoanInformation";
        public static final String SECTION_ID_WARRENTY = "warrenty";
        public static final String SECTION_ID_REPAIR_HISTORY = "repairHistory";
        public static final String SECTION_ID_COMPONENTS = "components";
        public static final String SECTION_ID_LOAN_INFORMATION = "equipmentLoanInformation";

        public static final String[] EDIT_DETAIL_INFORMATION_FIELDS = new String[] { CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsPropertyConstants.Asset.GOVERNMENT_TAG_NUMBER, CamsPropertyConstants.Asset.NATIONAL_STOCK_NUMBER, CamsPropertyConstants.Asset.MANUFACTURER_NAME, CamsPropertyConstants.Asset.MANUFACTURER_MODEL_NUMBER, CamsPropertyConstants.Asset.FINANCIAL_DOCUMENT_POSTING_PERIOD_CODE, CamsPropertyConstants.Asset.FINANCIAL_DOCUMENT_POSTING_YEAR, CamsPropertyConstants.Asset.SERIAL_NUMBER, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsPropertyConstants.Asset.OLD_TAG_NUMBER, CamsPropertyConstants.Asset.LAST_INVENTORY_DATE, CamsPropertyConstants.Asset.TOTAL_COST_AMOUNT, CamsPropertyConstants.Asset.FEDERAL_CONTRIBUTION, CamsPropertyConstants.Asset.ASSET_DEPRECIATION_DATE, CamsPropertyConstants.Asset.FINANCIAL_OBJECT_SUB_TYP_CODE };
        public static final String[] EDIT_ORGANIZATION_INFORMATION_FIELDS = new String[] { CamsPropertyConstants.AssetOrganization.ASSET_ORGANIZATION + "." + CamsPropertyConstants.AssetOrganization.ORGANIZATION_TAG_NUMBER, CamsPropertyConstants.Asset.ESTIMATED_SELLING_PRICE, CamsPropertyConstants.Asset.RECEIVE_DATE, CamsPropertyConstants.Asset.REPLACEMENT_AMOUNT };
        public static final String SECTION_ID_MERGE_HISTORY = "mergeHistory";

        public static final String SECTION_TITLE_NO_PAYMENT = "- No payment exists for Capital Asset: ";
    }

    public static final String SET_PERIOD_DEPRECIATION_AMOUNT_REGEX = "setperiod\\d.*depreciation\\damount";
    public static final String CPTLAST_NBR_SEQ = "CPTLAST_NBR_SEQ";
    public static final String CONDITION_CODE_E = "E";
    public static final String ACQUISITION_TYPE_CODE_C = "C";
    public static final String VENDOR_NAME_CONSTRUCTED = "CONSTRUCTED";
    public static final String ASSET_TYPE_1000 = "1000";
    public static final String ASSET_TYPE_40000 = "40000";

    // TODO: replaced by System parameter
    public static class DepreciationConvention {
        public static final String CREATE_DATE = "CD";
        public static final String HALF_YEAR = "HY";
        public static final String FULL_YEAR = "FY";
    }


    public static final String ASSET_PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION = "AssetPaymentDifferentObjectSubTypeQuestion";

    public static class BarCodeInventory {
        public static final String FILE_TYPE_INDENTIFIER = "assetBarCodeInventoryInputFileType";
        public static final String DATA_FILE_TYPE = "csv";
        public static final String DATA_FILE_EXTENSION = ".csv";
        public static final String DONE_FILE_EXTENSION = ".done";
    }


    public static class EquipmentLoanOrReturnEditMode extends EditMode {
        public static final String DISPLAY_NEW_LOAN_TAB = "displayNewLoanTab";
    }

    public static final String ASSET_TRANSFER_DOCTYPE_CD = "AT";
    public final static String CAMS_GENERAL_LEDGER_POSTING_HELPER_BEAN_ID = "camsGeneralLedgerPendingEntryGenerationProcess";

    public static final String DOCUMENT_NUMBER_PATH = "documentNumber";
    public static final String DOCUMENT_PATH = "document";
    public static final String DOC_HEADER_PATH = DOCUMENT_PATH + "." + DOCUMENT_NUMBER_PATH;
    public static final String ASSET_LOAN_CONFIRM_QN_ID = "AssetTransferLoanConfirmation";
    
    public static class AssetGlobal {
        public static final String CAPITAL_OBJECT_ACCQUISITION_CODE_GROUP = "C;F;G;N;P;S;T";
        public static final String ADD_ASSET_ACQUISITION_CODE_GROUP = "G;T;S;F";
        public static final String ADD_ASSET_DOCUMENT_TYPE_CODE = "AA";
        public static final String ACQUISITION_TYPE_CODE_N = "N";
        public static final String NEW_CAPITAL_ASSET_DOCUMENT_TYPE_CODE_FOR = "PREQ";
        public static final String NON_CAPITAL_ASSET_DOCUMENT_TYPE_CODE = "AA;PREQ";
    }
}
