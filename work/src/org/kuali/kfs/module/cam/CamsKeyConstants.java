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


/**
 * Holds error key constants.
 */
public class CamsKeyConstants {
    public static final String CONTINUE_QUESTION = "document.question.continue.text";
    public static final String ERROR_INVALID_BUILDING_CODE = "error.invalid.building.code";
    public static final String ERROR_INVALID_ROOM_NUMBER = "error.invalid.room.number";
    public static final String ERROR_INVALID_IN_SERVICE_DATE = "error.invalid.in.service.date";
    public static final String ERROR_BLANK_IN_SERVICE_DATE_DISALLOWED = "error.blank.in.service.date.disallowed";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_PRE_ASSET_TAGGING = "message.batchUpload.title.pre.asset.tagging";
    public static final String ERROR_INVALID_ASSET_WARRANTY_NO = "error.invalid.asset.warranty.no";
    public static final String ERROR_CAPITAL_ASSET_VENDOR_NAME_REQUIRED = "error.capital.asset.vendor.name.required";
    public static final String ERROR_TAG_NUMBER_DUPLICATE = "error.tag.number.duplicate";
    public static final String ERROR_ASSET_RETIRED_CAPITAL = "error.asset.retired.capital";
    public static final String ERROR_ASSET_RETIRED_NON_CAPITAL_ACTIVE = "error.asset.retired.non.capital.active";
    public static final String ERROR_ASSET_RETIRED_ACT_NON_CAPITAL_2003 = "error.asset.retired.act.non.capital.2003";
    public static final String ERROR_FABRICATION_ESTIMATED_TOTAL_AMOUNT_REQUIRED = "error.asset.fabrication.totalAmount.required";
    public static final String ERROR_ESTIMATED_FABRICATION_COMPLETION_DATE_REQUIRED = "error.asset.fabrication.completionDate.required";
    public static final String ERROR_ESTIMATED_FABRICATION_LIFE_LIMIT_REQUIRED = "error.asset.fabrication.lifeLimit.required";
    public static final String ERROR_FABRICATION_ESTIMATED_TOTAL_AMOUNT_NEGATIVE = "error.asset.fabrication.totalAmount.negative";
    public static final String ERROR_ESTIMATED_FABRICATION_LIFE_LIMIT_NEGATIVE = "error.asset.fabrication.lifeLimit.negative";
    public static final String ORGANIZATION_OWNER_ACCOUNT_INACTIVE = "error.asset.org.owner.account.inactive";
    public static final String ORGANIZATION_OWNER_ACCOUNT_INVALID = "error.asset.org.owner.account.invalid";
    public static final String ERROR_ESTIMATED_FABRICATION_COMPLETION_DATE_PAST = "error.asset.fabrication.completionDate.past";

    public static class Depreciation {
        public static final String NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND = "error.batch.depreciation.assetsNotFound";
        public static final String ERROR_WHEN_CALCULATING_BASE_AMOUNT = "error.batch.depreciation.baseAmountCalculationError";
        public static final String ERROR_WHEN_CALCULATING_DEPRECIATION = "error.batch.depreciation.calculationError";
        public static final String ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE = "error.batch.depreciation.glpeUpdateError";
        public static final String DEPRECIATION_DATE_PARAMETER_NOT_FOUND = "error.batch.depreciation.depreciationDateNotFound";
        public static final String INVALID_DEPRECIATION_DATE_FORMAT = "error.batch.depreciation.invalidDepreciationDateFormat";
        public static final String DEPRECIATION_ALREADY_RAN_MSG = "error.batch.depreciation.alreadyRan";
        public static final String MSG_REPORT_DEPRECIATION_HEADING1 = "message.batch.report.depreciation.heading.description";
        public static final String MSG_REPORT_DEPRECIATION_HEADING2 = "message.batch.report.depreciation.heading.figures";
    }

