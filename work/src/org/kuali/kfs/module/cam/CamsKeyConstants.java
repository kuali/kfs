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
 * Holds error key constants.
 */
public class CamsKeyConstants {

    public static final String ERROR_INVALID_CAPITAL_ASSET_NUMBER = "error.invalid.capital.asset.number";
    public static final String ERROR_INVALID_BUILDING_CODE = "error.invalid.building.code";
    public static final String ERROR_INVALID_ROOM_NUMBER = "error.invalid.room.number";
    public static final String ERROR_PRE_TAG_NUMBER = "error.invalid.pre.tag.number";
    public static final String ERROR_PRE_TAG_DETAIL_EXCESS = "error.pre.tag.detail.excess";
    public static final String ERROR_NO_DETAIL_LINE = "error.invalid.no.detail.line";

    public static final String MESSAGE_BATCH_UPLOAD_TITLE_PRE_ASSET_TAGGING = "message.batchUpload.title.pre.asset.tagging";
    public static final String ERROR_INVALID_ASSET_WARRANTY_NO = "error.invalid.asset.warranty.no";

    public static class Depreciation {
        public static final String NO_ELIGIBLE_FOR_DEPRECIATION_ASSETS_FOUND = "error.batch.depreciation.assetsNotFound";
        public static final String ERROR_WHEN_CALCULATING_BASE_AMOUNT = "error.batch.depreciation.baseAmountCalculationError";
        public static final String ERROR_WHEN_CALCULATING_DEPRECIATION = "error.batch.depreciation.calculationError";
        public static final String ERROR_WHEN_UPDATING_GL_PENDING_ENTRY_TABLE = "error.batch.depreciation.glpeUpdateError";
        public static final String DEPRECIATION_DATE_PARAMETER_NOT_FOUND = "error.batch.depreciation.depreciationDateNotFound";
        public static final String INVALID_DEPRECIATION_DATE_FORMAT = "error.batch.depreciation.invalidDepreciationDateFormat";
        public static final String DEPRECIATION_ALREADY_RAN_MSG = "error.batch.depreciation.alreadyRan";
        public static final String ERROR_WHEN_UPDATING_DOCUMENT_HEADER_TABLE = "error.batch.depreciation.documentHeaderUpdateError";

        public static final String MSG_REPORT_DEPRECIATION_HEADING1 = "message.batch.report.depreciation.heading.description";
        public static final String MSG_REPORT_DEPRECIATION_HEADING2 = "message.batch.report.depreciation.heading.figures";
    }

    public static final String ERROR_ASSET_BUILDING_CODE_NULL = "error.asset.building.code.null";
    public static final String ERROR_ASSET_BUILDING_CODE_NOT_NULL = "error.asset.building.code.not.null";
    public static final String ERROR_ASSET_BUILDING_ROOMNO_NULL = "error.asset.building.roomno.null";
    public static final String ERROR_ASSET_BUILDING_ROOMNO_NOT_NULL = "error.asset.building.roomno.not.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_CONTACT_NAME_NULL = "error.asset.off.campus.contact.name.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_CONTACT_NAME_NOT_NULL = "error.asset.off.campus.contact.name.not.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_STREET_NULL = "error.asset.off.campus.street.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_STREET_NOT_NULL = "error.asset.off.campus.street.not.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_CITY_NULL = "error.asset.off.campus.city.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_CITY_NOT_NULL = "error.asset.off.campus.city.not.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_STATE_NULL = "error.asset.off.campus.state.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_STATE_NOT_NULL = "error.asset.off.campus.state.not.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_ZIPCODE_NULL = "error.asset.off.campus.zipcode.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_ZIPCODE_NOT_NULL = "error.asset.off.campus.zipcode.not.null";
    public static final String ERROR_ASSET_OFF_CAMPUS_COUNTRY_NOT_NULL = "error.asset.off.campus.country.not.null";
    public static final String ERROR_CAPITAL_ASSET_VENDOR_NAME_REQUIRED = "error.capital.asset.vendor.name.required";
    public static final String ERROR_TAG_NUMBER_DUPLICATE = "error.tag.number.duplicate";
    public static final String ERROR_TAG_NUMBER_RESTRICT_CHANGE = "error.tag.number.restrict.change";
    public static final String ERROR_ASSET_TYPE_CODE_RESTRICT_CHANGE = "error.asset.type.code.restrict.change";
    public static final String ERROR_ASSET_DESCRIPTION_RESTRICT_CHANGE = "error.asset.description.restrict.change";
    public static final String ERROR_INVALID_ASSET_STATUS_CHANGE = "error.invalid.asset.status.change";
    public static final String ERROR_ASSET_RETIRED_NOEDIT = "error.asset.retired.noedit";

    public static class Transfer {
        public static final String ERROR_ASSET_DOCS_PENDING = "error.asset.pending.docs";
        public static final String ERROR_ASSET_RETIRED_NOTRANSFER = "error.asset.retired.notransfer";
        public static final String ERROR_CAMPUS_PLANT_FUND_UNKNOWN = "error.campus.plant.fund.unknown";
        public static final String ERROR_ORG_PLANT_FUND_UNKNOWN = "error.org.plant.fund.unknown";
        public static final String ERROR_OWNER_ACCT_NOT_ACTIVE = "error.account.notactive";
        public static final String ERROR_INVALID_BUILDING_CODE = "error.invalid.building.code";
        public static final String ERROR_INVALID_CAMPUS_CODE = "error.invalid.campus.code";
        public static final String ERROR_INVALID_ROOM_NUMBER = "error.invalid.room.code";
    }

}
