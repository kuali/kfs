/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab;


/**
 * Holds error key constants.
 */
public class CabKeyConstants {
    public static final String ERROR_SPLIT_QTY_INVALID = "error.split.qty.invalid";
    public static final String ERROR_SPLIT_QTY_REQUIRED = "error.split.qty.required";
    public static final String ERROR_PO_ID_INVALID = "error.po.id.invalid";
    public static final String ERROR_PO_ID_EMPTY = "error.po.id.empty";
    public static final String MESSAGE_NO_ACTIVE_PURAP_DOC = "message.no.active.purap.doc";
    public static final String MESSAGE_CAB_CHANGES_SAVED_SUCCESS = "message.cab.changes.saved.success";

    public static final String ERROR_ITEM_CAPITAL_AND_EXPENSE = "errors.item.capitalAsset.capital.and.expense";
    public static final String WARNING_ABOVE_THRESHOLD_SUGESTS_CAPITAL_ASSET_LEVEL = "warnings.item.capitalAsset.threshold.objectCodeLevel";
    public static final String ERROR_ITEM_TRAN_TYPE_OBJECT_CODE_SUBTYPE = "errors.item.capitalAsset.tranType.objectCodeSubtype";
    public static final String ERROR_ITEM_WRONG_TRAN_TYPE = "errors.item.capitalAsset.wrong.tranType";
    public static final String ERROR_ITEM_NO_TRAN_TYPE = "errors.item.capitalAsset.no.tranType";
    public static final String ERROR_MERGE_QTY_EMPTY = "error.merge.qty.empty";
    public static final String ERROR_MERGE_DESCRIPTION_EMPTY = "error.merge.description.empty";
    public static final String ERROR_ALLOCATE_NO_LINE_SELECTED = "error.allocate.no.line.selected";
    public static final String ERROR_ALLOCATE_NO_TARGET_ACCOUNT = "error.allocate.no.target.account";
    public static final String ERROR_ADDITIONAL_CHARGES_EXIST = "error.additional.charges.exist";
    public static final String ERROR_MERGE_LINE_SELECTED = "error.merge.line.selected";
    public static final String ERROR_ADDL_CHARGE_PENDING = "error.addl.charge.pending";
    public static final String ERROR_TRADE_IN_PENDING = "error.trade.in.pending";
    public static final String QUESTION_TRADE_IN_INDICATOR_EXISTING = "question.trade.in.indicator.existing";
    public static final String QUESTION_SKIP_ASSET_NUMBERS_TO_ASSET_GLOBAL = "question.skip.asset.numbers.to.asset.global";
    public static final String QUESTION_DIFFERENT_OBJECT_SUB_TYPES = "question.different.object.sub.types";
    public static final String ERROR_FRACTIONAL_QUANTITY = "error.fractional.quanity";
    public static final String ERROR_MERGE_WITH_PRETAGGING = "error.merge.with.pretagging";
    public static final String DATA_EDIT_LOCK_ERROR = "cab.error.data.edit.lock";
    
    public static class CapitalAssetInformation {
        public static final String ERROR_ASSET_DO_NOT_ENTER_ANY_DATA = "error.asset.do.not.enter.any.data";
        public static final String ERROR_ASSET_REQUIRE_DATA_ENTRY = "error.asset.require.data.entry";
        public static final String ERROR_ASSET_UPDATE_ALLOW_ONLY = "error.asset.update.allow.only";
        public static final String ERROR_ASSET_CREATE_NEW_ALLOW_ONLY = "error.asset.create.new.allow.only";
        public static final String ERROR_ASSET_NEW_OR_UPDATE_ONLY = "error.asset.new.or.update.only";
        public static final String ERROR_ASSET_ACTIVE_CAPITAL_ASSET_REQUIRED = "error.asset.active.capital.asset.required";
        public static final String ERROR_ASSET_TAG_LINE_REQUIRED = "error.asset.tag.line.required";
        public static final String ERROR_ASSET_QUANTITY_NOT_MATCHING_TAG_LINES = "error.asset.quantity.not.matching.tag.lines";
    }
    
    public static final String CHART_ORG_DISALLOWED_BY_CURRENT_USER = "chart.org.disallowed.by.current.user";
    public static final String WARNING_GL_PROCESSED = "warning.gl.processed";
}