    public static class Payment {
        public static final String ERROR_INVALID_DOC_POST_DATE = "error.payment.invalid.document.postdate";
        public static final String ERROR_INVALID_OBJECT_SUBTYPE = "error.asset.payment.invalidObjectSubtypeCode";
        public static final String WARNING_NOT_SAME_OBJECT_SUB_TYPES = "warning.payment.object.subtype.not_the_same";
        public static final String ERROR_NON_CAPITAL_ASSET = "error.non.capital.asset.payment";
        public static final String ERROR_ASSET_EXISTS_IN_DOCUMENT = "error.asset.exists.in.payment.document";
        public static final String ERROR_NON_ASSETS_IN_DOCUMENT = "error.payment.document.noAssetsInDocumentFound";
        public static final String ERROR_NON_ZERO_COST_ASSETS_ALLOWED = "error.payment.document.noZeroValueAssetsAllowed";
        public static final String ERROR_POSTING_DATE_FUTURE_NOT_ALLOWED = "error.posting.date.future.notallowed";
    }

    public static class Transfer {
        public static final String ERROR_ASSET_RETIRED_NOTRANSFER = "error.asset.retired.notransfer";
        public static final String ERROR_CAMPUS_PLANT_FUND_UNKNOWN = "error.campus.plant.fund.unknown";
        public static final String ERROR_ORG_PLANT_FUND_UNKNOWN = "error.org.plant.fund.unknown";
        public static final String ERROR_OWNER_ACCT_NOT_ACTIVE = "error.account.notactive";
        public static final String ERROR_OWNER_CHART_CODE_INVALID = "error.receiving.org.chart.code.invalid";
        public static final String ERROR_OWNER_ACCT_INVALID = "error.receiving.org.acct.invalid";
        public static final String ERROR_TRFR_FDOC_INVALID = "error.transfer.fund.financial.doc.invalid";
        public static final String ERROR_TRFR_FDOC_REQUIRED = "error.transfer.fund.financial.doc.required";
        public static final String ERROR_TRFR_LOANED = "error.transfer.asset.loaned";
        public static final String ASSET_LOAN_NOTE = "note.asset.loan.active";
        public static final String ERROR_INVALID_USER_AUTH_ID = "error.representative.user.invalid";
        public static final String ERROR_PAYMENT_OBJECT_CODE_NOT_FOUND = "error.transfer.document.payment.objectcode.doesnt.exists";
        public static final String ERROR_INVALID_USER_GROUP_FOR_TRANSFER_NONMOVABLE_ASSET = "error.invalid.user.group.for.transfer.nonmoveable.asset";
        public static final String MESSAGE_NO_LEDGER_ENTRY_REQUIRED_TRANSFER = "message.no.ledger.entry.required.transfer";
    }

    public static class GLPosting {
        public static final String ERROR_ASSET_OBJECT_CODE_NOT_FOUND = "error.asset.object.code.not.found";
        public static final String ERROR_ASSET_OBJECT_CODE_INACTIVE = "error.asset.object.code.inactive";
        public static final String ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_NOT_FOUND = "error.object.code.from.asset.object.code.not.found";
        public static final String ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_INACTIVE = "error.object.code.from.asset.object.code.inactive";
        public static final String ERROR_OBJECT_CODE_FROM_ASSET_OBJECT_CODE_INVALID = "error.object.code.from.asset.object.code.invalid";
    }

