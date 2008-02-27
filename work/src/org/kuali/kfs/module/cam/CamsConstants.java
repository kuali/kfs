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
        public static final String NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPES     ="NON_DEPRECIABLE_CAMPUS_PLANT_FUND_OBJECT_SUB_TYPE"; 
        public static final String NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES      ="NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES";
        public static final String NON_DEPRECIABLE_NON_CAPITAL_ASSETS_STATUS_CODES       ="NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES";
        public static final String NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_SUB_OBJECT_TYPES="NON_DEPRECIABLE_ORGANIZATON_PLANT_FUND_OBJECT_SUB_TYPE";                
    }    

    public static class Report {
        public static final String REPORT_EXTENSION ="PDF";
        public static final String FILE_PREFIX = "CAMS";        
        public static final String DEPRECIATION_REPORT_TITLE = "Asset Depreciation Report - Statistics";        
    }
    
    public static class Depreciation {
        public static final String DEPRECIATION_ORIGINATION_CODE="01";
        public static final String TRANSACTION_DESCRIPTION      = "Batch Depreciation Asset ";
        public static final String DOCUMENT_DESCRIPTION         = "Batch Depreciation Entry";        
    }

    
    public static final String[] MONTHS = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    
    public static final String COMPONENT_NUMBER = "componentNumber";
    public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";

    public static class Discompositon {
        public static final String ASSET_DISCOMPOSTION_CODE_MERGE = "M";
        public static final String ASSET_DISCOMPOSTION_CODE_SEPARATE = "S";
    }
}
