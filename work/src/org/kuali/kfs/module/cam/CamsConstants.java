/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cam;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Global constants for CAMS.
 */
public class CamsConstants {
    public static final String CAM_MODULE_CODE = "KFS-CAM";
    public static final String[] MONTHS = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
    public static final String SET_PERIOD_DEPRECIATION_AMOUNT_REGEX = "setperiod\\d.*depreciation\\damount";
    public static final String GET_PERIOD_DEPRECIATION_AMOUNT_REGEX = "getperiod\\d.*depreciation\\damount";
    public static final String CPTLAST_NBR_SEQ = "CPTLAST_NBR_SEQ";
    public static final String DOCUMENT_NUMBER_PATH = "documentNumber";
    public static final String DOCUMENT_PATH = "document";
    public static final String DOC_HEADER_PATH = DOCUMENT_PATH + "." + DOCUMENT_NUMBER_PATH;
    public static final String INQUIRY_URL = "inquiry.do";
    public static final Currency CURRENCY_USD = Currency.getInstance("USD");
    public static final String LOCATION_INFORMATION_SECTION_ID = "Location Information";

    public static class DateFormats {
        public static final String MONTH_DAY_YEAR = "MM/dd/yyyy";
        public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";
        public static final String MILITARY_TIME = "HH:mm:ss";
        public static final String STANDARD_TIME = "hh:mm:ss";
        public static final String YEAR_MONTH_DAY_NO_DELIMITER = "yyyyMMdd";
        public static final String MILITARY_TIME_NO_DELIMITER = "HHmmss";
    }

    public static class PermissionNames {
        public static final String ADD_NEGATIVE_PAYMENTS = "Add Negative Payments";
        public static final String RETIRE_MULTIPLE = "Retire Multiple";
        public static final String OVERRIDE_CAPITALIZATION_LIMIT_AMOUNT = "Override CAPITALIZATION_LIMIT_AMOUNT";
        public static final String RETIRE_NON_MOVABLE_ASSETS = "Retire Non-Movable Assets";
        public static final String TRANSFER_NON_MOVABLE_ASSETS = "Transfer Non-Movable Assets";
        public static final String USE_ACQUISITION_TYPE_NEW = "Use Acquisition Type \"New\"";
        public static final String RAZE = "Raze";
        public static final String MERGE = "Merge";
        public static final String SEPARATE = "Separate";
        public static final String USE_RESTRICTED_RETIREMENT_REASON = "Use Restricted Retirement Reason";
        public static final String EDIT_WHEN_TAGGED_PRIOR_FISCAL_YEAR = "Edit When Tagged Prior Fiscal Year";
        public static final String MAINTAIN_ASSET_LOCATION = "Maintain Asset Location";
    }