    public static class AssetLocation {
        public static final String ERROR_INVALID_BUILDING_CODE = "error.invalid.building.code";
        public static final String ERROR_INVALID_CAMPUS_CODE = "error.invalid.campus.code";
        public static final String ERROR_INVALID_ROOM_NUMBER = "error.invalid.room.code";
        public static final String ERROR_INVALID_OFF_CAMPUS_STATE = "error.invalid.state.code";
        public static final String ERROR_INVALID_STATE_ZIP_CODE = "error.invalid.state.zip.code";
        public static final String ERROR_INVALID_ZIP_CODE = "error.invalid.zip.code";
        public static final String ERROR_LOCATION_INFO_REQUIRED = "error.location.info.required";
        public static final String ERROR_ONCAMPUS_BUILDING_CODE_REQUIRED = "error.oncampus.building.value.required";
        public static final String ERROR_ONCAMPUS_BUILDING_ROOM_NUMBER_REQUIRED = "error.oncampus.room.number.required";
        public static final String ERROR_OFFCAMPUS_ADDRESS_REQUIRED = "error.offcampus.address.required";
        public static final String ERROR_OFFCAMPUS_CITY_REQUIRED = "error.offcampus.city.required";
        public static final String ERROR_OFFCAMPUS_STATE_REQUIRED = "error.offcampus.state.required";
        public static final String ERROR_OFFCAMPUS_ZIP_REQUIRED = "error.offcampus.zip.required";
        public static final String ERROR_OFFCAMPUS_CONTACT_REQUIRED = "error.offcampus.contactName.required";
        public static final String ERROR_OFFCAMPUS_COUNTRY_REQUIRED = "error.offcampus.country.required";
        public static final String ERROR_CHOOSE_LOCATION_INFO = "error.location.choose.right";
        public static final String ERROR_LOCATION_OFF_CAMPUS_NOT_PERMITTED = "error.location.offcampus.not.permitted";
        public static final String ERROR_LOCATION_ON_CAMPUS_NOT_PERMITTED = "error.location.oncampus.not.permitted";
        public static final String ERROR_ONCAMPUS_BUILDING_ROOM_NUMBER_NOT_PERMITTED = "error.oncampus.room.number.not.permitted";
        public static final String ERROR_ONCAMPUS_SUB_ROOM_NUMBER_NOT_PERMITTED = "error.oncampus.sub.room.number.not.permitted";
        public static final String ERROR_CHOOSE_ASSET_TYPE = "error.choose.asset.type.validate.location";
        public static final String ERROR_LOCATION_NOT_PERMITTED_ASSET_TYPE = "error.location.not.permitted.asset.type";
        public static final String ERROR_ASSET_LOCATION_ROOM_NUMBER_NONMOVEABLE = "error.asset.location.room.number.nonmoveable";
        public static final String ERROR_ASSET_LOCATION_BUILDING_NONMOVEABLE = "error.asset.location.building.number.nonmoveable";
    }

    public static class Retirement {
        public static final String ERROR_RETIREMENT_DETAIL_INFO_NULL = "error.retirement.detail.info.null";
        public static final String ERROR_INVALID_RETIREMENT_DETAIL_INFO = "error.invalid.retirement.detail.info";
        public static final String ERROR_INVALID_MERGED_TARGET_ASSET_NUMBER = "error.invalid.merged.target.asset.number";
        public static final String ERROR_NON_CAPITAL_ASSET_RETIREMENT = "error.non.capital.asset.retirment";
        public static final String ERROR_NON_ACTIVE_ASSET_RETIREMENT = "error.non.active.asset.retirment";
        public static final String ERROR_INVALID_CAPITAL_ASSET_NUMBER = "error.invalid.capital.asset.number";
        public static final String ERROR_DUPLICATE_CAPITAL_ASSET_NUMBER_WITH_TARGET = "error.duplicate.capital.asset.number.with.target";
        public static final String ERROR_INVALID_USER_GROUP_FOR_NON_MOVEABLE_ASSET = "error.invalid.user.group.for.nonmoveable.asset";
        public static final String ERROR_BLANK_CAPITAL_ASSET_NUMBER = "error.blank.capital.asset.number";
        public static final String ERROR_ASSET_RETIREMENT_GLOBAL_NO_ASSET = "error.asset.retirement.global.no.asset";
        public static final String ERROR_DISALLOWED_MERGE_RETIREMENT_REASON_CODE = "error.disallowed.merge.retirement.reason.code";
        public static final String ERROR_DISALLOWED_RETIREMENT_REASON_CODE = "error.disallowed.retirement.reason.code";
        public static final String ERROR_DISALLOWED_MERGE_SEPARATE_REASON_CODE = "error.disallowed.merge.separate.reason.code";
        public static final String ERROR_DISALLOWED_RAZE_REASON_CODE = "error.disallowed.raze.reason.code";
        public static final String ERROR_MULTIPLE_ASSET_RETIRED = "error.multiple.asset.retired";
        public static final String ERROR_LOANED_ASSET_CANNOT_RETIRED = "error.retirement.asset.loaned";
        public static final String MESSAGE_NO_LEDGER_ENTRY_REQUIRED_RETIREMENT = "message.no.ledger.entry.required.retirement";
    }

