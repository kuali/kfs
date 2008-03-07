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
 * Global constancts for cams.
 */
public class CamsConstants {
    //public static final String SYSTEM_NAME = "Capital Assets Management System";
    
    public static final String DEPRECIATION_METHOD_SALVAGE_VALUE_CODE   ="SV";
    public static final String DEPRECIATION_METHOD_STRAIGHT_LINE_CODE   ="SL"; 
    public static final String TRANSFER_PAYMENT_CODE_N = "N";

    public static class AssetActions {
        public static final String LOAN = "loan";
        public static final String MERGE = "merge";
        public static final String PAYMENT = "payment";
        public static final String RETIRE = "retire";
        public static final String SEPARATE = "separate";
        public static final String TRANSFER= "transfer";
    }

    public static final String PRE_ASSET_TAGGING_FILE_TYPE_INDENTIFIER = "preAssetTaggingFileType";

    public static class Parameters {
        public static final String DEPRECIATION_DATE_PARAMETER                            ="DEPRECIATION_DATE";
        public static final String NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES     ="NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPE"; 
        public static final String NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES       ="NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES";
        public static final String NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES        ="NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES";
        public static final String NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES="NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_OBJECT_SUB_TYPE";                
    }    

    public static class Report {
        public static final String REPORT_EXTENSION          = "PDF";
        public static final String FILE_PREFIX               = "CAMS";                
    }
    
    public static class Depreciation {
        public static final String DEPRECIATION_ORIGINATION_CODE="01"; // 01 -> Transaction Processing
        public static final String TRANSACTION_DESCRIPTION      = "Batch Depreciation Asset ";
        public static final String DOCUMENT_DESCRIPTION         = "Batch Depreciation Entry";
        public static final String REPORT_FILE_NAME             = "DEPRECIATION_REPORT";
        public static final String DEPRECIATION_REPORT_TITLE = "Asset Depreciation Report - Statistics";        
    }

    public static class NotPendingDocumentStatuses {
        public static final String APPROVED = "A";
        public static final String CANCELED = "C";        
    }
    
    public static final String[] MONTHS = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    
    public static final String COMPONENT_NUMBER = "componentNumber";
    public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";

    public static class Dispositon {
        public static final String ASSET_MERGE_CODE = "M";
        public static final String ASSET_SEPARATE_CODE = "S";
    }
    
    public static class AssetPaymentErrors {
        public static final String INFORMATION_TAB_ERRORS = "AssetInfoErrors,document.capitalAssetNumber,document.organizationOwnerChartOfAccountsCode,document.campusCode,"+
                                                                  "document.representativeUniversalIdentifier";
        
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
    }
    
    // TODO:replaced by system parameter
    public static final String CAPITAL_ASSET_STATUS_CODES = "A,C,U,S,R";
    
}