    public static class RouteLevelNames {
        public static final String EXTERNAL_TRANSFER = "ExternalTransfer";
        public static final String PURCHASING = "Purchasing";
        public static final String MANAGEMENT = "Management";
        public static final String PLANT_FUND = "PlantFund";
        public static final String BORROWER = "Borrower";
    }

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
        public static final String VIEW = "view";
    }

    public static class StrutsActions {
        public static final String ONE_UP = "../";
        public static final String TRANSFER = "camsAssetTransfer.do";
        public static final String EQUIPMENT_LOAN_OR_RETURN = "camsEquipmentLoanOrReturn.do";
        public static final String PAYMENT = "camsAssetPayment.do";
    }

    public static class DocumentTypeName {
        // CAM doc
        public static final String ASSET_DEPRECIATION = "DEPR";
        public static final String ASSET_TRANSFER = "AT";
        public static final String ASSET_EQUIPMENT_LOAN_OR_RETURN = "ELR";
        public static final String ASSET_PAYMENT = "MPAY";
        public static final String ASSET_RETIREMENT_GLOBAL = "ARG";
        public static final String ASSET_ADD_GLOBAL = "AA";
        public static final String ASSET_EDIT = "CASM";
        public static final String ASSET_FABRICATION = "FR";
        public static final String ASSET_LOCATION_GLOBAL = "ALOC";
        public static final String ASSET_BARCODE_INVENTORY_ERROR = "BCIE";
        // Below docTypes will be used for locking purposed only
        public static final String ASSET_SEPARATE = "ASEP";
        public static final String ASSET_PAYMENT_FROM_CAB = "MPAYCAB";
        public static final String ASSET_FP_INQUIRY = "AFPINQ";
        public static final String ASSET_PREQ_INQUIRY = "APREQINQ";
        // Below used for view related document links
        public static final String COMPLEX_MAINTENANCE_DOC_BASE = "CAMM";
    }

    public static class PaymentDocumentTypeCodes {
        // These are the document type codes that payments are saved under when they are modified by assets. Note that not
        // necessarily the documents themselves use these document type codes.
        public static final String ASSET_GLOBAL_SEPARATE = "ASEP";
        public static final String ASSET_RETIREMENT_MERGE = "AMRG";
    }

    public static class Parameters {
        public static final String DEPRECIATION_RUN_DATE_PARAMETER = "DEPRECIATION_RUN_DATE";
        public static final String DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES = "DEPRECIATION_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPE";
        public static final String DEPRECIATION_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES = "DEPRECIATION_ORGANIZATON_PLANT_FUND_OBJECT_SUB_TYPE";
        public static final String NEW_IN_SERVICE_ASSET_DEPRECIATION_START_DATE = "NEW_IN_SERVICE_ASSET_DEPRECIATION_START_DATE";
        public static final String NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES = "NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES";
        public static final String NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES = "NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES";
        public static final String FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES = "FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES";
        public static final String FEDERAL_OWNED_OBJECT_SUB_TYPES = "FEDERAL_OWNED_OBJECT_SUB_TYPES";
        public static final String VALID_INVENTROY_STATUS_CODE_CHANGE = "VALID_ASSET_STATUS_BY_PRIOR_ASSET_STATUS";
        public static final String INVALID_INVENTROY_STATUS_CODE_CHANGE = "INVALID_ASSET_STATUS_BY_PRIOR_ASSET_STATUS";
        public static final String CAPITAL_ASSET_STATUS_CODES = "CAPITAL_ASSET_STATUS_CODES";
        public static final String RETIRED_STATUS_CODES = "RETIRED_STATUS_CODES";
        public static final String EDITABLE_FIELDS_WHEN_TAGGED_PRIOR_FISCAL_YEAR = "EDITABLE_FIELDS_WHEN_TAGGED_PRIOR_FISCAL_YEAR";
        public static final String OBJECT_SUB_TYPE_GROUPS = "OBJECT_SUB_TYPE_GROUPS";
        public static final String INVALID_ASSET_STATUSES_BY_ACQUISITION_TYPE = "INVALID_ASSET_STATUSES_BY_ACQUISITION_TYPE";
        public static final String VALID_ASSET_STATUSES_BY_ACQUISITION_TYPE = "VALID_ASSET_STATUSES_BY_ACQUISITION_TYPE";
        public static final String INVALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE = "INVALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE";
        public static final String VALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE = "VALID_OBJECT_SUB_TYPES_BY_ACQUISITION_TYPE";
        public static final String MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES = "MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES";
        public static final String NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES = "NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES";
        public static final String CAPITALIZATION_LIMIT_AMOUNT = "CAPITALIZATION_LIMIT_AMOUNT";
        public static final String DEFAULT_FABRICATION_ASSET_TYPE_CODE = "DEFAULT_FABRICATION_ASSET_TYPE";
        public static final String DEFAULT_FABRICATION_ASSET_MANUFACTURER = "DEFAULT_FABRICATION_ASSET_MANUFACTURER";
        public static final String DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE = "DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE";
        public static final String MERGE_SEPARATE_RETIREMENT_REASONS = "MERGE_SEPARATE_RETIREMENT_REASONS";
        public static final String RAZE_RETIREMENT_REASONS = "RAZE_RETIREMENT_REASONS";
        public static final String CAPITAL_OBJECT_SUB_TYPES = "CAPITAL_OBJECT_SUB_TYPES";
        public static final String BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS = "IGNORES_LOCKS_IND";
        public static final String MAX_NUMBER_OF_RECORDS_PER_DOCUMENT = "MAX_NUMBER_OF_RECORDS_PER_DOCUMENT";
        public static final String FISCAL_YEAR_END_MONTH_AND_DAY = "FISCAL_YEAR_END_MONTH_AND_DAY";
        public static final String BLANK_OUT_BEGIN_MMDD = "BLANK_OUT_BEGIN_MMDD";
        public static final String BLANK_OUT_PERIOD_RUN_DATE = "BLANK_OUT_PERIOD_RUN_DATE";
        public static final String RUN_DATE_NOTIFICATION_EMAIL_ADDRESSES = "RUN_DATE_NOTIFICATION_EMAIL_ADDRESSES";

        public static final String BLANK_OUT_END_MMDD = "BLANK_OUT_END_MMDD";
        public static final String MANUFACTURER_REQUIRED_FOR_NON_MOVEABLE_ASSET_IND = "MANUFACTURER_REQUIRED_FOR_NON_MOVEABLE_ASSET_IND";
        // CSU 6702 BEGIN
        public static String INCLUDE_RETIRED_ASSETS_IND = "INCLUDE_RETIRED_ASSETS_IND";
        // CSU 6702 BEGIN
    }

    public static class Report {
        public static final String REPORT_EXTENSION = "PDF";
        public static final String FILE_PREFIX = "CAMS";
    }

    public static class Depreciation {
        public static final String TRANSACTION_DESCRIPTION = "Batch Depreciation Asset ";
        public static final String DOCUMENT_DESCRIPTION = "Batch Depreciation Entry";
        public static final String REPORT_FILE_NAME = "DEPRECIATION_REPORT";
        public static final String DEPRECIATION_REPORT_TITLE = "Asset Depreciation Report - Statistics";
        public static final String DEPRECIATION_BATCH = "DEPRECIATION BATCH - ";
        public static final String DEPRECIATION_ALREADY_RAN_MSG = "Batch process already ran for the current depreciation date.";
    }

    public static class BarCodeInventory {
        public static final String FILE_TYPE_INDENTIFIER = "assetBarcodeInventoryInputFileType";
        public static final String DATA_FILE_TYPE = "csv";
        public static final String DATA_FILE_EXTENSION = ".csv";
        public static final String DONE_FILE_EXTENSION = ".done";
        public static final String BCI_MANUALLY_KEYED_CODE = "0";
        public static final String BCI_SCANED_INTO_DEVICE = "1";
    }

    public static class BarCodeInventoryError {
        public static final String STATUS_CODE_ERROR = "E";
        public static final String STATUS_CODE_CORRECTED = "C";
        public static final String STATUS_CODE_DELETED = "D";
        public static final String STATUS_CODE_ERROR_DESCRIPTION = "Error";
        public static final String STATUS_CODE_CORRECTED_DESCRIPTION = "Corrected";
        public static final String STATUS_CODE_DELETED_DESCRIPTION = "Deleted";
        public static final String BAR_CODE_ERROR_DOCUMENT_IGNORES_LOCKS_NO = "N";

        public static final Map<String, String> statusDescription = new HashMap<String, String>();
        static {
            statusDescription.put(STATUS_CODE_CORRECTED, STATUS_CODE_CORRECTED_DESCRIPTION);
            statusDescription.put(STATUS_CODE_DELETED, STATUS_CODE_DELETED_DESCRIPTION);
            statusDescription.put(STATUS_CODE_ERROR, STATUS_CODE_ERROR_DESCRIPTION);
        }
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

    public static class AssetLocationTypeCode {
        public static final String OFF_CAMPUS = "O";
        public static final String BORROWER = "B";
        public static final String BORROWER_STORAGE = "BS";
        public static final String RETIREMENT = "R";
    }

    public static class AssetLocationGlobal {
        public static final String SECTION_ID_EDIT_LIST_OF_ASSETS = "Edit List of Assets";
    }

    public static class AssetRetirementGlobal {
        public static final String ASSET_LOOKUPABLE_ID = "assetLookupable";
        public static final String DOCUMENT_HEADER = "documentHeader";
        public static final String SECTION_ID_ASSET_DETAIL_INFORMATION = "Asset Detail Information";
        public static final String SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT = "ExternalTransferOrGift";
        public static final String SECTION_ID_AUCTION_OR_SOLD = "AuctionOrSold";
        public static final String SECTION_ID_THEFT = "Theft";
        public static final String SECTION_TARGET_ASSET_RETIREMENT_INFO = "Retirement Target Information";
        public static final String DOCUMENT_TYPE_CODE = "ARG";
        public static final String CAPITALIZATION_LINE_DESCRIPTION = "CAPITALIZATION_LINE_DESCRIPTION";
        public static final String ACCUMULATED_DEPRECIATION_LINE_DESCRIPTION = "ACCUMULATED_DEPRECIATION_LINE_DESCRIPTION";
        public static final String OFFSET_AMOUNT_LINE_DESCRIPTION = "OFFSET_AMOUNT_LINE_DESCRIPTION";
        public static final String MERGE_AN_ASSET_DESCRIPTION = "Merge an Asset";
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
        public static final String CASH_RECEIPT_FINANCIAL_DOCUMENT_NUMBER = "Cash Receipt Financial Document Number";
        public static final String RETIREMENT_INSTITUTION_NAME = "Retirement Institution Name";
        public static final String PAID_CASE_NUMBER = "Police Case Number";
    }

    public static class Asset {
        public static final String DEPRECIATION_METHOD_SALVAGE_VALUE_CODE = "SV";
        public static final String DEPRECIATION_METHOD_STRAIGHT_LINE_CODE = "SL";
        public static final String CONDITION_CODE_E = "E";
        public static final String PRE_ASSET_TAGGING_FILE_TYPE_INDENTIFIER = "preAssetTaggingFileType";
        public static final String VENDOR_NAME_CONSTRUCTED = "CONSTRUCTED";
        public static final String ACQUISITION_TYPE_CODE_C = "C";
        public static final String NON_TAGGABLE_ASSET = "N";
        public static final int ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY = 10;
        public static final String SECTION_ID_FABRICATION_INFORMATION = "fabricationInformation";
        public static final String SECTION_ID_LAND_INFORMATION = "landInformation";
        public static final String SECTION_ID_PAYMENT_INFORMATION = "paymentInformation";
        public static final String SECTION_ID_PAYMENT_LOOKUP = "paymentLookup";
        public static final String SECTION_ID_DOCUMENT_LOOKUP = "documentLookup";
        public static final String SECTION_ID_DEPRECIATION_INFORMATION = "depreciationInformation";
        public static final String SECTION_ID_MERGE_HISTORY = "mergeHistory";
        public static final String SECTION_ID_HISTORY = "history";
        public static final String SECTION_ID_RETIREMENT_INFORMATION = "retirementInformation";
        public static final String SECTION_ID_EQUIPMENT_LOAN_INFORMATION = "equipmentLoanInformation";
        public static final String SECTION_ID_WARRENTY = "warrenty";
        public static final String SECTION_ID_REPAIR_HISTORY = "repairHistory";
        public static final String COLLECTION_ID_ASSET_REPAIR_HISTORY = "assetRepairHistory";
        public static final String SECTION_ID_COMPONENTS = "components";
        public static final String SECTION_ID_LOAN_INFORMATION = "equipmentLoanInformation";
        public static final String[] EDIT_DETAIL_INFORMATION_FIELDS = new String[] { CamsPropertyConstants.Asset.ASSET_DATE_OF_SERVICE, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsPropertyConstants.Asset.GOVERNMENT_TAG_NUMBER, CamsPropertyConstants.Asset.NATIONAL_STOCK_NUMBER, CamsPropertyConstants.Asset.MANUFACTURER_NAME, CamsPropertyConstants.Asset.MANUFACTURER_MODEL_NUMBER, CamsPropertyConstants.Asset.SERIAL_NUMBER, CamsPropertyConstants.Asset.CAMPUS_TAG_NUMBER, CamsPropertyConstants.Asset.OLD_TAG_NUMBER, CamsPropertyConstants.Asset.TOTAL_COST_AMOUNT, CamsPropertyConstants.Asset.FEDERAL_CONTRIBUTION, CamsPropertyConstants.Asset.ASSET_DEPRECIATION_DATE, CamsPropertyConstants.Asset.FINANCIAL_OBJECT_SUB_TYP_CODE };
        public static final String[] EDIT_ORGANIZATION_INFORMATION_FIELDS = new String[] { CamsPropertyConstants.AssetOrganization.ASSET_ORGANIZATION + "." + CamsPropertyConstants.AssetOrganization.ORGANIZATION_TAG_NUMBER, CamsPropertyConstants.Asset.ESTIMATED_SELLING_PRICE, CamsPropertyConstants.Asset.RECEIVE_DATE, CamsPropertyConstants.Asset.REPLACEMENT_AMOUNT };
        public static final String[] FABRICATION_INFORMATION_FIELDS = new String[] { CamsPropertyConstants.Asset.ESTIMATED_FABRICATION_COMPLETION_DATE, CamsPropertyConstants.Asset.FABRICATION_ESTIMATED_RETENTION_YEARS, CamsPropertyConstants.Asset.FABRICATION_ESTIMATED_TOTAL_AMOUNT };
        public static final String SECTION_TITLE_NO_PAYMENT = "- No payment exists for Capital Asset: ";
    }

    public static class AssetPayment {
        public static final String TRANSFER_PAYMENT_CODE_N = "N";
        public static final String TRANSFER_PAYMENT_CODE_Y = "Y";
        public static final String ASSET_PAYMENT_DIFFERENT_OBJECT_SUB_TYPE_CONFIRMATION_QUESTION = "AssetPaymentDifferentObjectSubTypeQuestion";
    }

    public static class DepreciationConvention {
        public static final String CREATE_DATE = "CD";
        public static final String HALF_YEAR = "HY";
        public static final String FULL_YEAR = "FY";
    }

    public static class EquipmentLoanOrReturnEditMode {
        public static final String DISPLAY_NEW_LOAN_TAB = "displayNewLoanTab";
        public static final String DISPLAY_RETURN_LOAN_FIELDS_READ_ONLY = "displayReturnLoanFieldsReadOnly";
    }

    public static class AssetGlobal {
        public static final String CAPITAL_OBJECT_ACQUISITION_CODE_PARAM = "CAPITAL_OBJECT_ACQUISITION_CODES";
        public static final String NON_NEW_ACQUISITION_GROUP_PARAM = "NON_NEW_ACQUISITION_CODES";
        public static final String NEW_ACQUISITION_CODE_PARAM = "NEW_ACQUISITION_CODE";
        public static final String FABRICATED_ACQUISITION_CODE = "FABRICATED_ACQUISITION_CODE";
        public static final String PRE_TAGGING_ACQUISITION_CODE = "PRE_TAGGING_ACQUISITION_CODE";
        public static final String PRE_TAGGING_ACQUISITION_TYPE_CODE = "P";
        public static final String LINE_DESCRIPTION_PAYMENT = "Payment";
        public static final String LINE_DESCRIPTION_PAYMENT_OFFSET = "Payment Offset";
        public static final String SECTION_ID_ASSET_ACQUISITION_TYPE = "assetAcquisitionType";
        public static final String SECTION_ID_ASSET_INFORMATION = "assetInformation";
        public static final String SECTION_ID_RECALCULATE_SEPARATE_SOURCE_AMOUNT = "recalculateSeparateSourceAmount";
    }

    public static class AssetSeparate {
        public static final String CALCULATE_EQUAL_SOURCE_AMOUNTS_BUTTON = "calculateEqualSourceAmountsButton";
        public static final String CALCULATE_SEPARATE_SOURCE_REMAINING_AMOUNT_BUTTON = "calculateSeparateSourceRemainingAmountButton";
        public static final String SEPARATE_AN_ASSET_DESCRIPTION = "Separate an Asset";
    }

    public static class GLPostingObjectCodeType {
        public static final String CAPITALIZATION = "Capitalization";
        public static final String ACCUMMULATE_DEPRECIATION = "Accummulate Depreciation";
        public static final String OFFSET_AMOUNT = "Offset Amount";
        public static final String INCOME = "Income";
    }

    public static class Postable {
        public static final String GL_BALANCE_TYPE_CODE_AC = "AC";
    }

    public static class AssetTransfer {
        public static final String DOCUMENT_TYPE_CODE = "AT";
    }


    public static class PreAssetTagging {
        public static final String SECTION_ID_PREASSET_TAGGING_DETAIL = "Edit List of Pre-Asset Tagging Details";
    }

    public static final String defaultLockingInformation = "-1";
}