    public static class AssetLocationGlobal {
        public static final String ERROR_INVALID_CAPITAL_ASSET_NUMBER = "error.asset.location.invalid.capital.asset.number";
        public static final String ERROR_INVALID_CAMPUS_CODE = "error.asset.location.invalid.campus.code";
        public static final String ERROR_INVALID_BUILDING_CODE = "error.asset.location.invalid.building.code";
        public static final String ERROR_INVALID_ROOM_NUMBER = "error.asset.location.invalid.room.code";
        public static final String ERROR_INVALID_ROOM_NUMBER_FOR_CAMPUS = "error.asset.location.invalid.room.code.for.campus";
        public static final String ERROR_TAG_NUMBER_REQUIRED = "error.asset.location.tag.number.required";
        public static final String ERROR_DUPLICATE_TAG_NUMBER_FOUND = "error.asset.location.duplicate.tag.number";
        public static final String ERROR_DUPLICATE_TAG_NUMBER_WITHIN_DOCUMENT = "error.asset.location.duplicate.tag.within.document";
        public static final String ERROR_ASSET_LOCATION_GLOBAL_NO_ASSET_DETAIL = "error.asset.location.no.asset.location.detail.line";
        public static final String ERROR_CAMPUS_CODE_REQUIRED = "error.asset.location.campus.code.required";
        public static final String ERROR_BUILDING_CODE_REQUIRED = "error.asset.location.building.code.required";
        public static final String ERROR_ROOM_NUMBER_REQUIRED = "error.asset.location.room.number.required";
        public static final String ERROR_ASSET_AUTHORIZATION = "error.asset.authorization";
    }

    public static class BarcodeInventory {
        public static final String TITLE_BAR_CODE_INVENTORY = "message.upload.title.barCodeInventory";
        public static final String ERROR_INVALID_FIELD = "error.document.invalid.field";
        public static final String ERROR_REQUIRED_FIELD = "error.document.required.field";
        public static final String ERROR_NOT_ALLOWED_FIELD = "error.document.not.allowed.field";
        public static final String ERROR_CAPITAL_ASSET_DOESNT_EXIST = "error.document.capitalAsset.not.found";
        public static final String ERROR_CAPITAL_ASSET_IS_RETIRED = "error.document.capitalAsset.retired";
        public static final String ERROR_DUPLICATED_TAG_NUMBER = "error.document.duplicated.tagNumber";
        public static final String ERROR_ASSET_LOCKED = "error.document.locked.asset";
        public static final String ERROR_INVALID_FILE_TYPE = "error.uploadFile.invalid.type";
        public static final String ERROR_INACTIVE_FIELD = "error.document.inactive.field";
        public static final String ERROR_APPROVE_DOCUMENT_WITH_ERROR_EXIST = "error.approve.document.with.error.exist";
        public static final String ERROR_RECORDS_NO_SELECTED = "error.document.records_no_selected";
        public static final String ERROR_CHECKBOX_MUST_BE_CHECKED = "error.checkbox.must.be.checked";
        public static final String ERROR_GLOBAL_REPLACE_SEARCH_CRITERIA = "error.global.replace.empty.fields";
        public static final String ERROR_VALIDATE_ITEMS_BEFORE_APPROVE = "error.validate.items.before.approval";
    }

    public static class EquipmentLoanOrReturn {
        public static final String ERROR_INVALID_BORROWER_ID = "error.invalid.borrower.id";
        public static final String ERROR_INVALID_LOAN_DATE = "error.invalid.loan.date";
        public static final String ERROR_INVALID_EXPECTED_RETURN_DATE = "error.invalid.expected.return.date";
        public static final String ERROR_INVALID_EXPECTED_MAX_DATE = "error.invalid.expected.max.date";
        public static final String ERROR_INVALID_LOAN_RETURN_DATE = "error.invalid.loan.return.date";
        public static final String ERROR_INVALID_BORROWER_STATE = "error.invalid.borrower.state.code";
        public static final String ERROR_INVALID_BORROWER_STORAGE_STATE = "error.invalid.borrower.storage.state.code";
        public static final String ERROR_BORROWER_STORAGE_STATE_REQUIRED = "error.borrower.storage.state.required";
        public static final String ERROR_BORROWER_STORAGE_ZIP_REQUIRED = "error.borrower.storage.zip.required";
        public static final String ERROR_CAMPUS_TAG_NUMBER_REQUIRED = "error.campus.tag.number.required";
    }

