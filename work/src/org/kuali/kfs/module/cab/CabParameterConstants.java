/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cab;

import org.kuali.kfs.sys.ParameterKeyConstants;

/**
 * Holds constants for CAB business parameters.
 */
public class CabParameterConstants implements ParameterKeyConstants {

    public static class CapitalAsset {
        public static final String CAPITAL_ASSET_OBJECT_LEVELS = "PURCHASING_ACCOUNTS_PAYABLE_OBJECT_LEVELS";
        public static final String POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS = "PURCHASING_ACCOUNTS_PAYABLE_POSSIBLE_OBJECT_LEVELS";
        public static final String CAPITAL_ASSET_PRICE_THRESHOLD = "CAPITALIZATION_LIMIT_AMOUNT";
        public static final String RECURRING_CAMS_TRAN_TYPES = "PURCHASING_ASSET_TRANSACTION_TYPES_REQUIRING_RECURRING_PAYMENT_TERMS";

    }

}
