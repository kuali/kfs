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
    public static final String DEPRECIATION_METHOD_SALVAGE_VALUE_CODE   ="SV";
    public static final String DEPRECIATION_METHOD_STRAIGHT_LINE_CODE   ="SL"; 

    public static final String MAINTENANCE_TAG_METHOD_TO_CALL           = "tag";
    public static final String MAINTENANCE_SEPERATE_METHOD_TO_CALL      = "seperate";
    public static final String MAINTENANCE_PAYMENT_METHOD_TO_CALL       = "payment";
    public static final String MAINTENANCE_RETIRE_METHOD_TO_CALL        = "retire";
    public static final String MAINTENANCE_TRANSFER_METHOD_TO_CALL      = "transfer";
    public static final String MAINTENANCE_LOAN_METHOD_TO_CALL          = "loan";
    public static final String MAINTENANCE_MERGE_METHOD_TO_CALL         = "merge";

    public static final String PRE_ASSET_TAGGING_FILE_TYPE_INDENTIFIER = "preAssetTaggingFileType";

    public static class DocumentTypes {
        public static final String ASSET_ADDITION = "AA";
        public static final String ASSET_EDIT = "CASM";
        public static final String ASSET_TAG = "ATAG";
        public static final String ASSET_SEPERATE = "ASEP";
        public static final String ASSET_PAYMENT = "MPAY";
        public static final String ASSET_RETIREMENT = "AR";
        public static final String ASSET_TRANSFER = "AT";
        public static final String ASSET_LOAN = "ELR";
        public static final String ASSET_MERGE = "AMRG";
    }

    public static class Parameters {
        public static final String NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES     ="NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPE"; 
        public static final String NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES      ="NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES";
        public static final String NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES       ="NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES";
        public static final String NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES="NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_OBJECT_SUB_TYPE";                
    }

    // Following constants will be retired by KFSMI-324
    public static class SectionTitles {
        public static final String ASSET_RETIREMENT = "View Retirement Information";
    }    

    public static final String CAMS = "Capital Assets Management System";
    
    public static final String DEPRECIATION_REPORT_SUBTITLE = "Asset Depreciation Report - Statistics";
}