    public static class AssetGlobal {
        public static final String ERROR_INVENTORY_STATUS_REQUIRED = "error.asset.inventory.status.code.required";
        public static final String ERROR_INVENTORY_STATUS_REQUIRED_FOR_PAYMENT = "error.asset.inventory.status.code.required.for.payment";
        public static final String ERROR_OWNER_ACCT_NOT_ACTIVE = "error.asset.owner.account.not.active";
        public static final String ERROR_PAYMENT_ACCT_NOT_VALID = "error.asset.payment.account.not.valid";
        public static final String MIN_ONE_ASSET_REQUIRED = "error.document.min.one.asset.required";
        public static final String MIN_ONE_PAYMENT_REQUIRED = "error.document.min.one.payment.required";
        public static final String ERROR_VENDOR_NAME_REQUIRED = "error.capital.asset.vendor.name.required";
        public static final String ERROR_MFR_NAME_REQUIRED = "error.capital.asset.manufacturer.name.required";
        public static final String ERROR_ACQUISITION_TYPE_CODE_REQUIRED = "error.acquisition.code.required";
        //public static final String ERROR_ACQUISITION_TYPE_CODE_NOT_ALLOWED = "error.acquisition.code.not.allowed";
        //public static final String ERROR_INACTIVE_ACQUISITION_TYPE_CODE = "error.inactive.acquisition.code";
        public static final String ERROR_OWNER_CHART_INVALID = "error.asset.owner.chart.code.invalid";
        public static final String ERROR_OWNER_ACCT_NUMBER_INVALID = "error.asset.owner.account.number.invalid";
        public static final String ERROR_CAMPUS_TAG_NUMBER_DUPLICATE = "error.asset.campus.tag.number.duplicate";
        public static final String ERROR_CAPITAL_OBJECT_CODE_NOT_ALLOWED = "error.capital.object.code.not.allowed";
        public static final String ERROR_CAPITAL_OBJECT_CODE_INVALID = "error.capital.object.code.invalid";
        public static final String ERROR_DOCUMENT_TYPE_CODE_NOT_ALLOWED = "error.document.type.code.not.allowed";
        public static final String ERROR_ASSET_TYPE_REQUIRED = "error.valid.capital.asset.type.required";
        public static final String ERROR_ASSET_LOCATION_DEPENDENCY = "error.asset.location.validation.dependecy";
        public static final String ERROR_ASSET_PAYMENT_DEPENDENCY = "error.asset.payment.validation.dependecy";
        public static final String ERROR_CAPITAL_ASSET_PAYMENT_AMOUNT_MIN = "error.capital.asset.payment.min.limit";
        public static final String ERROR_NON_CAPITAL_ASSET_PAYMENT_AMOUNT_MAX = "error.noncapital.asset.payment.max.limit";
        public static final String ERROR_DOCUMENT_POSTING_DATE_REQUIRED = "error.document.posting.date.required";
        public static final String ERROR_INVALID_PAYMENT_AMOUNT = "error.payment.amount.invalid";
        public static final String ERROR_EXPENDITURE_FINANCIAL_DOCUMENT_NUMBER_REQUIRED = "error.expenditure.financial.document.number.required";
        public static final String ERROR_EXPENDITURE_FINANCIAL_DOCUMENT_TYPE_CODE_REQUIRED = "error.expenditure.financial.document.type.code.required";
        public static final String ERROR_FINANCIAL_DOCUMENT_POSTING_YEAR_REQUIRED = "error.financial.document.posting.year.required";
        public static final String ERROR_UNIVERSITY_NOT_DEFINED_FOR_DATE = "error.university.not.defined.for.date";
        public static final String ERROR_SEPARATE_ASSET_TOTAL_COST_NOT_MATCH_PAYMENT_TOTAL_COST = "error.separate.asset.total.cost.not.match.payment.total.cost";
        public static final String ERROR_SEPARATE_ASSET_ALREADY_SEPARATED = "error.separate.asset.already.separated";
        public static final String ERROR_INVALID_ACQUISITION_INCOME_OBJECT_CODE = "error.invalid.acquisition.income.object.code";
        public static final String ERROR_CHANGE_ASSET_TOTAL_AMOUNT_DISALLOW = "error.change.asset.total.amount.disallow";
    }

    public static class AssetSeparate {
        public static final String ERROR_ASSET_SPLIT_MAX_LIMIT = "error.max.payments.limit";
        public static final String ERROR_CAPITAL_ASSET_TYPE_CODE_REQUIRED = "error.capital.asset.type.code.required";
        public static final String ERROR_CAPITAL_ASSET_TYPE_CODE_INVALID = "error.capital.asset.type.code.invalid";
        public static final String ERROR_ASSET_DESCRIPTION_REQUIRED = "error.asset.description.required";
        public static final String ERROR_MANUFACTURER_REQUIRED = "error.manufacturer.required";
        public static final String ERROR_NON_CAPITAL_ASSET_SEPARATE_REQUIRED = "error.non.active.capital.asset.required";
        public static final String ERROR_TOTAL_SEPARATE_SOURCE_AMOUNT_REQUIRED = "error.total.separate.source.amount.required";
        public static final String ERROR_INVALID_TOTAL_SEPARATE_SOURCE_AMOUNT = "error.total.separate.source.amount.invalid";
        public static final String ERROR_ZERO_OR_NEGATIVE_DOLLAR_AMOUNT = "error.zero.or.negative.dollar.amount";
        public static final String ERROR_ZERO_OR_NEGATIVE_LOCATION_QUANTITY = "error.zero.or.negative.location.quantity";
        public static final String ERROR_SEPARATE_ASSET_BELOW_THRESHOLD = "error.separate.asset.below.threshold";
    }

    public static class Asset {
        public static final String ERROR_INVALID_SALVAGE_AMOUNT = "error.asset.salvage.amount.not.valid";
        public static final String ERROR_INVALID_BOOKVALUE_AMOUNT = "error.asset.bookValue.amount.not.valid";
        
        // for use with Asset Edit
        public static final String ERROR_OWNER_INVALID = "error.owner.invalid";
        public static final String ERROR_ACQUISITION_TYPE_CODE_INVALID = "error.acquisition.type.code.invalid";
        public static final String ERROR_ASSET_STATUS_INVALID = "error.asset.status.invalid";
        public static final String ERROR_ASSET_CONDITION_INVALID = "error.asset.condition.invalid";
        public static final String ERROR_TYPE_CODE_INVALID = "error.type.code.invalid";
        public static final String ERROR_FINANCIAL_OBJECT_SUBTYPE_CODE_INVALID = "error.financial.object.subtype.code.invalid";
        public static final String ERROR_DEPRECATION_METHOD_CODE_INVALID = "error.deprecation.method.code.invalid";
        
        public static final String ERROR_OWNER_INACTIVE = "error.owner.inactive";
        public static final String ERROR_ACQUISITION_TYPE_CODE_INACTIVE = "error.acquisition.type.code.inactive";
        public static final String ERROR_ASSET_STATUS_INACTIVE = "error.asset.status.inactive";
        public static final String ERROR_ASSET_CONDITION_INACTIVE = "error.asset.condition.inactive";
        public static final String ERROR_TYPE_CODE_INACTIVE = "error.type.code.inactive";
        public static final String ERROR_FINANCIAL_OBJECT_SUBTYPE_CODE_INACTIVE = "error.financial.object.subtype.code.inactive";
        public static final String ERROR_DEPRECATION_METHOD_CODE_INACTIVE = "error.deprecation.method.code.inactive";
    }

    public static class AssetRepairHistory {
        public static final String ERROR_DUPLICATE_INCIDENT_DATE = "error.duplicate.incident.date";
    }

    public static class AssetLock {
        public static final String ERROR_ASSET_LOCKED = "error.asset.locked";
        public static final String ERROR_ASSET_MAINTENANCE_LOCKED = "error.asset.maintenance.locked";
    }
    
    public static class PreTag {
        public static final String ERROR_PRE_TAG_INVALID_REPRESENTATIVE_ID = "error.invalid.representative.id";
        public static final String ERROR_PRE_TAG_NUMBER = "error.invalid.pre.tag.number";
        public static final String ERROR_PRE_TAG_DETAIL_EXCESS = "error.pre.tag.detail.excess";
    }
    
	public static class AssetPaymentAllocation {
		public static final String ERROR_PERCENT_NOT_100 = "error.asset.payment.allocation.percent";
		public static final String ERROR_AMOUNT_NOT_EQUAL = "error.asset.payment.allocation.not.equal";
	}
}
